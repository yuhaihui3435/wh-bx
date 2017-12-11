/**
 * Created by yuhaihui on 2017/12/9.
 */
import kit from '../../libs/kit';
const cardtype = {
    state: {
        cardtypeList: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 0,
        cardtype: {},
        cttList: [],
        ioList: [],
    },
    mutations: {
        set_cardtype_page(state, page){
            state.cardtypeList = page.list
            state.totalPage = page.totalPage
            state.pageNumber = page.pageNumber
            state.totalRow = page.totalRow
        },
        set_cardtype_list(state, list){
            state.cardtypeList = list
        },
        set_cardtype(state, obj){
            if (obj != undefined)
                state.cardtype = kit.clone(obj);
        },
        set_cardtype_dataReady(state, obj){
            state.cttList = obj.cttList;
            state.ioList = obj.ioList;
        },
    },
    actions: {
        cardtype_page: function ({commit, state}, param) {
            this._vm.$axios.post('/c00/page', param).then((res) => {
                commit('set_cardtype_page', res)
            });
        },
        cardtype_save: function ({commit, state}, param) {
            let vm = this._vm;
            let p = kit.clone(state.cardtype);
            p.serviceCert = param.serviceCert
            p.protocol = param.protocol
            let uploadList = param.uploadList;
            let fd = new FormData();
            if (uploadList.length > 0) {
                fd.append("files", uploadList);
                for (let s in p) {
                    fd.append(s, p[s])
                }
            }
            alert(fd.getHeaders())

            let getHeaders = (form=>{
                return new Promise((resolve,reject)=>{
                    form.getLength((err,length)=>{
                        if(err) reject(err)
                        let headers = Object.assign({'Content-Length':length},form.getHeaders())
                        resolve(headers)
                    })
                })
            })

            getHeaders(fd)
                .then(headers=>{
                    return new Promise(function (resolve, reject) {
                        vm.$axios.post('/c00/' + param.action, fd,headers).then((res) => {
                            resolve(res.resCode);
                        });
                    });
            })



        },
        cardtype_del: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c00/del', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        cardtype_updateStatus: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c00/updateStatus', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        cardtype_list: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c00/list', param).then((res) => {
                    commit('set_cardtype_list', res)
                });
            });
        },
        cardtype_get: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c00/get', param).then((res) => {
                    res.type = res.type + '';
                    res.category = res.category + '';
                    commit('set_cardtype', res)
                    resolve()
                });
            });
        },

        cardtype_dataReady: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/c00/dataReady').then((res) => {
                    commit('set_cardtype_dataReady', res)
                });
            });
        },


    }
}
export default cardtype
