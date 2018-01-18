#sql("findPage")
  select c.*,ct.name as cardtypeName,ca.batch as batch,sm.name as salesmenName,ct.faceVal as faceVal,d.outStatus as outStatus,ct.type as cardtypeType,(select text from s_taxonomy where id=ct.type) as cardtypeTypeTxt

    from b_cards c LEFT JOIN b_depot d on c.depotId=d.id LEFT JOIN b_cardapply ca on c.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id LEFT JOIN b_salesmen sm on d.salesmenId=sm.id
    LEFT JOIN b_cards_ph ph ON c.id=ph.cardsId LEFT JOIN b_cards_ip ip ON c.id=ip.cardsId LEFT JOIN b_cards_car_ph cph ON c.id=cph.cardsId LEFT JOIN b_cards_car_ip cip ON c.id=cip.cardsId
    where 1=1
    #for(x : cond)
      #if(x.key=='c.exportCode'&&x.value=='0')
         and c.exportCode is not NULL
        #else if(x.key=='c.exportCode'&&x.value=='1')
         and c.exportCode is NULL
        #else
         and #(x.key) #para(x.value)
      #end
    #end
    ORDER BY c.actAt,c.id DESC
#end

#sql("findByCode")
  select c.*,ct.name as cardtypeName,ca.batch as batch,sm.name as salesmenName,ct.faceVal as faceVal,d.outStatus as outStatus,t.text as cardtype,ct.manyPeople as manyPeople,ct.peopleCount as peopleCount,ct.protocol as protocol,ct.id as ctId,(select text from s_taxonomy where id=ct.type) as cardtypeTypeTxt
  from b_cards c LEFT JOIN b_depot d on c.depotId=d.id LEFT JOIN b_cardapply ca on c.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id
  LEFT JOIN b_salesmen sm on d.salesmenId=sm.id LEFT JOIN s_taxonomy t ON ct.type=t.id
  where  c.code=#para(code)
#end

#sql("findListByIds")
  select c.*,ct.name as cardtypeName,ca.batch as batch,sm.name as salesmenName,ct.faceVal as faceVal,d.outStatus as outStatus,ct.type as cardtypeType,(select text from s_taxonomy where id=ct.type) as cardtypeTypeTxt
    from b_cards c LEFT JOIN b_depot d on c.depotId=d.id LEFT JOIN b_cardapply ca on c.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id LEFT JOIN b_salesmen sm on d.salesmenId=sm.id
    where 1=1 and c.id in ( #para(ids) ) AND c.act='0' AND c.isLocked='0' AND c.exportCode is NULL
    ORDER BY c.actAt,c.id DESC
#end
#sql("findList")
  select c.*,ct.name as cardtypeName,ca.batch as batch,sm.name as salesmenName,ct.faceVal as faceVal,d.outStatus as outStatus,ct.type as cardtypeType,(select text from s_taxonomy where id=ct.type) as cardtypeTypeTxt

    from b_cards c LEFT JOIN b_depot d on c.depotId=d.id LEFT JOIN b_cardapply ca on c.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id LEFT JOIN b_salesmen sm on d.salesmenId=sm.id
    LEFT JOIN b_cards_ph ph ON c.id=ph.cardsId LEFT JOIN b_cards_ip ip ON c.id=ip.cardsId LEFT JOIN b_cards_car_ph cph ON c.id=cph.cardsId LEFT JOIN b_cards_car_ip cip ON c.id=cip.cardsId
    where 1=1
    #for(x : cond)
      #if(x.key=='c.exportCode'&&x.value=='0')
         and c.exportCode is not NULL
        #else if(x.key=='c.exportCode'&&x.value=='1')
         and c.exportCode is NULL
        #else
         and #(x.key) #para(x.value)
      #end
    #end
    ORDER BY c.actAt,c.id DESC
#end