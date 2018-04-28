package com.winning.esb.dao.impl;

import com.winning.esb.dao.IAppInfoDao;
import com.winning.esb.dao.ISvcInfoDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.AppInfoEnum;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/21.
 */
@Repository
public class AppInfoDaoImpl implements IAppInfoDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_AppInfo";

    @Override
    public void insert(List<AppInfoModel> objs) {
        if (!ListUtils.isEmpty(objs)) {
            StringBuffer sql = new StringBuffer();
            Map<String, Object> map = new HashMap<>();
            AppInfoModel obj;
            for (int i = 0, len = objs.size(); i < len; i++) {
                sql.append("insert into ").append(TB_NAME);
                sql.append("(appId, appName, orgId, appType, direction, status, appIdCurrent, order_num, desp, ctime, mtime) ");
                sql.append("values(");
                sql.append(":appId").append(i);
                sql.append(", :appName").append(i);
                sql.append(", :orgId").append(i);
                sql.append(", :appType").append(i);
                sql.append(", :direction").append(i);
                sql.append(", :status").append(i);
                sql.append(", :appIdCurrent").append(i);
                sql.append(", :order_num").append(i);
                sql.append(", :desp").append(i);
                sql.append(", :ctime").append(i);
                sql.append(", :mtime").append(i);
                sql.append(")");

                obj = objs.get(i);
                map.put("appId" + i, obj.getAppId());
                map.put("appName" + i, obj.getAppName());
                map.put("orgId" + i, obj.getOrgId());
                map.put("appType" + i, obj.getAppType());
                map.put("direction" + i, obj.getDirection());
                map.put("status" + i, obj.getStatus());
                map.put("appIdCurrent" + i, obj.getAppIdCurrent());
                map.put("desp" + i, obj.getDesp());
                map.put("ctime" + i, obj.getCtime());
                map.put("mtime" + i, obj.getMtime());
                map.put("order_num" + i, obj.getOrder_num());

                //每50个对象提交一次，或达到最后一条提交一次
                if (i == len - 1 || (i > 0 && i % 50 == 0)) {
                    namedParameterJdbcTemplate.update(sql.toString(), map);
                    sql.setLength(0);
                    map.clear();
                }
            }
        }
    }

    @Override
    public void update(List<AppInfoModel> objs) {
        if (!ListUtils.isEmpty(objs)) {
            StringBuffer sql = new StringBuffer();
            Map<String, Object> map = new HashMap<>();
            AppInfoModel obj;
            for (int i = 0, len = objs.size(); i < len; i++) {
                //如果是首次，或下次循环清空后的首次，则需要定义变量
                if (sql.length() == 0) {
                    sql.append(" DECLARE @appIdOld VARCHAR(100), @appId VARCHAR(100) ");
                }
                //获取系统代码
                sql.append(" SELECT @appIdOld = appId FROM ESB_AppInfo(nolock) WHERE id = :id").append(i);
                sql.append(" IF @appIdOld <> :appId").append(i);
                sql.append(" BEGIN ");
                //删除服务代码前缀（即旧的业务系统代码）
                sql.append(" UPDATE ").append(ISvcInfoDao.TB_NAME);
                sql.append(" SET code=SUBSTRING(code, LEN(@appIdOld) + 1, LEN(code) - LEN(@appIdOld))");
                sql.append(" WHERE aid = :id").append(i).append(" AND code LIKE @appIdOld + '%'");
                //将新业务系统代码添加到服务代码前
                sql.append(" UPDATE ").append(ISvcInfoDao.TB_NAME).append(" SET code=:appId").append(i);
                sql.append(" + code WHERE aid = :id").append(i);
                sql.append(" END ");

                //更新业务系统
                sql.append(" update ").append(TB_NAME);
                sql.append(" set appId = :appId").append(i);
                sql.append(", appName = :appName").append(i);
                sql.append(", orgId = :orgId").append(i);
                sql.append(", appType = :appType").append(i);
                sql.append(", direction = :direction").append(i);
                sql.append(", appIdCurrent = :appIdCurrent").append(i);
                sql.append(", desp = :desp").append(i);
                sql.append(", mtime = :mtime").append(i);
                sql.append(", order_num = :order_num").append(i);
                sql.append(" where id = :id").append(i);

                obj = objs.get(i);
                map.put("appId" + i, obj.getAppId());
                map.put("appName" + i, obj.getAppName());
                map.put("orgId" + i, obj.getOrgId());
                map.put("appType" + i, obj.getAppType());
                map.put("direction" + i, obj.getDirection());
                map.put("appIdCurrent" + i, obj.getAppIdCurrent());
                map.put("desp" + i, obj.getDesp());
                map.put("mtime" + i, obj.getMtime());
                map.put("order_num" + i, obj.getOrder_num());
                map.put("id" + i, obj.getId());

                //每50个对象提交一次，或达到最后一条提交一次
                if (i == len - 1 || (i > 0 && i % 50 == 0)) {
                    namedParameterJdbcTemplate.update(sql.toString(), map);
                    sql.setLength(0);
                    map.clear();
                }
            }
        }
    }

    @Override
    public void updateStatus(List<Integer> ids, Integer status) {
        String sql = "update " + TB_NAME + " set status = :status where id in (:ids) ";
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("ids", ids);
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void delete(Object id) {
        commonHandle.delete(TB_NAME, id);
    }

    @Override
    public List<AppInfoModel> getByID(List<Integer> idList) {
        return commonHandle.listByKey(TB_NAME, idList, AppInfoModel.class);
    }

    @Override
    public List<AppInfoModel> getByAppId(Object appId) {
        return commonHandle.listByColumn(TB_NAME, "appId", appId, AppInfoModel.class);
    }

    @Override
    public CommonObject query(Map map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (map != null) {
            String strTemp;
            Object temp = map.get("queryWord");
            if (!StringUtils.isEmpty(temp)) {
                map.put("queryWord", "%" + temp + "%");
                sqlWhere.append("and (appId like :queryWord or appName like :queryWord) ");
            }
            temp = map.get("appId");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and appId = :appId ");
            }
            temp = map.get("appIdList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and appId in (:appIdList) ");
            }
            temp = map.get("appName");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and appName = :appName ");
            }
            temp = map.get("appType");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and appType = :appType ");
            }
            temp = map.get("orgId");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and orgId = :orgId ");
            }
            temp = map.get("status");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and status = :status ");
            }
            temp = map.get("idList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and id in (:idList) ");
            }
            temp = map.get("idNotInList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and id not in (:idNotInList) ");
            }
            temp = map.get("direction");
            if (temp != null) {
                strTemp = String.valueOf(temp);
                if (String.valueOf(AppInfoEnum.DirectionEnum.Provider.getCode()).equals(strTemp)
                        || String.valueOf(AppInfoEnum.DirectionEnum.Consumer.getCode()).equals(strTemp)) {
                    sqlWhere.append("and direction in(").append(temp).append(", ")
                            .append(AppInfoEnum.DirectionEnum.All.getCode()).append(") ");
                } else {
                    sqlWhere.append(" and direction = :direction ");
                }
            }
            temp = map.get("needESB");
            if (temp != null && Boolean.parseBoolean(String.valueOf(temp)) == false) {
                sqlWhere.append(" and appType <> ").append(AppInfoEnum.AppTypeEnum.ESB.getCode());
            }
            temp = map.get("needStop");
            if (temp != null && Boolean.parseBoolean(String.valueOf(temp)) == false) {
                sqlWhere.append(" and status = ").append(AppInfoEnum.StatusEnum.Normal.getCode());
            }
        }
        LinkedHashMap<String, String> orderColumns = new LinkedHashMap<>();
        orderColumns.put("order_num", DatabaseConst.ORDER_ASC);
        return commonHandle.query(TB_NAME, map, sqlWhere, orderColumns, AppInfoModel.class);

    }
}