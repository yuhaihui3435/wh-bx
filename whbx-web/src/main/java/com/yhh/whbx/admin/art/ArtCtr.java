package com.yhh.whbx.admin.art;


import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.jfinal.upload.UploadFile;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.admin.model.Content;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreException;
import com.yhh.whbx.interceptors.UserInterceptor;
import com.yhh.whbx.kits.DateKit;
import com.yhh.whbx.kits.QiNiuKit;
import com.yhh.whbx.kits._StrKit;

import java.io.IOException;
import java.math.BigInteger;
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
        StringBuffer where = new StringBuffer("from s_content c left join s_user u on c.userId=u.id where 1=1 and c.dAt is null ");
        if (!isParaBlank("search")) {
            where.append(" and (instr(c.title,?)>0 or instr(c.text,?)>0 or instr(c.summary,?)>0 or instr(c.metaKeywords,?)>0 or instr(u.nickname,?)>0)");
            where.append(" order by c.cAt desc");
            page = Content.dao.paginate(getPN(), getPS(), "select c.* ", where.toString(), serach, serach, serach, serach, serach);
        } else {
            where.append(" order by c.cAt desc");
            page = Content.dao.paginate(getPN(), getPS(), "select c.* ", where.toString());
        }
        renderJson(page);
    }

    @Before({Tx.class})
    public void save() {
        Content content=getModel(Content.class,"",true);
        String base64Str=getPara("thumbnailBase64");
        if(StrUtil.isNotBlank(base64Str)){
            String imgKey= "/art/thumbnail/"+ DateKit.dateToStr(new Date(),DateKit.yyyyMMdd)+"/"+ _StrKit.getUUID()+".jpg";
            String qnRs=null;
            try {
                qnRs=QiNiuKit.put64image(base64Str,imgKey);
            } catch (IOException e) {
                LogKit.error("七牛上传base64图片失败:"+e.getMessage());
                throw new CoreException("上传图片到服务器失败>>"+e.getMessage());
            }
            if(qnRs==null) {
                renderFailJSON("图片上传失败", "");
                return ;
            }else{
                if(qnRs.indexOf("200")>-1){
                    content.setThumbnail(imgKey);
                }else{
                    LogKit.error("七牛base64上传失败:"+qnRs);
                    renderFailJSON("图片上传失败", "");
                    return ;
                }
            }
        }
        content.setUserid(currUser().getId()!=null?new BigInteger(currUser().getId()):null);
        content.save();
        renderSuccessJSON("保存成功","");
    }

    @Before({Tx.class})
    public void update() {

        renderSuccessJSON("保存成功","");

    }

    @Before({Tx.class})
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





}
