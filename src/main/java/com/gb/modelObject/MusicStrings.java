package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

/**
 * Questa classe è stata creata per poter mostrare all'utente i risultati
 * della ricerca a testo libero. Visto che nella tabella musica vengono
 * memorizzati principalmente degli ID (chiavi esterne), la ricerca a testo
 * libero viene effettuata su una tabella di join, costruita in modo da
 * contenere, invece degli ID esterni, i nomi o titoli ad essi associati.
 */
public class MusicStrings {

    private static final Logger logger = LoggerFactory.getLogger(MusicStrings.class);

    private Integer musicId;
    private String title;
    private String author;
    private String artist;
    private String album;
    private Integer year;
    private String genre;

    public MusicStrings() {}

    public MusicStrings(ResultSet rs) {
        try {
            setMusicId(rs.getInt(MUSICID));
            setTitle(rs.getString("musictitle"));
            setAuthor(rs.getString("groupname"));
            setArtist(rs.getString("artistname"));
            setAlbum(rs.getString("albumtitle"));
            setYear(rs.getInt(YEAR));
            setGenre(rs.getString("genrename"));
        } catch(SQLException e) {
            logger.error("Error creating Music object: {}", e.getMessage());
        }
    }

    public Integer getMusicId() {
        return musicId;
    }

    public void setMusicId(Integer musicId) {
        if(musicId <= 0) {
            throw new IllegalArgumentException("MusicId deve essere > 0.");
        }
        this.musicId = musicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title.length() > 100) {
            throw new IllegalArgumentException("Lunghezza titolo musica deve essere < 100.");
        }
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if(author.length() > 100) {
            throw new IllegalArgumentException("Lunghezza nome autore (gruppo) deve essere < 100.");
        }
        this.author = author;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        if(album != null && album.length() > 100) {
            throw new IllegalArgumentException("Lunghezza titolo album deve essere < 100.");
        }
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        if(artist.length() > 100) {
            throw new IllegalArgumentException("Lunghezza nome artista album deve essere < 100.");
        }
        this.artist = artist;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        if(year < 0 || year > 3000) {
            throw new IllegalArgumentException("Anno musica deve essere compreso fra 0 e 3000.");
        }
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if(genre.length() > 100) {
            throw new IllegalArgumentException("Lunghezza nome genere deve essere < 100.");
        }
        this.genre = genre;
    }
}
