package com.yhh.whbx.interceptors;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.ehcache.CacheKit;
import com.yhh.whbx.Consts;
import com.yhh.whbx.core.CoreController;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/26.
 */
public class WwwInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        CoreController controller = (CoreController)invocation.getController();
        String ck=invocation.getControllerKey();
        String ak=invocation.getActionKey();
        List<String> list=CacheKit.getKeys(Consts.CACHE_NAMES.paramCache.name());
        for (String str:list){
            controller.setAttr(str,(String)CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),str));
        }

        if(ak.equals("/")){
            controller.setAttr("indexCss","gradient");
        }else{
            controller.setAttr("mainNavClass","gradient");
        }

        invocation.invoke();


    }
}
