package com.yhh.whbx.admin.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.log.StaticLog;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.base.BaseRes;

import java.math.BigInteger;
import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Res extends BaseRes<Res> {
	public static final Res dao = new Res().dao();
	public  String listTree(List<Long> roleResIds){

		List<Res> resList;
		resList = dao.find("select * from s_res  where pId=0 order by seq");
		JSONArray ja=new JSONArray();
		JSONObject jo;
		for(Res res:resList){
			jo=new JSONObject();
			jo.put("id",res.getId());
			jo.put("pId",res.getPid());
			jo.put("title",res.getName());
			jo.put("expand",true);
			jo.put("url",res.getUrl());
			jo.put("seq",res.getSeq());
			jo.put("checked",(roleResIds!=null&&roleResIds.contains(res.getId()))?true:false);
			jo.put("description",res.getDescription());
			jo.put("logged",res.getLogged());
			dao.getChildren(jo,roleResIds);
			ja.add(jo);
		}
		StaticLog.info(ja.toJSONString());
		return ja.toJSONString();
	}

	public  void getChildren(JSONObject jsonObject,List<Long> roleResIds){
		int id=jsonObject.getIntValue("id");
		List<Res> list=dao.find("select * from s_res where pId=? order by seq",id);

		if(!list.isEmpty()){
			JSONArray ja=new JSONArray();
			JSONObject jo;
			for(Res res:list){
				jo=new JSONObject();
				jo.put("id",res.getId());
				jo.put("pId",res.getPid());
				jo.put("title",res.getName());
				jo.put("expand",true);
				jo.put("url",res.getUrl());
				jo.put("seq",res.getSeq());
				jo.put("checked",(roleResIds!=null&&roleResIds.contains(res.getId()))?true:false);
				jo.put("description",res.getDescription());
				jo.put("logged",res.getLogged());
				ja.add(jo);
				dao.getChildren(jo,roleResIds);
			}
			jsonObject.put("children",ja);
		}

	}


	public List<Res> getChildren(){
		List<Res> list=dao.find("select * from s_res where pId=? order by seq",getId());
		return list;
	}

	public List<Res> findResesByUserId(BigInteger userId){
		List<Role> roleList=Role.dao.findRolesByUserId(userId);
		int i=0;
		String p=null;
		for(Role r:roleList){
			if(p==null)
				p=r.getId().toString();
			else
				p=p+","+r.getId().toString();
		}

		List<Res> list= Res.dao.findByCache(Consts.CACHE_NAMES.userReses.name(),userId,"select r.* from s_res r left join s_role_res rr on r.id=rr.resId  where rr.roleId in (?) and r.pid=0 order by r.seq",p);
		return list;
	}

	public List<Res> findSecResesByUserId(BigInteger userId,long rId){

		List<Role> roleList=Role.dao.findRolesByUserId(userId);
		int i=0;
		String p=null;
		for(Role r:roleList){
			if(p==null)
				p=r.getId().toString();
			else
				p=p+","+r.getId().toString();
		}

		List<Res> list= Res.dao.findByCache(Consts.CACHE_NAMES.userReses.name(),userId+""+rId,"select r.* from s_res r left join s_role_res rr on r.id=rr.resId  where rr.roleId in (?) and r.pid=? order by r.seq",p,rId);
		return list;
	}
}
