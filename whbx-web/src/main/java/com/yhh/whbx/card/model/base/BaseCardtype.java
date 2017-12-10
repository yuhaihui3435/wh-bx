package com.yhh.whbx.card.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import com.yhh.whbx.core.CoreModel;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCardtype<M extends BaseCardtype<M>> extends CoreModel<M> implements IBean {

	public void setId(Long id) {
		set("id", id);
	}

	public Long getId() {
		return getLong("id");
	}

	public void setCode(String code) {
		set("code", code);
	}

	public String getCode() {
		return getStr("code");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getName() {
		return getStr("name");
	}

	public void setFaceVal(java.math.BigDecimal faceVal) {
		set("faceVal", faceVal);
	}

	public java.math.BigDecimal getFaceVal() {
		return get("faceVal");
	}

	public void setPhAgeToplmt(String phAgeToplmt) {
		set("phAgeToplmt", phAgeToplmt);
	}

	public String getPhAgeToplmt() {
		return getStr("phAgeToplmt");
	}

	public void setPhAgeLowerlmt(String phAgeLowerlmt) {
		set("phAgeLowerlmt", phAgeLowerlmt);
	}

	public String getPhAgeLowerlmt() {
		return getStr("phAgeLowerlmt");
	}

	public void setIpAgeToplmt(String ipAgeToplmt) {
		set("ipAgeToplmt", ipAgeToplmt);
	}

	public String getIpAgeToplmt() {
		return getStr("ipAgeToplmt");
	}

	public void setIpAgeLowerlmt(String ipAgeLowerlmt) {
		set("ipAgeLowerlmt", ipAgeLowerlmt);
	}

	public String getIpAgeLowerlmt() {
		return getStr("ipAgeLowerlmt");
	}

	public void setManyPeople(String manyPeople) {
		set("manyPeople", manyPeople);
	}

	public String getManyPeople() {
		return getStr("manyPeople");
	}

	public void setPeopleCount(String peopleCount) {
		set("peopleCount", peopleCount);
	}

	public String getPeopleCount() {
		return getStr("peopleCount");
	}

	public void setFiniteEffect(String finiteEffect) {
		set("finiteEffect", finiteEffect);
	}

	public String getFiniteEffect() {
		return getStr("finiteEffect");
	}

	public void setActCount(String actCount) {
		set("actCount", actCount);
	}

	public String getActCount() {
		return getStr("actCount");
	}

	public void setCategory(Integer category) {
		set("category", category);
	}

	public Integer getCategory() {
		return getInt("category");
	}

	public void setType(Integer type) {
		set("type", type);
	}

	public Integer getType() {
		return getInt("type");
	}

	public void setProtocol(String protocol) {
		set("protocol", protocol);
	}

	public String getProtocol() {
		return getStr("protocol");
	}

	public void setActMsg(String actMsg) {
		set("actMsg", actMsg);
	}

	public String getActMsg() {
		return getStr("actMsg");
	}

	public void setServiceCert(String serviceCert) {
		set("serviceCert", serviceCert);
	}

	public String getServiceCert() {
		return getStr("serviceCert");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getStatus() {
		return getStr("status");
	}

	public void setCAt(java.util.Date cAt) {
		set("cAt", cAt);
	}

	public java.util.Date getCAt() {
		return get("cAt");
	}

	public void setDAt(java.util.Date dAt) {
		set("dAt", dAt);
	}

	public java.util.Date getDAt() {
		return get("dAt");
	}

	public void setOper(java.lang.Integer oper) {
		set("oper", oper);
	}

	public java.lang.Integer getOper() {
		return getInt("oper");
	}

}
