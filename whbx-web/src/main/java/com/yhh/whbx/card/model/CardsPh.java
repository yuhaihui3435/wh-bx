package com.yhh.whbx.card.model;


import com.yhh.whbx.admin.model.Taxonomy;
import com.yhh.whbx.card.model.base.BaseCardsPh;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class CardsPh extends BaseCardsPh<CardsPh> {
	public static final CardsPh dao = new CardsPh().dao();

	@Override
	public String getTableName() {
		return "b_cards_ph";
	}

	public String getSexTxt(){
		return getSex().equals("1")?"男":"女";
	}

	public String getCertTypeTxt(){
		return Taxonomy.dao.findById(getCertTypeId()).getTitle();
	}

//	public Cards getCards(){
//		return Cards.dao.findByCardId(getCardsId());
//	}


}
