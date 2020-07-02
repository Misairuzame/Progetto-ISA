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

    List<JoinAll> joinAll();

    List<MusicJoinLink> musicJoinLink();

    List<MusicStrings> searchMusic(String searchTerm, int page);

}
