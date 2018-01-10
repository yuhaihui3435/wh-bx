package com.yhh.whbx.card.depot;

import com.jfinal.core.Controller;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Depot;
import com.yhh.whbx.core.CoreValidator;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card.depot]
 * 类名称:    [DepotValidator]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/16]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class DepotValidator extends CoreValidator {
    @Override
    protected void validate(Controller controller) {
        Depot depot=controller.getModel(Depot.class,"",true);
        String ak=getActionKey();
        List<Depot> list=null;
        if(ak.equals("/c02/save")){
            list=Depot.dao.findByCodeAndApplyId(depot.getCode(),depot.getCardapplyId());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),"编号已经存在");
                return ;
            }
            String str=DepotService.service.checkNum(depot.getBNum(),depot.getENum(),depot.getCardapplyId());
            if(str.equals(Consts.YORN_STR.no.getVal())){
                addError(Consts.REQ_JSON_CODE.fail.name(),"卡号范围重叠");
                return ;
            }

        }else  if(ak.equals("/c02/update")){
            list=Depot.dao.findByCodeAndApplyIdAndNeId(depot.getCode(),depot.getCardapplyId(),depot.getId());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),"编号已经存在");
                return ;
            }
            String str=DepotService.service.checkNumWithoutId(depot.getBNum(),depot.getENum(),depot.getCardapplyId(),depot.getId());
            if(str.equals(Consts.YORN_STR.no.getVal())){
                addError(Consts.REQ_JSON_CODE.fail.name(),"卡号范围重叠");
                return ;
            }
        }
    }

    @Override
    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
