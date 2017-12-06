package com.yhh.whbx.admin.taxonomy;

import com.alibaba.fastjson.JSON;
import com.xiaoleilu.hutool.log.StaticLog;
import com.yhh.whbx.admin.model.Taxonomy;
import com.yhh.whbx.core.CoreController;

import java.util.Date;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/6.
 */
public class TaxCtr extends CoreController {

    public void treeJsonArray(){
        List list=Taxonomy.dao.findAllList();
        StaticLog.info(JSON.toJSONString(list));
        renderJson(list);
    }

    public void save(){
        Taxonomy taxonomy=getModel(Taxonomy.class,"",true);
        taxonomy.save();
        renderSuccessJSON("新增分类成功");
    }

    public void update(){
        Taxonomy taxonomy=getModel(Taxonomy.class,"",true);
        taxonomy.update();
        renderSuccessJSON("更新分类成功");
    }

    public void del(){
        Long id=getParaToLong("id");
        Taxonomy taxonomy=Taxonomy.dao.findById(id);
        taxonomy.setDAt(new Date());
        taxonomy.update();
        renderSuccessJSON("删除分类成功");
    }

}
