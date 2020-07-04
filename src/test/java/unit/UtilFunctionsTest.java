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
    public void isNonNegativeIntegerWithNegativeNumbers(@InRange(maxInt = -1) int negativeNumber) {

        String intString = String.valueOf(negativeNumber);

        assertFalse(UtilFunctions.isNonNegativeInteger(intString));
    }

    @Property
    public void isNonNegativeIntegerWithPositiveNumbers(@InRange(minInt = 0) int positiveNumber) {

        String intString = String.valueOf(positiveNumber);

        assertTrue(UtilFunctions.isNonNegativeInteger(intString));
    }

    @Property(trials = 50)
    public void isNonNegativeIntegerWithStrings(
            @From(StringGenerator.class) String testString,
            @From(AlphanumericGenerator.class) String alphanumString) {

        assertFalse(UtilFunctions.isNonNegativeInteger(testString));

        assertFalse(UtilFunctions.isNonNegativeInteger(alphanumString));
    }

}