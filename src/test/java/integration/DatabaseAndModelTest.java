package integration;

import com.gb.db.Database;
import com.gb.modelObject.Album;
import com.gb.modelObject.Genre;
import com.gb.modelObject.Group;
import com.gb.modelObject.Music;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ho definito questo test un "integration test" perchè invece
 * di testare i singoli metodi della classe PostgreSQLImpl, si
 * testa anche l'interazione fra di loro, in particolare il caso
 * di test prevede che funzioni correttamente la ricerca, l'inserimento,
 * la modifica e l'eliminazione di dati dal database.
 */
class DatabaseAndModelTest {

    private static Database database;

    @BeforeAll
    static void printName() {
        System.out.println("[Integration test] DatabaseAndModelTest");
        database = Database.getDatabase();

        assertNotNull(database);
    }

    @Test
    void databaseFlow() {
        final int musicId = Integer.MAX_VALUE;
        Random rand = new Random();

        try {
            Database.getConnection().setAutoCommit(false);

            List<Album> albumList = database.getAllAlbums(0);
            List<Group> groupList = database.getAllGroups(0);
            List<Genre> genreList = database.getAllGenres(0);

            assertNotNull(albumList);
            assertNotNull(groupList);
            assertNotNull(genreList);

            Music music = new Music(musicId,
                    "temporaryTestMusic",
                    groupList.get(rand.nextInt(groupList.size())).getGroupId(),
                    albumList.get(rand.nextInt(albumList.size())).getAlbumId(),
                    2020,
                    genreList.get(rand.nextInt(genreList.size())).getGenreId());

            int result = database.insertMusic(music);

            assertTrue(result >= 0,
                    "Errore durante l'inserimento di una canzone.");

            music.setTitle("temporaryTestMusic2");
            music.setAuthorId(groupList.get(rand.nextInt(groupList.size())).getGroupId());
            music.setAlbumId(albumList.get(rand.nextInt(albumList.size())).getAlbumId());
            music.setYear(2021);
            music.setGenreId(genreList.get(rand.nextInt(genreList.size())).getGenreId());

            result = database.updateMusic(music);

            assertTrue(result >= 0,
                    "Errore durante la modifica di una canzone.");

            result = database.deleteMusic(musicId);

            assertTrue(result >= 0,
                    "Errore durante l'eliminazione di una canzone.");

            List<Music> shouldBeEmpty = database.getMusicById(musicId);

            assertTrue(shouldBeEmpty.isEmpty(),
                    "E' stata trovata una canzone nonostante la si sia appena eliminata.");
        } catch (SQLException e) {
            fail("Ci sono stati errori in DatabaseFlow.");
        } finally {
            try {
                Database.getConnection().rollback();
                Database.getConnection().setAutoCommit(true);
            } catch (SQLException ey) {
                System.out.println(ey.getMessage());
            }
        }
    }

}
