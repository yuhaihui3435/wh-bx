package com.yhh.whbx.card.type;

import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;

import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card.type]
 * 类名称:    [CardTypeCtr]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/10]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class CardTypeCtr extends CoreController {

    public void page() {
        Page<Cardtype> page;
        Kv kv = Kv.create();
        if (!isParaBlank("type"))
            kv.put("type=", getParaToLong("type"));

        if (!isParaBlank("level") )
            kv.put("category=", getParaToLong("category"));

        if (!isParaBlank("status") )
            kv.put("status=", getPara("status"));


        if (!isParaBlank("code"))
            kv.put("code=", getPara("code"));
        if (!isParaBlank("name"))
            kv.put("name like", "%"+getPara("name")+"%");

        SqlPara sqlPara = Db.getSqlPara("cardType.findPage", Kv.by("cond", kv));

        page = Cardtype.dao.paginate(getPN(), getPS(), sqlPara);

        renderJson(page);

    }

    public void list(){
        renderJson(Cardtype.dao.findAll());
    }
    @Before({CardtypeValidator.class, Tx.class})
    public void save(){
        Cardtype Cardtype=getModel(Cardtype.class,"",true);
        Cardtype.setCAt(new Date());
        Cardtype.setStatus(Consts.STATUS.enable.getVal());
        Cardtype.setOper(currUser()==null?null:Integer.parseInt(currUser().getId()));
        Cardtype.save();
        renderSuccessJSON("新增卡类型成功。");
    }
    @Before({CardtypeValidator.class, Tx.class})
    public void update(){
        Cardtype Cardtype=getModel(Cardtype.class,"",true);
        Cardtype.setOper(currUser()==null?null:Integer.parseInt(currUser().getId()));
        Cardtype.update();
        renderSuccessJSON("修改卡类型信息成功");
    }

    public void del(){
        Long id=getParaToLong("id");
        Cardtype Cardtype= com.yhh.whbx.card.model.Cardtype.dao.findById(id);
        Cardtype.setDAt(new Date());
        Cardtype.setOper(currUser()==null?null:Integer.parseInt(currUser().getId()));
        Cardtype.update();
        renderSuccessJSON("删除卡类型信息成功");
    }

    public void get(){
        Long id=getParaToLong("id");
        renderJson(Cardtype.dao.findById(id));
    }

    public void updateStatus(){
        Long id=getParaToLong("id");
        String status=getPara("status");
        Cardtype Cardtype= com.yhh.whbx.card.model.Cardtype.dao.findById(id);
        Cardtype.setStatus(status);
        Cardtype.update();
        renderSuccessJSON(status.equals("0")?"卡类型信息激活成功":"卡类型信息停用成功");
    }

    public void dataReady(){
        Map<String,Object> map=new HashMap<>();
        map.put("cttList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"cttList"));
        map.put("ioList",CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"ioList"));
        renderJson(map);
    }
}
