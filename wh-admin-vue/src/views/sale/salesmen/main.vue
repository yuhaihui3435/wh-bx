<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    销售人员列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="android-add" @click="add">新增销售人员</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                    <Col span="12"  offset="4"  align="right">
                    <Form inline>
                        <FormItem prop="st">
                            <Select v-model="st" placeholder="类型" clearable style="width:100px">
                                <Option v-for="item in saleTypeList" :value="item.id" :key="item.id">{{ item.title }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem prop="sl">
                            <Select v-model="sl" placeholder="职级" clearable style="width:100px">
                                <Option v-for="item in saleLevelList" :value="item.id" :key="item.id">{{ item.title }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem prop="status">
                            <Select v-model="status" placeholder="状态" clearable style="width:100px">
                                <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label
                                    }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem prop="searchKey">
                            <Input v-model="searchKey" placeholder="请输入..." style="width: 200px"/>
                        </FormItem>
                        <FormItem>
                            <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                                  icon="search">搜索</Button></span>
                        </FormItem>
                    </Form>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="salesmenList" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="changePage" show-total
                              show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <salesmenForm ref="sf"></salesmenForm>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import salesmenForm from './form.vue'
    import consts from '../../../libs/consts'

    export default {

        computed: {
            ...mapState({
                'salesmenList': state => state.salesmen.salesmenList,
                'totalPage': state => state.salesmen.totalPage,
                'total': state => state.salesmen.totalRow,
                'pageNumber': state => state.salesmen.pageNumber,
                'salesmen': state => state.salesmen.salesmen,
                'saleTypeList': state => state.salesmen.saleTypeList,
                'saleLevelList': state => state.salesmen.saleLevelList,
                'allSalesmenList': state => state.salesmen.allSalesmenList,

            })
        },
        methods: {
            del(i){

                this.$store.dispatch('salesmen_del', {'id': i}).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('salesmen_page')
                    }
                });
            },
            edit(salesmen){
                this.$store.dispatch('salesmen_get', {id: salesmen.id}).then(() => {
                    this.$refs.sf.open('编辑销售人员', false);
                })

            },
            add(){
                this.$store.commit('set_salesmen',{})
                this.$refs.sf.open('新增销售人员', true)
            },
            search(){
                let param={type:this.st,level:this.sl,status:this.status,search:this.searchKey}
                this.$store.dispatch('salesmen_page', param)
            },
            changePage(pn){
                let param={type:this.st,level:this.sl,status:this.status,search:this.searchKey,pn:pn}
                this.$store.dispatch('salesmen_page', param)
            },
            refresh(){
                let param={type:this.st,level:this.sl,status:this.status,search:this.searchKey}
                this.$store.dispatch('salesmen_page')
            },

            stop(id){
                this.$store.dispatch('salesmen_updateStatus',{id:id,status:'1'}).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('salesmen_page')
                    }
                });
            },

            active(id){
                this.$store.dispatch('salesmen_updateStatus',{id:id,status:'0'}).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('salesmen_page')
                    }
                });
            }

        },
        mounted () {
            this.$store.dispatch('salesmen_page')
            this.$store.dispatch('salesmen_dataReady')
        },

        components: {
            salesmenForm: salesmenForm,
        },

        data () {
            return {
                st: '',//类型 查询条件
                sl: '',//职级 查询条件
                status: '',//状态 查询条件
                statusList: consts.status,
                searchKey: '',
                self: this,
                tableColums: [
                    {
                        title: '编号',
                        key: 'code',
                    },

                    {
                        title: '姓名',
                        key: 'name',
                    },

                    {
                        title: '身份证号',
                        key: 'idcard',
                    },
                    {
                        title: '手机号',
                        key: 'phone',
                    },
                    {
                        title: 'EMAIL',
                        key: 'email',
                    },
                    {
                        title: '银行卡号',
                        key: 'bankcard',
                    },
                    {
                        title: '地址',
                        key: 'address',
                    },
                    {
                        title: '上级',
                        key: 'parentTxt',
                    },
                    {
                        title: '职级',
                        key: 'levelTxt',
                    },
                    {
                        title: '类型',
                        key: 'typeTxt',
                    },
                    {
                        title: '状态',
                        key: 'statusTxt',
                        width:150,
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
                        title: '操作',
                        key: 'action',
                        width: 200,
                        align: 'center',
                        render: (h, param) => {
                            let btns = new Array;
                            btns.push(consts.editBtn(this, h, param))
                            btns.push(consts.delBtn(this, h, param))
                            if (param.row.status == '0')
                                btns.push(consts.stopBtn(this, h, param))
                            else
                                btns.push(consts.actBtn(this, h, param))
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