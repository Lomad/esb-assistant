//公用的请求前缀
var ajaxReqPre = "/ajax/error/";

$(document).ready(function () {
    global_Object.initDomEvent();
});
var global_Object = {
    //数据请求链接
    ajaxCountErrorProviders: contextPath + ajaxReqPre + 'countErrorProviders',

    //页面对象ID
    idTreeWrapper : '#treeWrapper',
    idWrapperId2 : '#wrapperId2',

    //初始化
    initDomEvent: function () {
        //绑定业务系统树
        var params = {
            timeType : BizStable.MonitorDateType.TODAY   //今天
        };
        CommonFunc.ajaxPostJson(global_Object.ajaxCountErrorProviders, params, global_Object.bindTree);

        // $('button.btn-time').click(function () {
        //     if (!$(this).hasClass('blue')) {
        //         $(this).addClass('blue').siblings().removeClass('blue');
        //
        //     }
        // });
    },
    //绑定业务系统树
    bindTree: function (resp) {
        // console.log(resp);  //测试

        if(resp.success) {
            if(CommonFunc.isEmpty(resp.data)) {
                //调整高度
                CommonFunc.maxHeightToFrame(global_Object.idTreeWrapper, global_Object.idWrapperId2);
                //显示提示信息
                $('#appTree').html('未发现错误信息');
                //绑定列表
                global_Object.bindTable();
            } else {
                //绑定树
                var params = {
                    needESB: false,
                    callback: global_Object.treeChange,
                    sysDirection: 1,
                    triggerLevel: 1,
                    triggerNullToOtherLevel: true,
                    strIdList : JSON.stringify(resp.data)
                };
                CommonTree.init(params);
            }
        } else {
            CommonFunc.msgFa(resp.msgError);
        }
    },
    treeChange: function (nodeId, myData, treeObj, treeNode) {
        // console.log(nodeId);  //测试
        // console.log(myData);  //测试
        // console.log(treeObj);  //测试
        // console.log(treeNode);  //测试

        //绑定错误信息的统计列表
        if(treeNode.level==0) {
            //机构

        } else {
            //业务系统

            //绑定列表
            global_Object.bindTable(treeNode.myData.appId, treeNode.myData.appName);
        }

        //调整高度
        CommonFunc.maxHeightToFrame(global_Object.idTreeWrapper, global_Object.idWrapperId2);

        //设置滚动条
        CommonFunc.setScrollBarWithWrapper();
    },
    //绑定列表
    bindTable: function (appId, appName) {
        // console.log(nodeId);  //测试
        // console.log(myData);  //测试
        // console.log(treeObj);  //测试
        // console.log(treeNode);  //测试

        DetailedTimes.serverAppId = appId;
        DetailedTimes.serverAppName = appName;
        DetailedTimes.type = BizStable.MonitorDateType.ERROR_QUEUE;
        DetailedTimes.status = BizStable.MonitorStatus.FAILURE;
        DetailedTimes.idTreeWrapper = global_Object.idTreeWrapper;
        DetailedTimes.idWrapperId2 = global_Object.idWrapperId2;
        DetailedTimes.statusSelectEnable = false;
        DetailedTimes.duringSelectShow = false;
        DetailedTimes.initDomEvent();
    },
}