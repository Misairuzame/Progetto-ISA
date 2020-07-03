package unit;

import com.gb.db.PostgreSQLImpl.PostgreSQLImpl;
import com.gb.modelObject.Music;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static com.gb.Constants.*;

class PostgreSQLImplTest {

    @Test
    void getAllMusic() {
        System.out.println(this.getClass() + " getAllMusic()");
        PostgreSQLImpl database = PostgreSQLImpl.getInstance();

        assertNotNull(database);

        List<Music> musicList = database.getAllMusic(0);

        assertTrue(musicList.size() <= PAGE_SIZE,
                "Il numero di risultati per pagina è maggiore di quello definito.");
    }

    @Test
    void getMusicById() {
        System.out.println(this.getClass() + " getMusicById()");
        PostgreSQLImpl database = PostgreSQLImpl.getInstance();

        assertNotNull(database);

        List<Music> musicList = database.getMusicById(1234);

        assertEquals(1, musicList.size(),
                "Non è stata trovata una canzone esistente, o è stato restituito più di un risultato.");
    }

    @Test
    void updateMusic() {
    }

    @Test
    void insertMusic() {
    }

    @Test
    void deleteMusic() {
    }

    @Test
    void searchMusic() {
        System.out.println(this.getClass() + " searchMusic()");
        PostgreSQLImpl database = PostgreSQLImpl.getInstance();

        assertNotNull(database);

        assertDoesNotThrow(() -> {
            database.searchMusic("\\ ' \" \n \t ! £ $ % & / ( ) = ? ^ [ ] { } ; - _ . § ç ° +",0);
        }, "Non vengono cercati correttamente dei caratteri speciali.");
    }
}
