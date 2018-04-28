package com.winning.esb.dao.impl;

import com.winning.esb.dao.IOrgInfoDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.OrgInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/9
 */
@Repository
public class OrgInfoDaoImpl implements IOrgInfoDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;
    private final String TB_NAME = "ESB_OrgInfo";

    @Override
    public void insert(OrgInfoModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + TB_NAME + " (code, name, logo, order_num, desp, ctime, mtime) ");
        sql.append("values(:code, :name, :logo, :order_num, :desp, :ctime, :mtime)");
        Map<String, Object> map = new HashMap<>();
        map.put("code", obj.getCode());
        map.put("name", obj.getName());
        map.put("logo", obj.getLogo());
        map.put("desp", obj.getDesp());
        map.put("order_num", obj.getOrder_num());
        map.put("ctime", obj.getCtime());
        map.put("mtime", obj.getMtime());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void update(OrgInfoModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + TB_NAME);
        sql.append(" set code = :code ");
        sql.append(", name = :name ");
        sql.append(", logo = :logo ");
        sql.append(", desp = :desp ");
        sql.append(", mtime = :mtime ");
        sql.append(", order_num = :order_num");
        sql.append(" where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("id", obj.getId());
        map.put("code", obj.getCode());
        map.put("name", obj.getName());
        map.put("logo", obj.getLogo());
        map.put("desp", obj.getDesp());
        map.put("mtime", obj.getMtime());
        map.put("order_num", obj.getOrder_num());
        namedParameterJdbcTemplate.update(sql.toString(), map);

    }

    @Override
    public void delete(Integer id) {
        commonHandle.delete(TB_NAME, id);
    }

    @Override
    public List<OrgInfoModel> getByID(Object id) {
        return commonHandle.listByKey(TB_NAME, id, OrgInfoModel.class);
    }

    @Override
    public CommonObject query(Map map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (map != null) {
            Object temp = map.get("queryWord");
            if (!StringUtils.isEmpty(temp)) {
                map.put("queryWord", "%" + temp + "%");
                sqlWhere.append("and (code like :queryWord or name like :queryWord) ");
            }
            temp = map.get("code");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and code = :code ");
            }
            temp = map.get("name");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and name = :name ");
            }
            temp = map.get("nameList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and name in (:nameList) ");
            }
        }
        LinkedHashMap<String, String> orderColumns = new LinkedHashMap<>();
        orderColumns.put("order_num", DatabaseConst.ORDER_ASC);
        return commonHandle.query(TB_NAME, map, sqlWhere, orderColumns, OrgInfoModel.class);
    }
}