package unit;

import com.gb.modelObject.Link;
import com.gb.utils.InvalidStringGenerator;
import com.gb.utils.StringGenerator;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class LinkTest {

    @BeforeClass
    public static void printName() {
        System.out.println("[Unit test] LinkTest");
    }

    @Property(trials = 50)
    public void setMusicId(@InRange(maxInt = 0) int negativeId, @InRange(minInt = 1) int positiveId) {
        IllegalArgumentException e1 = null;
        try {
            new Link().setMusicId(negativeId);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Link().setMusicId(positiveId);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setLink(@From(InvalidStringGenerator.class) String invalidOne, @From(InvalidStringGenerator.class) String invalidTwo, @From(StringGenerator.class) String valid) {
        IllegalArgumentException e1 = null;
        try {
            new Link().setLink(invalidOne+invalidTwo);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Link().setLink(valid);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

}