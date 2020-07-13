package com.gb.restApp;

import static spark.Spark.*;

import com.gb.db.Database;
import com.gb.modelObject.*;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

import static org.apache.http.HttpStatus.*;
import static javax.ws.rs.core.MediaType.*;
import static com.gb.Constants.*;
import static com.gb.utils.UtilFunctions.*;
import static com.gb.restApp.DeserializationHelper.*;
import static com.gb.restApp.MessageHandler.*;
import static com.gb.restApp.DbReturnHelper.*;

/*
 * Documentazione per le costanti rappresentanti gli stati HTTP, fornite da Apache HTTP:
 * http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpStatus.html
 *
 * Costanti rappresentanti vari Media Type fornite da JAX-RS:
 * https://docs.oracle.com/javaee/7/api/javax/ws/rs/core/MediaType.html
 */

/**
 * La classe Main fa partire il Web server e si occupa
 * principalmente di effettuare il routing delle richieste.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static void info(String toLog) {
        logger.info("Returned: {}", toLog);
    }

    private static final TemplateEngine engine = MyTemplateEngine.getEngineInstance();

    public static void main(String[] args) {

        port(8080);

        staticFiles.location("/public");

        before(Main::applyFilters);

        get("/", Main::getHomepage);

        path("/music", () -> {
            get("",  Main::dispatchMusic);

            get(":id", Main::dispatchMusicId);
            get("/:id", Main::dispatchMusicId);
        });

        get("/album",  Main::dispatchAlbum);

        get("/artist",  Main::dispatchArtist);

        get("/group",  Main::dispatchGroup);

        get("/genre",  Main::dispatchGenre);

        get("/link",  Main::dispatchLink);

        get("/mjoinl", Main::musicJoinLink);

        get("/arjoing", Main::artistJoinGroup);

        get("/joinall", Main::joinAll);

        get("/search", Main::searchMusic);

        get("/viewlinks", Main::viewLinks);

        get("/favicon.ico", Main::favicon);

        get("/:form", Main::dispatchForms);

        notFound((req, res) -> MessageHandler.handleNotFound(res));

    }

    /*
     * Sezione di utility varie
     */

    private static void applyFilters(Request req, Response res) {
        /*
          Toglie lo slash finale, se presente. Facilita il matching.
          Il redirect funziona solamente con richieste GET,
          motivo per cui viene fatto il "doppio matching"
          nel metodo main.
         */
        String path = req.pathInfo();
        if (req.requestMethod().equals(GET) && path.endsWith("/") && !path.equals("/")) {
            res.redirect(path.substring(0, path.length() - 1));
        }

        /*
          Mette il content-type della Response a "text/html"
          e l'encoding a UTF-8.
         */
        res.raw().setContentType(TEXT_HTML);
        res.raw().setCharacterEncoding("UTF-8");

        /*
          Logga la Request
         */
        StringBuilder text = new StringBuilder();
        String message = "Received request: " + req.requestMethod() + " " + req.url();
        text.append(message);
        if(!req.queryParams().isEmpty()) {
            text.append("?");
            text.append(req.queryString());
        }
        if(req.headers(CONT_TYPE) != null && !req.headers(CONT_TYPE).equals("")) {
            text.append("\n");
            text.append("Request content-type:\n");
            text.append(req.headers(CONT_TYPE));
        }
        if(req.body() != null && !req.body().equals("")) {
            text.append("\n");
            text.append("Request body:\n");
            text.append(req.body());
        }
        logger.info(text.toString());
    }

    private static String getHomepage(Request req, Response res) {
        res.status(SC_OK);
        String message = "Benvenuto su MusicService!";
        info(message);
        Map<String, String> model = new HashMap<>();
        model.put("welcometext", message);
        return engine.render(new ModelAndView(model, "home"));
    }


    /*
     * Sezione per effettuare il dispatching delle richieste.
     * Necessario per gestire i metodi HTTP.
     */

    private static String dispatchMusic(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        switch (userMethod) {
            case GET:
                return getMusic(req, res);
            case PUT:
                return updateMusic(req, res);
            case POST:
                return insertMusic(req, res);
            case DELETE:
                return deleteMusic(req, res);
            default:
                return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                        "Metodo HTTP non supportato.");
        }
    }

    private static String dispatchMusicId(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        if(userMethod.equalsIgnoreCase(GET))    return getMusicById(req, res);

        return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                "Metodo HTTP non supportato.");
    }

    private static String dispatchGroup(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        switch (userMethod) {
            case GET:
                return getGroups(req, res);
            case PUT:
                return updateGroup(req, res);
            case POST:
                return insertGroup(req, res);
            case DELETE:
                return deleteGroup(req, res);
            default:
                return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                        "Metodo HTTP non supportato.");
        }
    }

    private static String dispatchGenre(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        switch (userMethod) {
            case GET:
                return getGenres(req, res);
            case PUT:
                return updateGenre(req, res);
            case POST:
                return insertGenre(req, res);
            case DELETE:
                return deleteGenre(req, res);
            default:
                return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                        "Metodo HTTP non supportato.");
        }
    }

    private static String dispatchAlbum(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        switch (userMethod) {
            case GET:
                return getAlbums(req, res);
            case PUT:
                return updateAlbum(req, res);
            case POST:
                return insertAlbum(req, res);
            case DELETE:
                return deleteAlbum(req, res);
            default:
                return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                        "Metodo HTTP non supportato.");
        }
    }

    private static String dispatchArtist(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        switch (userMethod) {
            case GET:
                return getArtists(req, res);
            case PUT:
                return updateArtist(req, res);
            case POST:
                return insertArtist(req, res);
            case DELETE:
                return deleteArtist(req, res);
            default:
                return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                        "Metodo HTTP non supportato.");
        }
    }

    private static String dispatchLink(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        switch (userMethod) {
            case GET:
                return getLinks(req, res);
            case POST:
                return insertLink(req, res);
            default:
                return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                        "Metodo HTTP non supportato.");
        }
    }


    /*
     * Sezione per la visualizzazione dei form
     */

    /**
     * Questo metodo fa un ulteriore routing, oltre a quello già fatto
     * dal framework. Permette di mostrare all'utente determinate pagine
     * per cui non si è definita una route precisa nel Main, in modo da
     * non rendere il Main troppo complicato. Inoltre questo metodo si
     * occupa di preparare le view, inserendoci dati che rendono
     * l'interfaccia Web più navigabile per l'utente.
     * @param req L'oggetto Request
     * @param res L'oggetto Response
     * @return La stringa da mostrare all'utente
     */
    private static String dispatchForms(Request req, Response res) {
        Map<String, Object> model = new HashMap<>();
        String viewName = req.params("form");
        File testFile = new File(USER_DIR + "\\src\\main\\resources\\templates\\" + viewName + ".html");
        if (!testFile.exists()) {
            return handleNotFound(res);
        }

        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        /* Se un case non ha "break", vengono eseguite tutte le istruzioni fino a che
           non si trova un "break" (o finisce il blocco switch). Questo viene comodo
           nell'effettuare degli "or" fra i vari casi. Viene detto "fall through". */
        switch (viewName) {
            case "upmusic":
                putObjectInModel("musicToEdit", (id) -> db.getMusicById(id).get(0), req, model);
                // fall through
            case "insmusic":
                putMapInModel("authorMap", db::getGroupMap, model);
                putMapInModel("albumMap", db::getAlbumMap, model);
                putMapInModel("genreMap", db::getGenreMap, model);
                break;
            case "delmusic":
                putIdInModel("musicToDel", req, model);
                break;
            case "upartist":
                putObjectInModel("artistToEdit", (id) -> db.getArtistById(id).get(0), req, model);
                putMapInModel("groupMap", db::getGroupMap, model);
                break;
            case "insartist":
                // fall through
            case "insalbum":
                putMapInModel("groupMap", db::getGroupMap, model);
                break;
            case "delartist":
                putIdInModel("artistToDel", req, model);
                break;
            case "upalbum":
                putObjectInModel("albumToEdit", (id) -> db.getAlbumById(id).get(0), req, model);
                putMapInModel("groupMap", db::getGroupMap, model);
                break;
            case "delalbum":
                putIdInModel("albumToDel", req, model);
                break;
            case "upgroup":
                putObjectInModel("groupToEdit", (id) -> db.getGroupById(id).get(0), req, model);
                break;
            case "delgroup":
                putIdInModel("groupToDel", req, model);
                break;
            case "upgenre":
                putObjectInModel("genreToEdit", (id) -> db.getGenreById(id).get(0), req, model);
                break;
            case "delgenre":
                putIdInModel("genreToDel", req, model);
                break;
        }

        return engine.render(new ModelAndView(model, viewName));
    }


    /*
     * Sezione per l'esecuzione di query
     */

    private static String getMusic(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Map<String, Object> model = new HashMap<>();

        List<Music> musicList;
        int pageNum = 0;
        if (req.queryParams("page") != null) {
            if (!isGeThanZero(req.queryParams("page"))) {
                return handleParseError(res);
            } else {
                pageNum = Integer.parseInt(req.queryParams("page"));
            }
        }

        //Ricerca tramite album
        if(req.queryParams("albumid") != null) {
            if(!isPositiveInteger(req.queryParams("albumid"))) {
                return handleParseError(res);
            } else {
                int albumId = Integer.parseInt(req.queryParams("albumid"));
                musicList = db.getMusicByAlbum(albumId, pageNum);
                String albumName = db.getAlbumById(albumId).get(0).getTitle();
                model.put("albumId", albumId);
                model.put("albumName", albumName);
            }
        } else
        //Ricerca tramite genere
         if(req.queryParams("genreid") != null) {
            if (!isPositiveInteger(req.queryParams("genreid"))) {
                return handleParseError(res);
            } else {
                int genreId = Integer.parseInt(req.queryParams("genreid"));
                musicList = db.getMusicByGenre(genreId, pageNum);
                String genreName = db.getGenreById(genreId).get(0).getName();
                model.put("genreId", genreId);
                model.put("genreName", genreName);
            }
        } else
        //Ricerca tramite gruppo
         if(req.queryParams("groupid") != null) {
            if (!isPositiveInteger(req.queryParams("groupid"))) {
                return handleParseError(res);
            } else {
                int groupId = Integer.parseInt(req.queryParams("groupid"));
                musicList = db.getMusicByGroup(groupId, pageNum);
                String groupName = db.getGroupById(groupId).get(0).getName();
                model.put("groupId", groupId);
                model.put("groupName", groupName);
            }
        } else
        //Ricerca tramite artista
         if(req.queryParams("artistid") != null) {
            if (!isPositiveInteger(req.queryParams("artistid"))) {
                return handleParseError(res);
            } else {
                int artistId = Integer.parseInt(req.queryParams("artistid"));
                musicList = db.getMusicByArtist(artistId, pageNum);
                String artistName = db.getArtistById(artistId).get(0).getName();
                model.put("artistId", artistId);
                model.put("artistName", artistName);
            }
        }
        //Default
        else {
            musicList = db.getAllMusic(pageNum);
        }

        if (musicList == null) {
            return handleInternalError(res);
        }
        if (musicList.isEmpty()) {
            return handleNotFound(res);
        }

        res.status(SC_OK);

        info(musicList.toString());

        model.put("musicList", musicList);
        model.put("page", pageNum);
        return engine.render(new ModelAndView(model, "musicList"));
    }

    private static String getMusicById(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        return dbGetByIdQueryResult(db::getMusicById, MUSICID, "musicList", "musicList", req, res);
    }

    private static String insertMusic(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Music musicToAdd = new Music();
        try {
            deserializeMusic(musicToAdd, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione della musica da inserire");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.insertMusic(musicToAdd),
                "Esiste gia' una musica con id "+musicToAdd.getMusicId()+".",
                "Musica con id "+musicToAdd.getMusicId()+" aggiunta con successo.",
                SC_CONFLICT, SC_CREATED, res);
    }

    private static String updateMusic(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Music musicToUpdate = new Music();
        try {
            deserializeMusic(musicToUpdate, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione della musica da aggiornare");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.updateMusic(musicToUpdate),
                "Non esiste una musica con id "+musicToUpdate.getMusicId()+", impossibile aggiornarla.",
                "Musica con id "+musicToUpdate.getMusicId()+" modificata con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String deleteMusic(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        if(req.queryParams(MUSICID) == null || !isGeThanZero(req.queryParams(MUSICID))) {
            return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int musicId = Integer.parseInt(req.queryParams(MUSICID));

        return dbQueryResult(() -> db.deleteMusic(musicId),
                "Non esiste una musica con id "+musicId+", impossibile eliminarla.",
                "Musica con id "+musicId+" eliminata con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String searchMusic(Request req, Response res) {
        int pageNum = 0;

        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        if(req.queryParams("page") != null) {
            if(!isGeThanZero(req.queryParams("page"))) {
                return handleParseError(res);
            } else {
                pageNum = Integer.parseInt(req.queryParams("page"));
            }
        }

        if(req.queryParams("string") == null || req.queryParams("string").equals("")) {
            return returnMessage(res, SC_BAD_REQUEST, "text-warning",
                    "Specificare la stringa di ricerca in maniera corretta.");
        }

        List<MusicStrings> musicList = db.searchMusic(req.queryParams("string"), pageNum);
        if (musicList == null) {
            return handleInternalError(res);
        }
        if (musicList.isEmpty()) {
            return handleNotFound(res);
        }

        res.status(SC_OK);

        info(musicList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("musicList", musicList);
        model.put("page", pageNum);
        model.put("string", req.queryParams("string"));
        return engine.render(new ModelAndView(model, "search"));
    }

    private static String getAlbums(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        return dbGetQueryResult(db::getAllAlbums, "albumList", "albumlist", req, res);
    }

    private static String insertAlbum(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Album albumToAdd = new Album();
        try {
            deserializeAlbum(albumToAdd, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione dell' album da inserire");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.insertAlbum(albumToAdd),
                "Esiste gia' un album con id "+albumToAdd.getAlbumId()+".",
                "Album con id "+albumToAdd.getAlbumId()+" aggiunto con successo.",
                SC_CONFLICT, SC_CREATED, res);
    }

    private static String updateAlbum(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Album albumToUpdate = new Album();
        try {
            deserializeAlbum(albumToUpdate, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione dell' album da aggiornare");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.updateAlbum(albumToUpdate),
                "Non esiste un album con id "+albumToUpdate.getAlbumId()+", impossibile aggiornarlo.",
                "Album con id "+ albumToUpdate.getAlbumId()+" modificato con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String deleteAlbum(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        if(req.queryParams(ALBUMID) == null || !isPositiveInteger(req.queryParams(ALBUMID))) {
            return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int albumId = Integer.parseInt(req.queryParams(ALBUMID));

        return dbQueryResult(() -> db.deleteAlbum(albumId),
                "Non esiste un album con id "+ albumId +", impossibile eliminarlo.",
                "Album con id "+ albumId +" eliminato con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String getArtists(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        return dbGetQueryResult(db::getAllArtists, "artistList", "artistlist", req, res);
    }

    private static String updateArtist(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Artist artistToUpdate = new Artist();
        try {
            deserializeArtist(artistToUpdate, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione dell' artista da aggiornare");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.updateArtist(artistToUpdate),
                "Non esiste un artista con id "+ artistToUpdate.getArtistId()+", impossibile aggiornarlo.",
                "Artista con id "+ artistToUpdate.getArtistId()+" modificato con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String insertArtist(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Artist artistToAdd = new Artist();
        try {
            deserializeArtist(artistToAdd, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione dell' artista da inserire");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.insertArtist(artistToAdd),
                "Esiste gia' un artista con id "+ artistToAdd.getArtistId()+".",
                "Artista con id "+ artistToAdd.getArtistId()+" aggiunto con successo.",
                SC_CONFLICT, SC_CREATED, res);
    }

    private static String deleteArtist(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        if(req.queryParams(ARTISTID) == null || !isPositiveInteger(req.queryParams(ARTISTID))) {
            return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int artistId = Integer.parseInt(req.queryParams(ARTISTID));

        return dbQueryResult(() -> db.deleteArtist(artistId),
                "Non esiste un artista con id "+ artistId +", impossibile eliminarlo.",
                "Artista con id "+ artistId +" eliminato con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String getGroups(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        return dbGetQueryResult(db::getAllGroups, "groupList", "grouplist", req, res);
    }

    private static String insertGroup(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Group groupToAdd = new Group();
        try {
            deserializeGroup(groupToAdd, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione del gruppo da inserire");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.insertGroup(groupToAdd),
                "Esiste gia' un gruppo con id "+groupToAdd.getGroupId()+".",
                "Gruppo con id "+groupToAdd.getGroupId()+" aggiunto con successo.",
                SC_CONFLICT, SC_CREATED, res);
    }

    private static String updateGroup(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Group groupToEdit = new Group();
        try {
            deserializeGroup(groupToEdit, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione del gruppo da modificare");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.updateGroup(groupToEdit),
                "Non esiste un gruppo con id "+ groupToEdit.getGroupId()+", impossibile aggiornarlo.",
                "Gruppo con id "+ groupToEdit.getGroupId()+" modificato con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String deleteGroup(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        if(req.queryParams(GROUPID) == null || !isPositiveInteger(req.queryParams(GROUPID))) {
            return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int groupId = Integer.parseInt(req.queryParams(GROUPID));

        return dbQueryResult(() -> db.deleteGroup(groupId),
                "Non esiste un gruppo con id "+ groupId +", impossibile eliminarlo.",
                "Gruppo con id "+ groupId +" eliminato con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String getGenres(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        return dbGetQueryResult(db::getAllGenres, "genreList", "genrelist", req, res);
    }

    private static String insertGenre(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Genre genreToAdd = new Genre();
        try {
            deserializeGenre(genreToAdd, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione del genere da inserire");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.insertGenre(genreToAdd),
                "Esiste gia' un genere con id "+ genreToAdd.getGenreId()+".",
                "Genere con id "+ genreToAdd.getGenreId()+" aggiunto con successo.",
                SC_CONFLICT, SC_CREATED, res);
    }

    private static String updateGenre(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Genre genreToEdit = new Genre();
        try {
            deserializeGenre(genreToEdit, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione del genere da modificare");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.updateGenre(genreToEdit),
                "Non esiste un genere con id "+ genreToEdit.getGenreId()+", impossibile aggiornarlo.",
                "Genere con id "+ genreToEdit.getGenreId()+" modificato con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String deleteGenre(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        if(req.queryParams(GENREID) == null || !isPositiveInteger(req.queryParams(GENREID))) {
            return returnMessage(res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int genreId = Integer.parseInt(req.queryParams(GENREID));

        return dbQueryResult(() -> db.deleteGenre(genreId),
                "Non esiste un genere con id "+ genreId +", impossibile eliminarlo.",
                "Genere con id "+ genreId +" eliminato con successo.",
                SC_BAD_REQUEST, SC_OK, res);
    }

    private static String getLinks(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        return dbGetQueryResult(db::getAllLinks, "linkList", "linklist", req, res);
    }

    private static String insertLink(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        Link linkToAdd = new Link();
        try {
            deserializeLink(linkToAdd, req);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione del link da inserire");
            return handleParseError(res);
        }

        return dbQueryResult(() -> db.insertLink(linkToAdd),
                "",
                "Link per la canzone "+ linkToAdd.getMusicId()+" aggiunto con successo.",
                SC_INTERNAL_SERVER_ERROR, SC_CREATED, res);
    }

    private static String musicJoinLink(Request req, Response res) {
        Database db = Database.getDatabase();
        if(db == null) {
            return handleInternalError(res);
        }

        return dbGetQueryResult(db::musicJoinLink, "musicJoinLinkList", "musicJoinLink", req, res);
    }

    private static String artistJoinGroup(Request req, Response res) {
        Database db = Database.getDatabase();
        if(db == null) {
            return handleInternalError(res);
        }

        return dbGetQueryResult(db::artistJoinGroup, "artistJoinGroupList", "artistJoinGroup", req, res);
    }

    private static String joinAll(Request req, Response res) {
        Database db = Database.getDatabase();
        if(db == null) {
            return handleInternalError(res);
        }

        return dbGetQueryResult(db::joinAll, "joinAllList", "joinAll", req, res);
    }

    private static String viewLinks(Request req, Response res) {
        Database db = Database.getDatabase();
        if (db == null) {
            return handleInternalError(res);
        }

        int musicId;
        try {
            musicId = Integer.parseInt(req.queryParams(MUSICID));
        } catch (IllegalArgumentException e) {
            logger.warn("Errore nella deserializzazione dell'id della musica.");
            return handleParseError(res);
        }

        Music music = db.getMusicById(musicId).get(0);

        List<Link> linkList = db.getLinksForMusic(musicId);
        if (linkList == null) {
            return handleInternalError(res);
        }
        if (linkList.isEmpty()) {
            return handleNotFound(res);
        }

        res.status(SC_OK);

        info(linkList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("linkList", linkList);
        model.put("music", music);
        return engine.render(new ModelAndView(model, "linksformusic"));
    }

    /**
     * Funzione per fornire l'iconcina  di fianco al titolo.
     * Adattato dalla seguente fonte:
     * @author hamishmorgan
     * https://github.com/hamishmorgan/ERL/blob/master/src/test/java/spark/SparkExamples.java
     */
    private static String favicon(Request req, Response res) {
        try {
            InputStream in = null;
            OutputStream out;
            try {
                in = new BufferedInputStream(new FileInputStream(".\\favicon.ico"));
                out = new BufferedOutputStream(res.raw().getOutputStream());
                res.raw().setContentType("image/x-icon");
                res.status(SC_OK);
                ByteStreams.copy(in, out);
                out.flush();
                return "";
            } finally {
                Closeables.close(in, true);
            }
        } catch (FileNotFoundException ex) {
            logger.warn(ex.getMessage());
            res.status(SC_NOT_FOUND);
            return ex.getMessage();
        } catch (IOException ex) {
            logger.warn(ex.getMessage());
            res.status(SC_INTERNAL_SERVER_ERROR);
            return ex.getMessage();
        }
    }

}
