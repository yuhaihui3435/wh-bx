package com.yhh.whbx.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaoleilu.hutool.log.StaticLog;
import com.yhh.whbx.Consts;
import com.yhh.whbx.admin.model.Param;
import com.yhh.whbx.kits.FileKit;
import com.yhh.whbx.vo.SSQ;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/11/16.
 */
public class CoreData {


    public static void loadAllCache(){
        loadParam();loadSSQ();
    }

    public static void loadParam(){
        List<Param> list=Param.dao.find("select * from "+Param.TABLE);
        for(Param p:list){
            CacheKit.put(Consts.CACHE_NAMES.paramCache.name(),p.getK(),p.getVal());
        }
        StaticLog.info("系统参数加载成功");
    }


    public static void loadSSQ(){
        String shengStr= FileKit.loadFile2Str("jsonData/shengData.json");
        String shiStr=FileKit.loadFile2Str("jsonData/shiData.json");
        String quStr=FileKit.loadFile2Str("jsonData/quData.json");
        JSONArray shengJA= JSON.parseArray(shengStr);
        JSONArray shiJA=JSON.parseArray(shiStr);
        JSONArray quJA=JSON.parseArray(quStr);
        List<SSQ> shengList=new ArrayList<SSQ>();
        List<SSQ> shiList=new ArrayList<SSQ>();
        List<SSQ> quList=new ArrayList<SSQ>();
        JSONObject jo=null;
        SSQ ssq=null;
        String shengCk,shiCk=null;
        int proID;
        int shiID;
        for (int i = 0; i <shengJA.size() ; i++) {
            jo=shengJA.getJSONObject(i);
            ssq=new SSQ();
            ssq.setId(jo.getIntValue("ProID"));
            ssq.setName(jo.getString("name"));
            ssq.setSort(jo.getIntValue("ProSort"));
            shengList.add(ssq);
            shengCk=ssq.getName()+ssq.getId();
            proID=ssq.getId();
            for (int j = 0; j < shiJA.size(); j++) {
                jo=shiJA.getJSONObject(j);
                if(jo.getIntValue("ProID")==proID) {
                    ssq = new SSQ();
                    ssq.setId(jo.getIntValue("CityID"));
                    ssq.setName(jo.getString("name"));
                    ssq.setSort(jo.getIntValue("CitySort"));
                    ssq.setPid(jo.getIntValue("ProID"));
                    shiList.add(ssq);
                    shiCk=ssq.getName()+ssq.getId();
                    shiID=ssq.getId();
                    for (int k = 0; k < quJA.size(); k++) {
                        jo=quJA.getJSONObject(k);
                        if(jo.getIntValue("CityID")==shiID){
                            ssq = new SSQ();
                            ssq.setId(jo.getIntValue("Id"));
                            ssq.setName(jo.getString("DisName"));
                            ssq.setSort(jo.getIntValue("DisSort"));
                            ssq.setPid(jo.getIntValue("CityID"));
                            quList.add(ssq);
                        }
                    }
                    CacheKit.put(Consts.CACHE_NAMES.ssq.name(),shiCk,JSON.toJSONString(quList));
                    quList.clear();
                }
            }
            CacheKit.put(Consts.CACHE_NAMES.ssq.name(),shengCk,JSON.toJSONString(shiList));
            shiList.clear();
        }
        StaticLog.info("省市区数据加载成功");

    }



}
