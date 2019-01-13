package com.yhh.whbx.card.type;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.core.CoreException;

/**
 * Created by yuhaihui8913 on 2018/1/25.
 */
public class CardTypeService {
    private static final Cardtype dao=Cardtype.dao;

    public void actCountCheck(String certCode,Long cardTypeId){
        Cardtype cardtype=dao.findById(cardTypeId);
        String actCount_str=cardtype.getActCount();
        int actCount=Integer.parseInt(actCount_str);

        SqlPara sqlPara=Db.getSqlPara("cardsIp.findCardIpCountByCertCode",certCode,cardTypeId);
        Record record=Db.findFirst(sqlPara);
        Long actedCount=record.getLong("actCount");
        if(actCount<=actedCount){
            throw new CoreException("证件号"+certCode+"的被投保人,已经投保了"+actedCount+"次，超过了该类保险规定的最大投保次数"+actCount);
        }

    }

}
