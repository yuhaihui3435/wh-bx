
#sql("findCardIpCountByCertCode")
  select count(*) as actCount from b_cards_ip where idCard=?
#end