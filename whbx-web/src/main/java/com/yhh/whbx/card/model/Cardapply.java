package com.yhh.whbx.card.model;


import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Taxonomy;
import com.yhh.whbx.admin.model.User;
import com.yhh.whbx.card.model.base.BaseCardapply;
import com.yhh.whbx.kits.DateKit;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Cardapply extends BaseCardapply<Cardapply> {
	public static final Cardapply dao = new Cardapply().dao();

	@Override
	public String getTableName() {
		return "b_cardapply";
	}

	public List<Cardapply> findAllNotDel(){
		return dao.find("select * from "+getTableName()+" where dAt is null");
	}

	public List<Cardapply> findByBatchAndCardtype(Integer ct,String batch){
		return dao.find("select * from "+getTableName()+" where batch=? and cardtypeId=?",batch,ct);
	}

	public List<Cardapply> findByBatchAndCardtypeAndNEId(Integer ct,String batch,Long id){
		return dao.find("select * from "+getTableName()+" where batch=? and cardtypeId=? and id <>?",batch,ct,id);
	}

	public String getCardtypeTxt(){
		return Cardtype.dao.findById(getCardtypeId()).getName();
	}

	public String getCardtypeTypeTxt(){
		return Cardtype.dao.findById(getCardtypeId()).getTypeTxt();
	}

	public String getMediaTxt(){
		Taxonomy taxonomy=null;
		if(getMedia()!=null)
		 taxonomy=(Taxonomy)CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),getMedia().toString());
		return getMedia()==null?"":taxonomy!=null?taxonomy.getTitle():"";
	}

	public String getCheckStatusTxt(){
		return StrUtil.isNotBlank(getCheckStatus())?getCheckStatus().equals(Consts.CHECK_STATUS.normal.getVal())? Consts.CHECK_STATUS.normal.getValTxt():getCheckStatus().equals(Consts.CHECK_STATUS.revokeCheck.getVal())? Consts.CHECK_STATUS.revokeCheck.getValTxt(): Consts.CHECK_STATUS.waitingCheck.getValTxt():"";
	}

	public String getStatusTxt(){
		return StrUtil.isNotBlank(getStatus())? Consts.STATUS.enable.getVal().equals(getStatus())? Consts.STATUS.enable.getValTxt(): Consts.STATUS.forbidden.getValTxt():"";
	}

	public String exeCardTxt(){
		return StrUtil.isNotBlank(getExeCard())?Consts.YORN_STR.yes.getVal().equals(getExeCard())? Consts.YORN_STR.yes.getLabel(): Consts.YORN_STR.no.getLabel():"";
	}

	public String operTxt(){
		return getOper()!=null?User.dao.findById(getOper()).getNickname():"";
	}

	public String checkOperTxt(){
		return getCheckOper()!=null?User.dao.findById(getCheckOper()).getNickname():"";
	}


	public String getCAtTxt(){
		return getCAt()==null?"": DateKit.dateToStr(getCAt(),DateKit.STR_DATEFORMATE);
	}

	public List<Cardapply> findByCardtypeIdAndBatch(Integer cardtypeId,String batch){
		Kv kv=Kv.by("cardtypeId=",cardtypeId);
		if(StrUtil.isNotBlank(batch)){
			kv.put("batch like",batch);
		}
		SqlPara sqlPara = Db.getSqlPara("cardapply.findEnableList", Kv.by("cond", kv));
		return Cardapply.dao.find( sqlPara);
	}
}
