package com.yhh.whbx.card.report;

import com.jfinal.kit.Kv;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.core.CoreController;

/**
 * Created by yuhaihui8913 on 2018/1/23.
 */
public class ReportCtr extends CoreController {
    /**
     * 卡销售量统计
     */
    public void ds00(){
        Kv kv= Kv.create();

        if (!isParaBlank("sTime")) {
            String[] array = StrUtil.split(getPara("sTime"), " - ");
            kv.put("c.actAt>=", StrUtil.trimEnd(array[0]));
            kv.put("c.actAt<", StrUtil.trimEnd(array[1]));
        }
    }



}
