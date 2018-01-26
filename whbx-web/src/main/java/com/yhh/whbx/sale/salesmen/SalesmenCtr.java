package com.yhh.whbx.sale.salesmen;

import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.sale.model.Salesmen;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 简介       销售人员管理
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.sale.salesmen]
 * 类名称:    [SalesmenCtr]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/9]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class SalesmenCtr extends CoreController {

    public void page() {
        Page<Salesmen> page;
        Kv kv = Kv.create();
        if (isParaExists("type") && getParaToLong("type") != null)
            kv.put("bs.type=", getParaToLong("type"));

        if (isParaExists("level") && getParaToLong("level") != null)
            kv.put("bs.level=", getParaToLong("level"));

        if (isParaExists("status") && StrUtil.isNotBlank(getPara("status")))
            kv.put("bs.status=", getPara("status"));


        if (!isParaBlank("search"))
            kv.put("search", getPara("search"));

        SqlPara sqlPara = Db.getSqlPara("salesmen.findList", Kv.by("cond", kv));

        page = Salesmen.dao.paginate(getPN(), getPS(), sqlPara);

        renderJson(page);

    }

    public void list(){
        String name=getPara("name");

        if(StrUtil.isNotBlank(name)){
            renderJson(Salesmen.dao.findByNameLike("%"+name+"%"));
            return;
        }else{
            renderJson(Salesmen.dao.findAll());
            return ;
        }
    }
    @Before({SalesmenValidator.class, Tx.class})
    public void save(){
        Salesmen salesmen=getModel(Salesmen.class,"",true);
        salesmen.setCAt(new Date());
        salesmen.setStatus(Consts.STATUS.enable.getVal());
        salesmen.setOper(currUser()==null?null:Integer.parseInt(currUser().getId().toString()));
        salesmen.save();
        renderSuccessJSON("新增业务员成功。");
    }
    @Before({SalesmenValidator.class, Tx.class})
    public void update(){
        Salesmen salesmen=getModel(Salesmen.class,"",true);
        salesmen.setOper(currUser()==null?null:Integer.parseInt(currUser().getId().toString()));
        salesmen.update();
        renderSuccessJSON("修改业务员信息成功");
    }

    public void del(){
        Long id=getParaToLong("id");
        Salesmen salesmen=Salesmen.dao.findById(id);
        salesmen.setDAt(new Date());
        salesmen.setOper(currUser()==null?null:Integer.parseInt(currUser().getId().toString()));
        salesmen.update();
        renderSuccessJSON("删除业务员信息成功");
    }

    public void get(){
        Long id=getParaToLong("id");
        renderJson(Salesmen.dao.findById(id));
    }

    public void updateStatus(){
        Long id=getParaToLong("id");
        String status=getPara("status");
        Salesmen salesmen=Salesmen.dao.findById(id);
        salesmen.setStatus(status);
        salesmen.update();
        renderSuccessJSON(status.equals("0")?"业务员信息激活成功":"业务员信息停用成功");
    }

    public void dataReady(){
        Map<String,Object> map=new HashMap<>();
        map.put("saleTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"saleTypeList"));
        map.put("saleLevelList",CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"saleLevelList"));
        renderJson(map);
    }

}
