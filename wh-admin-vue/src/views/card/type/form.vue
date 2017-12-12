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

                            <Form :label-width="80" :model="cardtype" ref="formValidate" :rules="ruleValidate">

                                <Card>

                                    <Row>
                                        <Col span="12">
                                        <FormItem label="编号" prop="code">
                                            <Input v-model="cardtype.code"></Input>
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="名称" prop="name">
                                            <Input v-model="cardtype.name"></Input>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span="12">
                                    <FormItem label="面值" prop="faceVal">
                                        <Input v-model="cardtype.faceVal"></Input>
                                    </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="类型" prop="type">
                                            <Select v-model="cardtype.type" clearable @on-change="typeChange">
                                                <Option v-for="item in cttList" :value="item.id+''" :key="item.id+''">
                                                    {{item.title}}
                                                </Option>
                                            </Select>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row v-show="!driverType">
                                        <Col span="12">
                                        <FormItem label="投保人年龄上限" prop="phAgeToplmt" >
                                            <InputNumber :max="99" :min="1" :step="1" value="1"
                                                         v-model="cardtype.phAgeToplmt"></InputNumber>
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="投保人年龄下限" prop="phAgeLowerlmt">
                                            <InputNumber :max="99" :min="1" :step="1" value="1"
                                                         v-model="cardtype.phAgeLowerlmt"></InputNumber>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row v-show="!driverType">
                                        <Col span="12">
                                        <FormItem label="被投保人年龄上限" prop="phAgeToplmt">
                                            <InputNumber :max="99" :min="0" :step="1"
                                                         v-model="cardtype.ipAgeToplmt"></InputNumber>
                                            年
                                            <InputNumber :max="366" :min="1" :step="1"
                                                         v-model="cardtype.ipAgeToplmtDay"></InputNumber>
                                            天
                                        </FormItem>


                                        </Col>
                                        <Col span="12">
                                        <FormItem label="被投保人年龄下限" prop="ipAgeLowerlmt">
                                            <InputNumber :max="99" :min="1" :step="1" value="1"
                                                         v-model="cardtype.ipAgeLowerlmt"></InputNumber>
                                        </FormItem>
                                        </Col>
                                    </Row>

                                    <Row v-show="!driverType">
                                        <Col span="12">
                                        <FormItem label="是否多人" prop="manyPeople">
                                            <Select v-model="cardtype.manyPeople" clearable >
                                                <Option v-for="item in statusList" :value="item.value+''"
                                                        :key="item.value+''">{{item.label}}
                                                </Option>
                                            </Select>
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="投保人数" prop="peopleCount">
                                            <InputNumber :max="99" :min="1" :step="1" value="1"
                                                         v-model="cardtype.peopleCount"></InputNumber>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span="12">
                                        <FormItem label="有效年限" prop="finiteEffect">
                                            <InputNumber :max="99" :min="1" :step="1" value="1"
                                                         v-model="cardtype.finiteEffect"></InputNumber>
                                        </FormItem>
                                        </Col>
                                        <Col span="12">
                                        <FormItem label="单身份证可激活张数" prop="actCount">
                                            <InputNumber :max="99" :min="1" :step="1" value="1"
                                                         v-model="cardtype.actCount"></InputNumber>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row v-show="!driverType">
                                        <Col span="12">
                                        <FormItem label="职类" prop="category">
                                            <Select v-model="cardtype.category" clearable >
                                                <Option v-for="item in ioList" :value="item.id+''" :key="item.id+''">
                                                    {{item.title}}
                                                </Option>
                                            </Select>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span="24">
                                        <FormItem label="激活成功提示消息" prop="actMsg">
                                            <Input v-model="cardtype.actMsg" type="textarea" :rows="4"
                                                   icon="android-list"/>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                </Card>
                            </Form>

                        </TabPane>
                        <TabPane label="协议信息" icon="document" name="protocol">

                            <Card>
                                <myTinymce ref="protocol" textareaId="protocol"></myTinymce>
                            </Card>

                        </TabPane>
                        <TabPane label="服务凭证信息" icon="document-text" name="serviceCert">

                            <Card>

                                <myTinymce ref="serviceCert" textareaId="serviceCert"></myTinymce>
                            </Card>

                        </TabPane>
                        <TabPane label="保险条款文件" icon="ios-folder" name="InsuranceClauseDocument">

                            <Card>

                                <!--<p slot="title">-->
                                    <!--<Icon type="image"></Icon>-->
                                    <!--保险条款上传-->
                                <!--</p>-->

                                <div class="height-120px">
                                    <Row type="flex" justify="center" align="middle" class="height-100">
                                        <Upload
                                                action=""
                                                :format="['doc','docx','xls','xlsx','pdf','txt']"
                                                :before-upload="handleUpload"
                                                 multiple
                                        >
                                            <span>选择文件上传&nbsp;&nbsp;</span>
                                            <Button type="ghost" icon="ios-cloud-upload-outline">上传文件</Button>
                                        </Upload>

                                    </Row>
                                </div>

                                <div class="margin-top-10" v-show="uploadList.length>0">
                                    <Card>
                                        <div>
                                            <div v-for="(item,index) in uploadList">
                                                <template>
                                                    <Row>
                                                        <Icon type="document"></Icon>
                                                        &nbsp;{{item.name}}&nbsp;&nbsp;
                                                        <Poptip confirm title="您确认删除这个文件吗？"
                                                                @on-ok="handleRemove(index)">
                                                            <Icon type="trash-a" @click.native="delModal=true;del_index=index"  color="red">ss</Icon>
                                                        </Poptip>
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
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button @click="reset" v-show="isAdd">重置</Button>
                <Button type="error" @click="cardtypeModal=false">关闭</Button>
            </div>
        </Modal>

        <Modal v-model="delModal" width="360">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="information-circled"></Icon>
                <span>删除确认</span>
            </p>
            <div style="text-align:center">

                <p>您确认要删除该文件吗?</p>
            </div>
            <div slot="footer">
                <Button type="error" size="large" long :loading="del_modal_loading" @click="handleRemove()">删除</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    import tinymce from 'tinymce';
    import myTinymce from '../../my-components/text-editor/my-tinymce.vue'
    export default {
        computed: {
            ...mapState({
                'cardtype': state => state.cardtype.cardtype,
                'cttList': state => state.cardtype.cttList,
                'ioList': state => state.cardtype.ioList,
                'uploadPicMaxSize': state => state.uploadPicMaxSize,
            })
        },
        methods: {
            open(title, isAdd){
                let vm = this;
                this.modalTitle = title;
                this.isAdd = isAdd;
                this.cardtypeModal = true;
                this.modalLoading = false;
                if (isAdd) {
//                    this.$store.commit('set_cardtype');
                    this.$refs.protocol.setContent('');
                    this.$refs.serviceCert.setContent('');
                } else {
                    this.$refs.protocol.setContent(this.cardtype.protocol);
                    this.$refs.serviceCert.setContent(this.cardtype.serviceCert);
                }
            },
            save(){
                let vm = this;
                this.modalLoading = true;
                console.info(this.$refs.serviceCert)
                console.info(this.$refs.protocol)
                let serviceCert = this.$refs.serviceCert.getContent();
                let protocol = this.$refs.protocol.getContent();
                let param = {serviceCert: serviceCert, protocol: protocol,uploadList:this.uploadList}
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action = 'save';
                        if (!vm.isAdd)
                            action = 'update';

                        param.action = action;
                        this.$store.dispatch('cardtype_save', param).then((res) => {
                            if (res && res == 'success') {
                                vm.cardtypeModal = false;
                                this.$store.dispatch('cardtype_page')
                            } else {
                                this.modalLoading = false;
                            }
                        })
                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            vChange(b){
                //本模式窗体 隐藏时候恢复窗体的一些初始设置
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                    this.modalLoading = false;
                    this.driverType=false;//类型下拉框恢复初始状态
                    this.actTabName="baseInfo"//恢复tab的初始状态选中第一个tab
                }
            },
            reset(){
                this.$refs['formValidate'].resetFields()
            },
            handleUpload(file){
                let vm = this;
                let name=file.name;
                if(vm.uploadNameList.indexOf(name)>-1){
                    this.$Notice.warning({
                        title: '文件已存在',
                        desc: '文件 >>' + file.name + '<< 正等待上传。'
                    });
                    return false;
                }
                let ary=name.split(".");
                let format=['doc','docx','xls','xlsx','pdf','txt'];
                let fileSize=file.size;
                if(format.indexOf(ary[1])==-1){
                    this.$Notice.warning({
                        title: '文件格式不正确',
                        desc: '文件 >>' + file.name + '<< 格式不正确，请选择doc,docx,xls,xlsx,pdf,txt文件。'
                    });
                    return false;
                }
                if(parseInt(fileSize)>parseInt(this.uploadPicMaxSize)){
                    this.$Notice.warning({
                        title: '文件大小错误',
                        desc: '文件 >>' +file.name+'<< 过大，不能超过' + parseInt(vm.uploadPicMaxSize)/1024/1024 + "M"
                    });
                    return false;
                }
                vm.uploadList.push(file)
                vm.uploadNameList.push(file.name);
                return false;
            },
            handleRemove(){
                this.del_modal_loading=true;
                this.uploadList.splice(this.del_index, 1);
                this.uploadNameList.splice(this.del_index, 1);
                this.del_modal_loading=false;
                this.delModal=false;
            },
            typeChange(value){
                console.info(value)
                for (let t of this.cttList){
                    if(t.id==value){
                        if(t.text=='driverInsurance'){
                            this.driverType=true;
                        }else{
                            this.driverType=false;
                        }
                    }
                }
            }

        },
        mounted () {

        },
        data () {
            return {
                uploadList: [],
                uploadNameList:[],
                self: this,
                cardtypeModal: false,
                isAdd: true,
                delModal:false,
                del_index:'',
                del_modal_loading:false,
                modalTitle: '新增卡类型',
                modalLoading: false,
                driverType:false,
                actTabName:'baseInfo',
                statusList: consts.yon,
                ruleValidate: {
                    code: [
                        {type: 'string', required: true, message: '编号不能为空', trigger: 'blur'},
                        {type: 'string', max: 50, message: '编号长度不能超过50', trigger: 'blur'}
                    ],
                    name: [
                        {required: true, message: '名称不能为空', trigger: 'blur'},
                        {type: 'string', message: '名称长度不能超过50', max: 50, trigger: 'blur'}
                    ],
                    faceVal: [
                        {required: true, message: '面值不能为空', trigger: 'blur'},
                        {type: 'number', message: '面值必须为数字', trigger: 'blur', transform: value => +value},
                    ],
                    actMsg: [
                        {required: true, message: '激活成功提示消息不能为空', trigger: 'blur'},
                    ],
                    type: [
                        {required: true, message: '类型必选', trigger: 'change'},
                    ],

                },
            }
        }, watch: {
            cardtype: {
                handler: function (val, oldval) {
                },
                deep: true
            }
        },
        components: {
            myTinymce
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>