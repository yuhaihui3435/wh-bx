package com.yhh.whbx.card.query;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.core.CoreController;

/**
 * Created by yuhaihui8913 on 2017/12/20.
 */
public class CardsCtr extends CoreController {
    public void page() {
        Page<Record> page;
        Kv kv = Kv.create();
        if (!isParaBlank("code"))
            kv.put("c.code=", getPara("code"));
        if (!isParaBlank("bNum"))
            kv.put("d.bNum=", getPara("bNum"));
        if (!isParaBlank("eNum"))
            kv.put("d.eNum=", getParaToLong("eNum"));
        if (!isParaBlank("faceVal"))
            kv.put("ct.faceVal=", getPara("faceVal"));
        if (!isParaBlank("status"))
            kv.put("c.status=", getPara("status"));
        if (!isParaBlank("actTime")){
            String[] array= StrUtil.split(getPara("actTime")," - ");
            kv.put("c.actAt>=",StrUtil.trimEnd(array[0]));
            kv.put("c.actAt<",StrUtil.trimEnd(array[1]));
        }

        SqlPara sqlPara = Db.getSqlPara("cardapply.findPage", Kv.by("cond", kv));
        page = Db.paginate(getPN(), getPS(), sqlPara);
        renderJson(page);
    }

}
