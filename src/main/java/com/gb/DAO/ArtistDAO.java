package com.gb.DAO;

import com.gb.modelObject.Artist;
import com.gb.modelObject.ArtistJoinGroup;

import java.util.List;

public interface ArtistDAO {

    List<Artist> getAllArtists(int page);

    List<ArtistJoinGroup> artistJoinGroup(int page);

    int updateArtist(Artist artist);

    int insertArtist(Artist artist);

    int deleteArtist(int artistId);

    List<Artist> getArtistById(int artistId);

}
