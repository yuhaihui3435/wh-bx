/**
 * Created by yuhaihui on 2017/12/9.
 */
import kit from "../../libs/kit";
const depot = {
    state: {
        depotList: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 0,
        depot: {},
        salesmenList: [],
        cardtypeList: [],
        cardapplyList:[],
    },
    mutations: {
        set_depot_page(state, page){
            state.depotList = page.list
            state.totalPage = page.totalPage
            state.pageNumber = page.pageNumber
            state.totalRow = page.totalRow
        },
        set_depot_list(state, list){
            state.depotList = list
        },
        set_depot(state, obj){
            if (obj != undefined)
                state.depot = Object.assign({},obj);
        },
        set_depot_dataReady(state, obj){
            state.cardtypeList = obj.cardtypeList;
            state.salesmenList = obj.salesmenList;
        },
        set_depot_cardapply_list(state,list){
            state.cardapplyList=list
        }
    },
    actions: {
        depot_page: function ({commit, state}, param) {
            this._vm.$axios.post('/c02/page', param).then((res) => {
                commit('set_depot_page', res)
            });
        },
        depot_save: function ({commit, state}, param) {
            let vm = this._vm;
            let p = kit.clone(state.depot);
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c02/save' , p).then((res) => {
                    resolve(res.resCode);
                });
            });


        },
        depot_update: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c02/update', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        depot_create: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c02/actCard', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        depot_cardapply_list:function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c01/listByCardtypeId', param).then((res) => {
                    commit('set_depot_cardapply_list', res)
                });
            });
        },
        depot_cardapply_list1:function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c01/listByCardtypeId', param).then((res) => {
                    resolve(res)
                });
            });
        },
        depot_list: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c02/list', param).then((res) => {
                    commit('set_depot_list', res)
                });
            });
        },
        depot_get: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c02/get', param).then((res) => {
                    commit('set_depot', res)
                    resolve()
                });
            });
        },

        depot_dataReady: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c02/dataReady').then((res) => {
                    commit('set_depot_dataReady', res)
                });
            });
        },


    }
}
export default depot
