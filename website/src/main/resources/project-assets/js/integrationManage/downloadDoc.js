/**
 * Created by xuehao on 2017/08/10.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/integrationManage/downloadDoc/";

var tableID = '#listTable';
var idSvcTypeList = '#svcTypeList'

var idTreeWrapper = '#treeWrapper';
var idWrapperId2 = '#wrapperId2';

var idLoginUserid = '#loginUserid';

//全局变量

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    treeAID: 0,
    ajaxDownload: contextPath + ajaxReqPre + 'download',
    ajaxListDownload: contextPath + ajaxReqPre + 'listDownload',
    initDomEvent: function () {
        global_Object.treeAID = 0;

        //绑定下拉列表
        global_Object.bindSvcType();

        //绑定业务系统树
        var params = {
            callback : global_Object.treeChange,
            triggerLevel : 1,
            triggerNullToOtherLevel: true,
            username: $(idLoginUserid).val()
        };
        CommonTree.init(params);

        $("#btnDownloadSelected").on("click", function () {
            global_Object.download(0);
        });
        $("#btnDownloadAll").on("click", function () {
            global_Object.download(1);
        });

        $(idSvcTypeList).on("change",function () {
            global_Object.bindTableData();
        });

        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId2);

        //设置滚动条
        CommonFunc.setScrollBarWithWrapper();
    },
    //绑定服务类型
    bindSvcType: function () {
        var svcTypeList = [{
            "item1" : "0",
            "item2" : "服务类型"
        }, {
            "item1" : "1",
            "item2" : "提供"
        }, {
            "item1" : "2",
            "item2" : "订阅"
        }];
        CommonFunc.bindSelect(idSvcTypeList, svcTypeList, "0");
    },
    treeChange: function (sysID, obj) {
        // console.log(sysID); //测试
        // console.log(obj); //测试

        //将选择的业务系统ID保存到全局变量中
        global_Object.treeAID = obj.id;
        //刷新列表
        global_Object.bindTableData();
    },
    bindTableData: function (aid) {
        // console.log(data);  //测试

        var columns = [
            {title: '服务类型', data: 'svcDirection', width: "13%"},
            {title: '服务代码', data: 'code', width: "25%"},
            {title: '服务名称', data: 'name', width: "25%"},
            {title: '版本号', data: 'version', width: "8%"},
            {title: '描述信息', data: 'desp', width: "22%"}
        ];
        var columnDefs = [{
            targets: 1,
            render: function (data, type, row, meta) {
                // console.log(data);  //测试
                // console.log(row);  //测试

                var className, faName, displayName;
                if (data == 1) {
                    className = 'prject-color-normal2';
                    faName = 'sign-in';
                    displayName = '提供';
                } else {
                    className = 'prject-color-normal3';
                    faName = 'sign-out';
                    displayName = '订阅';
                }
                var html = '<span class="' + className + '">'
                    + '<i class="fa fa-' + faName + '"></i> ' + displayName + '</span>';
                return html;
            }
        },
            $.fn.dataTable.columndefs.cutoff(2),
            $.fn.dataTable.columndefs.cutoff(3),
            $.fn.dataTable.columndefs.cutoff(4),
            $.fn.dataTable.columndefs.cutoff(5)
        ];
        var myAttrs = {
            paging : false,
            info : false,
            checkbox: true
        };
        CommonTable.createTableAdvanced(tableID, function (data, callback, settings) {
            var reqData = {
                aid: global_Object.treeAID,
                svcDirection : $(idSvcTypeList).val()
            };
            //ajax请求数据
            CommonFunc.ajaxPostForm(global_Object.ajaxListDownload, reqData, function (respData) {
                global_Object.bindTableAjax(respData, callback);
            }, CommonFunc.msgEx);
        }, columns, columnDefs, myAttrs);
    },
    bindTableAjax: function (respData, callback) {
        var svcList = [];
        if(!respData.success) {
            CommonFunc.msgSu(respData.errorMsg);
        } else {
            var datas = respData.datas;

            // console.log(datas); //测试

            if (datas && datas.length > 0) {
                svcList = datas
            }
        }

        //绑定数据到列表
        callback({
            recordsTotal: respData.totalSize,//过滤之前的总数据量
            recordsFiltered: respData.totalSize,//过滤之后的总数据量
            data: svcList
        });

        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId2);
    },
    afterPost: function (data) {
        if (data.success) {
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    /**
     * 下载
     * @param type  0-下载选择，1-下载全部
     */
    download: function (type) {
        var sidList = [];
        $(tableID+' .checkbox_select').each(function () {
            if (type == 1 || (type == 0 && this.checked))
                sidList.push($(this).val());
        });

        if (sidList && sidList.length > 0) {
            var data = {
                aid: global_Object.treeAID,
                sid: sidList.join(',')
            };

            var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxDownload, data);
            if (resp.success) {
                var a = document.getElementById("downPdf");
                a.href = contextPath + resp.data;
                a.download = resp.data;
                a.click();
            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }
        } else {
            CommonFunc.msgFa('请选择下载的服务！');
        }

    }
};