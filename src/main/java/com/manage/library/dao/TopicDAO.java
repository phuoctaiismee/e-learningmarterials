package com.manage.library.dao;

import com.manage.library.model.Resource;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import com.manage.library.utils.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopicDAO extends GlobalDAO<Topic, Integer> {

    private static final String INSERT_SQL = "INSERT INTO Topics (Name, Subject_Id) VALUES (?, ?)";
    private static final String UPDATE_SQL = "UPDATE Topics SET Name = ? WHERE Id = ?";
    private static final String DELETE_SQL = "DELETE FROM Topics WHERE Id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Topics";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Topics WHERE Id = ?";
    private static final String SELECT_BY_NAME_AND_SUBJECTID_SQL = "SELECT * FROM Topics WHERE Name = ? And Subject_Id = ?";

    // Lấy RESOURCE DỰA VÀO TOPIC
    private static final String SELECT_RESOURCES_BY_TOPIC_ID_SQL = "SELECT * FROM Resources WHERE Topic_Id = ?";
    private static final String SELECT_RESOURCES_BY_PARENT_ID_SQL = "SELECT * FROM Resources WHERE Parent_Id = ?";

// Phương thức để lấy danh sách resources cho một chủ đề và các resources bên trong
    private List<Resource> getResourcesByTopicId(int topicId) {
        List<Resource> resources = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(SELECT_RESOURCES_BY_TOPIC_ID_SQL, topicId);
            while (rs.next()) {
                Resource entity = new Resource();
                entity.setId(rs.getInt("ID"));
                entity.setName(rs.getString("NAME"));
                entity.setTypeId(rs.getInt("TYPE_ID"));
                entity.setTopicId(rs.getInt("TOPIC_ID"));
                entity.setUrl(rs.getString("URL"));
                entity.setParentId(rs.getInt("PARENT_ID"));

                // Gọi đệ quy để lấy danh sách các resources bên trong nếu có
                List<Resource> childResources = getResourcesByParentId(entity.getId());
                resources.addAll(childResources);

                resources.add(entity);
            }
            rs.getStatement().getConnection().close();
            return resources;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

// Phương thức để lấy danh sách resources cho một parent_id
    private List<Resource> getResourcesByParentId(int parentId) {
        List<Resource> resources = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(SELECT_RESOURCES_BY_PARENT_ID_SQL, parentId);
            while (rs.next()) {
                Resource entity = new Resource();
                entity.setId(rs.getInt("ID"));
                entity.setName(rs.getString("NAME"));
                entity.setTypeId(rs.getInt("TYPE_ID"));
                entity.setTopicId(rs.getInt("TOPIC_ID"));
                entity.setUrl(rs.getString("URL"));
                entity.setParentId(rs.getInt("PARENT_ID"));
                resources.add(entity);
            }
            rs.getStatement().getConnection().close();
            return resources;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Topic entity) {
        JdbcHelper.update(INSERT_SQL, entity.getName(), entity.getSubjectId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public void update(Topic entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getName(), entity.getId());
    }

    @Override
    public List<Topic> select() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Topic selectID(Integer id) {
        List<Topic> list = selectBySql(SELECT_BY_ID_SQL, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Topic selectName(String name, Integer subId) {
        List<Topic> list = selectBySql(SELECT_BY_NAME_AND_SUBJECTID_SQL, name, subId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    protected List<Topic> selectBySql(String sql, Object... args) {
        List<Topic> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Topic entity = new Topic();
                entity.setId(rs.getInt("ID"));
                entity.setName(rs.getString("NAME"));
                entity.setSubjectId(rs.getInt("SUBJECT_ID"));

                // Gọi phương thức để lấy danh sách resources cho chủ đề và các resources bên trong
                List<Resource> resources = getResourcesByTopicId(entity.getId());
                entity.setResources(resources);
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
