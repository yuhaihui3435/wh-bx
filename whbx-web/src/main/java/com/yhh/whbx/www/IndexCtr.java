package com.yhh.whbx.www;

import com.jfinal.aop.Before;
import com.jfinal.aop.Duang;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Attachment;
import com.yhh.whbx.card.CardsService;
import com.yhh.whbx.card.model.*;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreException;
import com.yhh.whbx.interceptors.CardStatusCheckInterceptor;

import java.util.Date;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/26.
 */
public class IndexCtr extends CoreController
{
    private final  static  CardsService cardsService= Duang.duang(CardsService.class);

    public void index(){
        render("index.html");
    }
    @Before({CardStatusCheckInterceptor.class})
    public void actCard(){
        String code=getPara("code");
        String pwd=getPara("pwd");
        try {
            Cards cards = cardsService.checkCard(code, pwd);
            cards.setAct(Consts.YORN_STR.yes.getVal());
            cards.setActAt(new Date());
            cards.update();
            renderSuccessJSON("卡激活成功,请补充详细信息");
            return;
        }catch (CoreException e){
            renderFailJSON(e.getMsg());
            return;
        }
    }

    @Before({CardStatusCheckInterceptor.class})
    public void enterDetail(){
        String code=getPara("code");
        code="010100201";
        Cards cards=Cards.dao.findByCode(code);
        setAttr("card",cards);
        Integer cardtypeId=cards.getCtId();
        Cardtype cardtype=Cardtype.dao.findById(cardtypeId);
        if(cardtype!=null&&cardtype.getCategory()!=null){
            setAttr("cardtype",cardtype);
            setAttr("ioList",InsuranceOcc.dao.findFirstLevel(cardtype.getCategoryCode()));
        }
        //意外险
        if(cards.getCardtype().equals("accidentInsurance")){
            render("card_00/main.html");
        }
        //车险
        else if(cards.getCardtype().equals("driverInsurance")){
            render("card_00/main.html");
        }
        else{
            throw new CoreException("卡信息错误，请联系业务员");
        }


    }


    public void viewDetail(){
        String code=getPara("code");
        String pwd=getPara("pwd");
        Integer cardsId=null;
        Cards cards=null;
        try {
            cards=cardsService.checkCard(code,pwd);
            cardsId=cards.getId().intValue();
        }catch (CoreException e){
            setAttr(ERROR_MSG,e.getMsg());
            render("card_00/cardQuery.html");
            return;
        }
        List<Attachment> attachments=null;
        if(cards.getCardtype().equals("accidentInsurance")) {
            CardsPh cardsPh = CardsPh.dao.findByPropEQ("cardsId", cardsId).get(0);
            List<CardsIp> cardsIpList = CardsIp.dao.findByPropEQ("cardsId", cardsId);
            attachments=Attachment.dao.findByObjIdAndModule(cardsId,"accidentInsurance");
            setAttr("cardsPh",cardsPh);
            setAttr("cardsIpList",cardsIpList);
            setAttr("electronicPolicy",attachments);
            setAttr("card",cards);
            render("card_00/view.html");
        }else if (cards.getCardtype().equals("driverInsurance")) {
            CardsCarPh cardsCarPh = CardsCarPh.dao.findByPropEQ("cardsId", cardsId).get(0);
            CardsCarIp cardsCarIp = CardsCarIp.dao.findByPropEQ("cardsId", cardsId).get(0);
            attachments=Attachment.dao.findByObjIdAndModule(cardsId,"driverInsurance");
            setAttr("cardsCarPh",cardsCarPh);
            setAttr("cardsCarIp",cardsCarIp);
            setAttr("electronicPolicy",attachments);
            setAttr("card",cards);
            render("card_01/view.html");
        }

    }


    public void occQuery(){
        String insurance=getPara("insurance");
        List<InsuranceOcc> list=null;
        if(isParaExists("occ_0")){
            String occ_0_code=getPara("occ_0");
            list=InsuranceOcc.dao.findSecLevel(occ_0_code,insurance);
            renderJson(list);
            return;
        }else if(isParaExists("occ_1")){
            String occ_1_code=getPara("occ_1");
            list=InsuranceOcc.dao.findThirdLevel(occ_1_code,insurance);
            renderJson(list);
            return;
        }
    }
}
