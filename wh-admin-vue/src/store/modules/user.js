import Cookies from 'js-cookie';

const user = {
    state: {
        userList:[],
        totalPage:0,
        pageNumber:1,

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
        }
    }
};

export default user;
