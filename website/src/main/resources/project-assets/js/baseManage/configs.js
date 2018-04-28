/**
 * Created by xuehao on 2017/07/27.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/baseManage/configs/";

var tableID = '#listTable';
var formContainerID = '#editFormContainer';
var formID = '#editForm';
var idWrapperId2 = '#wrapperId2';
var idConfigValueInput = '#configValueInput';
var idConfigValueSelect = '#configValueSelect';

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    ajaxQuery: contextPath + ajaxReqPre + 'query',
    ajaxEdit: contextPath + ajaxReqPre + 'edit',
    //常量
    CHAR_COMMA : '&#44;',    //逗号转义字符
    CHAR_COLON : '&#58;',    //冒号转义字符
    //配置参数值所在的元素ID
    idConfigValue : null,
    //初始化
    initDomEvent: function () {
        global_Object.bindTableData();  //加载列表

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
    },
    bindTableData: function () {
        // console.log(data);  //测试

        var columns = [
            {title: '名称', data: 'name', width: "20%"},
            {title: '取值', data: 'value', width: "30%"},
            {title: '描述', data: 'desp', width: "45%"},
            {title: '操作', data: null, className: 'optColumn'}
        ];
        var columnDefs = [
            $.fn.dataTable.columndefs.cutoff(0),{
            targets: 1,
            render: function (data, type, row, meta) {
                // console.log(data);  //测试
                // console.log(type);  //测试
                // console.log(row);  //测试
                // console.log(meta);  //测试

                var showData = data;
                var candidateValues = row.candidate_values;
                if(!CommonFunc.isEmpty(candidateValues)) {
                    var simpleObj = global_Object.createCandidateValuesObj(candidateValues); //生成候选值对象
                    var obj;
                    for(var iIndex in simpleObj) {
                        obj = simpleObj[iIndex];
                        if(obj.item1 == data) {
                            showData = obj.item2;
                            break;
                        }
                    }
                }
                return showData;
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
                 // console.log(respData); //测试

                if(respData.success) {
                    callback({
                        recordsTotal: respData.totalSize,//过滤之前的总数据量
                        recordsFiltered: respData.totalSize,//过滤之后的总数据量
                        data: respData.datas
                    });
                } else {
                    CommonFunc.msgFa(respData.errorMsg);
                }

                //调整高度
                CommonFunc.maxHeightToFrame(idWrapperId2);
            },
            error: CommonFunc.msgEx
        })
    },
    edit: function (rowIndex) {
        // console.log($(tableID).dataTable().fnGetData()[rowIndex]);  //测试

        var row = $(tableID).dataTable().fnGetData()[rowIndex];
        global_Object.bindCandidateValues(row);
        CommonFunc.setForm(formID, row);
        $(formContainerID).modal("show");
    },
    //绑定下拉列表
    bindCandidateValues: function (rowData) {
        // console.log(rowData);  //测试

        if(!CommonFunc.isEmpty(rowData.candidate_values)) {
            var candidateValues = rowData.candidate_values;
            var simpleObj = global_Object.createCandidateValuesObj(candidateValues); //生成候选值对象
            CommonFunc.bindSelect(idConfigValueSelect, simpleObj);

            //设置文本框与下拉列表的显示与隐藏
            global_Object.idConfigValue = idConfigValueSelect;
            $(idConfigValueSelect).val(rowData.value);
            $(idConfigValueSelect).removeClass("hidden");
            $(idConfigValueInput).addClass("hidden");
        } else {
            //设置文本框与下拉列表的显示与隐藏
            global_Object.idConfigValue = idConfigValueInput;
            $(idConfigValueInput).val(rowData.value);
            $(idConfigValueSelect).addClass("hidden");
            $(idConfigValueInput).removeClass("hidden");
        }
    },
    //生成候选值对象
    createCandidateValuesObj: function (candidateValues) {
        // console.log(candidateValues); //测试

        if(!CommonFunc.isEmpty(candidateValues)) {
            //替换转义字符
            candidateValues = candidateValues.replace(/\\,/g, '&#44;');   //逗号
            candidateValues = candidateValues.replace(/\\:/g, '&#58;');   //冒号
            //分割候选值
            var items = candidateValues.split(',');
            //将候选值转为通用的简单对象格式
            var  simpleObj = [], item, itemValName, itemVal, itemName;
            for(var iIndex in items) {
                item = items[iIndex];
                if(item.indexOf(":")>=0) {
                    //拆分单项
                    itemValName = item.split(':');
                    itemVal = itemValName[0];
                    itemName = itemValName[1];
                } else {
                    itemVal = item;
                    itemName = item;
                }
                //将转义字符改为原字符
                itemVal = itemVal.replace(/&#44;/g, ',');
                itemName = itemName.replace(/&#58;/g, ':');
                //生成对象数组
                simpleObj.push({
                    item1 : itemVal,
                    item2 : itemName
                });
            }
            return simpleObj;
        } else {
            return null;
        }
    },
    save: function () {
        CommonFunc.confirmAsync("您确定修改该参数吗？", function () {
            var vals = CommonFunc.getForm(formID);
            vals.value = $(global_Object.idConfigValue).val();
            CommonFunc.ajaxPostForm(global_Object.ajaxEdit, vals, global_Object.afterSave, CommonFunc.msgEx);
        });
    },
    afterSave: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            CommonFunc.clearForm(formID);
            $(formContainerID).modal("hide");
            global_Object.bindTableData();  //刷新列表
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },

};