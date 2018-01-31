package com.yhh.whbx.card.report;

import com.jfinal.aop.Clear;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.interceptors.AdminAAuthInterceptor;
import com.yhh.whbx.kits.DateKit;
import com.yhh.whbx.sale.model.Salesmen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            kv.put("bDate", DateKit.getTimeStampBegin(DateUtil.parseDate(array[0])));
            kv.put("eDate",DateKit.getTimeStampEnd(DateUtil.parseDate(array[1])));
        }else{
            renderFailJSON("查询时间段必填");
            return;
        }
        if(!isParaBlank("ctId")){
            kv.put("ctId",getPara("ctId"));
        }

        SqlPara sqlPara=Db.getSqlPara("cards.ds00",kv);
        Page page=Db.paginate(getPN(),200,sqlPara);
        renderJson(page);
    }


    /**
     * 卡销售量统计
     */
    public void ds01(){
        Kv kv= Kv.create();
        if(!isParaBlank("smId")){
            kv.put("smId",getPara("smId"));
        }
        if (!isParaBlank("sTime")) {
            String[] array = StrUtil.split(getPara("sTime"), " - ");
            kv.put("bDate", DateKit.getTimeStampBegin(DateUtil.parseDate(array[0])));
            kv.put("eDate",DateKit.getTimeStampEnd(DateUtil.parseDate(array[1])));
        }else{
            renderFailJSON("查询时间段必填");
            return;
        }
        SqlPara sqlPara=Db.getSqlPara("cards.ds01",kv);
        Page page=Db.paginate(getPN(),200,sqlPara);
        renderJson(page);
    }

    /**
     * 卡销售量统计，个人详细
     */
    public void ds0100(){
        Kv kv= Kv.create();
        if(!isParaBlank("smId")){
            kv.put("smId",getPara("smId"));
        }else{
            renderFailJSON("销售员必填");
            return;
        }
        if (!isParaBlank("sTime")) {
            String[] array = StrUtil.split(getPara("sTime"), " - ");
            kv.put("bDate", DateKit.getTimeStampBegin(DateUtil.parseDate(array[0])));
            kv.put("eDate",DateKit.getTimeStampEnd(DateUtil.parseDate(array[1])));
        }else{
            renderFailJSON("查询时间段必填");
            return;
        }
        SqlPara sqlPara=Db.getSqlPara("cards.ds0100",kv);
        List list=Db.find(sqlPara);
        renderJson(list);
    }

    @Clear(AdminAAuthInterceptor.class)
    public void dataReady(){
        Map<String, Object> map = new HashMap<>();
        map.put("salesmenList", Salesmen.dao.findAll());
        map.put("cardtypeList", Cardtype.dao.findEnableList());
        renderJson(map);
    }

}
