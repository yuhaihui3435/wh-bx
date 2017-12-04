<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    用户列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="person-add">新增用户</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                    <Col span="8" offset="8" align="right">
                    <Input v-model="searchKey" placeholder="请输入..." style="width: 200px"/>
                    <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                                 icon="search">搜索</Button></span>
                    <Button @click="cancelSearch" type="ghost">取消</Button>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" :data="page.list" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="page.totalPage" :current="page.pageNumber" @on-change=""></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
    </div>
</template>

<script>
    export default {
        methods: {
            del(i){

            },
            edit(i){

            },
            stop(i){

            },
            active(i){

            },
            add(){

            },
            changePage(){

            },
            search(){

            },
            cancelSearch(){

            },
            refresh(){

            },
            setRole(i){

            }


        },
        data () {
            return {
                self: this,
                page: {},
                searchKey:'',
                tableColums: [

                    {
                        title: '登录名',
                        key: 'loginnanme',
                    },
                    {
                        title: '姓名',
                        key: 'nickname',
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
                        title: '证件号',
                        key: 'idcard',
                    },
                    {
                        title: '最后登录时间',
                        key: 'logged',
                    },
                    {
                        title: '创建时间',
                        key: 'catTxt',
                    },
                    {
                        title: '删除时间',
                        key: 'loginnanme',
                    },
                    {
                        title: '状态',
                        key: 'statusTxt'
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 150,
                        align: 'center',
                        render (row, column, index) {
                            if (row.dAt == '') {

                                if (row.status != '' && row.status == '0')
                                    return `<i-button type="error" size="small" @click="stop(${index})">停用</i-button> <i-button type="primary" size="small" @click="edit(${index})">编辑</i-button> <i-button type="error" size="small" @click="del(${index})">删除</i-button> <i-button type="primary" size="small" @click="setRole(${index})">设置角色</i-button>`;
                                else if (row.status != '' && row.status == '0')
                                    return `<i-button type="primary" size="small" @click="active(${index})">激活</i-button> <i-button type="primary" size="small" @click="edit(${index})">编辑</i-button> <i-button type="error" size="small" @click="del(${index})">删除</i-button> <i-button type="primary" size="small" @click="setRole(${index})">设置角色</i-button>`;
                            }

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