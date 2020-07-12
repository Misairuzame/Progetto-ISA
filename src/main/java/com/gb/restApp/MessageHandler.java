package com.gb.restApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.*;

public class MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static void info(String toLog) {
        logger.info("Returned: {}", toLog);
    }

    public static String returnMessage(Response res, int httpStatus, String messageType, String messageText) {
        res.status(httpStatus);
        info(messageText);
        Map<String, String> model = new HashMap<>();
        model.put("messagetype", messageType);
        model.put("messagetext", messageText);
        return Main.engine.render(new ModelAndView(model, "message"));
    }

    public static String handleNotFound(Response res) {
        return returnMessage(res, SC_NOT_FOUND, "text-warning",
                "Risorsa o collezione non trovata.");
    }

    public static String handleInternalError(Response res) {
        return returnMessage(res, SC_INTERNAL_SERVER_ERROR, "text-danger",
                "Si e' verificato un errore.");
    }

    public static String handleParseError(Response res) {
        return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                "Errore nella deserializzazione dei parametri inviati.\n" +
                        "Specificare i parametri in maniera corretta.");
    }

}
