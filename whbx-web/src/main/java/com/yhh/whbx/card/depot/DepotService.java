package com.yhh.whbx.card.depot;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yhh.whbx.Consts;
import com.yhh.whbx.card.model.Cardapply;
import com.yhh.whbx.card.model.Cards;
import com.yhh.whbx.card.model.Depot;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [whbx]
 * 包:        [com.yhh.whbx.card.depot]
 * 类名称:    [DepotService]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/12/16]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class DepotService {

    public static final DepotService service=new DepotService();

    public static  final Depot dao= Depot.dao;

    /**
     * 检查起始结束编号是否合法，不能与已有数据重合
     * @param bNum
     * @param eNum
     * @param cardapplyId
     * @return
     */
    public String checkNum(int bNum,int eNum,Integer cardapplyId){
        Kv kv=Kv.by("bNum",bNum);
        kv.put("eNum",eNum);
        kv.put("cardapplyId",cardapplyId);
        SqlPara sqlPara = Db.getSqlPara("depot.checkNum",  kv);
        List<Depot> list=dao.find( sqlPara);

        return list.size()>0? Consts.YORN_STR.no.getVal(): Consts.YORN_STR.yes.getVal();
    }

    public String checkCount(int bNum,int eNum,Integer cardapplyId){
        int len=eNum-bNum;
        Long remainder= Cards.dao.countByCardapplyIdAndInDepot(cardapplyId);
        return len>remainder? Consts.YORN_STR.no.getVal(): Consts.YORN_STR.yes.getVal();
    }

}
