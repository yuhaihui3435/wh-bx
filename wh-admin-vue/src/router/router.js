import Main from '@/views/Main.vue';

// 不作为Main组件的子页面展示的页面单独写，如下
//isCheck 表示 组件是否需要权限检查
export const loginRouter = {
    path: '/login',
    name: 'login',
    meta: {
        title: '登录'
    },
    component: resolve => { require(['@/views/login.vue'], resolve); }
};

export const page404 = {
    path: '/*',
    name: 'error-404',
    meta: {
        title: '404-页面不存在'
    },
    component: resolve => { require(['@/views/error-page/404.vue'], resolve); }
};

export const page403 = {
    path: '/403',
    meta: {
        title: '403-权限不足'
    },
    name: 'error-403',
    component: resolve => { require(['@//views/error-page/403.vue'], resolve); }
};

export const page500 = {
    path: '/500',
    meta: {
        title: '500-服务端错误'
    },
    name: 'error-500',
    component: resolve => { require(['@/views/error-page/500.vue'], resolve); }
};



export const locking = {
    path: '/locking',
    name: 'locking',
    component: resolve => { require(['@/views/main-components/lockscreen/components/locking-page.vue'], resolve); }
};

// 作为Main组件的子页面展示但是不在左侧菜单显示的路由写在otherRouter里
export const otherRouter = {
    path: '/',
    name: 'otherRouter',
    redirect: '/home',
    isCheck:'no',
    component: Main,
    children: [
        { path: 'home', title: '控制中心',isCheck:'no', name: 'home_index', component: resolve => { require(['@/views/home/home.vue'], resolve); } },
        { path: 'ownspace', title: '个人中心', isCheck:'no',name: 'ownspace_index', component: resolve => { require(['@/views/own-space/own-space.vue'], resolve); } },
    ]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里
export const appRouter = [
    {
        path: '/admin',
        icon: 'monitor',
        isCheck:'yes',
        name: 'admin',
        title: '系统管理',
        component: Main,
        children: [
            { path: 'param', title: '参数管理',isCheck:'yes', name: 'admin_param', component: resolve => { require(['@/views/admin/param/main.vue'], resolve); } },
            { path: 'user', title: '用户管理', isCheck:'yes',name: 'admin_user', component: resolve => { require(['@/views/admin/user/main.vue'], resolve); } },
            { path: 'role', title: '角色管理', isCheck:'yes',name: 'admin_role', component: resolve => { require(['@/views/admin/role/main.vue'], resolve); } },
            { path: 'tax',  title: '分类管理', isCheck:'yes',name: 'admin_tax', component: resolve => { require(['@/views/admin/taxonomy/main.vue'], resolve); } },
            { path: 'art', title: '文章管理', isCheck:'yes',name: 'admin_art', component: resolve => { require(['@/views/admin/art/main.vue'], resolve); } },
            // { path: 'artList', title: '文章列表', name: 'admin_artList', component: resolve => { require(['@/views/admin/art/list.vue'], resolve); } },
        ]
    },
    {
        path: '/sale',
        icon: 'social-yen-outline',
        name: 'sale',
        isCheck:'yes',
        title: '销售人员管理',
        component: Main,
        children: [
            { path: 'salesmen', title: '销售人员管理',isCheck:'yes',access:'/sl00/page', name: 'sale_salesmen', component: resolve => { require(['@/views/sale/salesmen/main.vue'], resolve); } },

        ]
    },
    {
        path: '/card',
        icon: 'card',
        name: 'card',
        isCheck:'yes',
        title: '卡管理',
        component: Main,
        children: [
            { path: 'type', title: '卡类型管理', isCheck:'yes',name: 'card_type', component: resolve => { require(['@/views/card/type/main.vue'], resolve); } },
            { path: 'apply', title: '卡申请', isCheck:'yes',name: 'card_apply', component: resolve => { require(['@/views/card/apply/main.vue'], resolve); } },
            { path: 'oow', title: '卡出库', isCheck:'yes',name: 'card_oow', component: resolve => { require(['@/views/card/oow/main.vue'], resolve); } },
            { path: 'query', title: '卡查询',isCheck:'yes', name: 'card_query', component: resolve => { require(['@/views/card/query/main.vue'], resolve); } },
            { path: 'act', title: '卡激活', isCheck:'yes',name: 'card_act', component: resolve => { require(['@/views/card/act/main.vue'], resolve); } },

        ]
    },
    {
        path: '/report',
        icon: 'ios-pie',
        isCheck:'yes',
        name: 'report',
        title: '数据统计',
        component: Main,
        children: [
            { path: 'cards', title: '卡销量统计',isCheck:'yes', name: 'report_cards', component: resolve => { require(['@/views/card/report/ds00.vue'], resolve); } },
            { path: 'salesmen', title: '销售员销量统计',isCheck:'yes', name: 'report_salesmen',component: resolve => { require(['@/views/card/report/ds01.vue'], resolve); } },
        ]
    },
];

// 所有上面定义的路由都要写在下面的routers里
export const routers = [
    loginRouter,
    otherRouter,
    locking,
    ...appRouter,
    page500,
    page403,
    page404
];
