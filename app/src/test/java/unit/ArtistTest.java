package unit;

import com.gb.modelObject.Artist;
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
public class ArtistTest {

    @BeforeClass
    public static void printName() {
        System.out.println("[Unit test] ArtistTest");
    }

    @Property(trials = 50)
    public void setArtistId(@InRange(maxInt = 0) int negativeId, @InRange(minInt = 1) int positiveId) {
        IllegalArgumentException e1 = null;
        try {
            new Artist().setArtistId(negativeId);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Artist().setArtistId(positiveId);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setName(@From(InvalidStringGenerator.class) String invalid, @From(StringGenerator.class) String valid) {
        IllegalArgumentException e1 = null;
        try {
            new Artist().setName(invalid);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Artist().setName(valid);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setGroupId(@InRange(maxInt = 0) int negativeId, @InRange(minInt = 1) int positiveId) {
        IllegalArgumentException e1 = null;
        try {
            new Artist().setGroupId(negativeId);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Artist().setGroupId(positiveId);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

}