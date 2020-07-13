package com.gb.restApp;

import com.gb.modelObject.*;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.gb.Constants.*;
import static com.gb.utils.UtilFunctions.isPositiveInteger;

/**
 * Questa classe contiene i metodi per la
 * deserializzazione di oggetti a partire dai
 * parametri nell'URL. I metodi put...InModel
 * servono a facilitare la modifica e l'inserimento
 * di elementi nel database da parte dell'utente.
 */
public class DeserializationHelper {

    public static void putIdInModel(String queryParam, Request req, Map<String,Object> model) {
        if (req.queryParams(queryParam) != null && !req.queryParams(queryParam).equals("") &&
                isPositiveInteger(req.queryParams(queryParam))) {
            int id = Integer.parseInt(req.queryParams(queryParam));
            model.put(queryParam, id);
        } else {
            model.put(queryParam, null);
        }
    }

    public static void putObjectInModel(String queryParam, Function<Integer,Object> function, Request req, Map<String,Object> model) {
        if (req.queryParams(queryParam) != null && !req.queryParams(queryParam).equals("") &&
                isPositiveInteger(req.queryParams(queryParam))) {
            int id = Integer.parseInt(req.queryParams(queryParam));
            Object toEdit = function.apply(id);
            model.put(queryParam, toEdit);
        }
    }

    public static void putMapInModel(String queryParam, Supplier<Map<Integer,String>> function, Map<String,Object> model) {
        Map<Integer,String> map = function.get();
        model.put(queryParam, map);
    }

    public static void deserializeMusic(Music music, Request req) throws UnsupportedEncodingException {
        music.setMusicId(Integer.parseInt(req.queryParams(MUSICID)));
        music.setTitle(URLDecoder.decode(req.queryParams(TITLE), "UTF-8"));
        music.setAuthorId(Integer.parseInt(req.queryParams(AUTHORID)));
        if (req.queryParams(ALBUMID) != null && !req.queryParams(ALBUMID).equals("")) {
            music.setAlbumId(Integer.parseInt(req.queryParams(ALBUMID)));
        } else {
            music.setAlbumId(null);
        }
        music.setYear(Integer.parseInt(req.queryParams(YEAR)));
        music.setGenreId(Integer.parseInt(req.queryParams(GENREID)));
    }

    public static void deserializeAlbum(Album album, Request req) throws UnsupportedEncodingException {
        album.setAlbumId(Integer.parseInt(req.queryParams(ALBUMID)));
        album.setTitle(URLDecoder.decode(req.queryParams(TITLE), "UTF-8"));
        album.setYear(Integer.parseInt(req.queryParams(YEAR)));
        album.setGroupId(Integer.parseInt(req.queryParams(GROUPID)));
    }

    public static void deserializeArtist(Artist artist, Request req) throws UnsupportedEncodingException {
        artist.setArtistId(Integer.parseInt(req.queryParams(ARTISTID)));
        artist.setName(URLDecoder.decode(req.queryParams(NAME), "UTF-8"));
        artist.setGroupId(Integer.parseInt(req.queryParams(GROUPID)));
    }

    public static void deserializeGroup(Group group, Request req) throws UnsupportedEncodingException {
        group.setGroupId(Integer.parseInt(req.queryParams(GROUPID)));
        group.setName(URLDecoder.decode(req.queryParams(NAME), "UTF-8"));
    }

    public static void deserializeGenre(Genre genre, Request req) throws UnsupportedEncodingException {
        genre.setGenreId(Integer.parseInt(req.queryParams(GENREID)));
        genre.setName(URLDecoder.decode(req.queryParams(NAME), "UTF-8"));
    }

    public static void deserializeLink(Link link, Request req) throws UnsupportedEncodingException {
        link.setMusicId(Integer.parseInt(req.queryParams(MUSICID)));
        link.setLink(URLDecoder.decode(req.queryParams(LINK), "UTF-8"));
    }

}
