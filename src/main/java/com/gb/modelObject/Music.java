package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class Music {

    private static final Logger logger = LoggerFactory.getLogger(Music.class);

    private Integer musicId;
    private String title;
    private Integer authorId;
    private Integer albumId;
    private Integer year;
    private Integer genreId;

    public Music() { }

    public Music(ResultSet rs) {
        try {
            setMusicId(rs.getInt(MUSICID));
            setTitle(rs.getString(TITLE));
            setAuthorId(rs.getInt(AUTHORID));
            if(rs.getString(ALBUMID) == null) {
                setAlbumId(null);
            } else {
                setAlbumId(rs.getInt(ALBUMID));
            }
            setYear(rs.getInt(YEAR));
            setGenreId(rs.getInt(GENREID));
        } catch(SQLException e) {
            logger.error("Error creating Music object: {}", e.getMessage());
        }
    }

    public Music(Integer musicId, String title, Integer authorId, Integer albumId, Integer year, Integer genreId) {
        setMusicId(musicId);
        setTitle(title);
        setAuthorId(authorId);
        setAlbumId(albumId);
        setYear(year);
        setGenreId(genreId);
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

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        if(authorId <= 0) {
            throw new IllegalArgumentException("Music.authorId deve essere > 0.");
        }
        this.authorId = authorId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        if(albumId != null && albumId < 0) {
            throw new IllegalArgumentException("Music.albumId, se specificato, deve essere > 0.");
        }
        this.albumId = albumId;
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

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        if(genreId <= 0) {
            throw new IllegalArgumentException("Music.genreId deve essere > 0.");
        }
        this.genreId = genreId;
    }

}
