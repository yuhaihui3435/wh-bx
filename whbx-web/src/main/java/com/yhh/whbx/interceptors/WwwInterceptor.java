package com.yhh.whbx.interceptors;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.yhh.whbx.core.CoreController;

/**
 * Created by yuhaihui8913 on 2017/12/26.
 */
public class WwwInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        CoreController controller = (CoreController)invocation.getController();
        String ck=invocation.getControllerKey();
        String ak=invocation.getActionKey();

        if(ak.equals("/")){
            controller.setAttr("indexCss","gradient");
        }else{
            controller.setAttr("mainNavClass","gradient");
        }

        invocation.invoke();


    }
}
