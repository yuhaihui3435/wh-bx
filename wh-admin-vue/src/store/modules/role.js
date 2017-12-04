
const  role={
    state: {
        roleList:[],
    },
    mutations: {
        set_role_list(state,page){
            state.roleList=page.list
        }
    },
    actions:{
        role_list:function ({ commit,state },param) {
            this._vm.$axios.post('/ad02/list',param).then((res)=>{
                commit('set_role_list',res)
            });
        }
    }
}
export default role