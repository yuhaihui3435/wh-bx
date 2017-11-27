package com.yhh.whbx.auth;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/11/16.
 */
public interface Res {

    String getKey();

    String getPKey();

    String getName();

    String getContent();

    String getType();

    boolean isShow();

    Object getMetadata();

    List<Res> getChildren();


}
