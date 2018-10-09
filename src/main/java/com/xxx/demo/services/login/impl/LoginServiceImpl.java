package com.xxx.demo.services.login.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxx.demo.frame.constants.AppResultConstants;
import com.xxx.demo.frame.util.MD5Util;
import com.xxx.demo.frame.util.TokenUtils;
import com.xxx.demo.mapper.sys.SysUserMapper;
import com.xxx.demo.mapper.user.TsUserMapper;
import com.xxx.demo.models.sys.SysUser;
import com.xxx.demo.models.sys.TsUser;
import com.xxx.demo.services.login.LoginService;
import com.xxx.demo.services.user.impl.SysUserServiceImpl;
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
        JSONObject jsonObject = new JSONObject();

        // 判断【用户名】、【密码】参数合法性
        if (null == phoneNum || "".equalsIgnoreCase(phoneNum)) {
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
            jsonObject.put(AppResultConstants.MSG, SysUserServiceImpl.USER_NAME_ERROR);
        } else if (null == password || "".equalsIgnoreCase(password)) {
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
            jsonObject.put(AppResultConstants.MSG, SysUserServiceImpl.PASSWORD_ERROR);
        } else {
            // 查找用户，判断用户账号密码是否正确
            TsUser tsUser = tsUserMapper.findEffctiveByPhoneNum(phoneNum);
            if (null == tsUser) {
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
                jsonObject.put(AppResultConstants.MSG, SysUserServiceImpl.USER_ERROR);
            } else if (!MD5Util.md5(password).equalsIgnoreCase(tsUser.getPassword())) {
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
                jsonObject.put(AppResultConstants.MSG, SysUserServiceImpl.PASSWORD_WRONG_ERROR);
            } else {
                // 账号密码正确，生成token
                String accessToken = TokenUtils.createJwtToken(phoneNum);
                jsonObject.put(AppResultConstants.DATA, accessToken);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
                jsonObject.put(AppResultConstants.MSG, SysUserServiceImpl.LOGIN_SUCCESS);
            }
        }

        return jsonObject;
    }

    @Override
    public JSONObject logout(String userName) {

        return null;
    }
}
