package com.manage.library.dao;

import com.manage.library.model.Resource;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import com.manage.library.utils.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO extends GlobalDAO<Subject, Integer> {

    private static final String INSERT_SQL = "INSERT INTO Subjects (Name) VALUES (?)";
    private static final String UPDATE_SQL = "UPDATE Subjects SET Name = ? WHERE Id = ?";
    private static final String DELETE_SQL = "DELETE FROM Subjects WHERE Id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Subjects";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Subjects WHERE Id = ?";
    private static final String SELECT_BY_NAME_SQL = "SELECT * FROM Subjects WHERE Name = ?";

    // Câu truy vấn để lấy danh sách topics của một subject
    private static final String SELECT_TOPICS_BY_SUBJECT_ID = "SELECT * FROM Topics WHERE Subject_Id = ?";

    private ResourceDAO resouceDAO = new ResourceDAO();
    
    @Override
    public Subject selectID(Integer id) {
        List<Subject> list = selectBySql(SELECT_BY_ID_SQL, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Subject selectName(String name) {
        List<Subject> list = selectBySql(SELECT_BY_NAME_SQL, name);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Subject> select() {
        List<Subject> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(SELECT_ALL_SQL);
            while (rs.next()) {
                Subject entity = new Subject();
                entity.setId(rs.getInt("ID"));
                entity.setName(rs.getString("NAME"));

                // Lấy danh sách topics của subject
                List<Topic> topics = getTopicsBySubjectId(entity.getId());
                entity.setTopics(topics);

                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Topic> getTopicsBySubjectId(int subjectId) {
        List<Topic> topics = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(SELECT_TOPICS_BY_SUBJECT_ID, subjectId);
            while (rs.next()) {
                Topic topic = new Topic();
                topic.setId(rs.getInt("ID"));
                topic.setName(rs.getString("NAME"));
                List<Resource> res = resouceDAO.selectTopicId(topic.getId());
                topic.setResources(res);
                topics.add(topic);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return topics;
    }
    
  
    @Override
    public void insert(Subject entity) {
        try {
            JdbcHelper.update(INSERT_SQL, entity.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public void update(Subject entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getName(), entity.getId());
    }

    @Override
    protected List<Subject> selectBySql(String sql, Object... args) {
        List<Subject> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Subject entity = new Subject();
                entity.setId(rs.getInt("ID"));
                entity.setName(rs.getString("NAME"));
                List<Topic> listTopics = getTopicsBySubjectId(entity.getId());
                entity.setTopics(listTopics);
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
