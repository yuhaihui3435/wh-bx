package com.yhh.whbx.card.model;


import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.User;
import com.yhh.whbx.card.model.base.BaseDepot;
import com.yhh.whbx.sale.model.Salesmen;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Depot extends BaseDepot<Depot> {
	public static final Depot dao = new Depot().dao();

	@Override
	public String getTableName() {
		return "b_depot";
	}

	public List<Depot> findByCodeAndNeId(String code, Long id){
		return dao.find("select * from "+getTableName()+" where code =? and id<>?",code,id);
	}


	public String getCardtypeTxt(){
		return Cardtype.dao.findById(getCardtypeId()).getName();
	}

	public String getBatch(){
		return Cardapply.dao.findById(getCardapplyId()).getBatch();
	}

	public String getSalesmenTxt(){
		return getSalesmenId()!=null?Salesmen.dao.findById(getSalesmenId()).getName():"无";
	}

	public String getStatusTxt(){
		return getStatus().equals(Consts.STATUS.forbidden.getVal())? Consts.STATUS.forbidden.getValTxt(): Consts.STATUS.enable.getValTxt();
	}

	public String getOutstatusTxt(){
		return getOutStatus().equals(Consts.YORN_STR.no.getVal())? Consts.YORN_STR.no.getLabel(): Consts.YORN_STR.yes.getLabel();
	}

	public String getOperTxt(){
		return getOper()!=null? User.dao.findById(getOper()).getNickname():"无";
	}


}
