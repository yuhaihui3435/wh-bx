<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    新闻列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="person-add" @click="add">新增新闻</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                    <Col span="8" offset="8" align="right">
                    <Input v-model="searchKey" placeholder="请输入..." style="width: 200px"/>
                    <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                          icon="search">搜索</Button></span>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" :data="artList" :columns="tableColums"   stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="search" show-total show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    export default {
        data () {
            return {
                searchKey:'',
                self: this,
                tableColums: [

                    {
                        title: '标题',
                        key: 'title',
                    },
                    {
                        title: '状态',
                        key: 'status',
                    },
                    {
                        title: '栏目',
                        key: 'catalogTxt',
                    },
                    {
                        title: '精华',
                        key: 'good',
                        render:(h,param)=>{
                            let color=(param.row.good=='0')?'green':'red'
                            let txt=(param.row.good=='0')?'是':'否'
                            return h('Tag',{
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            },txt)
                        }
                    },
                    {
                        title: '置顶',
                        key: 'top',
                        render:(h,param)=>{
                            let color=(param.row.top=='0')?'green':'red'
                            let txt=(param.row.top=='0')?'是':'否'
                            return h('Tag',{
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            },txt)
                        }
                    },
                    {
                        title: '发布状态',
                        key: 'flag',
                        render:(h,param)=>{
                            let color=(param.row.flag=='00')?'green':'red'
                            let txt=(param.row.flag=='00')?'已发布':'草稿'
                            return h('Tag',{
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            },txt)
                        }
                    },
                    {
                        title: '创建时间',
                        key: 'c_at',
                    },
                    {
                        title: '作者',
                        key: 'c_at',
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 200,
                        align: 'center',
                    }
                    ]
            }
        },
        methods:{
            add(){

            },
            refresh(){

            },
            search(pn){
                this.$store.dispatch('art_list',{search:this.searchKey,pageNumber:pn});
            }
        },
        components: {

        },
        computed: {
            ...mapState({
                'artList':state =>state.art.artList,
                'totalPage': state => state.art.totalPage,
                'total': state => state.art.totalRow,
                'pageNumber': state => state.art.pageNumber,
                'art': state => state.art.art,
            })
        },
        mounted () {
            this.$store.dispatch('art_list');
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>