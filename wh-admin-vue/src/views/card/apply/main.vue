<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    卡申请列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="android-add" @click="add">新增申请</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                    <Col span="16" offset="0" align="right">
                    <Form inline>
                        <FormItem>
                            <Select v-model="cardtypeId" placeholder="卡类型" clearable style="width:100px" align="left">
                                <Option v-for="item in cardtypeList" :value="item.id" :key="item.id">{{ item.name }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="media" placeholder="介质" clearable style="width:100px" align="left">
                                <Option v-for="item in mediaList" :value="item.id" :key="item.id">{{ item.title }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="status" placeholder="状态" clearable style="width:100px" align="left">
                                <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label
                                    }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="checkStatus" placeholder="审核状态" clearable style="width:100px" align="left">
                                <Option v-for="item in checkStatusList" :value="item.value" :key="item.value">{{
                                    item.label
                                    }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="exeCard" placeholder="是否制卡" clearable style="width:100px" align="left">
                                <Option v-for="item in yonList" :value="item.value" :key="item.value">{{ item.label
                                    }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                                  icon="search">搜索</Button></span>
                        </FormItem>
                    </Form>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="cardapplyList" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" :page-size="pageSize" @on-change="changePage" show-total :pageSize="15"
                              show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <cardapplyForm ref="caf"></cardapplyForm>
        <cardapplyView ref="cav"></cardapplyView>
        <cardapplyCheckForm ref="cacf"></cardapplyCheckForm>
    </div>

</template>
<script>
    import {mapState} from 'vuex'
    import cardapplyForm from './form.vue'
    import cardapplyView from './view.vue'
    import cardapplyCheckForm from './checkForm.vue'
    import consts from '../../../libs/consts'

    const moment = require('moment');

    let delBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要删除这条数据吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.del(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '删除')]);
    };
    let stopBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要禁用这条数据吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.stop(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '禁用')]);
    }
    let actBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要激活这条数据吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.active(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'success',
                size: 'small'
            }
        }, '激活')]);
    }
    export default {

        computed: {
            ...mapState({
                'cardapplyList': state => state.cardapply.cardapplyList,
                'totalPage': state => state.cardapply.totalPage,
                'total': state => state.cardapply.totalRow,
                'pageNumber': state => state.cardapply.pageNumber,
                'pageSize': state => state.cardapply.pageSize,
                'cardapply': state => state.cardapply.cardapply,
                'cardtypeList': state => state.cardapply.cardtypeList,
                'mediaList': state => state.cardapply.mediaList,
            })
        },
        methods: {
            del(row){
                row['dAt'] = moment().format('YYYY-MM-DD HH:mm:ss');
                row['exe'] = 'del'
                this.$store.dispatch('cardapply_updateStatus', row).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('cardapply_page')
                    }
                });
            },
            edit(cardapply){
                let vm = this;
                this.$store.dispatch('cardapply_get', {id: cardapply.id}).then(() => {
                    if (vm.cardapply.status == '0' && vm.cardapply.checkStatus == '01' && vm.cardapply.exeCard == '1' && (vm.cardapply.dAt == undefined))
                        this.$refs.caf.open('编辑申请', false);
                    else
                        this.$Message.error("数据状态不正确，无法完成该操作，请刷新数据查看最新的数据状态");
                })

            },
            add(){
                this.$store.commit('set_cardapply', {})
                this.$refs.caf.open('新增申请', true)
            },
            view(id){
                this.$store.dispatch('cardapply_get', {id: id}).then(() => {
                    this.$refs.cav.open();
                })
            },
            search(){
                let param = {
                    cardtypeId: this.cardtypeId,
                    media: this.media,
                    status: this.status,
                    checkStatus: this.checkStatus,
                    exeCard: this.exeCard
                }
                this.$store.dispatch('cardapply_page', param)
            },
            changePage(pn){
                let param = {
                    cardtypeId: this.cardtypeId,
                    media: this.media,
                    status: this.status,
                    checkStatus: this.checkStatus,
                    exeCard: this.exeCard,
                    pn: pn
                }
                this.$store.dispatch('cardapply_page', param)
            },
            refresh(){
                let param = {
                    cardtypeId: this.cardtypeId,
                    media: this.media,
                    status: this.status,
                    checkStatus: this.checkStatus,
                    exeCard: this.exeCard
                }
                this.$store.dispatch('cardapply_page')
            },

            stop(row){
                row['status'] = '1';
                row['exe'] = 'stop';
                this.$store.dispatch('cardapply_update', row).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('cardapply_page')
                    }
                });
            },

            active(row){
                row['status'] = '0';
                row['exe'] = 'active';
                this.$store.dispatch('cardapply_update', row).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('cardapply_page')
                    }
                });
            }
            , createCard(row){
                this.$store.dispatch('cardapply_create', row).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('cardapply_page')
                    }
                });
            },
            exportToExcel(row){
                let env=consts.env;
                window.open(env+"/c01/exportCardsExcel?applyId="+row.id,'_blank');
            },
            check(row){
                this.$store.dispatch('cardapply_get', {id: row.id}).then(() => {
                    this.$refs.cacf.open();
                })
            }

        },
        mounted () {
            this.$store.dispatch('cardapply_page')
            this.$store.dispatch('cardapply_dataReady')
        },

        components: {
            cardapplyForm, cardapplyView,cardapplyCheckForm
        },

        data () {
            return {
                cardtypeId: '',//卡类型
                media: '',//职级 查询条件
                status: '',//状态 查询条件
                checkStatus: '',
                exeCard: '',
                self:this,
                statusList: consts.status,
                checkStatusList: consts.checkStatus,
                yonList: consts.yon,
                tableColums: [
                    {
                        title: '卡类型',
                        key: 'cardtypeTxt',
                        width:100,
                        fixed:'left'
                    },
                    {
                        title: '类型',
                        width:100,
                        key: 'cardtypeTypeTxt',
                    },

                    {
                        title: '批次',
                        width:100,
                        key: 'batch',
                    },

                    {
                        title: '介质',
                        width:100,
                        key: 'mediaTxt',
                    },
                    {
                        title: '数量',
                        width:100,
                        key: 'num',
                    },
                    {
                        title: '申请时间',
                        width:100,
                        key: 'cAtTxt',
                    },
                    {
                        title: '下载时间',
                        width:100,
                        key: 'downloadAt',
                    },
                    {
                        title: '操作员',
                        width:100,
                        key: 'operTxt',
                    },
                    {
                        title: '状态',
                        key: 'status',
                        width: 120,
                        render: (h, param) => {
                            let color = (param.row.status == '0') ? 'green' : 'red'
                            let txt = (param.row.status == '0') ? '正常' : '禁用'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },
                    {
                        title: '审核状态',
                        key: 'checkStatus',
                        width: 150,
                        render: (h, param) => {
                            let color = (param.row.checkStatus == '01') ? 'blue' : (param.row.checkStatus == '00') ? 'green' : 'red'
                            let txt = (param.row.checkStatus == '01') ? '等待审核' : (param.row.checkStatus == '00') ? '审核通过' : '审核未通过'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },
                    {
                        title: '制卡状态',
                        key: 'exeCard',
                        width: 120,
                        render: (h, param) => {
                            let color = (param.row.exeCard == '0') ? 'green' : 'red'
                            let txt = (param.row.exeCard == '0') ? '是' : '否'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },

                    {
                        title: '操作',
                        key: 'action',
                        width: 250,
                        align: 'center',
                        render: (h, param) => {
                            let vm=this;
                            let btns = new Array;
                            let row = param.row;
                            btns.push(consts.viewBtn(this, h, param));
                            if (row.checkStatus == '01' && row.dAt == undefined && row.exeCard == '1') {
                                btns.push(consts.editBtn(this, h, param))
                                btns.push(delBtn(this, h, param))
                            }

                            if (param.row.status == '0' && row.dAt == undefined)
                                btns.push(stopBtn(this, h, param))
                            else if (param.row.status == '1' && row.dAt == undefined)
                                btns.push(actBtn(this, h, param))
                            if (param.row.status == '0' && row.dAt == undefined && row.checkStatus == '01' && row.exeCard == '1') {
                                btns.push(h('Button', {
                                    props: {
                                        type: 'primary',
                                        size: 'small'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            vm.check(param.row)
                                        }
                                    }
                                }, '审核'))
                            }
                            if (param.row.status == '0' && row.dAt == undefined && row.checkStatus == '00' && row.exeCard == '1') {
                                btns.push(h('Poptip', {
                                    props: {
                                        confirm: '',
                                        title: '您确定要开始制卡吗？'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        'on-ok': () => {
                                            vm.createCard(param.row)
                                        }
                                    }
                                }, [h('Button', {
                                    props: {
                                        type: 'success',
                                        size: 'small'
                                    }
                                }, '制卡')]))
                            }
                            if (param.row.status == '0' && row.dAt == undefined && row.checkStatus == '00' && row.exeCard == '0') {
                                btns.push(h('Button', {
                                    props: {
                                        type: 'primary',
                                        size: 'small'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            vm.exportToExcel(param.row)
                                        }
                                    }
                                }, 'EXCEL导出'))
                            }
                            return h('div', btns)
                        }
                    }

                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>