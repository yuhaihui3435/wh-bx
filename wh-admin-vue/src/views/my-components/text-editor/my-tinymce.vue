<template>
    <div>
        <textarea :id="textareaId"></textarea>
    </div>
</template>

<script>
    import env from '../../../../build/env';
    import kit from '../../../libs/kit';
    import tinymce from 'tinymce';
    import {mapState} from 'vuex'



    export default{
        name: 'my-tinymce',
        computed: {
            ...mapState({
                'uploadPicMaxSize': state => state.uploadPicMaxSize,
            })
        },
        data(){
            return {

            }
        },
        props:['textareaId'],
        mounted () {
            let maxSize=this.uploadPicMaxSize;
            tinymce.init({
                selector: '#'+this.textareaId,
                branding: false,
                elementpath: false,
                height: 400,
                language: 'zh_CN.GB2312',
                menubar: 'edit insert view format table tools',
                theme: 'modern',
                images_upload_url: '/cmn/act01',//此属性必须填写，才能显示上传图片的tab，但上传的路径没有使用这里的路径，而是在images_upload_handler处理中自定义的上传路径
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
                },
                images_upload_handler: function (blobInfo, success, failure) {
                    if(blobInfo.blob().size>maxSize){
                        failure('上传的图片过大，图片大小不能超过'+maxSize+'kb')
                        return ;
                    }

                    var xhr, formData;
                    xhr = new XMLHttpRequest();
                    xhr.withCredentials = false;
                    xhr.open('POST', (env=='development'?'/api':'')+'/cmn/act01');

                    xhr.onload = function() {
                        var json;

                        if (xhr.status != 200) {
                            failure('HTTP Error: ' + xhr.status);
                            return;
                        }

                        json = JSON.parse(xhr.responseText);

                        if (!json || typeof json.resData != 'string') {
                            failure('Invalid JSON: ' + xhr.responseText);
                            return;
                        }

                        success(json.resData);
                    };

                    formData = new FormData();
                    formData.append('file', blobInfo.blob(), blobInfo.filename());

                    xhr.send(formData);
                }
            });

        },
        methods:{
            getContent(){
                return tinymce.activeEditor.getContent({format: 'raw'});
            },
            setContent(txt){
                tinymce.activeEditor.setContent(txt)
            },

        },
        destroyed () {
            tinymce.get(this.textareaId).destroy();
        }
    }
</script>
