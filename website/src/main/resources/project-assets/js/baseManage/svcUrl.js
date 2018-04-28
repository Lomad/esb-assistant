
//公用的请求前缀
var ajaxReqPre = "/ajax/baseManage/svcUrl/";

var tableID = '#listTable';
var formContainerID = '#editFormContainer';
var formID = '#editForm';
var svcType = '#svcType';
var esbAgent = '#esbAgent';
var idWrapperId2 = '#wrapperId2';

// var curentPageDatas = [];
// var errorElement = [];
// var fTable;
$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    ajaxGetBaseInfo: contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxQuery: contextPath + ajaxReqPre + 'query',
    ajaxSave: contextPath + ajaxReqPre + 'save',
    ajaxLinkTest: contextPath + ajaxReqPre + 'linkTest',
    ajaxDel: contextPath + ajaxReqPre + 'delete',
    initDomEvent: function () {
        //绑定基础信息
        CommonFunc.ajaxPostForm(global_Object.ajaxGetBaseInfo, null, global_Object.bindBaseInfo, CommonFunc.msgEx);

        $("#btnAdd").on("click", function () {
            global_Object.showModal();
        });


        $("#btnQuery").on("click", function () {
            global_Object.bindTableData();  //刷新列表
        });
        $('#queryWord').on('keypress', function (event) {
            if (event.keyCode == "13") {
                global_Object.bindTableData();  //刷新列表
            }
        });

        $("#svcType").on("change", function () {
            global_Object.showTips();
        });

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
    bindBaseInfo: function (data) {
        // console.log(data);  //测试
        // console.log(data.map.groupId);  //测试

        if(!CommonFunc.isEmpty(data) && !CommonFunc.isEmpty(data.map)) {
            CommonFunc.bindSelect(svcType, data.map.svcType);
            CommonFunc.bindSelect(esbAgent, data.map.esbAgent);

            global_Object.bindTableData();  //加载列表
        } else {
            CommonFunc.msgFa('获取基础数据失败！');
        }
    },
    bindTableData: function () {
        // console.log(data);  //测试

        var columns = [
            {title: '服务类型', data: 'svcTypeName', width:"10%"},
            {title: '服务地址(Url)', data: 'urlShort', width:"35%"},
            {title: 'ESB代理', data: 'esbAgentName', width:"10%"},
            {title: '简称', data: 'name', width:"20%"},
            {title: '状态', data: 'status', width:"10%"},
            {title: '操作', data: null, width:"10%", className: 'optColumn'}
        ];
        var columnDefs = [
            $.fn.dataTable.columndefs.cutoff(1),
            $.fn.dataTable.columndefs.cutoff(2),
            $.fn.dataTable.columndefs.cutoff(3),
            $.fn.dataTable.columndefs.cutoff(4), {
            targets: 5,
            render: function (data, type, row, meta) {
                var html;
                if (row.status == 1) {
                    html = '<span class="prject-color-success"><i class="fa s-icon fa-check-circle"></i> 访问正常</span>';
                } else if (row.status == 0) {
                    html = '<span class="prject-color-failure"><i class="fa s-icon fa-times-circle"></i> 无法访问</span>';
                } else {
                    html = '';
                }
                return html;
            }
        }, {
            targets: 6,
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
        CommonTable.createTableAdvanced(tableID, global_Object.bindTableAjax, columns, columnDefs ,myAttrs);
    },
    bindTableAjax: function (data, callback, settings) {
        var startIndex = data.start;
        var queryWord = $("#queryWord").val();
        var reqData = {
            datas: JSON.stringify({
                queryWord: queryWord,
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
                //console.log(respData); //测试

                var tempContent;

                //设置服务类型名称
                var svcTypes = CommonFunc.getSelectOptions(svcType);
                $.each(respData.datas, function (index, item) {
                    if (CommonFunc.isEmpty(item.svcType))
                        item['svcTypeName'] = '';
                    else
                        item['svcTypeName'] = svcTypes[item.svcType];
                });
                //设置ESB代理标志名称
                var esbAgentList = CommonFunc.getSelectOptions(esbAgent);
                $.each(respData.datas, function (index, item) {
                    if (CommonFunc.isEmpty(item.esbAgent))
                        item['esbAgentName'] = '';
                    else
                        item['esbAgentName'] = esbAgentList[item.esbAgent];
                });

                $.each(respData.datas, function (index, item) {
                    tempContent = CommonFunc.isEmpty(item.url) ? '' : item.url;
                    item['urlShort'] = '<div class="showEllipsis" title="'+tempContent +'"' +
                        ' style = "width:560px">'+tempContent +'</div>';
                });

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
        global_Object.showTips();
    },
    showTips: function () {
        var svcTypeVal = $("#svcType").val();
        var svcTypeText = $("#svcType").find("option:selected").text();
        var tips = "";

        if (svcTypeVal == 0)
            tips = svcTypeText + "地址示例：http://127.0.0.1:9180/services/test?wsdl";
        else if (svcTypeVal == 1)
            tips = svcTypeText + "地址示例：http://127.0.0.1:9180/user/login";
        else if (svcTypeVal == 2)
            tips = svcTypeText + "地址示例：127.0.0.1:9180";

        $("#urlTips").text(tips);
    },
    afterSave: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            $(formContainerID).modal("hide");
            CommonFunc.clearForm(formID);
            global_Object.bindTableData();  //刷新列表
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    linkTest: function (data) {
        // console.log(data);  //测试
        if (data.success) {
            CommonFunc.msgSuModal("链接成功");
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },

    edit: function (rowIndex) {
        global_Object.showModal();
        CommonFunc.setForm(formID, $(tableID).dataTable().fnGetData()[rowIndex]);
        global_Object.showTips();
    },

    delMany: function (idList) {
        if (CommonFunc.confirm("您确定删除吗？")) {
            var reqData = {
                strIdList: JSON.stringify(idList)
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxDel, reqData, global_Object.afterSave, CommonFunc.msgEx);
        }
    },
//保存
    saveClick: function(){
        var vals = CommonFunc.getForm(formID);
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxSave, vals);
        global_Object.afterSave(resp);
    },

    linkClick: function () {
        var vals = CommonFunc.getForm(formID);
        CommonFunc.ajaxPostForm(global_Object.ajaxLinkTest,
            vals,
            global_Object.linkTest, CommonFunc.msgEx);

    }
};