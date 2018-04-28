/**
 * Created by xuehao on 2017/08/10.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/integrationManage/simulationTest/";
var ajaxStepLogReqPre = "/ajax/integrationManage/simulationTestStepLog/";
var ajaxBusinessReqPre = "/ajax/integrationManage/simulationBusinessTest/";
var ajaxStructureReqPre = "/ajax/serviceManage/svcStructure/";
var ajaxUnitTestReqPre = "/ajax/integrationManage/svcUnitTest/";

//ID
var idStepLogForm = '#stepLogForm';
var idSvcList = '#svcList';

//全局变量
// var svcExtList = [];   //用于保存服务列表
var svcCurrentIndex = 0;
// var stepLogList = [];

var global_Object_Step2 = {
    svcExtList: [],   //用于保存服务列表
    stepLogList: [],  //用于保存步骤明细列表
    ajaxListSimulationList: contextPath + ajaxReqPre + 'listSimulationList',
    ajaxSave: contextPath + ajaxStepLogReqPre + 'save',
    ajaxSend: contextPath + ajaxBusinessReqPre + 'send',
    ajaxReceive: contextPath + ajaxBusinessReqPre + 'receive',
    ajaxExport: contextPath + ajaxStructureReqPre + 'export',
    //ajaxAfterReceive: contextPath + ajaxUnitTestReqPre + 'receive',
    ajaxAfterReceive: contextPath + ajaxBusinessReqPre + 'afterReceive',
    initDomEvent: function () {

        global_Object_Step2.svcExtList=[];
        global_Object_Step2.stepLogList=[];

        $("#btnPost").on("click", function () {
            global_Object_Step2.postClick();
        });

        $("#btnSaveAndGoNext").on("click", function () {
            global_Object_Step2.saveAndGoNextClick();
        });

        $("#btnCreateMsgIn").on("click", function () {
            global_Object_Step2.createMsgIn();
        });

    },
    initSvcStep: function (aid, tid) {
        //console.log(tid);  //测试

        var data = {
            aid: aid,
            tid: tid
        };
        CommonFunc.ajaxPostForm(global_Object_Step2.ajaxListSimulationList, data, global_Object_Step2.bindList, CommonFunc.msgEx);
    },
    //绑定服务列表
    bindList: function (data) {
        // console.log(data);  //测试

        if (!data.success) {
            CommonFunc.msgEx('获取服务列表失败！' + data.errorMsg);
            return;
        }
        var html = '';
        var activeIndex;
        var datas = data.datas;
        if (datas && datas.length > 0) {
            var badgeCss,badgeContent;
            //清空数组
            global_Object_Step2.svcExtList.splice(0, global_Object_Step2.svcExtList.length);
            global_Object_Step2.stepLogList.splice(0, global_Object_Step2.stepLogList.length);
            //绑定待测试的服务清单
            for (var i = 0, len = datas.length; i < len; i++) {
                //console.log(datas[i]);  //测试

                //保存到全局变量
                global_Object_Step2.svcExtList.push(datas[i]);
                global_Object_Step2.stepLogList.push(datas[i].stepLogModel);

                //生成单项html
                html += '<li id="svcItem' + i + '" class="list-group-item shape-link" onclick="global_Object_Step2.changeSvc(' + i + ')">';
                //徽章设为测试次数
                if (datas[i].testCount && datas[i].testCount > 0) {
                    if (datas[i].stepLogModel.result && datas[i].stepLogModel.result == 1) {
                        badgeCss = "badgeSuccess";
                        badgeContent = "成功";
                    }else if (datas[i].stepLogModel.result && datas[i].stepLogModel.result == 2) {
                        badgeCss = "badgeFailure";
                        badgeContent = "失败";
                    }else {
                        badgeCss = "badgeNormal";
                        badgeContent = "未知";
                    }

                    html += '<span class="badge ' + badgeCss + '">' + badgeContent + '</span>';
                } else if (CommonFunc.isEmpty(activeIndex)) {
                    activeIndex = i;
                }

                //如果是提供的服务则使用“sign-in”图标，如果调用外部服务则使用“sign-out”图标
                if(datas[i].obj.aid == datas[i].svcInfoModel.aid)
                    html += '<i class="fa fa-sign-in prject-color-normal2" title="提供服务"></i>';
                else
                    html += '<i class="fa fa-sign-out prject-color-normal3" title="消费服务"></i>';
                html += '' + datas[i].svcInfoModel.name + '</li>';
            }
            //设置激活项
            if (CommonFunc.isEmpty(activeIndex))
                activeIndex = datas.length - 1;
            global_Object_Step2.changeSvc(activeIndex, false);
        }
        $(idSvcList).html(html);
    },
    //请求后台
    postClick: function () {
        // console.log('sendClick');    //测试
        // console.log(global_Object_Step2.stepLogList);    //测试
        //console.log(global_Object_Step2.svcExtList[svcCurrentIndex].obj);    //测试

        //请求消息
        var vals = {
            svcType: global_Object.urlApp.svcType,
            url: global_Object.urlApp.url,
            sid: global_Object_Step2.svcExtList[svcCurrentIndex].svcInfoModel.id
        };
        var resp, stepLogContent;
        var svcExt = global_Object_Step2.svcExtList[svcCurrentIndex];
        if(svcExt.obj.aid == svcExt.svcInfoModel.aid) {
            stepLogContent = CommonFunc.getForm(idStepLogForm);
            var outMsg = stepLogContent.out_msg;
            //console.log(outMsg);
            if (CommonFunc.isEmpty(outMsg)) {
                CommonFunc.msgFa('输入信息不能为空！');
                return;
            }
            vals.msg = outMsg;
            resp = CommonFunc.ajaxPostFormSync(global_Object_Step2.ajaxSend, vals, global_Object.afterPost, CommonFunc.msgEx);
            if(resp.success){
                // if(resp.data.result == 1)
                //     resp.data.result = "正常";
                // else
                //     resp.data.result = "失败";
                CommonFunc.setForm(idStepLogForm, resp.data);
                //console.log(resp.data);
                //设置明细步骤的测试日志
                stepLogContent = CommonFunc.getForm(idStepLogForm);
                global_Object_Step2.stepLogList[svcCurrentIndex] = {
                    tid: global_Object.testID,
                    sid: global_Object_Step2.svcExtList[svcCurrentIndex].svcInfoModel.id,
                    user_id: 0,
                    btime: new Date(),
                    out_msg: stepLogContent.out_msg,
                    ack_msg: stepLogContent.ack_msg,
                    result: stepLogContent.result,
                    etime: null
                };

                //检测服务清单是否测试完成
                var stepLogList = global_Object_Step2.stepLogList;
                //console.log(svcCurrentIndex);
                //console.log(stepLogList.length);

                var iIndexTemp = global_Object_Step2.checkFinish();
                //console.log(iIndexTemp);
                if (CommonFunc.isEmpty(iIndexTemp))
                    CommonFunc.msgSu();
                else if(svcCurrentIndex == stepLogList.length - 1)
                    CommonFunc.msgSu('第' + (iIndexTemp + 1) + '个服务未测试');
                //保存消息，并进入下一步
                global_Object_Step2.saveAndGoNextClick();

            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }

        } else {
            CommonFunc.ajaxPostForm(global_Object_Step2.ajaxReceive, vals, global_Object.afterPost, CommonFunc.msgEx);
            global_Object_Step2.timer = setInterval(global_Object_Step2.afterPost,2000);
        }

    },

    afterPost:function () {
        var resp = CommonFunc.ajaxPostFormSync(global_Object_Step2.ajaxAfterReceive);
        console.log(resp);
            if(resp.success) {
                clearInterval(global_Object_Step2.timer);
                // if(resp.data.result == 1)
                //     resp.data.result = "正常";
                // else
                //     resp.data.result = "失败";
                CommonFunc.setForm(idStepLogForm, resp.data);
                //console.log(resp.data);
                //设置明细步骤的测试日志
                stepLogContent = CommonFunc.getForm(idStepLogForm);
                global_Object_Step2.stepLogList[svcCurrentIndex] = {
                    tid: global_Object.testID,
                    sid: global_Object_Step2.svcExtList[svcCurrentIndex].svcInfoModel.id,
                    user_id: 0,
                    btime: new Date(),
                    out_msg: stepLogContent.out_msg,
                    ack_msg: stepLogContent.ack_msg,
                    result: stepLogContent.result,
                    etime: null
                };

                //检测服务清单是否测试完成
                var stepLogList = global_Object_Step2.stepLogList;
                //console.log(svcCurrentIndex);
                //console.log(stepLogList.length);

                var iIndexTemp = global_Object_Step2.checkFinish();
                //console.log(iIndexTemp);
                if (CommonFunc.isEmpty(iIndexTemp))
                    CommonFunc.msgSu();
                else if (svcCurrentIndex == stepLogList.length - 1)
                    CommonFunc.msgSu('第' + (iIndexTemp + 1) + '个服务未测试');
                //保存消息，并进入下一步
                global_Object_Step2.saveAndGoNextClick();
            }

    },

    //保存结果，继续下一步
    saveAndGoNextClick: function () {
        // console.log('===================================');    //测试

        //获取测试服务的步骤明细
        var iIndex = svcCurrentIndex;
        var stepLog = global_Object_Step2.stepLogList[iIndex];
        if (CommonFunc.isEmpty(stepLog)) {
            CommonFunc.msgFa('请先发送(或接收)消息！');
            return;
        }
        //console.log(stepLog);
        var stepLogContent = CommonFunc.getForm(idStepLogForm);
        // if(stepLogContent.result == "正常")
        //     stepLogContent.result = 1;
        // else
        //     stepLogContent.result = 2;
        stepLog['out_msg'] = stepLogContent.out_msg;
        stepLog['ack_msg'] = stepLogContent.ack_msg;
        stepLog['result'] = stepLogContent.result;
        stepLog['etime'] = new Date();

        // console.log('=============TEMP======================');    //测试
        // console.log(iIndex);    //测试
        //console.log(stepLogContent);    //测试
        //console.log(stepLog);    //测试

        var resp = CommonFunc.ajaxPostFormSync(global_Object_Step2.ajaxSave, stepLog);
        if (resp.success) {
            //更新最近一次的明细
            global_Object_Step2.svcExtList[svcCurrentIndex].testCount += 1;
            global_Object_Step2.stepLogList[svcCurrentIndex] = resp.data;
            global_Object_Step2.setBadge(resp.data);
            //变更服务步骤
            global_Object_Step2.changeSvc();
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },
    //变更服务步骤
    changeSvc: function (iIndex, gotoFinish) {
        // console.log('===================================');    //测试
        // console.log(global_Object_Step2.svcExtList);    //测试
        // console.log(svcCurrentIndex);    //测试
        // console.log(iIndex);    //测试

        //设置当前服务的步骤
        if(iIndex == 0)
            $('#svcItem' + 0).addClass('svcTestStepItemActive');
        if (!CommonFunc.isEmpty(iIndex) && iIndex >= 0 && iIndex <= global_Object_Step2.svcExtList.length - 1) {
            svcCurrentIndex = iIndex;
        } else if (svcCurrentIndex < global_Object_Step2.svcExtList.length - 1) {
            svcCurrentIndex = svcCurrentIndex + 1;
            iIndex = svcCurrentIndex;
        } else if (gotoFinish != false) {
            var iIndexTemp = global_Object_Step2.checkFinish();
            //console.log(iIndexTemp);
            if (CommonFunc.isEmpty(iIndexTemp)) {
                CommonFunc.msgSu('测试完成');
                //进入下一步骤
                global_Object.changeStep(global_Object.$step.getIndex(), 2);
                return;
            } else {
                iIndex = iIndexTemp;
            }
        }

        //更新激活的服务项样式
        $(idSvcList + ' li.svcTestStepItemActive').removeClass('svcTestStepItemActive');
        $('#svcItem' + iIndex).addClass('svcTestStepItemActive');

        //设置“发送/接收”的按钮样式
        global_Object_Step2.setButtonPost();

        //还原form（显示数据或清空数据）
        var stepLog = global_Object_Step2.stepLogList[iIndex];
        if (!CommonFunc.isEmpty(stepLog)) {
            //console.log(stepLog);

            CommonFunc.setForm(idStepLogForm, stepLog);
            if(stepLog.result == 1)
                $("#stepLogResult").val("正常");
            else
                $("#stepLogResult").val("失败");
        }
        else
            CommonFunc.clearForm(idStepLogForm);
    },
    //设置徽章
    setBadge: function (data) {
        // console.log(data);    //测试

        var badgeCountOld = $('#svcItem' + svcCurrentIndex + ' span').html();
        var badgeCss = 'badge', badgeContent;
        if (data.result == 1) {
            badgeCss += " badgeSuccess";
            badgeContent = "成功";
        }else if (data.result == 2) {
            badgeCss += " badgeFailure";
            badgeContent = "失败";
        }else {
            badgeCss += " badgeNormal";
            badgeContent = "未知";
        }
        if (CommonFunc.isEmpty(badgeCountOld)) {
            $('#svcItem' + svcCurrentIndex).append('<span class="' + badgeCss + '">' + badgeContent + '</span>');
        } else {
            $('#svcItem' + svcCurrentIndex + ' span').html(badgeContent);
            $('#svcItem' + svcCurrentIndex + ' span').removeClass().addClass(badgeCss);
        }
    },
    //检测服务清单是否测试完成
    checkFinish: function () {
        var iIndex;
        var stepLogList = global_Object_Step2.stepLogList;

        if (stepLogList && stepLogList.length > 0) {
            for (var i = 0, len = stepLogList.length; i < len; i++) {
                //console.log(stepLogList[i]);
                if (CommonFunc.isEmpty(stepLogList[i])) {
                    iIndex = i;
                    $('[name="out_msg"]').focus();
                    break;
                }
            }
        } else {
            iIndex = 0;
        }
        return iIndex;
    },
    //设置“发送/接收”的按钮样式
    setButtonPost: function () {
        var data = global_Object_Step2.svcExtList[svcCurrentIndex];
        if(data.obj.aid == data.svcInfoModel.aid)
            $("#btnPost").html('<i class="fa fa-sign-in prject-color-white" style="margin-right: 4px"></i>发送');
        else
            $("#btnPost").html('<i class="fa fa-sign-out prject-color-white" style="margin-right: 4px"></i>接收');
    },
    //生成输入消息
    createMsgIn: function () {
        // console.log(svcCurrentIndex);   //测试
        // console.log(global_Object_Step2.svcExtList);   //测试
        // console.log(global_Object_Step2.svcExtList[svcCurrentIndex]);   //测试

        var svcInfo = global_Object_Step2.svcExtList[svcCurrentIndex].svcInfoModel;
        var datas = {
            msgType: svcInfo.msgType,
            sid: svcInfo.id,
            direction: 1,
            returnType: 1
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object_Step2.ajaxExport, datas);
        if(resp.success) {
            $('[name="out_msg"]').val(resp.data);
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    }

};