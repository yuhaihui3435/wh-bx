package com.yhh.whbx.www;

import com.jfinal.aop.Duang;
import com.jfinal.aop.Enhancer;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Attachment;
import com.yhh.whbx.card.CardsService;
import com.yhh.whbx.card.model.*;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreException;

import java.util.Date;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/26.
 */
public class IndexCtr extends CoreController
{
    private final  static  CardsService cardsService= Duang.duang(CardsService.class);

    public void index(){
        ;render("index.html");
    }

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


    public void enterDetail(){
        render("card_00/main.html");
        return;
//        String code=getPara("code");
//        Cards cards=Cards.dao.findByCode(code);
//        if(cards==null){
//            setAttr(ERROR_MSG,"卡信息不存在");
//            renderError(404);
//            return;
//        }
//        setSessionAttr("card",cards);
//        //意外险
//        if(cards.getCardtype().equals("accidentInsurance")){
//
//        }
//        //车险
//        else if(cards.getCardtype().equals("driverInsurance")){
//
//        }else{
//            setAttr(ERROR_MSG,"卡信息错误，请联系销售人员");
//            renderError(500);
//            return;
//        }
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





}
