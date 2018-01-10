package com.yhh.whbx.www;

import com.aliyuncs.exceptions.ClientException;
import com.jfinal.aop.Before;
import com.jfinal.aop.Duang;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Attachment;
import com.yhh.whbx.card.CardsService;
import com.yhh.whbx.card.model.*;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreException;
import com.yhh.whbx.interceptors.CardStatusCheckInterceptor;
import com.yhh.whbx.kits.ALSMSKit;
import com.yhh.whbx.kits.ResKit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/26.
 */
public class IndexCtr extends CoreController {
    private final static CardsService cardsService = Duang.duang(CardsService.class);

    public void index() {
        render("index.html");
    }
//    @Before({CardStatusCheckInterceptor.class})
//    public void actCard(){
//        String code=getPara("code");
//        String pwd=getPara("pwd");
//        try {
//            Cards cards = cardsService.checkCard(code, pwd);
//
//            if(StrUtil.isNotBlank(cards.getAct())&&cards.getAct().equals(Consts.YORN_STR.yes.getVal())){
//                renderSuccessJSON("卡已经激活");
//                return;
//            }
//
//
//            renderSuccessJSON("卡激活成功,请补充详细信息");
//            return;
//        }catch (CoreException e){
//            renderFailJSON(e.getMsg());
//            return;
//        }
//    }

    @Before({CardStatusCheckInterceptor.class})
    public void enterDetail() {
        String code = getPara("code");
        String pwd = getPara("pwd");
        keepPara(code,pwd);
        Cards cards = null;
        try {
            cards = cardsService.checkCard(code, pwd);
        } catch (CoreException e) {
            throw e;
        }

        if(cards.getAct().equals(Consts.YORN_STR.yes.getVal())){
            throw  new CoreException("卡片已经激活，请直接查询保单详细");

        }

        //code="010100201";
        cards = Cards.dao.findByCode(code);
//        if(cards.getAct()==null||!cards.getAct().equals(Consts.YORN_STR.yes.getVal())){
//            renderFailJSON("卡片未被激活，请先激活卡片");
//            return;
//        }


        setAttr("card", cards);
        Integer cardtypeId = cards.getCtId();

        //意外险
        if (cards.getCardtype().equals("accidentInsurance")) {
            List<CardsPh> cardsPhs = CardsPh.dao.findByPropEQ("cardsId", cards.getId());
            if (cardsPhs.size() > 0) {
                setAttr("cardsPh", cardsPhs.get(0));
                setAttr("cardsIpList", CardsIp.dao.findByPropEQ("cardsId", cards.getId()));
                render("card_00/view.html");
            } else {
                Cardtype cardtype = Cardtype.dao.findById(cardtypeId);
                if (cardtype != null && cardtype.getCategory() != null) {
                    setAttr("cardtype", cardtype);
                    setAttr("ioList", InsuranceOcc.dao.findFirstLevel(cardtype.getCategoryCode()));
                }
                setAttr("certTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "certTypeList"));
                render("card_00/main.html");
            }
        }
        //车险
        else if (cards.getCardtype().equals("driverInsurance")) {
            CardsCarIp cardsCarIp = CardsCarIp.dao.findFristByPropEQ("cardsId", cards.getId());
            if (cardsCarIp != null) {
                setAttr("cardsCarIp", cardsCarIp);
                setAttr("cardsCarPh", CardsCarPh.dao.findFristByPropEQ("cardsId", cards.getId()));
                render("card_01/view.html");
            } else {
                Cardtype cardtype = Cardtype.dao.findById(cardtypeId);
                setAttr("cardtype", cardtype);
                setAttr("certTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "certTypeList"));
                setAttr("companyCertTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "companyCertTypeList"));
                setAttr("carTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "carTypeList"));
                render("card_01/main.html");
            }
        } else {
            throw new CoreException("卡信息错误，请联系业务员");
        }


    }

    @Before({CardStatusCheckInterceptor.class, Tx.class})
    public void saveCardInfo() {
        CardsPh cardsPh = getModel(CardsPh.class, "ph", true);
        Cards cards = Cards.dao.findById(cardsPh.getCardsId());
        cards.setAct(Consts.YORN_STR.yes.getVal());
        cards.setActAt(new Date());
        cards.update();
        List<CardsPh> cardsPhs = CardsPh.dao.findByPropEQ("cardsId", cards.getId());

        if (cardsPhs.size() > 0) {
            renderFailJSON("保单信息已经录入，如要修改请联系销售人员");
            return;
        }

        if (cards.getAct() == null || cards.getAct().equals(Consts.YORN_STR.no.getVal())) {
            renderFailJSON("卡片未被激活，请先激活卡片");
            return;
        }

        int ipLen = getParaToInt("ipLen", -1);
        List<CardsIp> list = new ArrayList<>();
        CardsIp cardsIp = null;
        for (int i = 0; i < ipLen; i++) {
            cardsIp = getModel(CardsIp.class, "ip" + i, true);
            list.add(cardsIp);
        }

        cardsPh.save();
        Db.batchSave(list, 10);
        try {
            String ret = ALSMSKit.sendSMS(ResKit.getConfig("sms.anbao.card.act.signname"), ResKit.getConfig("sms.anbao.card.act.templatecode"), cardsPh.getTel(), "{\"code\":\"" + cards.getCode() + "\"}");
            if (!ret.equals(Consts.YORN_STR.yes.name()))
                LogKit.error("卡激活短信通知失败");

        } catch (ClientException e) {
            LogKit.error("卡激活短信通知失败>>" + e.getMessage());
        }

        renderSuccessJSON(cardsService.getCardtypeByCardcode(cards.getCode()).getActMsg());

    }

    @Before({CardStatusCheckInterceptor.class, Tx.class})
    public void saveCardCarInfo() {
        CardsCarPh cardsCarPh=getModel(CardsCarPh.class,"",true);
        CardsCarIp cardsCarIp=getModel(CardsCarIp.class,"",true);
        Integer cardsId=getParaToInt("cardsId");
        Integer carType=getParaToInt("carType");
        cardsCarIp.setType(carType);
        String cardsCode=getPara("code");
        cardsCarIp.setCardsId(cardsId);
        cardsCarPh.setCardsId(cardsId);
        Cards cards=Cards.dao.findFristByPropEQ("code",cardsCode);
        cards.setAct(Consts.YORN_STR.yes.getVal());
        cards.setActAt(new Date());

        if(CardsCarIp.dao.findFristByPropEQ("cardsId",cardsId)!=null){
            renderFailJSON("此卡疑似已经被激活");
            return;
        }

        if(CardsCarPh.dao.findFristByPropEQ("cardsId",cardsId)!=null){
            renderFailJSON("此卡疑似已经被激活");
            return;
        }

        cards.update();
        cardsCarIp.setCAt(new Date());
        cardsCarPh.setCAt(new Date());
        cardsCarIp.save();
        cardsCarPh.save();
        renderSuccessJSON(cardsService.getCardtypeByCardcode(cards.getCode()).getActMsg());
    }


    public void viewDetail() {
        String code = getPara("code");
        String pwd = getPara("pwd");
        Integer cardsId = null;
        Cards cards = null;
        try {
            cards = cardsService.checkCard(code, pwd);
            cardsId = cards.getId().intValue();
        } catch (CoreException e) {
            throw e;
        }
        cards = Cards.dao.findByCode(code);
        if (cards.getAct() == null || !cards.getAct().equals(Consts.YORN_STR.yes.getVal())) {
            throw new CoreException("此卡未激活，请激活后进行查看");
        }
        List<Attachment> attachments = null;
        if (cards.getCardtype().equals("accidentInsurance")) {
            CardsPh cardsPh = CardsPh.dao.findFristByPropEQ("cardsId", cardsId);
            List<CardsIp> cardsIpList = CardsIp.dao.findByPropEQ("cardsId", cardsId);
            attachments = Attachment.dao.findByObjIdAndModule(cardsId, "accidentInsurance");
            setAttr("cardsPh", cardsPh);
            setAttr("cardsIpList", cardsIpList);
            setAttr("electronicPolicy", attachments);
            setAttr("card", cards);
            render("card_00/view.html");
        } else if (cards.getCardtype().equals("driverInsurance")) {
            CardsCarPh cardsCarPh = CardsCarPh.dao.findFristByPropEQ("cardsId", cardsId);
            CardsCarIp cardsCarIp = CardsCarIp.dao.findFristByPropEQ("cardsId", cardsId);
            attachments = Attachment.dao.findByObjIdAndModule(cardsId, "driverInsurance");
            setAttr("cardsCarPh", cardsCarPh);
            setAttr("cardsCarIp", cardsCarIp);
            setAttr("electronicPolicy", attachments);
            setAttr("card", cards);
            render("card_01/view.html");
        }

    }


    public void occQuery() {
        String insurance = getPara("insurance");
        List<InsuranceOcc> list = null;
        if (isParaExists("occ_0")) {
            String occ_0_code = getPara("occ_0");
            list = InsuranceOcc.dao.findSecLevel(occ_0_code, insurance);
            renderJson(list);
            return;
        } else if (isParaExists("occ_1")) {
            String occ_1_code = getPara("occ_1");
            list = InsuranceOcc.dao.findThirdLevel(occ_1_code, insurance);
            renderJson(list);
            return;
        }
    }

}
