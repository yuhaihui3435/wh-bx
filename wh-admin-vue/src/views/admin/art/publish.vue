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
                        <FormItem label="文章标题" :error="articleError">
                            <Input v-model="art.title" icon="android-list"/>
                        </FormItem>
                        <FormItem label="摘要" :error="articleError">
                            <Input v-model="art.summary" type="textarea" :rows="4"
                                   icon="android-list"/>
                        </FormItem>
                    </Form>
                    <div class="margin-top-20">
                        <textarea id="articleEditor"></textarea>
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
                        <Select size="small" style="width:90px" value="00" v-model="art.flag">
                            <Option v-for="item in articleStateList" :value="item.value" :key="item.value">{{ item.label
                                }}
                            </Option>
                        </Select>
                    </p>
                    <p class="margin-top-10">
                        <Icon type="flag"></Icon>
                        &nbsp;&nbsp;置顶：&nbsp;<b></b>
                        <Select size="small" style="width:90px" value="1" v-model="art.top">
                            <Option v-for="item in yornList" :value="item.value" :key="item.value">{{ item.label }}
                            </Option>
                        </Select>
                    </p>
                    <p class="margin-top-10">
                        <Icon type="fireball"></Icon>
                        &nbsp;&nbsp;精华：&nbsp;<b></b>
                        <Select size="small" style="width:90px" value="1" v-model="art.good">
                            <Option v-for="item in yornList" :value="item.value" :key="item.value">{{ item.label }}
                            </Option>
                        </Select>
                    </p>

                    <Row class="margin-top-20 publish-button-con">
                        <span class="publish-button"><Button @click="handlePreview">预览</Button></span>
                        <span class="publish-button"><Button @click="handlePublish" :loading="publishLoading"
                                                             icon="ios-checkmark" style="width:90px;"
                                                             type="primary">发布</Button></span>
                    </Row>
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
                                        action="//jsonplaceholder.typicode.com/posts/"
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
        </Modal>
    </div>
</template>

<script>
    import env from '../../../../build/env';
    import tinymce from 'tinymce';
    import {mapState} from 'vuex'
    export default {
        name: 'art-publish',
        computed: {
            ...mapState({
                'art': state => state.art.art,
                'artTaxList': state => state.art.artTaxList,
                'uploadPicMaxSize': state => state.uploadPicMaxSize,
            })
        },

        data () {
            return {
                articleError: '',
                editPathButtonType: 'ghost',
                articleStateList: [{value: '01', label: '草稿'}, {value: '00', 'label': '发布'}],
                yornList: [{value: '0', label: '是'}, {value: '1', 'label': '否'}],
                publishLoading: false,
                artModal: false,
                isAdd: true,
                modalTitle: '新增文章',
                imgData: '',
                file: '',
                previewImg: false,
                showImg:false
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
                if (isAdd)
                    this.$store.commit('set_art', {});
                this.modalLoading = false;
            },
            handlePublish(){

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
            handlePreview () {
                if (this.canPublish()) {
                    this.art.text = tinymce.activeEditor.getContent();
                    this.$router.push({
                        name: 'preview'
                    });
                }
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


            tinymce.init({
                selector: '#articleEditor',
                branding: false,
                elementpath: false,
                height: 400,
                language: 'zh_CN.GB2312',
                menubar: 'edit insert view format table tools',
                theme: 'modern',
                images_upload_url: '/ad/uploadPic',
                plugins: [
                    'advlist autolink lists link image charmap print preview hr anchor pagebreak imagetools',
                    'searchreplace visualblocks visualchars code fullscreen fullpage',
                    'insertdatetime media nonbreaking save table contextmenu directionality',
                    'emoticons paste textcolor colorpicker textpattern imagetools codesample'
                ],
                toolbar1: ' newnote print fullscreen preview | undo redo | insert | styleselect | forecolor backcolor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image emoticons media codesample',
                autosave_interval: '20s',
                image_advtab: true,
                table_default_styles: {
                    width: '100%',
                    borderCollapse: 'collapse'
                }
            });

            this.$store.dispatch('art_tax_jsonArray');

        },
        destroyed () {
            tinymce.get('articleEditor').destroy();
        }
    };
</script>
