package com.yhh.whbx.core;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.log.Log4jLogFactory;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;
import com.yhh.whbx.kits.ResKit;

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
        constants.setLogFactory(new Log4jLogFactory());
    }

    @Override
    public void configRoute(Routes routes) {

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

    }

    @Override
    public void configHandler(Handlers handlers) {

    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        CoreDataInit.initSSQ();//省市区 Cache 初始化
    }
}
