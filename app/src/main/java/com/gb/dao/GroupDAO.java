package com.gb.dao;

import com.gb.modelObject.Group;

import java.util.List;
import java.util.Map;

public interface GroupDAO {

    List<Group> getAllGroups(int page);

    List<Group> getGroupById(int groupId);

    int insertGroup(Group group);

    int updateGroup(Group group);

    int deleteGroup(int groupId);

    Map<Integer, String> getGroupMap();

}
