package com.winning.esb.dao.impl;

import com.winning.esb.dao.IConfigsDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.QueryParameterKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 17/07/27.
 */
@Repository
public class ConfigsDaoImpl implements IConfigsDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String DB_NAME = "ESB_Configs";

    @Override
    public CommonObject query(Map map) {
        CommonObject commonObject = new CommonObject();
        StringBuffer sqlSelect = new StringBuffer();
        StringBuffer sqlCount = new StringBuffer();

        List<ConfigsModel> resultList;
        int count;
        if (map == null) {
            sqlSelect.append("select * from " + DB_NAME);
            resultList = namedParameterJdbcTemplate.query(sqlSelect.toString(), map,
                    new BeanPropertyRowMapper<>(ConfigsModel.class));
            count = resultList.size();
        } else {
            int startIndex = 0, pageSize = 0;
            Object temp = map.get(QueryParameterKeys.STARTINDEX.getKey());
            if (temp != null) {
                startIndex = (int) temp;
            }
            temp = map.get(QueryParameterKeys.PAGESIZE.getKey());
            if (temp != null) {
                pageSize = (int) temp;
            }
            int endIndex = startIndex + pageSize;

            //生成筛选条件
            StringBuffer sqlWhere = new StringBuffer();
            temp = map.get("queryWord");
            if (temp != null && !StringUtils.isEmpty(String.valueOf(temp))) {
                map.put("queryWord","%"+temp+"%");
                sqlWhere.append("and  (code like  :queryWord   or name like   :queryWord)   ");
            }
            temp = map.get("code");
            if (temp != null && temp !="") {
                sqlWhere.append("and code = :code ");
            }
            temp = map.get("name");
            if (temp != null && temp !="") {
                sqlWhere.append("and name = :name ");
            }
            temp = map.get("type");
            if (temp != null && temp !="") {
                sqlWhere.append("and type in (:type) ");
            }
            temp = map.get("visible");
            if (temp != null && temp !="") {
                sqlWhere.append("and visible = :visible ");
            }

            String where;
            if (sqlWhere.length() > 0) {
                sqlWhere = com.winning.esb.utils.StringUtils.trimLeft(sqlWhere);
                if (sqlWhere.indexOf("and ") == 0 || sqlWhere.indexOf("AND ") == 0) {
                    where = " WHERE " + sqlWhere.substring(4);
                } else if (sqlWhere.length() > 5 && !"WHERE".equals(sqlWhere.substring(0, 5).toUpperCase())) {
                    where = " WHERE " + sqlWhere.toString();
                } else {
                    where = sqlWhere.toString();
                }
                where = where + " ";
            } else {
                where = "";
            }

            //获取总数
            sqlCount.append("SELECT COUNT(*) FROM " + DB_NAME + "(NOLOCK) ");
            sqlCount.append(where);
            count = namedParameterJdbcTemplate.queryForObject(sqlCount.toString(), map, Integer.class);
            //设置筛选脚本
            boolean ascendOrder = false;
            if (pageSize > 0) { //按照分页结构返回
                String orderBack = ascendOrder ? "ASC" : "DESC";
                String orderFilter = ascendOrder ? "DESC" : "ASC";
                if (endIndex >= count) {
                    //如果是最后一页，则需要单独处理
                    sqlSelect.append("SELECT TOP " + (count - startIndex) + " * FROM " + DB_NAME + "(NOLOCK) ");
                    sqlSelect.append(where);
                    sqlSelect.append("ORDER BY code "+orderFilter+" ");
                } else {
                    //其他页码
                    sqlSelect.append("SELECT * FROM " + DB_NAME + "(NOLOCK) WHERE code IN (");
                    sqlSelect.append("SELECT TOP " + pageSize + " code FROM ( ");
                    sqlSelect.append("SELECT TOP " + endIndex + " code FROM " + DB_NAME + "(NOLOCK) ");
                    sqlSelect.append(where);
                    sqlSelect.append("ORDER BY code "+orderBack+") tb ORDER BY code "+orderFilter+") ORDER BY code "+orderBack+" ");
                }
                //获取记录
                resultList = namedParameterJdbcTemplate.query(sqlSelect.toString(), map, new BeanPropertyRowMapper<>(ConfigsModel.class));
            } else { //按照普通查询结构返回
                //获取记录
                sqlSelect.append("SELECT * FROM " + DB_NAME + "(NOLOCK) ");
                sqlSelect.append(where);
                resultList = namedParameterJdbcTemplate.query(sqlSelect.toString(), map, new BeanPropertyRowMapper<>(ConfigsModel.class));
            }
        }

        commonObject.setTotalSize(count);
        commonObject.setDatas(resultList);
        return commonObject;
    }

    @Override
    public void insert(ConfigsModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + DB_NAME + " (code, name, value, desp, type, regex, visible, ctime, mtime) ");
        sql.append("values(:code, :name, :value, :desp, :type, :regex, :visible, :ctime, :mtime)");
        Map<String, Object> map = new HashMap<>();
        map.put("code", obj.getCode());
        map.put("name", obj.getName());
        map.put("value", obj.getValue());
        map.put("desp", obj.getDesp());
        map.put("type", obj.getType());
        map.put("regex", obj.getRegex());
        map.put("visible", obj.getVisible());
        map.put("ctime", obj.getCtime());
        map.put("mtime", obj.getMtime());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public String editValue(ConfigsModel obj) {
        String err = "";
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("update " + DB_NAME + " set value = :value, mtime = :mtime where code = :code ");
            Map<String, Object> map = new HashMap<>();
            map.put("code", obj.getCode());
            map.put("value", obj.getValue());
            map.put("mtime", new Date());
            namedParameterJdbcTemplate.update(sql.toString(), map);
        }catch (Exception e){
            err = String.valueOf(e);
        }
        return err;
    }

    @Override
    public List<ConfigsModel> getByCode(Object code) {
//        String sql = "select * from " + DB_NAME + " where code = :code";
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//        ConfigsModel result = namedParameterJdbcTemplate.query(sql, map, RowMapper<ConfigsModel.class>);
//        return result;

        return commonHandle.listByColumn(DB_NAME, "code", code, ConfigsModel.class);
    }

    @Override
    public void delete(String code) {
        String sql = "delete from " + DB_NAME + " where code = :code ";
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        namedParameterJdbcTemplate.update(sql, map);
    }
}