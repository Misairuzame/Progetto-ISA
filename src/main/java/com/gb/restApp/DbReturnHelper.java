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
import static com.gb.utils.UtilFunctions.isGeThanZero;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

public class DbReturnHelper {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static void info(String toLog) {
        logger.info("Returned: {}", toLog);
    }

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

    public static String dbGetQueryResult(Function<Integer,List<?>> function,
                                          String listName, String viewName,
                                          Request req, Response res) {
        int pageNum = 0;
        if(req.queryParams("page") != null) {
            if(!isGeThanZero(req.queryParams("page"))) {
                return handleParseError(res);
            } else {
                pageNum = Integer.parseInt(req.queryParams("page"));
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
        return Main.engine.render(new ModelAndView(model, viewName));
    }

    public static String dbGetByIdQueryResult(Function<Integer,List<?>> function,
                                              String paramToUse,
                                              String listName, String viewName,
                                              Request req, Response res) {
        if(req.queryParams(paramToUse) == null || !isGeThanZero(req.queryParams(paramToUse))) {
            return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int id = Integer.parseInt(req.queryParams(paramToUse));

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
        return Main.engine.render(new ModelAndView(model, viewName));
    }

}
