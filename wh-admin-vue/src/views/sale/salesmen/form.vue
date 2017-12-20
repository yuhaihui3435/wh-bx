<template>
    <div>
        <Modal
                v-model="salesmenModal"
                :mask-closable="false"
                width="500"
                @on-visible-change="vChange"
                :styles="{top: '20px'}"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Row>
                <Col span="24">
                <Card>
                    <Form :label-width="80" :model="salesmen" ref="formValidate" :rules="ruleValidate">
                        <FormItem label="编号" prop="code">
                            <Input v-model="salesmen.code" ></Input>
                        </FormItem>
                        <FormItem label="姓名" prop="name">
                            <Input v-model="salesmen.name" ></Input>
                        </FormItem>
                        <FormItem label="身份证号" prop="idcard">
                            <Input v-model="salesmen.idcard" ></Input>
                        </FormItem>
                        <FormItem label="手机号" prop="phone">
                            <Input v-model="salesmen.phone" ></Input>
                        </FormItem>
                        <FormItem label="职级" prop="level">
                            <Select v-model="salesmen.level" clearable style="width:100px">
                                <Option v-for="item in saleLevelList" :value="item.id+''" :key="item.id+''">{{ item.title }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem label="类型" prop="type">
                            <Select v-model="salesmen.type" clearable style="width:100px">
                                <Option v-for="item in saleTypeList" :value="item.id+''" :key="item.id+''">{{ item.title }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem label="EMAIL" prop="email">
                            <Input v-model="salesmen.email" ></Input>
                        </FormItem>
                        <FormItem label="银行卡号" prop="bankcard">
                            <Input v-model="salesmen.bankcard" ></Input>
                        </FormItem>
                        <FormItem label="地址" prop="address">
                            <Input v-model="salesmen.address" ></Input>
                        </FormItem>

                        <FormItem label="上级" prop="pId">
                            <Select v-model="salesmen.pId" clearable style="width:100px">
                                <Option v-for="item in allSalesmenList" v-if="isAdd || (!isAdd && item.id!=salesmen.id)" :value="item.id+''" :key="item.id">{{ item.name }}
                                </Option>
                            </Select>
                        </FormItem>
                    </Form>

                </Card>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button @click="reset" v-show="isAdd">重置</Button>
                <Button type="error" @click="salesmenModal=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import IDValidator from '../../../libs/IDValidator'

    export default {
        computed: {
            ...mapState({
                'salesmen': state => state.salesmen.salesmen,
                'allSalesmenList': state => state.salesmen.allSalesmenList,
                'saleTypeList': state => state.salesmen.saleTypeList,
                'saleLevelList': state => state.salesmen.saleLevelList,

            })
        },
        methods: {
            open(title,isAdd){
              let vm=this;
              this.modalTitle=title;
              this.isAdd=isAdd;
              this.salesmenModal=true;
              this.modalLoading = false;
            },
            save(){
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action='save';
                        if(!vm.isAdd)
                            action='update';
                        this.$store.dispatch('salesmen_save',action).then((res) => {
                            if (res && res == 'success') {
                                vm.salesmenModal = false;
                                this.$store.dispatch('salesmen_page')
                            } else {
                                this.modalLoading = false;
                            }
                        })
                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            vChange(b){
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                    this.modalLoading = false;
                }
            },
            reset(){
                this.$refs['formValidate'].resetFields()
            }
        },
        mounted () {

        },
        data () {
            return {
                self: this,
                salesmenModal: false,
                isAdd:true,
                modalTitle: '新增销售人员',
                modalLoading: false,
                ruleValidate: {
                    code: [
                        {type: 'string', required: true, message: '编号不能为空', trigger: 'blur'},
                        {type: 'string', max: 50, message: '编号长度不能超过50', trigger: 'blur'}
                    ],
                    name: [
                        {required: true, message: '姓名不能为空',  trigger: 'blur'},
                        {type: 'string', message: '姓名长度不能超过50', max: 50, trigger: 'blur'}
                    ],
                    phone: [
                        {required: true, message: '手机号不能为空', max: 20, trigger: 'blur'},
                        {type: 'string', message: '请输入11位手机号', len: 11, trigger: 'blur'},
                        {
                            type: 'string',
                            message: '手机号码无效',
                            pattern: /^((13|14|15|17|18)[0-9]{1}\d{8})$/,
                            trigger: 'blur'
                        }
                    ],
                    email: [
                        {type: 'email', message: 'email格式不正确',  trigger: 'blur'},
                        {type: 'string', message: 'email长度不能超过100', max: 100, trigger: 'blur'}
                    ], idcard: [
                        {required: true, message: '身份证号不能为空',  trigger: 'blur'},
                        {type: 'string', max: 50, message: '证件号长度不能超过50', trigger: 'blur'},
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
                            let idv=new IDValidator();
                            let ret=idv.isValid(value);
                            if(!ret){
                                errors.push("身份证号码不合法")
                            }
                            callback(errors);
                        }}
                    ],
                    bankcard: [
                        {type: 'string', max: 50, message: '银行卡号长度不能超过50', trigger: 'blur'}
                    ],
                    address: [
                        {type: 'string', max: 150, message: '地址长度不能超过150', trigger: 'blur'}
                    ],
                    type: [
                        {required: true, message: '类型不能为空',  trigger: 'change'},
                    ],
                    level: [
                        {required: true, message: '职级不能为空',  trigger: 'change'},
                    ],
                },
            }
        },watch:{
                salesmen:{
                    handler:function(val,oldval){
                        //console.log(val)
                    },
                    deep:true//对象内部的属性监听，也叫深度监听
                }
        }
        }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>