package com.yhh.whbx.card.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import com.yhh.whbx.core.CoreModel;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDepot<M extends BaseDepot<M>> extends CoreModel<M> implements IBean {

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

	public void setCardtypeId(Integer cardtypeId) {
		set("cardtypeId", cardtypeId);
	}

	public Integer getCardtypeId() {
		return getInt("cardtypeId");
	}

	public void setCardapplyId(Integer cardapplyId) {
		set("cardapplyId", cardapplyId);
	}

	public Integer getCardapplyId() {
		return getInt("cardapplyId");
	}

	public void setBNum(Integer bNum) {
		set("bNum", bNum);
	}

	public Integer getBNum() {
		return getInt("bNum");
	}

	public void setENum(Integer eNum) {
		set("eNum", eNum);
	}

	public Integer getENum() {
		return getInt("eNum");
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

	public void setOper(Integer oper) {
		set("oper", oper);
	}

	public Integer getOper() {
		return getInt("oper");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getStatus() {
		return getStr("status");
	}

	public void setOutStatus(String outStatus) {
		set("outStatus", outStatus);
	}

	public String getOutStatus() {
		return getStr("outStatus");
	}

	public void setSalesmenId(Integer salesmenId) {
		set("salesmenId", salesmenId);
	}

	public Integer getSalesmenId() {
		return getInt("salesmenId");
	}

	public void setOutTime(java.util.Date outTime) {
		set("outTime", outTime);
	}

	public java.util.Date getOutTime() {
		return get("outTime");
	}

}
