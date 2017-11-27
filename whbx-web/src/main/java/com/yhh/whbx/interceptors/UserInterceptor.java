package com.yhh.whbx.interceptors;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.yhh.whbx.Consts;
import com.yhh.whbx.auth.AuthHandle;
import com.yhh.whbx.auth.User;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.kits.CookieKit;
import com.yhh.whbx.kits.ReqKit;
import com.yhh.whbx.kits.ResKit;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 * 用户身份认证，前置处理
 *
 *
 */
public class UserInterceptor implements AuthHandle, Interceptor {


    public void intercept(Invocation inv) {
        String domain= ResKit.getConfig("domain");
        CoreController controller = (CoreController)inv.getController();
        String ck=inv.getControllerKey();
        HttpServletRequest request=controller.getRequest();
        String userId= checkUserLoginStatus(controller);
        boolean flag = false;
        User user = null;

        if(StrKit.notBlank(userId)) {
            user = getUser(userId);
            if(user != null) {
                flag = true;
                controller.setAttr(Consts.CURR_USER,user);
            }
        }
        //是否需要用户身份认证,方便测试
        if(ResKit.getConfigBoolean("userAuth"))
            flag=true;

        if(flag) {
            inv.invoke();
        } else {
            String querystring = request.getQueryString();
            String beforeUrl = request.getRequestURL() + "?" + querystring;
            if (StrKit.isBlank(querystring)) {
                beforeUrl = request.getRequestURL().toString();
            }


            CookieKit.remove(controller, Consts.USER_ACCESS_TOKEN);
            if(ReqKit.isAjaxRequest(controller.getRequest())){
                if(ck.contains("admin"))
                    controller.renderUnauthenticationJSON("admin");
                else {
                    controller.renderUnauthenticationJSON("front");
                }
            }else {
                try {
                    if (ck.contains("admin"))
                        controller.redirect(domain+"/admin/login");
                   else
                        controller.redirect(domain+"/index?v=pleaseLogin&callback=" + URLEncoder.encode(beforeUrl, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    LogKit.error("encode转码失败："+e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public User getUser(String userId) {
        return null;
    }

    @Override
    public String checkUserLoginStatus(Controller controller) {
        return CookieKit.get(controller, Consts.USER_ACCESS_TOKEN);
    }
}
