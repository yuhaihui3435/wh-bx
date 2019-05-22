package com.yhh.whbx.card.query;

import com.aliyuncs.exceptions.ClientException;
import com.jfinal.aop.Before;
import com.jfinal.aop.Duang;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.qiniu.common.QiniuException;
import com.xiaoleilu.hutool.date.DateException;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.log.StaticLog;
import com.xiaoleilu.hutool.poi.excel.ExcelReader;
import com.xiaoleilu.hutool.poi.excel.ExcelUtil;
import com.xiaoleilu.hutool.poi.excel.ExcelWriter;
import com.xiaoleilu.hutool.util.MapUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Attachment;
import com.yhh.whbx.admin.model.Taxonomy;
import com.yhh.whbx.card.CardsService;
import com.yhh.whbx.card.model.*;
import com.yhh.whbx.card.type.CardTypeService;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreException;
import com.yhh.whbx.kits.ALSMSKit;
import com.yhh.whbx.kits.DateKit;
import com.yhh.whbx.kits.QiNiuKit;
import com.yhh.whbx.kits.ResKit;
import com.yhh.whbx.sale.model.Salesmen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by yuhaihui8913 on 2017/12/20.
 */
public class CardsCtr extends CoreController {

    private final static CardsService cardsService = Duang.duang(CardsService.class);
    private final static CardTypeService cardTypeService = Duang.duang(CardTypeService.class);

    public void page() {
        Page<Cards> page;
        SqlPara sqlPara = Db.getSqlPara("cards.findPage", Kv.by("cond", buildQueryCondition()));
        page = Cards.dao.paginate(getPN(), getPS(), sqlPara);
        renderJson(page);
    }

    public void actPage() {
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
//        try {
        String ids = getPara("ids");
        Kv kv = null;
        List<Cards> list = null;
        Cards _cards = null;
        SqlPara sqlPara = null;
        List<Map> list1 = null;
        if (StrUtil.isNotBlank(ids)) {
            kv = Kv.by("ids", ids.split(","));
            sqlPara = Db.getSqlPara("cards.findListByIds", kv);
            list = Cards.dao.find(sqlPara);

        } else {
            sqlPara = Db.getSqlPara("cards.findList", Kv.by("cond", buildQueryCondition()));
            list = Cards.dao.find(sqlPara);
        }

        if (!list.isEmpty()) list1 = new ArrayList<>();
        else {
            renderFailJSON("没有查询到符合导出条件的数据");
            return;
        }

        long exportCode = Math.abs(DateUtil.current(true));

        boolean sendSMS=true;


        for (Cards cards : list) {
            if(StrUtil.isNotBlank(cards.getExportCode())){
                sendSMS=false;
            }
            cardsService.assemblePolicy(cards, list1);
            _cards = Cards.dao.findById(cards.getId());
            _cards.setExportAt(new Date());
            _cards.setExportCode(exportCode + "");
            _cards.update();
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
        alias.put("policyNum", "保单号");
        alias.put("reportTel", "报案电话");
        alias.put("bAt", "保险开始时间");
        alias.put("eAt", "保险终止时间");
        alias.put("salesmenName","紧急联系人");
        writer.setHeaderAlias(alias);
        writer.write(list1);
//            getResponse().setContentType("application/x-msdownload");
//            getResponse().setHeader("Content-Disposition",
//                    "attachment;filename=\"" + URLEncoder.encode(exportCode + "", "UTF-8") + ".xls\"");
//            OutputStream out = getResponse().getOutputStream();
        String fileName = exportCode + ".xls";
        File file = FileUtil.file(PathKit.getWebRootPath() + "/WEB-INF/tmp/" + fileName);
        OutputStream out = new FileOutputStream(file);
        writer.flush(out);
        writer.close();
        out.flush();
        out.close();
        QiNiuKit.upload(file, "/cards/export/" + fileName);
        file.delete();

        if(sendSMS) {
            LogKit.info("发送激活短信通知");
            for (int i = 0; i < list1.size(); i++) {
                Map<String, String> map = list1.get(i);
                try {
                    String ret = ALSMSKit.sendSMS(ResKit.getConfig("sms.anbao.card.act.signname"), ResKit.getConfig("sms.anbao.card.act.templatecode"), map.get("phTel"), "{\"code\":\"" + map.get("cardsCode") + "\",\"person\":\"" + map.get("phName") + "\"}");
                    if (!ret.equals(Consts.YORN_STR.yes.name()))
                        LogKit.error("卡激活短信通知失败");

                } catch (ClientException e) {
                    LogKit.error("卡激活短信通知失败>>" + e.getMessage());
                }
            }
        }


        renderSuccessJSON("卡激活数据导出成功", (String) CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url") + "/cards/export/" + fileName);
//        } catch (Exception e) {
//            LogKit.error("卡激活数据导出失败>>" + e.getMessage());
//            renderFailJSON("卡激活数据导出失败");
//        }
    }

    @Before({Tx.class})
    public void exportActCarCards() throws IOException {
//        try {
        String ids = getPara("ids");
        Kv kv = null;
        List<Cards> list = null;
        SqlPara sqlPara = null;
        Cards _cards = null;
        List<Map> list1 = null;
        if (StrUtil.isNotBlank(ids)) {
            kv = Kv.by("ids", ids);
            sqlPara = Db.getSqlPara("cards.findListByIds", kv);
            list = Cards.dao.find(sqlPara);

        } else {
            sqlPara = Db.getSqlPara("cards.findList", Kv.by("cond", buildQueryCondition()));
            list = Cards.dao.find(sqlPara);
        }

        if (!list.isEmpty()) list1 = new ArrayList<>();
        else {
            renderFailJSON("没有查询到符合导出条件的数据");
            return;
        }

        long exportCode = Math.abs(DateUtil.current(true));
        boolean sendSMS=true;
        for (Cards cards : list) {
            if(StrUtil.isNotBlank(cards.getExportCode())){
                sendSMS=false;
            }
            cardsService.assemblePolicy(cards, list1);
            _cards = Cards.dao.findById(cards.getId());
            _cards.setExportAt(new Date());
            _cards.setExportCode(exportCode + "");
            _cards.update();
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
        alias.put("policyNum", "保单号");
        alias.put("reportTel", "报案电话");
        alias.put("bAt", "保险开始时间");
        alias.put("eAt", "保险终止时间");
        alias.put("salesmenName","紧急联系人");
        writer.setHeaderAlias(alias);
        writer.write(list1);
//            getResponse().setContentType("application/x-msdownload");
//            getResponse().setHeader("Content-Disposition",
//                    "attachment;filename=\"" + URLEncoder.encode(exportCode + "", "UTF-8") + ".xls\"");
//            OutputStream out = getResponse().getOutputStream();
        String fileName = exportCode + ".xls";
        File file = FileUtil.file(PathKit.getWebRootPath() + "/WEB-INF/tmp/" + fileName);
        OutputStream out = new FileOutputStream(file);
        writer.flush(out);
        writer.close();
        out.flush();
        out.close();
        QiNiuKit.upload(file, "/cards/export/" + fileName);
        file.delete();
        if(sendSMS) {
            LogKit.info("发送车辆短信通知");
            for (int i = 0; i < list1.size(); i++) {
                Map<String, String> map = list1.get(i);
                try {
                    String ret = ALSMSKit.sendSMS(ResKit.getConfig("sms.anbao.card.act.signname"), ResKit.getConfig("sms.anbao.card.act.templatecode"), map.get("cphTel"), "{\"code\":\"" + map.get("cardsCode") + "\",\"person\":\"" + map.get("cphName") + "\"}");
                    if (!ret.equals(Consts.YORN_STR.yes.name()))
                        LogKit.error("卡激活短信通知失败");

                } catch (ClientException e) {
                    LogKit.error("卡激活短信通知失败>>" + e.getMessage());
                }
            }
        }


        renderSuccessJSON("卡激活数据导出成功", (String) CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url") + "/cards/export/" + fileName);
//        } catch (Exception e) {
//            LogKit.error("卡激活数据导出失败>>" + e.getMessage());
//            renderFailJSON("卡激活数据导出失败");
//        }
    }

    /**
     * 上传保单
     */
    public void uploadPolicy() {
        try {
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
        } catch (Exception e) {
            LogKit.error("上传电子保单失败>>" + e.getMessage());
            renderFailJSON("上传电子保单失败");
        }
    }

    public void importPolicyInfo() {
        try {
            UploadFile uploadFile = getFile();
            List<Map<String, Object>> list = null;
            String code, policyNum, reportTel,bAt,eAt;

            StringBuilder stringBuilder = new StringBuilder();
            String msg = "一共导入{}条数据，失败{}条。";
            int count = 0;
            int fCount = 0;
            Object obj = null;
            ExcelReader excelReader = ExcelUtil.getReader(uploadFile.getFile());


            list = excelReader.readAll();
            count = list.size();
            for (Map map : list) {
                code = (String) map.get("卡号");
                obj = map.get("保单号");
                policyNum = obj == null ? "" : obj.toString();
                obj = map.get("报案电话");
                reportTel = obj == null ? "" : obj.toString();
                obj=map.get("保险开始时间");
                bAt=obj==null?"":obj.toString();
                obj=map.get("保险终止时间");
                eAt=obj==null?"":obj.toString();
                Cards cards = Cards.dao.findFristByPropEQ("code", code);
                if (cards == null) {
                    if (stringBuilder.length() == 0)
                        stringBuilder.append(code);
                    else
                        stringBuilder.append(",").append(code);
                } else {
                    cards.setPolicyNum(policyNum);
                    cards.setReportTel(reportTel);
                    cards.setBAt(bAt);
                    cards.setEAt(eAt);
                    cards.update();
                }
            }

            msg = StrUtil.format(msg, count, fCount);

            if (stringBuilder.length() > 0) {
                msg = msg + "失败的卡号为：" + stringBuilder.toString();
            }

            renderSuccessJSON(msg);
        } catch (Exception e) {
            LogKit.error("保单数据导入失败>>" + e.getMessage());
            renderFailJSON("保单数据导入失败");
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
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType")) && getPara("cardtypeType").equals("407")) ? "ph.idCard=" : "cph.certCode=", getPara("phIdNum"));
        if (!isParaBlank("phTel"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType")) && getPara("cardtypeType").equals("407")) ? "ph.tel=" : "cph.tel=", getPara("phTel"));
        if (!isParaBlank("phName"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType")) && getPara("cardtypeType").equals("407")) ? "ph.name=" : "cph.name=", getPara("phName"));
        if (!isParaBlank("ipIdNum"))
            kv.put((StrUtil.isNotBlank(getPara("cardtypeType")) && getPara("cardtypeType").equals("407")) ? "ip.idCard=" : "cip.plateNum=", getPara("plateNum"));
        if (!isParaBlank("ipName"))
            kv.put("ip.name=", getPara("ipName"));
        if (!isParaBlank("cardtypeType"))
            kv.put("ct.type=", getPara("cardtypeType"));
        if (!isParaBlank("actTime")) {
            String[] array = StrUtil.split(getPara("actTime"), " - ");
            if(StrUtil.isNotBlank(StrUtil.trimEnd(array[0]))) {
                kv.put("c.actAt>=", StrUtil.trimEnd(array[0]));
            }
            if(StrUtil.isNotBlank(StrUtil.trimEnd(array[1]))) {
                kv.put("c.actAt<", StrUtil.trimEnd(array[1]));
            }
        }

        return kv;
    }

    public void get() {
        String code = getPara("cardCode");
        Cards cards = Cards.dao.findByCode(code);
        Map<String, Object> ret = new HashMap<>();
        ret.put("cards", cards);
        List<Attachment> attachments = Attachment.dao.findByObjIdAndModule(cards.getId().intValue(), "cards");
        ret.put("electronicPolicy", attachments);
        renderJson(ret);
    }

    /**
     * 批量激活导入，意外险
     */
    public void batchImportAct00() {
        UploadFile uploadFile = getFile();
        ExcelReader excelReader = ExcelUtil.getReader(uploadFile.getFile());
        StringBuilder ret = new StringBuilder();
        List<Map<String, Object>> list = null;
        list = excelReader.readAll();
        int count = 0;
        int fCount = 0;
        String col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13, col14, col15, col16, col17;
        Object obj = null;
        String msg = "共有{}条数据准备处理，导入过程中出现错误。<br>";
        Cards cards = null;
        CardsPh cardsPh = null;
        CardsIp cardsIp = null;
        StringBuilder stringBuilder = null;
        Taxonomy taxonomy = null;
        Date date = null;
        count = list.size();
        Cardtype cardtype = null;
        InsuranceOcc insuranceOcc = null;
        String ctt = null;
        List<DTO> dtos = new ArrayList<>();
        DTO dto = null;
        for (Map map : list) {
            stringBuilder = new StringBuilder();
            col1 = (String) map.get("卡号");
            obj = map.get("密码");
            col2 = obj == null ? "" : obj.toString();
            obj = map.get("投保人姓名");
            col3 = obj == null ? "" : obj.toString();
            obj = map.get("投保人证件类型");
            col4 = obj == null ? "" : obj.toString();
            obj = map.get("投保人证件号");
            col5 = obj == null ? "" : obj.toString();
            obj = map.get("投保人生日");
            col6 = obj == null ? "" : obj.toString();
            obj = map.get("投保人性别");
            col7 = obj == null ? "" : obj.toString();
            obj = map.get("投保人联系电话");
            col8 = obj == null ? "" : obj.toString();
            obj = map.get("投保人EMAIL");
            col9 = obj == null ? "" : obj.toString();
            obj = map.get("投保人与被投保人关系");
            col10 = obj == null ? "" : obj.toString();
            obj = map.get("被投保人姓名");
            col11 = obj == null ? "" : obj.toString();
            obj = map.get("被投保人证件类型");
            col12 = obj == null ? "" : obj.toString();
            obj = map.get("被投保人证件号");
            col13 = obj == null ? "" : obj.toString();
            obj = map.get("被投保人生日");
            col14 = obj == null ? "" : obj.toString();
            obj = map.get("被投保人性别");
            col15 = obj == null ? "" : obj.toString();
            obj = map.get("被投保人联系电话");
            col16 = obj == null ? "" : obj.toString();
            obj = map.get("被投保人工作");
            col17 = obj == null ? "" : obj.toString();
            try {
                cards = cardsService.checkCard(col1, col2);
                cardtype = cardsService.getCardtypeByCardcode(cards.getCode());
                ctt = cardtype.getTypeVal();
                if (!ctt.equals("accidentInsurance")) {
                    ret.append("卡号>>" + col1 + "类型不匹配，无法进行激活<br>");
                    fCount++;
                    break;
                }
                if (cards.getAct() != null && cards.getAct().equals(Consts.YORN_STR.yes.getVal())) {
                    ret.append("卡号>>" + col1 + "已经被激活<br>");
                    fCount++;
                    break;
                }
            } catch (CoreException ce) {
                stringBuilder.append(">>" + ce.getMsg());
            }
            if (cards != null) {
                cardsPh = new CardsPh();
                if (StrUtil.isBlank(col3)) {
                    stringBuilder.append(">>投保人姓名不能为空");
                } else {
                    cardsPh.setName(col3);
                }
                if (StrUtil.isBlank(col4)) {
                    stringBuilder.append(">>投保人证件类型不能为空");
                } else {
                    taxonomy = Taxonomy.dao.findFristByPropEQ("title", col4);
                    if (taxonomy == null)
                        stringBuilder.append(">>投保人证件类型系统未识别");
                    else
                        cardsPh.setCertTypeId(taxonomy.getId().intValue());
                }
                if (StrUtil.isBlank(col5)) {
                    stringBuilder.append(">>投保人证件号不能为空");
                } else {
                    cardsPh.setIdCard(col5);
                }
                if (StrUtil.isBlank(col6)) {
                    stringBuilder.append(">>投保人生日不能为空");
                } else {
                    try {
                        date = DateUtil.parseDate(col6);
                        cardsPh.setBirthDay(date);
                    } catch (DateException e) {
                        stringBuilder.append(">>投保人生日格式不正确");
                    }
                }
                if (StrUtil.isBlank(col7)) {
                    stringBuilder.append(">>投保人性别不能为空");
                } else {
                    cardsPh.setSex(col7.equals("男") ? "1" : "0");
                }
                if (StrUtil.isBlank(col8)) {
                    stringBuilder.append(">>投保人联系电话不能为空");
                } else {
                    cardsPh.setTel(col8);
                }
                cardsPh.setEmail(col9);
                cardsIp = new CardsIp();
                if (StrUtil.isBlank(col10)) {
                    stringBuilder.append(">>投保人与被投保人关系不能为空");
                } else {
                    cardsIp.setRelationship(CardsService.relationships.get(col10));
                }

                if (StrUtil.isBlank(col11)) {
                    stringBuilder.append(">>被投保人姓名不能为空");
                } else {
                    cardsIp.setName(col11);
                }
                if (StrUtil.isBlank(col12)) {
                    stringBuilder.append(">>被投保人证件类型不能为空");
                } else {
                    taxonomy = Taxonomy.dao.findFristByPropEQ("title", col12);
                    if (taxonomy == null)
                        stringBuilder.append(">>投保人证件类型系统未识别");
                    else
                        cardsIp.setCertTypeId(taxonomy.getId().intValue());
                }
                if (StrUtil.isBlank(col13)) {
                    stringBuilder.append(">>被投保人证件号不能为空");
                } else {
                    cardsIp.setIdCard(col13);
                    try {
                        cardTypeService.actCountCheck(cardsIp.getIdCard(), cardtype.getId());
                    } catch (CoreException ce) {
                        stringBuilder.append(">>" + ce.getMsg());
                    }
                }
                if (StrUtil.isBlank(col14)) {
                    stringBuilder.append(">>被投保人生日不能为空");
                } else {
                    try {
                        date = DateUtil.parseDate(col14);
                        cardsIp.setBirthDay(date);
                    } catch (DateException e) {
                        stringBuilder.append(">>被投保人生日格式不正确");
                    }
                }
                if (StrUtil.isBlank(col15)) {
                    stringBuilder.append(">>被投保人性别不能为空");
                } else {
                    cardsIp.setSex(col15.equals("男") ? "1" : "0");
                }
                if (StrUtil.isBlank(col16)) {
                    stringBuilder.append(">>被投保人联系电话不能为空");
                } else {
                    cardsIp.setTel(col16);
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.insert(0, "卡号>>" + col1);
                StaticLog.error(stringBuilder.toString());
                ret.append(stringBuilder.toString() + "<br>");
                fCount++;
                break;
            }

            if (StrUtil.isNotBlank(col17)) {
                insuranceOcc = new InsuranceOcc();
                insuranceOcc.setName(col17);
                insuranceOcc = InsuranceOcc.dao.checkAndAdd(insuranceOcc);
                cardsIp.setJob(insuranceOcc != null ? insuranceOcc.getId().intValue() : null);
            }
            dto = new DTO(cards, cardsPh, cardsIp);
            dtos.add(dto);
        }

        if (ret.length() > 0) {
            ret.insert(0, StrUtil.format(msg, count));
            renderFailJSON(ret.toString());
            return;
        } else {
            if (!dtos.isEmpty()) {
                cardsService.batchSavePhAndIp(dtos);
                for (int i = 0; i < dtos.size(); i++) {
                    DTO data = dtos.get(i);
                    try {
                        String smsRet = ALSMSKit.sendSMS(ResKit.getConfig("sms.anbao.card.act.signname"), ResKit.getConfig("sms.anbao.card.act.templatecode"), data.getCardsPh().getTel(), "{\"code\":\"" + data.getCards().getCode() + "\",\"person\":\"" + data.getCardsPh().getName() + "\"}");
                        if (!smsRet.equals(Consts.YORN_STR.yes.name()))
                            LogKit.error("卡激活短信通知失败");

                    } catch (ClientException e) {
                        LogKit.error("卡激活短信通知失败>>" + e.getMessage());
                    }
                }
            }
            else {
                renderFailJSON("导入的数据为空");
                return;
            }
            renderSuccessJSON("批量激活数据导入成功");
        }

    }

    /**
     * 批量激活导入，车险
     */
    public void batchImportAct01() {

        UploadFile uploadFile = getFile();
        ExcelReader excelReader = ExcelUtil.getReader(uploadFile.getFile());
        StringBuilder ret = new StringBuilder();
        List<Map<String, Object>> list = null;
        list = excelReader.readAll();
        int count = 0;
        int fCount = 0;
        String col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13, col14, col15, col16;
        Object obj = null;
        String msg = "共有{}条数据准备处理，导入过程中出现错误。<br>";
        Cards cards = null;
        CardsCarPh cardsCarPh = null;
        CardsCarIp cardsCarIp = null;
        StringBuilder stringBuilder = null;
        Taxonomy taxonomy = null;
        count = list.size();
        Cardtype cardtype = null;
        String ctt = null;
        List<DTO01> dto01List = new ArrayList<>();
        DTO01 dto01 = null;
        for (Map map : list) {
            stringBuilder = new StringBuilder();
            obj = map.get("卡号");
            col1 = obj == null ? "" : obj.toString();
            obj = map.get("密码");
            col2 = obj == null ? "" : obj.toString();
            obj = map.get("投保人类型");
            col3 = obj == null ? "" : obj.toString();
            obj = map.get("投保人姓名");
            col4 = obj == null ? "" : obj.toString();
            obj = map.get("投保人证件类型");
            col5 = obj == null ? "" : obj.toString();
            obj = map.get("投保人证件号");
            col6 = obj == null ? "" : obj.toString();
            obj = map.get("投保人联系人姓名");
            col7 = obj == null ? "" : obj.toString();
            obj = map.get("投保人联系电话");
            col8 = obj == null ? "" : obj.toString();
            obj = map.get("投保人EMAIL");
            col9 = obj == null ? "" : obj.toString();
            obj = map.get("投保人地址");
            col10 = obj == null ? "" : obj.toString();
            obj = map.get("被投保车车牌号");
            col11 = obj == null ? "" : obj.toString();
            obj = map.get("被投保车发动机号");
            col12 = obj == null ? "" : obj.toString();
            obj = map.get("被投保车大架号");
            col13 = obj == null ? "" : obj.toString();
            obj = map.get("被投保车营运类型");
            col14 = obj == null ? "" : obj.toString();
            obj = map.get("被投保车类型");
            col15 = obj == null ? "" : obj.toString();
            obj = map.get("被投保车座位数");
            col16 = obj == null ? "" : obj.toString();
            try {
                cards = cardsService.checkCard(col1, col2);
                cardtype = cardsService.getCardtypeByCardcode(cards.getCode());
                ctt = cardtype.getTypeVal();
                if (!ctt.equals("driverInsurance")) {
                    ret.append("卡号>>" + col1 + "类型不匹配，无法进行激活<br>");
                    fCount++;
                    break;
                }
                if (cards.getAct() != null && cards.getAct().equals(Consts.YORN_STR.yes.getVal())) {
                    ret.append("卡号>>" + col1 + "已经被激活<br>");
                    fCount++;
                    break;
                }
            } catch (CoreException ce) {
                stringBuilder.append(">>" + ce.getMsg());
            }
            if (cards != null) {
                cardsCarPh = new CardsCarPh();
                cardsCarPh.setType(col3.equals("个人") ? "1" : "2");
                cardsCarPh.setName(col4);
                if (StrUtil.isBlank(col5)) {
                    stringBuilder.append(">>投保人证件类型不能为空");
                } else {
                    taxonomy = Taxonomy.dao.findFristByPropEQ("title", col5);
                    if (taxonomy == null)
                        stringBuilder.append(">>投保人证件类型系统未识别");
                    else
                        cardsCarPh.setCertTypeId(taxonomy.getId().intValue());
                }
                cardsCarPh.setCertCode(col6);
                cardsCarPh.setLinkName(col7);
                cardsCarPh.setTel(col8);
                cardsCarPh.setEmail(col9);
                cardsCarPh.setAddress(col10);
                cardsCarIp = new CardsCarIp();
                cardsCarIp.setPlateNum(col11);
                cardsCarIp.setEngineNum(col12);
                cardsCarIp.setFrameNum(col13);
                cardsCarIp.setProp(col14.equals("营运") ? "2" : "1");
                taxonomy = Taxonomy.dao.findFristByPropEQ("title", col15);
                if (taxonomy == null)
                    stringBuilder.append(">>车辆类型在系统中未找到");
                else
                    cardsCarIp.setType(taxonomy.getId().intValue());
                cardsCarIp.setSeatCount(col16);
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.insert(0, "卡号>>" + col1);
                StaticLog.error(stringBuilder.toString());
                ret.append(stringBuilder.toString() + "<br>");
                fCount++;
                break;
            }
            dto01 = new DTO01(cards, cardsCarPh, cardsCarIp);
            dto01List.add(dto01);
        }

        if (ret.length() > 0) {
            ret.insert(0, StrUtil.format(msg, count));
            renderFailJSON(ret.toString());
            return;
        } else {
            if (dto01List.isEmpty()) {
                renderFailJSON("导入的数据为空");
                return;
            }else{
                cardsService.batchSaveCarPhAndIp(dto01List);
                for (int i = 0; i < dto01List.size(); i++) {
                    DTO01 data = dto01List.get(i);
                    try {
                        String smsRet = ALSMSKit.sendSMS(ResKit.getConfig("sms.anbao.card.act.signname"), ResKit.getConfig("sms.anbao.card.act.templatecode"), data.getCardsCarPh().getTel(), "{\"code\":\"" + data.getCards().getCode() + "\",\"person\":\"" + data.getCardsCarPh().getName() + "\"}");
                        if (!smsRet.equals(Consts.YORN_STR.yes.name()))
                            LogKit.error("卡激活短信通知失败");

                    } catch (ClientException e) {
                        LogKit.error("卡激活短信通知失败>>" + e.getMessage());
                    }
                }
            }
        }
        renderSuccessJSON("批量激活数据导入成功");
    }


    public class DTO {
        private Cards cards;
        private CardsIp cardsIp;
        private CardsPh cardsPh;

        public DTO(Cards cards, CardsPh cardsPh, CardsIp cardsIp) {
            this.cards = cards;
            this.cardsIp = cardsIp;
            this.cardsPh = cardsPh;
        }

        public Cards getCards() {
            return cards;
        }

        public void setCards(Cards cards) {
            this.cards = cards;
        }

        public CardsIp getCardsIp() {
            return cardsIp;
        }

        public void setCardsIp(CardsIp cardsIp) {
            this.cardsIp = cardsIp;
        }

        public CardsPh getCardsPh() {
            return cardsPh;
        }

        public void setCardsPh(CardsPh cardsPh) {
            this.cardsPh = cardsPh;
        }
    }


    public class DTO01 {
        private Cards cards;
        private CardsCarPh cardsCarPh;
        private CardsCarIp cardsCarIp;

        public DTO01(Cards cards, CardsCarPh cardsCarPh, CardsCarIp cardsCarIp) {
            this.cards = cards;
            this.cardsCarIp = cardsCarIp;
            this.cardsCarPh = cardsCarPh;
        }

        public Cards getCards() {
            return cards;
        }

        public void setCards(Cards cards) {
            this.cards = cards;
        }

        public CardsCarPh getCardsCarPh() {
            return cardsCarPh;
        }

        public void setCardsCarPh(CardsCarPh cardsCarPh) {
            this.cardsCarPh = cardsCarPh;
        }

        public CardsCarIp getCardsCarIp() {
            return cardsCarIp;
        }

        public void setCardsCarIp(CardsCarIp cardsCarIp) {
            this.cardsCarIp = cardsCarIp;
        }
    }
}
