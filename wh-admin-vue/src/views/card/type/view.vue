<template>
    <div>
        <Modal
                v-model="cardtypeModal"
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
                <Card>
                    <Tabs v-model="actTabName">
                        <TabPane label="基本信息" icon="home" name="baseInfo">

                            <Form :label-width="150"  >

                                <Card>

                                    <Row>
                                        <Col span="12">
                                        <FormItem label="编号:">
                                            {{cardtype.code}}
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="名称:" >
                                            {{cardtype.name}}
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span="12">
                                        <FormItem label="面值:" >
                                            {{cardtype.faceVal}}
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="类型:" >
                                                <div v-for="item in cttList"  :key="item.id+''">
                                                    <span v-if="item.id==cardtype.type+''">{{item.title}}</span>
                                                </div>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row v-show="!driverType">
                                        <Col span="12">
                                        <FormItem label="投保人年龄上限:" >
                                            {{cardtype.phAgeToplmt}}
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="投保人年龄下限:">
                                            {{cardtype.phAgeLowerlmt}}
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row v-show="!driverType">
                                        <Col span="12">
                                        <FormItem label="被投保人年龄上限:" >
                                        {{cardtype.ipAgeToplmt}}
                                            年
                                            {{cardtype.ipAgeToplmtDay}}
                                            天
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="被投保人年龄下限:" >
                                            {{cardtype.ipAgeLowerlmt}}
                                        </FormItem>
                                        </Col>
                                    </Row>

                                    <Row v-show="!driverType">
                                        <Col span="12">
                                        <FormItem label="是否多人:" >
                                                <div v-for="item in statusList" >
                                                    <span v-if="item.value==cardtype.manyPeople">{{item.label}}</span>
                                                </div>
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="投保人数:" >
                                            {{cardtype.peopleCount}}
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span="12">
                                        <FormItem label="有效年限:" >
                                            {{cardtype.finiteEffect}}
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="单身份证可激活张数:" >
                                            {{cardtype.actCount}}
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row v-show="!driverType">
                                        <Col span="12" v-show="cardtype.category!=''">
                                        <FormItem label="职类:" >
                                                <div v-for="item in ioList">
                                                    <span v-if="item.id==cardtype.category+''">{{item.title}}</span>
                                                </div>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span="24">
                                        <FormItem label="激活成功提示消息:" >
                                            {{cardtype.actMsg}}
                                        </FormItem>
                                        </Col>
                                    </Row>
                                </Card>
                            </Form>

                        </TabPane>
                        <TabPane label="协议信息" icon="document" name="protocol">

                            <Card>
                                <div v-html='cardtype.protocol'></div>
                            </Card>

                        </TabPane>
                        <TabPane label="服务凭证信息" icon="document-text" name="serviceCert">

                            <Card>
                                <div v-html='cardtype.serviceCert'></div>
                            </Card>

                        </TabPane>
                        <TabPane label="保险条款文件" icon="ios-folder" name="InsuranceClauseDocument">

                            <Card class="margin-top-10">
                                <p slot="title">
                                    <Icon type="document"></Icon>
                                    已上传文件
                                </p>
                                <div class="margin-top-10" v-show="cardtype.clauseList&&cardtype.clauseList.length>0">
                                    <Card>
                                        <div>
                                            <div v-for="(item,index) in cardtype.clauseList">
                                                <template>
                                                    <Row>
                                                        <Icon type="document"></Icon>
                                                        &nbsp;{{item.name}}&nbsp;&nbsp;
                                                        <Icon type="eye"
                                                              @click.native="handleView(index)"
                                                              color="blue">
                                                        </Icon>
                                                    </Row>
                                                </template>
                                            </div>
                                        </div>
                                    </Card>
                                </div>
                            </Card>
                        </TabPane>
                    </Tabs>
                </Card>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="error" @click="cardtypeModal=false">关闭</Button>
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
                'cardtype': state => state.cardtype.cardtype,
                'cttList': state => state.cardtype.cttList,
                'ioList': state => state.cardtype.ioList,
            })
        },
        methods: {
            open(){
                this.cardtypeModal = true;
                let value=this.cardtype.type;
                for (let t of this.cttList) {
                    if (t.id == value) {
                        if (t.text == 'driverInsurance') {
                            this.driverType = true;
                        } else {
                            this.driverType = false;
                        }
                    }
                }
            },
            vChange(b){
                //本模式窗体 隐藏时候恢复窗体的一些初始设置
                if (!b) {
                    this.actTabName = "baseInfo"//恢复tab的初始状态选中第一个tab
                }
            },

            handleView(index){
                let obj=this.cardtype.clauseList[index];
                console.info(obj)
                let url=this.cardtype.accessUrl;

                window.open(url,'_blank');
            },
        },
        mounted () {

        },
        data () {
            return {
                cardtypeModal: false,
                modalTitle: '卡类型详细信息',
                driverType: false,
                actTabName: 'baseInfo',
                statusList: consts.yon,

            }
        },
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>