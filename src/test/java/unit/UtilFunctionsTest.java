package unit;

import com.gb.utils.UtilFunctions;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class UtilFunctionsTest {

    @Test
    void isNumber() {
        System.out.println(this.getClass() + " isNumber()");
        String string = String.valueOf((new Random().nextInt(Integer.MAX_VALUE)));

        assertTrue(UtilFunctions.isNumber(string));

        string = "abcde";

        assertFalse(UtilFunctions.isNumber(string));

        string = "a1b2c3";

        assertFalse(UtilFunctions.isNumber(string));
    }
}