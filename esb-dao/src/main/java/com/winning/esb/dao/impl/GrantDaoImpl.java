package com.winning.esb.dao.impl;

import com.winning.esb.dao.IGrantDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.GrantModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GrantDaoImpl implements IGrantDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_Grant";

    @Override
    public Integer insert(GrantModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + TB_NAME + " (aid, sid, apply_time, lic_key, secret_key, approve_time, approve_state, ctime, mtime) ");
        sql.append("values(:aid, :sid, :apply_time, :lic_key, :secret_key, :approve_time, :approve_state, :ctime, :mtime) ");
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void insert(List<GrantModel> list) {
        if (!ListUtils.isEmpty(list)) {
            StringBuffer sql = new StringBuffer();
            Map<String, Object> map = new HashMap<>();
            GrantModel obj;
            for (int i = 0, len = list.size(); i < len; i++) {
                obj = list.get(i);
                sql.append("IF NOT EXISTS(SELECT TOP 1 1 FROM " + TB_NAME
                        + " WHERE aid = :aid_" + i + " and sid = :sid_" + i + ") ");
                sql.append("insert into " + TB_NAME + " (aid, sid, apply_time, lic_key, secret_key, approve_time, approve_state, ctime, mtime) ");
                sql.append("values(:aid_" + i + ", :sid_" + i + ", :apply_time_" + i + ", :lic_key_" + i + ", :secret_key_" + i +
                        ", :approve_time_" + i + ", :approve_state_" + i + ", :ctime_" + i + ", :mtime_" + i + ") ");
                map.put("aid_" + i, obj.getAid());
                map.put("sid_" + i, obj.getSid());
                map.put("apply_time_" + i, obj.getApply_time());
                map.put("lic_key_" + i, obj.getLic_key());
                map.put("secret_key_" + i, obj.getSecret_key());
                map.put("approve_time_" + i, obj.getApprove_time());
                map.put("approve_state_" + i, obj.getApprove_state());
                map.put("mtime_" + i, obj.getMtime());
                map.put("ctime_" + i, obj.getCtime());
            }
            namedParameterJdbcTemplate.update(sql.toString(), map);
        }
    }

    @Override
    public void update(GrantModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append(" update ").append(TB_NAME);
        sql.append(" set lic_key = :lic_key, secret_key = :secret_key, approve_time = :approve_time, ");
        sql.append(" approve_state = :approve_state, mtime = :mtime where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("lic_key", obj.getLic_key());
        map.put("secret_key", obj.getSecret_key());
        map.put("approve_time", obj.getApprove_time());
        map.put("approve_state", obj.getApprove_state());
        map.put("mtime", obj.getMtime());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void delete(List<Integer> idList) {
        commonHandle.delete(TB_NAME, idList);
    }

    @Override
    public void deleteBySid(List<Integer> sidList) {
        commonHandle.delete(TB_NAME, "sid", sidList);
    }

    @Override
    public List<GrantModel> queryByAid(List<Integer> aidList) {
        return commonHandle.listByColumn(TB_NAME, "aid", aidList, GrantModel.class);
    }

    @Override
    public CommonObject query(Map map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (map != null) {
            Object temp;
            temp = map.get("aid");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append(" and aid = :aid ");
            }

            temp = map.get("aidList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append(" and aid in (:aidList) ");
            }

            temp = map.get("sid");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append(" and sid = :sid ");
            }

            temp = map.get("sidList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append(" and sid in (:sidList) ");
            }

            temp = map.get("approve_state");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append(" and approve_state = :approve_state ");
            }
        }
        return commonHandle.query(TB_NAME, map, sqlWhere, GrantModel.class);
    }

    @Override
    public void approveState(GrantModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append(" update ").append(TB_NAME);
        sql.append(" set approve_time = :approve_time, approve_state = :approve_state, mtime = :mtime where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("approve_time", obj.getApprove_time());
        map.put("approve_state", obj.getApprove_state());
        map.put("mtime", obj.getMtime());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public List<Integer> getAidGrantAllSvc() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT aid FROM (SELECT g.aid, COUNT(*) AS svcCountGrant, ");
        sql.append("(SELECT COUNT(*) FROM ESB_SvcInfo WHERE aid<>g.aid) AS svcCountOtherApp ");
        sql.append("FROM ESB_Grant AS g GROUP BY aid ) tb WHERE tb.svcCountGrant>=tb.svcCountOtherApp ");
        Map map = null;
        return namedParameterJdbcTemplate.queryForList(sql.toString(), map, Integer.class);
    }

}