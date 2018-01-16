package com.yhh.whbx.card.query;

import com.jfinal.aop.Before;
import com.jfinal.aop.Duang;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.qiniu.common.QiniuException;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.poi.excel.ExcelUtil;
import com.xiaoleilu.hutool.poi.excel.ExcelWriter;
import com.xiaoleilu.hutool.util.MapUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Attachment;
import com.yhh.whbx.card.CardsService;
import com.yhh.whbx.card.model.Cards;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.kits.DateKit;
import com.yhh.whbx.kits.QiNiuKit;
import com.yhh.whbx.sale.model.Salesmen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by yuhaihui8913 on 2017/12/20.
 */
public class CardsCtr extends CoreController {

    private final static CardsService cardsService = Duang.duang(CardsService.class);

    public void page() {
        Page<Cards> page;


        SqlPara sqlPara = Db.getSqlPara("cards.findPage", Kv.by("cond", buildQueryCondition()));
        page = Cards.dao.paginate(getPN(), getPS(), sqlPara);
        renderJson(page);
    }

    public void dataReady() {
        Map<String, Object> map = new HashMap<>();
        map.put("salesmenList", Salesmen.dao.findAll());
        map.put("cardtypeList", Cardtype.dao.findEnableList());
        renderJson(map);
    }

    @Before({Tx.class})
    public void lock() {
        //将card状态更新到锁定状态
        int cardsId = getParaToInt("cardsId");
        Cards cards = Cards.dao.findById(cardsId);
        cards.setActAt(null);
        cards.setAct(Consts.YORN_STR.no.getVal());
        cards.setIsLocked(Consts.YORN_STR.no.getVal());
        cards.update();

        //删除掉激活录入的数据
        Cardtype cardtype = cardsService.getCardtypeByCardcode(cards.getCode());
        cardsService.cleanActData(cardtype.getTypeVal(), cardsId);

        renderSuccessJSON("卡锁定成功，激活数据被清除");
    }

    @Before({Tx.class})
    public void unlock() {
        int cardsId = getParaToInt("cardsId");
        Cards cards = Cards.dao.findById(cardsId);
        cards.setIsLocked(Consts.YORN_STR.yes.getVal());
        cards.update();

        renderSuccessJSON("卡解锁成功");

    }

    @Before({Tx.class})
    public void unAct() {
        int cardsId = getParaToInt("cardsId");
        Cards cards = Cards.dao.findById(cardsId);
        cards.setActAt(null);
        cards.setAct(Consts.YORN_STR.no.getVal());
        cards.update();

        //删除掉激活录入的数据
        Cardtype cardtype = cardsService.getCardtypeByCardcode(cards.getCode());
        cardsService.cleanActData(cardtype.getTypeVal(), cardsId);

        renderSuccessJSON("卡激活撤销成功");

    }


    @Before({Tx.class})
    public void exportActCards() throws IOException {
        String ids = getPara("ids");
        Kv kv = null;
        List<Cards> list = null;
        SqlPara sqlPara = null;
        List<Map> list1 = null;
        if (StrUtil.isNotBlank(ids)) {
            kv = Kv.by("ids", ids);
            sqlPara = Db.getSqlPara("cards.findListByIds", kv);
            list = Cards.dao.find(sqlPara);

        } else {
            sqlPara = Db.getSqlPara("cards.findPage", Kv.by("cond", buildQueryCondition()));
            list = Cards.dao.find(sqlPara);
        }

        if (!list.isEmpty()) list1 = new ArrayList<>();

        long exportCode = Math.abs(DateUtil.current(true));

        for (Cards cards : list) {
            cards.setExportAt(new Date());
            cards.setExportCode(exportCode + "");
            cardsService.assemblePolicy(cards, list1);
        }


        ExcelWriter writer = ExcelUtil.getWriter();
        //自定义标题别名
        Map<String, String> alias = MapUtil.newHashMap();
        alias.put("cardsCode", "卡号");
        alias.put("phName", "投保人姓名");
        alias.put("phCertType", "投保人证件类型");
        alias.put("phIdNum", "投保人证件号");
        alias.put("phBirth", "投保人生日");
        alias.put("phSex", "投保人性别");
        alias.put("phTel", "投保人联系电话");
        alias.put("phEmail", "投保人EMAIL");
        alias.put("relationship", "投保人与被投保人关系");
        alias.put("ipName", "被投保人姓名");
        alias.put("ipCertType", "被投保人证件类型");
        alias.put("ipIdNum", "被投保人证件号");
        alias.put("ipBirth", "被投保人生日");
        alias.put("ipSex", "被投保人性别");
        alias.put("ipTel", "被投保人联系电话");
        alias.put("ipJob", "被投保人工作");
        writer.setHeaderAlias(alias);


        writer.write(list1);
        getResponse().setContentType("application/x-msdownload");
        getResponse().setHeader("Content-Disposition",
                "attachment;filename=\"" + URLEncoder.encode(exportCode + "", "UTF-8") + ".xls\"");
        OutputStream out = getResponse().getOutputStream();
        writer.flush(out);
        writer.close();
        out.flush();
        out.close();
        renderNull();
    }

    @Before({Tx.class})
    public void exportActCarCards() throws IOException {
        String ids = getPara("ids");
        Kv kv = null;
        List<Cards> list = null;
        SqlPara sqlPara = null;
        List<Map> list1 = null;
        if (StrUtil.isNotBlank(ids)) {
            kv = Kv.by("ids", ids);
            sqlPara = Db.getSqlPara("cards.findListByIds", kv);
            list = Cards.dao.find(sqlPara);

        } else {
            sqlPara = Db.getSqlPara("cards.findPage", Kv.by("cond", buildQueryCondition()));
            list = Cards.dao.find(sqlPara);
        }

        if (!list.isEmpty()) list1 = new ArrayList<>();

        long exportCode = Math.abs(DateUtil.current(true));

        for (Cards cards : list) {
            cards.setExportAt(new Date());
            cards.setExportCode(exportCode + "");
            cardsService.assemblePolicy(cards, list1);
        }

        ExcelWriter writer = ExcelUtil.getWriter();
        //自定义标题别名
        Map<String, String> alias = MapUtil.newHashMap();
        alias.put("cardsCode", "卡号");
        alias.put("cphType", "投保人类型");
        alias.put("cphName", "投保人姓名");
        alias.put("cphCertType", "投保人证件类型");
        alias.put("cphIdNum", "投保人证件号");
        alias.put("cphTel", "投保人联系电话");
        alias.put("cphEmail", "投保人EMAIL");
        alias.put("cphLinkName", "投保联系人姓名");
        alias.put("cphAddress", "投保人地址");
        alias.put("cipPlateNum", "被投保车车牌号");
        alias.put("cipEngineNum", "被投保车发动机号");
        alias.put("cipFrameNum", "被投保车大架号");
        alias.put("cipProp", "被投保车营运类型");
        alias.put("cipSeatCount", "被投保车座位数");
        alias.put("cipType", "被投保车类型");
        writer.setHeaderAlias(alias);
        writer.write(list1);
        getResponse().setContentType("application/x-msdownload");
        getResponse().setHeader("Content-Disposition",
                "attachment;filename=\"" + URLEncoder.encode(exportCode + "", "UTF-8") + ".xls\"");
        OutputStream out = getResponse().getOutputStream();
        writer.flush(out);
        writer.close();
        out.flush();
        out.close();
        renderNull();
    }

    /**
     * 上传保单
     */
    public void uploadPolicy() {
        UploadFile uploadFile = getFile();
        int cardsId = getParaToInt("cardsId");
        int cardsCode = getParaToInt("cardsCode");
        if (uploadFile != null) {
            String savePath = getPara("sp");
            if (StrUtil.isBlank(savePath))
                savePath = "/cmn/files/";
            String serverUrl = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url");
            String filename = null;
            Attachment attachment = null;
            filename = DateKit.dateToStr(new Date(), DateKit.yyyyMMdd) + "/" + cardsCode + "_" + uploadFile.getOriginalFileName();
            try {
                QiNiuKit.upload(uploadFile.getFile(), savePath + filename);
            } catch (QiniuException e) {
                LogKit.error("上传文件失败>>" + e.getMessage());
                renderFailJSON("文件上传失败");
                return;
            }
            attachment = new Attachment();
            attachment.setCAt(new Date());
            attachment.setModule("cards");
            attachment.setName(uploadFile.getOriginalFileName());
            attachment.setObjId(cardsId);
            attachment.setPath(savePath + filename);
            attachment.save();
            renderSuccessJSON("上传电子保单成功");
        }


    }

    /**
     * 装配 查询条件
     *
     * @return
     */
    private Kv buildQueryCondition() {
        Kv kv = Kv.create();
        if (!isParaBlank("code"))
            kv.put("c.code=", getPara("code"));
        if (!isParaBlank("bNum"))
            kv.put("d.bNum=", getPara("bNum"));
        if (!isParaBlank("eNum"))
            kv.put("d.eNum=", getParaToLong("eNum"));
        if (!isParaBlank("faceVal"))
            kv.put("ct.faceVal=", getPara("faceVal"));
        if (!isParaBlank("status"))
            kv.put("c.status=", getPara("status"));
        if (!isParaBlank("cardtypeId"))
            kv.put("ct.id=", getPara("cardtypeId"));
        if (!isParaBlank("cardapplyId"))
            kv.put("ca.id=", getPara("cardapplyId"));
        if (!isParaBlank("actStatus"))
            kv.put("c.act=", getPara("actStatus"));
        if (!isParaBlank("lockStatus"))
            kv.put("c.isLocked=", getPara("lockStatus"));
        if (!isParaBlank("outStatus"))
            kv.put("d.outStatus=", getPara("outStatus"));
        if (!isParaBlank("salesmenId"))
            kv.put("d.salesmenId=", getPara("salesmenId"));
        if (!isParaBlank("exportStatus"))
            kv.put("c.exportCode", getPara("exportStatus"));
        if (!isParaBlank("exportCode"))
            kv.put("c.exportCode=", getPara("exportCode"));
        if (!isParaBlank("policyNum"))
            kv.put("c.policyNum=", getPara("policyNum"));
        if (!isParaBlank("phIdNum"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType")) && getPara("cardtypeType").equals("407")) ? "ph.idCard=" : "cph.idCard=", getPara("phIdNum"));
        if (!isParaBlank("phName"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType")) && getPara("cardtypeType").equals("407")) ? "ph.name=" : "cph.name=", getPara("phName"));
        if (!isParaBlank("ipIdNum"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType")) && getPara("cardtypeType").equals("407")) ? "ip.idCard=" : "cip.idCard=", getPara("ipIdNum"));
        if (!isParaBlank("ipName"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType")) && getPara("cardtypeType").equals("407")) ? "ip.name=" : "cip.name=", getPara("ipName"));
        if (!isParaBlank("cardtypeType"))
            kv.put("ct.type=", getPara("cardtypeType"));
        if (!isParaBlank("actTime")) {
            String[] array = StrUtil.split(getPara("actTime"), " - ");
            kv.put("c.actAt>=", StrUtil.trimEnd(array[0]));
            kv.put("c.actAt<", StrUtil.trimEnd(array[1]));
        }

        return kv;
    }


}
