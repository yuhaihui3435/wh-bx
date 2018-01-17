package com.yhh.whbx.card;

import com.xiaoleilu.hutool.crypto.SecureUtil;
import com.xiaoleilu.hutool.date.DatePattern;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.lang.Base64;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.*;
import com.yhh.whbx.core.CoreException;

import java.nio.charset.Charset;
import java.util.*;

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
        List<Cards> list=dao.findByPropEQ("code",code);
        Cards cards=list.isEmpty()?null:list.get(0);

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


    public Cardtype getCardtypeByCardcode(String cardcode){
        Cards cards=Cards.dao.findByCode(cardcode);
        Integer ctId=cards.getCtId();
        return Cardtype.dao.findById(ctId);
    }

    public void cleanActData(String type,int cardsId){
        if (type.equals("accidentInsurance")) {
            CardsPh cardsPh=CardsPh.dao.findFristByPropEQ("cardsId",cardsId);
            if(cardsPh!=null)
            cardsPh.delete();
            List<CardsIp> cardsIps=CardsIp.dao.findByPropEQ("cardsId",cardsId);
            for(CardsIp cardsIp:cardsIps){
                cardsIp.delete();
            }
        }else if(type.equals("driverInsurance")){
            CardsCarPh cardsCarPh=CardsCarPh.dao.findFristByPropEQ("cardsId",cardsId);
            CardsCarIp cardsCarIp=CardsCarIp.dao.findFristByPropEQ("cardsId",cardsId);
            if(cardsCarIp!=null)
            cardsCarIp.delete();
            if(cardsCarPh!=null)
            cardsCarPh.delete();
        }
    }

    public static final HashMap<String,String> relationships=new HashMap<>();
    static {
        relationships.put("00","父亲");
        relationships.put("01","母亲");
        relationships.put("02","儿子");
        relationships.put("03","女儿");
        relationships.put("04","配偶");
        relationships.put("05","本人");


    }

    /**
     * 装配保单
     * @param cards 卡数据
     * @param list  导出数据载体
     */
    public void assemblePolicy(Cards cards,List list){
        Map ret=null;
        if(cards.getCardtypeTypeTxt().equals("accidentInsurance")) {
            List<CardsIp> cardsIpList=cards.getCip();
            for (CardsIp cardsIp:cardsIpList){
                ret=new LinkedHashMap();
                ret.put("cardsCode",cards.getCode());
                ret.put("phName", cards.getCph().getName());
                ret.put("phCertType",cards.getCph().getCertTypeTxt());
                ret.put("phIdNum", cards.getCph().getIdCard());
                ret.put("phBirth", DateUtil.format(cards.getCph().getBirthDay(), DatePattern.NORM_DATE_PATTERN));
                ret.put("phSex",cards.getCph().getSexTxt());
                ret.put("phTel", cards.getCph().getTel());
                ret.put("phEmail",cards.getCph().getEmail());
                ret.put("relationship",relationships.get(cardsIp.getRelationship()));
                ret.put("ipName",cardsIp.getName());
                ret.put("ipCertType",cardsIp.getCertTypeTxt());
                ret.put("ipIdNum",cardsIp.getIdCard());
                ret.put("ipBirth",DateUtil.format(cardsIp.getBirthDay(), DatePattern.NORM_DATE_PATTERN));
                ret.put("ipSex",cardsIp.getSexTxt());
                ret.put("ipTel",cardsIp.getTel());
                ret.put("ipJob",cardsIp.getJobTxt());
                ret.put("policyNum","");
                ret.put("reportTel","");
                list.add(ret);
            }
        }else if(cards.getCardtypeTypeTxt().equals("driverInsurance")){
            ret=new LinkedHashMap();
            ret.put("cardsCode",cards.getCode());
            ret.put("cphType", cards.getCcph().getTypeTxt());
            ret.put("cphName", cards.getCcph().getName());
            ret.put("cphCertType", cards.getCcph().getCertTypeTxt());
            ret.put("cphIdNum", cards.getCph().getIdCard());
            ret.put("cphLinkName", cards.getCcph().getLinkName());
            ret.put("cphTel", cards.getCcph().getTel());
            ret.put("cphEmail", cards.getCph().getEmail());
            ret.put("cphAddress", cards.getCcph().getFullAddress());
            ret.put("cipPlateNum", cards.getCcip().getPlateNum());
            ret.put("cipEngineNum", cards.getCcip().getEngineNum());
            ret.put("cipFrameNum", cards.getCcip().getFrameNum());
            ret.put("cipProp", cards.getCcip().getPropTxt());
            ret.put("cipType", cards.getCcip().getCarTypeTxt());
            ret.put("cipSeatCount", cards.getCcip().getSeatCount());
            ret.put("policyNum","");
            ret.put("reportTel","");
            list.add(ret);
        }
    }


    /**
     * 被投保人年龄检查
     * @param cardsIp
     * @param cards
     */
    public void ipAgeCheck(CardsIp cardsIp,Cards cards){
        Cards _cards=Cards.dao.findByCode(cards.getCode());
        Cardtype cardtype=Cardtype.dao.findById(_cards.getCtId());
        String ipAgeTopYear=cardtype.getIpAgeToplmt();
        String ipAgeTopDay=cardtype.getIpAgeToplmtDay();
        String ipAgeLowerYear=cardtype.getIpAgeLowerlmt();

        Date birthDay=cardsIp.getBirthDay();

        long birthDays=DateUtil.betweenDay(birthDay,DateUtil.date(),true);

        long ipAgeTopYear_l=Long.valueOf(ipAgeTopYear)*365;
        long ipAgeTopDay_l=Long.valueOf(ipAgeTopDay);
        long ipAgeLowerYear_l=Long.valueOf(ipAgeLowerYear)*365;

        if(!(birthDays<=ipAgeLowerYear_l&&birthDays>=(ipAgeTopDay_l+ipAgeTopYear_l)))
            throw new CoreException("被投保人年龄不符合保险投保范围");
    }

    /**
     * 投保人年龄检查
     * @param cardsPh
     * @param cards
     */
    public void phAgeCheck(CardsPh cardsPh,Cards cards){
        Cards _cards=Cards.dao.findByCode(cards.getCode());
        Cardtype cardtype=Cardtype.dao.findById(_cards.getCtId());
        String ageTopYear=cardtype.getPhAgeToplmt();
        String ageLowerYear=cardtype.getPhAgeLowerlmt();

        Date birthDay=cardsPh.getBirthDay();

        long birthDays=DateUtil.betweenDay(birthDay,DateUtil.date(),true);

        long ageTopYear_l=Long.valueOf(ageTopYear)*365;
        long ageLowerYear_l=Long.valueOf(ageLowerYear)*365;

        if(!(birthDays<=ageLowerYear_l&&birthDays>=(ageTopYear_l)))
            throw new CoreException("投保人年龄不符合保险投保范围");
    }

}
