<template>
    <div>
        <Modal
                v-model="cardapplyModal"
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
                    <Form :label-width="150" :model="cardapply" ref="formValidate" :rules="ruleValidate">
                        <Card>
                            <Row>
                                <Col span="12">
                                <FormItem label="卡类型" prop="cardtypeId">
                                    <Select v-model="cardapply.cardtypeId" clearable :disabled="!isAdd">
                                        <Option v-for="item in cardtypeList" :value="item.id" :key="item.id">
                                            {{item.name}}
                                        </Option>
                                    </Select>
                                </FormItem>
                                </Col>
                                <Col span="12">
                                <FormItem label="批次" prop="batch">
                                    <Input v-model="cardapply.batch"></Input>
                                </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="12">
                                <FormItem label="介质" prop="media">
                                    <Select v-model="cardapply.media" clearable>
                                        <Option v-for="item in mediaList" :value="item.id" :key="item.id">
                                            {{item.title}}
                                        </Option>
                                    </Select>
                                </FormItem>
                                </Col>
                                <Col span="12">
                                <FormItem label="数量" prop="num">
                                    <Input v-model="cardapply.num"></Input>
                                </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="24">
                                <FormItem label="备注" prop="remark">
                                    <Input v-model="cardapply.remark" type="textarea" :rows="4"></Input>
                                </FormItem>
                                </Col>
                            </Row>
                        </Card>
                    </Form>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button @click="reset" v-show="isAdd">重置</Button>
                <Button type="error" @click="cardapplyModal=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'

    export default {
        computed: {
            ...mapState({
                'cardapply': state => state.cardapply.cardapply,
                'cardtypeList': state => state.cardapply.cardtypeList,
                'mediaList': state => state.cardapply.mediaList,
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
                this.modalTitle = title;
                this.isAdd = isAdd;
                this.cardapplyModal = true;
                this.modalLoading = false;
                if (isAdd) {

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
                        param=Object.assign(param,this.cardapply)
                        this.$store.dispatch('cardapply_'+action, param).then((res) => {
                            if (res && res == 'success') {
                                vm.cardapplyModal = false;
                                this.$store.dispatch('cardapply_page')
                            } else {
                                this.modalLoading = false;
                            }
                        })


                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            reset(){
                this.$refs['formValidate'].resetFields()
            },
        },
        mounted () {
        },
        data () {
            return {
                cardapplyModal: false,
                isAdd: true,
                modalTitle: '新增卡申请',
                modalLoading: false,

                ruleValidate: {
                    cardtypeId: [
                        {type: 'number', required: true, message: '卡类型不能为空', trigger: 'change'},
                    ],
                    batch: [
                        {required: true, message: '批次不能为空', trigger: 'blur'},
                        {type: 'string', message: '批次长度不能超过四位', max: 4, trigger: 'blur'}
                    ],
                    num: [
                        {required: true,type: 'number', message: '数量不能为空', trigger: 'blur'},
                        {type: 'number', message: '数量必须为数字', trigger: 'blur', transform: value => +value},
                    ],
                },
            }
        }, watch: {}, components: {}
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>
