package com.yhh.whbx.card.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.yhh.whbx.core.CoreModel;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCardsIp<M extends BaseCardsIp<M>> extends CoreModel<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setRelationship(java.lang.String relationship) {
		set("relationship", relationship);
	}

	public java.lang.String getRelationship() {
		return getStr("relationship");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return getStr("name");
	}

	public void setCertTypeId(java.lang.Integer certTypeId) {
		set("certTypeId", certTypeId);
	}

	public java.lang.Integer getCertTypeId() {
		return getInt("certTypeId");
	}

	public void setBirthDay(java.util.Date birthDay) {
		set("birthDay", birthDay);
	}

	public java.util.Date getBirthDay() {
		return get("birthDay");
	}

	public void setSex(java.lang.String sex) {
		set("sex", sex);
	}

	public java.lang.String getSex() {
		return getStr("sex");
	}

	public void setTel(java.lang.String tel) {
		set("tel", tel);
	}

	public java.lang.String getTel() {
		return getStr("tel");
	}

	public void setBeneficiary(java.lang.String beneficiary) {
		set("beneficiary", beneficiary);
	}

	public java.lang.String getBeneficiary() {
		return getStr("beneficiary");
	}

	public void setCAt(java.util.Date cAt) {
		set("cAt", cAt);
	}

	public java.util.Date getCAt() {
		return get("cAt");
	}

	public void setIdCard(java.lang.String idCard) {
		set("idCard", idCard);
	}

	public java.lang.String getIdCard() {
		return getStr("idCard");
	}

	public void setCardsId(java.lang.Integer cardsId) {
		set("cardsId", cardsId);
	}

	public java.lang.Integer getCardsId() {
		return getInt("cardsId");
	}

}
