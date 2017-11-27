package com.yhh.whbx.auth;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/11/16.
 */
public interface Role {

    String getKey();

    String getName();

    List<Res> getHoldRes();

    boolean hasResByName(String resName);

    boolean hasResByContent(String resContent);

    boolean hasResByKey(String key);

    Object getMetadata();

}
