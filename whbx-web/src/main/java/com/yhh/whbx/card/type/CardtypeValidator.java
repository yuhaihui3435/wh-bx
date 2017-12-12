package com.yhh.whbx.card.type;

import com.jfinal.core.Controller;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreValidator;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card.type]
 * 类名称:    [CardtypeValidator]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/10]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class CardtypeValidator extends CoreValidator {
    public static final String CODE_EXIST="编号已经存在";
    public static final String NAME_EXIST="名称已经存在";
    @Override
    protected void validate(Controller controller) {
        controller.getFiles();
        Cardtype cardtype=controller.getModel(Cardtype.class,"",true);
        String ak=getActionKey();
        List<Cardtype> list=null;
        if(ak.equals("/c00/save")){
            list=Cardtype.dao.findByPropEQ("code",cardtype.getCode());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),CODE_EXIST);
                return ;
            }
            list=Cardtype.dao.findByPropEQ("name",cardtype.getName());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),NAME_EXIST);
                return ;
            }
        }else if(ak.equals("/c00/update")){
            list=Cardtype.dao.findByPropEQ("code",cardtype.getCode());
            if(!list.isEmpty()){
                for(Cardtype ct : list){
                    if(ct.getId()!=cardtype.getId()){
                        addError(Consts.REQ_JSON_CODE.fail.name(),CODE_EXIST);
                        return ;
                    }
                }

            }
            list=Cardtype.dao.findByPropEQ("name",cardtype.getName());
            if(!list.isEmpty()){
                for(Cardtype ct : list){
                    if(ct.getId()!=cardtype.getId()) {
                        addError(Consts.REQ_JSON_CODE.fail.name(), NAME_EXIST);
                        return;
                    }
                }
            }
        }

    }

    @Override
    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
