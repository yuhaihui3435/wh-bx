package com.yhh.whbx.interceptors;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cardapply;
import com.yhh.whbx.card.model.Cards;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreException;
import com.yhh.whbx.kits.ReqKit;

/**
 * Created by yuhaihui8913 on 2018/1/8.
 */
public class CardStatusCheckInterceptor implements Interceptor
{
    @Override
    public void intercept(Invocation invocation) {
        CoreController cc=(CoreController)invocation.getController();
        String code=invocation.getController().getPara("code");
        Cards cards=Cards.dao.findByCode(code);
        if(cards==null){
            if(ReqKit.isAjaxRequest(cc.getRequest())){
                cc.renderFailJSON("卡片信息不存在,请联系销售人员");
                return;
            }else{
                throw new CoreException("卡片信息不存在，请联系销售人员");
            }
        }else{
            if(!cards.getStatus().equals(Consts.STATUS.enable.getVal())){
                if(ReqKit.isAjaxRequest(cc.getRequest())){
                    cc.renderFailJSON("卡片被停用,请联系销售人员");
                    return;
                }else{
                    throw new CoreException("卡片被停用，请联系销售人员");
                }
            }else if (cards.getOutStatus().equals(Consts.YORN_STR.no.getVal())){
                if(ReqKit.isAjaxRequest(cc.getRequest())){
                    cc.renderFailJSON("卡片未出库,请联系销售人员");
                    return;
                }else{
                    throw new CoreException("卡片未出库，请联系销售人员");
                }
            }else if (cards.getIsLocked().equals(Consts.YORN_STR.no.getVal())){
                if(ReqKit.isAjaxRequest(cc.getRequest())){
                    cc.renderFailJSON("卡片被锁定,请联系销售人员");
                    return;
                }else{
                    throw new CoreException("卡片被锁定，请联系销售人员");
                }
            }
            Cardapply cardapply=Cardapply.dao.findById(cards.getApplyId());
            Cardtype cardtype=Cardtype.dao.findById(cardapply.getCardtypeId());
            if(cardtype!=null&&cardtype.getStatus().equals(Consts.YORN_STR.no.getVal())){
                if(ReqKit.isAjaxRequest(cc.getRequest())){
                    cc.renderFailJSON("该类型卡被禁用,无法注册");
                    return;
                }else{
                    throw new CoreException("该类型卡被禁用,无法注册");
                }
            }

        }
        invocation.invoke();

    }
}
