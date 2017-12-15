<template>
    <div>
        <Modal
                v-model="cardapplyModal"
                :mask-closable="false"
                width="900"
                :styles="{top: '20px'}"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Row>
                <Col span="24">
                <Form :label-width="150" >
                    <Card>
                        <Row>
                            <Col span="12">
                            <FormItem label="卡类型:" >
                                {{cardapply.cardtypeTxt}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="批次:" >
                                {{cardapply.batch}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="介质:" >
                                {{cardapply.mediaTxt}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="数量:" >
                                {{cardapply.num}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="数据状态:" >
                                {{cardapply.statusTxt}}
                            </FormItem>
                            </Col>
                            <Col span="12" >
                            <FormItem label="审核状态:" >
                                {{cardapply.checkStatusTxt}}
                            </FormItem>
                            </Col>

                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="制卡状态:" >
                                {{cardapply.exeCardTxt}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="审核人员:" >
                                {{cardapply.checkOperTxt}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="12">
                            <FormItem label="创建时间:" >
                                {{cardapply.cAt}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="下载时间:" >
                                {{cardapply.downloadAt}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="24">
                            <FormItem label="审核备注:" >
                                {{cardapply.checkReason}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="24">
                            <FormItem label="备注:" >
                                {{cardapply.remark}}
                            </FormItem>
                            </Col>
                        </Row>
                    </Card>
                </Form>
                </Col>
            </Row>
            <div slot="footer">
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

            open(){
                this.cardapplyModal = true;
            },
        },
        mounted () {
        },
        data () {
            return {
                cardapplyModal: false,
                modalTitle: '申请详细信息',
            }
        }, watch: {}, components: {}
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>
