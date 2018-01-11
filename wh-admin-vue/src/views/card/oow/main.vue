<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    卡出库列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="android-add" @click="add">新增出库</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                    <Col span="16" offset="0" align="right">
                    <Form inline>
                        <FormItem>
                            <Select v-model="cardtypeId" placeholder="卡类型" clearable style="width:100px" align="left" @on-change="cardtypeChange">
                                <Option v-for="item in cardtypeList" :value="item.id" :key="item.id">{{ item.name }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="cardapplyId"  placeholder="批次" clearable style="width:100px" align="left" v-show="cardapplyList.length>0">
                                <Option v-for="item in cardapplyList" :value="item.id" :key="item.id">{{ item.batch }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="status" placeholder="状态" clearable style="width:100px" align="left">
                                <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label}}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="outStatus" placeholder="出库状态" clearable style="width:100px" align="left">
                                <Option v-for="item in yonList" :value="item.value" :key="item.value">{{ item.label
                                    }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="salesmenId" placeholder="业务员" clearable style="width:100px" align="left">
                                <Option v-for="item in salesmenList" :value="item.id" :key="item.id">{{ item.name
                                    }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <DatePicker type="datetimerange"   placement="bottom-end" placeholder="出库时间" @on-change="dateTimeChange" style="width: 200px"></DatePicker>
                        </FormItem>
                        <FormItem>
                            <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                                  icon="search">搜索</Button></span>
                        </FormItem>
                    </Form>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="depotList" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="changePage" show-total :pageSize="15"
                              show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <depotForm ref="df"></depotForm>
        <unlockForm ref="uf"></unlockForm>
        <!--<depotCheckForm ref="cacf"></depotCheckForm>-->
    </div>

</template>
<script>
    import {mapState} from 'vuex'
    import depotForm from './form.vue'
    import depotView from './view.vue'
    import unlockForm from './unlockForm.vue'
    import consts from '../../../libs/consts'

    const moment = require('moment');

    let delBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要删除这条数据吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.del(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '删除')]);
    };
    let stopBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要禁用这条数据吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.stop(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '禁用')]);
    }
    let actBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要激活这条数据吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.active(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'success',
                size: 'small'
            }
        }, '激活')]);
    }
    export default {

        computed: {
            ...mapState({
                'depotList': state => state.depot.depotList,
                'totalPage': state => state.depot.totalPage,
                'total': state => state.depot.totalRow,
                'pageNumber': state => state.depot.pageNumber,
                'depot': state => state.depot.depot,
                'cardtypeList': state => state.depot.cardtypeList,
                'salesmenList': state => state.depot.salesmenList,
                'cardapplyList': state => state.depot.cardapplyList,
            })
        },
        methods: {
            del(row){
                row['dAt'] = moment().format('YYYY-MM-DD HH:mm:ss');
                row['exe'] = 'del'
                this.$store.dispatch('depot_update', row).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('depot_page')
                    }
                });
            },
            edit(depot){
                let vm = this;
                console.info(depot)
                this.$store.dispatch('depot_get', {id: depot.id}).then(() => {
                    if (vm.depot.status == '0' && (vm.depot.dAt == undefined)&&vm.depot.outStatus!='0')
                        this.$refs.df.open('编辑出库信息', false);
                    else
                        this.$Message.error("数据状态不正确，无法完成该操作，请刷新数据查看最新的数据状态");
                })

            },
            add(){
                this.$store.commit('set_depot', {})
                this.$store.commit('set_depot_recommendNum',{})
                this.$refs.df.open('新增卡出库', true)
            },
            view(id){
                this.$store.dispatch('depot_get', {id: id}).then(() => {
                    this.$refs.cav.open();
                })
            },
            search(){
                let param = {
                    cardtypeId: this.cardtypeId,
                    outStatus: this.outStatus,
                    status: this.status,
                    salesmenId: this.salesmenId,
                    outTime: this.outTime,
                    cardapplyId: this.cardapplyId
                }
                this.$store.dispatch('depot_page', param)
            },
            changePage(pn){
                let param = {
                    cardtypeId: this.cardtypeId,
                    outStatus: this.outStatus,
                    status: this.status,
                    salesmenId: this.salesmenId,
                    outTime: this.outTime,
                    cardapplyId: this.cardapplyId,
                    pn: pn
                }
                this.$store.dispatch('depot_page', param)
            },
            refresh(){
                let param = {
                    cardtypeId: this.cardtypeId,
                    outStatus: this.outStatus,
                    status: this.status,
                    salesmenId: this.salesmenId,
                    outTime: this.outTime,
                    cardapplyId: this.cardapplyId
                }
                this.$store.dispatch('depot_page')
            },

            stop(row){
                row['status'] = '1';
                row['exe'] = 'stop';
                this.$store.dispatch('depot_update', row).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('depot_page')
                    }
                });
            },

            active(row){
                row['status'] = '0';
                row['exe'] = 'active';
                this.$store.dispatch('depot_update', row).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('depot_page')
                    }
                });
            }
            , createCard(row){
                this.$store.dispatch('depot_create', row).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('depot_page')
                    }
                });
            },
            outDepot(row){
                row['outStatus'] = '0';
                row['exe'] = 'out';
                row['outTime'] = moment().format('YYYY-MM-DD HH:mm:ss');
                this.$store.dispatch('depot_update', row).then((res) => {
                    if (res && res == 'success') {
                    this.$store.dispatch('depot_page')
                }
            });
            },
            unlockCard(row){
                let vm=this;
                this.$store.commit('set_depot',row);
                this.$store.dispatch('depot_unlockRecord_list',{depotId:row.id}).then(()=>{
                    vm.$refs.uf.open();
                });
            },
            cardtypeChange(val){
                if(val!='')
                this.$store.dispatch('depot_cardapply_list',{ctId:val});
                else
                    this.$store.commit('set_depot_cardapply_list',[]);
            },
            dateTimeChange(val){
                if(val[0]!='')
                    this.outTime=val[0]+' - '+val[1];
                else
                    this.outTime='';
            },



        },
        mounted () {
            this.$store.dispatch('depot_page')
            this.$store.dispatch('depot_dataReady')
        },

        components: {
            depotForm, depotView,unlockForm
        },

        data () {
            return {
                cardapplyId:'',
                cardtypeId: '',//卡类型
                outStatus: '',//是否已经出库
                status: '',//状态 查询条件
                salesmenId: '',//分配业务员
                outTime: '',//出库时间
                self:this,
                statusList: consts.status,
                yonList: consts.yon,
                tableColums: [
                    {
                        title: '卡类型',
                        key: 'cardtypeTxt',
                    },
                    {
                        title: '批次',
                        key: 'batch',
                    },

                    {
                        title: '编号',
                        key: 'code',
                    },

                    {
                        title: '起始号',
                        key: 'bNum',
                    },
                    {
                        title: '结束号',
                        key: 'eNum',
                    },
                    {
                        title: '出库时间',
                        key: 'outTime',
                    },
                    {
                        title: '出库状态',
                        key: 'outstatusTxt',
                    },
                    {
                        title: '销售员',
                        key: 'salesmenTxt',
                    },
                    {
                        title: '操作员',
                        key: 'operTxt',
                    },
                    {
                        title: '状态',
                        key: 'status',
                        width: 120,
                        render: (h, param) => {
                            let color = (param.row.status == '0') ? 'green' : 'red'
                            let txt = (param.row.status == '0') ? '正常' : '禁用'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 250,
                        align: 'center',
                        render: (h, param) => {
                            let vm=this;
                            let btns = new Array;
                            let row = param.row;

//                            btns.push(consts.viewBtn(this, h, param));
                            if (row.dAt == undefined && row.outStatus == '1') {
                                btns.push(consts.editBtn(this, h, param))
                                btns.push(delBtn(this, h, param))
                            }

                            if (param.row.status == '0' && row.dAt == undefined)
                                btns.push(stopBtn(this, h, param))
                            else if (param.row.status == '1' && row.dAt == undefined)
                                btns.push(actBtn(this, h, param))
                            if (param.row.status == '0' && row.dAt == undefined && row.outStatus == '1' ) {
                                btns.push(h('Poptip', {
                                    props: {
                                        confirm: '',
                                        title: '您确定要删除这条数据吗？'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        'on-ok': () => {
                                        vm.outDepot(param.row)
                                    }
                                }
                            }, [h('Button', {
                                    props: {
                                        type: 'success',
                                        size: 'small'
                                    }
                                }, '出库')]))
                            }
                            if (param.row.status == '0' && row.dAt == undefined && row.outStatus == '0' && row.salesmenId!=undefined) {

                                let btnLabel=row.allLockStatus=='0'?'卡已全部解锁':'解锁';
                                let btnStyle=row.allLockStatus=='0'?'primary':'success';
                                btns.push(h('Button', {
                                    props: {
                                        type: btnStyle,
                                        size: 'small'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            vm.unlockCard(param.row)
                                        }
                                    }
                                }, btnLabel))
                            }
                            return h('div', btns)
                        }
                    }

                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>