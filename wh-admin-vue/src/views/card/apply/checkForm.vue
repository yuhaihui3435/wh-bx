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
                <Form :label-width="150"  ref="formValidate">
                    <Card>
                        <Row>
                            <Col span="12">
                            <FormItem label="卡类型:">
                                {{cardapply.cardtypeTxt}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="批次:">
                                {{cardapply.batch}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="介质:">
                                {{cardapply.mediaTxt}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="数量:">
                                {{cardapply.num}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="数据状态:">
                                {{cardapply.statusTxt}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="创建时间:">
                                {{cardapply.cAt}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row v-show="cardapply.remark!=undefined">
                            <Col span="24">
                            <FormItem label="备注:">
                                {{cardapply.remark}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="审核状态:">
                                <RadioGroup v-model="checkStatus">
                                    <Radio label="00"><span>审核通过</span></Radio>
                                    <Radio label="02"><span>审核未通过</span></Radio>
                                </RadioGroup>
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="24">
                            <FormItem label="审核备注:">
                                <Input v-model="cardapply.checkReason" type="textarea" :rows="4"></Input>
                            </FormItem>
                            </Col>
                        </Row>
                    </Card>
                </Form>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
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
            })
        },
        methods: {
            vChange(b){
                //本模式窗体 隐藏时候恢复窗体的一些初始设置
                if (!b) {
                    this.modalLoading = false;
                    this.checkStatus = '00';
                }
            },
            open(){
                this.cardapplyModal = true;
                this.modalLoading = false;

            },
            save(){
                let vm = this;
                this.modalLoading = true;
                let param = Object.assign({}, this.cardapply)
                param['checkStatus'] = this.checkStatus;
                param['exe'] = "check";

                this.$store.dispatch('cardapply_update', param).then((res) => {
                    if (res && res == 'success') {
                        vm.cardapplyModal = false;
                        this.$store.dispatch('cardapply_page')
                    } else {
                        this.modalLoading = false;
                    }
                })

            },
        },
        mounted () {
        },
        data () {
            return {
                cardapplyModal: false,
                modalTitle: '卡申请审核',
                modalLoading: false,
                checkStatus: '00',
            }
        }, watch: {}, components: {}
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>
