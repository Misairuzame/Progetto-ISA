package com.gb.restApp;

import spark.TemplateEngine;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

/**
 * Questa classe fornisce un livello di astrazione per quanto
 * riguarda l'engine che processa i template; inoltre crea un
 * punto centralizzato per ottenere l'engine, in modo da evitare
 * dipendenze cicliche nell'applicazione.
 */
public class MyTemplateEngine {

    private static TemplateEngine engineInstance;

    public static TemplateEngine getEngineInstance() {
        if (engineInstance == null) {
             engineInstance = new ThymeleafTemplateEngine();
        }
        return engineInstance;
    }

}
