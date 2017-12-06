package com.yhh.whbx.admin.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseMapping<M extends BaseMapping<M>> extends Model<M> implements IBean {

	public void setId(Long id) {
		set("id", id);
	}

	public Long getId() {
		return getLong("id");
	}

	public void setCid(Long cid) {
		set("cid", cid);
	}

	public Long getCid() {
		return getLong("cid");
	}

	public void setTid(Long tid) {
		set("tid", tid);
	}

	public Long getTid() {
		return getLong("tid");
	}

}
