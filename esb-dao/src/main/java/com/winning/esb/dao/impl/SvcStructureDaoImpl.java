package com.winning.esb.dao.impl;

import com.winning.esb.dao.ISvcStructureDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
@Repository
public class SvcStructureDaoImpl implements ISvcStructureDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    public static final String TB_NAME = "ESB_SvcStructure";

    @Override
    public Integer insert(SvcStructureModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + TB_NAME);
        sql.append("(sid, direction, pid, code, name, order_num, is_attr, can_edit, required,");
        sql.append(" is_loop, result_mark, data_type, value_default, desp, ctime, mtime) ");
        sql.append("values(:sid, :direction, :pid, :code, :name, :order_num, :is_attr, :can_edit, :required,");
        sql.append(" :is_loop, :result_mark, :data_type, :value_default, :desp, :ctime, :mtime)");
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(SvcStructureModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + TB_NAME + " set sid = :sid ");
        sql.append(", direction = :direction ");
        sql.append(", pid = :pid ");
        sql.append(", code = :code ");
        sql.append(", name = :name ");
        sql.append(", order_num = :order_num ");
        sql.append(", is_attr = :is_attr ");
        sql.append(", required = :required ");
        sql.append(", is_loop = :is_loop ");
        sql.append(", result_mark = :result_mark ");
        sql.append(", data_type = :data_type ");
        sql.append(", value_default = :value_default ");
        sql.append(", desp = :desp ");
        sql.append(", mtime = :mtime ");
        sql.append("where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("sid", obj.getSid());
        map.put("direction", obj.getDirection());
        map.put("pid", obj.getPid());
        map.put("code", obj.getCode());
        map.put("name", obj.getName());
        map.put("order_num", obj.getOrder_num());
        map.put("is_attr", obj.getIs_attr());
        map.put("required", obj.getRequired());
        map.put("is_loop", obj.getIs_loop());
        map.put("result_mark", obj.getResult_mark());
        map.put("data_type", obj.getData_type());
        map.put("value_default", obj.getValue_default());
        map.put("desp", obj.getDesp());
        map.put("mtime", obj.getMtime());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void updateWhenDrop(List<SvcStructureModel> objs) {
        if (objs != null && objs.size() > 0) {
            StringBuffer sql = new StringBuffer();
            Map<String, Object> map = new HashMap<>();
            for (int i = 0, len = objs.size(); i < len; i++) {
                sql.append(" update " + TB_NAME + " set pid = :pid_" + i);
                sql.append(", order_num = :order_num_" + i);
                sql.append(", mtime = :mtime_" + i);
                sql.append(" where id = :id_" + i);
                map.put("pid_" + i, objs.get(i).getPid());
                map.put("order_num_" + i, objs.get(i).getOrder_num());
                map.put("mtime_" + i, objs.get(i).getMtime());
                map.put("id_" + i, objs.get(i).getId());
            }
            namedParameterJdbcTemplate.update(sql.toString(), map);
        }
    }

    @Override
    public void delete(Integer id) {
        commonHandle.delete(TB_NAME, id);
    }

    @Override
    public void delete(Integer sid, Integer direction) {
        StringBuffer sql = new StringBuffer();
        sql.append("delete from " + ValueListDaoImpl.TB_NAME + " where ssid in ");
        sql.append("(select id from " + TB_NAME + " where sid = :sid and direction = :direction) ");
        sql.append("delete from " + TB_NAME + " where sid = :sid and direction = :direction ");
        Map<String, Object> map = new HashMap<>();
        map.put("sid", sid);
        map.put("direction", direction);
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public CommonObject query(Map<String, Object> map) {
        StringBuffer sqlWhere = new StringBuffer();
        if(map != null){
            Object temp = map.get("pid");
            if (temp !=null && temp != "") {
                sqlWhere.append("and pid = :pid  ");
            }
        }
        return commonHandle.query(TB_NAME, map, sqlWhere, SvcStructureModel.class);
    }

    @Override
    public void deleteBySid(List<Integer> sidList) {
        commonHandle.delete(TB_NAME, "sid", sidList);
    }

    @Override
    public List<SvcStructureModel> queryBySvcID(Integer sid, Integer direction) {
        String sql = "select * from " + TB_NAME + " where sid = :sid ";
        if (direction != null) {
            sql += "and direction = :direction ";
        }
        sql += " order by order_num";
        Map<String, Object> map = new HashMap<>();
        map.put("sid", sid);
        if (direction != null) {
            map.put("direction", direction);
        }
        List<SvcStructureModel> resultList = namedParameterJdbcTemplate.query(sql, map, new BeanPropertyRowMapper<>(SvcStructureModel.class));
        return resultList;
    }

    @Override
    public List<SvcStructureModel> queryById(Integer id) {
        String sql = "select * from " + TB_NAME + " where id = :id ";
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        List<SvcStructureModel> resultList = namedParameterJdbcTemplate.query(sql, map, new BeanPropertyRowMapper<>(SvcStructureModel.class));
        return resultList;
    }

    @Override
    public SvcStructureModel getResultNode(Integer id, Integer sid) {
        StringBuffer sqlWhere = new StringBuffer();
        Map<String, Object> map = new HashMap<>();
        if (map != null) {
            //结果标志
            map.put("result_mark", SvcStructureEnum.ResultMarkEnum.Yes.getCode());
            sqlWhere.append("and result_mark = :result_mark ");
            //方向
            map.put("direction", SvcStructureEnum.DirectionEnum.Ack.getCode());
            sqlWhere.append("and direction = :direction ");
            //服务结构ID
            if (id != null) {
                map.put("id", id);
                sqlWhere.append("and id <> :id ");
            }
            //服务ID
            map.put("sid", sid);
            sqlWhere.append("and sid = :sid ");
        }
        CommonObject commonObject = commonHandle.query(TB_NAME, map, sqlWhere, SvcStructureModel.class);
        List<SvcStructureModel> resultList = ListUtils.transferToList(commonObject.getDatas());
        return ListUtils.isEmpty(resultList) ? null : resultList.get(0);
    }

    @Override
    public Integer getMaxOrderNumByID(Integer pid) {
        String sql = "select max(order_num) from " + TB_NAME + " where pid = :pid ";
        Map<String, Object> map = new HashMap<>();
        map.put("pid", pid);
        return namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
    }

    @Override
    public boolean existCode(String code, Integer pid, Integer id, Integer direction, Integer sid) {
        String sql = "select count(*) from " + TB_NAME + " where pid = :pid and code = :code and direction = :direction and sid = :sid ";
        if (id != null) {
            sql += " and id<>:id ";
        }
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("pid", pid);
        map.put("direction", direction);
        map.put("sid", sid);
        if (id != null) {
            map.put("id", id);
        }
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return (count != null && count.intValue() > 0) ? true : false;
    }


}