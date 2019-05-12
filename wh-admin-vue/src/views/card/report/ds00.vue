<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="ios-pulse-strong"></Icon>
                    卡销量统计
                </p>
                <Row>
                    <Col span="24" offset="0" align="right">
                    <Form inline>


                        <FormItem>
                            <Select v-model="cardtypeId" placeholder="卡类型" clearable style="width:100px" align="left" >
                                <Option v-for="item in cardtypeList" :value="item.id" :key="item.id">{{ item.name }}
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
                    <Table :context="self" :row-class-name="rowClassName" border :data="ds00List" :columns="tableColums" >


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
    const moment = require('moment');
    export default {

        computed: {
            ...mapState({
                'ds00List': state => state.cards.ds00List,
                'totalPage': state => state.cards.totalPage,
                'total': state => state.cards.totalRow,
                'pageNumber': state => state.cards.pageNumber,
                'cardtypeList': state => state.cards.cardtypeList,
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
                    ctId: this.cardtypeId,
                    sTime: this.sTime,
                    pn: pn
                }
                this.$store.dispatch('ds00_page', param)
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
            }

        },
        mounted () {
            this.$store.commit('set_ds00_page',{})
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
        },
        data () {
            return {
                at:'',
                cardtypeId:'',//卡类型id
                sTime: '',//出库时间
                self:this,
                tableColums: [
                    {
                        title: '卡类型名称',
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

                        key: 'aeCount',
                    },
                    {
                        title: '激活未导出数量',

                        key: 'eCount',
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
                                                    text: '最近120天',
                                                    value () {
                                                        const end = new Date();
                                                        const start = new Date();
                                                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 120);
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
                                                                                                                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 180);
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