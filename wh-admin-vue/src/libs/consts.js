
let consts={

}

consts.editBtn=(vm,h,param)=>{
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
                vm.edit(param.row)
            }
        }
    }, '编辑')
};
consts.viewBtn=(vm,h,param)=>{
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
                vm.view(param.row.id)
            }
        }
    }, '查看')
};

consts.delBtn=(vm,h,param)=>{
    return h('Poptip', {
        props: {
            confirm: '',
            title: '您确定要删除这条数据吗？'
        },
        style: {
            marginRight: '5px'
        },
        on: {
            'on-ok': () => {
                vm.del(param.row.id)
            }
        }
    }, [h('Button', {
        props: {
            type: 'error',
            size: 'small'
        }
    }, '删除')]);
};

consts.stopBtn=(vm,h,param)=>{
    return h('Poptip', {
        props: {
            confirm: '',
            title: '您确定要禁用这条数据吗？'
        },
        style: {
            marginRight: '5px'
        },
        on: {
            'on-ok': () => {
                vm.stop(param.row.id)
            }
        }
    }, [h('Button', {
        props: {
            type: 'error',
            size: 'small'
        }
    }, '禁用')]);
}
consts.actBtn=(vm,h,param)=>{
    return h('Poptip', {
        props: {
            confirm: '',
            title: '您确定要激活这条数据吗？'
        },
        style: {
            marginRight: '5px'
        },
        on: {
            'on-ok': () => {
                vm.active(param.row.id)
            }
        }
    }, [h('Button', {
        props: {
            type: 'success',
            size: 'small'
        }
    }, '激活')]);
}

consts.status=[{value:'0',label:'正常'},{value:'1',label:'禁用'}]

export default consts