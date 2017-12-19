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
                <span>{{modalTitle}}</span>
            </p>
            <Row>
                <Col span="24">
                <Form :label-width="150" :model="depot" ref="formValidate" :rules="ruleValidate">
                    <Card>
                        <Row>
                            <Col span="12">
                            <FormItem label="卡类型" prop="cardtypeId">
                                <Select v-model="depot.cardtypeId" clearable v-show="isAdd" @on-change="cardtypeChange">
                                    <Option v-for="item in cardtypeList" :value="item.id" :key="item.id">
                                        {{item.name}}
                                    </Option>
                                </Select>
                                <span v-show="!isAdd">{{depot.cardtypeTxt}}</span>
                            </FormItem>
                            </Col>
                            <Col span="12" >
                            <FormItem label="批次" prop="cardapplyId"  v-show="(depot.cardapplyId!=undefined&&depot.cardapplyId!='')||cardapplyList.length>0">
                                <Select v-model="depot.cardapplyId" @on-change="batchChange" clearable v-show="isAdd" >
                                    <Option v-for="item in cardapplyList" :value="item.id" :key="item.id">
                                        {{item.batch}}
                                    </Option>
                                </Select>
                                <span v-show="!isAdd">{{depot.batch}}</span>
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="出库编号" prop="code">
                                <Input v-model="depot.code"></Input>
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="销售员" prop="salesmenId">
                                <Select v-model="depot.salesmenId" placeholder="业务员" clearable style="width:100px" align="left">
                                    <Option v-for="item in salesmenList" :value="item.id" :key="item.id">{{ item.name}}
                                    </Option>
                                </Select>
                            </FormItem>
                            </Col>
                        </Row>
                        <Row v-show="recommendNum.bNum!=undefined">
                            <Col span="12">
                            <FormItem label="可用的最小起始编号" >
                                <span>{{recommendNum.bNum}}</span>
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="可出库的卡数量" >
                                <span>{{recommendNum.eNum}}</span>
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="起始号" prop="bNum">
                                <Input v-model="depot.bNum"></Input>
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="结束号" prop="eNum">
                                <Input v-model="depot.eNum"></Input>
                            </FormItem>
                            </Col>
                        </Row>
                    </Card>
                </Form>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
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
                'depot': state => state.depot.depot,
                'cardtypeList': state => state.depot.cardtypeList,
                'salesmenList': state => state.depot.salesmenList,
                'recommendNum': state => state.depot.recommendNum,
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
            open(title, isAdd){
                //this.$refs['formValidate'].resetFields()
                this.modalTitle = title;
                this.isAdd = isAdd;
                this.depotModal = true;
                this.modalLoading = false;
                if (isAdd) {
                    this.$store.commit('set_depot',{bNum:'',eNum:''})
                } else {


                }
            },
            save(){
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action='save';
                        let param= {};

                        if (!vm.isAdd){
                            action='update'
                            param['exe']='update'

                        }
                        param=Object.assign(param,this.depot)
                        this.$store.dispatch('depot_'+action, param).then((res) => {
                            if (res && res == 'success') {
                                vm.depotModal = false;
                                this.$store.dispatch('depot_page')
                            } else {
                                this.modalLoading = false;
                            }
                        })


                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            cardtypeChange(val){
                if(!(this.isAdd))return;
                this.depot.cardapplyId='';
                if(val!='')
                    this.$store.dispatch('depot_cardapply_list1',{ctId:val}).then((list)=>{
                        this.cardapplyList=list;
                    });
                else
                    this.cardapplyList=[];
            },
            batchChange(val){
                if(val!='')
                this.$store.dispatch('depot_recommendNum',{applyId:val})
            }
        },
        mounted () {
            vm=this;

        },
        data () {
            return {
                depotModal: false,
                isAdd: true,
                modalTitle: '新增卡出库',
                modalLoading: false,
                cardapplyList:[],
                ruleValidate: {
                    cardtypeId: [
                        {type: 'number', required: true, message: '请选择卡类型', trigger: 'change'},
                    ],
                    cardapplyId: [
                        {type: 'number', required: true, message: '请选择批次', trigger: 'change'},
                    ],
                    code: [
                        {required: true, message: '出库编号不能为空', trigger: 'blur'},
                    ],
                    salesmenId: [
                        {type: 'number', required: true, message: '请选择销售员', trigger: 'change'},
                    ],
                    bNum: [
//                        {required: true, message: '起始号不能为空', trigger: 'blur'},
//                        {type: 'number', message: '起始号必须为数字',min:1, trigger: 'blur', transform: value => +value},
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
                            if(value==''){
                                errors.push("起始号不能为空");
                                callback(errors)
                                return ;
                            }

                            if (!(/(^[1-9]\d*$)/.test(value))) {
                                errors.push("起始号必须为正整数")
                                callback(errors)
                                return ;
                            }


                            value=value==undefined?-1:parseInt(value);
                            let eNum=vm.depot.eNum;
                            eNum=eNum==undefined?-1:parseInt(eNum);
                            if(value>=eNum&&eNum>-1){
                                errors.push("起始号不能大于结束号");
                            }
                            callback(errors);
                        }}
                    ],
                    eNum: [
//                        {required: true, message: '结束号不能为空', trigger: 'blur'},
//                        {type: 'number', message: '结束号必须为数字', trigger: 'blur',min:1, transform: value => +value},
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
                            if(value==''){
                                errors.push("结束号不能为空");
                                callback(errors)
                                return ;
                            }

                            if (!(/(^[1-9]\d*$)/.test(value))) {
                                errors.push("结束号必须为正整数")
                                callback(errors)
                                return ;
                            }
                            value=value==undefined?-1:parseInt(value);
                            let bNum=vm.depot.bNum;
                            bNum=bNum==undefined?-1:parseInt(bNum);
                            if(value<=bNum&&bNum>-1){
                                errors.push("结束号不能小于起始号");
                            }
                            callback(errors);
                        }}
                    ],
                },
            }
        }, watch: {
                depot(curVal,oldVal){
                    console.info(curVal,oldVal)
                }

        }, components: {}
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>
