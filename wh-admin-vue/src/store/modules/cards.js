
import kit from '../../libs/kit';
const cards = {
    state: {
        cardsList: [],
        cardsList_00: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 1,
        cards: {},
        cardtypeList: [],
        salesmenList:[],
        cardapplyList:[]
    },
    mutations: {
        set_cards_page(state, page){
            state.cardsList = page.list
            state.totalPage = page.totalPage
            state.totalRow = page.totalRow
            state.pageNumber = page.pageNumber
        },
        set_cards_page_00(state, page){
            state.cardsList_00 = page.list

            for(var i=0;i<state.cardsList_00.length;i++){
                let c=state.cardsList_00[i];
                c['_disabled']=c.disabled;
            }

            state.totalPage = page.totalPage
            state.totalRow = page.totalRow
            state.pageNumber = page.pageNumber
        },
        set_cards(state, obj){
            if (obj != undefined)
                state.cards = Object.assign({},obj);
            else {
                state.cards = {};
            }
        },
        set_cards_dataReady(state, obj){
            state.cardtypeList = obj.cardtypeList;
            state.salesmenList = obj.salesmenList;
        },
        set_cards_cardapply_list(state,list){
            state.cardapplyList=list
        },

    },
    actions: {
        cards_page: function ({commit, state}, param) {
            this._vm.$axios.post('/c03/page', param).then((res) => {
                commit('set_cards_page', res)
            });
        },
        cards_page_00: function ({commit, state}, param) {
            this._vm.$axios.post('/c03/page', param).then((res) => {
                commit('set_cards_page_00', res)
            });
        },
        cards_get: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c03/get', param).then((res) => {
                    commit('set_cards', res.cards)
                    resolve()
                });
            });
        },
        cards_dataReady: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c03/dataReady').then((res) => {
                    commit('set_cards_dataReady', res)
                });
            });
        },
        cards_cardapply_list:function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c01/listByCardtypeId', param).then((res) => {
                    commit('set_cards_cardapply_list', res)
                });
            });
        },

        cards_lock: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c03/lock',param).then((res) => {
                    resolve(res)
                });
            });
        },
        cards_unlock: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c03/unlock',param).then((res) => {
                    resolve(res)
                });
            });
        },
        cards_unAct:function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c03/unAct',param).then((res) => {
                    resolve(res)
                });
            });
        },

    }
}
export default cards