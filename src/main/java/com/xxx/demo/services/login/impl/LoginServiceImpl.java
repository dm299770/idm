package com.xxx.demo.services.login.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxx.demo.frame.constants.AppResultConstants;
import com.xxx.demo.frame.util.MD5Util;
import com.xxx.demo.frame.util.TokenUtils;
import com.xxx.demo.mapper.sys.SysUserMapper;
import com.xxx.demo.mapper.user.TsUserMapper;
import com.xxx.demo.models.jsonBean.login.LoginData;
import com.xxx.demo.models.sys.SysUser;
import com.xxx.demo.models.sys.TsUser;
import com.xxx.demo.services.login.LoginService;
import com.xxx.demo.services.user.impl.SysUserServiceImpl;
import com.xxx.demo.services.user.impl.TsUserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:登录接口实现
 * @author:@leo.
 */
@Service
public class LoginServiceImpl implements LoginService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TsUserMapper tsUserMapper;

    @Override
    public JSONObject login(String phoneNum, String password) {
        try {
            // 判断【用户名】、【密码】参数合法性
            JSONObject jsonObject = new JSONObject();

            if (null == phoneNum || "".equalsIgnoreCase(phoneNum)) {
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
                jsonObject.put(AppResultConstants.MSG, TsUserServiceImpl.USER_NAME_ERROR);
            } else if (null == password || "".equalsIgnoreCase(password)) {
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
                jsonObject.put(AppResultConstants.MSG, TsUserServiceImpl.PASSWORD_ERROR);
            } else {
                // 查找用户，判断用户账号密码是否正确
                TsUser tsUser = tsUserMapper.findEffctiveByPhoneNum(phoneNum);
                if (null == tsUser) {
                    jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
                    jsonObject.put(AppResultConstants.MSG, TsUserServiceImpl.USER_ERROR);
                //} else if (!MD5Util.md5(password).equalsIgnoreCase(tsUser.getPassword())) {
                } else if (!password.equalsIgnoreCase(tsUser.getPassword())) {
                    jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
                    jsonObject.put(AppResultConstants.MSG, TsUserServiceImpl.PASSWORD_WRONG_ERROR);
                } else {
                    // 账号密码正确，生成token
                    String accessToken = TokenUtils.createJwtToken(phoneNum);
                    LoginData data = new LoginData(accessToken);
                    jsonObject.put(AppResultConstants.DATA, data);
                    jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
                    jsonObject.put(AppResultConstants.MSG, TsUserServiceImpl.LOGIN_SUCCESS);
                }
            }

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            JSONObject jsonExcptionObject = new JSONObject();
            jsonExcptionObject.put(AppResultConstants.MSG,AppResultConstants.SEVER_ERROR);
            jsonExcptionObject.put(AppResultConstants.STATUS ,AppResultConstants.ERROR_STATUS );
            return jsonExcptionObject;

        }
    }

    @Override
    public JSONObject logout(String userName) {

        return null;
    }
}
