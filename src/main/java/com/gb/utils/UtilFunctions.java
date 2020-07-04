package com.gb.utils;

public class UtilFunctions {

    /**
     * Restituisce true se la stringa passata come argomento è
     * un intero non negativo, effettuandone il parsing.
     */
    public static boolean isNonNegativeInteger(String num) {
        try {
            int integer = Integer.parseInt(num);
            return integer >= 0;
        } catch (Exception e) {
            return false;
        }
    }

}
