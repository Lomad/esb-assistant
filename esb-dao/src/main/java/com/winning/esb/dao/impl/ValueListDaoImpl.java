package com.winning.esb.dao.impl;

import com.winning.esb.dao.IValueListDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ValueListDaoImpl implements IValueListDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    public static final String TB_NAME = "ESB_ValueList";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insert(Integer ssid, List<ValueListModel> list, boolean deleteBeforeInsert) {
        StringBuilder sql = new StringBuilder();
        Map<String, Object> map = new HashMap<>();
        ValueListModel obj;
        //拼接Delete脚本
        if (deleteBeforeInsert) {
            sql.append(" delete from ").append(TB_NAME).append(" where ssid = :ssid").append(" ");
        }
        //拼接Insert脚本
        for (int i = 0, len = list.size(); i < len; i++) {
            obj = list.get(i);
            sql.append(" insert into ").append(TB_NAME);
            sql.append(" (ssid, type, value, desp) ");
            sql.append("values(:ssid");
            sql.append(", :type_").append(i);
            sql.append(", :value_").append(i);
            sql.append(", :desp_").append(i);
            sql.append(") ");
            //参数
            map.put("type_" + i, obj.getType());
            map.put("value_" + i, obj.getValue());
            map.put("desp_" + i, obj.getValue());
        }
        map.put("ssid", ssid);
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void delete(Integer ssid) {
        commonHandle.delete(TB_NAME, "ssid", ssid);
    }

    @Override
    public List<ValueListModel> queryBySid(Integer sid, Integer direction) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from ").append(TB_NAME).append("(nolock) where ssid in ");
        sql.append("(select id from ").append(SvcStructureDaoImpl.TB_NAME).append("(nolock) where sid=:sid and direction=:direction) ");
        Map<String, Object> map = new HashMap<>();
        map.put("sid", sid);
        map.put("direction", direction);
        return namedParameterJdbcTemplate.query(sql.toString(), map, new BeanPropertyRowMapper<>(ValueListModel.class));
    }

    @Override
    public List<ValueListModel> queryBySsid(Integer ssid, Integer type) {
        StringBuffer sqlWhere = new StringBuffer();
        Map map = new HashMap();

        sqlWhere.append("and ssid = :ssid ");
        map.put("ssid", ssid);

        if (type != null) {
            sqlWhere.append("and type = :type ");
            map.put("type", type);
        }

        CommonObject commonObject = commonHandle.query(TB_NAME, map, sqlWhere, ValueListModel.class);
        return (commonObject.getDatas() != null && commonObject.getDatas().size() > 0) ?
                ListUtils.transferToList(commonObject.getDatas()) : null;
    }
}