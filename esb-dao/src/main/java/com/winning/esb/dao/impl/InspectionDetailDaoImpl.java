package com.winning.esb.dao.impl;

import com.winning.esb.dao.IInspectionDetailDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InspectionDetailDaoImpl implements IInspectionDetailDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String DB_NAME = "ESB_InspectionDetail";

    @Override
    public void insert(List<InspectionDetailModel> list) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> map = new HashMap<>();
        InspectionDetailModel obj;
        for (int i = 0, len = list.size(); i < len; i++) {
            obj = list.get(i);
            sql.append("insert into ").append(DB_NAME);
            sql.append(" (ins_id, index_id, result, desp, time_len, btime, etime, ctime) ");
            sql.append("values(:ins_id_").append(i);
            sql.append(", :index_id_").append(i);
            sql.append(", :result_").append(i);
            sql.append(", :desp_").append(i);
            sql.append(", :time_len_").append(i);
            sql.append(", :btime_").append(i);
            sql.append(", :etime_").append(i);
            sql.append(", :ctime_").append(i);
            sql.append(")");
            map.put("ins_id_" + i, obj.getIns_id());
            map.put("index_id_" + i, obj.getIndex_id());
            map.put("result_" + i, obj.getResult());
            map.put("desp_" + i, obj.getDesp());
            map.put("time_len_" + i, obj.getTime_len());
            map.put("btime_" + i, obj.getBtime());
            map.put("etime_" + i, obj.getEtime());
            map.put("ctime_" + i, obj.getCtime());
        }
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Deprecated
    @Override
    public List<InspectionDetailModel> queryMaxByInsIDList(List<Integer> insIDList) {
        if (!ListUtils.isEmpty(insIDList)) {
            String nolock = DatabaseConst.NOLOCK_BRACKETS;
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT * FROM ESB_InspectionDetail").append(nolock);
            sql.append(" WHERE id IN (SELECT MAX(id) FROM ESB_InspectionDetail").append(nolock);
            sql.append(" WHERE ins_id IN (:insID) and result=2 GROUP BY ins_id) ");
            sql.append(" UNION ");
            sql.append(" SELECT * FROM ESB_InspectionDetail").append(nolock).append(" WHERE id IN ( ");
            sql.append(" SELECT MAX(id) FROM ESB_InspectionDetail").append(nolock);
            sql.append(" WHERE ins_id IN (:insID) AND ins_id NOT IN ( ");
            sql.append(" SELECT ins_id FROM ESB_InspectionDetail").append(nolock);
            sql.append(" WHERE ins_id IN (:insID) and result=2 GROUP BY ins_id) AND ISNULL(result, '')<>'' ");
            sql.append(" GROUP BY ins_id) ");
            Map<String, Object> map = new HashMap<>();
            map.put("insID", insIDList);
            return namedParameterJdbcTemplate.query(sql.toString(), map, new BeanPropertyRowMapper<>(InspectionDetailModel.class));
        } else {
            return null;
        }
    }

    @Override
    public List<InspectionDetailModel> queryByInsID(Integer insID) {
        return commonHandle.listByColumn(DB_NAME, "ins_id", insID, InspectionDetailModel.class);
    }
}