package unit;

import com.gb.modelObject.MusicStrings;
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
public class MusicStringsTest {

    @BeforeClass
    public static void printName() {
        System.out.println("[Unit test] MusicStringsTest");
    }

    @Property(trials = 50)
    public void setMusicId(@InRange(maxInt = -1) int negativeId, @InRange(minInt = 0) int positiveId) {
        IllegalArgumentException e1 = null;
        try {
            new MusicStrings().setMusicId(negativeId);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new MusicStrings().setMusicId(positiveId);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setTitle(@From(InvalidStringGenerator.class) String invalid, @From(StringGenerator.class) String valid) {
        IllegalArgumentException e1 = null;
        try {
            new MusicStrings().setTitle(invalid);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new MusicStrings().setTitle(valid);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setAuthor(@From(InvalidStringGenerator.class) String invalid, @From(StringGenerator.class) String valid) {
        IllegalArgumentException e1 = null;
        try {
            new MusicStrings().setAuthor(invalid);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new MusicStrings().setAuthor(valid);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setArtist(@From(InvalidStringGenerator.class) String invalid, @From(StringGenerator.class) String valid) {
        IllegalArgumentException e1 = null;
        try {
            new MusicStrings().setArtist(invalid);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new MusicStrings().setArtist(valid);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 50)
    public void setAlbum(@From(InvalidStringGenerator.class) String invalid, @From(StringGenerator.class) String valid) {
        IllegalArgumentException e1 = null;
        try {
            new MusicStrings().setAlbum(invalid);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new MusicStrings().setAlbum(valid);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

    @Property(trials = 33)
    public void setYear(@InRange(maxInt = -1) int lower, @InRange(minInt = 3001) int upper, @InRange(minInt = 0, maxInt = 3000) int valid) {
        IllegalArgumentException e1 = null;
        try {
            new MusicStrings().setYear(lower);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new MusicStrings().setYear(upper);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNotNull(e2);

        IllegalArgumentException e3 = null;
        try {
            new MusicStrings().setYear(valid);
        } catch (IllegalArgumentException ex) {
            e3 = ex;
        }
        assertNull(e3);
    }

    @Property(trials = 50)
    public void setGenre(@From(InvalidStringGenerator.class) String invalid, @From(StringGenerator.class) String valid) {
        IllegalArgumentException e1 = null;
        try {
            new MusicStrings().setGenre(invalid);
        } catch (IllegalArgumentException ex) {
            e1 = ex;
        }
        assertNotNull(e1);

        IllegalArgumentException e2 = null;
        try {
            new MusicStrings().setGenre(valid);
        } catch (IllegalArgumentException ex) {
            e2 = ex;
        }
        assertNull(e2);
    }

}