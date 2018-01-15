<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    已激活意外险查询
                </p>
                <Row>
                    <Col span="24" offset="0" align="left">
                    <Form inline>
                        <FormItem >
                            <Input v-model="code" placeholder="卡号" style="width: 200px"/>
                        </FormItem>

                        <FormItem >
                            <Input v-model="exportCode" placeholder="导出编号" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="policyNum" placeholder="保单号" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="phName" placeholder="投保人姓名" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="phIdNum" placeholder="投保人证件号" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="ipName" placeholder="被投保人姓名" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="ipIdNum" placeholder="被投保人证件号" style="width: 200px"/>
                        </FormItem>

                        <FormItem>
                            <DatePicker type="datetimerange" v-model="at"   placement="bottom-end" placeholder="激活时间" @on-change="dateTimeChange" style="width: 200px"></DatePicker>
                        </FormItem>
                        <FormItem>
                            <Select v-model="exportStatus" placeholder="导出状态" clearable style="width:100px" align="left">
                                <Option v-for="item in yonList" :value="item.value" :key="item.value">{{ item.label}}
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
                    <Poptip
                            confirm
                            title="您确认到执行导出操作吗？"
                            @on-ok="e"
                            @on-cancel=""><span  style="margin: 0 10px;"><Button type="primary" icon="ios-download">按勾选导出</Button></span></Poptip>
                    <Poptip
                            confirm
                            title="您确认到执行导出操作吗？"
                            @on-ok="eByQuery"
                            @on-cancel=""><span  style="margin: 0 10px;"><Button type="primary" icon="ios-download">按查询条件导出</Button></span></Poptip>
                    <Poptip
                            confirm
                            title="你确认要执行导入操作吗？"
                            @on-ok="i"
                            @on-cancel=""><span  style="margin: 0 10px;"><Button type="primary" icon="ios-upload">导入</Button></span></Poptip>
                </Row>
                <Row class="margin-top-10">


                    <Table :context="self" border :data="cardsList" :columns="tableColums" @on-selection-change="getSelection" stripe></Table>
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

    let lockBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要锁定【'+param.row.code+'】这张卡吗？'
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
    let unActBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定【'+param.row.code+'】撤销到未激活吗？'
            },
            style: {
                marginRight: '5px',
            },
            on: {
                'on-ok': () => {
                    vm.unAct(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '撤销到未激活')]);
    }


    export default {

        computed: {
            ...mapState({
                'cardsList': state => state.cards.cardsList_00,
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
                    exportStatus:this.exportStatus,
                    salesmenId: this.salesmenId,
                    actTime: this.actTime,
                    code:this.code,
                    policyNum:this.policyNum,
                    actStatus:this.actStatus,
                    cardtypeType:this.cardtypeType,
                    phIdNum:this.phIdNum,
                    exportCode:this.exportCode,
                    phName:this.phName,
                    ipName:this.ipName,
                    ipIdNum:this.ipIdNum,
                    pn: pn
                }
                this.$store.dispatch('cards_page_00', param)
            },
            dateTimeChange(val){
                console.info(this.at)
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
            unAct(obj){
                this.$store.dispatch('cards_unAct',{cardsId:obj.id}).then((res)=>{
                    this.search();
                });
            },
            e(){
                if(this.selections.length==0){
                    this.$Message.warning("请选择要导出的卡数据")
                    return ;
                }
                let ids=[];
                for(var i=0;i<selections.length;i++){
                    ids.push(selections[i].id)
                }
                ids=ids.join(",");




            },
            eByQuery(){

            },
            i(){

            },
            uploadBD(obj){

            },
            getSelection(selection){
                this.selections=selection;
            },
            reset(){
                    this.exportStatus='';
                    this.exportCode='';
                    this.phName='';
                    this.code='';//卡号
                    this.salesmenId= '';//分配业务员
                    this.actTime='';//
                    this.policyNum='';
                    this.phIdNum='';
                    this.ipName='';
                    this.ipIdNum='';
                    this.at='';
            }

        },
        mounted () {
            let param = {
                cardtypeType:this.cardtypeType,
                actStatus:this.actStatus,
            }
            this.$store.dispatch('cards_page_00',param)
            this.$store.commit('set_cards_cardapply_list',[]);
            this.$store.dispatch('cards_dataReady')
        },
        components: {

        },
        data () {
            return {
                at:'',
                selections:[],
                exportStatus:'',
                cardtypeType:407,
                exportCode:'',
                phName:'',
                code:'',//卡号
                actStatus:'0',//是否激活
                salesmenId: '',//分配业务员
                actTime: '',//
                policyNum:'',
                phIdNum:'',
                ipName:'',
                ipIdNum:'',
                self:this,
                yonList: consts.yon,
                tableColums: [
                     {
                        type: 'selection',
                        width: 60,
                        align: 'center',
                        fixed: 'left'
                    },
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
                        title: '投保人姓名',
                        width: 100,
                        render:(h,param)=>{
                            return h('div',param.row.cph.name)
                        }
                    },
                    {
                        title: '投保人证件号',
                        width: 200,

                        render:(h,param)=>{
                            return h('div',param.row.cph.idCard)
                        }
                    },
                    {
                        title: '投保人电话',
                        width: 150,

                        render:(h,param)=>{
                            return h('div',param.row.cph.tel)
                        }
                    },
                    {
                        title: '投保人EMAIL',
                        width: 250,

                        render:(h,param)=>{
                            return h('div',param.row.cph.email)
                        }
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 350,
                        align: 'center',
                        render: (h, param) => {
                            let vm=this;
                            let btns = new Array;
                            let row = param.row;
                            btns.push(consts.viewBtn(this, h, param));
                            if(row.act=='0'&&row.isLocked=='0'&&row.exportCode)
                            btns.push(consts.uploadBDBtn(this, h, param));

                            if(row.act=='0')
                            btns.push(unActBtn(this,h,param))
                            if(row.isLocked=='0')
                            btns.push(lockBtn(this,h,param))

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