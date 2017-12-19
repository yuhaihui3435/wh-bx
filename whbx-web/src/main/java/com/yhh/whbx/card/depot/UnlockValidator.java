package com.yhh.whbx.card.depot;

import com.jfinal.core.Controller;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Depot;
import com.yhh.whbx.card.model.Unlock;
import com.yhh.whbx.core.CoreValidator;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/12/19.
 */
public class UnlockValidator extends CoreValidator {
    @Override
    protected void validate(Controller controller) {
        Unlock unlock=controller.getModel(Unlock.class,"",true);
        String ak=getActionKey();
        List<Unlock> list=null;
        if(ak.equals("/c02/saveUnlockRecord")){
            String ret=Unlock.dao.checkNumEnable(unlock.getBNum(),unlock.getENum(),unlock.getDepotId());
            if(ret.equals(Consts.YORN_STR.no.getVal())){
                addError(Consts.REQ_JSON_CODE.fail.name(),"解锁编号范围无效，与已解锁编号范围重叠。");
                return ;
            }

            Depot depot= Depot.dao.findById(unlock.getDepotId());
            if(unlock.getBNum()<depot.getBNum() || unlock.getENum()>depot.getENum()){
                addError(Consts.REQ_JSON_CODE.fail.name(),"解锁编号范围不在出库卡编号范围内。");
                return ;
            }

        }
    }

    @Override
    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
