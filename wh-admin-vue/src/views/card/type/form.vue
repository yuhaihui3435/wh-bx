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

                            <Form :label-width="150" :model="cardtype" ref="formValidate" :rules="ruleValidate">

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
                                            <Select v-model="cardtype.type" clearable @on-change="typeChange" :disabled="!isAdd">
                                                <Option v-for="item in cttList" :value="item.id+''" :key="item.id+''">
                                                    {{item.title}}
                                                </Option>
                                            </Select>
                                        </FormItem>
                                        </Col>
                                    </Row>
                                    <Row v-show="!driverType">
                                        <Col span="12">
                                        <FormItem label="投保人年龄上限" prop="phAgeToplmt">
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
                                        <FormItem label="被投保人年龄上限" prop="ipAgeToplmt">
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
                                            <Select v-model="cardtype.manyPeople" clearable>
                                                <Option v-for="item in statusList" :value="item.value"
                                                        :key="item.value">{{item.label}}
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
                                            <Select v-model="cardtype.category" clearable>
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
                                <VueTinymce ref="protocol" v-model="cardtype.protocol" :setting="tinymceCfg" :imgUploadUrl="tinymceImgUploadUrl"></VueTinymce>
                            </Card>

                        </TabPane>
                        <TabPane label="服务凭证信息" icon="document-text" name="serviceCert">

                            <Card>

                                <VueTinymce ref="serviceCert" :setting="tinymceCfg" v-model="cardtype.serviceCert" :imgUploadUrl="tinymceImgUploadUrl"></VueTinymce>
                            </Card>

                        </TabPane>
                        <TabPane label="保险条款文件" icon="ios-folder" name="InsuranceClauseDocument">

                            <Card>

                                <p slot="title">
                                <Icon type="image"></Icon>
                                待上传文件
                                </p>

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

                                                            <Icon type="trash-a"
                                                                  @click.native="delModal=true;del_index=index"
                                                                  color="red">
                                                            </Icon>

                                                    </Row>
                                                </template>
                                            </div>
                                        </div>
                                    </Card>
                                </div>
                            </Card>

                            <Card class="margin-top-10" v-show="!isAdd">
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
                                                            <Icon type="trash-a"
                                                                  @click.native="uploaded_delModal=true;del_index=index"
                                                                  color="red">
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

        <Modal v-model="uploaded_delModal" width="360">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="information-circled"></Icon>
                <span>删除确认</span>
            </p>
            <div style="text-align:center">

                <p>您确认要删除该文件吗?</p>
            </div>
            <div slot="footer">
                <Button type="error" size="large" long :loading="del_modal_loading" @click="uploaded_handleRemove()">删除</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    import {VueTinymce, Config} from '../../my-components/text-editor/'

    let vm=null;
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
                this.uploadList=[]
                this.uploadNameList=[]
                this.modalTitle = title;
                this.isAdd = isAdd;
                this.cardtypeModal = true;
                this.modalLoading = false;
                if (isAdd) {
                    this.driverType = false;//类型下拉框恢复初始状态
                } else {
                }
            },
            save(){
                let vm = this;
                this.modalLoading = true;
                let param = { uploadList: this.uploadList}
                this.$refs['formValidate'].validate((valid) => {
                    console.info(valid)
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
                    this.actTabName = "baseInfo"//恢复tab的初始状态选中第一个tab
                }
            },
            reset(){
                this.$refs['formValidate'].resetFields()
            },
            handleUpload(file){
                let vm = this;
                let name = file.name;
                if (vm.uploadNameList.indexOf(name) > -1) {
                    this.$Notice.warning({
                        title: '文件已存在',
                        desc: '文件 >>' + file.name + '<< 正等待上传。'
                    });
                    return false;
                }
                let ary = name.split(".");
                let format = ['doc', 'docx', 'xls', 'xlsx', 'pdf', 'txt'];
                let fileSize = file.size;
                if (format.indexOf(ary[1]) == -1) {
                    this.$Notice.warning({
                        title: '文件格式不正确',
                        desc: '文件 >>' + file.name + '<< 格式不正确，请选择doc,docx,xls,xlsx,pdf,txt文件。'
                    });
                    return false;
                }
                if (parseInt(fileSize) > parseInt(this.uploadPicMaxSize)) {
                    this.$Notice.warning({
                        title: '文件大小错误',
                        desc: '文件 >>' + file.name + '<< 过大，不能超过' + parseInt(vm.uploadPicMaxSize) / 1024 / 1024 + "M"
                    });
                    return false;
                }
                vm.uploadList.push(file)
                vm.uploadNameList.push(file.name);
                return false;
            },
            handleRemove(){
                this.del_modal_loading = true;
                this.uploadList.splice(this.del_index, 1);
                this.uploadNameList.splice(this.del_index, 1);
                this.del_modal_loading = false;
                this.delModal = false;
            },
            uploaded_handleRemove(){
                this.uploaded_del_modal_loading = true;
                let obj=this.cardtype.clauseList[this.del_index];

                this.$store.dispatch('cardtype_del_file',{fileId:obj.id}).then((res)=>{
                    if (res && res == 'success') {
                        this.cardtype.clauseList.splice(this.del_index, 1);
                    }
                })
                this.uploaded_del_modal_loading = false;
                this.uploaded_delModal = false;
            },
            typeChange(value){
                console.info(value)
                for (let t of this.cttList) {
                    if (t.id == value) {
                        if (t.text == 'driverInsurance') {
                            this.driverType = true;
                        } else {
                            this.driverType = false;
                        }
                    }
                }
            }

        },
        mounted () {
            vm=this;
        },
        data () {
            return {
                uploadList: [],
                uploadNameList: [],
                self: this,
                cardtypeModal: false,
                isAdd: true,
                delModal: false,//待上传
                del_modal_loading: false,//待上传
                uploaded_delModal:false,//已上传
                uploaded_del_modal_loading: false,//已上传
                del_index: '',

                tinymceCfg: Object.assign(Config, {
                    height: 200
                }),
                tinymceImgUploadUrl:consts.imgUploadUrl,
                modalTitle: '新增卡类型',
                modalLoading: false,
                driverType: false,
                actTabName: 'baseInfo',
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
                    phAgeToplmt:[
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
                            value=value==undefined?1:parseInt(value);
                            let phAgeLowerlmt=vm.cardtype.phAgeLowerlmt;
                            phAgeLowerlmt=phAgeLowerlmt==undefined?1:parseInt(phAgeLowerlmt);
                            if(phAgeLowerlmt<value){
                                errors.push("投保人上限不能大于下限");
                            }
                            callback(errors);
                        }}
                    ],
                    phAgeLowerlmt:[
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
                            value=value==undefined?1:parseInt(value);
                            let phAgeToplmt=vm.cardtype.phAgeToplmt;
                            phAgeToplmt=phAgeToplmt==undefined?1:parseInt(phAgeToplmt);
                            if(phAgeToplmt>value){
                                errors.push("投保人下限不能小于上限");
                            }
                            callback(errors);
                        }}
                    ],
                    ipAgeToplmt:[
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
                            value=value==undefined?1:parseInt(value);
                            let ipAgeLowerlmt=vm.cardtype.ipAgeLowerlmt;
                            ipAgeLowerlmt=ipAgeLowerlmt==undefined?1:parseInt(ipAgeLowerlmt);
                            if(ipAgeLowerlmt<value){
                                errors.push("被投保人上限不能大于下限");
                            }
                            callback(errors);
                        }}
                    ],
                    ipAgeLowerlmt:[
                        {validator(rule, value, callback, source, options) {
                            var errors = [];
                            value=value==undefined?1:parseInt(value);
                            let ipAgeToplmt=vm.cardtype.ipAgeToplmt;
                            ipAgeToplmt=ipAgeToplmt==undefined?1:parseInt(ipAgeToplmt);
                            if(ipAgeToplmt>value){
                                errors.push("被投保人下限不能小于上限");
                            }
                            callback(errors);
                        }}
                    ]


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
            VueTinymce
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>