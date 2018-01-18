<template>
    <div>
        <Modal
                v-model="actModal"
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
                            <Col span="24" align="middle"><h1>卡信息</h1> </Col>
                        </Row>

                        <Row>
                            <Col span="8">
                            <FormItem label="卡号:" >
                                {{card.code}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="卡类型:" >
                                {{card.cardtypeName}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="面值:" >
                                {{card.faceVal}}
                            </FormItem>
                            </Col>
                        </Row>
                        <div v-if="card.ccph">
                        <Row>
                            <Col span="24" align="middle"><h1>投保人信息</h1> </Col>
                        </Row>
                        <Row>
                            <Col span="8">
                            <FormItem label="投保人/公司名称:" >
                                {{card.ccph.name}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="投保人/公司证件类型:" >
                                {{card.ccph.certTypeTxt}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="投保人/公司证件号:" >
                                {{card.ccph.certCode}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="8">
                            <FormItem label="联系人:" >
                                {{card.ccph.linkName}}
                            </FormItem>
                            </Col>

                            <Col span="8" >
                            <FormItem label="联系电话:" >
                                {{card.ccph.tel}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="EMAIL:" >
                                {{card.ccph.email}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="24">
                            <FormItem label="地址:" >
                                {{card.ccph.fullAddress}}
                            </FormItem>
                            </Col>

                        </Row>
                        </div>
                        <div v-if="card.ccip">
                        <Row>
                            <Col span="24" align="middle"><h1>被投保车辆信息</h1> </Col>
                        </Row>
                            <Row>
                                <Col span="8">
                                <FormItem label="车辆类型:" >
                                    {{card.ccip.carTypeTxt}}
                                </FormItem>
                                </Col>
                                <Col span="8">
                                <FormItem label="营运性质:" >
                                    {{card.ccip.propTxt}}
                                </FormItem>
                                </Col>
                                <Col span="8">
                                <FormItem label="座位数:" >
                                    {{card.ccip.seatCount}}
                                </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="8">
                                <FormItem label="车牌号:" >
                                    {{card.ccip.plateNum}}
                                </FormItem>
                                </Col>
                                <Col span="8">
                                <FormItem label="车架子号:" >
                                    {{card.ccip.frameNum}}
                                </FormItem>
                                </Col>
                                <Col span="8">
                                <FormItem label="发动机号:" >
                                    {{card.ccip.engineNum}}
                                </FormItem>
                                </Col>
                            </Row>
                        </div>
                        <div v-if="card.policyNum">
                        <Row>
                            <Col span="24" align="middle"><h1>保险单信息</h1> </Col>
                        </Row>
                        <Row>
                            <Col span="8">
                            <FormItem label="保单号:" >
                                {{card.policyNum}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="报案电话:" >
                                {{card.reportTel}}
                            </FormItem>
                            </Col>

                        </Row>
                        </div>
                        <div v-if="electronicPolicy.length>0">
                        <Row >
                            <Col span="24" align="middle"><h1>电子保单</h1> </Col>
                        </Row>
                        <Row v-for="(y,index) in electronicPolicy" :key="index">
                            <Col span="24">
                            <FormItem label="电子保单" >
                                <a :href="y.accessUrl" target="_blank">{{y.name}}</a>
                            </FormItem>
                            </Col>
                        </Row>
                        </div>
                    </Card>
                </Form>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="error" @click="actModal=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    import {formatDate} from '../../../libs/date'

    export default {
        computed: {
            ...mapState({
                'card': state => state.cards.cards,
                'electronicPolicy': state => state.cards.electronicPolicy,
            }),

        },
        methods: {

            open(){
                this.actModal = true;
            },
        },
        mounted () {
        },
        data () {
            return {
                actModal: false,
                modalTitle: '详细信息',
            }
        }, watch: {}, components: {},filters:{
            formatDate(time){
                let date = new Date(time)
                return formatDate(date, 'yyyy-MM-dd')
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>
