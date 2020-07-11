package com.gb.DAO;

import com.gb.modelObject.Album;

import java.util.List;
import java.util.Map;

public interface AlbumDAO {

    List<Album> getAllAlbums(int page);

    List<Album> getAlbumById(int albumId);

    int deleteAlbum(int albumId);

    int insertAlbum(Album album);

    int updateAlbum(Album album);

    Map<Integer, String> getAlbumMap();

}
