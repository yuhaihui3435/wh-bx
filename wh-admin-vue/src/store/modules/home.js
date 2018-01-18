const home = {
    state: {
        countStatisticsData:{}
    },
    mutations: {
        setCountStatisticsData:function (state,obj) {
            state.countStatisticsData=Object.assign({},obj);
        }
    },
    actions:{
        countStatisticsData_query :function ({ commit,state }) {
            this._vm.$axios.get('/homeCountStatistics').then((res)=>{
                commit('setCountStatisticsData',res)
            });
        },
    }
};

export default home;