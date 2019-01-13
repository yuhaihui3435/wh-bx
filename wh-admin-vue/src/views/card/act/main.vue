<template>
        <Tabs type="card" @on-click="handleTabClick">
            <Tab-pane label="已激活意外险卡"><act00 ref="act00"></act00></Tab-pane>
            <Tab-pane label="已激活驾乘险卡"><act01 ref="act01"></act01></Tab-pane>
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
            handleTabClick(name){
                if(name==0){
                    this.$refs.act00.search(1);
                }else if(name==1){
                    this.$refs.act01.search(1);
                }
            }
        },
        mounted () {

        },
        components: {
            act00,act01
        },
        data () {
            return {
                curTabName:''
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>