package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class Group {

    private Integer groupId;
    private String name;

    private static final Logger logger = LoggerFactory.getLogger(Group.class);

    public Group() {
    }

    public Group(ResultSet rs) {
        try {
            setGroupId(rs.getInt(GROUPID));
            setName(rs.getString(NAME));
        } catch(SQLException e) {
            logger.error("Error creating Group object: {}", e.getMessage());
        }
    }

    public Group(Integer groupId, String name) {
        setGroupId(groupId);
        setName(name);
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        if(groupId < 0) {
            throw new IllegalArgumentException("GroupId deve essere > 0.");
        }
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name.length() > 100) {
            throw new IllegalArgumentException("Lunghezza nome gruppo deve essere < 100.");
        }
        this.name = name;
    }

}
