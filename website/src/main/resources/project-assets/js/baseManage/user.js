/**
 * Created by xuehao on 2017/07/27.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/baseManage/user/";

var tableID ='#listTable';
var formContainerID = '#editFormContainer';
var formID ='#editForm';
var formPwdContainerID = '#editPwdFormContainer';
var formPwdID ='#editPwdForm';
var idRole = '#role';
var idAppTreeSkin = '#appTreeSkin';
var idAppTree = '#appTree';
var idWrapperId2 = '#wrapperId2';

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    ajaxGetBaseInfo : contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxQuery : contextPath + ajaxReqPre + 'query',
    ajaxSave : contextPath + ajaxReqPre + 'save',
    ajaxChangePwd : contextPath + ajaxReqPre + 'changePwd',
    ajaxResetPwd : contextPath + ajaxReqPre + 'resetPwd',
    ajaxDel : contextPath + ajaxReqPre + 'delete',
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

        $(idRole).on("change", function () {
            global_Object.roleChange();
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

        var myAttrs = {
            required : true
        };
        CommonFunc.bindSelectAdvanced(idRole, data.map.role, myAttrs);

        global_Object.bindTableData();  //加载列表
    },
    bindTableData: function () {
        // console.log(data);  //测试

        var columns = [
            {title: '用户名', data: 'username'},
            {title: '角色', data: 'roleName'},
            {title: '操作', data: null, width:"8%"}
        ];
        var columnDefs = [{
            targets: 3,
            render: function (data, type, row, meta) {
                var html =
                    '  <span onclick="global_Object.showChangePwd(' + meta.row + ')"><i class="fa fa-key fa-lg cp"></i></span>'
                    + '  <span onclick="global_Object.edit(' + meta.row
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

                var users = [];
                var user;
                var tempContent;
                //设置角色名称
                var map = CommonFunc.getSelectOptions(idRole);
                $.each(respData.datas, function (index, item) {
                    user = item.obj;
                    tempContent = CommonFunc.isEmpty(user.role) ? '' : map[user.role];
                    user['roleName'] = CommonFunc.isEmpty(tempContent) ? '' : tempContent;
                    user['aidList'] = CommonFunc.isEmpty(item.appIdList) ? '' : JSON.stringify(item.appIdList);
                    users.push(user);
                });
                //绑定表格
                callback({
                    recordsTotal: respData.totalSize,//过滤之前的总数据量
                    recordsFiltered: respData.totalSize,//过滤之后的总数据量
                    data: users
                });

                //调整高度
                CommonFunc.maxHeightToFrame(idWrapperId2);
            },
            error: CommonFunc.msgEx
        })
    },
    saveClick: function () {
        var vals = CommonFunc.getForm(formID);
        var req = {
            strUser : JSON.stringify(vals),
            strAidList : JSON.stringify(CommonTree.getCheckedIdListFromMyData())
        };
        var data = CommonFunc.ajaxPostFormSync(global_Object.ajaxSave, req);
        global_Object.afterSave(data);
        if(data.success) {
            if (!CommonFunc.isEmpty(data.errorMsg))
                CommonFunc.msgSuModal('保存成功，请妥善保存密码：<br/>' + data.errorMsg);
        }
    },
    changePwd: function () {
        var vals = CommonFunc.getForm(formPwdID);
        if(vals.passwordOld != vals.confirmPasswordOld) {
            CommonFunc.msgFa('旧密码输入错误！');
            return;
        } else if(vals.password != vals.confirmPassword) {
            CommonFunc.msgFa('两次输入的新密码不同！');
            return;
        }
        var data = CommonFunc.ajaxPostFormSync(global_Object.ajaxChangePwd, vals);
        if(data.success) {
            $(formPwdContainerID).modal("hide");
            CommonFunc.clearForm(formPwdID);
            global_Object.bindTableData();  //刷新列表
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    resetPwd: function () {
        if(CommonFunc.confirm('您确定重置密码吗？')) {
            var vals = CommonFunc.getForm(formPwdID);
            var data = CommonFunc.ajaxPostFormSync(global_Object.ajaxResetPwd, vals);
            if(data.success) {
                $(formPwdContainerID).modal("hide");
                CommonFunc.clearForm(formPwdID);
                global_Object.bindTableData();  //刷新列表
                if(!CommonFunc.isEmpty(data.errorMsg))
                    CommonFunc.msgSuModal('重置成功，请妥善保存密码：<br/>'+data.errorMsg);
                else
                    CommonFunc.msgSu();
            } else {
                CommonFunc.msgFa(data.errorMsg);
            }
        }
    },
    roleChange: function (aidList) {
        //绑定业务系统树
        var val = $(idRole).val();
        if(!CommonFunc.isEmpty(val) && val != 0) {
            $(idAppTreeSkin).show();
            var myAttrs = {
                needESB:true,
                check : true,
                async : false,
                checkedIdList : aidList
            };
            CommonTree.init(myAttrs);
        } else {
            $(idAppTreeSkin).hide();
        }
    },
    showModal: function () {
        CommonFunc.clearForm(formID);
        $(idAppTreeSkin).hide();
        $(formContainerID).modal("show");
    },
    afterSave: function (data) {
        // console.log(data);  //测试

        if(data.success) {
            $(formContainerID).modal("hide");
            CommonFunc.clearForm(formID);
            global_Object.bindTableData();  //刷新列表
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    showChangePwd: function (rowIndex) {
        CommonFunc.clearForm(formPwdID);
        $(formPwdContainerID).modal("show");
        var rowData = $(tableID).dataTable().fnGetData()[rowIndex];
        var pwd = {
            id : rowData.id,
            passwordOld : rowData.password
        };
        CommonFunc.setForm(formPwdID, pwd);
    },
    edit: function (rowIndex) {
        global_Object.showModal();
        var rowData = $(tableID).dataTable().fnGetData()[rowIndex];
        CommonFunc.setForm(formID, rowData);
        //触发角色下拉列表事件
        global_Object.roleChange(rowData['aidList']);
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