package com.yhh.whbx.card.query;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.card.model.Cards;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.sale.model.Salesmen;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuhaihui8913 on 2017/12/20.
 */
public class CardsCtr extends CoreController {
    public void page() {
        Page<Cards> page;
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
        if (!isParaBlank("cardtypeId"))
            kv.put("ct.id=", getPara("cardtypeId"));
        if (!isParaBlank("cardapplyId"))
            kv.put("ca.id=", getPara("cardapplyId"));
        if (!isParaBlank("actStatus"))
            kv.put("c.act=", getPara("actStatus"));
        if (!isParaBlank("lockStatus"))
            kv.put("c.isLocked=", getPara("lockStatus"));
        if (!isParaBlank("outStatus"))
            kv.put("d.outStatus=", getPara("outStatus"));
        if (!isParaBlank("salesmenId"))
            kv.put("d.salesmenId=", getPara("salesmenId"));
        if (!isParaBlank("actTime")){
            String[] array= StrUtil.split(getPara("actTime")," - ");
            kv.put("c.actAt>=",StrUtil.trimEnd(array[0]));
            kv.put("c.actAt<",StrUtil.trimEnd(array[1]));
        }

        SqlPara sqlPara = Db.getSqlPara("cards.findPage", Kv.by("cond", kv));
        page = Cards.dao.paginate(getPN(), getPS(), sqlPara);
        renderJson(page);
    }

    public void dataReady() {
        Map<String, Object> map = new HashMap<>();
        map.put("salesmenList", Salesmen.dao.findAll());
        map.put("cardtypeList", Cardtype.dao.findEnableList());
        renderJson(map);
    }

}
