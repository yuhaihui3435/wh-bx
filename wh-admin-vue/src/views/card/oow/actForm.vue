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
                <span>卡激活</span>
            </p>
            <Row>
                <Col span="12">
                <Row class="margin-top-10">
                    <Table :context="self" border :data="actRecordList" :columns="tableColums" stripe></Table></Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="changePage" show-total :pageSize="15"
                              show-elevator></Page>
                    </div>
                </div>
                </Col>
                <Col span="12">
                <Form :label-width="150" :model="depot" ref="formValidate" :rules="ruleValidate">
                    <Card>
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
                        <Row>
                            <Col span="12">
                            <FormItem label="起始号" prop="bNum">
                                <Input v-model="bNum"></Input>
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="结束号" prop="eNum">
                                <Input v-model="eNum"></Input>
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
                'totalPage': state => state.depot.totalPage,
                'total': state => state.depot.totalRow,
                'pageNumber': state => state.depot.pageNumber,
                'actRecordList': state => state.depot.actRecordList,
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
                this.depotModal = true;
                this.modalLoading = false;
            },
            save(){
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let param= {};
                        param=Object.assign(param,this.depot)
                        this.$store.dispatch('depot_', param).then((res) => {
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
            changePage(pn){
                let param = {
                    depotId: this.depot.id,
                    pn: pn
                }
                this.$store.dispatch('depot_actRecord', param)
            },
        },
        mounted () {
            vm=this;
        },
        data () {
            return {
                depotModal: false,
                modalLoading: false,
                bNum:'',
                eNum:'',
                tableColums: [
                    {
                        title: '起始号',
                        key: 'cardtypeTxt',
                    },
                    {
                        title: '终止号',
                        key: 'batch',
                    },],
                ruleValidate: {
                    bNum: [
                        {required: true,type: 'number', message: '数量不能为空',min:1, trigger: 'blur',transform: value => +value},
                        {type: 'number', message: '数量必须为数字', trigger: 'blur', transform: value => +value},
                        {validator(rule, value, callback, source, options) {
                            console.info(vm)
                            var errors = [];
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
                        {required: true,type: 'number', message: '数量不能为空',min:1, trigger: 'blur',transform: value => +value},
                        {type: 'number', message: '数量必须为数字', trigger: 'blur', transform: value => +value},
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
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
        }, watch: {}, components: {}
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>
