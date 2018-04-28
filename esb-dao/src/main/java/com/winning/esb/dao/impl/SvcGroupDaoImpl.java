package com.winning.esb.dao.impl;

import com.winning.esb.dao.ISvcGroupDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.SvcGroupModel;
import com.winning.esb.model.common.CommonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/9
 */
@Repository
public class SvcGroupDaoImpl implements ISvcGroupDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_SvcGroup";

    @Override
    public void insert(SvcGroupModel obj) {
        String sql = "insert into " + TB_NAME + " (name) values(:name)";
        Map<String, Object> map = new HashMap<>();
        map.put("name", obj.getName());
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void update(SvcGroupModel obj) {
        String sql = "update " + TB_NAME + " set name = :name where id = :id ";
        Map<String, Object> map = new HashMap<>();
        map.put("id", obj.getId());
        map.put("name", obj.getName());
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void delete(Integer id) {
        String sql = "delete from " + TB_NAME + " where id = :id ";
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public SvcGroupModel getByID(Integer id) {
        String sql = "select * from " + TB_NAME + " where id = :id ";
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, map, new BeanPropertyRowMapper<>(SvcGroupModel.class));
    }

    @Override
    public CommonObject query(Map map) {
        //生成筛选条件
        StringBuffer sqlWhere = new StringBuffer();
        if (map != null) {
            Object temp = map.get("queryWord");
            if (temp != null && !StringUtils.isEmpty(String.valueOf(temp))) {
                map.put("queryWord", "%" + temp + "%");
                sqlWhere.append("and name like  :queryWord  ");
            }
            temp = map.get("name");
            if (temp != null) {
                sqlWhere.append("and name = :name ");
            }
        }
        return commonHandle.query(TB_NAME, map, sqlWhere, SvcGroupModel.class);
    }

}