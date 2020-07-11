package com.gb.DAO;

import com.gb.modelObject.Genre;

import java.util.List;
import java.util.Map;

public interface GenreDAO {

    List<Genre> getAllGenres(int page);

    List<Genre> getGenreById(int genreId);

    int insertGenre(Genre genre);

    int updateGenre(Genre genre);

    int deleteGenre(int genreId);

    Map<Integer, String> getGenreMap();

}
