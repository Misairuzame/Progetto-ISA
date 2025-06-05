package unit;

import com.gb.modelObject.Music;
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
public class MusicTest {

    @BeforeClass
    public static void printName() {
        System.out.println("[Unit test] MusicTest");
    }

    @Property(trials = 50)
    public void setMusicId(@InRange(maxInt = 0) int negativeId, @InRange(minInt = 1) int positiveId) {
        IllegalArgumentException e1 = null;
        try {
            new Music().setMusicId(negativeId);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Music().setMusicId(positiveId);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setTitle(@From(InvalidStringGenerator.class) String invalid, @From(StringGenerator.class) String valid) {
        IllegalArgumentException e1 = null;
        try {
            new Music().setTitle(invalid);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Music().setTitle(valid);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setAuthorId(@InRange(maxInt = 0) int negativeId, @InRange(minInt = 1) int positiveId) {
        IllegalArgumentException e1 = null;
        try {
            new Music().setAuthorId(negativeId);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Music().setAuthorId(positiveId);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setAlbumId(@InRange(maxInt = 0) int negativeId, @InRange(minInt = 1) int positiveId) {
        IllegalArgumentException e1 = null;
        try {
            new Music().setAlbumId(negativeId);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Music().setAlbumId(positiveId);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 33)
    public void setYear(@InRange(maxInt = -1) int lower, @InRange(minInt = 3001) int upper, @InRange(minInt = 0, maxInt = 3000) int valid) {
        IllegalArgumentException e1 = null;
        try {
            new Music().setYear(lower);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Music().setYear(upper);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNotNull(e2);

        IllegalArgumentException e3 = null;
        try {
            new Music().setYear(valid);
        } catch (IllegalArgumentException ex) {
            e3 = ex;
        }
        assertNull(e3);
    }

    @Property(trials = 50)
    public void setGenreId(@InRange(maxInt = 0) int negativeId, @InRange(minInt = 1) int positiveId) {
        IllegalArgumentException e1 = null;
        try {
            new Music().setGenreId(negativeId);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new Music().setGenreId(positiveId);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

}