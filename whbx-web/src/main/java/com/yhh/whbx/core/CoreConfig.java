package com.yhh.whbx.core;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log4jLogFactory;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;
import com.yhh.whbx.CMNCtr;
import com.yhh.whbx.admin.Res.ResCtr;
import com.yhh.whbx.admin.art.ArtCtr;
import com.yhh.whbx.admin.param.ParamCtr;
import com.yhh.whbx.admin.role.RoleCtr;
import com.yhh.whbx.admin.taxonomy.TaxCtr;
import com.yhh.whbx.admin.user.UserCtr;
import com.yhh.whbx.card.type.CardTypeCtr;
import com.yhh.whbx.interceptors.ExceptionInterceptor;
import com.yhh.whbx.kits.ResKit;
import com.yhh.whbx.sale.salesmen.SalesmenCtr;

/**
 * Created by yuhaihui8913 on 2017/11/14.
 */
public class CoreConfig extends JFinalConfig{
    @Override
    public void configConstant(Constants constants) {
        constants.setDevMode(ResKit.getConfigBoolean("devMode"));
        //constants.setMainRenderFactory(new BeetlRenderFactory());
        constants.setError500View("/WEB-INF/template/common/500.html");
        constants.setError404View("/WEB-INF/template/common/404.html");
        constants.setError403View("/WEB-INF/template/common/403.html");
        constants.setError401View("/WEB-INF/template/common/401.html");
        constants.setEncoding("UTF-8");
        constants.setJsonFactory(new FastJsonFactory());
        constants.setLogFactory(new Log4jLogFactory());
    }

    @Override
    public void configRoute(Routes routes) {
        routes.add(new Routes() {
            @Override
            public void config() {
                add("/ad00", ParamCtr.class);
                add("/ad01", UserCtr.class);
                add("/ad02", RoleCtr.class);
                add("/ad03", ResCtr.class);
                add("/ad04", ArtCtr.class);
                add("/ad05", TaxCtr.class);

            }
        });

        routes.add(new Routes() {
            @Override
            public void config() {
                add("/cmn", CMNCtr.class);
            }
        });

        routes.add(new Routes() {
            @Override
            public void config() {
                add("/sl00", SalesmenCtr.class);
            }
        });

        routes.add(new Routes() {
            @Override
            public void config() {
                add("/c00", CardTypeCtr.class);
            }
        });
    }

    @Override
    public void configEngine(Engine engine) {
        engine.addSharedObject("ctx", JFinal.me().getContextPath());
        engine.setDevMode(ResKit.getConfigBoolean("devMode", false));
        //使用JF模板渲染通用页面
        engine.addSharedFunction("/WEB-INF/template/common/css.html");
        engine.addSharedFunction("/WEB-INF/template/common/js.html");
        engine.addSharedFunction("/WEB-INF/template/admin/_layout.html");
        engine.addSharedFunction("/WEB-INF/template/www/_layout.html");
    }

    @Override
    public void configPlugin(Plugins plugins) {
        //开启druid数据库连接池
        DruidPlugin druidPlugin = createDruidPlugin();
        // StatFilter提供JDBC层的统计信息
        druidPlugin.addFilter(new StatFilter());
        // WallFilter的功能是防御SQL注入攻击
        WallFilter wallDefault = new WallFilter();
        wallDefault.setDbType("mysql");
        druidPlugin.addFilter(wallDefault);
        druidPlugin.setInitialSize(ResKit.getConfigInt("db.default.poolInitialSize"));
        druidPlugin.setMaxPoolPreparedStatementPerConnectionSize(ResKit.getConfigInt("db.default.poolMaxSize"));
        druidPlugin.setTimeBetweenConnectErrorMillis(ResKit.getConfigInt("db.default.connectionTimeoutMillis"));
        plugins.add(druidPlugin);
        //开启DB+record 映射关系插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        _MappingKit.mapping(arp);
        arp.setBaseSqlTemplatePath(PathKit.getRootClassPath()+"/sql");
        arp.addSqlTemplate("all.sql");
        arp.setShowSql(true);
        plugins.add(arp);
        //开启eheache缓存
        plugins.add(new EhCachePlugin());
    }

    private DruidPlugin createDruidPlugin() {
        DruidPlugin druidDefault = new DruidPlugin(ResKit.getConfig("db.default.url"), ResKit.getConfig("db.default.user"),
                ResKit.getConfig("db.default.password"),ResKit.getConfig("db.default.driver"));
        return druidDefault;
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.add(new ExceptionInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {

    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        CoreData.loadAllCache();//省市区 Cache 初始化
    }
}
