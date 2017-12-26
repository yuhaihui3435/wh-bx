#sql("findPage")
  select c.*,ct.name as cardtypeName,ca.batch as batch,sm.name as salesmenName,ct.faceVal as faceVal,d.outStatus as outStatus  from b_cards c LEFT JOIN b_depot d on c.depotId=d.id LEFT JOIN b_cardapply ca on c.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id LEFT JOIN b_salesmen sm on d.salesmenId=sm.id where 1=1
    #for(x : cond)
      and #(x.key) #para(x.value)
    #end
    ORDER BY c.actAt,c.id DESC
#end

#sql("findByCode")
  select c.*,ct.name as cardtypeName,ca.batch as batch,sm.name as salesmenName,ct.faceVal as faceVal,d.outStatus as outStatus,t.text as cardtype,ct.manyPeople as manyPeople,ct.peopleCount as peopleCount,ct.protocol as protocol
  from b_cards c LEFT JOIN b_depot d on c.depotId=d.id LEFT JOIN b_cardapply ca on c.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id
  LEFT JOIN b_salesmen sm on d.salesmenId=sm.id LEFT JOIN s_taxonomy t ON ct.type=t.id
  where  c.code=#para(code) AND
#end