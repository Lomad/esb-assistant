package com.winning.esb.dao.impl;

import com.winning.esb.dao.ISvcUrlDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.UserModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SvcUrlDaoImpl implements ISvcUrlDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String DB_NAME = "ESB_SvcUrl";

    @Override
    public void insert(SvcUrlModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(DB_NAME).append(" (svcType,  url, status, name, desp, esbAgent) ");
        sql.append(" values(:svcType, :url, :status, :name, :desp, :esbAgent)");
        Map<String, Object> map = new HashMap<>();
        map.put("svcType", obj.getSvcType());
        map.put("url", obj.getUrl());
        map.put("status", obj.getStatus());
        map.put("name", obj.getName());
        map.put("desp", obj.getDesp());
        map.put("esbAgent", obj.getEsbAgent());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void update(SvcUrlModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update ").append(DB_NAME)
                .append(" set svcType = :svcType, ")
                .append(" url = :url, ")
                .append(" status = :status, ")
                .append(" name = :name, ")
                .append(" desp = :desp, ")
                .append(" esbAgent = :esbAgent ")
                .append(" where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("svcType", obj.getSvcType());
        map.put("url", obj.getUrl());
        map.put("status", obj.getStatus());
        map.put("name", obj.getName());
        map.put("desp", obj.getDesp());
        map.put("esbAgent", obj.getEsbAgent());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void delete(Integer id) {
        commonHandle.delete(DB_NAME, id);
    }

    @Override
    public CommonObject query(Map map) {
        StringBuffer sqlWhere = new StringBuffer();
        if(map != null){
            Object temp = map.get("queryWord");
            if (temp !=null && temp != "") {
                map.put("queryWord", "%" + temp + "%");
                sqlWhere.append("and (url like :queryWord or name like :queryWord)  ");
            }
            temp = map.get("url");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and url = :url ");
            }
            temp = map.get("name");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and name = :name ");
            }
            temp = map.get("esbAgent");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and esbAgent = :esbAgent ");
            }
        }
        return commonHandle.query(DB_NAME, map, sqlWhere, SvcUrlModel.class);
    }

    @Override
    public List<SvcUrlModel> getByID(List<Integer> idList) {
        return commonHandle.listByKey(DB_NAME, idList, SvcUrlModel.class);
    }

    @Override
    public SvcUrlModel getByUrl(String url) {
        return commonHandle.getByColumn(DB_NAME, "url", url, SvcUrlModel.class);
    }

    @Override
    public Integer getMaxId(){
        String sql = "select top 1 id from "+ DB_NAME + " order by id desc";
        Map<String,Object> map = new HashMap<>();
        return namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
    }
}
