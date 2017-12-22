<template>
    <div>
        <Modal
                v-model="cardsModal"
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
                <Tabs type="card">
                    <TabPane label="卡片基本信息">
                        <Form :label-width="150" >
                            <Card>
                                <Row>
                                    <Col span="12">
                                    <FormItem label="卡号:" >
                                        {{cards.code}}
                                    </FormItem>
                                    </Col>
                                    <Col span="12">
                                    <FormItem label="面值:" >
                                        {{cards.faceVal}}
                                    </FormItem>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col span="12">
                                    <FormItem label="卡类型:" >
                                        {{cards.cardtypeName}}
                                    </FormItem>
                                    </Col>
                                    <Col span="12">
                                    <FormItem label="卡分类:" >
                                        {{cards.cardtypeType}}
                                    </FormItem>
                                    </Col>
                                </Row>
                                <Row>
                                    <Col span="12">
                                    <FormItem label="批次:" >
                                        {{cards.batch}}
                                    </FormItem>
                                    </Col>
                                    <Col span="12">
                                    <FormItem label="数据状态:" >
                                        {{cards.status=='0'?'正常':'停用'}}
                                    </FormItem>
                                    </Col>
                                </Row>
                                <Row>
                                    <Col span="12">
                                    <FormItem label="是否出库:" >
                                        {{cards.dePotId==undefined?'未出库':'已出库'}}
                                    </FormItem>
                                    </Col>
                                    <Col span="12">
                                    <FormItem label="是否解锁:" >
                                        {{cards.isLocked=='0'?'已解锁':'未解锁'}}
                                    </FormItem>
                                    </Col>
                                </Row>
                                <Row>
                                    <Col span="12">
                                    <FormItem label="是否激活:" >
                                        {{cards.act=='0'?'已激活':'未激活'}}
                                    </FormItem>
                                    </Col>
                                    <Col span="12">
                                    <FormItem label="激活时间:" v-show="cards.actAt!=undefined">
                                        {{cards.actAt}}
                                    </FormItem>
                                    </Col>
                                </Row>
                            </Card>
                        </Form>
                    </TabPane>
                    <TabPane label="投保人信息"></TabPane>
                    <TabPane label="被投保人信息"></TabPane>
                </Tabs>

                </Col>
            </Row>
            <div slot="footer">
                <Button type="error" @click="cardsModal=false">关闭</Button>
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
                'cards': state => state.cards.cards,
            })
        },
        methods: {

            open(){

                this.cardsModal = true;
            },
        },
        mounted () {

        },
        data () {
            return {
                cardsModal: false,
                modalTitle: '卡详细信息',
            }
        }, watch: {}, components: {}
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>
