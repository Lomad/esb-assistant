package com.winning.esb.dao.impl;

import com.winning.esb.dao.ISvcInfoDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.DownloadEnum;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
@Repository
public class SvcInfoDaoImpl implements ISvcInfoDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    @Override
    public Integer insert(SvcInfoModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(TB_NAME);
        sql.append(" (code, name, version, aid, groupId, urlId, url, urlAgentId, dataProtocal, msgType, ");
        sql.append(" otherMark, otherInfo, desp, ctime, mtime, status) ");
        sql.append("values(:code, :name, :version, :aid, :groupId, :urlId, :url, :urlAgentId, :dataProtocal, :msgType, ");
        sql.append(" :otherMark, :otherInfo, :desp, :ctime, :mtime, :status)");
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(SvcInfoModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + TB_NAME + " set code = :code ");
        sql.append(", name = :name ");
        sql.append(", aid = :aid ");
        sql.append(", version = :version ");
        sql.append(", groupId = :groupId ");
        sql.append(", urlId = :urlId ");
        sql.append(", url = :url ");
        sql.append(", urlAgentId = :urlAgentId ");
        sql.append(", dataProtocal = :dataProtocal ");
        sql.append(", msgType = :msgType ");
        sql.append(", otherMark = :otherMark ");
        sql.append(", otherInfo = :otherInfo ");
        sql.append(", desp = :desp ");
        sql.append(", mtime = :mtime ");
        sql.append("where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("version", obj.getVersion());
        map.put("code", obj.getCode());
        map.put("name", obj.getName());
        map.put("aid", obj.getAid());
        map.put("groupId", obj.getGroupId());
        map.put("urlId", obj.getUrlId());
        map.put("url", obj.getUrl());
        map.put("urlAgentId", obj.getUrlAgentId());
        map.put("dataProtocal", obj.getDataProtocal());
        map.put("msgType", obj.getMsgType());
        map.put("otherMark", obj.getOtherMark());
        map.put("otherInfo", obj.getOtherInfo());
        map.put("desp", obj.getDesp());
        map.put("mtime", obj.getMtime());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void updateRawContent(Integer id, Integer svcStructureDirection, String rawContent) {
        String columnName;
        if (SvcStructureEnum.DirectionEnum.In.getCode() == svcStructureDirection.intValue()) {
            columnName = "rawIn";
        } else {
            columnName = "rawAck";
        }
        String sql = "update " + TB_NAME + " set " + columnName + "=:" + columnName + " where id = :id";
        Map<String, Object> map = new HashMap<>();
        map.put(columnName, rawContent);
        map.put("id", id);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void delete(List<Integer> idlist) {
        commonHandle.delete(TB_NAME, idlist);
    }

    @Override
    public void updateStatus(List<Integer> idlist, int status) {
        String sql = "update " + TB_NAME + " set status = :status where id in (:idlist) ";
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("idlist", idlist);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public List<SvcInfoModel> getByID(List<Integer> idList) {
        return commonHandle.listByKey(TB_NAME, idList, SvcInfoModel.class);
    }

    @Override
    public List<SvcInfoModel> getByAppId(List<String> appIdList) {
        if (ListUtils.isEmpty(appIdList)) {
            return null;
        } else {
            String nolock = DatabaseConst.NOLOCK_BRACKETS;
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT a.* FROM  ESB_SvcInfo a").append(nolock)
                    .append(" JOIN ESB_AppInfo b").append(nolock).append(" ON a.aid=b.id ");
            if (appIdList.size() == 1) {
                sql.append(" WHERE b.appId = :appId ");
            } else {
                sql.append(" WHERE b.appId IN (:appId) ");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("appId", appIdList);
            return namedParameterJdbcTemplate.query(sql.toString(), map, new BeanPropertyRowMapper<>(SvcInfoModel.class));
        }
    }

    @Override
    public List<SvcInfoModel> getByCode(List<String> codeList) {
        return commonHandle.listByColumn(TB_NAME, "code", codeList, SvcInfoModel.class);
    }

    @Override
    public List<String> listCode(Map map) {
        StringBuffer sqlWhere = createWhere(map);
        return ListUtils.transferToList(
                commonHandle.query(TB_NAME, Arrays.asList("code"), map, sqlWhere, null, String.class).getDatas());
    }

    @Override
    public CommonObject query(Map map) {
        StringBuffer sqlWhere = createWhere(map);
        return commonHandle.query(TB_NAME, map, sqlWhere, SvcInfoModel.class);
    }

    @Override
    public int count(Map map) {
        StringBuffer sqlWhere = createWhere(map);
        return commonHandle.count(TB_NAME, map, sqlWhere);
    }

    /**
     * 生成查询条件
     */
    private StringBuffer createWhere(Map map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (!MapUtils.isEmpty(map)) {
            Object temp = map.get("queryWord");
            if (temp != null && temp != "") {
                map.put("queryWord", "%" + temp + "%");
                sqlWhere.append("and (code like :queryWord or name like :queryWord) ");
            }
            temp = map.get("code");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and code = :code ");
            }
            temp = map.get("codeList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and code in (:codeList) ");
            }
            temp = map.get("name");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and name = :name ");
            }
            temp = map.get("aid");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and aid = :aid ");
            }
            temp = map.get("aidList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and aid in (:aidList) ");
            }
            temp = map.get("aidNotInList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and aid not in (:aidNotInList) ");
            }
            temp = map.get("urlId");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and urlId = :urlId ");
            }
            temp = map.get("version");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and version = :version ");
            }
            temp = map.get("id");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and id = :id ");
            }
            temp = map.get("idList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and id in (:idList) ");
            }
            temp = map.get("idNotInList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append(" and id not in (:idNotInList) ");
            }
            temp = map.get("status");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and status = :status ");
            }
            temp = map.get("otherMark");
            if (temp != null) {
                sqlWhere.append("and otherMark = :otherMark ");
            }
        }
        return sqlWhere;
    }

    @Override
    public List<Map<String, Object>> listDownload(Integer aid, Integer svcDirection) {
        String nolock = DatabaseConst.NOLOCK_BRACKETS;
        StringBuffer sql = new StringBuffer();
        if (svcDirection == null || svcDirection.intValue() == DownloadEnum.SvcDirectionEnum.Provided.getCode()) {
            sql.append(" SELECT id, code, name, version, desp, ")
                    .append(DownloadEnum.SvcDirectionEnum.Provided.getCode()).append(" as svcDirection ")
                    .append(" FROM ESB_SvcInfo").append(nolock).append(" WHERE aid = :aid ");
        }
        if (svcDirection == null || svcDirection.intValue() == DownloadEnum.SvcDirectionEnum.Subscription.getCode()) {
            if (sql.length() > 0) {
                sql.append(" UNION ");
            }
            sql.append(" SELECT id, code, name, version, desp, ")
                    .append(DownloadEnum.SvcDirectionEnum.Subscription.getCode()).append(" as svcDirection ")
                    .append(" FROM ESB_SvcInfo").append(nolock).append(" WHERE id IN ( ")
                    .append(" SELECT sid FROM ESB_Grant").append(nolock).append(" WHERE aid = :aid AND approve_state=2) ");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("aid", aid);
        return namedParameterJdbcTemplate.queryForList(sql.toString(), map);
    }

    @Override
    public List<SvcInfoModel> getByAid(List<Integer> aidList, List<Integer> aidNotInList, List<Integer> idNotInList, String queryWord, Integer svcStatus) {
        StringBuffer sqlWhere = new StringBuffer();
        Map<String, Object> map = new HashMap<>();
        if (!ListUtils.isEmpty(aidList)) {
            map.put("aidList", aidList);
            sqlWhere.append(" and aid in (:aidList) ");
        }
        if (!ListUtils.isEmpty(aidNotInList)) {
            map.put("aidNotInList", aidNotInList);
            sqlWhere.append(" and aid not in (:aidNotInList) ");
        }
        if (!ListUtils.isEmpty(idNotInList)) {
            map.put("idNotInList", idNotInList);
            sqlWhere.append(" and id not in (:idNotInList) ");
        }
        if (!StringUtils.isEmpty(queryWord)) {
            map.put("queryWord", "%" + queryWord + "%");
            sqlWhere.append(" and (code like :queryWord or name like :queryWord) ");
        }
        if (svcStatus != null) {
            map.put("status", svcStatus);
            sqlWhere.append(" and status = :status ");
        }
        return ListUtils.transferToList(commonHandle.query(TB_NAME, map, sqlWhere, SvcInfoModel.class).getDatas());
    }

    @Override
    public List<SvcInfoModel> getByGroupId(List<Integer> groupIdList) {
        StringBuffer sqlWhere = new StringBuffer();
        Map<String, Object> map = new HashMap<>();
        if (!ListUtils.isEmpty(groupIdList)) {
            map.put("groupIdList", groupIdList);
            sqlWhere.append(" and groupId in (:groupIdList) ");
        }
        return ListUtils.transferToList(commonHandle.query(TB_NAME, map, sqlWhere, SvcInfoModel.class).getDatas());
    }
}