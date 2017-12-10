<style lang="less">
    @import '../../../styles/common.less';
    @import 'upload.less';
</style>

<template>
    <div>
        <Modal
                v-model="artModal"
                :mask-closable="false"
                width="900"
                :styles="{top: '20px'}"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Row>
                <Col span="18">
                <Card>
                    <Form :label-width="80">
                        <FormItem label="文章标题" >
                            <Input v-model="art.title" icon="android-list"/>
                        </FormItem>
                        <FormItem label="引用链接" >
                            <Input v-model="art.linkTo" icon="link"/>
                        </FormItem>
                        <FormItem label="摘要" >
                            <Input v-model="art.summary" type="textarea" :rows="4"
                                   icon="android-list"/>
                        </FormItem>
                    </Form>
                    <div class="margin-top-20">
                        <!--<textarea id="articleEditor"></textarea>-->
                        <myTinymce ref="mt"></myTinymce>
                    </div>
                </Card>
                </Col>
                <Col span="6" class="padding-left-10">
                <Card>
                    <p slot="title">
                        <Icon type="paper-airplane"></Icon>
                        发布
                    </p>
                    <p class="margin-top-10">
                        <Icon type="android-time"></Icon>
                        发布&nbsp;&nbsp;状&nbsp;&nbsp;&nbsp; 态：
                        <Select size="small" style="width:90px"  v-model="art.flag" disabled>
                            <Option v-for="item in articleStateList" :value="item.value" :key="item.value">{{ item.label
                                }}
                            </Option>
                        </Select>
                    </p>
                    <p class="margin-top-10">
                        <Icon type="flag"></Icon>
                        &nbsp;&nbsp;置顶：&nbsp;<b></b>
                        <Select size="small" style="width:90px"  v-model="art.top">
                            <Option v-for="item in yornList" :value="item.value" :key="item.value">{{ item.label }}
                            </Option>
                        </Select>
                    </p>
                    <p class="margin-top-10">
                        <Icon type="fireball"></Icon>
                        &nbsp;&nbsp;精华：&nbsp;<b></b>
                        <Select size="small" style="width:90px"  v-model="art.good">
                            <Option v-for="item in yornList" :value="item.value" :key="item.value">{{ item.label }}
                            </Option>
                        </Select>
                    </p>


                </Card>
                <div class="margin-top-10">
                    <Card>
                        <p slot="title">
                            <Icon type="navicon-round"></Icon>
                            分类目录
                        </p>

                                <div >
                                    <Tree :data="artTaxList" ref="artTaxSelected" show-checkbox></Tree>
                                </div>

                    </Card>
                </div>
                <div class="margin-top-10">
                    <Card>
                        <p slot="title">
                            <Icon type="image"></Icon>
                            缩略图
                        </p>
                        <div class="height-120px">
                            <Row type="flex" justify="center" align="middle" class="height-100">
                                <Upload
                                        action=""
                                        :format="['jpg', 'png', 'jpeg', 'gif', 'bmp', 'svg']"
                                        :before-upload="handleUpload"
                                        :on-format-error="handleFormatError"
                                        :max-size="uploadPicMaxSize"
                                        :on-exceeded-size="handlerSizeError"
                                >
                                    <span>选择图片上传&nbsp;&nbsp;</span>
                                    <Button type="ghost" icon="ios-cloud-upload-outline">上传文件</Button>
                                </Upload>

                            </Row>
                        </div>
                        <div class="margin-top-10" v-show="previewImg">
                            <Card>
                                <div>
                                    <div class="admin-upload-list">
                                        <template>
                                            <img :src="imgData">
                                            <div class="admin-upload-list-cover">
                                                <Icon type="ios-eye-outline" @click.native="handleView()"></Icon>
                                                <Icon type="ios-trash-outline" @click.native="handleRemove()"></Icon>
                                            </div>
                                        </template>
                                    </div>
                                </div>
                            </Card>
                        </div>
                    </Card>
                    <Modal title="查看图片" v-model="showImg" @on-cancel="artModal=true;showImg=false" @on-ok="artModal=true;showImg=false">
                        <img :src="imgData" v-if="showImg"
                             style="width: 100%">
                    </Modal>

                </div>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="success" :loading="publishLoading" @click="handlePublish">保存</Button>
                <Button type="error" @click="artModal=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
    import env from '../../../../build/env';
    import tinymce from 'tinymce';
    import {mapState} from 'vuex'
    import myTinymce from '../../my-components/text-editor/my-tinymce.vue'
    export default {
        name: 'art-publish',
        computed: {
            ...mapState({
                'art': state => state.art.art,
                'artTaxList': state => state.art.artTaxList,
                'uploadPicMaxSize': state => state.uploadPicMaxSize,
            })
        },
        components:{
            myTinymce
        },
        data () {
            return {
                articleStateList: [{value: '01', label: '草稿'}, {value: '00', 'label': '发布'}],
                yornList: [{value: '0', label: '是'}, {value: '1', 'label': '否'}],
                artModal: false,
                isAdd: true,
                modalTitle: '新增文章',
                imgData: '',
                file: '',
                previewImg: false,
                showImg:false,
                publishLoading:false
            };
        },
        methods: {
            open(title, isAdd){
                this.isAdd = isAdd
                this.modalTitle = title;
                this.artModal = true;
                this.showImg=false;
                this.file='';
                this.previewImg=false;
                this.imgData='';
                if (isAdd){
                    this.$store.commit('set_art');
                    this.$refs.mt.setContent('');
                }else{
                    this.$refs.mt.setContent(this.art.text);
                    //this.imgData=this.art.thumbnailImgUrl;
                }


            },
            handlePublish(){
                let vm=this;
                this.publishLoading=true;
                if(!this.art.title||this.art.title.length==0) {
                    this.$Message.error("请填写标题");
                    this.publishLoading=false;
                    return
                }

                let taxArray=this.$refs.artTaxSelected.getCheckedNodes();
                let taxIds='';
                for(let s of taxArray){
                    if(taxIds==''){
                        taxIds=s.id;
                    }else{
                        taxIds=taxIds+","+s.id
                    }
                }

                let thumbnailBase64=this.imgData
                let text=this.$refs.mt.getContent();
                //let action=(this.art.id)?'update':'save'
                let param={'text':text,'tax':taxIds,'thumbnailBase64':thumbnailBase64,'action':'save'}

                this.$store.dispatch('art_save',param).then((res)=>{
                    this.publishLoading = false;
                    if (res && res == 'success') {
                        vm.artModal = false;
                        this.$store.dispatch('art_list')
                    }
                })


            },
            handleUpload(file){
                let vm = this;
                let reader = new FileReader();
                reader.onload = function () {
                    vm.imgData = this.result;
                }
                reader.readAsDataURL(file)
                this.previewImg = true
                return false;
            },
            handleRemove(){
                this.imgData = '';
                this.file = '';
                this.showImg=false;
                this.previewImg = false;
            },
            handleFormatError (file) {
                this.$Notice.warning({
                    title: '文件格式不正确',
                    desc: '文件 ' + file.name + ' 格式不正确，请选择图片文件。'
                });
            },
            handleView(){
                this.showImg=true;
                this.artModal=false;
            },
            handlerSizeError(file){
                let vm = this;
                this.$Notice.warning({
                    title: '文件大小错误',
                    desc: '文件大小不能超过' + vm.uploadPicMaxSize + "kb"
                });
            }

        },
        mounted () {
            this.$store.dispatch('art_tax_jsonArray');

        },

        destroyed () {

        }
    };
</script>
