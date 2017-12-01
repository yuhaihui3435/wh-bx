package com.yhh.whbx.admin.param;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.yhh.whbx.admin.model.Param;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreData;
import com.yhh.whbx.kits.QiNiuKit;

import java.util.Enumeration;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/1.
 */
public class ParamCtr extends CoreController {

    /**
     * 查询系统设置参数
     */
    public void getSettingJSON() {
        List<Param> paramList = Param.dao.find("select * from " + Param.TABLE);
        renderJson(paramList);
    }

    /**
     * 系统参数设置
     *
     */
    @Before(Tx.class)
    public void save() {
        List<UploadFile> fileList = getFiles();
        String pName, fileName = null;
        Param param = null;
        for (UploadFile uf : fileList) {
            pName = uf.getParameterName();

            fileName = "images/" + System.currentTimeMillis() + uf.getFileName();
            if(!isParaBlank(pName+"_bak")){
                QiNiuKit.del(getPara(pName+"_bak"));
            }
            QiNiuKit.upload(uf.getFile(), fileName);
            param = Param.dao.findByKey(pName);
            if (param == null) {
                param = new Param();
                param.setK(pName);
                param.setVal(fileName);
                param.setNote(uf.getFile().length()+"");
                param.save();
            } else {
                param.setVal(fileName);
                param.setNote(uf.getFile().length()+"");
                param.update();
            }
        }
        Enumeration<String> stringEnumeration = getParaNames();
        String key;
        String val = null;
        while (stringEnumeration.hasMoreElements()) {
            key = stringEnumeration.nextElement();
            if(key.contains("_bak"))continue;
            param = Param.dao.findByKey(key);
            val = getPara(key);
            if (isParaExists(key + "_bak")) {
                if (!isParaBlank(key + "_bak") && isParaBlank(key)) {
                    val = getPara(key + "_bak");
                }
            }
            if (param == null) {
                param = new Param();
                param.setK(key);
                param.setVal(val);
                param.save();
            } else {
                param.setVal(val);
                param.update();
            }

        }
        CoreData.loadParam();
        renderSuccessJSON("设置成功", "");
    }
}
