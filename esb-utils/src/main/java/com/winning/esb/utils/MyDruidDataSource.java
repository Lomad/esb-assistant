package com.winning.esb.utils;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyDruidDataSource extends DruidDataSource {
    private static final Logger logger = LoggerFactory.getLogger(MyDruidDataSource.class);

    @Override
    public void setUsername(String username) {
        try {
			String decryptUser = Base64Utils.getFromBase64(username);
			this.username=decryptUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public void setPassword(String password) {
        try {
			String decryptPasswd = Base64Utils.getFromBase64(password);
			if (!StringUtils.isEmpty(password) && password.equals(decryptPasswd)) {
                logger.info("密码未加密！");
	            return;
	        }
	        if (inited) {
                logger.info("密码更改");
	        }
			this.password=decryptPasswd;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }
}