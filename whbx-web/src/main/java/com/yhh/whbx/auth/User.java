package com.yhh.whbx.auth;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/11/16.
 */
public interface User {

    String getKey();

    String getUsername();

    String getLoginname();

    String getPwd();

    String getEmail();

    String getPhone();

    String getSex();

    String getAge();

    Object getMetadata();

    List<Role> getHoldRoleLIST();

    String getHoldRoleJSON();

    List<Res> getHoldResLIST();

    String getHoldResJSON();

    boolean hasRole(String rolename);

    boolean hasResByNameOrContent(String str);

    boolean isAdmin();

}
