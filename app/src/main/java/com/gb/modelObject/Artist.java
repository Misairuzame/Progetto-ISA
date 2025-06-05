package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class Artist {

    private Integer artistId;
    private String name;
    private Integer groupId;

    private static final Logger logger = LoggerFactory.getLogger(Artist.class);

    public Artist() {
    }

    public Artist(ResultSet rs) {
        try {
            setArtistId(rs.getInt(ARTISTID));
            setName(rs.getString(NAME));
            setGroupId(rs.getInt(GROUPID));
        } catch(SQLException e) {
            logger.error("Error creating Artist object: {}", e.getMessage());
        }
    }

    public Artist(Integer artistId, String name, Integer groupId) {
        this.artistId = artistId;
        this.name = name;
        this.groupId = groupId;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        if(artistId <= 0) {
            throw new IllegalArgumentException("ArtistId deve essere > 0.");
        }
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name.length() > 100) {
            throw new IllegalArgumentException("Lunghezza nome artista deve essere < 100.");
        }
        this.name = name;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        if(groupId <= 0) {
            throw new IllegalArgumentException("Artist.groupId deve essere > 0.");
        }
        this.groupId = groupId;
    }

}
