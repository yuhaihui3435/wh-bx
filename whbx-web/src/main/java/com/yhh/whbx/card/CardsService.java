package com.yhh.whbx.card;

import com.xiaoleilu.hutool.crypto.SecureUtil;
import com.xiaoleilu.hutool.lang.Base64;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cardapply;
import com.yhh.whbx.card.model.Cards;
import com.yhh.whbx.core.CoreException;

import java.nio.charset.Charset;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card]
 * 类名称:    [CardsService]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/28]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class CardsService {
    private static final Cards dao=Cards.dao;


    public Cards checkCard(String code,String pwd)throws CoreException{
        if(StrUtil.isBlank(code)){
            throw new CoreException("卡编号不能为空");
        }
        if(StrUtil.isBlank(pwd)){
            throw new CoreException("卡密码不能为空");

        }

        Cards cards=dao.findByCode(code);
        if(cards.getAct().equals(Consts.YORN_STR.yes.getVal())){
            throw new CoreException("此卡片已经被激活,可以进行保单查询");
        }

        if(cards==null){
            throw new CoreException("卡号不存在");
        }
        if(cards.getStatus()!=null&&cards.getStatus().equals(Consts.YORN_STR.no.getVal())){
            throw new CoreException("此卡状态异常，请联系销售人员");
        }
        if(cards.getDepotId()==null){
            throw new CoreException("此卡未出库，请联系销售人员");
        }

        if(cards.getIsLocked().equals(Consts.YORN_STR.no.getVal())){
            throw new CoreException("此卡被锁定，请联系销售人员");
        }

        Integer applyId=cards.getApplyId();
        Cardapply cardapply=Cardapply.dao.findById(new Long(applyId));
        String key=cardapply.getSkey();
        byte[] key_array= Base64.decode(key, Charset.forName("UTF-8"));
        pwd= SecureUtil.aes(key_array).encryptHex(pwd);
        if(!cards.getPwd().equals(pwd)){
            throw new CoreException("卡密码不正确");
        }
        return cards;
    }

}
