package unit;

import com.gb.utils.AlphanumericGenerator;
import com.gb.utils.StringGenerator;
import com.gb.utils.UtilFunctions;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitQuickcheck.class)
public class UtilFunctionsTest {

    @BeforeClass
    public static void printName() {
        System.out.println("[Unit test] UtilFunctionsTest");
    }

    @Property
    public void isPositiveIntegerWithNegativeNumbers(@InRange(maxInt = 0) int negativeNumber) {

        String intString = String.valueOf(negativeNumber);

        assertFalse(UtilFunctions.isPositiveInteger(intString));
    }

    @Property
    public void isPositiveIntegerWithPositiveNumbers(@InRange(minInt = 1) int positiveNumber) {

        String intString = String.valueOf(positiveNumber);

        assertTrue(UtilFunctions.isPositiveInteger(intString));
    }

    @Property(trials = 50)
    public void isPositiveIntegerWithStrings(
            @From(StringGenerator.class) String testString,
            @From(AlphanumericGenerator.class) String alphanumString) {

        assertFalse(UtilFunctions.isPositiveInteger(testString));

        assertFalse(UtilFunctions.isPositiveInteger(alphanumString));
    }

}