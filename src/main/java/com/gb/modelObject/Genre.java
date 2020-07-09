package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class Genre {

    private Integer genreId;
    private String name;

    private static final Logger logger = LoggerFactory.getLogger(Genre.class);

    public Genre() {
    }

    public Genre(ResultSet rs) {
        try {
            setGenreId(rs.getInt(GENREID));
            setName(rs.getString(NAME));
        } catch(SQLException e) {
            logger.error("Error creating Genre object: {}", e.getMessage());
        }
    }

    public Genre(Integer genreId, String name) {
        setGenreId(genreId);
        setName(name);
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        if(genreId < 0) {
            throw new IllegalArgumentException("GenreId deve essere > 0.");
        }
        this.genreId = genreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name.length() > 100) {
            throw new IllegalArgumentException("Lunghezza nome genere deve essere < 100.");
        }
        this.name = name;
    }

}
