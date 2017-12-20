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
                            <DatePicker type="datetimerange"   placement="bottom-end" placeholder="激活时间" @on-change="dateTimeChange" style="width: 200px"></DatePicker>
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
                            <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                                  icon="search">搜索</Button></span>
                        </FormItem>
                    </Form>
                    </Col>
                </Row>
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

            }
        },
        mounted () {
            this.$store.dispatch('cards_page')
            this.$store.dispatch('cards_dataReady')
        },
        components: {
        },
        data () {
            return {
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
                    {
                        title: '卡号',
                        key: 'code',
                    },

                    {
                        title: '卡类型',
                        key: 'cardtypeTxt',
                    },
                    {
                        title: '批次',
                        key: 'batch',
                    },
                    {
                        title: '销售员',
                        key: 'salesmenTxt',
                    },

                    {
                        title: '激活状态',
                        key: 'actTxt',
                    },

                    {
                        title: '激活时间',
                        key: 'actTime',
                    },
                    {
                        title: '解锁状态',
                        key: 'lockStatus',
                    },
                    {
                        title: '激活姓名',
                        key: 'actName',
                    },
                    {
                        title: '卡面值',
                        key: 'faceVal',
                    },
                    {
                        title: '卡余额',
                        key: 'faceBalance',
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
                            btns.push(consts.viewBtn(this, h, param));
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