package unit;

import com.gb.db.PostgreSQLImpl.PostgreSQLImpl;
import com.gb.modelObject.Link;
import com.gb.modelObject.Music;
import com.gb.modelObject.MusicStrings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static com.gb.Constants.*;

/**
 * I test per le funzioni di inserimento, aggiornamento
 * ed eliminazione vengono fatti tutti insieme in un
 * integration test (PostgresAndModelTest)
 */
class PostgreSQLImplTest {

    private static PostgreSQLImpl database;

    @BeforeAll
    public static void createDb() {
        System.out.println("[Unit test] PostgreSQLImplTest");
        database = PostgreSQLImpl.getInstance();

        assertNotNull(database);
    }

    @Test
    void getAllMusic() {
        List<Music> musicList = database.getAllMusic(0);

        assertTrue(musicList.size() <= PAGE_SIZE,
                "Il numero di risultati per pagina è maggiore di quello definito.");
    }

    @Test
    void getMusicById() {
        List<Music> musicList = database.getMusicById(1234);

        assertEquals(1, musicList.size(),
                "Non è stata trovata una canzone esistente, o è stato restituito più di un risultato.");
    }

    @Test
    void searchMusic() {
        final String searchString = "m";

        List<MusicStrings> musicList = database.searchMusic(searchString, 0);

        assertFalse(musicList.isEmpty(),
                "La ricerca di 'm' dovrebbe restituire almeno 1 risultato.");

        assertDoesNotThrow(() -> {
            database.searchMusic("\\ ' \" \n \t ! £ $ % & / ( ) = ? ^ [ ] { } ; - _ . § ç ° +",0);
        }, "Non vengono cercati correttamente dei caratteri speciali.");
    }

    @Test
    void getLinksForMusic() {
        final int musicId = 357357;

        List<Link> linkList = database.getLinksForMusic(musicId);

        assertFalse(linkList.isEmpty(),
                "Dovrebbe essere restituito almeno un link.");

        assertEquals(musicId, linkList.get(0).getMusicId(),
                "Il musicId specificato e quello del link recuperato devono coincidere.");
    }


}
