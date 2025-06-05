package unit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.gb.restApp.DbReturnHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DbReturnHelperTest {

    @BeforeAll
    static void printName() {
        System.out.println("[Unit test] DbReturnHelperTest");
    }

    @Test
    void dbQueryResultTest() {
        String failureMessage = "FALLIMENTO";
        String successMessage = "SUCCESSO";

        Supplier<Integer> databaseCall = mock(Supplier.class);
        when(databaseCall.get()).thenReturn(-2, -1, 0);

        Response res = mock(Response.class);

        String resultString;
        resultString = dbQueryResult(databaseCall, failureMessage, successMessage, 0, 0, res);
        assertTrue(resultString.contains("verificato un errore"));

        resultString = dbQueryResult(databaseCall, failureMessage, successMessage, 0, 0, res);
        assertTrue(resultString.contains(failureMessage));

        resultString = dbQueryResult(databaseCall, failureMessage, successMessage, 0, 0, res);
        assertTrue(resultString.contains(successMessage));
    }

    @Test
    void dbGetQueryResultTest() {
        Response res = mock(Response.class);

        Request req = mock(Request.class);
        when(req.queryParams("page")).thenReturn("abcde", "1234", "1234");

        Function<Integer, List<?>> databaseCall = mock(Function.class);
        when(databaseCall.apply(anyInt())).thenReturn(null, new ArrayList<>());

        String resultString;
        resultString = dbGetQueryResult(databaseCall, "", "", req, res);
        assertTrue(resultString.contains("Errore nella deserializzazione"));

        resultString = dbGetQueryResult(databaseCall, "", "", req, res);
        assertTrue(resultString.contains("verificato un errore"));

        resultString = dbGetQueryResult(databaseCall, "", "", req, res);
        assertTrue(resultString.contains("non trovata"));
    }

    @Test
    void dbGetByIdQueryResultTest() {
        final String param = "test";
        Response res = mock(Response.class);

        Request req = mock(Request.class);
        when(req.queryParams(param)).thenReturn("abcde", "1234", "1234");

        Function<Integer, List<?>> databaseCall = mock(Function.class);
        when(databaseCall.apply(anyInt())).thenReturn(null, new ArrayList<>());

        String resultString;
        resultString = dbGetByIdQueryResult(databaseCall, param, "", "", req, res);
        assertTrue(resultString.contains("Specificare un id nel formato corretto."));

        resultString = dbGetByIdQueryResult(databaseCall, param, "", "", req, res);
        assertTrue(resultString.contains("verificato un errore"));

        resultString = dbGetByIdQueryResult(databaseCall, param, "", "", req, res);
        assertTrue(resultString.contains("non trovata"));
    }
}