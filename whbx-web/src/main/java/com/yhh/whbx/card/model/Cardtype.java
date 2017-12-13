package com.yhh.whbx.card.model;

import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Attachment;
import com.yhh.whbx.admin.model.Taxonomy;
import com.yhh.whbx.card.model.base.BaseCardtype;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Cardtype extends BaseCardtype<Cardtype> {
	public static final Cardtype dao = new Cardtype().dao();

	@Override
	public String getTableName() {
		return "b_cardType";
	}

	public String getStatusTxt(){
		return StrUtil.isNotBlank(getStatus())?getStatus().equals(Consts.STATUS.enable.getVal())?Consts.STATUS.enable.getValTxt():Consts.STATUS.forbidden.getValTxt():"";
	}
	public String getTypeTxt(){
		return getType()!=null?((Taxonomy)CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),getType().toString()))!=null?((Taxonomy)CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),getType().toString())).getTitle():"":"";
	}
	public String getCategoryTxt(){
		return getCategory()!=null?((Taxonomy)CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),getCategory().toString()))!=null?((Taxonomy)CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),getCategory().toString())).getTitle():"":"";
	}
	public List<Attachment> getClauseList(){
		return Attachment.dao.findByObjId(getId().intValue());
	}

	public List<Cardtype> findEnableList(){
		return dao.find("select * from "+getTableName()+" where dAt is null and status=?", Consts.STATUS.enable.getVal());
	}


}
