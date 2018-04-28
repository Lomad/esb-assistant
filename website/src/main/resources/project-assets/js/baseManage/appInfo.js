/**
 * Created by xuehao on 2017/07/27.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/baseManage/appInfo/";

var tableID = '#listTable';
var formContainerID = '#editFormContainer';
var formID = '#editForm';
var orgId = '#orgId';
var queryOrg = '#queryOrg';
var queryStatus = '#queryStatus';
var appType = '#appType';
var id_direction = '#direction';
// var appStatus = '#appStatus';
var idWrapperId2 = '#wrapperId2';
// var appIdCurrent = '#appIdCurrent';

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    ajaxGetBaseInfo: contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxQuery: contextPath + ajaxReqPre + 'query',
    ajaxSave: contextPath + ajaxReqPre + 'save',
    ajaxUpdateStatus: contextPath + ajaxReqPre + 'updateStatus',
    ajaxDel: contextPath + ajaxReqPre + 'delete',
    //常量
    STATUS_ENABLE: 1,   //已启用
    STATUS_DISABLE: 0,  //已停用
    //初始化
    initDomEvent: function () {
        //绑定基础信息
        CommonFunc.ajaxPostForm(global_Object.ajaxGetBaseInfo, null, global_Object.bindBaseInfo, CommonFunc.msgEx);

        $("#btnAdd").on("click", function () {
            global_Object.showModal();
        });

        $("#btnSave").on("click", function () {
            global_Object.save();
        });

        $("#btnQuery").on("click", function () {
            global_Object.bindTableData();  //刷新列表
        });
        $('#queryWord').on('keypress', function (event) {
            if (event.keyCode == "13") {
                global_Object.bindTableData();  //刷新列表
            }
        });
        $(queryOrg).on("change", function () {
            global_Object.bindTableData();  //刷新列表
        });
        $(queryStatus).on("change", function () {
            global_Object.bindTableData();  //刷新列表
        });

        $('#btnEnable').on("click", function () {
            global_Object.updateStatus(global_Object.STATUS_ENABLE);
        });
        $('#btnDisable').on("click", function () {
            global_Object.updateStatus(global_Object.STATUS_DISABLE);
        });
        $('#btnDelete').on("click", function () {
            var idList = CommonTable.getSelectedData(tableID);
            if (idList && idList.length > 0) {
                global_Object.delMany(idList);
                $('[name="select_all"]').removeAttr("checked");
            } else {
                CommonFunc.msgFa('请选择信息！');
            }
        });
    },
    bindBaseInfo: function (data) {
        // console.log(data);  //测试

        if (CommonFunc.isEmpty(data.map.org) || data.map.org.length < 1) {
            CommonFunc.msgFa('机构信息为空！');
            return;
        }

        CommonFunc.bindSelect(orgId, data.map.org);
        CommonFunc.bindSelect(id_direction, data.map.direction);
        CommonFunc.bindSelect(appType, data.map.appType);
        // CommonFunc.bindSelect(appStatus, data.map.status, '1');

        CommonFunc.bindSelectAdvanced(queryOrg, data.map.org, {required: true, requiredText: '全部机构'});
        CommonFunc.bindSelectAdvanced(queryStatus, data.map.status, {required: true, requiredText: '全部状态'});

        global_Object.bindTableData();  //加载列表
    },
    bindTableData: function () {
        // console.log(data);  //测试

        var statusMap = CommonFunc.getSelectOptions(queryStatus);
        var columns = [
            {title: '机构', data: 'orgName', width: "15%"},
            {title: '代码(AppId)', data: 'appId', width: "12%"},
            {title: '名称', data: 'appName', width: "21%"},
            {title: '类型', data: 'appTypeName', width: "80px"},
            {title: '方向', data: 'directionName', width: "18%"},
            {title: '状态', data: 'status'},
            {title: '排序', data: 'order_num'},
            {title: '操作', data: null, className: 'optColumn', width: "75px"}
        ];
        var columnDefs = [
            $.fn.dataTable.columndefs.cutoff(1),
            $.fn.dataTable.columndefs.cutoff(2),
            $.fn.dataTable.columndefs.cutoff(3),
            $.fn.dataTable.columndefs.cutoff(4),
            $.fn.dataTable.columndefs.cutoff(5), {
            targets: 6,
            render: function (data, type, row, meta) {
                // console.log(row);  //测试

                var html, statusName = statusMap[data];
                if (row.status == global_Object.STATUS_ENABLE) {
                    html = '<span class="prject-color-success"><i class="fa fa-check-circle"></i> ' + statusName + '</span>';
                } else if (row.status == global_Object.STATUS_DISABLE) {
                    html = '<span class="prject-color-failure"><i class="fa fa-times-circle"></i> ' + statusName + '</span>';
                } else {
                    html = '<span><i class="fa fa-circle-o"></i>未知</span>'
                }
                return html;
            }
        }, {
            targets: 8,
            width: "75px",
            render: function (data, type, row, meta) {
                var html = '  <span onclick="global_Object.edit(' + meta.row
                    + ')" class="tableBtnImg" title="编辑"><i class="fa fa-pencil-square-o fa-lg cp"></i></span>';
                return html;
            }
        }];
        var myAttrs = {
            checkbox: true
        };
        CommonTable.createTableAdvanced(tableID, global_Object.bindTableAjax, columns, columnDefs, myAttrs);
    },
    bindTableAjax: function (data, callback, settings) {
        var startIndex = data.start;
        var queryWord = $("#queryWord").val();
        var reqData = {
            datas: JSON.stringify({
                queryWord: queryWord,
                startIndex: startIndex,
                orgId: $(queryOrg).val(),
                status: $(queryStatus).val(),
                pageSize: data.length
            })
        };
        $.ajax({
            url: global_Object.ajaxQuery,
            type: "post",
            contentType: "application/x-www-form-urlencoded",
            data: reqData,
            success: function (respData) {
                // console.log(respData); //测试

                //设置机构名称
                var items = CommonFunc.getSelectOptions(orgId);
                $.each(respData.datas, function (index, item) {
                    item['orgName'] = CommonFunc.isEmpty(item.orgId) ? '' : items[item.orgId];
                });
                //设置AppType名称
                items = CommonFunc.getSelectOptions(appType);
                $.each(respData.datas, function (index, item) {
                    item['appTypeName'] = CommonFunc.isEmpty(item.appType) ? '' : items[item.appType];
                });
                //设置direction名称
                items = CommonFunc.getSelectOptions(id_direction);
                $.each(respData.datas, function (index, item) {
                    item['directionName'] = CommonFunc.isEmpty(item.direction) ? '' : items[item.direction];
                });
                // //设置Status名称
                // items = CommonFunc.getSelectOptions(queryStatus);
                // $.each(respData.datas, function (index, item) {
                //     item['statusName'] = CommonFunc.isEmpty(item.status) ? '' : items[item.status];
                // });

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
    showModal: function () {
        CommonFunc.clearForm(formID);
        $(formContainerID).modal("show");
    },
    afterSave: function (data) {
        if (data.success) {
            $(formContainerID).modal("hide");
            CommonFunc.clearForm(formID);
            global_Object.bindTableData();  //刷新列表
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    edit: function (rowIndex) {
        global_Object.showModal();
        var vals = $(tableID).dataTable().fnGetData()[rowIndex];
        vals.appIdOld = vals.appId;
        CommonFunc.setForm(formID, vals);
    },
    save: function () {
        var vals = CommonFunc.getForm(formID);
        var appId = vals.appId;
        var appIdOld = vals.appIdOld;
        if(appIdOld != appId) {
            var tipInfo = '<span class="msg-stress">代码(AppId)发生变化，包含的所有服务代码前缀将改为最新代码，替换规则如下：</span><br>';
            tipInfo += '1、如果原服务代码为“' + appIdOld + '1001”，则修改后代码为“' + appId + '1001”；<br>';
            tipInfo += '2、如果原服务代码为“1001”，则修改后代码为“' + appId + '1001”；<br>';
            tipInfo += '<span class="msg-stress">您确定修改吗？</span>';
            CommonFunc.confirmAsync(tipInfo, function () {
                CommonFunc.ajaxPostForm(global_Object.ajaxSave, vals, global_Object.afterSave);
            });
        } else {
            CommonFunc.ajaxPostForm(global_Object.ajaxSave, vals, global_Object.afterSave);
        }
    },
    updateStatus: function (status) {
        var idList = CommonTable.getSelectedData(tableID);
        if (idList && idList.length > 0) {
            CommonFunc.confirmAsync("您确定修改状态吗？", function () {
                var reqData = {
                    strIdList: JSON.stringify(idList),
                    status: status
                };
                CommonFunc.ajaxPostForm(global_Object.ajaxUpdateStatus, reqData, global_Object.afterSave);
            });
        } else {
            CommonFunc.msgFa('请选择信息！');
        }
    },
    delMany: function (idList) {
        CommonFunc.confirmAsync("您确定删除吗？", function () {
            var reqData = {
                strIdList: JSON.stringify(idList)
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxDel, reqData, global_Object.afterSave);
        });
    }
};