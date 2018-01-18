<style lang="less">
    @import "./home.less";
    @import "../../styles/common.less";
</style>
<template>
    <div class="home-main">
        <Row :gutter="10">
            <Col :md="24" :lg="24">

                    <Col :xs="24" :sm="12" :md="5" :style="{marginBottom: '10px'}">
                    <infor-card
                            id-name="actNotExportCount"
                            :end-val="countStatisticsData.actNotExportCount"
                            iconType="fireball"
                            color="#f25e43"
                            intro-text="截止到现在激活未导出数量"
                    ></infor-card>
                    </Col>
                    <Col :xs="24" :sm="12" :md="5" :style="{marginBottom: '10px'}">
                        <infor-card
                            id-name="applyCount"
                            :end-val="countStatisticsData.applyCount"
                            iconType="card"
                            color="#2d8cf0"
                            intro-text="今日卡申请数量"
                        ></infor-card>
                    </Col>
                    <Col :xs="24" :sm="12" :md="5" :style="{marginBottom: '10px'}">
                        <infor-card
                            id-name="outDepotCount"
                            :end-val="countStatisticsData.unlock"
                            iconType="ios-cart"
                            color="#64d572"
                            :iconSize="50"
                            intro-text="今日开卡数量"
                        ></infor-card>
                    </Col>
                    <Col :xs="24" :sm="12" :md="5" :style="{marginBottom: '10px'}">
                        <infor-card
                            id-name="actCount"
                            :end-val="countStatisticsData.actCount"
                            iconType="ios-bolt"
                            color="#ffd572"
                            intro-text="今日卡激活数量"
                        ></infor-card>
                    </Col>
                    <Col :xs="24" :sm="12" :md="4" :style="{marginBottom: '10px'}">
                        <infor-card
                            id-name="exportCount"
                            :end-val="countStatisticsData.exportCount"
                            iconType="shuffle"
                            color="#8A2BE2"
                            intro-text="今日卡导出数量"
                        ></infor-card>
                    </Col>



            </Col>
        </Row>
        <Row :gutter="10" class="margin-top-10">
            <Col :md="24" :lg="8" :style="{marginBottom: '10px'}">
                <Card>
                    <p slot="title" class="card-title">
                        <Icon type="android-map"></Icon>
                        上周每日开卡量
                    </p>
                    <div class="data-source-row">
                        <visite-volume></visite-volume>
                    </div>
                </Card>
            </Col>
            <Col :md="24" :lg="8" :style="{marginBottom: '10px'}">
            <Card>
                <p slot="title" class="card-title">
                    <Icon type="android-map"></Icon>
                    上周每日激活量
                </p>
                <div class="data-source-row">
                    <visite-volume></visite-volume>
                </div>
            </Card>
            </Col>
            <Col :md="24" :lg="8" :style="{marginBottom: '10px'}">
                <Card>
                    <p slot="title" class="card-title">
                        <Icon type="ios-pulse-strong"></Icon>
                        销售人员开卡量
                    </p>
                    <div class="data-source-row">
                        <data-source-pie></data-source-pie>
                    </div>
                </Card>
            </Col>

        </Row>
        <Row :gutter="10" class="margin-top-10">
            <Col :md="24" :lg="24" >
            <Row>
                <Card>
                    <p slot="title" class="card-title">
                        <Icon type="ios-shuffle-strong"></Icon>
                        上周各类卡开卡量统计
                    </p>
                    <div class="line-chart-con">
                        <service-requests></service-requests>
                    </div>
                </Card>
            </Row>
            </Col>
        </Row>

    </div>
</template>

<script>
import cityData from './map-data/get-city-value.js';
import homeMap from './components/map.vue';
import dataSourcePie from './components/dataSourcePie.vue';
import visiteVolume from './components/visiteVolume.vue';
import serviceRequests from './components/serviceRequests.vue';
import userFlow from './components/userFlow.vue';
import countUp from './components/countUp.vue';
import inforCard from './components/inforCard.vue';
import mapDataTable from './components/mapDataTable.vue';
import toDoListItem from './components/toDoListItem.vue';
import {mapState} from 'vuex'

export default {
    name: 'home',
    components: {
        homeMap,
        dataSourcePie,
        visiteVolume,
        serviceRequests,
        userFlow,
        countUp,
        inforCard,
        mapDataTable,
        toDoListItem
    },
    data () {
        return {

        };
    },
    mounted () {
        this.$store.dispatch('countStatisticsData_query')
    },
    computed: {
        avatorPath () {
            return localStorage.avatorImgPath;
        },
        ...mapState({
            'countStatisticsData': state => state.home.countStatisticsData,
        })

    },
    methods: {

    }
};
</script>
