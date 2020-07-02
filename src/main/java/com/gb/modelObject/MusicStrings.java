package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class MusicStrings {

    private static final Logger logger = LoggerFactory.getLogger(MusicStrings.class);

    private Integer musicId;
    private String title;
    private String author;
    private String album;
    private Integer year;
    private String genre;

    public MusicStrings(ResultSet rs) {
        try {
            setMusicId(rs.getInt(MUSICID));
            setTitle(rs.getString("musictitle"));
            setAuthor(rs.getString("groupname"));
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
        this.musicId = musicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
