package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class Album {

    private Integer albumId;
    private String title;
    private Integer year;
    private Integer groupId;

    private static final Logger logger = LoggerFactory.getLogger(Album.class);

    public Album() {
    }

    public Album(ResultSet rs) {
        try {
            setAlbumId(rs.getInt(ALBUMID));
            setTitle(rs.getString(TITLE));
            setYear(rs.getInt(YEAR));
            setGroupId(rs.getInt(GROUPID));
        } catch(SQLException e) {
            logger.error("Error creating Album object: {}", e.getMessage());
        }
    }

    public Album(Integer albumId, String title, Integer year, Integer groupId) {
        setAlbumId(albumId);
        setTitle(title);
        setYear(year);
        setGroupId(groupId);
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        if(albumId < 0) {
            throw new IllegalArgumentException("AlbumId deve essere > 0.");
        }
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title.length() > 100) {
            throw new IllegalArgumentException("Lunghezza titolo album deve essere < 100.");
        }
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        if(year < 0 || year > 3000) {
            throw new IllegalArgumentException("Anno album deve essere compreso fra 0 e 3000.");
        }
        this.year = year;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        if(groupId < 0) {
            throw new IllegalArgumentException("Album.groupid deve essere > 0.");
        }
        this.groupId = groupId;
    }

}
