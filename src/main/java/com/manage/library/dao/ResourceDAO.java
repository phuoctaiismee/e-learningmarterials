package com.manage.library.dao;

import com.manage.library.model.Resource;
import com.manage.library.utils.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO extends GlobalDAO<Resource, Integer> {

    private static final String INSERT_SQL = "INSERT INTO Resources (Name, Type_Id, Topic_Id, Parent_Id, Url) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Resources SET Name = ?, Url = ? WHERE Id = ?";
    private static final String DELETE_SQL = "DELETE FROM Resources WHERE Id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Resources";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Resources WHERE Id = ?";
    private static final String SELECT_BY_NAME_AND_TOPICID_SQL = "SELECT * FROM Resources WHERE Name = ? And Topic_Id = ?";
    private static final String SELECT_BY_NAME_AND_PARENTID_SQL = "SELECT * FROM Resources WHERE Name = ? And Parent_Id = ?";
    private static final String SELECT_BY_PARENT_ID_SQL = "SELECT * FROM Resources WHERE Parent_Id = ?";
    private static final String SELECT_BY_TOPIC_ID_SQL = "SELECT * FROM Resources WHERE Topic_Id = ?";

    @Override
    public void insert(Resource entity) {
        JdbcHelper.update(INSERT_SQL, entity.getName(), entity.getTypeId(), entity.getTopicId() != 0 ? entity.getTopicId() : null, entity.getParentId() != 0 ? entity.getParentId() : null, entity.getUrl());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public void update(Resource entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getName(), entity.getUrl(), entity.getId());
    }

    @Override
    public List<Resource> select() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Resource selectID(Integer id) {
        List<Resource> list = selectBySql(SELECT_BY_ID_SQL, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Resource selectNameandTopicId(String name, Integer topicID) {
        List<Resource> list = selectBySql(SELECT_BY_NAME_AND_TOPICID_SQL, name, topicID);
        return list.isEmpty() ? null : list.get(0);
    }

    public Resource selectNameandParentId(String name, Integer parentID) {
        List<Resource> list = selectBySql(SELECT_BY_NAME_AND_PARENTID_SQL, name, parentID);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Resource> selectParentId(Integer parentId) {
        List<Resource> list = selectBySql(SELECT_BY_PARENT_ID_SQL, parentId);
        return list;
    }
    
        public List<Resource> selectTopicId(Integer topicID) {
        List<Resource> list = selectBySql(SELECT_BY_TOPIC_ID_SQL, topicID);
        return list;
    }

    @Override
    protected List<Resource> selectBySql(String sql, Object... args) {
        List<Resource> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Resource entity = new Resource();
                entity.setId(rs.getInt("ID"));
                entity.setName(rs.getString("NAME"));
                entity.setUrl(rs.getString("URL"));
                entity.setTypeId(rs.getInt("TYPE_ID"));
                entity.setTopicId(rs.getInt("TOPIC_ID"));
                entity.setParentId(rs.getInt("PARENT_ID"));
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
