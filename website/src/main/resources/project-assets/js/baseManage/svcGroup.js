/**
 * Created by xuehao on 2017/07/27.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/baseManage/svcGroup/";

var tableID ='#listTable';
var formContainerID = '#editFormContainer';
var formID ='#editForm';
var idWrapperId2 = '#wrapperId2';

$(document).ready(function () {
    global_Object.initDomEvent();
    global_Object.bindTableData();  //加载列表
});

var global_Object = {
    ajaxQuery : contextPath + ajaxReqPre + 'query',
    ajaxSave : contextPath + ajaxReqPre + 'save',
    ajaxDel : contextPath + ajaxReqPre + 'delete',
    initDomEvent: function () {

        $("#btnAdd").on("click", function () {
            global_Object.showModal();
        });

        $("#btnSave").on("click", function () {
            var vals = CommonFunc.getForm(formID);
            CommonFunc.ajaxPostForm(global_Object.ajaxSave,
                vals,
                global_Object.afterSave, CommonFunc.msgEx);
        });

        $("#btnQuery").on("click", function () {
            global_Object.bindTableData();  //刷新列表
        })

        $('#btnDelete').on("click", function () {
            var idList = [];
            $('.checkbox_select').each(function () {
                if (this.checked)
                    idList.push($(this).val());
            });
            if (idList && idList.length > 0) {
                global_Object.delMany(idList);
                $('[name="select_all"]').removeAttr("checked");
            } else {
                CommonFunc.msgFa('请选择待删除的信息！');
            }
        });
    },
    bindTableData: function () {
        // console.log(data);  //测试

        var columns = [
            {title: 'ID', data: 'id', visible: false},
            {title: '名称', data: 'name'},
            {title: '操作', data: null, width:"5%", className: 'optColumn'}
        ];
        var columnDefs = [{
            targets: 3,
            width: "75px",
            render: function (data, type, row, meta) {
                var html = '  <span onclick="global_Object.edit(' + meta.row
                    + ')" class="tableBtnImg" title="编辑"><i class="fa fa-pencil-square-o fa-lg cp"></i></span>';
                return html;
            }
        }];
        var myAttrs = {
            checkbox : true
        };
        CommonTable.createTableAdvanced(tableID, global_Object.bindTableAjax, columns, columnDefs, myAttrs);
    },
    bindTableAjax: function (data, callback, settings) {
        var startIndex = data.start;
        var queryWord = $("#queryWord").val();
        var reqData = {
            datas: JSON.stringify({
                queryWord : queryWord,
                startIndex: startIndex,
                pageSize: data.length
            })
        };
        $.ajax({
            url: global_Object.ajaxQuery,
            type: "post",
            contentType: "application/x-www-form-urlencoded",
            data: reqData,
            success: function (respData) {
                // console.log(respData.datas); //测试

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
        console.log(data);  //测试

        if(data.success) {
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
        CommonFunc.setForm(formID, $(tableID).dataTable().fnGetData()[rowIndex]);
    },

    delMany: function (idList) {
        if (CommonFunc.confirm("您确定删除吗？")) {
            var reqData = {
                strIdList: JSON.stringify(idList)
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxDel, reqData, global_Object.afterSave, CommonFunc.msgEx);
        }
    }
};