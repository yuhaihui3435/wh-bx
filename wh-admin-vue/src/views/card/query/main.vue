<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    卡查询
                </p>
                <Row>
                    <Col span="24" offset="0" align="left">
                    <Form inline>
                        <FormItem >
                            <Input v-model="code" placeholder="卡号" style="width: 200px"/>
                        </FormItem>

                        <FormItem >
                            <Input v-model="bNum" placeholder="起始号" style="width: 200px"/>-<Input v-model="eNum" placeholder="结束号" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="faceVal" placeholder="面值" style="width: 200px"/>
                        </FormItem>

                        <FormItem>
                            <DatePicker type="datetimerange"   placement="bottom-end" v-model="at" placeholder="激活时间" @on-change="dateTimeChange" style="width: 200px"></DatePicker>
                        </FormItem>
                        <FormItem>
                            <Select v-model="cardtypeId" placeholder="卡类型" clearable style="width:100px" align="left" @on-change="cardtypeChange">
                                <Option v-for="item in cardtypeList" :value="item.id" :key="item.id">{{ item.name }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem v-if="cardapplyList.length>0">
                            <Select v-model="cardapplyId"  placeholder="批次" clearable style="width:100px" align="left" >
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
                            <Select v-model="actStatus" placeholder="激活状态" clearable style="width:100px" align="left">
                                <Option v-for="item in yonList" :value="item.value" :key="item.value">{{ item.label}}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="lockStatus" placeholder="解锁状态" clearable style="width:100px" align="left">
                                <Option v-for="item in yonList" :value="item.value" :key="item.value">{{ item.label}}
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
                            <span @click="search(1)" style="margin: 0 10px;"><Button type="primary"
                                                                                  icon="search">搜索</Button></span>
                            <span @click="reset" style="margin: 0 10px;"><Button type="primary"
                                                                                 icon="ios-redo">重置</Button></span>
                        </FormItem>
                    </Form>
                    </Col>
                </Row>
                <!--<Row class="margin-top-10">-->
                    <!--<span @click="search" style="margin: 0 10px;"><Button type="error" icon="locked">锁定</Button></span>-->
                    <!--<span @click="search" style="margin: 0 10px;"><Button type="success" icon="unlocked">解锁</Button></span>-->
                <!--</Row>-->
                <Row class="margin-top-10">


                    <Table :context="self" border :data="cardsList" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="search" show-total :pageSize="15"
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

    let lockBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要锁定这张卡吗？'
            },
            style: {
                marginRight: '5px',
            },
            on: {
                'on-ok': () => {
                    vm.lock(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '锁定')]);
    }
    let unLockBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要解锁这张卡吗？'
            },
            style: {
                marginRight: '5px',
                'z-index':100
            },
            on: {
                'on-ok': () => {
                    vm.unlock(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'success',
                size: 'small'
            }
        }, '解锁')]);
    }

    export default {

        computed: {
            ...mapState({
                'cardsList': state => state.cards.cardsList,
                'totalPage': state => state.cards.totalPage,
                'total': state => state.cards.totalRow,
                'pageNumber': state => state.cards.pageNumber,
                'cardtypeList': state => state.cards.cardtypeList,
                'salesmenList': state => state.cards.salesmenList,
                'cardapplyList': state => state.cards.cardapplyList,
            })
        },
        methods: {
            search(pn){
                console.info(pn)
                if(pn==undefined)pn=1
                let param = {
                    cardtypeId: this.cardtypeId,
                    outStatus: this.outStatus,
                    status: this.status,
                    salesmenId: this.salesmenId,
                    actTime: this.actTime,
                    cardapplyId: this.cardapplyId,
                    code:this.code,
                    faceVal:this.faceVal,
                    actStatus:this.actStatus,
                    lockStatus:this.lockStatus,
                    outStatus:this.outStatus,
                    bNum:this.bNum,
                    eNum:this.eNum,
                    pn: pn
                }
                this.$store.dispatch('cards_page', param)
            },
            dateTimeChange(val){
                if(val[0]!='')
                    this.actTime=val[0]+' - '+val[1];
                else
                    this.actTime='';
            },
            cardtypeChange(val){
                if(val!='')
                    this.$store.dispatch('cards_cardapply_list',{ctId:val});
                else
                    this.$store.commit('set_cards_cardapply_list',[]);
            },
            lock(obj){
                this.$store.dispatch('cards_lock',{cardsId:obj.id}).then((res)=>{
                    this.search();
                });
            },
            unlock(obj){
                this.$store.dispatch('cards_unlock',{cardsId:obj.id}).then((res)=>{
                    this.search();
            });
            },
            reset(){
                this.cardapplyId='';//卡申请id
                this.cardtypeId='';//卡类型id
                this.code='';//卡号
                this.faceVal='';//面值
                this.actStatus='';//是否激活
                this.lockStatus='';//是否解锁
                this.outStatus='';
                this.bNum= '';//号段开始
                this.eNum= '';//号段结束
                this.status= '';//状态 查询条件
                this.salesmenId= '';//分配
                this.actTime= '';//出库时间
                this.at='';
            }

        },
        mounted () {
            this.$store.dispatch('cards_page')
            this.$store.commit('set_cards_cardapply_list',[]);
            this.$store.dispatch('cards_dataReady')
        },
        components: {
        },
        data () {
            return {
                at:'',
                cardapplyId:'',//卡申请id
                cardtypeId:'',//卡类型id
                code:'',//卡号
                faceVal:'',//面值
                actStatus:'',//是否激活
                lockStatus:'',//是否解锁
                outStatus:'',
                bNum: '',//号段开始
                eNum: '',//号段结束
                status: '',//状态 查询条件
                salesmenId: '',//分配业务员
                actTime: '',//出库时间
                self:this,
                statusList: consts.status,
                yonList: consts.yon,
                tableColums: [
//                    {
//                        type: 'selection',
//                        width: 60,
//                        align: 'center',
//                        fixed: 'left'
//                    },
                    {
                        title: '卡号',
                        key: 'code',
                        fixed: 'left',
                        width: 100,

                    },

                    {
                        title: '卡类型',
                        width: 100,
                        key: 'cardtypeName',
                    },
                    {
                        title: '批次',
                        width: 100,
                        key: 'batch',
                    },
                    {
                        title: '销售员',
                        width: 100,
                        key: 'salesmenName',
                    },
                    {
                        title: '卡面值',
                        width: 100,
                        key: 'faceVal',
                    },
                    {
                        title: '激活时间',
                        width: 100,
                        key: 'actAt',
                    },
                    {
                        title: '激活状态',
                        key: 'act',
                        width: 120,
                        render: (h, param) => {
                            let color = (param.row.act == '0') ? 'green' : 'red'
                            let txt = (param.row.act == '0') ? '激活' : '未激活'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },
                    {
                        title: '出库状态',
                        key: 'outStatus',
                        width: 120,
                        render: (h, param) => {
                            let color = (param.row.outStatus == '0') ? 'green' : 'red'
                            let txt = (param.row.outStatus == '0') ? '已出库' : '未出库'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },
                    {
                        title: '解锁状态',
                        key: 'isLocked',
                        width: 120,
                        render: (h, param) => {
                            let color = (param.row.isLocked == '0') ? 'green' : 'red'
                            let txt = (param.row.isLocked == '0') ? '已解锁' : '未解锁'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },

//                    {
//                        title: '卡余额',
//                        key: 'faceBalance',
//                    },
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
                        width: 200,
//                        fixed: 'right',
                        align: 'center',
                        render: (h, param) => {
                            let vm=this;
                            let btns = new Array;
                            let row = param.row;
                            btns.push(consts.viewBtn(this, h, param));
                            if(row.depotId!=undefined&&row.isLocked=='0'){
                                btns.push(lockBtn(this,h,param))
                            }else  if(row.depotId!=undefined&&(row.isLocked=='1' || row.isLocked==undefined)){
                                btns.push(unLockBtn(this,h,param))
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