package com.winning.esb.dao.impl;

import com.winning.esb.dao.IUserAppDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.UserAppModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserAppDaoImpl implements IUserAppDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_UserApp";

    @Override
    public void insert(List<UserAppModel> list) {
        if (!ListUtils.isEmpty(list)) {
            StringBuffer sql = new StringBuffer();
            Map<String, Object> map = new HashMap<>();
            UserAppModel obj;
            for (int i = 0, len = list.size(); i < len; i++) {
                obj = list.get(i);
                sql.append("insert into ").append(TB_NAME).append(" (userid, aid)  values(:userid_" + i + ", :aid_" + i + ")");
                map.put("userid_" + i, obj.getUserid());
                map.put("aid_" + i, obj.getAid());
            }
            namedParameterJdbcTemplate.update(sql.toString(), map);
        }
    }

    @Override
    public void delete(Integer id) {
        commonHandle.delete(TB_NAME, id);
    }

    @Override
    public void deleteByUserid(Integer userid) {
        commonHandle.delete(TB_NAME, "userid", userid);
    }

    @Override
    public CommonObject query(Map map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (map != null) {
            Object temp = map.get("userid");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and userid = :userid ");
            }
            temp = map.get("username");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and userid = (SELECT TOP 1 id FROM ESB_User(NOLOCK) WHERE username = :username) ");
            }
            temp = map.get("aid");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and aid = :aid ");
            }
        }
        return commonHandle.query(TB_NAME, map, sqlWhere, UserAppModel.class);
    }
}