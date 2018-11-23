package com.yhh.whbx.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.aop.Clear;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.render.JsonRender;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Res;
import com.yhh.whbx.admin.model.User;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.interceptors.AdminAAuthInterceptor;
import com.yhh.whbx.interceptors.AdminIAuthInterceptor;
import com.yhh.whbx.kits.CookieKit;
import com.yhh.whbx.kits.ResKit;
import com.yhh.whbx.kits.ext.BCrypt;

import java.util.*;

/**
 * Created by yuhaihui8913 on 2018/1/26.
 * 登录请求 处理
 *
 *
 */
@Clear({AdminIAuthInterceptor.class, AdminAAuthInterceptor.class})
public class LoginCtr extends CoreController{

    public static final int LOGIN_MAX_RETRY_COUNT = 5;//密码重试最大次数
    public static final int LOGIN_PROTECTED_MIN = 15;//登陆失败保护时间
    public static final String LOGIN_RETRY_DATE = "LOGIN_RETRY_DATE";
    public static final String LOGIN_RETRY_COUNT = "LOGIN_RETRY_COUNT";
    public void login(){
        String username=getPara("user");
        String password=getPara("password");
        String rm=getPara("rememberMe");
        DateTime reTryDate = (DateTime) CacheKit.get(Consts.CACHE_NAMES.login.name(), username + "LOGIN_RETRY_DATE");
        if (reTryDate != null) {
            Date now = new Date();
            if (reTryDate.compareTo(now) >= 0) {
                String s = DateUtil.formatBetween(reTryDate, now);
                renderFailJSON("你的账户由于尝试登录失败次数查过5次，暂时被保护。请：" + s + "后重试。");
                return;
            }else{
                CacheKit.remove(Consts.CACHE_NAMES.login.name(),username+LOGIN_RETRY_DATE);
            }
        }
        if(StrUtil.isBlank(username)){
            renderFailJSON("用户名/email/手机号不能为空");
            return;
        }

        if (StrUtil.isBlank(password)){
            renderFailJSON("密码不能为空");
            return;
        }
//        if (ResKit.getConfigBoolean("userAuth")) {
//            if (!validateCaptcha("checkCode")) {
//                renderFailJSON("验证码不正确");
//                return;
//            }
//        }


        User user = com.yhh.whbx.admin.model.User.dao.findFirst("select * from s_user where loginname=? or email=? or phone=?", username,username,username);

        if(user==null){
            renderFailJSON("用户不存在!");
            return;
        }

        if (BCrypt.checkpw(password, user.getPassword())) {
            if (user.getStatus().equals(Consts.STATUS.enable.getVal())) {

                Map<String, Object> data = new HashMap<String, Object>();
                Set<Res> resList = Res.dao.findAllResByUserId(user.getId());
                data.put("resList",resList);
                data.put("username",user.getNickname());
                data.put("loginname",user.getLoginname());
                user.setLogged(new Date());
                user.update();
                if(StrKit.notBlank(rm)&&rm.equals("0"))
                    CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), 60*60*24*14);
                else
                    CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), Consts.COOKIE_TIMEOUT);
                renderSuccessJSON("登录成功", JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect));
                return;
            } else {
                renderFailJSON("该用户被禁用", "");
                return;
            }
        } else {
            Integer pwdErrCount = (Integer) CacheKit.get(Consts.CACHE_NAMES.login.name(), username + "LOGIN_RETRY_COUNT");
            if (pwdErrCount == null) {
                pwdErrCount = 1;
                CacheKit.put(Consts.CACHE_NAMES.login.name(),username+LOGIN_RETRY_COUNT,pwdErrCount);
                renderFailJSON("密码不正确,还可以尝试:" + (LOGIN_MAX_RETRY_COUNT - pwdErrCount) + "次");
            } else if (pwdErrCount == LOGIN_MAX_RETRY_COUNT) {
                Date date = new Date();
                DateTime dateTime = DateUtil.offsetMinute(date, LOGIN_PROTECTED_MIN);
                CacheKit.put(Consts.CACHE_NAMES.login.name(), username + "LOGIN_RETRY_DATE", dateTime);
                CacheKit.remove(Consts.CACHE_NAMES.login.name(), username + "LOGIN_RETRY_COUNT");
                renderFailJSON("您的账号尝试登陆失败次数过多，将被进行保护，请"+LOGIN_PROTECTED_MIN+"分钟后重试");
            } else if (pwdErrCount < LOGIN_MAX_RETRY_COUNT) {

                pwdErrCount++;
                CacheKit.put(Consts.CACHE_NAMES.login.name(),username+LOGIN_RETRY_COUNT,pwdErrCount);
                renderFailJSON("密码不正确,还可以尝试:" + (LOGIN_MAX_RETRY_COUNT - pwdErrCount) + "次");
            }



            return;
        }

    }

    public void logout(){
        CookieKit.remove(this,Consts.USER_ACCESS_TOKEN);
        renderSuccessJSON("退出系统成功");
    }

    public void createCaptch() {
        renderCaptcha();
    }

}
