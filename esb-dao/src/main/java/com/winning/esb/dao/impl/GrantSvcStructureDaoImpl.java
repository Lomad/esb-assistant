package com.winning.esb.dao.impl;

import com.winning.esb.dao.IGrantSvcStructureDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.GrantSvcStructureModel;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GrantSvcStructureDaoImpl implements IGrantSvcStructureDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_GrantSvcStructure";

    @Override
    public void insert(List<GrantSvcStructureModel> list) {
        if (!ListUtils.isEmpty(list)) {
            StringBuffer sql = new StringBuffer();
            Map<String, Object> map = new HashMap<>();
            GrantSvcStructureModel obj;
            for (int i = 0, len = list.size(); i < len; i++) {
                obj = list.get(i);
                sql.append("insert into ").append(TB_NAME).append(" (gid, ssid) values(:gid_" + i + ", :ssid_" + i + ") ");
                map.put("gid_" + i, obj.getGid());
                map.put("ssid_" + i, obj.getSsid());
            }
            namedParameterJdbcTemplate.update(sql.toString(), map);
        }
    }

    @Override
    public void delete(List<Integer> idList) {
        commonHandle.delete(TB_NAME, idList);
    }

    @Override
    public void deleteByGid(Integer gid) {
        commonHandle.delete(TB_NAME, "gid", gid);
    }

    @Override
    public List<GrantSvcStructureModel> queryByGid(Integer gid) {
        return commonHandle.listByColumn(TB_NAME, "gid", gid, GrantSvcStructureModel.class);
    }

}