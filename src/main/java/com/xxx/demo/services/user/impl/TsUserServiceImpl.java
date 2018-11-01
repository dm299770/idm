package com.xxx.demo.services.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.xxx.demo.dto.sys.UserInfo;
import com.xxx.demo.frame.constants.AppResultConstants;
import com.xxx.demo.frame.constants.ApplicationPropertiesConstants;
import com.xxx.demo.frame.util.FileUtil;
import com.xxx.demo.frame.util.MD5Util;
import com.xxx.demo.frame.util.SMSUtil;
import com.xxx.demo.frame.util.VcUtil;
import com.xxx.demo.mapper.user.TsUserInfoMapper;
import com.xxx.demo.mapper.user.TsUserMapper;
import com.xxx.demo.models.sys.TsUser;
import com.xxx.demo.models.sys.TsUserInfo;
import com.xxx.demo.services.user.TsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

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
    public final static String CELL_PHONE_ERROR = "手机号格式不正确";
    public final static String CELL_EXIST_ERROR = "手机号已注册";
    public final static String CELL_NOTEXIST_ERROR = "手机号未注册或已失效";
    public final static String PASSWORD_ERROR = "密码格式不正确";
    public final static String PASSWORD_WRONG_ERROR = "密码错误";
    public final static String OLD_PASSWORD_ERROR = "原密码格式不正确";
    public final static String OLD_PASSWORD_WRONG_ERROR = "原密码不正确";
    public final static String NEW_PASSWORD_ERROR = "新密码格式不正确";
    public final static String TYPE_ERROR = "属性值不存在或不能为空";
    public final static String SAVE_SUCCESS = "保存成功";
    public final static String SIGNIN_SUCCESS = "注册成功";
    public final static String LOGIN_SUCCESS = "登录成功";
    public final static String MODIFY_SUCCESS = "修改成功";
    public final static String SERVER_ERROR = "服务器异常";
    public final static String MODIFY_FAIL = "修改失败";

    public final static String USER_ERROR = "用户信息错误";
    public final static String USER_NAME_ERROR = "账号格式不正确";


    @Autowired
    private TsUserMapper tsUserMapper;
    @Autowired
    private TsUserInfoMapper tsUserInfoMapper;
    @Autowired
    private ApplicationPropertiesConstants applicationConstants;


    @Override
    public TsUser findById(String id) {
        return tsUserMapper.findById(id);
    }

    @Override
    public UserInfo findEffctiveUserInfoByPhoneNum(String phoneNum) {
        return tsUserMapper.findEffctiveUserInfoByPhoneNum(phoneNum);
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
    public void saveUserInfo(TsUserInfo bean) {
        tsUserInfoMapper.save(bean);
    }

    @Override
    public void delete(String[] userIds) {
        tsUserMapper.delete(userIds);
    }


    @Override
    public void updateUserInfo(String phoneNum, String type, String value) {
        tsUserInfoMapper.updateByType(phoneNum, type, value);
    }

    @Override
    public void updatePassword(String phoneNum, String oldPassword, String newPassword) {
        tsUserMapper.updatePassword(phoneNum, oldPassword, newPassword);
    }

    @Override
    public void reSetPassword(String phoneNum, String newPassword) {
        tsUserMapper.reSetPassword(phoneNum, newPassword);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public JSONObject registeredUser(String phoneNum, String password, String token) {
        //验证码校验,功能暂未实现

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
        else if (findEffctiveByPhoneNum(phoneNum) != null) {
            jsonObject.put(AppResultConstants.MSG, CELL_EXIST_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else {
            // AppResultConstants.FAIL_STATUS.保存用户
            // isPassed:APP申请注册账号是否审核通过.1审核通过；0待审核；AppResultConstants.FAIL_STATUS审核不通过
            // 密码加密-修改为前台加密
            // password = MD5Util.md5(password);
            //TsUser bean = new TsUser(UUID.randomUUID() + "",phoneNum ,password,1,new Date(),null);
            //user基本信息
            TsUser user = new TsUser();
            String uuid = UUID.randomUUID() + "";
            user.setUserId(uuid);
            user.setPhoneNum(phoneNum);
            user.setPassword(password);
            user.setIsEffctive(1);
            user.setCreateDate(new Date());
            user.setToken(null);
            //添加userInfo信息
            TsUserInfo userInfo = new TsUserInfo();
            userInfo.setUserId(uuid);
            userInfo.setNickName("user_"+(Math.random()*9+1)*1000);//默认初始昵称
            userInfo.setIsEffctive(1);
            userInfo.setCreateDate(new Date());
            try {
                save(user);
                saveUserInfo(userInfo);
                // 3.组织返回信息
                jsonObject.put(AppResultConstants.MSG, SIGNIN_SUCCESS);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
            } catch (Exception e) {
                logger.error("registeredUser:" + e);
                jsonObject.put(AppResultConstants.MSG, SERVER_ERROR);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.ERROR_STATUS);
            }
        }
        return jsonObject;
    }

    @Override
    public JSONObject resetUserPassword(String phoneNum, String newPassword) {
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
        else if (findEffctiveByPhoneNum(phoneNum) == null) {
            jsonObject.put(AppResultConstants.MSG, CELL_NOTEXIST_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else {
            try {
                reSetPassword(phoneNum, newPassword);
                jsonObject.put(AppResultConstants.MSG, MODIFY_SUCCESS);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
            } catch (Exception e) {
                logger.error("resetUserPassword:" + e);
                jsonObject.put(AppResultConstants.MSG, SERVER_ERROR);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.ERROR_STATUS);


            }
        }
        return jsonObject;
    }


    @Override
    public JSONObject modifyUserPassword(String phoneNum, String oldPassword, String newPassword) {
        JSONObject jsonObject = new JSONObject();
        // 校验参数合法性
        if (null == phoneNum || "".equalsIgnoreCase(phoneNum)) {
            jsonObject.put(AppResultConstants.MSG, CELL_PHONE_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else if (null == oldPassword || "".equalsIgnoreCase(oldPassword)) {
            jsonObject.put(AppResultConstants.MSG, OLD_PASSWORD_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else if (null == newPassword || "".equalsIgnoreCase(newPassword)) {
            jsonObject.put(AppResultConstants.MSG, NEW_PASSWORD_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else {
            try {
                TsUser tsUser = findEffctiveByPhoneNum(phoneNum);
                if (null != tsUser && oldPassword.equalsIgnoreCase(tsUser.getPassword())) {
                    updatePassword(phoneNum, oldPassword, newPassword);
                    jsonObject.put(AppResultConstants.MSG, MODIFY_SUCCESS);
                    jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
                } else {
                    jsonObject.put(AppResultConstants.MSG, OLD_PASSWORD_WRONG_ERROR);
                    jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
                }

            } catch (Exception e) {
                logger.error("modifyUserPassword:" + e);
                jsonObject.put(AppResultConstants.MSG, SERVER_ERROR);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.ERROR_STATUS);
            }
        }
        return jsonObject;
    }

    @Override
    public JSONObject modifyUserInfo(String phoneNum, String type, String value) {
        JSONObject jsonObject = new JSONObject();
        String[] typeArray = {"NICK_NAME", "REAL_NAME", "SEXUAL", "EMEY_CONTACT", "PROFILE_PHOTO"};//属性值必须包含于这4项
        List<String> typeList = Arrays.asList(typeArray);
        // 1.校验参数合法性
        if (null == phoneNum || "".equalsIgnoreCase(phoneNum)) {
            jsonObject.put(AppResultConstants.MSG, CELL_PHONE_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else if (null == type || "".equalsIgnoreCase(type)) {
            jsonObject.put(AppResultConstants.MSG, TYPE_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else if (!typeList.contains(type)) {
            jsonObject.put(AppResultConstants.MSG, TYPE_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else {
            // 更新用户信息
            try {
                updateUserInfo(phoneNum, type, value);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
                jsonObject.put(AppResultConstants.MSG, MODIFY_SUCCESS);
            } catch (Exception e) {
                logger.error("modifyUserInfo:" + e);
                jsonObject.put(AppResultConstants.MSG, SERVER_ERROR);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.ERROR_STATUS);
            }
        }
        return jsonObject;
    }

    @Override
    public JSONObject setProfilePhoto(String userid, String phoneNum, MultipartFile file) {

        JSONObject jsonObject = new JSONObject();
        //将头像输出到指定目录
        String photopath = applicationConstants.getPhotoPath();
        if (photopath == null && photopath.equalsIgnoreCase("")) {
            logger.debug("头像文件目标路径不存在");
            jsonObject.put(AppResultConstants.MSG, SERVER_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.ERROR_STATUS);

        } else if (file == null) {
            //验证文件不能为空
            jsonObject.put(AppResultConstants.MSG, MODIFY_FAIL);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        } else {

            try {
                // File targetPhoto = new File(photopath+File.separator+userid+File.separator+System.currentTimeMillis()+".png");
                //File phontoParentPath = new File(photopath+File.separator+userid);
               /* if(!targetPhoto.getParentFile().exists()){
                    targetPhoto.getParentFile().mkdirs();
                }

                if(!targetPhoto.exists()){
                    targetPhoto.createNewFile();
                }*/

                File fileRoot = new File(photopath + File.separator + userid);
                String newFileName = System.currentTimeMillis() + "." + FileUtil.getTypePart(file.getOriginalFilename());
                if (!fileRoot.exists()) {
                    fileRoot.mkdirs();
                }
                File targetFile = new File(fileRoot.getAbsolutePath(), newFileName);
                file.transferTo(targetFile);
                //更新用户头像url
                String photo_url = applicationConstants.getServerHost() + ":" + applicationConstants.getServerPort()
                        + "/" + userid + "/" + targetFile.getName();
                //modifyUserInfo(phoneNum,"PROFILE_PHOTO",photo_url);
                updateUserInfo(phoneNum, "PROFILE_PHOTO", photo_url);
                jsonObject.put(AppResultConstants.MSG, SAVE_SUCCESS);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.SUCCESS_STATUS);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("拷贝头像到目标路径异常:" + e.getMessage());
                jsonObject.put(AppResultConstants.MSG, SERVER_ERROR);
                jsonObject.put(AppResultConstants.STATUS, AppResultConstants.ERROR_STATUS);
            }
        }

        return jsonObject;
    }

    @Override
    public JSONObject sendVCtoPhoneNum(String phoneNum) {


        //发送到目标手机
        //String status = SMSUtil.sendSms();
        //根据status判断是否发送成功 ,并保存60秒后过期


        return null;
    }


}
