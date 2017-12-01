package com.yhh.whbx.admin.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.yhh.whbx.core.CoreModel;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseRes<M extends BaseRes<M>> extends CoreModel<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return getStr("name");
	}

	public void setUrl(java.lang.String url) {
		set("url", url);
	}

	public java.lang.String getUrl() {
		return getStr("url");
	}

	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	public java.lang.String getDescription() {
		return getStr("description");
	}

	public void setPid(java.lang.Integer pid) {
		set("pid", pid);
	}

	public java.lang.Integer getPid() {
		return getInt("pid");
	}

	public void setSeq(java.lang.Integer seq) {
		set("seq", seq);
	}

	public java.lang.Integer getSeq() {
		return getInt("seq");
	}

	public void setLogged(java.lang.String logged) {
		set("logged", logged);
	}

	public java.lang.String getLogged() {
		return getStr("logged");
	}

}
