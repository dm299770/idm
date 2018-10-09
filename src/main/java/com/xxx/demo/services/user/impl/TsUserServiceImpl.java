package com.xxx.demo.services.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.xxx.demo.frame.constants.AppResultConstants;
import com.xxx.demo.frame.util.MD5Util;
import com.xxx.demo.mapper.user.TsUserMapper;
import com.xxx.demo.models.sys.TsUser;
import com.xxx.demo.services.user.TsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * 系统用户service
 *
 * @author leo
 */
@Service
public class TsUserServiceImpl implements TsUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 返回给APP端提示信息
     */
    public final static String USER_ID_ERROR = "用户主键不能为空";
    public final static String USER_ERROR = "用户信息错误";
    public final static String USER_NAME_ERROR = "账号不能为空";
    public final static String REAL_NAME_ERROR = "姓名不能为空";
    public final static String CELL_PHONE_ERROR = "手机号不能为空";
    public final static String CELL_EXIST_ERROR = "手机号已注册";
    public final static String CELL_NOTEXIST_ERROR = "手机号未注册或已失效";
    public final static String EMODEL_ID_ERROR = "所属单位不能为空";
    public final static String PASSWORD_ERROR = "密码不能为空";
    public final static String PASSWORD_WRONG_ERROR = "密码错误";
    public final static String OLD_PASSWORD_ERROR = "原密码不能为空";
    public final static String OLD_PASSWORD_WRONG_ERROR = "原密码不能为空";
    public final static String NEW_PASSWORD_ERROR = "新密码不能为空";
    public final static String SAVE_SUCCESS = "保存成功";
    public final static String LOGIN_SUCCESS = "登录成功";

    @Autowired
    private TsUserMapper tsUserMapper;

    @Override
    public TsUser findById(String id) {
        return tsUserMapper.findById(id);
    }

    @Override
    public TsUser findEffctiveByPhoneNum(String phoneNum) {
        return tsUserMapper.findEffctiveByPhoneNum(phoneNum);
    }

    @Override
    public void save(TsUser bean) {
        tsUserMapper.save(bean);
    }


    @Override
    public void delete(String[] userIds) {
        tsUserMapper.delete(userIds);
    }



//    @Override
//    public void update(TsUser bean) {
//        tsUserMapper.update(bean);
//    }

    @Override
    public void updatePassword(String phoneNum, String oldPassword, String newPassword) {
        tsUserMapper.updatePassword(phoneNum, oldPassword, newPassword);
    }

    @Override
    public void reSetPassword(String phoneNum, String newPassword) {
        tsUserMapper.reSetPassword(phoneNum, newPassword);
    }

    @Override
    public  JSONObject registeredUser(String phoneNum,String password, String token){
        JSONObject jsonObject = new JSONObject();
        // 1.校验参数合法性(phoneNum、password)
        if (null == phoneNum || "".equalsIgnoreCase(phoneNum)) {
            jsonObject.put(AppResultConstants.MSG, CELL_PHONE_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else if (null == password || "".equalsIgnoreCase(password)) {
            jsonObject.put(AppResultConstants.MSG, PASSWORD_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        }
        //注册用户是否存在验证
        else if(findEffctiveByPhoneNum(phoneNum)!=null){
            jsonObject.put(AppResultConstants.MSG, CELL_EXIST_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        }
        else {
            // AppResultConstants.FAIL_STATUS.保存用户
            // isPassed:APP申请注册账号是否审核通过.1审核通过；0待审核；AppResultConstants.FAIL_STATUS审核不通过
            // 密码加密-修改为前台加密
            password = MD5Util.md5(password);
            //TsUser bean = new TsUser(UUID.randomUUID() + "",phoneNum ,password,1,new Date(),null);
            TsUser bean = new TsUser();
            bean.setUserId(UUID.randomUUID() + "");
            bean.setPhoneNum(phoneNum);
            bean.setPassword(password);
            bean.setIsEffctive(1);
            bean.setCreateTime(new Date());
            bean.setToken(null);
            //TODO AppResultConstants.FAIL_STATUS.1保存用户与区域的关系
            try {
                save(bean);
                // 3.组织返回信息
                jsonObject.put(AppResultConstants.MSG, SAVE_SUCCESS);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
            } catch (Exception e) {
                logger.error("registeredUser:" + e);
            }
        }
        return jsonObject;
    }

    @Override
    public JSONObject resetUserPassword(String phoneNum,String newPassword) {
        JSONObject jsonObject = new JSONObject();
        // 校验参数合法性
        if (null == phoneNum || "".equalsIgnoreCase(phoneNum)) {
            jsonObject.put(AppResultConstants.MSG, CELL_PHONE_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else if (null == newPassword || "".equalsIgnoreCase(newPassword)) {
            jsonObject.put(AppResultConstants.MSG, NEW_PASSWORD_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        }
        //注册用户是否存在验证
        else if(findEffctiveByPhoneNum(phoneNum)==null){
            jsonObject.put(AppResultConstants.MSG,CELL_NOTEXIST_ERROR );
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        }
        else {
            try {
                reSetPassword(phoneNum,newPassword);
                jsonObject.put(AppResultConstants.MSG, SAVE_SUCCESS);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
            } catch (Exception e) {
                logger.error("resetUserPassword:" + e);
            }
        }
        return jsonObject;
    }

}
