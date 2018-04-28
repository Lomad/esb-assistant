/**
 * Created by xuehao on 2017/09/25.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/integrationManage/svcFlowTest/";
var ajaxReqPreUrl = "/ajax/baseManage/svcUrl/";
var ajaxStructureReqPre = "/ajax/serviceManage/svcStructure/";
var ajaxTestReqPre = "/ajax/integrationManage/simulationTestLog/";
var ajaxTestStepReqPre = "/ajax/integrationManage/simulationTestStepLog/";
var ajaxBizReqPre = "/ajax/integrationManage/simulationBusinessTest/";

//ID
var idBtnShowLogList = '#btnShowLogList';
var idLoginUserid = '#loginUserid';
var idWrapperId1 = '#wrapperId1';
var idWrapperId2 = '#wrapperId2';
var idWrapperId3 = '#wrapperId3';
var idWrapperId4 = '#wrapperId4';
var idFlowTree = "#flowTree";
var flowTree;
var idAppTree = '#appTree';
var idFlowSvcTree = "#flowSvcTree";
var flowSvcTree;
var formContainerID = "#flowFormContainer";
var formID = "#flowForm";
var flowSvcFormContainerID = "#flowSvcFormContainer";
var flowSvcFormID = "#flowSvcForm";
var idLogForm = '#logForm';
var idMsgReqTips = '#msgReqTips';
var idMsgAckTips = '#msgAckTips';

var idFlowImgTitle = '#flowImgTitle';
var idFlowImgInfo = '#flowImgInfo';
var idTestStepImgConsumer = '#testStepImgConsumer';
var idTestStepImgProvider = '#testStepImgProvider';
var idMsgTypeReq = '#msgTypeReq';
var idMsgTypeAckTransfer = '#msgTypeAckTransfer';
var idMsgTypeReqTransfer = '#msgTypeReqTransfer';
var idMsgTypeAck = '#msgTypeAck';

var viewLogFormContainer = '#viewLogFormContainer';
var viewLogForm = '#viewLogForm';
var testLogFormContainer = '#testLogFormContainer';
var testLogForm = '#testLogForm';

var tableID = '#listTable';

//全局变量

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    //测试概要信息对象（包含：flow - 测试场景节点，svc - 当前测试的服务节点, index - 当前步骤的索引号, isFinish - 是否完成）
    testOverview: {},
    //服务测试地址对象（包含：url，urlStatus，urlType，urlAgent，urlAgentStatus,）
    testUrl: {},

    //消息状态：等待中
    MSG_STATUS_WAITING: 0,
    //消息状态：已完成（发送或接收）
    MSG_STATUS_OK: 1,

    //流程树的步骤节点样式：服务图标
    NODE_ICONSKIN_SVC: 'svc',
    //流程树的步骤节点样式：等待图标
    NODE_ICONSKIN_WAIT: 'wait',

    //定时接收函数的ID
    receiveIntervalID: null,

    ajaxGetTree: contextPath + ajaxReqPre + 'getTree',
    ajaxSaveFlow: contextPath + ajaxReqPre + 'saveFlow',
    ajaxDeleteFlow: contextPath + ajaxReqPre + 'deleteFlow',
    ajaxSaveFlowSvc: contextPath + ajaxReqPre + 'saveFlowSvc',
    ajaxQuerySidByFidAid: contextPath + ajaxReqPre + 'querySidByFidAid',
    ajaxUpdateFlowSvcOrder: contextPath + ajaxReqPre + 'updateFlowSvcOrder',
    ajaxDeleteFlowSvc: contextPath + ajaxReqPre + 'deleteFlowSvc',
    ajaxGetLatestSidByTID: contextPath + ajaxTestStepReqPre + 'getLatestSidByTID',
    ajaxQueryTestStepLog: contextPath + ajaxTestStepReqPre + 'query',
    ajaxGetStepLogByTID: contextPath + ajaxTestStepReqPre + 'getByTID',
    ajaxCreateTestLog: contextPath + ajaxTestReqPre + 'createTestLog',
    ajaxFinishTestLog: contextPath + ajaxTestReqPre + 'finishTestLog',
    ajaxExportMsg: contextPath + ajaxStructureReqPre + 'export',
    ajaxSend: contextPath + ajaxBizReqPre + 'send',
    ajaxReceiveStart: contextPath + ajaxReqPre + "receiveStart",
    ajaxReceiveStop: contextPath + ajaxReqPre + "receiveStop",
    ajaxReceive: contextPath + ajaxReqPre + "receive",
    ajaxGetUrlInfo: contextPath + ajaxReqPreUrl + 'getUrlInfo',
    ajaxCheckUrlStatus: contextPath + ajaxReqPreUrl + 'checkUrlStatus',
    ajaxDownLogPdf: contextPath + ajaxReqPre + 'downLogPdf',
    //初始化
    initDomEvent: function () {
        //初始化树
        global_Object.initFlowTree();

        $("#btnCreateFlow").on("click", function () {
            global_Object.showFlowModal();
        });

        $("#btnBackMainBoard").on("click", function () {
            global_Object.showMainBoard(3);
        });

        $(idBtnShowLogList).on("click", function () {
            global_Object.showMainBoard(4);
        });

        $('#btnQuerySvc').on('click', function () {
            CommonFunc.prompt('请输入服务代码或名称查询', global_Object.bindFlowSvcTree);
        });

        $('#btnRereshSvc').on('click', function () {
            global_Object.bindFlowSvcTree();
        });

        $('#btnStartTest').on('click', function () {
            global_Object.createTestLog();
        });

        $('#btnTestLogDesp').on('click', function () {
            global_Object.showTestLogModal();
        });

        $('#btnFinishTestLog').on('click', function () {
            global_Object.finishTestLog();
        });

        $('#btnDownLogPdf').on('click', function () {
            global_Object.downLogPdf();
        });

        //设置面板高度
        global_Object.showMainBoard(3);
        //设置滚动条
        CommonFunc.setScrollBarWithWrapper();
    },
    /**
     * 初始化树
     */
    initFlowTree: function (data) {
        //绑定树
        var req = {
            datas: JSON.stringify({
                username: $(idLoginUserid).val()
            })
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxGetTree, req, global_Object.bindFlowTree);
    },
    /**
     * 绑定流程树
     */
    bindFlowTree: function (data) {
        if (!data.success) {
            CommonFunc.msgEx('获取测试场景失败！' + data.errorMsg);
            return;
        }
        var datas = data.datas;
        var setting = {
            view: {
                showIcon: true,
                showLine: false,
                selectedMulti: false,
                nameIsHTML: true,
                addHoverDom: global_Object.addHoverDom,
                removeHoverDom: global_Object.removeHoverDom
            },
            edit: {
                enable: true,
                showRenameBtn: false,
                showRemoveBtn: false,
                drag: {
                    isCopy: false,
                    inner: false
                }
            },
            callback: {
                beforeDrop: global_Object.beforeDrop,
                onDrop: global_Object.onDrop,
                onClick: global_Object.flowTreeClick,
                beforeClick: global_Object.flowTreeBeforeClick
            }
        };

        //绑定树以及change事件
        flowTree = $.fn.zTree.init($(idFlowTree), setting, datas);
        //设置默认选择节点
        global_Object.setFlowTreeCookieNode();
    },
    //显示按钮，并定义相应按钮的功能
    addHoverDom: function (treeId, treeNode) {
        // console.log(treeNode);  //测试

        var sObj = $("#" + treeNode.tId + "_span");

        //流程名称节点加上操作按钮
        if (treeNode.level == 0) {
            if (treeNode.editNameFlag || $("#editBtn_" + treeNode.tId).length > 0)
                return;
            var addStr = '';
            addStr += "<span class='fa fa-plus-circle' id='addBtn_" + treeNode.tId + "' title='新增步骤' onfocus='this.blur();'></span>";
            addStr += "<span class='fa fa-edit' id='editBtn_" + treeNode.tId + "' title='修改场景' onfocus='this.blur();'></span>";
            addStr += "<span class='fa fa-trash' id='deleteBtn_" + treeNode.tId + "' title='删除场景' onfocus='this.blur();'></span>";
            sObj.after(addStr);
            //新增子节点按钮
            btn = $("#addBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                global_Object.showFlowSvcModal();
                CommonFunc.setForm(flowSvcFormID, {fid: treeNode.myData.obj.id});
                return false;
            });
            //修改按钮
            btn = $("#editBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                // console.log(treeNode);  //测试

                global_Object.showFlowModal();
                CommonFunc.setForm(formID, treeNode.myData.obj);

                return false;
            });
            //删除按钮
            btn = $("#deleteBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                CommonFunc.confirmAsync('您确定删除吗（将删除该场景涉及的所有步骤、日志等）？', function () {
                    CommonFunc.ajaxPostForm(global_Object.ajaxDeleteFlow, {id: treeNode.myData.obj.id}, global_Object.afterSaveFlow);
                });

                return false;
            });
        } else if (treeNode.level == 1) {
            if (treeNode.editNameFlag || $("#deleteFlowSvcBtn_" + treeNode.tId).length > 0)
                return;
            var addStr = '';
            addStr += "<span class='fa fa-refresh' id='refreshFlowSvcBtn_" + treeNode.tId + "' title='从该步骤开始，重新测试' onfocus='this.blur();'></span>";
            addStr += "<span class='fa fa-file-text-o' id='viewLogBtn_" + treeNode.tId + "' title='查看日志' onfocus='this.blur();'></span>";
            addStr += "<span class='fa fa-trash' id='deleteFlowSvcBtn_" + treeNode.tId + "' title='删除步骤' onfocus='this.blur();'></span>";
            sObj.after(addStr);
            //重新测试
            btn = $("#refreshFlowSvcBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                CommonFunc.confirmAsync('您确定从该步骤开始，重新测试吗？', function () {
                    var mainLog = treeNode.getParentNode().myData.log;
                    var log = treeNode.myData.log;
                    var preNode = treeNode.getPreNode();
                    var preNodeLog = CommonFunc.isEmpty(preNode) ? null : preNode.myData.log;
                    if (!CommonFunc.isEmpty(mainLog) && !CommonFunc.isEmpty(mainLog.etime)) {
                        CommonFunc.msgFa('测试尚未开启！');
                    } else if (!CommonFunc.isEmpty(log) || !CommonFunc.isEmpty(preNodeLog)) {
                        // global_Object.svcTreeNodeClick(treeNode);

                        //重设全局变量
                        global_Object.testOverview.flow = treeNode.getParentNode();
                        global_Object.testOverview.svc = treeNode;

                        //设置当前测试的等待图标
                        global_Object.turnToNextStep(treeNode.myData.svc.id, false);

                        //清空请求与应答
                        global_Object.clearLogForm();
                        // //开启接收测试消息
                        // global_Object.receiveStart();
                    } else {
                        CommonFunc.msgFa('该步骤尚未开始，无需重新测试！');
                    }
                });
                return false;
            });
            //查看日志
            btn = $("#viewLogBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                var log = treeNode.myData.log;
                //如果本地日志为空，则请求数据库
                if (CommonFunc.isEmpty(log) || (CommonFunc.isEmpty(log.out_msg) && CommonFunc.isEmpty(log.ack_msg))) {
                    var treeNodeFlow = treeNode.getParentNode();
                    var objLog = treeNodeFlow.myData.log;
                    var objFlowSvc = treeNode.myData.obj;
                    var req = {
                        datas: JSON.stringify({
                            tid: objLog.id,
                            sid: objFlowSvc.sid,
                            pageSize: 1
                        })
                    };
                    CommonFunc.ajaxPostForm(global_Object.ajaxQueryTestStepLog, req, function (resp) {
                        // console.log(resp);  //测试

                        if (resp.success && resp.datas.length > 0) {
                            log = resp.datas[0];
                            treeNode.myData.log = log;
                            global_Object.showStepLogModal(log);
                        } else if (resp.datas.length < 1) {
                            CommonFunc.msgFa('暂无日志！');
                        } else {
                            CommonFunc.msgFa(resp.errorMsg);
                        }
                    });
                } else {
                    if (CommonFunc.isEmpty(log.out_msg) && CommonFunc.isEmpty(log.ack_msg)) {
                        CommonFunc.msgFa('暂无日志！');
                    } else {
                        CommonFunc.setForm(viewLogForm, log);
                        $(viewLogFormContainer).modal('show');
                    }
                }

                return false;
            });
            //删除按钮
            btn = $("#deleteFlowSvcBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                CommonFunc.confirmAsync('您确定删除该步骤吗？', function () {
                    CommonFunc.ajaxPostForm(global_Object.ajaxDeleteFlowSvc, {
                        id: treeNode.myData.obj.id
                    }, global_Object.afterSaveFlowSvc);
                });
                return false;
            });
        }
    },
    //移除按钮
    removeHoverDom: function (treeId, treeNode) {
        if (treeNode.level == 0) {
            $("#addBtn_" + treeNode.tId).unbind().remove();
            $("#editBtn_" + treeNode.tId).unbind().remove();
            $("#deleteBtn_" + treeNode.tId).unbind().remove();
        } else if (treeNode.level == 1) {
            $("#refreshFlowSvcBtn_" + treeNode.tId).unbind().remove();
            $("#viewLogBtn_" + treeNode.tId).unbind().remove();
            $("#deleteFlowSvcBtn_" + treeNode.tId).unbind().remove();
        }
    },
    //拖拽事件
    beforeDrop: function (treeId, treeNodes, targetNode, moveType, isCopy) {
        // console.log(treeId); //测试

        var result;
        var errInfo = "";
        if (treeNodes && treeNodes.length == 1) {
            var treeNode = treeNodes[0];
            var pid = targetNode.myData.pid;
            if (targetNode.level == 0) {
                errInfo = "禁止该类操作！";
            }
        }
        if (!CommonFunc.isEmpty(errInfo)) {
            result = false;
            CommonFunc.msgFa(errInfo);
        } else {
            result = true;
        }

        return result;
    },
    //拖拽事件
    onDrop: function (event, treeId, treeNodes, targetNode, moveType, isCopy) {
        // console.log(treeId); //测试
        // console.log(treeNodes); //测试
        // console.log(targetNode); //测试
        // console.log(moveType); //测试

        var treeNode = treeNodes[0];
        var siblingsNodes = targetNode.getParentNode().children;  //用于获取同级节点
        var dataArray = [];
        if (siblingsNodes != null) {
            for (var i = 0; i < siblingsNodes.length; i++) {
                dataArray.push({
                    id: siblingsNodes[i].myData.obj.id,
                    order_num: i + 1
                });
            }
            var vals = {
                objs: JSON.stringify(dataArray)
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxUpdateFlowSvcOrder, vals, global_Object.afterSaveFlowSvc);
        }
    },
    flowTreeBeforeClick: function (treeId, treeNode) {
        return treeNode.level == 0;
    },
    //设置流程树的默认节点
    setFlowTreeCookieNode: function () {
        // console.log(flowTree); //测试

        //获取根节点列表
        var nodes = flowTree.getNodes();
        var node;
        if (!CommonFunc.isEmpty(nodes)) {
            var cookieFind = false;
            //获取cookie
            var cookieValue = CommonFunc.getCookie(idFlowTree + '_' + CommonFunc.getURL());
            //根据Cookie获取待选择的节点，如果cookie为空，则获取第一个节点
            if (!CommonFunc.isEmpty(cookieValue)) {
                //将树形节点转换为列表
                var nodesArray = flowTree.transformToArray(nodes);
                if (nodesArray && nodesArray.length > 0) {
                    for (var i = 0, len = nodesArray.length; i < len; i++) {
                        node = nodesArray[i];
                        if (node.level.toString() == "0" && node.myData.obj.id.toString() == cookieValue) {
                            cookieFind = true;
                            flowTree.selectNode(node);
                            global_Object.flowTreeClick(null, idFlowTree, node);
                        }
                    }
                }
            }
            //如果未找到cookie节点，则选择第一个节点
            if (!cookieFind) {
                node = nodes[0];
                flowTree.selectNode(node);
                global_Object.flowTreeClick(null, idFlowTree, node);
            }
        }
    },
    flowTreeClick: function (event, treeId, treeNode, clickFlag) {
        // console.log(treeNode); //测试

        //场景节点
        if (treeNode.level == 0) {
            global_Object.flowTreeNodeClick(treeNode);
        }
        //步骤节点
        else if (treeNode.level == 1) {
            global_Object.svcTreeNodeClick(treeNode);
        }
    },
    /**
     * 场景节点单击事件
     */
    flowTreeNodeClick: function (treeNode) {
        // console.log(treeNode); //测试

        //记录cookie
        CommonFunc.setCookie(idFlowTree + '_' + CommonFunc.getURL(), treeNode.myData.obj.id, 1);

        //保存场景节点
        global_Object.testOverview.flow = treeNode;

        //折叠所有节点
        flowTree.expandAll(false);
        //展开所选节点
        flowTree.expandNode(treeNode, true);

        //获取场景对象和日志对象，判断是否已经开启测试
        var flowObj = treeNode.myData.obj;
        var logModel = treeNode.myData.log;
        var logHasStarted = false;
        var resultDesp = '', flowName = flowObj.name;
        $(idBtnShowLogList).addClass("hidden");
        if (!CommonFunc.isEmpty(logModel)) {
            if (!CommonFunc.isEmpty(logModel.etime)) {
                resultDesp = '【' + flowName + '】最近一次测试已完成，是否开启新的测试？';
                $(idBtnShowLogList).removeClass("hidden");
            } else {
                logHasStarted = true;
            }
        } else {
            resultDesp = '【' + flowName + '】测试尚未开启，是否开启新的测试？';
        }
        //设置主面板等信息
        if (logHasStarted) {
            global_Object.showMainBoard(2);
        } else {
            $('#testStatusDesp').html(resultDesp);
            global_Object.showMainBoard(3);
        }

        //获取上一次测试的最后一步服务ID
        if (!CommonFunc.isEmpty(logModel)) {
            global_Object.getLatestSidByTID(logModel.id);
        }
    },
    /**
     * 服务步骤节点单击事件
     */
    svcTreeNodeClick: function (treeNode) {
        // console.log(treeNode); //测试

        //获取服务对象
        var svcObj = treeNode.myData.svc;
        //获取服务地址信息
        CommonFunc.ajaxPostForm(global_Object.ajaxGetUrlInfo, {
            urlAgentId: svcObj.urlAgentId,
            urlId: svcObj.urlId,
            url: svcObj.url
        }, global_Object.getUrlInfo);
    },
    //获取服务地址信息
    getUrlInfo: function (resp) {
        // console.log(resp); //测试

        //设置地址信息
        if (resp.success) {
            var data = resp.data;
            //保存地址对象
            global_Object.testUrl = data;
        } else {
            //重置地址与状态
            global_Object.testUrl = {};
        }
        //显示测试服务、地址信息
        global_Object.showCurrentTestOverview();
    },

    /**
     * 设置显示的主面板
     * @param type  2 - 测试面板， 3 - 开启测试面试， 4 - 测试结果面板
     */
    showMainBoard: function (type) {
        // console.log(type);  //测试

        //删除日志文本框已有的高度限制，避免对影响主面板高度
        if (type == 2) {
            var $outMsg = $(idLogForm).find('[name="out_msg"]');
            var $ackMsg = $(idLogForm).find('[name="ack_msg"]');
            $outMsg.height(10);
            $ackMsg.height(10);
        }

        var idWrapperId = '#wrapperId';
        for (var i = 2; i <= 4; i++) {
            if (type == i) {
                $(idWrapperId + i).removeClass('hidden');
            } else {
                $(idWrapperId + i).addClass('hidden');
            }
        }
        CommonFunc.maxHeightToFrame(idWrapperId1, idWrapperId + type);

        //获取测试日志明细
        if (type == 4) {
            global_Object.bindStepLogTable();
        }

        //调整日志文本框高度
        if (type == 2) {
            global_Object.setParamTextHeight();
        }
    },

    //创建测试主日志
    createTestLog: function () {
        var treeNodeFlow = global_Object.testOverview.flow;
        if (!CommonFunc.isEmpty(treeNodeFlow) && treeNodeFlow.children && treeNodeFlow.children.length > 0) {
            var obj = treeNodeFlow.myData.obj;
            CommonFunc.ajaxPostForm(global_Object.ajaxCreateTestLog, {
                fid: obj.id
            }, function (resp) {
                if (resp.success) {
                    //保存日志模型
                    treeNodeFlow.myData.log = resp.data;
                    //显示测试面板
                    global_Object.showMainBoard(2);
                    //选中步骤（即待测的服务）
                    global_Object.testOverview.index = 0;
                    global_Object.turnToNextStep(0);
                    // //开启接收测试消息
                    // global_Object.receiveStart();
                } else {
                    CommonFunc.msgFa(resp.errorMsg);
                }
            });
        } else {
            CommonFunc.msgFa('该场景暂无步骤，请增加测试步骤再开启测试！');
        }
    },

    /**
     * 获取上一次测试的最后一步服务ID
     * @param logId 主日志ID
     */
    getLatestSidByTID: function (logId) {
        // console.log(logId); //测试

        CommonFunc.ajaxPostForm(global_Object.ajaxGetLatestSidByTID, {
            tid: logId
        }, function (resp) {
            if (resp.success) {
                // console.log(resp);  //测试

                //最近一次的测试服务ID
                var latestSid = resp.data;
                if (CommonFunc.isEmpty(latestSid)) {
                    latestSid = 0;
                }
                global_Object.turnToNextStep(latestSid);

                // //开启接收测试消息
                // global_Object.receiveStart();
            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }
        });
    },

    //显示当前步骤的服务、地址信息
    showCurrentTestOverview: function () {
        // console.log(global_Object.testOverview);    //测试

        var svcNodeMyData = global_Object.testOverview.svc.myData;
        //设置测试流程图的标题
        var svc = svcNodeMyData.svc;
        var step = global_Object.testOverview.index + 1;
        $(idFlowImgTitle).html('集成测试第' + step + '步：' + svc.name);

        //设置测试流程图中的系统名称
        var consumerApp = svcNodeMyData.consumer;
        var consumerName = consumerApp.appName;
        var providerApp = svcNodeMyData.provider;
        var providerName = providerApp.appName;
        $(idTestStepImgProvider).html("<b>提供方</b><br><span class='msg-tips'>" + providerName + "</span>");
        $(idTestStepImgConsumer).html("<b>消费方</b><br><span class='msg-tips'>" + consumerName + "</span>");
        $(idMsgTypeReq).html(svc.msgType);
        $(idMsgTypeReqTransfer).html(svc.msgType);
        $(idMsgTypeAck).html(svc.msgType);
        $(idMsgTypeAckTransfer).html(svc.msgType);

        //设置测试流程图的描述信息
        var testUrl = global_Object.testUrl;
        var tips = '<div class="row">';
        tips += '<div class="col-md-5 col-md-offset-1">';
        tips += 'ESB通信方式：' + testUrl.urlAgentType;
        tips += '，ESB通信地址：' + testUrl.urlAgent;
        tips += '</div>';
        tips += '<div class="col-md-6">';
        tips += providerName + '通信方式：' + testUrl.urlType;
        tips += '，' + providerName + '通信地址：' + testUrl.url;
        tips += '</div>';
        tips += '</div>';
        $(idFlowImgInfo).html(tips);

        //调整日志文本框高度（由于通信地址等内容会影响整体高度，因此需要调用下句再次调整文本框高度）
        global_Object.setParamTextHeight();
    },

    //开启接收消息模式，并定时轮询后台
    receiveStart: function () {
        // console.log(global_Object.testOverview);    //测试

        if (!global_Object.testOverview.isFinish) {
            var logObj = global_Object.testOverview.flow.myData.log;
            var svcObj = global_Object.testOverview.svc.myData.svc;
            var vals = {
                datas: JSON.stringify({
                    tid: logObj.id,
                    svc: svcObj
                })
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxReceiveStart, vals, function () {
                //如果定时轮询接收未启动，则启动定时轮询
                if (CommonFunc.isEmpty(global_Object.receiveIntervalID)) {
                    global_Object.receiveIntervalID = window.setInterval(function () {
                        CommonFunc.ajaxPostForm(global_Object.ajaxReceive, null, global_Object.receive);
                    }, 5000);
                }
            });
        }
    },
    //接收后台消息
    receive: function (resp) {
        //判断处理结果
        if (resp.success) {
            var data = resp.data;
            if (!CommonFunc.isEmpty(data)) {
                //显示消息
                CommonFunc.setForm(idLogForm, data);
                //请求消息
                if (!CommonFunc.isEmpty(data.out_msg)) {
                    global_Object.fillLogStatusTips(idMsgReqTips, BizStable.MsgDirection.req, global_Object.MSG_STATUS_OK);
                }
                //应答消息
                if (!CommonFunc.isEmpty(data.ack_msg)) {
                    global_Object.fillLogStatusTips(idMsgAckTips, BizStable.MsgDirection.ack, global_Object.MSG_STATUS_OK);
                }
                //如果应答消息不为空，则转到下一步骤
                if (!CommonFunc.isEmpty(data.ack_msg)) {
                    global_Object.turnToNextStep(null, null, true);
                    // //开启接收测试消息
                    // global_Object.receiveStart();
                }

                //将日志信息保存到服务步骤
                var svcObjMyData = global_Object.testOverview.svc.myData;
                svcObjMyData.log = data;
            }
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },

    /**
     * 转到下一步骤
     * @param sid   服务ID（如果不为空，则根据该ID定位步骤，如果为0，表示还没开始测试，默认选择第一步）
     * @param isSelectCurrent   true - 选择当前sid对应的节点（默认）， false - 选择当前sid对应节点的下一个节点
     * @param showWait   true - 显示跳转等待， false - 不显示跳转等待
     */
    turnToNextStep: function (sid, isSelectCurrent, showWait) {
        //console.log(sid);   //测试
        // console.log(global_Object.testOverview);   //测试

        //如果为空，则默认设为true
        if (CommonFunc.isEmpty(isSelectCurrent)) {
            isSelectCurrent = true;
        }

        //获取当前场景节点
        var treeNodeFlow = global_Object.testOverview.flow;
        //选择当前场景
        flowTree.selectNode(treeNodeFlow);
        //选中下一个步骤（待测的服务）
        var svcNodes = treeNodeFlow.children;
        var nextStepIndex;
        var svcNode;
        //sid为空，则根据“global_Object.testOverview.index”定位步骤
        if (CommonFunc.isEmpty(sid) || sid == 0) {
            nextStepIndex = (CommonFunc.isEmpty(global_Object.testOverview.index) || sid == 0) ?
                0 : (global_Object.testOverview.index + 1);
            //console.log(nextStepIndex);
        }
        //sid不为空，则根据该sid定位步骤
        else {
            for (var i = 0, len = svcNodes.length; i < len; i++) {
                svcNode = svcNodes[i];
                if (svcNode.myData.svc.id == sid) {
                    nextStepIndex = isSelectCurrent ? i + 1 : i;
                    //console.log(nextStepIndex);
                    break;
                }
            }
        }
        //转到下一步骤
        if (nextStepIndex < svcNodes.length) {
            //设置是否结束的变量
            global_Object.testOverview.isFinish = false;
            //保存当前测试的服务与索引号
            global_Object.testOverview.svc = svcNodes[nextStepIndex];
            global_Object.testOverview.index = nextStepIndex;

            if(showWait == true) {
                //倒计时弹窗
                var maxWaitSeconds = 3;
                var msgInfo = '该步骤已完成，将在' + maxWaitSeconds + '秒后自动跳转到下一步！';
                var resultLayerLoading = CommonFunc.msgLoading(msgInfo, maxWaitSeconds, function () {
                    //关闭等待弹窗中的倒计时
                    window.clearInterval(resultLayerLoading.layerLoadingIntervalID);
                    //关闭等待弹窗
                    CommonFunc.msgLoadingClose(resultLayerLoading.layerLoadingIndex);

                    //进入下一步骤
                    global_Object.goToNextStep();
                });
            } else {
                //进入下一步骤
                global_Object.goToNextStep();
            }
        }
        //测试结束
        else {
            global_Object.testOverview.isFinish = true;
            //关闭倒计时
            window.clearInterval(global_Object.receiveIntervalID);
            global_Object.receiveIntervalID = null;

            var logModel = global_Object.testOverview.flow.myData.log;
            if (!CommonFunc.isEmpty(logModel.etime)) {
                global_Object.showMainBoard(3);
            } else {
                global_Object.showTestLogModal();
                global_Object.showMainBoard(4);
            }
        }
    },
    /**
     * 进入下一步骤
     */
    goToNextStep: function () {
        //触发节点的单击事件，达到“切换到接收消息视图”的效果
        global_Object.svcTreeNodeClick(global_Object.testOverview.svc);
        //清空请求与应答
        global_Object.clearLogForm();
        //设置当前测试的等待图标
        global_Object.setCurrentStepImg();

        //开启接收测试消息
        global_Object.receiveStart();
    },

    /**
     * 设置当前测试步骤的等待图标
     */
    setCurrentStepImg: function () {
        // console.log(global_Object.testOverview);    //测试

        //获取当前场景节点
        var treeNodeFlow = global_Object.testOverview.flow;
        var svcNodes = treeNodeFlow.children;
        //设置当前测试的等待图标
        var svcNode, iconSkin, currentIndex = global_Object.testOverview.index;
        for (var i = 0, len = svcNodes.length; i < len; i++) {
            svcNode = svcNodes[i];
            iconSkin = svcNode.iconSkin;
            if (i == currentIndex) {
                if (iconSkin == global_Object.NODE_ICONSKIN_SVC) {
                    svcNode.iconSkin = global_Object.NODE_ICONSKIN_WAIT;
                    flowTree.updateNode(svcNode);
                }
            } else {
                if (iconSkin == global_Object.NODE_ICONSKIN_WAIT) {
                    svcNode.iconSkin = global_Object.NODE_ICONSKIN_SVC;
                    flowTree.updateNode(svcNode);
                }
            }
        }
    },

    /**
     * 设置日志的状态提示信息
     * @param targetID  提示信息的容器ID
     * @param direction 消息方向
     * @param status    消息状态
     */
    fillLogStatusTips: function (targetID, direction, status) {

        //获取消费方和提供方的名称
        var svcNodeMyData = global_Object.testOverview.svc.myData;
        var consumerApp = svcNodeMyData.consumer;
        var consumerName = consumerApp.appName;
        var providerApp = svcNodeMyData.provider;
        var providerName = providerApp.appName;

        //方向
        var directionName;
        if (direction == BizStable.MsgDirection.req) {
            directionName = '【' + consumerName + '】发送';
        } else {
            directionName = '【' + providerName + '】应答';
        }
        //状态
        var tips;
        if (status == global_Object.MSG_STATUS_OK) {
            tips = directionName + '已完成';
        } else {
            tips = '正在等待' + directionName + ' <i class="fa fa-spinner fa-pulse"></i>';
        }
        $(targetID).html(tips);
    },

    /**
     * 绑定明细日志列表
     */
    bindStepLogTable: function () {
        var svcOverview = global_Object.getSvcOverview();
        var columns = [
            {title: '开始时间', data: 'btime', width: "18%"},
            {title: '结束时间', data: 'etime', width: "16%"},
            {title: '耗时(ms)', data: 'time_len', width: "8%"},
            {title: '服务名称', data: 'svcName', width: "20%"},
            {title: '提供方', data: 'provider', width: "12%"},
            {title: '消费方', data: 'consumer', width: "12%"},
            {title: '结果', data: 'result', width: "6%"},
            {title: '详细', data: null, className: 'optColumn'}
        ];
        var columnDefs = [{
            targets: 3,
            render: function (data, type, row, meta) {
                return svcOverview[row.sid].svcName;
            }
        }, {
            targets: 4,
            render: function (data, type, row, meta) {
                return svcOverview[row.sid].provider;
            }
        }, {
            targets: 5,
            render: function (data, type, row, meta) {
                return svcOverview[row.sid].consumer;
            }
        }, {
            targets: 6,
            render: function (data, type, row, meta) {
                var html;
                if (data == 1) {
                    html = '<span class="prject-color-success"><i class="fa s-icon fa-check-circle"></i> 成功</span>';
                } else if (data == 2) {
                    html = '<span class="prject-color-failure"><i class="fa s-icon fa-times-circle"></i> 失败</span>';
                } else {
                    html = '<span class="prject-color-normal"><i class="fa s-icon fa-question-circle"></i> 未知</span>';
                }
                return html;
            }
        }, {
            targets: 7,
            width: "75px",
            render: function (data, type, row, meta) {
                var html = '  <span onclick="global_Object.viewStepLog(' + meta.row
                    + ')" class="tableBtnImg" title="查看"><i class="fa fa-file-text-o fa-lg cp"></i></span>';
                return html;
            }
        }];
        var myAttrs = {
            paging: false,
            info: false
        };
        CommonTable.createTableAdvanced(tableID, function (data, callback, settings) {
            var logObj = global_Object.testOverview.flow.myData.log;
            var reqData = {
                tid: logObj.id
            };
            //ajax请求数据
            CommonFunc.ajaxPostForm(global_Object.ajaxGetStepLogByTID, reqData, function (respData) {
                // console.log(respData);  //测试

                var datas = [];
                if (!respData.success) {
                    CommonFunc.msgSu(respData.errorMsg);
                } else {
                    if (respData.datas && respData.datas.length > 0) {
                        datas = respData.datas
                    }
                }

                //绑定数据到列表
                callback({
                    recordsTotal: datas.totalSize,//过滤之前的总数据量
                    recordsFiltered: datas.totalSize,//过滤之后的总数据量
                    data: datas
                });

                //调整高度
                CommonFunc.maxHeightToFrame(idWrapperId1, idWrapperId4);
            });
        }, columns, columnDefs, myAttrs);
    },
    viewStepLog: function (rowIndex) {
        var rowData = $(tableID).dataTable().fnGetData()[rowIndex];
        global_Object.showStepLogModal(rowData);
    },
    //显示步骤明细日志的弹窗
    showStepLogModal: function (log) {
        CommonFunc.clearForm(viewLogForm);
        CommonFunc.setForm(viewLogForm, log);
        $(viewLogFormContainer).modal('show');
    },

    /**
     * 获取场景中的所有服务名称、提供方、消费方信息的对象【key - 服务ID， value - 服务概要对象】
     */
    getSvcOverview: function () {
        var result = {};
        var svcNodeList = global_Object.testOverview.flow.children;
        if (!CommonFunc.isEmpty(svcNodeList) && svcNodeList.length > 0) {
            var svcNode, svcMyData, svc, consumer, provider;
            for (var i = 0, len = svcNodeList.length; i < len; i++) {
                svcNode = svcNodeList[i];
                svcMyData = svcNode.myData;
                svc = svcMyData.svc;
                provider = svcMyData.provider;
                consumer = svcMyData.consumer;

                result[svc.id] = {
                    svcName: svc.name,
                    provider: provider.appName,
                    consumer: consumer.appName
                };
            }
        }
        return result;
    },

    //清空日志Form
    clearLogForm: function () {
        //清空请求与应答
        CommonFunc.clearForm(idLogForm);
        //重置状态提示信息
        global_Object.fillLogStatusTips(idMsgReqTips, BizStable.MsgDirection.req, global_Object.MSG_STATUS_WAITING);
        global_Object.fillLogStatusTips(idMsgAckTips, BizStable.MsgDirection.ack, global_Object.MSG_STATUS_WAITING);
    },

    //设置输入和应答消息的文本框高度
    setParamTextHeight: function () {
        var $outMsg = $(idLogForm).find('[name="out_msg"]');
        var $ackMsg = $(idLogForm).find('[name="ack_msg"]');
        //删除已有的高度限制
        $outMsg.css('height', '');
        $ackMsg.css('height', '');
        //获取流程图容器的高度
        var flowImgWrapperHeight = $(logForm).parent().parent().prev().outerHeight(true);
        //获取主面板的高度
        var mainBoardHeight = $(idWrapperId2).outerHeight(true);
        //调整左右联测的高度
        var baseHeight = mainBoardHeight - flowImgWrapperHeight - 70;
        $outMsg.height(baseHeight);
        $ackMsg.height(baseHeight);
    },

    //显示流程概况的弹窗
    showFlowModal: function () {
        CommonFunc.clearForm(formID);
        $(formContainerID).modal("show");
    },
    //保存流程概况后的回调
    afterSaveFlow: function (resp) {
        // console.log(resp);  //测试

        if (resp.success) {
            global_Object.initFlowTree();
            CommonFunc.msgSu();
            $(formContainerID).modal("hide");
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },
    //保存流程概况
    saveFlowClick: function () {
        var vals = CommonFunc.getForm(formID);
        CommonFunc.ajaxPostForm(global_Object.ajaxSaveFlow, vals, global_Object.afterSaveFlow);
    },
    //显示流程步骤的弹窗
    showFlowSvcModal: function () {
        //销毁弹窗的服务树
        CommonTree.destroy(idFlowSvcTree);

        //初始添加授权弹窗的业务系统文本框树
        var myAttrs = {
            treeID: CommonFunc.formatIdRemoveJquery(idAppTree),
            callback: global_Object.bindFlowSvcTree,
            exceptAppGrantAllSvc: true,
            async: false,
            // username: $(idLoginUserid).val(),
            triggerLevel: 1,
            triggerNullToOtherLevel: true
        };
        var respTreeObject = TextboxTree.init(myAttrs);
        if (!CommonFunc.isEmpty(respTreeObject.resp) && respTreeObject.resp.success
            && respTreeObject.resp.datas && respTreeObject.resp.datas.length > 0) {
            TextboxTree.enable(CommonFunc.formatIdRemoveJquery(idAppTree));
            CommonFunc.clearForm(flowSvcFormID);
            $(flowSvcFormContainerID).modal("show");
        } else {
            CommonFunc.msgFa('所有业务系统已申请全部服务的授权！');
        }
    },
    //绑定流程步骤的服务树
    bindFlowSvcTree: function (queryWord) {
        // console.log('init');  //测试

        //获取主测试ID
        var formData = CommonFunc.getForm(flowSvcFormID);
        var fid = formData.fid;
        //消费方系统ID
        var consumerId = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idAppTree));
        if (CommonFunc.isEmpty(consumerId)) {
            CommonFunc.msgFa('请选择消费方！');
            return;
        }
        //获取已经添加的服务ID
        var req = {
            fid: fid,
            aid: consumerId
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxQuerySidByFidAid, req, function (resp) {
            // console.log(resp);  //测试

            var params = {
                treeID: idFlowSvcTree,
                check: true,
                checkWhenClick: false,
                needSvc: true,
                async: false,
                svcStatus: 1,
                strIdNotInList: JSON.stringify([consumerId]),
                strSidNotInList: JSON.stringify(resp.datas),
                queryWord: queryWord
            };
            var treeObj = CommonTree.init(params);
            var resp = treeObj.resp;
            if (resp.success) {
                flowSvcTree = treeObj.tree;
            } else {
                CommonFunc.msgFa('获取服务失败！' + resp.errorMsg);
            }
        });
    },
    //保存流程步骤后的回调
    afterSaveFlowSvc: function (resp) {
        // console.log(resp);  //测试

        if (resp.success) {
            global_Object.initFlowTree();
            CommonFunc.msgSu('服务步骤保存成功，您可以拖动调整步骤顺序！');
            $(flowSvcFormContainerID).modal("hide");
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },
    //保存步骤
    saveFlowSvcClick: function () {
        var err = '';
        var formData = CommonFunc.getForm(flowSvcFormID);
        //消费方系统ID
        var consumerId = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idAppTree));
        //获取服务ID列表
        var sidList = [];
        if (flowSvcTree) {
            var nodes = flowSvcTree.getCheckedNodes(true);
            if (nodes && nodes.length > 0) {
                for (var i = 0, len = nodes.length; i < len; i++) {
                    if (nodes[i].level == 2) {
                        sidList.push(nodes[i].myData.id);
                    }
                }
            }
            if (sidList.length < 1) {
                err += '请选择服务！';
            }
        }
        //判断是否错误
        if (!CommonFunc.isEmpty(err)) {
            CommonFunc.msgFa(err);
            return;
        }
        var vals = {
            "fid": formData.fid,
            "aid": consumerId,
            "strSidList": JSON.stringify(sidList)
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxSaveFlowSvc, vals, global_Object.afterSaveFlowSvc);
    },

    //弹出结束测试主日志的结果说明
    showTestLogModal: function () {
        CommonFunc.clearForm(testLogForm);
        var logModel = global_Object.testOverview.flow.myData.log;
        CommonFunc.setForm(testLogForm, logModel);
        $(testLogFormContainer).modal("show");
    },
    //结束测试主日志
    finishTestLog: function () {
        var reqData = CommonFunc.getForm(testLogForm);
        CommonFunc.ajaxPostFormSync(global_Object.ajaxFinishTestLog, reqData, function (resp) {
            if (resp.success) {
                //console.log(resp);  //测试

                global_Object.testOverview.flow.myData.log = resp.data;
                CommonFunc.msgSu();
                CommonFunc.clearForm(testLogForm);
                $(testLogFormContainer).modal("hide");

                global_Object.flowTreeNodeClick(global_Object.testOverview.flow);
            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }
        });
    },

    //下载日志PDF
    downLogPdf: function (ack) {
        var logObj = global_Object.testOverview.flow.myData.log;
        var reqData = {
            fid: logObj.fid,
            tid: logObj.id
        };
        CommonFunc.ajaxPostFormSync(global_Object.ajaxDownLogPdf, reqData, function (resp) {
            if (resp.success) {
                var a = document.getElementById("downAck");
                a.href = contextPath + resp.data;
                a.download = resp.data;
                a.click();
            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }
        });
    },

};