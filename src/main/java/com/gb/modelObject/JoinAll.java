package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinAll {
    private static final Logger logger = LoggerFactory.getLogger(JoinAll.class);

    private int musicId;
    private String musicTitle;
    private String groupName;
    private int numArtists;
    private String albumTitle;
    private int year;
    private String genreName;
    private int numLinks;

    public JoinAll(ResultSet rs) {
        try {
            setMusicId(rs.getInt("musicid"));
            setMusicTitle(rs.getString("musictitle"));
            setGroupName(rs.getString("groupname"));
            setNumArtists(rs.getInt("numartisti"));
            setAlbumTitle(rs.getString("albumtitle"));
            setYear(rs.getInt("year"));
            setGenreName(rs.getString("genrename"));
            setNumLinks(rs.getInt("numlink"));
        } catch (SQLException e) {
            logger.error("Error creating JoinAll object: {}", e.getMessage());
        }
    }

    public int getMusicId() {
        return musicId;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getNumArtists() {
        return numArtists;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public int getYear() {
        return year;
    }

    public String getGenreName() {
        return genreName;
    }

    public int getNumLinks() {
        return numLinks;
    }

    public void setMusicId(int musicId) {
        if(musicId <= 0) {
            throw new IllegalArgumentException("MusicId deve essere > 0.");
        }
        this.musicId = musicId;
    }

    public void setMusicTitle(String musicTitle) {
        if(musicTitle.length() > 100) {
            throw new IllegalArgumentException("Lunghezza titolo musica deve essere < 100.");
        }
        this.musicTitle = musicTitle;
    }

    public void setGroupName(String groupName) {
        if(groupName.length() > 100) {
            throw new IllegalArgumentException("Lunghezza nome gruppo deve essere < 100.");
        }
        this.groupName = groupName;
    }

    public void setNumArtists(int numArtists) {
        if(numArtists < 0) {
            throw new IllegalArgumentException("Numero artisti deve essere >= 0.");
        }
        this.numArtists = numArtists;
    }

    public void setAlbumTitle(String albumTitle) {
        if(albumTitle != null && albumTitle.length() > 100) {
            throw new IllegalArgumentException("Lunghezza titolo album deve essere < 100.");
        }
        this.albumTitle = albumTitle;
    }

    public void setYear(int year) {
        if(year < 0 || year > 3000) {
            throw new IllegalArgumentException("Anno musica deve essere compreso fra 0 e 3000.");
        }
        this.year = year;
    }

    public void setGenreName(String genreName) {
        if(genreName.length() > 100) {
            throw new IllegalArgumentException("Lunghezza nome genere deve essere < 100.");
        }
        this.genreName = genreName;
    }

    public void setNumLinks(int numLinks) {
        if(numLinks < 0) {
            throw new IllegalArgumentException("Numero di link deve essere >= 0.");
        }
        this.numLinks = numLinks;
    }
}
