<template>
    <div>
        <Modal
                v-model="depotModal"
                :mask-closable="false"
                width="900"
                @on-visible-change="vChange"
                :styles="{top: '20px'}"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>卡解锁</span>
            </p>

            <Row>
                <Col span="24" v-show="depot.allLockStatus=='0'">
                    <Alert type="success" show-icon>出库申请中的卡片已经全部解锁</Alert>
                </Col>
                <Col span="12" >
                <Card>
                    <p slot="title">
                        <Icon type="ios-list"></Icon>
                        已经解锁号段
                    </p>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="unlockRecordList" :columns="tableColums" stripe></Table></Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="changePage" show-total :pageSize="15"
                              show-elevator></Page>
                    </div>
                </div>
                </Card>
                </Col>

                <Col span="12" class="padding-left-10">
                <Form :label-width="150" :model="depot" ref="formValidate" :rules="ruleValidate">
                    <Card>
                        <p slot="title">
                            <Icon type="paper-airplane"></Icon>
                            号段解锁
                        </p>
                        <Row>
                            <Col span="12">
                            <FormItem label="卡类型">
                                <span >{{depot.cardtypeTxt}}</span>
                            </FormItem>
                            </Col>
                            <Col span="12" >
                            <FormItem label="批次">
                                <span >{{depot.batch}}</span>
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="出库编号" >
                                <span>{{depot.code}}</span>
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="销售员" >
                                <span>{{depot.salesmenTxt}}</span>
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="起始号" >
                                <span>{{depot.bNum}}</span>
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="结束号" >
                                <span>{{depot.eNum}}</span>
                            </FormItem>
                            </Col>
                        </Row>
                        <Row v-show="depot.allLockStatus=='1'">
                            <Col span="12">
                            <FormItem label="起始号" prop="jsBNum">
                                <Input v-model="depot.jsBNum"></Input>
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="结束号" prop="jsENum">
                                <Input v-model="depot.jsENum"></Input>
                            </FormItem>
                            </Col>
                        </Row>
                    </Card>
                </Form>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save" v-show="depot.allLockStatus=='1'">保存</Button>
                <Button type="error" @click="depotModal=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    let vm='';
    export default {
        computed: {
            ...mapState({
                'depot': state => state.depot.depot1,
                'totalPage': state => state.depot.totalPage1,
                'total': state => state.depot.totalRow1,
                'pageNumber': state => state.depot.pageNumber1,
                'unlockRecordList': state => state.depot.unlockRecordList,
            })
        },
        methods: {
            vChange(b){
                //本模式窗体 隐藏时候恢复窗体的一些初始设置
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                    this.modalLoading = false;
                }
            },
            open(){
                this.$refs['formValidate'].resetFields()
                this.depotModal = true;
                this.modalLoading = false;
//                console.info(this.depot);
//                if(this.depot.allLockStatus=='0'){
//                    this.$Notice.info({
//                        title: '通知',
//                        desc: '出库申请中的卡片已经全部被解锁'
//                    });
//                }
            },
            save(){

                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {

                        let param= {bNum:vm.depot.jsBNum,eNum:vm.depot.jsENum};
                        param=Object.assign(param,{depotId:vm.depot.id})
                        this.$store.dispatch('depot_unlockRecord_save', param).then((res) => {
                            if (res && res.resCode == 'success') {
                                vm.depotModal = false;
                                this.$store.dispatch('depot_unlockRecord_list').then(()=>{
                                    this.$store.dispatch('depot_page')
                                })
                            } else {
                                this.modalLoading = false;
                            }
                        })

                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            changePage(pn){
                let param = {
                    depotId: this.depot.id,
                    pn: pn
                }
                this.$store.dispatch('depot_unlockRecord_list', param)
            },
        },
        mounted () {
            vm=this;
        },
        data () {
            return {
                depotModal: false,
                modalLoading: false,
                self:this,
                tableColums: [
                    {
                        title: '起始号',
                        key: 'bNum',
                    },
                    {
                        title: '终止号',
                        key: 'eNum',
                    },
                    {
                        title: '解锁时间',
                        key: 'cAt',
                    },],
                ruleValidate: {
                    jsBNum: [
                        {required: true, message: '起始号不能为空', trigger: 'blur'},
                        {type: 'number', message: '起始号必须为数字',min:1, trigger: 'blur', transform: value => +value},
                        {validator(rule, value, callback, source, options) {

                            var errors = [];
                            value=value==undefined?-1:parseInt(value);
                            let jsENum=vm.depot.jsENum;
                            jsENum=jsENum==undefined?-1:parseInt(jsENum);
                            if(value>=jsENum&&jsENum>-1){
                                errors.push("起始号不能大于结束号");
                            }


                            let bNum=vm.depot.bNum;
                            if(value<bNum){
                                errors.push("解锁起始号不能小于起始号")
                            }

                            callback(errors);
                        }}
                    ],
                    jsENum: [
                        {required: true, message: '结束号不能为空', trigger: 'blur'},
                        {type: 'number', message: '结束号必须为数字',min:1, trigger: 'blur', transform: value => +value},
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
                            value=value==undefined?-1:parseInt(value);
                            let jsBNum=vm.depot.jsBNum;
                            jsBNum=jsBNum==undefined?-1:parseInt(jsBNum);
                            if(value<=jsBNum&&jsBNum>-1){
                                errors.push("结束号不能小于起始号");
                            }

                            let eNum=vm.depot.eNum;
                            if(value>eNum){
                                errors.push("解锁结束号不能大于结束号")
                            }


                            callback(errors);
                        }}
                    ],
                },
            }
        }, watch: {}, components: {}
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>
