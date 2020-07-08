package com.gb.DAO;

import com.gb.modelObject.Link;

import java.util.List;

public interface LinkDAO {

    List<Link> getAllLinks(int page);

    List<Link> getLinksForMusic(int musicId);

    int insertLink(Link link);

}
