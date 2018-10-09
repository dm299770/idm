package com.xxx.demo.services.login;

import com.alibaba.fastjson.JSONObject;

/**
 * @description:登录相关的方法
 * @author:@leo.
 */
public interface LoginService {
    /**
     * 登录
     *
     * @param phoneNum 账号
     * @param password 密码
     * @return 返回登录结果及用户信息
     */
    JSONObject login(String phoneNum, String password);

    /**
     * 退出登录
     *
     * @param phoneNum 账号
     * @return 结果信息
     */
    JSONObject logout(String phoneNum);

}
