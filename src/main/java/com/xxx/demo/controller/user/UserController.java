package com.xxx.demo.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.xxx.demo.frame.annotation.CurrentUser;
import com.xxx.demo.frame.annotation.LoginRequired;
import com.xxx.demo.models.sys.SysUser;
import com.xxx.demo.models.sys.TsUser;
import com.xxx.demo.services.user.SysUserService;
import com.xxx.demo.services.user.TsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息
 *
 * @author leo
 */
@RestController
@RequestMapping({"/user"})
public class UserController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SysUserService sysUserServices;
    @Autowired
    private TsUserService tsUserServices;

//    @ResponseBody
//    @RequestMapping(value = "/registeredUser")
//    public Object registeredUser(String userName, String realName, String cellphone, String emodelId, String password, String email, String description) {
//        logger.info("registeredUser:userName:" + userName + ",realName:" + realName + ",cellphone:" + cellphone + ",emodelId:" + emodelId + ",password" + password + ",email" + email + ",description:" + description);
//        JSONObject jsonObject = sysUserServices.registeredUser(userName, realName, cellphone, emodelId, password, email, description);
//        return jsonObject;
//    }

    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/modifyUserPassword")
    public Object modifyUserPassword(String userId, String oldPassword, String newPassword) {
        JSONObject jsonObject = sysUserServices.modifyUserPassword(userId, oldPassword, newPassword);
        return jsonObject;
    }


    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/modifyUserInfo")
    public Object modifyUserInfo(@CurrentUser SysUser user, String userId, String userName, String realName, String cellphone, String emodelId,
                                 @RequestParam(value = "email", required = false) String email,
                                 @RequestParam(value = "description", required = false) String description) {
        logger.info(user.getUserName());
        logger.info("registeredUser:userId:" + userId + ",userName:" + userName + ",realName:" + realName + ",cellphone:" + cellphone + ",emodelId:" + emodelId + ",email" + email + ",description:" + description);
        JSONObject jsonObject = sysUserServices.modifyUserInfo(userId, userName, realName, cellphone, emodelId, email, description);
        return jsonObject;
    }

//    @LoginRequired
//    @ResponseBody
//    @RequestMapping(value = "/resetUserPassword")
//    public Object resetUserPassword(String userId, String newPassword) {
//        JSONObject jsonObject = sysUserServices.resetUserPassword(userId, newPassword);
//        return jsonObject;
//    }

    /**
     *获取用户信息
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/getUserInfo")
    public Object getUserInfo(@CurrentUser TsUser user) {
        return user;
    }

    /**
     * 注册用户
     **/
    @ResponseBody
    @RequestMapping(value = "/registeredUser")
    public Object registeredUser(String phoneNum, String password) {
        logger.info("registeredUser:phoneNum:" + phoneNum + ",password:" + password );
        JSONObject jsonObject =tsUserServices.registeredUser(phoneNum,password,null);
        return jsonObject;
    }

    /***
     *重置密码
     **/
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/resetUserPassword")
    public Object resetUserPassword(String phoneNum, String newPassword) {
        //JSONObject jsonObject = sysUserServices.resetUserPassword(userId, newPassword);
        JSONObject jsonObject = tsUserServices.resetUserPassword(phoneNum,newPassword);
        return jsonObject;
    }



}


