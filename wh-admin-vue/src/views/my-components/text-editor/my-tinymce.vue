<script>
    import env from '../../../../build/env';
    import kit from '../../../libs/kit';
    import tinymce from 'tinymce';
    import {mapState} from 'vuex'


    const INIT = 0;
    const INPUT = 1;
    const CHANGED = 2;
    export default{
        name: 'my-tinymce',
        computed: {
            ...mapState({
                'uploadPicMaxSize': state => state.uploadPicMaxSize,
            })
        },
        data(){
            return {
                id: 'vue-tinymce-'+Date.now(),
                editor: null,
                status: INIT,
                backup: ''
            }
        },
        props: {
            value: {
                type: String,
                default: ''
            },
            setup: {
                type: Function,
                default: function () {
                }
            },
            setting: {
                type: Object,
                default: function () {

                }
            },
            imgUploadUrl:''

        },
        render(createElement){
            console.info('render')
            return createElement('div', {
                attrs: {
                    id: this.id
                }
            });
        },
        watch: {
            value(val){
                if(this.status === CHANGED) return this.status = INPUT;
                if(!this.editor || !this.editor.initialized) return;
                this.editor.setContent(val);
            }
        },
        created(){
            if(typeof tinymce === "undefined") throw new Error('tinymce undefined');
        },
        mounted () {
            console.info(this.id);
            let maxSize = this.uploadPicMaxSize;
            let url=this.imgUploadUrl;
            const setting = Object.assign({},
                this.setting,
                {
                    selector: '#'+this.id,
                    setup: (editor)=> {
                        this.setup(editor);
                        this.editor = editor;
                        editor.on('init', ()=>{
                            editor.setContent(this.value);
                            editor.on('input change undo redo execCommand KeyUp', ()=>{
                                if(this.status === INPUT || this.status === INIT) return this.status = CHANGED;
                                this.$emit('input', editor.getContent());
                            });
                            editor.on('NodeChange', ()=>{
                                this.$emit('input', editor.getContent());
                            });
                        });
                    },
                    images_upload_handler: function (blobInfo, success, failure) {
                        if(url==''){
                            failure('没有找到图片上传服务器');
                            return;
                        }
                        if (blobInfo.blob().size > maxSize) {
                            failure('上传的图片过大，图片大小不能超过' + maxSize + 'kb')
                            return;
                        }
                        var xhr, formData;
                        xhr = new XMLHttpRequest();
                        xhr.withCredentials = false;
                        xhr.open('POST', url);
                        xhr.onload = function () {
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
                }
            );
            console.info('init finish')
            tinymce.init(setting);



        },
        beforeDestroy: function(){
            this.editor.remove();
            this.editor.destroy(true)
            this.editor=null;
        }
    }
</script>
