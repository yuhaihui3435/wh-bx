package com.yhh.whbx.interceptors;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.User;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.kits.ReqKit;
import com.yhh.whbx.kits.ResKit;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yuhaihui8913 on 2017/11/16.
 * 请求访问控制前置处理
 *
 */
public class AuthorizationInterceptor implements Interceptor{
    @Override
    public void intercept(Invocation invocation) {
        String domain= ResKit.getConfig("domain");
        CoreController controller=(CoreController) invocation.getController();
        String ck=invocation.getControllerKey();
        User user=controller.getAttr(Consts.CURR_USER);
        HttpServletRequest request=controller.getRequest();
        boolean flag=false;
        flag=user.hasResByNameOrContent(ck);

        //是否需要用户身份认证,方便测试
        if(ResKit.getConfigBoolean("userAuth"))
            flag=true;

        if(flag) {
            invocation.invoke();
        } else {
            String querystring = request.getQueryString();
            String beforeUrl = request.getRequestURL() + "?" + querystring;
            if (StrKit.isBlank(querystring)) {
                beforeUrl = request.getRequestURL().toString();
            }
            if(ReqKit.isAjaxRequest(controller.getRequest())){
                if(ck.contains("admin"))
                    controller.renderUnauthorizationJSON("admin");
                else {
                    controller.renderUnauthorizationJSON("front");
                }
            }else {
                try {
                    controller.setAttr("beforeUrl",URLEncoder.encode(beforeUrl, "UTF-8"));
                    controller.render("/common/403.html");
                } catch (UnsupportedEncodingException e) {
                    LogKit.error("encode转码失败："+e.getMessage());
                    e.printStackTrace();
                }
            }
        }


    }
}
