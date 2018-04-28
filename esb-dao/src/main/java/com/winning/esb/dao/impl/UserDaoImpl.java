package com.winning.esb.dao.impl;

import com.winning.esb.dao.IUserDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.UserModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDaoImpl implements IUserDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_User";

    @Override
    public Integer insert(UserModel obj) {
        String sql = "insert into " + TB_NAME + " (username, password, role) values(:username, :password, :role)";
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(UserModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + TB_NAME + " set username = :username");
        sql.append(", role = :role ");
        sql.append(" where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("username", obj.getUsername());
        map.put("role", obj.getRole());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void changePwd(UserModel obj) {
        String sql = "update " + TB_NAME + " set password = :password where id = :id ";
        Map<String, Object> map = new HashMap<>();
        map.put("password", obj.getPassword());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void delete(Integer id) {
        commonHandle.delete(TB_NAME, id);
    }

    @Override
    public CommonObject query(Map map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (map != null) {
            Object temp = map.get("queryWord");
            if (!StringUtils.isEmpty(temp)) {
                map.put("queryWord", "%" + temp + "%");
                sqlWhere.append("and username like :queryWord ");
            }
            temp = map.get("username");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and username = :username ");
            }
            temp = map.get("password");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and password = :password ");
            }
        }
        return commonHandle.query(TB_NAME, map, sqlWhere, UserModel.class);
    }
}