package com.yhh.whbx.www;

import com.xiaoleilu.hutool.crypto.SecureUtil;
import com.xiaoleilu.hutool.lang.Base64;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cardapply;
import com.yhh.whbx.card.model.Cards;
import com.yhh.whbx.core.CoreController;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by yuhaihui8913 on 2017/12/26.
 */
public class IndexCtr extends CoreController
{

    public void index(){
        ;render("index.html");
    }

    public void actCard(){
        String code=getPara("code");
        String pwd=getPara("pwd");
        if(StrUtil.isBlank(code)){
            renderFailJSON("卡编号不能为空");
            return;
        }
        if(StrUtil.isBlank(pwd)){
            renderFailJSON("卡密码不能为空");
            return;
        }

        Cards cards=Cards.dao.findByCode(code);
        if(cards.getAct().equals(Consts.YORN_STR.yes.getVal())){
            renderSuccessJSON("此卡片已经被激活,可以进行保单查询");
            return;
        }

        if(cards==null){
            renderFailJSON("卡号不存在");
            return;
        }
        if(cards.getStatus()!=null&&cards.getStatus().equals(Consts.YORN_STR.no.getVal())){
            renderFailJSON("此卡状态异常，请联系销售人员");
            return;
        }
        if(cards.getDepotId()==null){
            renderFailJSON("此卡未出库，请联系销售人员");
            return;
        }

        if(cards.getIsLocked().equals(Consts.YORN_STR.no.getVal())){
            renderFailJSON("此卡被锁定，请联系销售人员");
            return;
        }


        Integer applyId=cards.getApplyId();
        Cardapply cardapply=Cardapply.dao.findById(new Long(applyId));
        String key=cardapply.getSkey();
        byte[] key_array= Base64.decode(key, Charset.forName("UTF-8"));
        pwd=SecureUtil.aes(key_array).encryptHex(pwd);

        if(cards.getPwd().equals(pwd)){
            cards.setAct(Consts.YORN_STR.yes.getVal());
            cards.setActAt(new Date());
            cards.update();
            renderSuccessJSON("卡激活成功,请补充详细信息");
            return;
        }else {
            renderFailJSON("卡密码不正确");
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









}
