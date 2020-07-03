package integration;

import com.gb.db.PostgreSQLImpl.PostgreSQLImpl;
import com.gb.modelObject.Album;
import com.gb.modelObject.Genre;
import com.gb.modelObject.Group;
import com.gb.modelObject.Music;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PostgresAndModelTest {

    @Test
    void databaseFlow() {
        System.out.println(this.getClass() + " databaseFlow() [Integration test]");
        PostgreSQLImpl database = PostgreSQLImpl.getInstance();

        assertNotNull(database);

        int musicId = Integer.MAX_VALUE;
        Random rand = new Random();

        List<Album> albumList = database.getAllAlbums();
        List<Group> groupList = database.getAllGroups();
        List<Genre> genreList = database.getAllGenres();

        Music music = new Music(musicId,
                "temporaryTestMusic",
                groupList.get(rand.nextInt(groupList.size())).getGroupId(),
                albumList.get(rand.nextInt(albumList.size())).getAlbumId(),
                2020,
                genreList.get(rand.nextInt(genreList.size())).getGenreId());

        int result = database.insertMusic(music);

        assertTrue(result >= 0,
                "Errore durante l'inserimento di una canzone.");

        result = database.deleteMusic(musicId);

        assertTrue(result >= 0,
                "Errore durante l'eliminazione di una canzone.");

        List<Music> shouldBeEmpty = database.getMusicById(musicId);

        assertTrue(shouldBeEmpty.isEmpty(),
                "E' stato trovata una canzone nonostante la si sia appena eliminata.");
    }

}
