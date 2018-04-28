//公用的请求前缀
var ajaxReqPre = "/ajax/baseManage/dataSource/";

var tableID = '#listTable';
// var formContainerID = '#editFormContainer';
// var formID = '#editForm';
var formDbContainerID = '#editFormDbContainer';
var formWebContainerID = '#editFormWebContainer';
var formESBContainerID = '#editFormESBContainer';
var formESBID = '#editESBForm';
var formDbID = '#editDbForm';
var formWebID = '#editWebForm';
var idWrapperId2 = '#wrapperId2';

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    ajaxGetBaseInfo: contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxQuery: contextPath + ajaxReqPre + 'query',
    ajaxEdit: contextPath + ajaxReqPre + 'edit',
    ajaxTestConnect: contextPath + ajaxReqPre + 'testConnect',
    initDomEvent: function () {
        //绑定基础信息
        CommonFunc.ajaxPostForm(global_Object.ajaxGetBaseInfo, null, global_Object.bindBaseInfo, CommonFunc.msgEx);
        global_Object.bindTableData();  //加载列表

        // $("#btnSave").on("click", function () {
        //     // var vals = CommonFunc.getForm(formID);
        //     // CommonFunc.ajaxPostForm(global_Object.ajaxEdit,
        //     //     vals, global_Object.afterSave, CommonFunc.msgEx);
        //     global_Object.save();
        // });
        $("#btnSaveDB").on("click", function () {
            global_Object.saveDB();
        });

        $("#btnSaveWeb").on("click", function () {
            global_Object.saveWeb();
        });

        $("#btnQuery").on("click", function () {
            global_Object.bindTableData();  //刷新列表
        });
        $('#queryWord').on('keypress', function (event) {
            if (event.keyCode == "13") {
                global_Object.bindTableData();  //刷新列表
            }
        });

        $("#typeList").on("change", function () {
            global_Object.bindTableData();  //刷新列表
        });
    },
    bindBaseInfo: function (data) {
        //console.log(data.map);  //测试

        CommonFunc.bindSelect('#dbType', data.map.dbType);
        CommonFunc.bindSelect('#type', data.map.type);
        var myAttrs = {defaultIndex: 0}
        CommonFunc.bindSelectAdvanced('#typeList', data.map.typeList, myAttrs);
        //$("#typeList").val(null);
    },
    bindTableData: function () {
        // console.log(data);  //测试

        var columns = [
            {title: '名称', data: 'name', width: "20%"},
            {title: '取值', data: 'value', width: "30%"},
            {title: '描述', data: 'desp', width: "45%"},
            {title: '操作', data: null, width: "5%"}
        ];
        var columnDefs = [$.fn.dataTable.columndefs.cutoff(0), {
            targets: 1,
            render: function (data, type, row) {
                if (row.type == 9 || row.type == 10 || row.type == 11 || row.type == 12) {
                    var dataList = data.split(",").slice(0, -2);
                    return dataList.join(",");
                } else {
                    return data;
                }
            }
        }, $.fn.dataTable.columndefs.cutoff(2), {
            targets: 3,
            render: function (data, type, row, meta) {
                var html =
                    '  <span onclick="global_Object.edit(' + meta.row
                    + ')"><i class="fa fa-pencil-square-o fa-lg cp"></i></span>';
                return html;
            }
        }];
        CommonTable.createTable(tableID, global_Object.bindTableAjax, columns, columnDefs);
    },
    bindTableAjax: function (data, callback, settings) {
        var startIndex = data.start;
        var queryWord = $("#queryWord").val();
        var type = $("#typeList").val();
        var reqData = {
            datas: JSON.stringify({
                queryWord: queryWord,
                startIndex: startIndex,
                pageSize: data.length,
                type: type
            })
        };
        //console.log(reqData);
        $.ajax({
            url: global_Object.ajaxQuery,
            type: "post",
            contentType: "application/x-www-form-urlencoded",
            data: reqData,
            success: function (respData) {
                //console.log(respData.datas); //测试

                callback({
                    recordsTotal: respData.totalSize,//过滤之前的总数据量
                    recordsFiltered: respData.totalSize,//过滤之后的总数据量
                    data: respData.datas
                });

                //调整高度
                CommonFunc.maxHeightToFrame(idWrapperId2);
            },
            error: CommonFunc.msgEx
        })
    },
    showModal: function (type) {

        //console.log('test1');   //测试
        //console.log(type);   //测试

        // CommonFunc.clearForm(formID);
        if (CommonFunc.isEmpty(type)) {
            // $(formContainerID).modal("show");
        } else if (type == 11) {
            $(formESBContainerID).modal("show");
        } else if (type == 9) {
            $(formDbContainerID).modal("show");
        } else {
            $(formWebContainerID).modal("show");
        }

    },
    afterSave: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            // CommonFunc.clearForm(formID);
            CommonFunc.clearForm(formDbID);
            CommonFunc.clearForm(formWebID);
            // $(formContainerID).modal("hide");
            $(formDbContainerID).modal("hide");
            $(formWebContainerID).modal("hide");
            global_Object.bindTableData();  //刷新列表
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    edit: function (rowIndex) {
        //console.log($(tableID).dataTable().fnGetData()[rowIndex]);  //测试

        var row = $(tableID).dataTable().fnGetData()[rowIndex];
        var type = row.type;
        if (type != 9 && type != 10 && type != 11 && type != 12)
            type = "";
        global_Object.showModal(type);
        if (CommonFunc.isEmpty(type)) {
            // CommonFunc.setForm(formID, row);
        } else if (type == 9) {
            var items = row.value.split(","); //字符分割
            //console.log(items);  //测试
            row = {
                code: row.code,
                dbType: items[0],
                ip: items[1],
                port: items[2],
                dbName: items[3],
                username: items[4],
                password: items[5]
            };
            CommonFunc.setForm(formDbID, row);
        } else {
            var items = row.value.split(","); //字符分割
            //console.log(items);  //测试
            row = {
                code: row.code,
                type: items[0],
                ip: items[1],
                port: items[2],
                username: items[3],
                password: items[4]
            };
            CommonFunc.setForm(formWebID, row);
        }
    },

    // save: function () {
    //     if (confirm("您确定保存该参数吗？")) {
    //         var vals = CommonFunc.getForm(formID);
    //         CommonFunc.ajaxPostForm(global_Object.ajaxEdit,
    //             vals, global_Object.afterSave, CommonFunc.msgEx);
    //     }
    // },
    saveDB: function () {
        if (confirm("您确定保存数据库连接信息吗？")) {
            var formVals = CommonFunc.getForm(formDbID);
            var vals = {
                code: formVals.code,
                value: formVals.dbType + ',' + formVals.ip + ',' + formVals.port + ','
                + formVals.dbName + ',' + formVals.username + ',' + formVals.password
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxEdit,
                vals, global_Object.afterSave, CommonFunc.msgEx);
        }
    },

    saveWeb: function () {
        if (confirm("您确定保存Odin连接信息吗？")) {
            var formVals = CommonFunc.getForm(formWebID);
            var value;
            console.log(formVals.code);
            if (formVals.code == "0008") {
                value = formVals.ip + ',' + formVals.port + ','
                    + formVals.username + ',' + formVals.password
            } else {
                value = formVals.ip + ',' + formVals.port
            }
            var vals = {
                code: formVals.code,
                value: value
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxEdit,
                vals, global_Object.afterSave, CommonFunc.msgEx);
        }
    },

    //数据库连接信息
    testConnect: function () {
        // console.log('连接测试');  //测试

        var vals = CommonFunc.getForm(formDbID);
        CommonFunc.ajaxPostForm(global_Object.ajaxTestConnect,
            vals, global_Object.afterTestConnect, CommonFunc.msgExSimple);
    },
    afterTestConnect: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            CommonFunc.msgSu('连接成功！');
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },

};