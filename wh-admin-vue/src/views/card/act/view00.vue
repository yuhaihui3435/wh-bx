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
                        <Row>
                            <Col span="24" align="middle"><h1>投保人信息</h1> </Col>
                        </Row>
                        <Row>
                            <Col span="8">
                            <FormItem label="投保人姓名:" >
                                {{card.cph==undefined?"":card.cph.name}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="投保人证件类型:" >
                                {{card.cph==undefined?"":card.cph.certTypeTxt}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="投保人证件号:" >
                                {{card.cph==undefined?"":card.cph.idCard}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="8">
                            <FormItem label="性别:" >
                                {{card.cph==undefined?"":card.cph.sexTxt}}
                            </FormItem>
                            </Col>
                            <Col span="8" >
                            <FormItem label="生日:" >
                                {{card.cph==undefined?"":card.cph.birthDay | formatDate}}
                            </FormItem>
                            </Col>
                            <Col span="8" >
                            <FormItem label="联系电话:" >
                                {{card.cph==undefined?"":card.cph.tel}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="8">
                            <FormItem label="EMAIL:" >
                                {{card.cph==undefined?"":card.cph.email}}
                            </FormItem>
                            </Col>

                        </Row>
                        <Row>
                            <Col span="24" align="middle"><h1>被投保人信息</h1> </Col>
                        </Row>
                        <div v-for="(x,index) in card.cip" :key="index">
                        <Row>
                            <Col span="8">
                            <FormItem label="与投保人关系:" >
                                {{x.relationshipTxt}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="被投保人姓名:" >
                                {{x.name}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="被投保人生日:" >
                                {{x.birthDay | formatDate}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="8">
                            <FormItem label="被投保人证件类型:" >
                                {{x.certTypeTxt}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="被投保人证件号:" >
                                {{x.idCard}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="被投保人性别:" >
                                {{x.sexTxt}}
                            </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="8">
                            <FormItem label="被投保人联系电话:" >
                                {{x.tel}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="受益人:" >
                                {{x.beneficiary}}
                            </FormItem>
                            </Col>
                            <Col span="8">
                            <FormItem label="被投保人职业:" >
                                {{x.jobTxt}}
                            </FormItem>
                            </Col>
                        </Row>
                        </div>
                        <Row v-if="card.policyNum">
                            <Col span="24" align="middle"><h1>保险单信息</h1> </Col>
                        </Row>
                        <Row v-if="card.policyNum">
                            <Col span="12">
                            <FormItem label="保单号:" >
                                {{card.policyNum}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="报案电话:" >
                                {{card.reportTel}}
                            </FormItem>
                            </Col>

                        </Row>
                        <Row v-if="card.policyNum">
                            <Col span="12">
                            <FormItem label="保险开始时间:" >
                                {{card.bAt}}
                            </FormItem>
                            </Col>
                            <Col span="12">
                            <FormItem label="保险终止时间:" >
                                {{card.eAt}}
                            </FormItem>
                            </Col>

                        </Row>
                        <Row v-if="electronicPolicy.length>0">
                            <Col span="24" align="middle"><h1>电子保单</h1> </Col>
                        </Row>
                        <Row v-for="(y,index) in electronicPolicy" :key="index">
                            <Col span="24">
                            <FormItem label="电子保单" >
                                <a :href="y.accessUrl" target="_blank">{{y.name}}</a>
                            </FormItem>
                            </Col>
                        </Row>
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
                modalTitle: '保单详细信息',
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
