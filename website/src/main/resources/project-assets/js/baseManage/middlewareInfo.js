/**
 * Created by 汪文汉 on 2017/07/27.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/baseManage/middlewareInfo/";

var formContainerID = '#editFormContainer';
var formID = '#ESBFormID';
var idWrapperId2 = '#wrapperId2';

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
        ajaxSave: contextPath + ajaxReqPre + 'save',
        ajaxQuery: contextPath + ajaxReqPre + 'query',
        initDomEvent: function () {

            CommonFunc.ajaxPostForm(global_Object.ajaxQuery, null, global_Object.bindForm, CommonFunc.msgEx);

            $("#selectOdin").on("click", function () {
                $.uniform.update($("#Odin").prop("checked", true));
                $.uniform.update($("#Rhapsody").prop("checked", false));
            });

            $("#btnSave").on("click", function () {
                var vals = CommonFunc.getForm(formID);
                vals.ESB_UserName = $("#ESB_UserName").val();
                vals.ESB_Password = $("#ESB_Password").val();
                var resp = {datas: JSON.stringify(vals)};
                CommonFunc.ajaxPostForm(global_Object.ajaxSave, resp, global_Object.afterSave);
            });
        },

        afterSave: function (data) {
            //console.log(data);  //测试

            if (data.success) {
                //CommonFunc.clearForm(formID);
                CommonFunc.msgSu();
            } else {
                CommonFunc.msgFa(data.errorMsg);
            }
        },

        bindForm: function (data) {
            if (data.success) {
                var obj = JSON.parse(data.data);
                $.uniform.update($("#Odin").prop("checked", false));
                CommonFunc.setForm(formID, data.data);
                $.uniform.update($("#"+obj.ESB_Type).prop("checked", true));
            } else {
                CommonFunc.msgFa(data.errorMsg);
            }

        }
    }
;