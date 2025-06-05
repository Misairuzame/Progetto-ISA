package com.gb.restApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.gb.restApp.MessageHandler.*;
import static com.gb.restApp.MyTemplateEngine.*;
import static com.gb.utils.UtilFunctions.isGeThanZero;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * Questa classe è stata creata principalmente per fare
 * refactoring, in quanto vari frammenti di codice del
 * Main erano duplicati o comunque molto simili. I metodi
 * di questa classe incapsulano le chiamate al database,
 * rendendo il codice della classe Main più compatto e
 * meno ripetitivo.
 */
public class DbReturnHelper {

    private static final Logger logger = LoggerFactory.getLogger(DbReturnHelper.class);

    private static void info(String toLog) {
        logger.info("Returned: {}", toLog);
    }

    /**
     * Questo metodo incapsula le query al database che restituiscono
     * un intero, quali le update, insert e delete. Permette di specificare
     * quali messaggi mostrare all'utente in caso di fallimento o successo.
     * @param databaseCall La funzione della classe Database da chiamare
     * @param failureMessage Il messaggio da mostrare in caso di fallimento
     * @param successMessage Il messaggio da mostrare in caso di successo
     * @param failureStatusCode Stato HTTP della Response in caso di fallimento
     * @param successStatusCode Stato HTTP della Response in caso di successo
     * @param res L'oggetto Response
     * @return La stringa da mostrare all'utente
     */
    public static String dbQueryResult(Supplier<Integer> databaseCall,
                                       String failureMessage, String successMessage,
                                       int failureStatusCode, int successStatusCode,
                                       Response res) {
        int result = databaseCall.get();
        if (result < 0) {
            if (result == -2) {
                return handleInternalError(res);
            }
            if (result == -1) {
                return returnMessage(res, failureStatusCode, "text-warning", failureMessage);
            }
        }
        return returnMessage(res, successStatusCode, "text-success", successMessage);
    }

    /**
     * Questo metodo incapsula le query al database che restituiscono
     * una lista, quindi le operazioni di SELECT. Permette di specificare
     * quali messaggi mostrare all'utente in caso di fallimento o successo.
     * @param function La funzione della classe Database da chiamare
     * @param listName Il nome della lista da inserire nella View
     * @param viewName La View da mostrare all'utente
     * @param req L'oggetto Request
     * @param res L'oggetto Response
     * @return La stringa da mostrare all'utente
     */
    public static String dbGetQueryResult(Function<Integer,List<?>> function,
                                          String listName, String viewName,
                                          Request req, Response res) {
        int pageNum = 0;
        String pageString = req.queryParams("page");
        if(pageString != null) {
            if(!isGeThanZero(pageString)) {
                return handleParseError(res);
            } else {
                pageNum = Integer.parseInt(pageString);
            }
        }

        List<?> list = function.apply(pageNum);
        if (list == null) {
            return handleInternalError(res);
        }
        if (list.isEmpty()) {
            return handleNotFound(res);
        }

        res.status(SC_OK);

        info(list.toString());

        Map<String, Object> model = new HashMap<>();
        model.put(listName, list);
        model.put("page", pageNum);
        return getEngineInstance().render(new ModelAndView(model, viewName));
    }

    /**
     * Questo metodo incapsula le query al database che restituiscono
     * una lista contenente un solo elemento, quindi le operazioni di
     * SELECT tramite ID. Permette di specificare quali messaggi mostrare
     * all'utente in caso di fallimento o successo.
     * @param function La funzione della classe Database da chiamare
     * @param paramToUse Nome del parametro da utilizzare come ID nella ricerca.
     *                   E' un parametro della query (/hello?id=foo)
     * @param listName Il nome della lista da inserire nella View
     * @param viewName La View da mostrare all'utente
     * @param req L'oggetto Request
     * @param res L'oggetto Response
     * @return La stringa da mostrare all'utente
     */
    public static String dbGetByIdQueryResult(Function<Integer,List<?>> function,
                                              String paramToUse,
                                              String listName, String viewName,
                                              Request req, Response res) {
        String parameter = req.queryParams(paramToUse);
        if(parameter == null || !isGeThanZero(parameter)) {
            return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int id = Integer.parseInt(parameter);

        List<?> list = function.apply(id);
        if (list == null) {
            return handleInternalError(res);
        }
        if (list.isEmpty()) {
            return handleNotFound(res);
        }

        res.status(SC_OK);

        info(list.toString());

        Map<String, Object> model = new HashMap<>();
        model.put(listName, list);
        return getEngineInstance().render(new ModelAndView(model, viewName));
    }

}
