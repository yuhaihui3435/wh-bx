package com.yhh.whbx.card.model;


import com.yhh.whbx.card.model.base.BaseCards;

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
}