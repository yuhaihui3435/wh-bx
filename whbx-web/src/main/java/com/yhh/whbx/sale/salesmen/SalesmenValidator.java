package com.yhh.whbx.sale.salesmen;

import com.jfinal.core.Controller;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.core.CoreValidator;
import com.yhh.whbx.sale.model.Salesmen;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.sale.salesmen]
 * 类名称:    [SalesmenValidator]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/9]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class SalesmenValidator extends CoreValidator {

    public static final String CODE_EXIST="编号已经存在";
    public static final String EMAIL_EXIST="邮箱已经使用";
    public static final String PHONE_EXIST="手机号已经使用";
    public static final String IDCARD_EXIST="身份证已经使用";
    public static final String CODE_REQUIRED="编号必填";

    @Override
    protected void validate(Controller controller) {
        Salesmen salesmen=controller.getModel(Salesmen.class,"",true);
        String ak=getActionKey();
        List<Salesmen> list=null;
        if(ak.equals("/sl00/save")){
            if(StrUtil.isBlank(salesmen.getCode())){
                addError(Consts.REQ_JSON_CODE.fail.name(),CODE_REQUIRED);
                return ;
            }
            list=Salesmen.dao.findByCode(salesmen.getCode());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),CODE_EXIST);
                return ;
            }
            if(StrUtil.isNotBlank(salesmen.getEmail())){
                list=Salesmen.dao.findByEmail(salesmen.getEmail());
                if(!list.isEmpty()){
                    addError(Consts.REQ_JSON_CODE.fail.name(),EMAIL_EXIST);
                    return ;
                }
            }

            list=Salesmen.dao.findByPhone(salesmen.getPhone());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),PHONE_EXIST);
                return ;
            }
            list=Salesmen.dao.findByIdcard(salesmen.getIdcard());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),EMAIL_EXIST);
                return ;
            }
        }
        if(ak.equals("/sl00/update")){
            list=Salesmen.dao.findByCode(salesmen.getCode());
            if(!list.isEmpty()){
                for (Salesmen s:list){
                    if(s.getId()!=salesmen.getId()){
                        addError(Consts.REQ_JSON_CODE.fail.name(),CODE_EXIST);
                        return ;
                    }
                }

            }
            if(StrUtil.isNotBlank(salesmen.getEmail())) {
                list = Salesmen.dao.findByEmail(salesmen.getEmail());
                if (!list.isEmpty()) {
                    for (Salesmen s : list) {
                        if (s.getId() != salesmen.getId()) {
                            addError(Consts.REQ_JSON_CODE.fail.name(), CODE_EXIST);
                            return;
                        }
                    }
                }
            }
            list=Salesmen.dao.findByPhone(salesmen.getPhone());
            if(!list.isEmpty()){
                for (Salesmen s:list){
                    if(s.getId()!=salesmen.getId()){
                        addError(Consts.REQ_JSON_CODE.fail.name(),CODE_EXIST);
                        return ;
                    }
                }
            }
            list=Salesmen.dao.findByIdcard(salesmen.getIdcard());
            if(!list.isEmpty()){
                for (Salesmen s:list){
                    if(s.getId()!=salesmen.getId()){
                        addError(Consts.REQ_JSON_CODE.fail.name(),CODE_EXIST);
                        return ;
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
