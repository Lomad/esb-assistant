/**
 * Created by xuehao on 2017/08/10.
 */

//公用的请求前缀
var ajaxLogReqPre = "/ajax/integrationManage/simulationTestLog/";

//ID
var idStepResultForm = '#stepResultForm';
var idSvcTestResultList = '#svcTestResultList';

//全局变量
var svcExtList = [];   //用于保存服务列表
var svcCurrentIndex = 0;
var stepLogList = [];

var global_Object_Step3 = {
    ajaxFinishTest: contextPath + ajaxLogReqPre + 'finishTest',
    ajaxExportReport: contextPath + ajaxLogReqPre + 'exportReport',
    //显示模拟测试的结果
    showResultList: function () {
        // console.log(data);  //测试

        var retResult = false;
        if(!CommonFunc.isEmpty(global_Object_Step2.checkFinish()))
            return retResult;

        var stepLogList = global_Object_Step2.stepLogList;
        var svcExtList = global_Object_Step2.svcExtList;
        if (stepLogList && stepLogList.length > 0) {
            var html = '';
            var badgeColor, badgeContent;
            for (var i = 0, len = stepLogList.length; i < len; i++) {
                if(CommonFunc.isEmpty(stepLogList[i]))
                    return retResult;

                //生成单项html
                html += '<li class="list-group-item">';
                //徽章设为测试成功标志
                if (stepLogList[i].result == 1) {
                    badgeColor = "64BD2E";
                    badgeContent = "成功";
                } else if (stepLogList[i].result == 2) {
                    badgeColor = "CC0000";
                    badgeContent = "失败";
                } else {
                    badgeColor = "e2e2e2";
                    badgeContent = "未知";
                }
                html += '<span class="badge" style="background-color:#' + badgeColor + '">' + badgeContent + '</span>';
                html += '' + svcExtList[i].svcInfoModel.name + '</li>';
            }
            $(idSvcTestResultList).html(html);
            retResult = true;
        }
        return retResult;
    },
    //完成模拟测试
    finishTest: function () {
        // console.log(data);  //测试

        var logContent = CommonFunc.getForm(idStepResultForm);
        var vals = {
            id : global_Object.testID,
            result : logContent.result,
            desp : logContent.desp,
            user_id : 0,
            etime : new Date()
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object_Step3.ajaxFinishTest, vals);
        if (resp.success) {
            $("select[id='logResult']").val("").focus();
            $("textarea[name='desp']").val("").focus();
            $(idSvcTestResultList).html("");
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },

    exportReport:function () {
        var vals = {
            tid : global_Object.testID
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object_Step3.ajaxExportReport, vals);

        if (resp.success) {
            var a = document.getElementById("downReport");
            a.href = contextPath + resp.data;
            a.download = resp.data;
            a.click();
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    }
};