package com.gb.DAO;

import com.gb.modelObject.JoinAll;
import com.gb.modelObject.Music;
import com.gb.modelObject.MusicJoinLink;
import com.gb.modelObject.MusicStrings;

import java.util.List;

public interface MusicDAO {

    List<Music> getAllMusic(int page);

    List<Music> getMusicById(int musicId);

    int updateMusic(Music music);

    int insertMusic(Music music);

    int deleteMusic(int id);

    List<JoinAll> joinAll(int page);

    List<MusicJoinLink> musicJoinLink(int page);

    List<MusicStrings> searchMusic(String searchTerm, int page);

    List<Music> getMusicByAlbum(int albumId, int page);

    List<Music> getMusicByGenre(int genreId, int page);

    List<Music> getMusicByGroup(int groupId, int page);

}
