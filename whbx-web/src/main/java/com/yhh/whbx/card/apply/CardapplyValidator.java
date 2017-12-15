package com.yhh.whbx.card.apply;

import com.jfinal.core.Controller;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cardapply;
import com.yhh.whbx.core.CoreValidator;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card.apply]
 * 类名称:    [CardapplyValidator]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/13]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class CardapplyValidator extends CoreValidator {

    public static final String BATCH_EXIST="批次已经存在";
    public static final String DATA_STATUS_ERROR="无法执行此操作，原因【数据状态不正确或未同步，请刷新数据】";

    @Override
    protected void validate(Controller controller) {
        Cardapply cardapply=controller.getModel(Cardapply.class,"",true);
        String ak=getActionKey();
        List<Cardapply> list=null;
        if(ak.equals("/c01/save")){
            list=Cardapply.dao.findByBatchAndCardtype(cardapply.getCardtypeId(),cardapply.getBatch());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),BATCH_EXIST);
                return ;
            }
        }else  if(ak.equals("/c01/update")){
            Cardapply ca=Cardapply.dao.findById(cardapply.getId());
            String exe=controller.getPara("exe");
            if(exe.equals("update")) {
                if ( ca.getDAt()!=null||!ca.getCheckStatus().equals(Consts.CHECK_STATUS.waitingCheck.getVal())||!ca.getExeCard().equals(Consts.YORN_STR.no.getVal())){
                    addError(Consts.REQ_JSON_CODE.fail.name(),DATA_STATUS_ERROR);
                    return ;
                }
                list=Cardapply.dao.findByBatchAndCardtypeAndNEId(cardapply.getCardtypeId(),cardapply.getBatch(),cardapply.getId());
                if(!list.isEmpty()){
                    addError(Consts.REQ_JSON_CODE.fail.name(),BATCH_EXIST);
                    return ;
                }
            }else if(exe.equals("check")){
                if (!ca.getStatus().equals(Consts.STATUS.enable.getVal()) ||ca.getDAt()!=null||!ca.getCheckStatus().equals(Consts.CHECK_STATUS.waitingCheck.getVal())||!ca.getExeCard().equals(Consts.YORN_STR.no.getVal())){
                    addError(Consts.REQ_JSON_CODE.fail.name(),DATA_STATUS_ERROR);
                    return ;
                }
            }else if(exe.equals("del")){
                if (ca.getDAt()!=null||ca.getCheckStatus().equals(Consts.CHECK_STATUS.normal.getVal())||!ca.getExeCard().equals(Consts.YORN_STR.no.getVal())){
                    addError(Consts.REQ_JSON_CODE.fail.name(),DATA_STATUS_ERROR);
                    return ;
                }
            }else if(exe.equals("active")){
                if (ca.getStatus().equals(Consts.STATUS.enable.getVal())||ca.getDAt()!=null){
                    addError(Consts.REQ_JSON_CODE.fail.name(),DATA_STATUS_ERROR);
                    return ;
                }
            }
            else if(exe.equals("stop")){
                if (ca.getStatus().equals(Consts.STATUS.forbidden.getVal())||ca.getDAt()!=null){
                    addError(Consts.REQ_JSON_CODE.fail.name(),DATA_STATUS_ERROR);
                    return ;
                }
            }

        }else  if(ak.equals("/c01/createCard")){
            Cardapply ca=Cardapply.dao.findById(cardapply.getId());
            if(ca.getStatus().equals(Consts.STATUS.forbidden.getVal())){
                addError(Consts.REQ_JSON_CODE.fail.name(),"卡申请被禁用，无法完成制卡操作");
                return ;
            }else if(!ca.getStatus().equals(Consts.CHECK_STATUS.normal.getVal())){
                addError(Consts.REQ_JSON_CODE.fail.name(),"卡申请未通过审核，无法完成制卡操作");
                return ;
            }else if(ca.getDAt()!=null){
                addError(Consts.REQ_JSON_CODE.fail.name(),"卡申请数据被删除，无法完成制卡操作");
                return ;
            }
        }
    }

    @Override
    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
