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
                    <Button type="primary" icon="person-add" @click="add">新增用户</Button>
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
                    <Table :context="self" :data="userList" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="totalPage" :current="pageNumber" @on-change=""></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <Modal
                v-model="userModal"
                @on-visible-change="vChange"
                :mask-closable="false"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="80" :model="user" :rules="ruleValidate">
                <FormItem label="用户名" prop="loginname">
                    <Input v-model="user.loginname" :maxlength="50" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
                <FormItem label="姓名" prop="nickname">
                    <Input v-model="user.nickname" :maxlength="50" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
                <FormItem label="手机号" prop="phone">
                    <Input v-model="user.phone" placeholder="请输入..." :maxlength="20" style="width: 300px"></Input>
                </FormItem>
                <FormItem label="EMAIL" prop="email">
                    <Input v-model="user.email" placeholder="请输入..." :maxlength="255" style="width: 300px"></Input>
                </FormItem>
                <FormItem label="身份证号" prop="idcard">
                    <Input v-model="user.idcard" placeholder="请输入..." :maxlength="50" style="width: 300px"></Input>
                </FormItem>
                <FormItem label="管理员身份">
                    <i-switch v-model="isAdmin" true-value="0" false-value="1">
                        <span slot="open">是</span>
                        <span slot="close">否</span>
                    </i-switch>
                </FormItem>
                <FormItem label="角色">
                    <CheckboxGroup v-model="ownRoles" @on-change="">
                        <Checkbox  v-for="role in roleList"  :key="role.id" :label="role.id">
                            <span>{{role.name}}({{role.description}})</span>
                        </Checkbox>
                    </CheckboxGroup>
                </FormItem>

            </Form>
            <div slot="footer">
                <Button type="success"  :loading="modalLoading" @click="save">保存</Button>
                <Button type="info"   @click="reset">重置</Button>
                <Button    @click="userModal=false">关闭</Button>
            </div>
        </Modal>
    </div>

</template>

<script>
    import {mapState} from 'vuex'
    export default {
        computed: {
            ...mapState({
                'userList': state => state.user.userList,
                'totalPage': state => state.user.totalPage,
                'pageNumber': state => state.user.pageNumber,
                'roleList':state=>state.role.roleList,
            })
        },
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
                let vm=this;
                this.$store.dispatch('role_list').then((res)=>{
                    vm.userModal=true;
                });

            },
            save(){
                let vm=this;
                this.modalLoading=true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        this.$Message.success('Success!');

                    } else {
                        this.modalLoading=false;
                    }
                })



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

            },
            vChange(b){
                if(!b){
                    this.$refs['formValidate'].resetFields()
                    this.modalLoading=false;
                }
            },
            reset(){
                this.$refs['formValidate'].resetFields()
            }
        },
        mounted () {
            this.$store.dispatch('user_list')
        },
        data () {
            return {
                self: this,
                searchKey: '',
                userModal: false,
                modalTitle: '新增用户',
                modalLoading:false,
                user: {},
                ownRoles:[],
                isAdmin: '1',
                ruleValidate:{
                    loginname: [
                        { required: true, message: '用户名不能为空', trigger: 'blur' }
                    ],
                    nickname: [
                        { required: true, message: '姓名不能为空', trigger: 'blur' }
                    ],
                    phone: [
                        { required: true, message: '手机号不能为空', trigger: 'blur' }
                    ],
                    email:[
                        { type: 'email', message: 'email格式不正确', trigger: 'blur' }
                    ]
                },
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