package com.yhh.whbx.interceptors;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Res;
import com.yhh.whbx.admin.model.Role;
import com.yhh.whbx.admin.model.User;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreException;
import com.yhh.whbx.kits.CookieKit;
import com.yhh.whbx.kits.ReqKit;
import com.yhh.whbx.kits.ResKit;

import java.math.BigInteger;

/**
 *
 * 用户身份认证，前置处理
 *
 *
 */
public class AdminIAuthInterceptor implements Interceptor {


    public void intercept(Invocation inv) {
        CoreController controller = (CoreController)inv.getController();
        String userId= CookieKit.get(controller,Consts.USER_ACCESS_TOKEN);
        boolean flag = false;
        User user=null;
        if (StrUtil.isNotEmpty(userId)) {
            flag=true;
            user=User.dao.findFirstByCache(Consts.CACHE_NAMES.user.name(),new BigInteger(userId),"select * from s_user where status='0' and id=? ",new BigInteger(userId));
            if(user==null){
                if(ReqKit.isAjaxRequest(controller.getRequest())){
                    controller.renderUnauthenticationJSON("admin");

                }else {
                    throw new CoreException("你的账户被停用");
                }
            }

            controller.setAttr(Consts.CURR_USER,user);
            controller.setAttr(Consts.CURR_USER_ROLES, Role.dao.findRolesByUserId(user.getId()));
            controller.setAttr(Consts.CURR_USER_RESES, Res.dao.findResesByUserId(user.getId()));
        }
        //是否需要用户身份认证,方便测试
        if(!ResKit.getConfigBoolean("userAuth"))
            flag=true;

        if(flag) {
            inv.invoke();
        } else {
            CookieKit.remove(controller, Consts.USER_ACCESS_TOKEN);
            if(ReqKit.isAjaxRequest(controller.getRequest())){
                    controller.renderUnauthenticationJSON("admin");
            }else {
                throw new CoreException("身份认证失败，请登录！");
            }
        }
    }
}
