package com.gb.db.PostgreSQLImpl;

import com.gb.modelObject.*;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gb.Constants.*;

public class PostgreSQLImpl extends com.gb.db.Database {

    private static Connection conn = null;
    private static PostgreSQLImpl postgresInstance = null;
    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLImpl.class);

    public static synchronized PostgreSQLImpl getInstance() {
        if(postgresInstance == null) {
            postgresInstance = new PostgreSQLImpl();
            if (conn == null) {
                return null;
            }
        }
        return postgresInstance;
    }

    public PostgreSQLImpl() {

        try {
            BufferedReader br = new BufferedReader(new FileReader("creds.txt"));
            String usr = br.readLine();
            String psw = br.readLine();
            br.close();
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setServerNames(new String[]{"localhost"});
            dataSource.setDatabaseName("MusicDBPostgres");
            dataSource.setPortNumbers(new int[]{5432});
            dataSource.setUser(usr);
            dataSource.setPassword(psw);
            dataSource.setCurrentSchema("MusicDB");
            conn = dataSource.getConnection();
            logger.info("Database connection created successfully.");

            String sql = "SET SCHEMA '"+DB_NAME+"'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
            logger.info("Schema "+ DB_NAME +" set successfully.");

        } catch (SQLException | IOException e) {
            logger.error("Exception during PostgreSQLImpl constructor: " + e.getMessage());
            conn = null;
        }

    }

    public static Connection getConnection() {
        return conn;
    }

    @Override
    public List<Music> getAllMusic(int page) {
        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM " + MUSIC_TABLE +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,PAGE_SIZE);
            ps.setInt(2,page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new Music(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in getAllMusic: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Music> getMusicById(int musicId) {
        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, musicId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    musicList.add(new Music(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in getMusicById: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<JoinAll> joinAll(int page) {
        List<JoinAll> musicList = new ArrayList<>();

        String sql =
                " SELECT M.musicid, M.title AS musictitle, groupname, tmptable.numartisti, Al.title AS albumtitle, " +
                " M.year, Ge.name AS genrename, COUNT(L.link) AS numlink " +
                " FROM " +
                " music AS M LEFT JOIN album AS Al ON (M.albumid = Al.albumid) " +
                " LEFT JOIN " +
                " ( " +
                    " SELECT COUNT(Ar.artistid) AS numartisti, Gr.name AS groupname, Gr.groupid AS tmpgrid " +
                    " FROM grouptable AS Gr LEFT JOIN artist AS Ar ON (Ar.groupid = Gr.groupid) " +
                    " GROUP BY tmpgrid, groupname " +
                " ) as tmptable ON (M.authorid = tmpgrid) " +
                " INNER JOIN genre AS Ge ON (M.genreid = Ge.genreid) " +
                " LEFT JOIN link AS L on (M.musicid = L.musicid) " +
                " GROUP BY M.musicid, tmptable.groupname, Al.title, Ge.name, tmptable.numartisti " +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, PAGE_SIZE);
            ps.setInt(2, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new JoinAll(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in joinAll: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int updateMusic(Music music) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, music.getMusicId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("La canzone con id {} non esiste, impossibile aggiornarla.", music.getMusicId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateMusic: " + e.getMessage());
            return -2;
        }

        String sql =
                " UPDATE " + MUSIC_TABLE + " SET " +
                 TITLE + " = ?, " + AUTHORID + " = ?, " + ALBUMID + " = ?, " +
                 YEAR + " = ?, "  + GENREID  + " = ? " +
                " WHERE " + MUSICID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, music.getTitle());
            ps.setInt(2, music.getAuthorId());
            if (music.getAlbumId() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, music.getAlbumId());
            }
            ps.setInt(4, music.getYear());
            ps.setInt(5, music.getGenreId());
            ps.setInt(6, music.getMusicId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateMusic: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int insertMusic(Music music) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, music.getMusicId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' una canzone con id {}, impossibile crearne una nuova.", music.getMusicId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertMusic: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + MUSIC_TABLE +
                " ( " + MUSICID + ", " + TITLE + ", " + AUTHORID + ", " + ALBUMID + ", " +
                 YEAR + ", " + GENREID + " ) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, music.getMusicId());
            ps.setString(2, music.getTitle());
            ps.setInt(3, music.getAuthorId());
            if (music.getAlbumId() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, music.getAlbumId());
            }
            ps.setInt(5, music.getYear());
            ps.setInt(6, music.getGenreId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertMusic: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int deleteMusic(int id) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, id);
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("La canzone con id {} non esiste, impossibile eliminarla.", id);
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in deleteMusic: " + e.getMessage());
            return -2;
        }

        String sql =
                " DELETE FROM " + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error in deleteMusic: {}", e.getMessage());
            return -2;
        }

        return 0;
    }

    /**
     * La query SQL che viene eseguita è la seguente:<br>
     *<br>
     * SELECT *<br>
     * FROM<br>
     * (<br>
     * &emsp;SELECT M.musicid, M.title AS musictitle, GR.name AS groupname, 'Vari artisti' AS artistname, AL.title AS albumtitle, M.year, GE.name AS genrename<br>
     * &emsp;FROM<br>
     * &emsp;music AS M LEFT JOIN album AS AL ON M.albumid = AL.albumid<br>
     * &emsp;JOIN grouptable AS GR ON M.authorid = GR.groupid<br>
     * &emsp;JOIN genre AS GE ON M.genreid = GE.genreid<br>
     * ) AS temp1<br>
     * WHERE (temp1.musictitle ~* ?<br>
     * OR temp1.groupname ~* ?<br>
     * OR temp1.albumtitle ~* ?<br>
     * OR temp1.genrename ~* ?)<br>
     * AND temp1.musicid NOT IN (<br>
     * &emsp;SELECT M.musicid<br>
     * &emsp;FROM<br>
     * &emsp;music AS M JOIN grouptable AS GR ON M.authorid = GR.groupid<br>
     * &emsp;JOIN artist AS AR ON AR.groupid = GR.groupid AND AR.name ~* ?<br>
     * )<br>
     *<br>
     * UNION<br>
     *<br>
     * SELECT M.musicid, M.title AS musictitle, GR.name AS groupname, AR.name AS artistname, AL.title AS albumtitle, M.year, GE.name AS genrename<br>
     * FROM<br>
     * music AS M LEFT JOIN album AS AL ON M.albumid = AL.albumid<br>
     * JOIN grouptable AS GR ON M.authorid = GR.groupid<br>
     * JOIN genre AS GE ON M.genreid = GE.genreid<br>
     * JOIN artist AS AR ON AR.groupid = GR.groupid AND AR.name ~* ?<br>
     *<br>
     * ORDER BY musicid<br>
     * LIMIT 10 OFFSET 0<br>
     *<br>
     * <b>Spiegazione</b>: Per permettere all'utente di cercare anche le canzoni di un certo artista, è necessario fare il join
     * fra la tabella music, la tabella grouptable e la tabella artist (c'è una gerarchia). Visto che una canzone può
     * avere più di un artista (una canzone ha un autore, che è un gruppo, il quale può essere composto da più artisti)
     * una query più semplice restituirebbe delle righe duplicate, perchè si avrebbero più righe per la stessa canzone dove
     * l'unica differenza è la colonna autore. In questo modo, invece, una canzone viene mostrata o se è stata composta
     * dall'artista cercato (in questo caso nella colonna artista si mostra il suo nome), o se qualsiasi altro campo di
     * ricerca fa match (in questo caso nella colonna artista si scrive 'vari artisti').
     * L'unico caso in cui vengono restituite più righe per la stessa musica è quando la stringa cercata fa match con
     * più di uno degli autori che hanno composto insieme una certa canzone. Questo comportamento è voluto.
     */
    @Override
    public List<MusicStrings> searchMusic(String searchTerm, int page) {
        List<MusicStrings> musicList = new ArrayList<>();
        searchTerm = searchTerm.replaceAll("([\\\\+*?\\[\\](){}|.^$])", "\\\\$1");

        String sql =
                " SELECT * " +
                " FROM " +
                " ( " +
                " SELECT M.musicid, M.title AS musictitle, GR.name AS groupname, 'Vari artisti' AS artistname, AL.title AS albumtitle, M.year, GE.name AS genrename " +
                " FROM " +
                 MUSIC_TABLE + " AS M LEFT JOIN " + ALBUM_TABLE + " AS AL ON M.albumid = AL.albumid " +
                " JOIN " + GROUP_TABLE + " AS GR ON M.authorid = GR.groupid " +
                " JOIN " + GENRE_TABLE + " AS GE ON M.genreid = GE.genreid " +
                ") AS temp1 " +
                " WHERE (temp1.musictitle ~* ? " +
                " OR temp1.groupname ~* ? " +
                " OR temp1.albumtitle ~* ? " +
                " OR temp1.genrename ~* ?) " +
                " AND temp1.musicid NOT IN ( " +
                " SELECT M.musicid " +
                " FROM " +
                 MUSIC_TABLE + " AS M JOIN " + GROUP_TABLE + " AS GR ON M.authorid = GR.groupid " +
                " JOIN " + ARTIST_TABLE + " AS AR ON AR.groupid = GR.groupid AND AR.name ~* ?" +
                " ) " +

                " UNION " +

                " SELECT M.musicid, M.title AS musictitle, GR.name AS groupname, AR.name AS artistname, AL.title AS albumtitle, M.year, GE.name AS genrename " +
                " FROM " +
                 MUSIC_TABLE + " AS M LEFT JOIN " + ALBUM_TABLE + " AS AL ON M.albumid = AL.albumid " +
                " JOIN " + GROUP_TABLE + " AS GR ON M.authorid = GR.groupid " +
                " JOIN " + GENRE_TABLE + " AS GE ON M.genreid = GE.genreid " +
                " JOIN " + ARTIST_TABLE + " AS AR ON AR.groupid = GR.groupid AND AR.name ~* ?" +
                " ORDER BY musicid " +
                " LIMIT ? OFFSET ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setString(3, searchTerm);
            ps.setString(4, searchTerm);
            ps.setString(5, searchTerm);
            ps.setString(6, searchTerm);
            ps.setInt(7, PAGE_SIZE);
            ps.setInt(8, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new MusicStrings(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in searchMusic: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Album> getAllAlbums(int page) {
        List<Album> albumList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM " + ALBUM_TABLE +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, PAGE_SIZE);
            ps.setInt(2, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    albumList.add(new Album(rs));
                }
            }
            return albumList;
        } catch (SQLException e) {
            logger.error("Error in getAllAlbums: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Album> getAlbumById(int albumId) {
        List<Album> albumList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, albumId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    albumList.add(new Album(rs));
                }
            }
            return albumList;
        } catch (SQLException e) {
            logger.error("Error in getAlbumById: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int deleteAlbum(int albumId) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, albumId);
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("L'album con id {} non esiste, impossibile eliminarlo.", albumId);
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in deleteAlbum: " + e.getMessage());
            return -2;
        }

        String sql =
                " DELETE FROM " + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, albumId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error in deleteAlbum: {}", e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int insertAlbum(Album album) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, album.getAlbumId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' un album con id {}, impossibile crearne uno nuovo.", album.getAlbumId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertAlbum: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + ALBUM_TABLE +
                " ( " + ALBUMID + ", " + TITLE + ", " + YEAR + ", " + GROUPID +
                " ) VALUES (?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, album.getAlbumId());
            ps.setString(2, album.getTitle());
            ps.setInt(3, album.getYear());
            ps.setInt(4, album.getGroupId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertAlbum: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int updateAlbum(Album album) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, album.getAlbumId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("L'album con id {} non esiste, impossibile aggiornarlo.", album.getAlbumId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateAlbum: " + e.getMessage());
            return -2;
        }

        String sql =
                " UPDATE " + ALBUM_TABLE + " SET " +
                 TITLE + " = ?," + YEAR + " = ?, " + GROUPID + " = ? " +
                " WHERE " + ALBUMID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, album.getTitle());
            ps.setInt(2, album.getYear());
            ps.setInt(3, album.getGroupId());
            ps.setInt(4, album.getAlbumId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateAlbum: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public List<Artist> getAllArtists(int page) {
        List<Artist> artistList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM " + ARTIST_TABLE +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, PAGE_SIZE);
            ps.setInt(2, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    artistList.add(new Artist(rs));
                }
            }
            return artistList;
        } catch (SQLException e) {
            logger.error("Error in getAllArtists: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<ArtistJoinGroup> artistJoinGroup(int page) {
        List<ArtistJoinGroup> list = new ArrayList<>();

        String sql =
                " SELECT A."+ARTISTID+", A."+NAME+", A."+GROUPID+", G."+NAME+
                " FROM "+ARTIST_TABLE+" as A LEFT JOIN "+GROUP_TABLE+" as G" +
                " ON A."+GROUPID+" = G."+GROUPID+
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, PAGE_SIZE);
            ps.setInt(2, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    list.add(new ArtistJoinGroup(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error in artistJoinGroup: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int updateArtist(Artist artist) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ARTIST_TABLE +
                " WHERE " + ARTISTID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, artist.getArtistId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("L'artista con id {} non esiste, impossibile aggiornarlo.", artist.getArtistId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateArtist: " + e.getMessage());
            return -2;
        }

        String sql =
                " UPDATE " + ARTIST_TABLE + " SET " +
                 NAME + " = ?, " + GROUPID + " = ? " +
                " WHERE " + ARTISTID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artist.getName());
            ps.setInt(2, artist.getGroupId());
            ps.setInt(3, artist.getArtistId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateArtist: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int insertArtist(Artist artist) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ARTIST_TABLE +
                " WHERE " + ARTISTID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, artist.getArtistId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' un artista con id {}, impossibile crearne uno nuovo.", artist.getArtistId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertArtist: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + ARTIST_TABLE +
                " ( " + ARTISTID + ", " + NAME + ", " + GROUPID +
                " ) VALUES (?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artist.getArtistId());
            ps.setString(2, artist.getName());
            ps.setInt(3, artist.getGroupId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateArtist: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int deleteArtist(int artistId) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ARTIST_TABLE +
                " WHERE " + ARTISTID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, artistId);
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("L'artista con id {} non esiste, impossibile eliminarlo.", artistId);
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in deleteArtist: " + e.getMessage());
            return -2;
        }

        String sql =
                " DELETE FROM " + ARTIST_TABLE +
                " WHERE " + ARTISTID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error in deleteArtist: {}", e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public List<Genre> getAllGenres(int page) {
        List<Genre> genreList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM " + GENRE_TABLE +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, PAGE_SIZE);
            ps.setInt(2, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    genreList.add(new Genre(rs));
                }
            }
            return genreList;
        } catch (SQLException e) {
            logger.error("Error in getAllGenres: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Genre> getGenreById(int genreId) {
        List<Genre> genreList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + GENRE_TABLE +
                " WHERE " + GENREID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genreId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    genreList.add(new Genre(rs));
                }
            }
            return genreList;
        } catch (SQLException e) {
            logger.error("Error in getGenreById: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int insertGenre(Genre genre) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + GENRE_TABLE +
                " WHERE " + GENREID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, genre.getGenreId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' un genere con id {}, impossibile crearne uno nuovo.", genre.getGenreId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertGenre: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + GENRE_TABLE +
                " ( " + GENREID + ", " + NAME +
                " ) VALUES (?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genre.getGenreId());
            ps.setString(2, genre.getName());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertGenre: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int updateGenre(Genre genre) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + GENRE_TABLE +
                " WHERE " + GENREID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, genre.getGenreId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("Il genere con id {} non esiste, impossibile aggiornarlo.", genre.getGenreId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateGenre: " + e.getMessage());
            return -2;
        }

        String sql =
                " UPDATE " + GENRE_TABLE + " SET " +
                 NAME + " = ? " +
                 " WHERE " + GENREID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genre.getName());
            ps.setInt(2, genre.getGenreId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateGenre: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int deleteGenre(int genreId) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + GENRE_TABLE +
                " WHERE " + GENREID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, genreId);
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("Il genere con id {} non esiste, impossibile eliminarlo.", genreId);
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in deleteGenre: " + e.getMessage());
            return -2;
        }

        String sql =
                " DELETE FROM " + GENRE_TABLE +
                " WHERE " + GENREID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genreId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error in deleteGenre: {}", e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public List<Group> getAllGroups(int page) {
        List<Group> groupList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM " + GROUP_TABLE +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, PAGE_SIZE);
            ps.setInt(2, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    groupList.add(new Group(rs));
                }
            }
            return groupList;
        } catch (SQLException e) {
            logger.error("Error in getAllGroups: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Group> getGroupById(int groupId) {
        List<Group> groupList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + GROUP_TABLE +
                " WHERE " + GROUPID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    groupList.add(new Group(rs));
                }
            }
            return groupList;
        } catch (SQLException e) {
            logger.error("Error in getGroupById: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int insertGroup(Group group) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + GROUP_TABLE +
                " WHERE " + GROUPID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, group.getGroupId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' un gruppo con id {}, impossibile crearne uno nuovo.", group.getGroupId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertGroup: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + GROUP_TABLE +
                " ( " + GROUPID + ", " + NAME +
                " ) VALUES (?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, group.getGroupId());
            ps.setString(2, group.getName());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertGroup: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int updateGroup(Group group) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + GROUP_TABLE +
                " WHERE " + GROUPID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, group.getGroupId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("Il gruppo con id {} non esiste, impossibile aggiornarlo.", group.getGroupId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateGroup: " + e.getMessage());
            return -2;
        }

        String sql =
                " UPDATE " + GROUP_TABLE + " SET " +
                 NAME + " = ? " +
                " WHERE " + GROUPID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, group.getName());
            ps.setInt(2, group.getGroupId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateGroup: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public int deleteGroup(int groupId) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + GROUP_TABLE +
                " WHERE " + GROUPID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, groupId);
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("Il gruppo con id {} non esiste, impossibile eliminarlo.", groupId);
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in deleteGroup: " + e.getMessage());
            return -2;
        }

        String sql =
                " DELETE FROM " + GROUP_TABLE +
                " WHERE " + GROUPID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error in deleteGroup: {}", e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public List<Link> getAllLinks(int page) {
        List<Link> linkList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM " + LINK_TABLE +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, PAGE_SIZE);
            ps.setInt(2, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    linkList.add(new Link(rs));
                }
            }
            return linkList;
        } catch (SQLException e) {
            logger.error("Error in getAllLinks: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Link> getLinksForMusic(int musicId) {
        List<Link> linkList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM " + LINK_TABLE +
                " WHERE " + MUSICID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, musicId);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    linkList.add(new Link(rs));
                }
            }
            return linkList;
        } catch (SQLException e) {
            logger.error("Error in getLinksForMusic: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<MusicJoinLink> musicJoinLink(int page) {
        List<MusicJoinLink> musicList = new ArrayList<>();

        String sql =
                " SELECT M."+MUSICID+", M."+TITLE+", M."+AUTHORID+", M."+ALBUMID+", M."+YEAR+", M."+GENREID+", L."+LINK+
                " FROM "+MUSIC_TABLE+" as M INNER JOIN "+LINK+" as L" +
                " ON M."+MUSICID+" = L."+MUSICID+
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, PAGE_SIZE);
            ps.setInt(2, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new MusicJoinLink(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in musicJoinLink: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int insertLink(Link link) {
        String sql =
                " INSERT INTO " + LINK_TABLE +
                " ( " + MUSICID + ", " + LINK +
                " ) VALUES (?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, link.getMusicId());
            ps.setString(2, link.getLink());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertLink: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public List<Music> getMusicByAlbum(int albumId, int page) {
        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + ALBUMID + " = ? " +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, albumId);
            ps.setInt(2, PAGE_SIZE);
            ps.setInt(3, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new Music(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in getMusicByAlbum: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Music> getMusicByGenre(int genreId, int page) {
        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + GENREID + " = ? " +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genreId);
            ps.setInt(2, PAGE_SIZE);
            ps.setInt(3, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new Music(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in getMusicByGenre: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Music> getMusicByGroup(int groupId, int page) {
        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + AUTHORID + " = ? " +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, PAGE_SIZE);
            ps.setInt(3, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new Music(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in getMusicByGroup: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Music> getMusicByArtist(int artistId, int page) {
        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT M.musicid, M.title, A.artistid AS authorid, M.albumid, M.year, M.genreid " +
                " FROM " + MUSIC_TABLE + " AS M, " + GROUP_TABLE + " AS G, " +
                 ARTIST_TABLE + " AS A " +
                " WHERE M.authorid = G.groupid AND G.groupid = A.groupid AND " +
                " A.artistid = ? " +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            ps.setInt(2, PAGE_SIZE);
            ps.setInt(3, page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new Music(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in getMusicByArtist: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Map<Integer, String> getGroupMap() {
        Map<Integer, String> groupMap = new HashMap<>();

        String sql =
                " SELECT G."+GROUPID+", G."+NAME+" "+
                " FROM " + GROUP_TABLE + " AS G ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    groupMap.put(rs.getInt(1), rs.getString(2));
                }
            }
            return groupMap;
        } catch (SQLException e) {
            logger.error("Error in getGroupMap: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Map<Integer, String> getAlbumMap() {
        Map<Integer, String> albumMap = new HashMap<>();

        String sql =
                " SELECT A."+ALBUMID+", A."+TITLE+" "+
                " FROM " + ALBUM_TABLE + " AS A ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    albumMap.put(rs.getInt(1), rs.getString(2));
                }
            }
            return albumMap;
        } catch (SQLException e) {
            logger.error("Error in getAlbumMap: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Map<Integer, String> getGenreMap() {
        Map<Integer, String> genreMap = new HashMap<>();

        String sql =
                " SELECT G."+GENREID+", G."+NAME+" "+
                " FROM " + GENRE_TABLE + " AS G ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    genreMap.put(rs.getInt(1), rs.getString(2));
                }
            }
            return genreMap;
        } catch (SQLException e) {
            logger.error("Error in getGenreMap: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Artist> getArtistById(int artistId) {
        List<Artist> artistList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + ARTIST_TABLE +
                " WHERE " + ARTISTID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    artistList.add(new Artist(rs));
                }
            }
            return artistList;
        } catch (SQLException e) {
            logger.error("Error in getArtistById: {}", e.getMessage());
            return null;
        }
    }

}
