package com.yhh.whbx.auth;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.User;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.kits.CookieKit;
import com.yhh.whbx.kits.ext.BCrypt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuhaihui8913 on 2018/1/26.
 * 登录请求 处理
 *
 *
 */
public class LoginCtr extends CoreController{

    public void login(){
        String username=getPara("user");
        String password=getPara("password");
        String rm=getPara("rememberMe");
        if(StrUtil.isBlank(username)){
            renderFailJSON("用户名不能为空");
            return;
        }

        if (StrUtil.isBlank(password)){
            renderFailJSON("密码不能为空");
            return;
        }


        User user = com.yhh.whbx.admin.model.User.dao.findFirst("select * from s_user where loginname=? and isAdmin='0' ", username);

        if (BCrypt.checkpw(password, user.getPassword())) {
            if (user.getStatus().equals(Consts.STATUS.enable.getVal())) {
                if(StrKit.notBlank(rm)&&rm.equals("0"))
                    CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), 60*60*24*14);
                else
                    CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), Consts.COOKIE_TIMEOUT);
                Map<String, String> data = new HashMap<String, String>();
                user.setLogged(new Date());
                user.update();
                renderSuccessJSON("登录成功", JSON.toJSONString(data));
                return;
            } else {
                renderFailJSON("该用户被禁用", "");
                return;
            }
        } else {
            renderFailJSON("密码不正确", "");
            return;
        }

    }

}
