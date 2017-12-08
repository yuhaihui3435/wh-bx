import Vue from 'vue';
import Vuex from 'vuex';

import app from './modules/app';
import user from './modules/user';
import param from './modules/param';
import role from './modules/role';
import art from './modules/art';
import res from './modules/res';
import tax from './modules/tax';

Vue.use(Vuex);

const store = new Vuex.Store({
    state: {
        spinShow:false,
        uploadPicMaxSize:524288,
        ignoreSpinshow:false
    },
    mutations: {
        upadteSpinshow(state,p){
            if(!this.state.ignoreSpinshow)
                state.spinShow=p;
        },
        updateIgnoreSpinshow(state,p){
            state.ignoreSpinshow=p
        }


    },
    actions: {

    },
    modules: {
        app,
        user,
        param,
        role,
        art,
        res,
        tax
    }
});



export default store;
