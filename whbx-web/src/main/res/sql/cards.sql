#sql("findPage")
  select c.* from b_cards c LEFT JOIN b_depot d on c.depotId=d.id LEFT JOIN b_cardapply ca on c.applyId=ca.id LEFT JOIN b_cardtype ct ON ca.cardtypeId=ct.id where 1=1 AND
    #for(x : cond)
      and #(x.key) #para(x.value)
    #end
    ORDER BY cAt DESC
#end