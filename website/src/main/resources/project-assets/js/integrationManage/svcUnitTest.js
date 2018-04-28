/**
 * Created by xuehao on 2017/09/25.
 */

//公用的请求前缀
var ajaxReqPre1 = "/ajax/integrationManage/svcUnitTest/";
var ajaxReqPreUrl = "/ajax/baseManage/svcUrl/";
var ajaxStructureReqPre = "/ajax/serviceManage/svcStructure/";
var ajaxBizReqPre = "/ajax/integrationManage/simulationBusinessTest/";

//ID
var idLoginUserid = '#loginUserid';

var idTreeWrapper = '#treeWrapper';
var idWrapperId2 = '#wrapperId2';
var idWrapperId3 = '#wrapperId3';
var formContainerID1 = '#editFormContainer1';
var tableID = '#listTable';
var formID = '#editForm';
var idLogForm = '#logForm';
var idBtnPost = "#btnPost";

//全局变量

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    //服务对象
    svcObj: null,

    //测试概要信息对象（包含：url，urlStatus，urlType，urlAgent，urlAgentStatus, mode - 平台模式，modeName - 平台模式名称）
    testOverview: null,

    //接收模式时，等待弹窗操作相关的变量
    receiveIntervalID: null,    //定时接收函数的ID
    receiveIntervalMaxWait: 120,   //定时接收的最大耗时（秒）
    receiveIntervalAll: 0,   //定时接收的总耗时（秒）
    layerLoadingIndex: 0,    //等待弹窗的索引号
    layerLoadingIntervalID: 0,    //等待弹窗中的倒计时

    //常量
    MODE_ESB_RECEIVER: 1,  //平台作为接收端模式
    MODE_ESB_SENDER: 2,    //平台作为客户端模式

    myDate: new Date(),
    ajaxExportMsg: contextPath + ajaxStructureReqPre + 'export',
    ajaxSend: contextPath + ajaxBizReqPre + 'send',
    ajaxReceiveStart: contextPath + ajaxBizReqPre + "receiveStart",
    ajaxReceiveStop: contextPath + ajaxBizReqPre + "receiveStop",
    ajaxReceive: contextPath + ajaxBizReqPre + "receive",
    ajaxTestLog: contextPath + ajaxReqPre1 + "testLog",
    ajaxDownload: contextPath + ajaxReqPre1 + 'download',
    ajaxDownloadAck: contextPath + ajaxReqPre1 + 'downloadAck',
    ajaxGetUrlInfo: contextPath + ajaxReqPreUrl + 'getUrlInfo',
    ajaxCheckUrlStatus: contextPath + ajaxReqPreUrl + 'checkUrlStatus',
    initDomEvent: function () {
        //绑定树
        global_Object.bindTree();

        $("#btnEsbReceiver").on("click", function () {
            global_Object.toggleMode(global_Object.MODE_ESB_RECEIVER, '平台作为接收端');
        });
        $("#btnEsbSender").on("click", function () {
            global_Object.toggleMode(global_Object.MODE_ESB_SENDER, '平台作为发送端');
        });
        $("#btnToggleMode").on("click", function () {
            if(global_Object.testOverview.mode == global_Object.MODE_ESB_RECEIVER) {
                $("#btnEsbSender").trigger('click');
            } else {
                $("#btnEsbReceiver").trigger('click');
            }
        });

        $("#btnCreateMsgIn").on("click", function () {
            if (CommonFunc.isEmpty(global_Object.svcObj) || global_Object.svcObj.id == 0)
                CommonFunc.msgFa("未选择服务");
            else
                global_Object.createMsgIn(global_Object.svcObj.id);
        });

        $("#btnClearMsg").on("click", function () {
            $('[name="out_msg"]').val("");
            $('[name="ack_msg"]').val("");
            $('[name="out_msg"]').focus();
        });

        $(idBtnPost).on("click", function () {
            global_Object.postClick();
        });

        $("#btnDownloadAll").on("click", function () {
            global_Object.download();
        });

        $("#btnDownloadMsgAck").on("click", function () {
            global_Object.downloadAck();
        });

        //全部服务
        $("#allTest").click(function () {
            //绑定树
            global_Object.bindTree();
        });
        //筛选已测试服务
        $("#tested").click(function () {
            //绑定树
            global_Object.bindTree(1);
        });
        //筛选未测试服务
        $("#untested").click(function () {
            //绑定树
            global_Object.bindTree(2);
        });

        //设置滚动条
        CommonFunc.setScrollBarWithWrapper();
    },
    /**
     * 绑定树
     * @param testFlag null-全部，1-已测试，2-未测试
     */
    bindTree: function (testFlag) {
        //绑定业务系统树
        var params = {
            callback: global_Object.treeChange,
            needSvc: true,
            isTestUnit: true,
            testUnitFlag: testFlag,
            triggerLevel: 2,
            triggerNullToOtherLevel: true,
            username: $(idLoginUserid).val()
        };
        CommonTree.init(params);
    },
    treeChange: function (nodeID, obj, treeObj, treeNode) {
        // console.log(nodeID); //测试
        // console.log(obj); //测试
        // console.log(treeNode); //测试

        //将选择的业务系统ID保存到全局变量中
        CommonFunc.clearForm(idLogForm);
        global_Object.svcObj = obj;

        //获取最近一次的测试
        //global_Object.bindLatestTest();

        //检测服务地址状态
        CommonFunc.ajaxPostForm(global_Object.ajaxGetUrlInfo, {
            urlAgentId: obj.urlAgentId,
            urlId: obj.urlId,
            url: obj.url
        }, global_Object.getUrlInfo);
        $('[name="out_msg"]').focus();

        //设置最近测试状态
        global_Object.setLatestTestState(treeNode);

        //初始化平台模式
        $(idWrapperId3).removeClass("hidden");
        $(idWrapperId2).addClass("hidden");
        global_Object.testOverview = {};

        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId2);
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId3);
        global_Object.setParamTextHeight();
    },
    //切换测试模式
    toggleMode: function (mode, modeName) {
        $(idWrapperId2).removeClass("hidden");
        $(idWrapperId3).addClass("hidden");
        global_Object.testOverview.mode = mode;
        global_Object.testOverview.modeName = modeName;
        if (mode == global_Object.MODE_ESB_RECEIVER) {
            $(idBtnPost).text('接收');
            CommonFunc.setForm("testOverview", {
                modeName: modeName,
                url: global_Object.testOverview.urlAgent,
                urlType: global_Object.testOverview.urlAgentType
            });
        } else {
            $(idBtnPost).text('发送');
            CommonFunc.setForm("testOverview", {
                modeName: modeName,
                url: global_Object.testOverview.url,
                urlType: global_Object.testOverview.urlType
            });
        }

        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper);
    },
    //获取服务地址信息
    getUrlInfo: function (resp) {
        // console.log(resp); //测试

        //设置地址与状态
        // var badgeCss, badgeContent;
        if (resp.success) {
            var data = resp.data;
            //保存地址对象
            global_Object.testOverview = data;

            //显示服务概况
            CommonFunc.setForm("svcOverview", global_Object.svcObj);

            //设置通信地址信息
            $('#esbReceiverUrlInfo').html('ESB代理通信方式：' + data.urlAgentType
                + '<br>ESB代理地址：<span title="' + data.urlAgent + '">' + CommonFunc.substr(data.urlAgent, 35) + '</span>');
            $('#esbSenderUrlInfo').html('通信方式：' + data.urlType
                + '<br>服务源地址：<span title="' + data.url + '">' + CommonFunc.substr(data.url, 35) + '</span>');

            // //设置状态
            // if (resp.data.urlStatus == 1 && resp.data.urlAgentStatus == 1) {
            //     badgeCss = "prject-backcolor-success";
            //     badgeContent = "访问正常";
            // } else {
            //     badgeCss = "prject-backcolor-failure";
            //     badgeContent = "无法访问";
            // }
        } else {
            // badgeCss = "prject-backcolor-failure";
            // badgeContent = "检测失败";
            //重置地址与状态
            global_Object.testOverview = {};
        }
        // var stateDesp = '服务地址<span class="badge margin-left-5 ' + badgeCss + '">' + badgeContent + '</span>';
        // $('#svcUrlState').html(stateDesp);
    },
    //检测服务地址状态
    checkUrlState: function (resp) {
    },
    //设置最近测试状态
    setLatestTestState: function (treeNode) {
        var name = CommonFunc.isEmpty(treeNode.name) ? '' : treeNode.name;
        var nameRaw = treeNode.myData.name;
        var testFlag = name.replace(nameRaw, '');
        var stateDesp = '最近测试';
        if (!CommonFunc.isEmpty(testFlag)) {
            testFlag = testFlag.replace('badge-tree', 'badge margin-left-5')
            stateDesp += testFlag;
        } else {
            stateDesp += '<span class="badge margin-left-5">待测试</span>';
        }
        $('#latestTestState').html(stateDesp);
    },
    //设置输入和应答消息的文本框高度
    setParamTextHeight: function () {
        var $outMsg = $('[name="out_msg"]');
        var $ackMsg = $('[name="ack_msg"]');
        var $btnPostWrapper = $(idBtnPost).parent().parent();
        //删除已有的高度限制
        $outMsg.css('height', '');
        $ackMsg.css('height', '');
        //调整左右联测的高度
        var baseHeight = $(idWrapperId2).outerHeight(true) - 135;
        $outMsg.height(baseHeight);
        $ackMsg.height(baseHeight);
        $btnPostWrapper.css('margin-top', baseHeight / 2 + 24);
    },
    createMsgIn: function (sid) {
        // console.log(sid);   //测试

        var datas = {
            msgType: global_Object.svcObj.msgType,
            sid: sid,
            direction: 1,
            returnType: 1,
            valueType: 2,
            wrapperDataProtocal: true
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxExportMsg, datas, function (resp) {
            // console.log(resp);   //测试

            if (resp.success) {
                if (CommonFunc.isEmpty(resp.data)) {
                    CommonFunc.msgEx("该服务未导入参数结构，请在服务管理中导入参数结构，或手工输入请求内容！", function () {
                        $('[name="out_msg"]').focus();
                    });
                } else {
                    $('[name="out_msg"]').val(resp.data);
                }
            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }
        });
    },
    //请求后台
    postClick: function () {
        // console.log(testOverview);    //测试

        var err = '';
        var testOverview = global_Object.testOverview;
        if (testOverview.mode == global_Object.MODE_ESB_SENDER) {
            //平台作为发送端，需验证服务源地址
            if (CommonFunc.isEmpty(testOverview.url)) {
                err += '服务源地址不存在！';
            } else if (testOverview.urlStatus == 0) {
                err += '服务源地址(' + testOverview.url + ')不可访问，无法测试！';
            }
            //平台作为发送端，需验证管理平台与ESB的内部交互地址
            if (CommonFunc.isEmpty(testOverview.urlEsbTest)) {
                err += '管理平台与ESB的内部交互地址未设置！';
            } else if (testOverview.urlEsbTestStatus == 0) {
                err += '管理平台与ESB的内部交互地址(' + testOverview.urlEsbTest + ')不可访问，无法测试！';
            }
        } else {
            //平台作为接收端，只需验证代理地址
            if (CommonFunc.isEmpty(testOverview.urlAgent)) {
                err += '服务代理地址不存在！';
            } else if (testOverview.urlAgentStatus == 0) {
                err += '服务代理地址(' + testOverview.urlAgent + ')不可访问，无法测试！';
            }
        }
        if (!CommonFunc.isEmpty(err)) {
            CommonFunc.msgFa(err);
            return;
        }

        //根据平台模式，调用不同的功能
        if (testOverview.mode == global_Object.MODE_ESB_SENDER) {
            global_Object.send();
        } else {
            //接收
            var vals = {
                datas: JSON.stringify({
                    svc: global_Object.svcObj
                })
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxReceiveStart, vals, global_Object.receiveStart);

            //重置表单
            global_Object.resetMsgForm();
        }
    },
    //发送请求
    send: function (resp) {
        // console.log('sendClick');    //测试

        //获取请求消息
        var msg = CommonFunc.getForm(idLogForm);
        if(CommonFunc.isEmpty(msg.out_msg)) {
            CommonFunc.msgFa('请求内容不能为空！');
            return;
        }
        //发送
        var vals = {
            sid: global_Object.svcObj.id,
            msg: msg.out_msg,
            esbTestUrl: global_Object.testOverview.urlEsbTest
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxSend, vals, global_Object.afterPost);
    },
    //开启接收消息模式
    receiveStart: function (resp) {
        if (resp.success) {
            //开启定时
            global_Object.receiveIntervalID = window.setInterval(function () {
                CommonFunc.ajaxPostForm(global_Object.ajaxReceive, null, global_Object.receive);
            }, 5000);

            //倒计时弹窗
            var msgInfo = "已开启接收模式，请在" + global_Object.receiveIntervalMaxWait + "秒内将信息发到平台！";
            var resultLayerLoading = CommonFunc.msgLoading(msgInfo, global_Object.receiveIntervalMaxWait, global_Object.receiveStop);
            global_Object.layerLoadingIndex = resultLayerLoading.layerLoadingIndex;
            global_Object.layerLoadingIntervalID = resultLayerLoading.layerLoadingIntervalID;
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },
    //接收消息
    receive: function (resp) {
        global_Object.receiveIntervalAll += 5;
        if (global_Object.receiveIntervalAll >= global_Object.receiveIntervalMaxWait
            || (resp.success && !CommonFunc.isEmpty(resp.data))) {
            //关闭倒计时
            window.clearInterval(global_Object.receiveIntervalID);
            global_Object.receiveIntervalID = null;
            global_Object.receiveIntervalAll = 0;

            //关闭等待弹窗
            CommonFunc.msgLoadingClose(global_Object.layerLoadingIndex);
            //关闭等待弹窗中的倒计时
            window.clearInterval(global_Object.layerLoadingIntervalID);
            global_Object.layerLoadingIndex = 0;
            global_Object.layerLoadingIntervalID = 0;
        }

        //判断处理结果
        if (resp.success) {
            if (CommonFunc.isEmpty(resp.data))
                return;

            var badgeCss, badgeContent;
            if (resp.data.result == 1) {
                badgeCss = "prject-backcolor-success";
                badgeContent = "成功";
            } else if (resp.data.result == 2) {
                badgeCss = "prject-backcolor-failure";
                badgeContent = "失败";
            } else {
                badgeCss = "";
                badgeContent = "未知";
            }
            CommonFunc.setForm(idLogForm, resp.data);
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },
    //关闭接收消息模式
    receiveStop: function () {
        CommonFunc.ajaxPostForm(global_Object.ajaxReceiveStop, null, function (resp) {
            if (!resp.success) {
                CommonFunc.msgFa("关闭接收模式发生错误！" + resp.errorMsg);
            }
        });
    },
    //请求回调
    afterPost: function (resp) {
        // console.log(resp);  //测试

        if (resp.success) {
            if (!CommonFunc.isEmpty(resp.data.ack_msg)) {
                var badgeCss, badgeContent;
                if (resp.data.result == 1) {
                    badgeCss = "prject-backcolor-success";
                    badgeContent = "成功";
                } else if (resp.data.result == 2) {
                    badgeCss = "prject-backcolor-failure";
                    badgeContent = "失败";
                } else {
                    badgeCss = "";
                    badgeContent = "未知";
                }
                //获取选中的节点
                var treeObj = $.fn.zTree.getZTreeObj("appTree");
                var selectNode = treeObj.getSelectedNodes();
                var newName = selectNode[0].myData.name;
                if (selectNode[0].name.indexOf(badgeContent) < 0) {
                    newName += '<span class="badge badge-tree ' + badgeCss + '">' + badgeContent + '</span>';
                }
                selectNode[0].name = newName;
                //更新节点显示值
                treeObj.updateNode(selectNode[0]);
                //更新最近一次的测试结果
                global_Object.setLatestTestState(selectNode[0]);
                CommonFunc.setForm(idLogForm, resp.data);
            }
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }

        //更新测试次数
        var count = $("#sp" + global_Object.svcObj.id).text();
        var tol = parseInt(count) + 1;
        $("#sp" + global_Object.svcObj.id).text(tol);
    },

    download: function () {

        var data = {
            myDate: global_Object.myDate
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxDownload, data, function (resp) {
            if (resp.success) {
                var a = document.getElementById("downLog");
                a.href = contextPath + resp.data;
                a.download = resp.data;
                a.click();
            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }
        });
    },
    downloadAck: function () {
        var ack = $('textarea[name="ack_msg"]').val();
        if (CommonFunc.isEmpty(ack)) {
            CommonFunc.msgSu('应答内容为空，无需下载！');
        } else {
            var vals = {
                ackMsg: ack
            };
            CommonFunc.ajaxPostFormSync(global_Object.ajaxDownloadAck, vals, function (resp) {
                if (resp.success) {
                    var a = document.getElementById("downAck");
                    a.href = contextPath + resp.data;
                    a.download = resp.data;
                    a.click();
                }
            });
        }
    },

};