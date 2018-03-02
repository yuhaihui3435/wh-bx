package com.yhh.whbx.card.type;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.qiniu.common.QiniuException;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Attachment;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.interceptors.AdminAAuthInterceptor;
import com.yhh.whbx.kits.DateKit;
import com.yhh.whbx.kits.QiNiuKit;

import java.util.*;

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

        if (!isParaBlank("category") )
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
        List<UploadFile> list=getFiles();
        String savePath=getPara("sp");
        if(StrUtil.isBlank(savePath))
            savePath="/cmn/files/";

        Cardtype cardtype=getModel(Cardtype.class,"",true);
        cardtype.setCAt(new Date());
        cardtype.setStatus(Consts.STATUS.enable.getVal());
        cardtype.setOper(currUser()==null?null:currUser().getId().intValue());
        cardtype.saveWithoutClean();

        String serverUrl= CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"qn_url");
        String filename=null;
        List<Attachment> attachments=new ArrayList<>();
        Attachment attachment=null;
        for (UploadFile uploadFile:list){
            filename= DateKit.dateToStr(new Date(),DateKit.yyyyMMdd)+"/"+ cardtype.getName()+"_"+uploadFile.getOriginalFileName();
            try {
                QiNiuKit.upload(uploadFile.getFile(),savePath+filename);
            } catch (QiniuException e) {
                LogKit.error("上传文件失败>>"+e.getMessage());
                renderFailJSON("文件上传失败");
                return;
            }

            attachment=new Attachment();
            attachment.setCAt(new Date());
            attachment.setModule("cardtype");
            attachment.setName(uploadFile.getOriginalFileName());
            attachment.setObjId(cardtype.getId().intValue());
            attachment.setPath(savePath+filename);
            attachment.save();
        }

        renderSuccessJSON("新增卡类型成功。");
    }
    @Before({CardtypeValidator.class, Tx.class})
    public void update(){
        List<UploadFile> list=getFiles();
        String savePath=getPara("sp");
        if(StrUtil.isBlank(savePath))
            savePath="/cmn/files/";
        Cardtype cardtype=getModel(Cardtype.class,"",true);
        cardtype.setOper(currUser()==null?null:currUser().getId().intValue());
        cardtype.updateWithoutClean();
        String serverUrl= CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"qn_url");
        String filename=null;
        List<Attachment> attachments=new ArrayList<>();
        Attachment attachment=null;
        for (UploadFile uploadFile:list){
            filename= DateKit.dateToStr(new Date(),DateKit.yyyyMMdd)+"/"+ cardtype.getName()+"_"+uploadFile.getFileName();
            try {
                QiNiuKit.upload(uploadFile.getFile(),savePath+filename);
            } catch (QiniuException e) {
                LogKit.error("上传文件失败>>"+e.getMessage());
                renderFailJSON("文件上传失败");
                return;
            }

            attachment=new Attachment();
            attachment.setCAt(new Date());
            attachment.setModule("cardtype");
            attachment.setName(filename);
            attachment.setObjId(cardtype.getId().intValue());
            attachment.setPath(savePath+filename);
            attachment.save();
        }
        renderSuccessJSON("修改卡类型信息成功");
    }
    @Before({ Tx.class})
    public void del(){
        Long id=getParaToLong("id");
        Cardtype Cardtype= com.yhh.whbx.card.model.Cardtype.dao.findById(id);
        Cardtype.setDAt(new Date());
        Cardtype.setOper(currUser()==null?null:currUser().getId().intValue());
        Cardtype.update();
        renderSuccessJSON("删除卡类型信息成功");
    }
    @Before({ Tx.class})
    public void delFile(){
        Long fId=getParaToLong("fileId");
        Attachment attachment=Attachment.dao.findById(fId);
        attachment.setDAt(new Date());
//        try {
//            QiNiuKit.del(attachment.getPath());
//        } catch (QiniuException e) {
//            LogKit.error("删除文件>>"+attachment.getPath()+"失败");
//            renderFailJSON("文件删除失败，请稍后重试");
//        }
        attachment.update();
        renderSuccessJSON("删除文件成功");

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
    @Clear(AdminAAuthInterceptor.class)
    public void dataReady(){
        Map<String,Object> map=new HashMap<>();
        map.put("cttList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"cttList"));
        map.put("ioList",CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"ioList"));
        renderJson(map);
    }
}
