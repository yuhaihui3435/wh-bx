package com.yhh.whbx.card.apply;

import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cardapply;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card.apply]
 * 类名称:    [CardapplyCtr]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/13]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class CardapplyCtr extends CoreController {
    public void page() {
        Page<Cardapply> page;
        Kv kv = Kv.create();
        if (!isParaBlank("cardtypeId"))
            kv.put("cardtypeId=", getParaToLong("cardtypeId"));

        if (!isParaBlank("batch") )
            kv.put("batch=", getParaToLong("batch"));
        if (!isParaBlank("media") )
            kv.put("media=", getParaToLong("media"));
        if (!isParaBlank("checkStatus") )
            kv.put("checkStatus=", getPara("checkStatus"));
        if (!isParaBlank("status") )
            kv.put("status=", getPara("status"));
        if (!isParaBlank("exeCard"))
            kv.put("exeCard=", getPara("exeCard"));

        SqlPara sqlPara = Db.getSqlPara("cardapply.findPage", Kv.by("cond", kv));
        page = Cardapply.dao.paginate(getPN(), getPS(), sqlPara);
        renderJson(page);
    }

    public void list(){
        renderJson(Cardapply.dao.findAllNotDel());
    }

    @Before({CardapplyValidator.class, Tx.class})
    public void save(){
        Cardapply cardapply=getModel(Cardapply.class,"",true);
        cardapply.setCAt(new Date());
        cardapply.setStatus(Consts.STATUS.enable.getVal());
        cardapply.setCheckStatus(Consts.CHECK_STATUS.waitingCheck.getVal());
        cardapply.setExeCard(Consts.YORN_STR.no.getVal());
        cardapply.setOper(currUser()==null?null:Integer.parseInt(currUser().getId()));
        cardapply.save();
        renderSuccessJSON("新增操作成功。");
    }

    @Before({CardapplyValidator.class, Tx.class})
    public void update(){
        Cardapply cardapply=getModel(Cardapply.class,"",true);
        String exe=getPara("exe");
        cardapply.update();
        String msg="操作成功";
        switch (exe){
            case "update":
                msg="修改操作成功。";
            case "stop":
                msg="停用操作成功。";
            case "active":
                msg="激活操作成功。";
            case "del":
                msg="删除操作成功。";
            case "check":
                msg=cardapply.getCheckStatus().equals(Consts.CHECK_STATUS.normal)?"审核通过。":"审核未通过";
            default:
                renderSuccessJSON(msg);
                return;
        }

    }

    public void get(){
        Long id=getParaToLong("id");
        renderJson(Cardapply.dao.findById(id));
    }


    public void dataReady(){
        Map<String,Object> map=new HashMap<>();
        map.put("mediaList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"mediaList"));
        map.put("cardtypeList", Cardtype.dao.findEnableList());
        renderJson(map);
    }
}
