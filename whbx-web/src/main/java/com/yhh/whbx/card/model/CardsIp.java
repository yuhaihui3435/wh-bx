package com.yhh.whbx.card.model;


import com.yhh.whbx.admin.model.Taxonomy;
import com.yhh.whbx.card.CardsService;
import com.yhh.whbx.card.model.base.BaseCardsIp;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class CardsIp extends BaseCardsIp<CardsIp> {
	public static final CardsIp dao = new CardsIp().dao();

	@Override
	public String getTableName() {
		return "b_cards_ip";
	}

	public String getSexTxt(){
		return getSex().equals("1")?"男":"女";
	}

	public String getCertTypeTxt(){
		return Taxonomy.dao.findById(getCertTypeId()).getTitle();
	}

	public String getJobTxt(){
		return getJob()!=null?InsuranceOcc.dao.findById(getJob()).getName():"";
	}

	public String getRelationshipTxt(){
		return CardsService.relationships.get(getRelationship());
	}
}
