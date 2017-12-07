import Vue from 'vue';
import iView from 'iview';
import {router} from './router/index';
import {appRouter} from './router/router';
import store from './store';
import App from './app.vue';
import '@/locale';
import 'iview/dist/styles/iview.css';
import VueI18n from 'vue-i18n';
import util from '@/libs/util';
import axios from 'axios'
import qs from 'qs'
import message from 'iview/src/components/message'
import env from '../build/env';
// import VeeValidate, {Validator} from 'vee-validate';
// import zh from 'vee-validate/dist/locale/zh_CN';
// Validator.addLocale(zh);
// const veeConfig = {
//     locale: 'zh_CN'
// };
Vue.use(VueI18n);
Vue.use(iView);
// Vue.use(VeeValidate,veeConfig);
require('es6-promise').polyfill()

let axiosIns = axios.create({});
if (env == 'development') {
    axiosIns.defaults.baseURL = '/api';
} else if (env == 'debug') {
    axiosIns.defaults.baseURL = '/api';
} else if (env == 'production') {
    axiosIns.defaults.baseURL = '';
}



axiosIns.defaults.headers.post['X-Requested-With'] = 'XMLHttpRequest';
axiosIns.defaults.headers.get['X-Requested-With'] = 'XMLHttpRequest';
axiosIns.defaults.responseType = 'json';
axiosIns.defaults.transformRequest = [function (data) {
    return qs.stringify(data);
}
];
axiosIns.defaults.validateStatus = function (status) {
    return true;
};
axiosIns.interceptors.request.use(function (config) {
    //配置config
    config.headers.Accept = 'application/json';
    // config.headers.System = 'vue';
    // let token = Vue.localStorage.get('token');
    // if(token){
    //     config.headers.Token = token;
    // }

    store.commit('upadteSpinshow',true);
    return config;
});

axiosIns.interceptors.response.use(function (response) {
    store.commit('upadteSpinshow',false);
    let data = response.data;
    let status = response.status;
    if (status === 200) {
        return Promise.resolve(response);
    } else {
        return Promise.reject(response);
    }
});

let ajaxMethod = ['get', 'post'];
let api = {};
ajaxMethod.forEach((method)=> {
    //数组取值的两种方式
    api[method] = function (uri, data, config) {
        return new Promise(function (resolve, reject) {
            axiosIns[method](uri, data, config).then((response)=> {
                let data=response.data;
                if (response.status==200) {
                    // message.warning("请您先登录")
                    // setTimeout(router.push({
                    //     path: "/login"
                    // }),2000)
                    //if(response.data.resCode=='success'){
                    if(data.resCode&&data.resCode=='success') {
                        message.success(data.resMsg);
                    }
                    else if(data.resCode&&data.resCode=='fail') {
                        message.error(data.resMsg);
                    }

                    resolve(data);


                } else if (response.status === 403) {
                    router.push({
                        path: "/403"
                    });
                }
                else if (response.status === 500) {
                    router.push({
                        path: "/500"
                    });
                }
                else if (response.status === 404) {
                    router.push({
                        path: "/404"
                    });
                }else{
                    resolve(data);
                }
            }).catch((response)=> {
                message.error("服务器内部错误，请稍后重试")
                reject(response)
            })
        })
    }
});

Vue.prototype.$axios = api;

new Vue({
    el: '#app',
    router: router,
    store: store,
    render: h => h(App),
    data: {
        currentPageName: ''
    },
    mounted () {
        this.currentPageName = this.$route.name;
        // 显示打开的页面的列表
        this.$store.commit('setOpenedList');
        this.$store.commit('initCachepage');
        // 权限菜单过滤相关
        this.$store.commit('updateMenulist');
        // iview-admin检查更新
        //util.checkUpdate(this);
    },
    created () {
        let tagsList = [];
        appRouter.map((item) => {
            if (item.children.length <= 1) {
                tagsList.push(item.children[0]);
            } else {
                tagsList.push(...item.children);
            }
        });
        this.$store.commit('setTagsList', tagsList);
    }
});
