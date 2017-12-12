export default {
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
};