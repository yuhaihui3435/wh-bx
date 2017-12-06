package com.yhh.whbx.admin.art;


import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.jfinal.upload.UploadFile;
import com.yhh.whbx.admin.model.Content;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.interceptors.UserInterceptor;
import com.yhh.whbx.kits.QiNiuKit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.admin.content]
 * 类名称:    [ArtCtr]
 * 类描述:    [内容管理]
 * 创建人:    [于海慧]
 * 创建时间:  [2017/1/3]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
@Before(UserInterceptor.class)
public class ArtCtr extends CoreController {


    public void list() {
        Page<Content> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from s_content c left join s_user u on c.user_id=u.id where 1=1 and c.dAt is null ");
        if (!isParaBlank("search")) {
            where.append(" and (instr(c.title,?)>0 or instr(c.text,?)>0 or instr(c.summary,?)>0 or instr(c.metaKeywords,?)>0 or instr(u.nickname,?)>0)");
            where.append(" order by c.c_at desc");
            page = Content.dao.paginate(getPN(), getPS(), "select c.* ", where.toString(), serach, serach, serach, serach, serach);
        } else {
            where.append(" order by c.c_at desc");
            page = Content.dao.paginate(getPN(), getPS(), "select c.* ", where.toString());
        }
        renderJson(page);
    }

    @Before({Tx.class})
    public void saveOrUpdate() {

        UploadFile uf = getFile();
        String pName, fileName = null;
        if(uf!=null) {
            pName = uf.getParameterName();
            fileName = "images/" + System.currentTimeMillis() + uf.getFileName();
            if (!isParaBlank(pName + "_bak")) {
                QiNiuKit.del(getPara(pName + "_bak"));
            }
            QiNiuKit.upload(uf.getFile(), fileName);
        }else{
            fileName=getPara("thumbnail" + "_bak");
        }

        renderSuccessJSON("保存成功","");

    }

    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void setTop(){
        int id=getParaToInt("content.id");
        Content c=Content.dao.findById(id);
        c.setTop(false);
        c.update();
        renderSuccessJSON("置顶设置成功","");
    }
    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void cancelTop(){
        int id=getParaToInt("content.id");
        Content c=Content.dao.findById(id);
        c.setTop(true);
        c.update();
        renderSuccessJSON("置顶取消设置成功","");
    }
    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void setGood(){
        int id=getParaToInt("content.id");
        Content c=Content.dao.findById(id);
        c.setGood(false);
        c.update();
        renderSuccessJSON("精华设置成功","");
    }
    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void cancelGood(){
        int id=getParaToInt("content.id");
        Content c=Content.dao.findById(id);
        c.setGood(true);
        c.update();
        renderSuccessJSON("精华取消设置成功","");
    }
    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void del(){

        String ids=getPara("ids");
        List<Integer> idsList=new ArrayList<Integer>();
        if(ids==null){
            renderFailJSON("没有找到要删除的文章","");
            return ;
        }else{
            String[] array=ids.split(",");
            for(String s:array){
                idsList.add(Integer.parseInt(s));
            }
        }


        for(Integer i:idsList){
            Content c=Content.dao.findById(i);
            c.setDAt(new Date());
            c.update();
        }
        renderSuccessJSON("删除成功","");

    }

    public void view(){
        int id=getParaToInt("id");
        Content c=Content.dao.findById(id);

        if(c==null||c.getDAt()!=null){
            renderError(404);
            return ;
        }
        setAttr("content",c);
        render("/admin/content/view.html");
    }


    public void get(){
        int id=getParaToInt("id");
        renderJson(Content.dao.findById(id));
        //renderCaptcha();
    }


}
