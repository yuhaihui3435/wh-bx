package com.yhh.whbx.card;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.xiaoleilu.hutool.crypto.SecureUtil;
import com.xiaoleilu.hutool.date.DatePattern;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.lang.Base64;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.*;
import com.yhh.whbx.card.query.CardsCtr;
import com.yhh.whbx.card.type.CardTypeService;
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

        Cardapply cardapply=Cardapply.dao.findById(cards.getApplyId());
        Cardtype cardtype=Cardtype.dao.findById(cardapply.getCardtypeId());
        if(cardtype!=null&&cardtype.getStatus().equals(Consts.YORN_STR.no.getVal())){
            throw new CoreException("此类型卡已经停用，请联系销售人员");
        }
        if(cards.getDepotId()==null){
            throw new CoreException("此卡未出库，请联系销售人员");
        }

        if(cards.getIsLocked().equals(Consts.YORN_STR.no.getVal())){
            throw new CoreException("此卡被锁定，请联系销售人员");
        }

//        Integer applyId=cards.getApplyId();
//        cardapply=Cardapply.dao.findById(new Long(applyId));
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
        relationships.put("父亲","00");
        relationships.put("母亲","01");
        relationships.put("儿子","02");
        relationships.put("女儿","03");
        relationships.put("配偶","04");
        relationships.put("本人","05");
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
            CardsPh cardsPh=cards.getCph();
            for (CardsIp cardsIp:cardsIpList){
                ret=new LinkedHashMap();

                ret.put("cardsCode",cards.getCode());
                ret.put("phName", cardsPh.getName());
                ret.put("phCertType",cardsPh.getCertTypeTxt());
                ret.put("phIdNum", cardsPh.getIdCard());
                ret.put("phBirth", DateUtil.format(cardsPh.getBirthDay(), DatePattern.NORM_DATE_PATTERN));
                ret.put("phSex",cardsPh.getSexTxt());
                ret.put("phTel", cardsPh.getTel());
                ret.put("phEmail",cardsPh.getEmail());
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
                ret.put("bAt","");
                ret.put("eAt","");
                ret.put("salesmenName",cards.getSalesmenName());
                list.add(ret);
            }
        }else if(cards.getCardtypeTypeTxt().equals("driverInsurance")){
            ret=new LinkedHashMap();
            CardsCarPh carPh=cards.getCcph();
            CardsCarIp carIp=cards.getCcip();
            ret.put("cardsCode",cards.getCode());
            ret.put("cphType", carPh.getTypeTxt());
            ret.put("cphName", carPh.getName());
            ret.put("cphCertType", carPh.getCertTypeTxt());
            ret.put("cphIdNum", carPh.getCertCode());
            ret.put("cphLinkName", carPh.getLinkName());
            ret.put("cphTel", carPh.getTel());
            ret.put("cphEmail", carPh.getEmail());
            ret.put("cphAddress", carPh.getFullAddress());
            ret.put("cipPlateNum", carIp.getPlateNum());
            ret.put("cipEngineNum", carIp.getEngineNum());
            ret.put("cipFrameNum", carIp.getFrameNum());
            ret.put("cipProp", carIp.getPropTxt());
            ret.put("cipType", carIp.getCarTypeTxt());
            ret.put("cipSeatCount", carIp.getSeatCount());
            ret.put("policyNum","");
            ret.put("reportTel","");
            ret.put("bAt","");
            ret.put("eAt","");
            ret.put("salesmenName",cards.getSalesmenName());
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

    public void savePhAndIp(Cards cards,CardsPh cardsPh,CardsIp cardsIp){
        cards.setAct(Consts.YORN_STR.yes.getVal());
        cards.setActAt(new Date());
        cards.update();
        cardsPh.setCardsId(cards.getId().intValue());
        cardsPh.save();
        cardsIp.setCardsId(cards.getId().intValue());
        cardsIp.save();
    }
    @Before(Tx.class)
    public void batchSavePhAndIp(List<CardsCtr.DTO> dtoList){
        for (CardsCtr.DTO dto:dtoList){
            savePhAndIp(dto.getCards(),dto.getCardsPh(),dto.getCardsIp());
        }
    }

    @Before(Tx.class)
    public void batchSaveCarPhAndIp(List<CardsCtr.DTO01> list){
           for (CardsCtr.DTO01 dto01:list){
               saveCarPhAndIp(dto01.getCards(),dto01.getCardsCarPh(),dto01.getCardsCarIp());
           }
    }

    public void saveCarPhAndIp(Cards cards,CardsCarPh cardsPh,CardsCarIp cardsIp){
        cards.setAct(Consts.YORN_STR.yes.getVal());
        cards.setActAt(new Date());
        cards.update();
        cardsPh.setCardsId(cards.getId().intValue());
        cardsPh.save();
        cardsIp.setCardsId(cards.getId().intValue());
        cardsIp.save();
    }




}
