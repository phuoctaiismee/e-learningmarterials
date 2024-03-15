package com.manage.library.dao;

import com.manage.library.model.ResourceType;
import com.manage.library.utils.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceTypeDAO extends GlobalDAO<ResourceType, Integer> {

    private static final String INSERT_SQL = "INSERT INTO ResourceTypes (Name) VALUES (?)";
    private static final String UPDATE_SQL = "UPDATE ResourceTypes SET Name = ? WHERE Id = ?";
    private static final String DELETE_SQL = "DELETE FROM ResourceTypes WHERE Id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM ResourceTypes";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM ResourceTypes WHERE Id = ?";

    @Override
    public void insert(ResourceType entity) {
        JdbcHelper.update(INSERT_SQL, entity.getName());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public void update(ResourceType entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getName(), entity.getId());
    }

    @Override
    public List<ResourceType> select() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public ResourceType selectID(Integer id) {
        List<ResourceType> list = selectBySql(SELECT_BY_ID_SQL, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    protected List<ResourceType> selectBySql(String sql, Object... args) {
        List<ResourceType> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                ResourceType entity = new ResourceType();
                entity.setId(rs.getInt("ID"));
                entity.setName(rs.getString("NAME"));
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
