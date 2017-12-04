<template>
    <div>
        <Row>
            <Col span="12">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    角色列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="person-add">新增角色</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
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
            <Col span="12">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    权限设置
                </p>
                <Row>
                    <Tree :data="resData" show-checkbox></Tree>
                </Row>
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
            add(){

            },
            saveRole(){

            },
            changePage(){

            },
            refresh(){

            },
            setRes(i){

            },
            saveRR(){

            }

        },
        data () {
            return {
                self: this,
                page: {},
                resData:[],
                tableColums: [

                    {
                        title: '角色名',
                        key: 'name',
                    },
                    {
                        title: '说明',
                        key: 'description',
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 150,
                        align: 'center',
                        render (row, column, index) {
                                    return '<i-button type="primary" size="small" @click="edit(${index})">编辑</i-button> <i-button type="error" size="small" @click="del(${index})">删除</i-button> <i-button type="primary" size="small" @click="setRes(${index})">设置权限</i-button>';
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