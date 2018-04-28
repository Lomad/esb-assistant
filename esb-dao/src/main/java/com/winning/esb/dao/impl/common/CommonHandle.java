package com.winning.esb.dao.impl.common;

import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.QueryParameterKeys;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by xuehao on 2017/8/21.
 */
@Component
public class CommonHandle {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void delete(String tbName, Object keyValue) {
        delete(tbName, "id", keyValue);
    }

    public void delete(String tbName, String columnName, Object columnValue) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(tbName).append(" where ").append(columnName);
        if (columnValue instanceof List) {
            sql.append(" in (:").append(columnName).append(") ");
        } else {
            sql.append(" = :").append(columnName).append(" ");
        }
        Map<String, Object> map = new HashMap<>();
        map.put(columnName, columnValue);
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    public <T> T getByKey(String tbName, Object keyValue, Class<T> clazz) {
        return getByColumn(tbName, "id", keyValue, clazz);
    }

    public <T> T getByColumn(String tbName, String colName, Object colValue, Class<T> clazz) {
        List<T> list = listByColumn(tbName, colName, colValue, 1, clazz);
        return (list == null || list.size() < 1) ? null : list.get(0);
    }

    public <T> List<T> listByKey(String tbName, Object keyValue, Class<T> clazz) {
        return listByColumn(tbName, "id", keyValue, clazz);
    }

    public <T> List<T> listByColumn(String tbName, Class<T> clazz) {
        return listByColumn(tbName, null, null, 0, clazz);
    }

    public <T> List<T> listByColumn(String tbName, String colName, Object colValue, Class<T> clazz) {
        return listByColumn(tbName, colName, colValue, 0, clazz);
    }

    public <T> List<T> listByColumn(String tbName, String colName, Object colValue, int topN, Class<T> clazz) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        if (topN > 0) {
            sql.append(" top ").append(topN);
        }
        sql.append(" * from ").append(tbName).append("(nolock) ");
        Map<String, Object> map;
        if (colName != null && colName.length() > 0) {
            sql.append(" where ").append(colName);
            if (colValue instanceof List) {
                sql.append(" in (:").append(colName).append(") ");
            } else {
                sql.append(" = :").append(colName).append(" ");
            }
            map = new HashMap<>();
            map.put(colName, colValue);
        } else {
            map = null;
        }
        return namedParameterJdbcTemplate.query(sql.toString(), map, new BeanPropertyRowMapper<>(clazz));
    }

    /**
     * 通用的查询接口，支持分页
     */
    public <T> List<T> queryList(String tbName, Map map, StringBuffer sqlWhere, Class<T> clazz) {
        return queryList(tbName, map, sqlWhere, null, clazz);
    }

    /**
     * 通用的查询接口，支持分页
     */
    public <T> List<T> queryList(String tbName, Map map, StringBuffer sqlWhere, LinkedHashMap<String, String> orderColumns, Class<T> clazz) {
        CommonObject commonObject = query(tbName, null, map, sqlWhere, orderColumns, clazz);
        return ListUtils.transferToList(commonObject.getDatas());
    }

    /**
     * 通用的查询接口，支持分页
     */
    public <T> CommonObject query(String tbName, Map map, StringBuffer sqlWhere, Class<T> clazz) {
        return query(tbName, null, map, sqlWhere, null, clazz);
    }

    /**
     * 通用的查询接口，支持分页
     */
    public <T> CommonObject query(String tbName, Map map, StringBuffer sqlWhere,
                                  LinkedHashMap<String, String> orderColumns, Class<T> clazz) {
        return query(tbName, null, map, sqlWhere, orderColumns, clazz);
    }

    /**
     * 通用的查询接口，支持分页
     *
     * @param cols        待返回的列名
     * @param map         筛选条件
     * @param sqlWhere
     */
    public <T> CommonObject query(String tbName, List<String> cols, Map map, StringBuffer sqlWhere,
                                  LinkedHashMap<String, String> orderColumns, Class<T> clazz) {
        CommonObject commonObject = new CommonObject();
        StringBuilder sqlSelect = new StringBuilder();
        StringBuilder sqlOrderBack = new StringBuilder();
        StringBuilder sqlOrderFilter = new StringBuilder();
        StringBuilder sqlOrderColumns = new StringBuilder();
        StringBuilder sqlCount = new StringBuilder();
        List<T> resultList;
        int count;
        //获取分页条件
        int startIndex = 0, pageSize = 0, endIndex = 0;
        if (!MapUtils.isEmpty(map)) {
            Object temp = map.get(QueryParameterKeys.STARTINDEX.getKey());
            if (temp != null) {
                startIndex = (int) temp;
            }
            temp = map.get(QueryParameterKeys.PAGESIZE.getKey());
            if (temp != null) {
                pageSize = (int) temp;
            }
            endIndex = startIndex + pageSize;
        }
        //设置排序字段
        if (pageSize > 0 && !MapUtils.isEmpty(orderColumns) && !orderColumns.containsKey("id")) {
            Map<String, String> orderColumnsTarget = new LinkedHashMap<>();
            if (!MapUtils.isEmpty(orderColumns)) {
                orderColumnsTarget.putAll(orderColumns);
            }
            //如果需要分页，且排序字段为空，则需要根据主键分页过滤
            orderColumnsTarget.put("id", DatabaseConst.ORDER_DESC);
            //将添加了ID的排序字段，重新赋值给orderColumns变量
            orderColumns.clear();
            orderColumns.putAll(orderColumnsTarget);
            orderColumnsTarget.clear();
        } else if (MapUtils.isEmpty(orderColumns)) {
            orderColumns = new LinkedHashMap<>();
            //如果需要分页，且排序字段为空，则需要根据主键分页过滤
            orderColumns.put("id", DatabaseConst.ORDER_DESC);
        }
        if (!MapUtils.isEmpty(orderColumns)) {
            for (String key : orderColumns.keySet()) {
                if (sqlOrderFilter.length() > 0) {
                    sqlOrderBack.append(", ");
                    sqlOrderFilter.append(", ");
                    sqlOrderColumns.append(", ");
                }
                sqlOrderBack.append(key).append(" ").append(DatabaseConst.ORDER_ASC.equals(orderColumns.get(key)) ?
                        DatabaseConst.ORDER_DESC : DatabaseConst.ORDER_ASC);
                sqlOrderFilter.append(key).append(" ").append(orderColumns.get(key));
                sqlOrderColumns.append(key);
            }
            sqlOrderColumns.insert(0, " ");
            sqlOrderColumns.append(" ");
        }
        //设置筛选条件
        String where = createWhere(sqlWhere);
        //获取总数
        sqlCount.append("SELECT COUNT(*) FROM ").append(tbName).append("(NOLOCK) ");
        sqlCount.append(where);
        count = namedParameterJdbcTemplate.queryForObject(sqlCount.toString(), map, Integer.class);
        //设置待返回的字段
        String returnCols;
        if(ListUtils.isEmpty(cols)) {
            returnCols = "*";
        } else {
            returnCols = String.join(",", cols);
        }
        //设置筛选脚本
        if (pageSize > 0) { //按照分页结构返回
            if (endIndex >= count) {
                //如果是最后一页，则需要单独处理
                sqlSelect.append("SELECT ").append(returnCols).append(" FROM ").append(tbName).append("(NOLOCK) WHERE id in ( ");
                sqlSelect.append("SELECT TOP ").append(count - startIndex).append(" id FROM ").append(tbName).append("(NOLOCK) ");
                sqlSelect.append(where);
                if (sqlOrderBack.length() > 0) {
                    sqlSelect.append(" ORDER BY ").append(sqlOrderBack);
                }
                sqlSelect.append(") ");
                if (sqlOrderBack.length() > 0) {
                    sqlSelect.append(" ORDER BY ").append(sqlOrderFilter);
                }
            } else {
                //其他页码
                sqlSelect.append("SELECT ").append(returnCols).append(" FROM ").append(tbName).append("(NOLOCK) WHERE id in ( ");
                sqlSelect.append("SELECT TOP ").append(pageSize).append(" id FROM ( ");
                sqlSelect.append("SELECT TOP ").append(endIndex).append(sqlOrderColumns)
                        .append(" FROM ").append(tbName).append("(NOLOCK) ");
                sqlSelect.append(where);
                if (sqlOrderFilter.length() > 0) {
                    sqlSelect.append(" ORDER BY ").append(sqlOrderFilter);
                }
                sqlSelect.append(") tb ");
                if (sqlOrderBack.length() > 0) {
                    sqlSelect.append(" ORDER BY ").append(sqlOrderBack);
                }
                sqlSelect.append(") ");
                if (sqlOrderBack.length() > 0) {
                    sqlSelect.append(" ORDER BY ").append(sqlOrderFilter);
                }
            }
            //获取记录
            resultList = namedParameterJdbcTemplate.query(sqlSelect.toString(), map, new BeanPropertyRowMapper<>(clazz));
        } else { //按照普通查询结构返回
            //获取记录
            sqlSelect.append("SELECT ").append(returnCols).append(" FROM ").append(tbName).append("(NOLOCK) ");
            sqlSelect.append(where);
            if (sqlOrderBack.length() > 0) {
                sqlSelect.append(" ORDER BY ").append(sqlOrderFilter);
            }
            if(String.class.equals(clazz)) {
                resultList = namedParameterJdbcTemplate.queryForList(sqlSelect.toString(), map, clazz);
            } else {
                resultList = namedParameterJdbcTemplate.query(sqlSelect.toString(), map, new BeanPropertyRowMapper<>(clazz));
            }
        }
        commonObject.setTotalSize(count);
        commonObject.setDatas(resultList);
        return commonObject;
    }

    /**
     * 通用的查询总数接口
     */
    public int count(String tbName, Map map, StringBuffer sqlWhere) {
        StringBuilder sqlCount = new StringBuilder();
        //设置筛选条件
        String where = createWhere(sqlWhere);
        //获取总数
        sqlCount.append("SELECT COUNT(*) FROM ").append(tbName).append("(NOLOCK) ").append(where);
        int count = namedParameterJdbcTemplate.queryForObject(sqlCount.toString(), map, Integer.class);
        return count;
    }

    /**
     * 生成查询条件
     */
    private String createWhere(StringBuffer sqlWhere) {
        String where;
        if (sqlWhere.length() > 0) {
            sqlWhere = StringUtils.trimLeft(sqlWhere);
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
        return where;
    }
}