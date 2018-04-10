<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    卡类型列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="android-add" @click="add">新增卡类型</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                    <Col span="16"  offset="0"  align="right">
                    <Form inline>
                        <FormItem prop="type">
                            <Select v-model="type" placeholder="类型" clearable style="width:100px" align="left">
                                <Option v-for="item in cttList" :value="item.id" :key="item.id">{{ item.title }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem prop="category">
                            <Select v-model="category" placeholder="职类" clearable style="width:100px" align="left">
                                <Option v-for="item in ioList" :value="item.id" :key="item.id">{{ item.title }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem prop="status">
                            <Select v-model="status" placeholder="状态" clearable style="width:100px" align="left">
                                <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label
                                    }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem prop="code">
                            <Input v-model="code" placeholder="请输入编号..." style="width: 100px"/>
                        </FormItem>
                        <FormItem prop="name">
                            <Input v-model="name" placeholder="请输入名称..." style="width: 100px"/>
                        </FormItem>
                        <FormItem>
                            <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                                  icon="search">搜索</Button></span>
                        </FormItem>
                    </Form>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="cardtypeList" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="changePage" show-total :page-size="15"
                              show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <cardtypeForm ref="ctf"></cardtypeForm>
        <cardtypeView ref="ctv"></cardtypeView>
    </div>

</template>
<script>
    import {mapState} from 'vuex'
    import cardtypeForm from './form.vue'
    import cardtypeView from './view.vue'
    import consts from '../../../libs/consts'

    export default {

        computed: {
            ...mapState({
                'cardtypeList': state => state.cardtype.cardtypeList,
                'totalPage': state => state.cardtype.totalPage,
                'total': state => state.cardtype.totalRow,
                'pageNumber': state => state.cardtype.pageNumber,
                'cardtype': state => state.cardtype.cardtype,
                'cttList': state => state.cardtype.cttList,
                'ioList': state => state.cardtype.ioList,
            })
        },
        methods: {
            del(i){

                this.$store.dispatch('cardtype_del', {'id': i}).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('cardtype_page')
                    }
                });
            },
            edit(cardtype){
                this.$store.dispatch('cardtype_get', {id: cardtype.id}).then(() => {
                    this.$refs.ctf.open('编辑卡类型', false);
                })
            },
            add(){
                this.$store.commit('set_cardtype',{actMsg:'恭喜您已经激活成功！',manyPeople:'1',ipAgeToplmt:0})
                this.$refs.ctf.open('新增卡类型', true)

            },
            view(id){
                this.$store.dispatch('cardtype_get', {id: id}).then(() => {
                    this.$refs.ctv.open();
                })
            },
            search(){
                let param={type:this.type,category:this.category,status:this.status,name:this.name,code:this.code}
                this.$store.dispatch('cardtype_page', param)
            },
            changePage(pn){
                let param={type:this.type,category:this.category,status:this.status,name:this.name,code:this.code,pn:pn}
                this.$store.dispatch('cardtype_page', param)
            },
            refresh(){
                let param={type:this.type,category:this.category,status:this.status,name:this.name,code:this.code}
                this.$store.dispatch('cardtype_page')
            },

            stop(id){
                this.$store.dispatch('cardtype_updateStatus',{id:id,status:'1'}).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('cardtype_page')
                    }
                });
            },

            active(id){
                this.$store.dispatch('cardtype_updateStatus',{id:id,status:'0'}).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('cardtype_page')
                    }
                });
            }

        },
        mounted () {
            this.$store.dispatch('cardtype_page')
            this.$store.dispatch('cardtype_dataReady')
        },

        components: {
            cardtypeForm,cardtypeView
        },

        data () {
            return {
                type: '',//类型 查询条件
                category: '',//职级 查询条件
                status: '',//状态 查询条件
                code:'',
                name:'',
                statusList: consts.status,
                self: this,
                tableColums: [
                    {
                        title: '编号',
                        key: 'code',
                    },

                    {
                        title: '名称',
                        key: 'name',
                    },

                    {
                        title: '面值',
                        key: 'faceVal',
                    },
                    {
                        title: '有效年限',
                        key: 'finiteEffect',
                    },
                    {
                        title: '职类',
                        key: 'categoryTxt',
                    },
                    {
                        title: '类型',
                        key: 'typeTxt',
                    },
                    {
                        title: '单身份证可激活数',
                        key: 'actCount',
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
                        width: 250,
                        align: 'center',
                        render: (h, param) => {
                            let btns = new Array;
                            btns.push(consts.editBtn(this, h, param))
                            btns.push(consts.delBtn(this, h, param))
                            btns.push(consts.viewBtn(this, h, param))
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