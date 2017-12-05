import Cookies from 'js-cookie';
import kit from '../../libs/kit';

const user = {
    state: {
        userList:[],
        totalPage:0,
        pageNumber:1,
        user:{roleIds:[]}
    },
    mutations: {
        logout (state, vm) {
            Cookies.remove('user');
            Cookies.remove('password');
            Cookies.remove('access');
            // 恢复默认样式
            let themeLink = document.querySelector('link[name="theme"]');
            themeLink.setAttribute('href', '');
            // 清空打开的页面等数据，但是保存主题数据
            let theme = '';
            if (localStorage.theme) {
                theme = localStorage.theme;
            }
            localStorage.clear();
            if (theme) {
                localStorage.theme = theme;
            }
        },
        set_user_list(state,page){
            state.userList=page.list
            state.totalPage=page.totalPage
            state.pageNumber=page.pageNumber
        },
        user_reset(state,vm){
            state.user={roleIds:[]}
        }
    },
    actions:{
        user_list:function ({ commit,state },param) {
            if(param&&!param.pn){
                param.pn=state.pageNumber;
            }
            this._vm.$axios.post('/ad01/list',param).then((res)=>{
                commit('set_user_list',res)
            });
        },

        user_save:function ({ commit,state },param) {

            let p=kit.clone(state.user)
            let rIds=p.roleIds;
            let rIds_str=rIds.join(",");
            p.roleIds=rIds_str;
            this._vm.$axios.post('/ad01/save',p).then((res)=>{
                commit('user_reset');
            });
        }
    }
};

export default user;
