<style lang="less">
    @import '../../../styles/common.less';
    .ivu-table .demo-table-error-row td{
        background-color: #ff6600;
        color: #fff;
    }
</style>
<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    已激乘驾险查询
                </p>
                <Row>
                    <Col span="24" offset="0" align="left">
                    <Form inline id="form" target="">
                        <FormItem >
                            <Input v-model="code" placeholder="卡号" style="width: 200px"/>
                        </FormItem>

                        <FormItem >
                            <Input v-model="exportCode" placeholder="导出编号" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="policyNum" placeholder="保单号" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="phName" placeholder="投保人/公司名称" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="phIdNum" placeholder="投保人/公司证件号" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="phTel" placeholder="投保人联系电话" style="width: 200px"/>
                        </FormItem>
                        <FormItem >
                            <Input v-model="ipPlateNum" placeholder="被投保车牌号" style="width: 200px"/>
                        </FormItem>

                        <FormItem>
                            <DatePicker :options="options" type="datetimerange" v-model="at"   placement="bottom-end" placeholder="激活时间" @on-change="dateTimeChange" style="width: 200px"></DatePicker>
                        </FormItem>
                        <FormItem>
                            <Select v-model="exportStatus" placeholder="导出状态" clearable style="width:100px" align="left">
                                <Option v-for="item in yonList" :value="item.value" :key="item.value">{{ item.label}}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Select v-model="salesmenId" placeholder="业务员" clearable style="width:100px" align="left">
                                <Option v-for="item in salesmenList" :value="item.id" :key="item.id">{{ item.name
                                    }}
                                </Option>
                            </Select>
                        </FormItem>

                        <FormItem>
                            <span @click="search(1)" style="margin: 0 10px;"><Button type="primary"
                                                                                     icon="search">搜索</Button></span>
                            <span @click="reset" style="margin: 0 10px;"><Button type="primary"
                                                                                 icon="ios-redo">重置</Button></span>
                        </FormItem>
                    </Form>
                    </Col>
                </Row>
                <!--<Row class="margin-top-10">-->
                <!--<span @click="search" style="margin: 0 10px;"><Button type="error" icon="locked">锁定</Button></span>-->
                <!--<span @click="search" style="margin: 0 10px;"><Button type="success" icon="unlocked">解锁</Button></span>-->
                <!--</Row>-->
                <Row class="margin-top-10">
                    <Col span="3">
                    <Poptip
                            confirm
                            title="您确认到执行导出操作吗？"
                            @on-ok="e"
                            @on-cancel=""><span  style="margin: 0 10px;"><Button type="primary" icon="ios-download">按勾选导出</Button></span></Poptip></Col>
                    <Col span="3">
                    <Poptip
                            confirm
                            title="您确认到执行导出操作吗？"
                            @on-ok="eByQuery"
                            @on-cancel=""><span  style="margin: 0 10px;"><Button type="primary" icon="ios-download">按查询条件导出</Button></span></Poptip></Col>
                    <Col span="3" align="center"><Upload :on-success="handleSuccess" :on-error="handleError" :before-upload="handleBeforUpload"
                                                         :format="['xls','xlsx']" :on-format-error="handleFormatError" :show-upload-list="false"
                                                         :action="env+'/c03/importPolicyInfo'">
                    <span  style="margin: 0 10px;"><Button type="primary" icon="ios-upload">导入</Button></span></Upload></Col>
                    <Col span="4" align="center"><span  style="margin: 0 10px;"><Button type="primary" icon="ios-download" @click="downloadTemplate">批量激活模板下载</Button></span></Col>
                    <Col span="3" align="center"><Upload :on-success="handleSuccess" :on-error="handleError" :before-upload="handleBeforUpload"
                                                         :format="['xls','xlsx']" :on-format-error="handleFormatError" :show-upload-list="false"
                                                         :action="env+'/c03/batchImportAct01'"><span  style="margin: 0 10px;"><Button type="primary" icon="ios-upload">批量激活导入</Button></span></Upload></Col>
                </Row>
                <Row class="margin-top-10">


                    <Table :context="self" :row-class-name="rowClassName" border :data="cardsList" :columns="tableColums" @on-selection-change="getSelection" ></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber"  @on-change="search" show-total :page-size="15"
                              show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <view01 ref="v"></view01>
    </div>
</template>
<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    import view01 from './view01.vue'


    let lockBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要锁定【'+param.row.code+'】这张卡吗？'
            },
            style: {
                marginRight: '5px',
            },
            on: {
                'on-ok': () => {
                    vm.lock(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '锁定')]);
    }
    let unActBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定【'+param.row.code+'】撤销到未激活吗？'
            },
            style: {
                marginRight: '5px',
            },
            on: {
                'on-ok': () => {
                    vm.unAct(param.row)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '撤销到未激活')]);
    }

    let uploadBDBtn=(vm,h,param)=>{
        return h('Col',{props:{span:3}},[h('Upload',{props:{
            action:consts.env+'/c03/uploadPolicy?cardsId='+param.row.id+'&cardsCode='+param.row.code,
            format:['pdf'],
            'show-upload-list':false,
            'on-success':(response, file, fileList)=>{
                vm.$store.commit('upadteSpinshow',false);
                if(response&&response.resCode&&response.resCode=='success'){
                    vm.$Message.success(response.resMsg);
                }else if(response&&response.resCode&&response.resCode=='fail'){
                    vm.$Message.error(response.resMsg);
                }
            },
            'on-error':(error, file, fileList)=>{
                vm.$store.commit('upadteSpinshow',false);
                vm.$Message.error('网络错误，请稍后再重试');
            },
            'on-format-error':(file)=>{
                vm.$store.commit('upadteSpinshow',false);
                vm.$Notice.warning({
                    title: '上传文件格式错误',
                    desc: '文件 ' + file.name + ' 格式不正确, 请选择 pdf.'
                });
            },
            'before-upload':()=>{
                vm.$store.commit('upadteSpinshow',true);
            }
        }},[h('Button', {
            props: {
                type: 'primary',
                size: 'small'
            },
            style: {
                marginRight: '5px'
            },
            on: {

            }
        }, '上传保单')])])
    };

    let viewBtn=(vm,h,param)=>{
        return h('Button', {
            props: {
                type: 'primary',
                size: 'small'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                click: () => {
                    vm.view(param.row.code)
                }
            }
        }, '查看')
    };
    export default {

        computed: {
            ...mapState({
                'cardsList': state => state.cards.cardsList_01,
                'totalPage': state => state.cards.totalPage,
                'total': state => state.cards.totalRow,
                'pageNumber': state => state.cards.pageNumber,
                'salesmenList': state => state.cards.salesmenList,
            })
        },
        methods: {
            rowClassName (row, index) {
                if (row.disabled ) {
                    return 'demo-table-error-row';
                }
                return '';
            },
            view(code){
                this.$store.dispatch('cards_get', {cardCode: code}).then(() => {
                    this.$refs.v.open();
                })
            },
            search(pn){

                if(pn==undefined)pn=1
                let param = {
                    exportStatus:this.exportStatus,
                    salesmenId: this.salesmenId,
                    actTime: this.actTime,
                    code:this.code,
                    policyNum:this.policyNum,
                    actStatus:this.actStatus,
                    cardtypeType:this.cardtypeType,
                    phIdNum:this.phIdNum,
                    exportCode:this.exportCode,
                    phName:this.phName,
                    ipPlateNum:this.ipPlateNum,
                    phTel:this.phTel,
                    pn: pn
                }
                this.$store.dispatch('cards_page_01', param)
            },
            dateTimeChange(val){
                if(val[0]!='')
                    this.actTime=val[0]+' - '+val[1];
                else
                    this.actTime='';
            },
            lock(obj){
                this.$store.dispatch('cards_lock',{cardsId:obj.id}).then((res)=>{
                    this.search();
                });
            },
            unlock(obj){
                this.$store.dispatch('cards_unlock',{cardsId:obj.id}).then((res)=>{
                    this.search();
                });
            },
            unAct(obj){
                this.$store.dispatch('cards_unAct',{cardsId:obj.id}).then((res)=>{
                    this.search();
                });
            },
            e(){
                if(this.selections.length==0){
                    this.$Message.warning("请选择要导出的卡数据")
                    return ;
                }
                let ids=[];
                for(var i=0;i<this.selections.length;i++){
                    ids.push(this.selections[i].id)
                }
                ids=ids.join(",");
                this.$store.dispatch('car_cards_act_export',{ids:ids,"exportStatus":"1"}).then((res)=>{
                    if(res&&res.resCode&&res.resCode=='success'){
                        this.$Message.success("卡激活数据导出成功")
                        this.search();
                        let url=res.resData;
                        window.open(url,'_blank')
                    }else if(res&&res.resCode&&res.resCode=='fail'){
                        this.$Message.success("卡激活数据导出失败>>"+res.resMsg);
                    }

                });
            },
            eByQuery(){
                let param = {
                    exportStatus:"1",
                    salesmenId: this.salesmenId,
                    actTime: this.actTime,
                    code:this.code,
                    policyNum:this.policyNum,
                    actStatus:this.actStatus,
                    cardtypeType:this.cardtypeType,
                    phIdNum:this.phIdNum,
                    exportCode:this.exportCode,
                    phName:this.phName,
                    ipPlateNum:this.ipPlateNum,
                    phTel:this.phTel,
                }

                this.$store.dispatch('car_cards_act_export',param).then((res)=>{
                    if(res&&res.resCode&&res.resCode=='success'){
                        this.$Message.success("卡激活数据导出成功")
                        this.search();
                        let url=res.resData;
                        window.open(url,'_blank')
                    }else if(res&&res.resCode&&res.resCode=='fail'){
                        this.$Message.success("卡激活数据导出失败>>"+res.resMsg);
                    }

                });
            },
            createInput(name,value){
                let input=document.createElement('input')
                input.name=name;
                input.value=value;
                input.type='hidden';
                return input
            },
            getSelection(selection){
                this.selections=selection;
            },
            reset(){
                this.exportStatus='';
                this.exportCode='';
                this.phName='';
                this.code='';//卡号
                this.salesmenId= '';//分配业务员
                this.actTime='';//
                this.policyNum='';
                this.phIdNum='';
                this.ipPlateNum='';
                this.at='';
                this.phTel='';
            },
            handleFormatError(file){
                vm.$store.commit('upadteSpinshow',false);
                this.$Notice.warning({
                    title: '上传文件格式错误',
                    desc: '文件 ' + file.name + ' 格式不正确, 请选择 xls 或者 xlsx.'
                });
            },
            handleSuccess(response, file, fileList){
                this.$store.commit('upadteSpinshow',false);
                if(response&&response.resCode&&response.resCode=='success'){
                    this.$Message.success(response.resMsg);
                    this.search();
                }else if(response&&response.resCode&&response.resCode=='fail'){
                    this.$Message.error({
                        content: response.resMsg,
                        duration: 10,
                        closable: true
                    });
                }
            },
            handleError(error, file, fileList){
                this.$store.commit('upadteSpinshow',false);
                this.$Message.error('网络错误，请稍后再重试');
            },
            handleBeforUpload(){
                this.$store.commit('upadteSpinshow',true);
            },
            downloadTemplate(){
                window.open(consts.env+'/static/templates/act01.xls')
            }

        },
        mounted () {

            let param = {
                cardtypeType:this.cardtypeType,
                actStatus:this.actStatus,
            }
            this.$store.dispatch('cards_page_01',param)
            this.$store.dispatch('cards_dataReady')
        },
        components: {
            view01
        },
        data () {
            return {
                env:consts.env,
                at:'',
                selections:[],
                exportStatus:'',
                cardtypeType:408,
                exportCode:'',
                phName:'',
                code:'',//卡号
                actStatus:'0',//是否激活
                salesmenId: '',//分配业务员
                actTime: '',//
                policyNum:'',
                phIdNum:'',
                ipPlateNum:'',
                phTel:'',
                self:this,
                yonList: consts.yon,
                tableColums: [
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center',
                        fixed: 'left'
                    },
                    {
                        title: '卡号',
                        key: 'code',
                        fixed: 'left',
                        width: 100,

                    },
                    {
                        title: '保单号',
                        key: 'policyNum',
                        fixed: 'left',
                        width: 100,

                    },

                    {
                        title: '卡类型',
                        width: 100,
                        key: 'cardtypeName',
                    },
                    {
                        title: '批次',
                        width: 100,
                        key: 'batch',
                    },
                    {
                        title: '销售员',
                        width: 100,
                        key: 'salesmenName',
                    },

                    {
                        title: '卡面值',
                        width: 100,
                        key: 'faceVal',
                    },
                    {
                        title: '激活时间',
                        width: 100,
                        key: 'actAt',
                    },
                    {
                        title: '投保人/公司名称',
                        width: 100,
                        render:(h,param)=>{
                            return h('div',param.row.ccph.name)
                        }
                    },
                    {
                        title: '投保人证件号',
                        width: 200,

                        render:(h,param)=>{
                            return h('div',param.row.ccph.certCode)
                        }
                    },
                    {
                        title: '投保人/公司联系人',
                        width: 150,

                        render:(h,param)=>{
                            return h('div',param.row.ccph.linkName)
                        }
                    },
                    {
                        title: '投保人/公司联系电话',
                        width: 150,

                        render:(h,param)=>{
                            return h('div',param.row.ccph.tel)
                        }
                    },
                    {
                        title: '投保人/公司EMAIL',
                        width: 250,

                        render:(h,param)=>{
                            return h('div',param.row.ccph.email)
                        }
                    },
                    {
                        title: '报案电话',
                        key: 'reportTel',
                        fixed: 'left',
                        width: 100,

                    },
                    {
                        title: '导出编号',
                        key: 'exportCode',
                        width: 100,

                    },
                    {
                        title: '导出时间',
                        key: 'exportAt',
                        width: 100,

                    },


                    {
                        title: '操作',
                        key: 'action',
                        width: 350,
                        align: 'center',
                        render: (h, param) => {
                            let vm=this;
                            let btns = new Array;
                            let row = param.row;
                            btns.push(viewBtn(this, h, param));
                            if(row.act=='0'&&row.isLocked=='0'&&row.exportCode&&row.policyNum)
                                btns.push(uploadBDBtn(this, h, param));

                            if(row.act=='0')
                                btns.push(unActBtn(this,h,param))
                            if(row.isLocked=='0')
                                btns.push(lockBtn(this,h,param))

                            return h('div', btns)
                        }
                    }

                ],
                options: {
                    shortcuts: [
                        {
                            text: '最近2天',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 2);
                                return [start, end];
                            }
                        },
                        {
                            text: '最近5天',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 4);
                                return [start, end];
                            }
                        },
                        {
                            text: '最近7天',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                                return [start, end];
                            }
                        },
                        {
                            text: '最近10天',
                            value () {
                                const end = new Date();
                                const start = new Date();
                                start.setTime(start.getTime() - 3600 * 1000 * 24 * 10);
                                return [start, end];
                            }
                        }
                    ]
                }
            }
        }
    }
</script>
