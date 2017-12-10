package com.yhh.whbx.core;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.yhh.whbx.admin.model.*;
import com.yhh.whbx.card.model.Cardtype;
import com.yhh.whbx.sale.model.Salesmen;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
//		arp.addMapping("ump_order", "id", Order.class);
		arp.addMapping("s_param", Param.class);
		arp.addMapping("s_res", Res.class);
		arp.addMapping("s_role", Role.class);
		arp.addMapping("s_role_res", RoleRes.class);
		arp.addMapping("s_user", User.class);
		arp.addMapping("s_user_role", UserRole.class);
		arp.addMapping("s_content", Content.class);
		arp.addMapping("s_mapping", Mapping.class);
		arp.addMapping("s_taxonomy", Taxonomy.class);
		arp.addMapping("b_salesmen", Salesmen.class);
		arp.addMapping("b_cardType", Cardtype.class);
		arp.addMapping("s_attachment", Attachment.class);

	}
}

