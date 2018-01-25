<template>
    <div>
        <Row>
            <Col span="24">
                <Row class="margin-top-10">
                    <Table :context="self"  border :data="ds0100List" :columns="tableColums" >
                    </Table>
                </Row>
            </Col>
        </Row>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    export default {
        computed: {
        },
        props:{
            queryParam:{}
        },
        methods: {
        },
        mounted () {
            this.$store.dispatch('ds0100_list',this.queryParam).then((list)=>{
                this.ds0100List=list;
            });
        },
        components: {
        },
        data () {
            return {
                ds0100List:[],
                self:this,
                tableColums: [
                    {
                        title: '卡类型',
                        key: 'name',
                    },

                    {
                        title: '出库数量',

                        key: 'dCount',
                    },
                    {
                        title: '开卡数量',

                        key: 'uCount',
                    },
                    {
                        title: '激活数量',

                        key: 'aCount',
                    },
                    {
                        title: '导出数量',

                        key: 'eCount',
                    },
                    {
                        title: '激活未导出数量',

                        key: 'aeCount',
                    },
                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>