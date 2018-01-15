package com.yhh.whbx.card.query;

import com.jfinal.aop.Before;
import com.jfinal.aop.Duang;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.CardsService;
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

    private final static CardsService cardsService = Duang.duang(CardsService.class);

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
        if (!isParaBlank("exportStatus"))
            kv.put("c.exportCode", getPara("exportStatus"));
        if(!isParaBlank("exportCode"))
            kv.put("c.exportCode=", getPara("exportCode"));
        if(!isParaBlank("policyNum"))
            kv.put("c.policyNum=", getPara("policyNum"));
        if(!isParaBlank("phIdNum"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType"))&&getPara("cardtypeType").equals("407"))?"ph.idCard=":"cph.idCard=", getPara("phIdNum"));
        if(!isParaBlank("phName"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType"))&&getPara("cardtypeType").equals("407"))?"ph.name=":"cph.name=", getPara("phName"));
        if(!isParaBlank("ipIdNum"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType"))&&getPara("cardtypeType").equals("407"))?"ip.idCard=":"cip.idCard=", getPara("ipIdNum"));
        if(!isParaBlank("ipName"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType"))&&getPara("cardtypeType").equals("407"))?"ip.name=":"cip.name=", getPara("ipName"));

        if (!isParaBlank("cardtypeType"))
            kv.put("ct.type=", getPara("cardtypeType"));
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
    @Before({Tx.class})
    public void lock(){
        //将card状态更新到锁定状态
        int cardsId=getParaToInt("cardsId");
        Cards cards=Cards.dao.findById(cardsId);
        cards.setActAt(null);
        cards.setAct(Consts.YORN_STR.no.getVal());
        cards.setIsLocked(Consts.YORN_STR.no.getVal());
        cards.update();

        //删除掉激活录入的数据
        Cardtype cardtype=cardsService.getCardtypeByCardcode(cards.getCode());
        cardsService.cleanActData(cardtype.getTypeVal(),cardsId);

        renderSuccessJSON("卡锁定成功，激活数据被清除");
    }
    @Before({Tx.class})
    public void unlock(){
        int cardsId=getParaToInt("cardsId");
        Cards cards=Cards.dao.findById(cardsId);
        cards.setIsLocked(Consts.YORN_STR.yes.getVal());
        cards.update();

        renderSuccessJSON("卡解锁成功");

    }

    @Before({Tx.class})
    public void unAct(){
        int cardsId=getParaToInt("cardsId");
        Cards cards=Cards.dao.findById(cardsId);
        cards.setActAt(null);
        cards.setAct(Consts.YORN_STR.no.getVal());
        cards.update();

        //删除掉激活录入的数据
        Cardtype cardtype=cardsService.getCardtypeByCardcode(cards.getCode());
        cardsService.cleanActData(cardtype.getTypeVal(),cardsId);

        renderSuccessJSON("卡激活撤销成功");

    }

    public void exportActCards(){
        String ids=getPara("ids");
        if(StrUtil.isNotBlank(ids)){

        }
    }



}
