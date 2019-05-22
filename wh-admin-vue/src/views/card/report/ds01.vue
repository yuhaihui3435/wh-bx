<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="ios-pulse-strong"></Icon>
                    销售人员销量统计
                </p>
                <Row>
                    <Col span="24" offset="0" align="right">
                    <Form inline>


                        <FormItem>
                            <Select v-model="salesmenId" placeholder="业务员" clearable style="width:100px" align="left">
                                <Option v-for="item in salesmenList" :value="item.id" :key="item.id">{{ item.name}}
                                </Option>
                            </Select>
                        </FormItem>

                        <FormItem>
                            <DatePicker type="daterange"  :options="options"   placement="bottom-end" v-model="at" placeholder="查询时间范围" @on-change="dateTimeChange" style="width: 300px"></DatePicker>
                        </FormItem>
                        <FormItem>
                            <span @click="search(1)" style="margin: 0 10px;"><Button type="primary"
                                                                                     icon="search">搜索</Button></span>
                            <span @click="reset" style="margin: 0 10px;"><Button type="primary"
                                                                                 icon="ios-redo">重置</Button></span>
                        </FormItem>
                    </Form>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" :row-class-name="rowClassName" border :data="ds01List" :columns="tableColums" @on-expand="expandHandle" >
                    </Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="search" show-total :page-size="15"
                              show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
    </div>

</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    import ds0010 from './ds0010.vue'
    const moment = require('moment');
    export default {

        computed: {
            ...mapState({
                'ds01List': state => state.cards.ds01List,
                'totalPage': state => state.cards.totalPage,
                'total': state => state.cards.totalRow,
                'pageNumber': state => state.cards.pageNumber,
                'salesmenList': state => state.cards.salesmenList,
            })
        },
        methods: {
            search(pn){
                if(this.sTime==''){
                    this.$Message.error("查询时间范围必选");
                    return;
                }

                if(pn==undefined)pn=1
                let param = {
                    smId: this.salesmenId,
                    sTime: this.sTime,
                    pn: pn
                }
                this.$store.dispatch('ds01_page', param)
            },
            dateTimeChange(val){
                if(val[0]!='')
                    this.sTime=val[0]+' - '+val[1];
                else
                    this.sTime='';
            },
            reset(){
                this.cardtypeId='';//卡类型id
                this.at='';
                this.sTime='';
            },
            rowClassName (row, index) {
                if (row.id === '-1')
                    return 'demo-table-info-row';
                return '';
            },
            expandHandle(row,index){
                

            }

        },
        mounted () {
            this.$store.commit('set_ds01_page',{})
            this.$store.dispatch('ds_dataReady')
            let now=new Date();
            let preNow=new Date();
            preNow.setDate(now.getDate()-30);
            now=now.getFullYear()+"-"+(now.getMonth()+1)+"-"+now.getDate()
            preNow=preNow.getFullYear()+"-"+(preNow.getMonth()+1)+"-"+preNow.getDate()
            this.at=[preNow,now]
            this.sTime=preNow+' - '+now
        },
        components: {
            ds0010
        },
        data () {
            return {
                at:'',
                salesmenId:'',//业务员id
                sTime: '',//出库时间
                self:this,
                tableColums: [
                    {
                        type: 'expand',
                        width: 50,
                        render: (h, params) => {
                                return h(ds0010, {
                                    props:{
                                        queryParam:{smId:params.row.id,sTime:this.sTime}
                                    }
                                })
                        }
                    },
                    {
                        title: '销售人员',
                        key: 'name',
                    },

                    {
                        title: '出库数量',

                        key: 'dCount',
                    },
                    {
                        title: '开卡数量',

                        key: 'uCount',
                    },
                    {
                        title: '激活数量',

                        key: 'aCount',
                    },
                    {
                        title: '导出数量',

                        key: 'eCount',
                    },
                    {
                        title: '激活未导出数量',

                        key: 'aeCount',
                    },
                ]
                ,
                options: {
                    shortcuts: [
                        {
                            text: '最近30天',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                                return [start, end];
                            }
                        },
                        {
                            text: '最近60天',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 60);
                                return [start, end];
                            }
                        },
                        {
                            text: '最近90天',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                                return [start, end];
                            }
                        }
                        ,
                                                {
                                                    text: '最近180天',
                                                    value () {
                                                        const end = new Date();
                                                        const start = new Date();
                                                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 180);
                                                        return [start, end];
                                                    }
                                                }
                        ,
                                                {
                                                    text: '最近360天',
                                                    value () {
                                                        const end = new Date();
                                                        const start = new Date();
                                                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 360);
                                                        return [start, end];
                                                    }
                                                }
                    ]
                }
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
    .ivu-table .demo-table-info-row td{
        background-color: #2db7f5;
        color: #fff;
        font-weight: 400;
        font-size: x-large;
    }
</style>