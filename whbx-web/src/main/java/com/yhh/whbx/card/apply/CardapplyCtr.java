package com.yhh.whbx.card.apply;

import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaoleilu.hutool.collection.CollUtil;
import com.xiaoleilu.hutool.crypto.SecureUtil;
import com.xiaoleilu.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.xiaoleilu.hutool.lang.Base64;
import com.xiaoleilu.hutool.poi.excel.ExcelUtil;
import com.xiaoleilu.hutool.poi.excel.ExcelWriter;
import com.xiaoleilu.hutool.util.MapUtil;
import com.xiaoleilu.hutool.util.RandomUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cardapply;
import com.yhh.whbx.card.model.Cards;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card.apply]
 * 类名称:    [CardapplyCtr]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/13]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class CardapplyCtr extends CoreController {
    public void page() {
        Page<Cardapply> page;
        Kv kv = Kv.create();
        if (!isParaBlank("cardtypeId"))
            kv.put("cardtypeId=", getParaToLong("cardtypeId"));
        if (!isParaBlank("batch"))
            kv.put("batch=", getParaToLong("batch"));
        if (!isParaBlank("media"))
            kv.put("media=", getParaToLong("media"));
        if (!isParaBlank("checkStatus"))
            kv.put("checkStatus=", getPara("checkStatus"));
        if (!isParaBlank("status"))
            kv.put("status=", getPara("status"));
        if (!isParaBlank("exeCard"))
            kv.put("exeCard=", getPara("exeCard"));

        SqlPara sqlPara = Db.getSqlPara("cardapply.findPage", Kv.by("cond", kv));
        page = Cardapply.dao.paginate(getPN(), getPS(), sqlPara);
        renderJson(page);
    }

    public void list() {
        renderJson(Cardapply.dao.findAllNotDel());
    }

    @Before({CardapplyValidator.class, Tx.class})
    public void save() {
        Cardapply cardapply = getModel(Cardapply.class, "", true);
        cardapply.setCAt(new Date());
        cardapply.setStatus(Consts.STATUS.enable.getVal());
        cardapply.setCheckStatus(Consts.CHECK_STATUS.waitingCheck.getVal());
        cardapply.setExeCard(Consts.YORN_STR.no.getVal());
        cardapply.setOper(currUser() == null ? null : Integer.parseInt(currUser().getId()));
        cardapply.save();
        renderSuccessJSON("新增操作成功。");
    }

    @Before({CardapplyValidator.class, Tx.class})
    public void update() {
        Cardapply cardapply = getModel(Cardapply.class, "", true);
        String exe = getPara("exe");
        cardapply.update();
        String msg = "操作成功";
        switch (exe) {
            case "update":
                msg = "修改操作成功。";
                break;
            case "stop":
                msg = "停用操作成功。";
                break;
            case "active":
                msg = "激活操作成功。";
                break;
            case "del":
                msg = "删除操作成功。";
                break;
            case "check":
                msg = cardapply.getCheckStatus().equals(Consts.CHECK_STATUS.normal) ? "审核通过。" : "审核未通过";
                break;
        }
        renderSuccessJSON(msg);

    }

    public void get() {
        Long id = getParaToLong("id");
        renderJson(Cardapply.dao.findById(id));
    }


    public void dataReady() {
        Map<String, Object> map = new HashMap<>();
        map.put("mediaList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "mediaList"));
        map.put("cardtypeList", Cardtype.dao.findEnableList());
        renderJson(map);
    }

    @Before({Tx.class})
    public void createCard() {
        Cardapply cardapply = getModel(Cardapply.class, "", true);
        Long seq = Cards.dao.countByCardapplyId(cardapply.getId().intValue());
        Integer count = cardapply.getNum();
        Cardtype cardtype = Cardtype.dao.findById(cardapply.getCardtypeId());
        if (cardtype.getStatus().equals(Consts.STATUS.forbidden.getVal())) {
            renderFailJSON("卡类型被停用，无法完成制卡操作");
            return;
        }
        Integer maxCardcount = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "createCardMaxCount") != null ? Integer.parseInt((String) CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "createCardMaxCount")) : null;
        maxCardcount = maxCardcount == null ? 99999 : maxCardcount;
        if (count + seq.intValue() > maxCardcount) {
            renderFailJSON("该申请制卡的数量超过了最大数量" + maxCardcount);
            return;
        }
        int y = (maxCardcount + "").length();
        String cardCode = null;
        String pwd = null;
        Cards card = null;
        byte[] key= SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        String skey=Base64.encode(key, Charset.forName("UTF-8"));
        cardapply.setSkey(skey);
        for (int i = 0; i < count; i++) {
            seq = seq + 1;
            cardCode = cardtype.getCode() + cardapply.getBatch() + StrUtil.fillBefore(seq + "", '0', y);
            pwd = RandomUtil.randomNumbers(6);
            card=new Cards();
            card.setStatus(Consts.STATUS.enable.getVal());
            card.setAct(Consts.YORN_STR.no.getVal());
            card.setApplyId(cardapply.getId().intValue());
            card.setCode(cardCode);
            card.setIsLocked(Consts.YORN_STR.no.getVal());
            card.setPwd(SecureUtil.aes(key).encryptHex(pwd));
            card.setSeq(seq.intValue());
            card.save();
        }
        cardapply.setExeCard(Consts.YORN_STR.yes.getVal());
        cardapply.update();
        renderSuccessJSON("制卡成功");
    }

    @Before({Tx.class})
    public void exportCardsExcel() throws IOException {
        int applyId = getParaToInt("applyId");
        Cardapply cardapply = Cardapply.dao.findById(applyId);
        cardapply.setDownloadAt(new Date());
        cardapply.update();
        List<Cards> cardsList = Cards.dao.findByCardapplyId(applyId);
        ExcelWriter writer = ExcelUtil.getWriter();
        CCBean ccBean = null;
        List<CCBean> ccBeans = CollUtil.newArrayList();

        byte[] key= Base64.decode(cardapply.getSkey());
        for (Cards cards : cardsList) {
            ccBean = new CCBean();
            ccBean.setCode(cards.getCode());
            ccBean.setPwd(SecureUtil.aes(key).decryptStr(cards.getPwd()));
            ccBeans.add(ccBean);
        }
        //自定义标题别名
        Map<String, String> alias = MapUtil.newHashMap();
        alias.put("code", "卡号");
        alias.put("pwd", "密码");
        writer.setHeaderAlias(alias);

        writer.write(ccBeans);
        getResponse().setContentType("application/x-msdownload");
        getResponse().setHeader("Content-Disposition",
                "attachment;filename=\"" + URLEncoder.encode(cardapply.getCardtypeTxt() + cardapply.getBatch(), "UTF-8") + ".xls\"");
        OutputStream out = getResponse().getOutputStream();
        writer.flush(out);
        writer.close();
        out.flush();
        out.close();
        renderNull();
    }

    public class CCBean {
        private String code;
        private String pwd;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }
    }

    public void listByCardtypeId(){
        Integer cardtypeId=getParaToInt("ctId");
        String batch=getPara("bacth");
        renderJson(Cardapply.dao.findByCardtypeIdAndBatch( cardtypeId,batch));
    }

    public void recommendNum(){
        Integer id=getParaToInt("applyId");
        Long lastSeq=Cards.dao.findLastCardCodeByCardapplyId(id);
        Long enableCount=Cards.dao.findEnableCardCount(id);
        Map<String,Long> map=new HashMap<>();
        map.put("bNum",lastSeq);
        map.put("eNum",enableCount);
        renderJson(map);
    }

}
