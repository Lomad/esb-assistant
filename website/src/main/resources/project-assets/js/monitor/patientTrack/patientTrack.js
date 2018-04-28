
$(document).ready(function () {
    global_Object.initDomEvent();
});
var global_Object = {
    //页面对象ID
    idTreeWrapper : '#treeWrapper',
    idWrapperId2 : '#wrapperId2',

    //初始化
    initDomEvent: function () {
        //绑定业务系统树
        global_Object.bindTree();
    },
    //绑定业务系统树
    bindTree: function () {
        // console.log(resp);  //测试
        //绑定树
        var params = {
            needESB: false,
            callback: global_Object.treeChange,
            sysDirection: 1,
            triggerLevel: 1,
            triggerNullToOtherLevel: true
        };
        CommonTree.init(params);
    },
    treeChange: function (nodeId, myData, treeObj, treeNode) {
        // console.log(nodeId);  //测试
        // console.log(myData);  //测试
        // console.log(treeObj);  //测试
        // console.log(treeNode);  //测试

        //绑定错误信息的统计列表
        if(treeNode.level==0) {
            //机构
            // global_Object.bindTableData(treeNode.myData.id);
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
        DetailedTimes.type = BizStable.MonitorDateType.TODAY;
        DetailedTimes.idTreeWrapper = global_Object.idTreeWrapper;
        DetailedTimes.idWrapperId2 = global_Object.idWrapperId2;
        DetailedTimes.queryDateShow = true;
        DetailedTimes.statusSelectShow = false;
        DetailedTimes.statusSelectEnable = false;
        DetailedTimes.duringSelectShow = false;
        DetailedTimes.initDomEvent();
    },
}