package com.xxx.demo.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.xxx.demo.dto.sys.UserInfo;
import com.xxx.demo.frame.annotation.CurrentUser;
import com.xxx.demo.frame.annotation.LoginRequired;
import com.xxx.demo.frame.constants.AppResultConstants;
import com.xxx.demo.frame.util.FileUtil;
import com.xxx.demo.frame.util.JsonUtil;
import com.xxx.demo.frame.util.VcUtil;
import com.xxx.demo.models.jsonBean.user.UserInfoRequsetBody;
import com.xxx.demo.models.sys.SysUser;
import com.xxx.demo.models.sys.TsUser;
import com.xxx.demo.services.user.SysUserService;
import com.xxx.demo.services.user.TsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * 用户信息
 *
 * @author leo
 */
@RestController
@RequestMapping({"/user"})
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * App 反馈提示信息
     * */
    public final static String Ex_ERROR = "文件类型错误";
    public final static String SELECT_SUCCESS = "获取成功";


    @Autowired
    private TsUserService tsUserServices;




    /**
     *获取用户信息
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/getUserInfo")
    public Object getUserInfo(@CurrentUser UserInfo user) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("msg",SELECT_SUCCESS);
            jsonObject.put("status",AppResultConstants.SUCCESS_STATUS);
            jsonObject.put("data",user);

            jsonObject = JsonUtil.EraseNull(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取用户信息异常");
            jsonObject.put("msg","服务器异常");
            jsonObject.put("status",AppResultConstants.ERROR_STATUS);
        }
        return jsonObject;
    }


    /**
     * 注册用户
     **/
    @ResponseBody
    @RequestMapping(value = "/registeredUser/{phoneNum}/{password}")
    public Object registeredUser(@PathVariable("phoneNum")String phoneNum, @PathVariable("password")String password) {
        logger.info("registeredUser:phoneNum:" + phoneNum + ",password:" + password );
        JSONObject jsonObject =tsUserServices.registeredUser(phoneNum,password,null);
        return jsonObject;

    }

    /***
     *重置密码
     **/
    //@LoginRequired (重置密码不需验证用户jwt)
    @ResponseBody
    @RequestMapping(value = "/resetUserPassword/{phoneNum}/{newPassword}")
    public Object resetUserPassword(@PathVariable("phoneNum")String phoneNum, @PathVariable("newPassword")String newPassword) {
        //JSONObject jsonObject = sysUserServices.resetUserPassword(userId, newPassword);
        JSONObject jsonObject = tsUserServices.resetUserPassword(phoneNum,newPassword);
        return jsonObject;
    }

    /**
     * 修改密码
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/modifyUserPassword/{oldPassword}/{newPassword}")
    public Object modifyUserPassword(@CurrentUser UserInfo user, @PathVariable("oldPassword")String oldPassword, @PathVariable("newPassword")String newPassword) {
        String phoneNum = user.getPhoneNum();
        JSONObject jsonObject = tsUserServices.modifyUserPassword(phoneNum, oldPassword, newPassword);
        return jsonObject;
    }

    /**
     * 修改用户属性(单一)
     * @param user
     * @param userInfoRequsetBody
     * @return
     */
    @LoginRequired
    @ResponseBody
    //@RequestMapping(value = "/modifyUserInfo/{type}/{value}")
    @RequestMapping(value = "/modifyUserInfo")
    public Object modifyUserInfo(@CurrentUser UserInfo user ,@RequestBody UserInfoRequsetBody userInfoRequsetBody) {
        JSONObject jsonObject = null;
        String type = userInfoRequsetBody.getType();
        String value = userInfoRequsetBody.getValue();
        //@CurrentUser
        //@RequestParam(value="type")
        //UserInfo user = null;
        if(user==null){
            jsonObject.put(AppResultConstants.MSG, AppResultConstants.LOGIN_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
        }else{
            logger.info(user.getPhoneNum());
            logger.info("registeredUser:phoneNum:" + user.getPhoneNum() + ",type:" + type + ",value:" + value);
            jsonObject = tsUserServices.modifyUserInfo(user.getPhoneNum(), type, value);
        }
        return jsonObject;
    }

    /**
     *
     * @param user
     * @param imageFile  头像文件
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/uploadProfilePhoto")
    public Object uploadProfilePhoto(@CurrentUser UserInfo user,@RequestParam(value = "imageFile") MultipartFile imageFile){
       //System.out.println(imageFile.getSize()) ;
        JSONObject jsonObject = new JSONObject();
        String[] exArray = {"png","jpg","gif"};//属性值必须包含于这4项
        List<String> typeList = Arrays.asList(exArray);
        //判断拓展名
        if (!typeList.contains(FileUtil.getTypePart(imageFile.getOriginalFilename()))){
            jsonObject.put(AppResultConstants.MSG,Ex_ERROR);
            jsonObject.put(AppResultConstants.STATUS, AppResultConstants.FAIL_STATUS);
            return jsonObject;
        }
        jsonObject = tsUserServices.setProfilePhoto(user.getUserId(),user.getPhoneNum(),imageFile);

        return jsonObject;
    }
}


