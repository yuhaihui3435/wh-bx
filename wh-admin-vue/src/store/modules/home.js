import kit from '../../libs/kit'
const home = {
    state: {
        countStatisticsData:{},
        dspOption:{}
    },
    mutations: {
        setCountStatisticsData:function (state,obj) {
            state.countStatisticsData=Object.assign({},obj);
            let label=[];
            let data=[];
            let records=[];
            let ss=state.countStatisticsData.salesmanStatistics;
            for(let i=0;i<ss.length;i++){
                label.push(ss[i][1]);
                records.push({value:ss[i][0],name:ss[i][1],itemStyle: {normal: {color: '#'+kit.getColor()}}})
            }
            state.dspOption={
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b} : {c} ({d}%)'
                },
                legend: {
                    orient: 'vertical',
                    left: 'right',
                    data: label
                },
                series: [
                    {
                        name: '销售人员销售量',
                        type: 'pie',
                        radius: '66%',
                        center: ['50%', '60%'],
                        data: records,
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            }

        }
    },
    actions:{
        countStatisticsData_query :function ({ commit,state }) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.get('/homeCountStatistics').then((res) => {
                    commit('setCountStatisticsData', res)
                    resolve()
                });
            });
        },
    }
};

export default home;