package com.yhh.whbx.card.model;


import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaoleilu.hutool.crypto.digest.HMac;
import com.xiaoleilu.hutool.crypto.digest.HmacAlgorithm;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.base.BaseCards;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Cards extends BaseCards<Cards> {
	public static final Cards dao = new Cards().dao();

	@Override
	public String getTableName() {
		return "b_cards";
	}

	public Long countByCardapplyId(Integer applyId){
		Record record=Db.findFirst("select count(*) as num from "+getTableName()+" where applyId=?",applyId);
		return record.getLong("num");
	}



	public List<Cards> findByCardapplyId(Integer applyId){
		return dao.find("select *  from "+getTableName()+" where applyId=?",applyId);
	}

	public void updateStatusByCardapplyId(Integer applyId){
		Db.batchUpdate(findByCardapplyId(applyId),50);
	}
	//统计未出库数量
	public Long countByCardapplyIdAndInDepot(Integer applyId){
		Record record=Db.findFirst("select count(*) as num from "+getTableName()+" where applyId=?  and depotId is null",applyId);
		return record.getLong("num");
	}

	public void updateDepotStatusByCardapplyIdAndBnumAndEnum(Integer applyId,int bNum,int eNum,Integer depotId){
		List<Cards> list=findByCardapplyId(applyId);
		Cardapply cardapply=Cardapply.dao.findById(applyId);
		Cardtype cardtype=Cardtype.dao.findById(cardapply.getCardtypeId());
		Integer maxCardcount = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "createCardMaxCount") != null ? Integer.parseInt((String) CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "createCardMaxCount")) : null;
		maxCardcount = maxCardcount == null ? 99999 : maxCardcount;
		int y = (maxCardcount + "").length();
		String code=null;
		Cards cards=null;
		for (int i=bNum;i<=eNum;i++){
			code=cardtype.getCode() + cardapply.getBatch() + StrUtil.fillBefore(i + "", '0', y);
			cards=findFristByPropEQ("code",code);
			if(cards==null)
				continue;
			cards.setDepotId(depotId);
			cards.update();
		}
	}

	public Cards findByCode(String code){
		SqlPara sqlPara=Db.getSqlPara("cards.findByCode", Kv.by("code",code));
		return dao.findFirst(sqlPara);
	}

	public Cards findByCardId(Integer cardId){
		SqlPara sqlPara=Db.getSqlPara("cards.findByCardId", Kv.by("cardId",cardId));
		return dao.findFirstByCache("cards","id_"+cardId,sqlPara.getSql());
	}

	public Long findLastCardCodeByCardapplyId(Integer cardapplyId){
		Record record=Db.findFirst("select min(seq) as seq from "+getTableName()+" where applyId=? and depotId is null ",cardapplyId);
		return record.getLong("seq")==null?0L:record.getLong("seq");
	}

	public Long findEnableCardCount(Integer cardapplyId){
		Record record=Db.findFirst("select count(id) as num from "+getTableName()+" where applyId=? and depotId is null",cardapplyId);
		return record.getLong("num")==null?0L:record.getLong("num");
	}

	public void updateByDepotIdAndSeq(Integer depotId,int seq,Integer unlockId){
		Db.update("update "+getTableName()+" set unlockId=?,isLocked='0' where depotId=? and seq=?",unlockId,depotId,seq);
	}

	public int countUnlockByDepotId(Integer depotId){
		return dao.find("select * from "+getTableName()+" where depotId=? and unlockId is not null and isLocked='0'",depotId).size();
	}


	/**
	 *
	 *
	 * 关联查询其他表的字段
	 *
	 */


	public String getCardtypeName() {
		return this.get("cardtypeName","");
	}

	public String getBatch(){
		return this.get("batch","");
	}

	public String getSalesmenName(){
		return this.get("salesmenName","");
	}

	public String getFaceVal(){
		return this.getBigDecimal("faceVal")==null?"":this.getBigDecimal("faceVal").toPlainString();
	}

	public String getOutStatus(){
		return this.get("outStatus","");
	}
	public Integer getCtId(){
		return this.getInt("ctId");
	}
	public String getCardtype(){
		return this.get("cardtype","");
	}
	public String getProtocol(){
		return this.get("protocol","");
	}
	public String getPeopleCount(){
		return this.get("peopleCount","1");
	}
	public String getManyPeople(){
		return this.get("manyPeople","1");
	}

	public boolean get_disabled(){
		return StrUtil.isBlank(getExportCode())?false:true;
	}

	public Integer getCardtypeType(){
		return this.getInt("cardtypeType");
	}
	public String getCardtypeTypeTxt(){
		return  this.get("cardtypeTypeTxt");
	}

	public CardsCarPh getCcph(){
		return (getCardtypeTypeTxt()!=null&&getCardtypeTypeTxt().equals("driverInsurance"))?CardsCarPh.dao.findFristByPropEQ("cardsId",getId()):null;
	}

	public CardsPh getCph(){
		return (getCardtypeTypeTxt()!=null&&getCardtypeTypeTxt().equals("accidentInsurance"))?CardsPh.dao.findFristByPropEQ("cardsId",getId()):null;
	}

	public CardsCarIp getCcip(){
		return (getCardtypeTypeTxt()!=null&&getCardtypeTypeTxt().equals("driverInsurance"))?CardsCarIp.dao.findFristByPropEQ("cardsId",getId()):null;
	}

	public List<CardsIp> getCip(){
		return (getCardtypeTypeTxt()!=null&&getCardtypeTypeTxt().equals("accidentInsurance"))?CardsIp.dao.findByPropEQ("cardsId",getId()):null;
	}

	public String getQt(){
		byte[]  key=getPwd().getBytes();
		HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
		return mac.digestHex(getCode());
	}


}
