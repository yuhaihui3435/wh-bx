package com.yhh.whbx;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;

/**
 * GeneratorKit
 */
public class GeneratorKit {
	
	public static DataSource getDataSource() {
		Prop p = PropKit.use("config.properties");
		DruidPlugin druidPlugin = new DruidPlugin(p.get("db.default.url"), p.get("db.default.user"), p.get("db.default.password"));
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.yhh.whbx.sale.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/com/mybank/pw/umpay/model/base";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.yhh.whbx.sale.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 设置数据库方言
		gernerator.setDialect(new MysqlDialect());
		// 添加不需要生成的BaseArea.java表名
		//gernerator.addExcludedTable("adv");
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);

		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(true);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		gernerator.setRemovedTableNamePrefixes("s_","b_");
		// 生成
		gernerator.generate();
	}
}




