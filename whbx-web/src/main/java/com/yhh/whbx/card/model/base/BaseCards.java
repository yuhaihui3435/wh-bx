package com.yhh.whbx.card.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import com.yhh.whbx.core.CoreModel;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCards<M extends BaseCards<M>> extends CoreModel<M> implements IBean {

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

	public void setPwd(String pwd) {
		set("pwd", pwd);
	}

	public String getPwd() {
		return getStr("pwd");
	}

	public void setAct(String act) {
		set("act", act);
	}

	public String getAct() {
		return getStr("act");
	}

	public void setActAt(java.util.Date actAt) {
		set("actAt", actAt);
	}

	public java.util.Date getActAt() {
		return get("actAt");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getStatus() {
		return getStr("status");
	}

	public void setApplyId(Integer applyId) {
		set("applyId", applyId);
	}

	public Integer getApplyId() {
		return getInt("applyId");
	}

}