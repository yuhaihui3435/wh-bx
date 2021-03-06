package com.yhh.whbx.www;

import com.aliyuncs.exceptions.ClientException;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Duang;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Attachment;
import com.yhh.whbx.admin.model.Content;
import com.yhh.whbx.card.CardsService;
import com.yhh.whbx.card.model.*;
import com.yhh.whbx.card.type.CardTypeService;
import com.yhh.whbx.core.CoreController;
import com.yhh.whbx.core.CoreException;
import com.yhh.whbx.interceptors.AdminIAuthInterceptor;
import com.yhh.whbx.interceptors.CardStatusCheckInterceptor;
import com.yhh.whbx.kits.ALSMSKit;
import com.yhh.whbx.kits.DateKit;
import com.yhh.whbx.kits.ResKit;

import java.util.*;

/**
 * Created by yuhaihui8913 on 2017/12/26.
 */
@Clear(AdminIAuthInterceptor.class)
public class IndexCtr extends CoreController {
    private final static CardsService cardsService = Duang.duang(CardsService.class);
    private final static CardTypeService cardTypeService = Duang.duang(CardTypeService.class);

    public void index() {
        List<Content> contents=Content.dao.find("select sc.* from  s_content sc left join s_mapping sm on sc.id=sm.cid where dAt is null and module='art' and flag='00' and tid=437 order by pAt desc limit 3");
        List<Content> products=Content.dao.find("select sc.* from  s_content sc left join s_mapping sm on sc.id=sm.cid where dAt is null and module='art' and flag='00' and tid=389 order by pAt desc limit 8");
        Content lastNotice=Content.dao.findFirst("select sc.* from s_content sc left join s_mapping sm on sc.id=sm.cid where dAt is null and tid=392 order by pAt desc limit 1");
        setAttr("pInfoList",contents);
        setAttr("lastNotice",lastNotice);
        setAttr("products",products);
        render("index.html");
    }
//    @Before({CardStatusCheckInterceptor.class})
//    public void actCard(){
//        String code=getPara("code");
//        String pwd=getPara("pwd");
//        try {
//            Cards cards = cardsService.checkCard(code, pwd);
//
//            if(StrUtil.isNotBlank(cards.getAct())&&cards.getAct().equals(Consts.YORN_STR.yes.getVal())){
//                renderSuccessJSON("卡已经激活");
//                return;
//            }
//
//
//            renderSuccessJSON("卡激活成功,请补充详细信息");
//            return;
//        }catch (CoreException e){
//            renderFailJSON(e.getMsg());
//            return;
//        }
//    }

    @Before({CardStatusCheckInterceptor.class})
    public void enterDetail() {
        String code = getPara("code");
        String pwd = getPara("pwd");
        keepPara(code,pwd);
        Cards cards = null;
        try {
            cards = cardsService.checkCard(code, pwd);
        } catch (CoreException e) {
            throw e;
        }

        if(cards.getAct().equals(Consts.YORN_STR.yes.getVal())){
            throw  new CoreException("卡片已经激活，请直接查询保单详细");

        }

        //code="010100201";
        cards = Cards.dao.findByCode(code);
//        if(cards.getAct()==null||!cards.getAct().equals(Consts.YORN_STR.yes.getVal())){
//            renderFailJSON("卡片未被激活，请先激活卡片");
//            return;
//        }


        setAttr("card", cards);
        Integer cardtypeId = cards.getCtId();

        //意外险
        if (cards.getCardtype().equals("accidentInsurance")) {
            List<CardsPh> cardsPhs = CardsPh.dao.findByPropEQ("cardsId", cards.getId());
            if (cardsPhs.size() > 0) {
                setAttr("cardsPh", cardsPhs.get(0));
                setAttr("cardsIpList", CardsIp.dao.findByPropEQ("cardsId", cards.getId()));
                render("card_00/view.html");
            } else {
                Cardtype cardtype = Cardtype.dao.findById(cardtypeId);
                if (cardtype != null && cardtype.getCategory() != null) {
                    setAttr("cardtype", cardtype);
                    setAttr("ioList", InsuranceOcc.dao.findFirstLevel(cardtype.getCategoryCode()));
                }
                setAttr("certTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "certTypeList"));
                render("card_00/main.html");
            }
        }
        //车险
        else if (cards.getCardtype().equals("driverInsurance")) {
            CardsCarIp cardsCarIp = CardsCarIp.dao.findFristByPropEQ("cardsId", cards.getId());
            if (cardsCarIp != null) {
                setAttr("cardsCarIp", cardsCarIp);
                setAttr("cardsCarPh", CardsCarPh.dao.findFristByPropEQ("cardsId", cards.getId()));
                render("card_01/view.html");
            } else {
                Cardtype cardtype = Cardtype.dao.findById(cardtypeId);
                setAttr("cardtype", cardtype);
                setAttr("certTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "certTypeList"));
                setAttr("companyCertTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "companyCertTypeList"));
                setAttr("carTypeList", CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(), "carTypeList"));
                render("card_01/main.html");
            }
        } else {
            throw new CoreException("卡信息错误，请联系业务员");
        }


    }

    @Before({CardStatusCheckInterceptor.class, Tx.class})
    public void saveCardInfo() {
        CardsPh cardsPh = getModel(CardsPh.class, "ph", true);
        Cards cards = Cards.dao.findById(cardsPh.getCardsId());
        Cardtype cardtype=cardsService.getCardtypeByCardcode(cards.getCode());



        cards.setAct(Consts.YORN_STR.yes.getVal());
        cards.setActAt(new Date());
        cards.update();
        List<CardsPh> cardsPhs = CardsPh.dao.findByPropEQ("cardsId", cards.getId());

        if (cardsPhs.size() > 0) {
            renderFailJSON("保单信息已经录入，如要修改请联系销售人员");
            return;
        }

        if (cards.getAct() == null || cards.getAct().equals(Consts.YORN_STR.no.getVal())) {
            renderFailJSON("卡片未被激活，请先激活卡片");
            return;
        }

        int ipLen = getParaToInt("ipLen", -1);
        List<CardsIp> list = new ArrayList<>();
        CardsIp cardsIp = null;
        for (int i = 0; i < ipLen; i++) {
            cardsIp = getModel(CardsIp.class, "ip" + i, true);
            cardsService.ipAgeCheck(cardsIp,cards);
            cardTypeService.actCountCheck(cardsIp.getIdCard(),cardtype.getId());
            list.add(cardsIp);
        }
        cardsService.phAgeCheck(cardsPh,cards);
        cardsPh.save();
        Db.batchSave(list, 10);
        try {
            String ret = ALSMSKit.sendSMS(ResKit.getConfig("sms.anbao.card.act.signname"), ResKit.getConfig("sms.anbao.card.act.templatecode"), cardsPh.getTel(), "{\"code\":\"" + cards.getCode() + "\",\"person\":\""+cardsPh.getName()+"\"}");
            if (!ret.equals(Consts.YORN_STR.yes.name()))
                LogKit.error("卡激活短信通知失败");

        } catch (ClientException e) {
            LogKit.error("卡激活短信通知失败>>" + e.getMessage());
        }

        renderSuccessJSON(cardsService.getCardtypeByCardcode(cards.getCode()).getActMsg());

    }

    @Before({CardStatusCheckInterceptor.class, Tx.class})
    public void saveCardCarInfo() {
        CardsCarPh cardsCarPh=getModel(CardsCarPh.class,"",true);
        CardsCarIp cardsCarIp=getModel(CardsCarIp.class,"",true);
        Integer cardsId=getParaToInt("cardsId");
        Integer carType=getParaToInt("carType");
        cardsCarIp.setType(carType);
        String cardsCode=getPara("code");
        cardsCarIp.setCardsId(cardsId);
        cardsCarPh.setCardsId(cardsId);
        Cards cards=Cards.dao.findFristByPropEQ("code",cardsCode);
        cards.setAct(Consts.YORN_STR.yes.getVal());
        cards.setActAt(new Date());

        if(CardsCarIp.dao.findFristByPropEQ("cardsId",cardsId)!=null){
            renderFailJSON("此卡疑似已经被激活");
            return;
        }

        if(CardsCarPh.dao.findFristByPropEQ("cardsId",cardsId)!=null){
            renderFailJSON("此卡疑似已经被激活");
            return;
        }

        cards.update();
        cardsCarIp.setCAt(new Date());
        cardsCarPh.setCAt(new Date());
        cardsCarIp.save();
        cardsCarPh.save();
        try {
            String ret = ALSMSKit.sendSMS(ResKit.getConfig("sms.anbao.card.act.signname"), ResKit.getConfig("sms.anbao.card.act.templatecode"), cardsCarPh.getTel(), "{\"code\":\"" + cards.getCode() + "\",\"person\":\""+cardsCarPh.getName()+"\"}");
            if (!ret.equals(Consts.YORN_STR.yes.name()))
                LogKit.error("卡激活短信通知失败");

        } catch (ClientException e) {
            LogKit.error("卡激活短信通知失败>>" + e.getMessage());
        }
        renderSuccessJSON(cardsService.getCardtypeByCardcode(cards.getCode()).getActMsg());
    }


    public void viewDetail() {
        String code = getPara("code");
        String pwd = getPara("pwd");
        Integer cardsId = null;
        Cards cards = null;
        try {
            cards = cardsService.checkCard(code, pwd);
            cardsId = cards.getId().intValue();
        } catch (CoreException e) {
            throw e;
        }
        cards = Cards.dao.findByCode(code);
        if (cards.getAct() == null || !cards.getAct().equals(Consts.YORN_STR.yes.getVal())) {
            throw new CoreException("此卡未激活，请激活后进行查看");
        }
        List<Attachment> attachments = null;
        if (cards.getCardtype().equals("accidentInsurance")) {
            CardsPh cardsPh = CardsPh.dao.findFristByPropEQ("cardsId", cardsId);
            List<CardsIp> cardsIpList = CardsIp.dao.findByPropEQ("cardsId", cardsId);
            attachments = Attachment.dao.findByObjIdAndModule(cardsId, "cards");
            setAttr("cardsPh", cardsPh);
            setAttr("cardsIpList", cardsIpList);
            setAttr("electronicPolicy", attachments);
            setAttr("card", cards);
            render("card_00/view.html");
        } else if (cards.getCardtype().equals("driverInsurance")) {
            CardsCarPh cardsCarPh = CardsCarPh.dao.findFristByPropEQ("cardsId", cardsId);
            CardsCarIp cardsCarIp = CardsCarIp.dao.findFristByPropEQ("cardsId", cardsId);
            attachments = Attachment.dao.findByObjIdAndModule(cardsId, "cards");
            setAttr("cardsCarPh", cardsCarPh);
            setAttr("cardsCarIp", cardsCarIp);
            setAttr("electronicPolicy", attachments);
            setAttr("card", cards);
            render("card_01/view.html");
        }

    }

    public void viewDetail_special() {
        String qt = getPara(0);
        String code = getPara(1);
        Integer cardsId = null;
        Cards cards = null;
        if(StrUtil.isBlank(qt))
            throw new CoreException("查询附加码不能为空");
        if(StrUtil.isBlank(code))
            throw  new CoreException("卡号不能为空");

        cards = Cards.dao.findByCode(code);
        if(!qt.equals(cards.getQt()))
            throw new CoreException("查询附加码不正确");

        if (cards.getAct() == null || !cards.getAct().equals(Consts.YORN_STR.yes.getVal())) {
            throw new CoreException("此卡未激活，请激活后进行查看");
        }
        cardsId=cards.getId().intValue();
        List<Attachment> attachments = null;
        if (cards.getCardtype().equals("accidentInsurance")) {
            CardsPh cardsPh = CardsPh.dao.findFristByPropEQ("cardsId", cardsId);
            List<CardsIp> cardsIpList = CardsIp.dao.findByPropEQ("cardsId", cardsId);
            attachments = Attachment.dao.findByObjIdAndModule(cardsId, "cards");
            setAttr("cardsPh", cardsPh);
            setAttr("cardsIpList", cardsIpList);
            setAttr("electronicPolicy", attachments);
            setAttr("card", cards);
            render("card_00/view.html");
        } else if (cards.getCardtype().equals("driverInsurance")) {
            CardsCarPh cardsCarPh = CardsCarPh.dao.findFristByPropEQ("cardsId", cardsId);
            CardsCarIp cardsCarIp = CardsCarIp.dao.findFristByPropEQ("cardsId", cardsId);
            attachments = Attachment.dao.findByObjIdAndModule(cardsId, "cards");
            setAttr("cardsCarPh", cardsCarPh);
            setAttr("cardsCarIp", cardsCarIp);
            setAttr("electronicPolicy", attachments);
            setAttr("card", cards);
            render("card_01/view.html");
        }

    }


    public void cardList() {
        String name = getPara("name");
        String idNo = getPara("idNo");
        if(StrUtil.isBlank(name)){
            throw new CoreException("姓名不能为空");
        }
        if(StrUtil.isBlank(idNo)){
            throw new CoreException("身份证号不能为空");
        }


        List<Cards> cardsPhs=Cards.dao.find("select bc.*,ct.name as cardtypeName,ct.faceVal as faceVal from b_cards bc left join  b_cards_ph bcp on bcp.cardsId=bc.id LEFT JOIN b_cardapply ca on bc.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id where bcp.name=? and bcp.idCard=?  ",name,idNo);

        List<Cards> cardsCarPhs=Cards.dao.find("select bc.*,ct.name as cardtypeName,ct.faceVal as faceVal from b_cards bc left join  b_cards_car_ph bccp on bccp.cardsid=bc.id LEFT JOIN b_cardapply ca on bc.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id  where bccp.name=? and bccp.certCode=?",name,idNo);

        if(cardsCarPhs.isEmpty()&&cardsPhs.isEmpty()){
            throw new CoreException("没有查询到投保数据");
        }


        setAttr("cardsPhs",cardsPhs);
        setAttr("cardsCarPhs",cardsCarPhs);

        render("cardList.html");
    }


    public void occQuery() {
        String insurance = getPara("insurance");
        List<InsuranceOcc> list = null;
        if (isParaExists("occ_0")) {
            String occ_0_code = getPara("occ_0");
            list = InsuranceOcc.dao.findSecLevel(occ_0_code, insurance);
            renderJson(list);
            return;
        } else if (isParaExists("occ_1")) {
            String occ_1_code = getPara("occ_1");
            list = InsuranceOcc.dao.findThirdLevel(occ_1_code, insurance);
            renderJson(list);
            return;
        }
    }

    /**
     *
     * home控制台的数据量统计
     *
     */
    public void homeCountStatistics(){
        //今天申请数量
        Long applyCount=Db.queryLong("select IFNULL(sum(num),0) from b_cardapply where dAt is null and cAt>? and cAt<?", DateKit.getTimeStampBegin(new Date()),DateKit.getTimeStampEnd(new Date()));
        //今天解锁数量
        Long unlock=Db.queryLong("select IFNULL(sum(CAST(eNum AS signed)-CAST(bNum AS signed)+1),0) from b_unlock where  cAt>? and cAt<?", DateKit.getTimeStampBegin(new Date()),DateKit.getTimeStampEnd(new Date()));
        //今天激活数量
        Long actCount=Db.queryLong("select count(id) from b_cards where  act='0' and status='0' and actAt>? and actAt<?", DateKit.getTimeStampBegin(new Date()),DateKit.getTimeStampEnd(new Date()));
        //今天导出数量
        Long exportCount=Db.queryLong("select count(id) from b_cards where  exportCode is not null and status='0' and exportAt>? and exportAt<?", DateKit.getTimeStampBegin(new Date()),DateKit.getTimeStampEnd(new Date()));
        //当前激活未导出数量
        Long actNotExportCount=Db.queryLong("select count(id) from b_cards where  exportCode is null and act='0' and status='0'");
        //前一周开卡数量
        Date lastWeekFirstDay= DateUtil.beginOfWeek(DateUtil.lastWeek());
        Date lastWeekLastDay=DateUtil.endOfDay(DateUtil.offsetDay(lastWeekFirstDay,6));
        //销售开卡量
        List salemanCards=Db.query("select IFNULL(t.count,0) as count,bs.name from  (select IFNULL(sum(CAST(bu.eNum AS signed)-CAST(bu.bNum AS signed)+1),0)as count,bs.name,bs.id as bsId from b_unlock bu left join b_depot bd on bu.depotId=bd.id  left join b_salesmen bs on bd.salesmenId=bs.id where  bu.cAt>=? and bu.cAt<=? group by bd.salesmenId) t right join `b_salesmen` bs on t.bsId=bs.id", lastWeekFirstDay,lastWeekLastDay);


        List lastWeekUnlockCount=Db.query("select IFNULL(t.count,0) as count,DATE_FORMAT(datelist,'%Y-%m-%d') as date from  (select IFNULL(sum(CAST(eNum AS signed)-CAST(bNum AS signed)+1),0) as count,DATE_FORMAT(cAt,'%Y-%m-%d') as date from b_unlock where  cAt>=? and cAt<=?   group by DATE_FORMAT(cAt,'%Y-%m-%d')) t right join calendar on t.date=date(datelist) where  datelist>=? and datelist<=? order by date", lastWeekFirstDay,lastWeekLastDay, lastWeekFirstDay,lastWeekLastDay);

        //前一周卡激活量
        List lastWeekActCount=Db.query("select IFNULL(t.count,0) as count,DATE_FORMAT(datelist,'%Y-%m-%d') as date from  (select count(id) as count,DATE_FORMAT(actAt,'%Y-%m-%d') as date from  b_cards where  act='0' and status='0' and actAt>=? and actAt<=?   group by DATE_FORMAT(actAt,'%Y-%m-%d')) t right join calendar on t.date=date(datelist) where  datelist>=? and datelist<=?  order by date", lastWeekFirstDay,lastWeekLastDay, lastWeekFirstDay,lastWeekLastDay);

        Map<String,Object> ret=new HashMap<>();
        ret.put("applyCount",applyCount);
        ret.put("unlock",unlock);
        ret.put("actCount",actCount);
        ret.put("exportCount",exportCount);
        ret.put("actNotExportCount",actNotExportCount);
        ret.put("salesmanStatistics",salemanCards);
        ret.put("lastWeekUnlockCount",lastWeekUnlockCount);
        ret.put("lastWeekActCount",lastWeekActCount);

        renderJson(ret);
    }

    public void a(){
        Integer id=getParaToInt(0);
        int pn =getParaToInt(1,1);
        Page<Content> contents=Content.dao.paginate(pn,getPS(),"select sc.* ","from s_content sc left join s_mapping sm on sc.id=sm.cid  where dAt is null and module='art' and flag='00' and sm.tid=? order by pAt desc",id);
        setAttr("cList",contents);
        setAttr("cId",id);
        render("list.html");
    }
    public void c(){
        Integer id=getParaToInt(0);
        int pn =getParaToInt(1,1);
        Page<Content> contents=Content.dao.paginate(pn,getPS(),"select sc.* ","from s_content sc left join s_mapping sm on sc.id=sm.cid  where dAt is null and module='art' and flag='00' and sm.tid=? order by pAt desc",id);
        setAttr("cList",contents);
        setAttr("cId",id);
        render("list_without_img.html");
    }

    public void b(){
        String index0=getPara(0);
        Integer id=0;
        //带有a_的第一位索引数据，表示直接显示详细内容
        if(index0.startsWith("a_")){
            String[] strings=index0.split("_");
            id=Integer.parseInt(strings[1]);
        }else {
            id = getParaToInt(0);
            setAttr("closeBtn","y");
        }
        Content content=Content.dao.findById(id);
        setAttr("content",content);

        render("view.html");
    }

    public void d(){
        Integer id=getParaToInt(0);
        int pn =getParaToInt(1,1);
        Content content=Content.dao.findFirst("select sc.* from s_content sc left join s_mapping sm on sc.id=sm.cid  where dAt is null and module='art' and flag='00' and sm.tid=? order by pAt desc limit 1",id);
        setAttr("content",content);
        render("view.html");
    }


    public void toCardAct(){
        render("cardAct.html");
    }

}
