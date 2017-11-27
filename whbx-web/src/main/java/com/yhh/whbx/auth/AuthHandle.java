package com.yhh.whbx.auth;

import com.jfinal.core.Controller;

/**
 * Created by yuhaihui8913 on 2017/11/16.
 */
public interface AuthHandle {

    User getUser(String userId);

    String checkUserLoginStatus(Controller controller);

}
