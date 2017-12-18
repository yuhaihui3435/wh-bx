package com.yhh.whbx.card.depot;

import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cards;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.card.model.Depot;
import com.yhh.whbx.card.model.Unlock;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.sale.model.Salesmen;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card.depot]
 * 类名称:    [DepotCtr]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/16]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class DepotCtr extends CoreController {

    private DepotService service=DepotService.service;

    public void page() {
        LogKit.info(service.toString());
        Page<Depot> page;
        Kv kv = Kv.create();
        if (!isParaBlank("cardtypeId"))
            kv.put("cardtypeId=", getParaToLong("cardtypeId"));
        if (!isParaBlank("cardapplyId"))
            kv.put("cardapplyId=", getParaToLong("cardapplyId"));
        if (!isParaBlank("outStatus"))
            kv.put("outStatus=", getPara("outStatus"));
        if (!isParaBlank("status"))
            kv.put("status=", getPara("status"));
        if (!isParaBlank("salesmenId"))
            kv.put("salesmenId=", getParaToInt("salesmenId"));
        if (!isParaBlank("outTime")){
            String[] array= StrUtil.split(getPara("outTime")," - ");
            kv.put("outTime>=",StrUtil.trimEnd(array[0]));
            kv.put("outTime<",StrUtil.trimEnd(array[1]));
        }




        SqlPara sqlPara = Db.getSqlPara("depot.findPage", Kv.by("cond", kv));
        page = Depot.dao.paginate(getPN(), getPS(), sqlPara);
        renderJson(page);
    }

    @Before({DepotValidator.class, Tx.class})
    public void save() {
        LogKit.info(service.toString());
        Depot depot = getModel(Depot.class, "", true);
        depot.setCAt(new Date());
        depot.setStatus(Consts.STATUS.enable.getVal());
        depot.setOper(currUser() == null ? null : Integer.parseInt(currUser().getId()));
        depot.setOutStatus(Consts.YORN_STR.no.getVal());
        String str=service.checkCount(depot.getBNum(),depot.getENum(),depot.getCardapplyId());
        if(str.equals(Consts.YORN_STR.yes.getVal()))depot.save();
        else {renderFailJSON("出库数量超出范围");return;}
        renderSuccessJSON("新增操作成功。");
    }

    @Before({DepotValidator.class, Tx.class})
    public void update() {
        Depot depot = getModel(Depot.class, "", true);
        String exe = getPara("exe");
        depot.update();
        String msg = "操作成功";
        switch (exe) {
            case "update":
                msg = "修改操作成功。";
                break;
            case "stop":
                msg = "停用操作成功。";
                break;
            case "active":
                msg = "激活操作成功。";
                break;
            case "del":
                msg = "删除操作成功。";
                break;
            case "out":
                //更新卡数据的，仓库id
                Cards.dao.updateDepotStatusByCardapplyIdAndBnumAndEnum(depot.getCardapplyId(),depot.getBNum(),depot.getENum(),depot.getId().intValue());
                msg="出库操作成功";
                break;
        }
        renderSuccessJSON(msg);

    }

    public void get() {
        Long id = getParaToLong("id");
        renderJson(Depot.dao.findById(id));
    }


    public void dataReady() {
        Map<String, Object> map = new HashMap<>();
        map.put("salesmenList", Salesmen.dao.findAll());
        map.put("cardtypeList", Cardtype.dao.findEnableList());
        renderJson(map);
    }

    public void actRecord(){
        Integer depotId=getParaToInt("depotId");
        renderJson(Unlock.dao.paginate(getPN(),getPS(),"select * ","from b_unlock where depotId=?",depotId));
    }


}
