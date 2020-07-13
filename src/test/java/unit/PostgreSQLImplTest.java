package unit;

import com.gb.db.postgreSQLImpl.PostgreSQLImpl;
import com.gb.modelObject.Link;
import com.gb.modelObject.MusicStrings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

import static com.gb.Constants.*;

/**
 * I test per le funzioni di inserimento, aggiornamento
 * ed eliminazione vengono fatti tutti insieme in un
 * integration test (DatabaseAndModelTest). Il database
 * è ulteriormente testato nell'integration test SeleniumTest.
 */
class PostgreSQLImplTest {

    private static PostgreSQLImpl database;

    @BeforeAll
    public static void createDb() {
        System.out.println("[Unit test] PostgreSQLImplTest");
        database = PostgreSQLImpl.getInstance();

        assertNotNull(database);
    }

    void getQueryWithPageTest(Supplier<List<?>> databaseQuery) {
        List<?> returnedList = databaseQuery.get();

        assertNotNull(returnedList,
                "Ci sono stati degli errori durante l'esecuzione della query con paginazione.");

        assertTrue(returnedList.size() <= PAGE_SIZE,
                "Il numero di risultati per pagina è maggiore di quello definito.");
    }

    void getQueryWithIdTest(Supplier<List<?>> databaseQuery) {
        List<?> returnedList = databaseQuery.get();

        assertNotNull(returnedList,
                "Ci sono stati degli errori durante l'esecuzione della query tramite id.");

        assertTrue(returnedList.size() <= 1,
                "La ricerca per id deve restituire al massimo un risultato.");
    }

    @Test
    void getAllMusic() {
        getQueryWithPageTest(() -> database.getAllMusic(0));
    }

    @Test
    void getMusicById() {
        getQueryWithIdTest(() -> database.getMusicById(1234));
    }

    @Test
    void getAllAlbums() {
        getQueryWithPageTest(() -> database.getAllAlbums(0));
    }

    @Test
    void getAlbumById() {
        getQueryWithPageTest(() -> database.getAlbumById(1234));
    }

    @Test
    void getAllArtists() {
        getQueryWithPageTest(() -> database.getAllArtists(0));
    }

    @Test
    void getArtistById() {
        getQueryWithPageTest(() -> database.getArtistById(2222));
    }

    @Test
    void getAllGroups() {
        getQueryWithPageTest(() -> database.getAllGroups(0));
    }

    @Test
    void getGroupById() {
        getQueryWithPageTest(() -> database.getGroupById(1234));
    }

    @Test
    void getAllGenres() {
        getQueryWithPageTest(() -> database.getAllGenres(0));
    }

    @Test
    void getGenreById() {
        getQueryWithPageTest(() -> database.getGenreById(1234));
    }

    @Test
    void getAllLinks() {
        getQueryWithPageTest(() -> database.getAllLinks(0));
    }

    @Test
    void artistJoinGroup() {
        getQueryWithPageTest(() -> database.artistJoinGroup(0));
    }

    @Test
    void musicJoinLink() {
        getQueryWithPageTest(() -> database.musicJoinLink(0));
    }

    @Test
    void joinAll() {
        getQueryWithPageTest(() -> database.joinAll(0));
    }

    @Test
    void searchMusic() {
        final String searchString = "e";

        List<MusicStrings> musicList = database.searchMusic(searchString, 0);

        assertFalse(musicList.isEmpty(),
                "La ricerca di 'e' dovrebbe restituire almeno 1 risultato.");

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
