<template>
        <Tabs type="card">
            <Tab-pane label="已激活意外险卡"><act00></act00></Tab-pane>
            <Tab-pane label="已激活驾乘险卡"><act01></act01></Tab-pane>
        </Tabs>
</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    import act00 from './act00.vue'
    import act01 from './act01.vue'

    export default {

        computed: {

        },
        methods: {
        },
        mounted () {

        },
        components: {
            act00,act01
        },
        data () {
            return {

            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>