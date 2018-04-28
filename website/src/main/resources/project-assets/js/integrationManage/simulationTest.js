/**
 * Created by xuehao on 2017/08/10.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/integrationManage/simulationTest/";
var ajaxLogReqPre = "/ajax/integrationManage/simulationTestLog/";
var ajaxBusinessReqPre = "/ajax/integrationManage/simulationBusinessTest/";
var ajaxUrlReqPre = "/ajax/baseManage/svcUrl/";


//ID
var formContainerID = '#editFormContainer';
var formID = '#editForm';
var idSvcList = '#svcList';
var idStepSimulationTest = '#stepSimulationTest';
var idUrlEsbID = '#urlEsbID';
var idUrlAppID = '#urlAppID';
var idLogResult = '#logResult';
var idStepLogResult = '#stepLogResult';
var svcType = '#svcType';

var idLoginUserid = '#loginUserid';

//全局变量
// var $step;
// var treeAID = 0;    //树中选择的业务系统ID
var defaultOptions = "<option value=''>请选择服务地址</option><option value='0'>【添加服务地址】</option>";

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    treeAID : 0,
    testID : 0,
    $step : null,
    urlEsb : {},
    urlApp : {},
    ajaxGetBaseInfo: contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxStartEsbService: contextPath + ajaxBusinessReqPre + 'startEsbService',
    ajaxTestAppService: contextPath + ajaxBusinessReqPre + 'testAppService',
    ajaxStartTest: contextPath + ajaxLogReqPre + 'startTest',
    ajaxBindLatestTest: contextPath + ajaxLogReqPre + 'bindLatestTest',
    ajaxTestResult: contextPath + ajaxLogReqPre + "testResult",
    ajaxSave: contextPath + ajaxUrlReqPre + 'save',
    ajaxLinkTest: contextPath + ajaxUrlReqPre + 'linkTest',
    ajaxUrlIdMax: contextPath + ajaxUrlReqPre + 'urlIdMax',
    initDomEvent: function () {
        // console.log('init');  //测试

        global_Object.treeAID = 0;
        global_Object.testID = 0;
        global_Object.$step = null;
        global_Object.urlEsb = {};
        global_Object.urlApp = {};

        //绑定基础信息
        CommonFunc.ajaxPostForm(global_Object.ajaxGetBaseInfo, null, global_Object.bindBaseInfo, CommonFunc.msgEx);

        //开启ESB模拟服务
        $("#startEsbService").on("click", function () {
            global_Object.startEsbService();
        });

        //测试业务系统服务
        $("#testAppService").on("click", function () {
            global_Object.testAppService();
        });

        $("#btnFinishTest").on("click", function () {
            global_Object_Step3.finishTest();
        });

        $("#btnExport").on("click", function () {
            global_Object_Step3.exportReport();

        });

        $("#saveBtn").on("click", function () {
            var vals = CommonFunc.getForm(formID);
            var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxSave, vals);
            global_Object.afterSave(resp);
        });

        $("#linkBtn").on("click", function () {
            var vals = CommonFunc.getForm(formID);
            CommonFunc.ajaxPostForm(global_Object.ajaxLinkTest,
                vals,
                global_Object.linkTest, CommonFunc.msgEx);
        });

        //平台服务地址下拉列表的change事件
        $(idUrlEsbID).on("change", function (me) {
            var val = $(idUrlEsbID).val();
            if(val == '0') {
                //添加服务地址
                var newUrlID = 0;   //测试

                //绑定基础信息
                var data = CommonFunc.ajaxPostFormSync(global_Object.ajaxGetBaseInfo, null);
                if(!data.success) {
                    CommonFunc.msgFa('获取平台服务地址失败！');
                } else {
                    CommonFunc.bindSelect(idUrlEsbID, data.data.urlId, newUrlID);
                    $(idUrlEsbID).prepend(defaultOptions);
                    if($(idUrlEsbID).val() == 0){
                        global_Object.showModal();
                    }
                }
            }
        });

        //业务系统服务地址下拉列表的change事件
        $(idUrlAppID).on("change", function (me) {
            var val = $(idUrlAppID).val();
            if(val == '0') {
                //添加服务地址
                var newUrlID = 0;   //测试

                //绑定基础信息
                var data = CommonFunc.ajaxPostFormSync(global_Object.ajaxGetBaseInfo, null);
                if(!data.success) {
                    CommonFunc.msgFa('获取平台服务地址失败！');
                } else {
                    CommonFunc.bindSelect(idUrlAppID, data.data.urlId, newUrlID);
                    $(idUrlAppID).prepend(defaultOptions);
                    if($(idUrlEsbID).val() == 0){
                        global_Object.showModal();
                    }
                }
            }
        });

        //初始化步骤
        global_Object.initStep();

        //绑定业务系统树
        var params = {
            callback : global_Object.treeChange,
            triggerLevel : 1,
            username: $(idLoginUserid).val()
        };
        CommonTree.init(params);

        //初始步骤二
        global_Object_Step2.initDomEvent();

    },
    bindBaseInfo: function (data) {
        // console.log(data);  //测试

        if(!data.success) {
            CommonFunc.msgFa('绑定基础数据失败！');
        } else {
            CommonFunc.bindSelect(idUrlEsbID, data.data.urlId, null);
            CommonFunc.bindSelect(idUrlAppID, data.data.urlId, null);
            CommonFunc.bindSelect(idLogResult, data.data.logResult, null);
            CommonFunc.bindSelect(svcType, data.data.svcType,null);
            //CommonFunc.bindSelect(idStepLogResult, data.data.stepLogResult, null);

            $(idUrlEsbID).prepend(defaultOptions);
            $(idUrlAppID).prepend(defaultOptions);

            $(idUrlEsbID).get(0).selectedIndex = 0;
            $(idUrlAppID).get(0).selectedIndex = 0;
        }
    },
    treeChange: function (sysID, obj) {
        // console.log(sysID); //测试
        console.log(obj); //测试

        //切换到第一步
        global_Object.changeStep(0);
        //将选择的业务系统ID保存到全局变量中
        global_Object.treeAID = obj.id;

        //获取最近一次的测试
        global_Object.bindLatestTest();
    },
    //绑定最近一次的未完成的测试
    bindLatestTest: function (data) {
        // console.log(data); //测试

        var vals = {
            aid : global_Object.treeAID
        };
        var data = CommonFunc.ajaxPostFormSync(global_Object.ajaxBindLatestTest, vals);
        if(data.success && data.data) {
            var data = data.data;
            global_Object.testID = data.log.id;
            global_Object.urlApp = data.urlApp;
            global_Object.urlEsb = data.urlEsb;
            $(idUrlEsbID).val(data.urlEsb.id);
            $(idUrlAppID).val(data.urlApp.id);
        } else {
            global_Object.testID = 0;
            global_Object.urlApp = {};
            global_Object.urlEsb = {};
            $(idUrlEsbID).get(0).selectedIndex = 0;
            $(idUrlAppID).get(0).selectedIndex = 0;
        }

        //弹窗确认是否进入下一步
        global_Object.confirmToNext();
    },
    //弹窗确认是否进入下一步
    confirmToNext: function () {
        // console.log(data); //测试

        if(!CommonFunc.isEmpty(global_Object.urlApp) && !CommonFunc.isEmpty(global_Object.urlEsb)) {
            global_Object.changeStep(null, 2);
        }
    },
    //初始化步骤控件
    initStep: function () {
        // console.log(data);  //测试

        $(idStepSimulationTest).html('');   //先清空，再生成步骤
        global_Object.$step = $(idStepSimulationTest);
        global_Object.$step.step({
            index: 0,
            time: 500,
            title: ["<span class='shape-link' onclick='global_Object.changeStep(0)'>通信测试</span>",
                "<span class='shape-link' onclick='global_Object.changeStep(1)'>模拟服务测试</span>",
                "<span class='shape-link' onclick='global_Object.changeStep(2)'>测试完成</span>"]
        });
    },
    /**
     * 改变步骤的事件
     * @param stepIndex 目标步骤的索引号
     * @param type      1-上一步，2-下一步
     */
    changeStep:function (stepIndex, type) {
        // console.log('=================');    //测试
        //console.log(stepIndex);    //测试
        //console.log(type);
        //$("textarea[name='out_msg']").val("").focus();
        //$("textarea[name='ack_msg']").val("").focus();
        if(CommonFunc.isEmpty(stepIndex))
            stepIndex = global_Object.$step.getIndex();
        if(CommonFunc.isEmpty(stepIndex))
            stepIndex = 0;
        var vals = {
            aid : global_Object.treeAID
        };
        //console.log(vals);
        var testResult = CommonFunc.ajaxPostFormSync(global_Object.ajaxTestResult,vals);
        //console.log(testResult.data);
        //如果是第二步，则初始化树
        if(stepIndex==0 && type==2 || stepIndex==1 && CommonFunc.isEmpty(type)) {
            //写入测试主日志表
            if(testResult.data != 0) {
                var err = global_Object.startTest();
                if (!CommonFunc.isEmpty(err)) {
                    CommonFunc.msgFa(err);
                    return;
                }
            }

            //初始化待测试的服务清单
            global_Object_Step2.initSvcStep(global_Object.treeAID, global_Object.testID);
        } else if(stepIndex==1 && type==2 || stepIndex==2 && CommonFunc.isEmpty(type)) {
            if(!global_Object_Step3.showResultList()) {
                CommonFunc.msgSu('模拟测试未完成！');
                return;
            }
        }

        //切换步骤
        if(type==1) {
            global_Object.$step.prevStep();
            stepIndex = global_Object.$step.getIndex();
        } else if(type==2) {
            global_Object.$step.nextStep();
            stepIndex = global_Object.$step.getIndex();
        }

        //根据切换的步骤，设置显示的内容
        for(var i=1, len=3; i<=len; i++) {
            $('#step'+i).removeClass("step-enable").addClass("step-diable");
        }
        $('#step'+(stepIndex+1)).removeClass("step-diable").addClass("step-enable");
        global_Object.$step.toStep(stepIndex);
    },
    //开启ESB模拟服务
    startEsbService: function () {
        // console.log(data);  //测试

        var urlId = $(idUrlEsbID).val();
        if(CommonFunc.isEmpty(urlId)) {
            CommonFunc.msgFa('请选择集成平台的服务地址！');
            return;
        }
        var vals = {
            urlId : urlId
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxStartEsbService, vals);
        if(resp.success) {
            CommonFunc.msgSu('服务启动成功');
            global_Object.urlEsb = resp.data;
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }

        //弹窗确认是否进入下一步
        global_Object.confirmToNext();
    },
    //测试业务系统服务
    testAppService: function () {
        // console.log(data);  //测试

        var urlId = $(idUrlAppID).val();
        if(CommonFunc.isEmpty(urlId)) {
            CommonFunc.msgFa('请选择业务系统的服务地址！');
            return;
        }
        var vals = {
            urlId : urlId
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxTestAppService, vals);
        if(resp.success) {
            CommonFunc.msgSu('连接成功');
            global_Object.urlApp = resp.data;
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }

        //弹窗确认是否进入下一步
        global_Object.confirmToNext();
    },
    //开始模拟测试
    startTest: function () {
        // console.log(data);  //测试

        var err = '';
        var urlEsbId = $(idUrlEsbID).val();
        if(CommonFunc.isEmpty(urlEsbId))
            err += '请选择集成平台的服务地址！';
        var urlAppId = $(idUrlAppID).val();
        if(CommonFunc.isEmpty(urlAppId))
            err += '请选择业务系统的服务地址！';
        if(!CommonFunc.isEmpty(err)) {
            return err;
        }

        // //如果测试ID已经存在，则无需在新增
        // if(!CommonFunc.isEmpty(global_Object.testID) && global_Object.testID!=0) {
        //     return err;
        // }

        var vals = {
            aid : global_Object.treeAID,
            urlEsbID : urlEsbId,
            urlAppID : urlAppId,
            btime : new Date()
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxStartTest, vals);
        if(resp.success) {
            global_Object.testID = resp.data;
        } else {
            err = resp.errorMsg;
        }
        return err;
    },
    showModal: function () {
        CommonFunc.clearForm(formID);
        $(formContainerID).modal("show");
        global_Object.showTips();
    },
    afterSave: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            $(formContainerID).modal("hide");
            var data = CommonFunc.ajaxPostFormSync(global_Object.ajaxGetBaseInfo, null);
            var urlData = CommonFunc.ajaxPostFormSync(global_Object.ajaxUrlIdMax,null);
            console.log(urlData);
            var newUrlID = urlData.data;
            CommonFunc.bindSelect(idUrlEsbID, data.data.urlId, newUrlID);
            //$(idUrlEsbID).val();
            //CommonFunc.clearForm(formID);
              //刷新列表
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
    showTips: function () {
        var svcTypeVal = $("#svcType").val();
        var tips = "";

        if (svcTypeVal == 0)
            tips = "Web服务地址示例：http://127.0.0.1:9180/services/test?wsdl";
        else if (svcTypeVal == 1)
            tips = "Restful地址示例：http://127.0.0.1:9180/user/login";
        else if (svcTypeVal == 2)
            tips = "Socket地址示例：127.0.0.1:9180";

        $("#urlTips").text(tips);
    },

};