import kit from '../../libs/kit';
const  art={
    state: {
        artList:[],
        totalPage:0,
        pageNumber:1,
        totalRow:1,
        art:{title:''},
        artTaxList:[]
    },
    mutations: {
        set_art_list(state,page){
            state.artList=page.list
            state.totalPage=page.totalPage
            state.totalRow=page.totalRow
            state.pageNumber=page.pageNumber
        },
        set_art(state,obj){
            if(obj !=undefined)
                state.role=kit.clone(obj);
        },
        set_art_taxJsonarray(state,obj){
            state.artTaxList=obj;
        }

    },
    actions:{
        art_list:function ({ commit,state },param) {
            this._vm.$axios.post('/ad04/list',param).then((res)=>{
                commit('set_art_list',res)
            });
        },
        art_save:function ({ commit,state },action) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad04/'+action, state.role).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        art_del:function ({ commit,state },param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad04/del', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        art_tax_jsonArray:function ({commit,state}) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad05/treeJsonArray', {'module':'art'}).then((res) => {
                    commit('set_art_taxJsonarray',res)
                });
            });
        }

    }
}
export default art