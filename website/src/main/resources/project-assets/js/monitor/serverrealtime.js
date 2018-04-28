/**
 * Created by Admin on 2016/10/21.
 */

var filter = contextPath + "/ajax";
var idTreeWrapper = '#treeDiv';
var idWrapperId2 = '#relationChartsDiv';
var idWrapperId3 = '#tableDiv';

$(document).ready(function () {

    global_Object.flname=$("#domain").val();
    global_Object.type=$("#type").val();
    global_Object.status=$("#status").val();
    global_Object.isRemote=$("#isRemote").val();

    // Tree_App.initProvider(global_Object.treeChange,$("#domain").val());
    global_Object.bindTree();

    //激活Bootstrap的tooltip
    $('[data-toggle="tooltip"]').tooltip();

    global_Object.N = "N";
    global_Object.D = "D";
    global_Object.H = "H";

    global_Object.URL_N = filter + "/paas/queryTransactionTypeList";
    global_Object.URL_D = filter + "/paas/queryTodayTransactionTypeReportByServer";
    global_Object.URL_H = filter + "/paas/queryHourTransactionTypeReportByServer";


    //定义cookies名称
    global_Object.cookie_domain_name = "app.config.domain";
    //type = N=实时 D=当天 H=时段
    global_Object.cookie_real_type = "app.config.real.type";
    global_Object.cookie_real_time = "app.config.real.time";

    global_Object.initDomEvent();

    var cookie_real_type = $.cookie(global_Object.cookie_real_type);
    if (!global_Object.type){
        global_Object.type = cookie_real_type || global_Object.N;
    }

    var cookie_real_time = $.cookie(global_Object.cookie_real_time);
    if (global_Object.type == global_Object.H && cookie_real_time) {
        global_Object.time = cookie_real_time;
    } else {
        global_Object.time = "";
    }

    if (global_Object.type == global_Object.D) {
        $("#time2").addClass("blue").siblings().removeClass("blue");
    } else if (global_Object.type == global_Object.H) {
        $("#time3").addClass("blue").siblings().removeClass("blue");
        $("#time3").html(global_Object.time + ' <i class="fa  fa-caret-down"></i>');
    }

    $('#tableDiv').resize(function(){
        global_Object.setHeight();
    });

});
var global_Object = {
    tableDataOld: [],
    tableData: [],
    flname: "",
    //消费方appId
    clientName:'',
    //消费方AppName
    clientAppName:'',
    url: "",
    totalSize: 0,
    type: "",
    time: "",
    status:"",
    option:"",
    isRemote:'',
    customName:'',

    //绑定业务系统树
    bindTree: function () {
        var params = {
            needESB: true,
            callback: global_Object.treeChange,
            sysDirection: 1,
            triggerLevel: 1,
            triggerNullToOtherLevel: true,
            defaultAppId : global_Object.flname
        };
        CommonTree.init(params);
    },
    treeChange: function (sysID, obj) {
        global_Object.flname = obj.appId;
        global_Object.customName = obj.appName;
        global_Object.setCommunicationChart();
        global_Object.setHeight();
    },
    setHeight :function () {
        // var rightUp = $("#relationChartsDiv").outerHeight(true), rightDown = $("#tableDiv").outerHeight(true);
        // var rightPart = rightUp + rightDown;
        // var leftPart = $("#treeDiv").outerHeight(true);
        // if (rightPart > leftPart) {
        //     $("#treeDiv").height(rightPart);
        // } else {
        //     $("#tableDiv").height(leftPart - rightUp - 15);
        // }

        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId2, idWrapperId3);

    },

    initDomEvent: function () {
        $("#time1").on("click", function () {
            global_Object.type = global_Object.N;
            global_Object.time = "";
            $.cookie(global_Object.cookie_real_type, global_Object.type, {expires: 7});
            $.cookie(global_Object.cookie_real_time, global_Object.time, {expires: 7});
            $(this).addClass("blue").siblings().removeClass("blue");
            global_Object.setCommunicationChart();
        });
        $("#time2").on("click", function () {
            global_Object.type = global_Object.D;
            global_Object.time = "";
            $.cookie(global_Object.cookie_real_type, global_Object.type, {expires: 7});
            $.cookie(global_Object.cookie_real_time, global_Object.time, {expires: 7});
            $(this).addClass("blue").siblings().removeClass("blue");
            global_Object.setCommunicationChart();
        });

        $("#time3v li").on("click", function () {
            global_Object.type = global_Object.H;
            global_Object.time = $(this).text();
            $.cookie(global_Object.cookie_real_type, global_Object.type, {expires: 7});
            $.cookie(global_Object.cookie_real_time, global_Object.time, {expires: 7});
            $("#time3").addClass("blue").siblings().removeClass("blue");
            $("#time3").html(global_Object.time + ' <i class="fa  fa-caret-down"></i>');
            global_Object.setCommunicationChart();
        });
        $("#querybtn").on("click", function () {
            global_Object.setTableData();
        });
        JqCommon.setScrollBar($("#treeDiv"));
        JqCommon.setScrollBar($("#tableDiv"));
    },
    queryTableData: function () {
        var url;
        var type = global_Object.type;
        var time = global_Object.time;
        var status = global_Object.status;
        var clientAppName = global_Object.clientName;

        if (type == global_Object.D) {
            url = global_Object.URL_D;
        } else {
            var nowhour = new Date().getHours();
            if (type == global_Object.N || nowhour == time.substr(0, 2)) {
                url = global_Object.URL_N;
            } else {
                time = JqCommon.getSimpleDateString(time);
                url = global_Object.URL_H;
            }
        }
        JqAjax.postByDefaultErrorCatch(url, {flname: global_Object.flname, time: time, status: status, clientAppName: clientAppName}, function (data) {
            global_Object.tableDataOld = data.transactionStatisticDatas;
            global_Object.tableData = data.transactionStatisticDatas;
            global_Object.totalSize = data.totalSize;
            if (global_Object.totalSize > 0) {
                global_Object.setTable();
            } else {
                $("#fTable tbody").html('<tr class="odd"><td valign="top" colspan="12" class="dataTables_empty">表中数据为空</td></tr>');
            }
            if (global_Object.isRemote){
                global_Object.isRemote = "";
            }
        });
    },

    setTableData: function () {
        var limit = $("#keyword").val();
        global_Object.tableData = JqCommon.setTableData(global_Object.tableDataOld, limit);

        global_Object.setTable();
    },
    setTable: function () {
        var alltr = function (data, type) {
            var tr = '<tr data-transactiontypename="' + data.transactionTypeName
                + '" data-serveripaddress="' + data.serverIpAddress
                +'"data-transactiontypeid="'+ data.transactionTypeId +'">';
            if (type == "transactionTypeName") {
                tr += '<td><i class="fa  icon cp fa-chevron-down"></i><a onclick="global_Object.openPostWindow(this)" href="javascript:void(0)">' + data.transactionTypeName + '</a></td>';
            }
            else if (type == "serverIpAddress") {
                tr += '<td><a onclick="global_Object.openPostWindow(this)" href="javascript:void(0)" >' + data.serverIpAddress + '</a></td>';
            }

            tr += '<td><a onclick="global_Object.openDetailsList(this)" data-count="'+ data.totalCount
                +'" href="javascript:void(0)">' + data.totalCount + '次</a></td>';
            tr += '<td><a onclick="global_Object.openPostAvg(this)" href="javascript:void(0)">' + data.avg + 'ms</a></td>';
            /*tr += '<td>' + data.line99Value + 'ms</td>';
            tr += '<td>' + data.line95Value + 'ms</td>';*/
            tr += '<td>' + data.min + 'ms</td>';
            tr += '<td>' + data.max + 'ms</td>';
            tr += '<td>' + data.tps + '</td>';

            if(data.failCount>0) {
                tr += '<td><a onclick="global_Object.openDetailsList(this, \''
                    + BizStable.MonitorStatus.FAILURE + '\')" data-count="' + data.failCount
                    + '" href="javascript:void(0)">' + data.failCount + '次</a></td>';
            } else {
                tr += '<td>0次</td>';
            }

            tr += '<td>' + data.failPercent + '%</td>';
            /*tr += '<td>' + data.std + '</td>';*/
            tr += '<td><i class="fa  fa-bar-chart-o cp" onclick="global_Object.queryPic(this)"></i></td>';
            return tr;
        };
        var html = [];
        $.each(global_Object.tableData, function (i, v) {
            html.push(alltr(v, "transactionTypeName"));
            var tableHtml = '<tr class="" style="display: none"><td colspan="12" style="padding: 10px"><div>' +
                '<table class="mytable subtable" style="border: 0px"> <thead>';
            tableHtml += '<tr>';
            tableHtml += '<th class="numeric">服务器地址</th>';
            tableHtml += ' <th class="numeric ">调用次数</th>';
            tableHtml += ' <th class="numeric ">平均耗时</th>';
            /*tableHtml += ' <th class="numeric ">99%</th>';
            tableHtml += ' <th class="numeric ">95%</th>';*/
            tableHtml += ' <th class="numeric ">最短耗时</th>';
            tableHtml += ' <th class="numeric ">最长耗时</th>';
            tableHtml += ' <th class="numeric ">吞吐量</th>';
            tableHtml += ' <th class="numeric ">失败次数</th>';
            tableHtml += ' <th class="numeric ">失败率</th>';
            /*tableHtml += ' <th class="numeric ">方差</th>';*/
            tableHtml += ' <th class="numeric ">显示图表</th>';
            tableHtml += '</tr></thead><tbody>';
            if (v.transactionStatisticDataDetails != null && v.transactionStatisticDataDetails.length > 0) {
                $.each(v.transactionStatisticDataDetails, function (i2, v2) {
                    tableHtml += alltr(v2, "serverIpAddress");
                });
            }
            tableHtml += ' </tbody></table></div></td></tr>';
            html.push(tableHtml);
        });

        $("#fTable tbody").html(html.join(""));
        global_Object.setHeight();
        $("#fTable .icon").on("click", function () {
            var tr = $(this).parents("tr");
            if ($(this).hasClass("fa-chevron-down")) {
                $(tr).next("tr").fadeIn();
                $(this).addClass("fa-chevron-up").removeClass("fa-chevron-down");
            }
            else {
                $(tr).next("tr").fadeOut();
                $(this).addClass("fa-chevron-down").removeClass("fa-chevron-up");
            }
        });

    },

    queryPic: function (obj) {
        $("#echart").css("width", $("#picEdit").width() * 0.6 - 30);
        $("#picEdit").modal("show");
        var url = "", unit = "分";
        if (global_Object.type == global_Object.N) {
            url = filter + "/paas/queryLastHourTransactionTypeCallTimesReportByServer";
        }
        else if (global_Object.type == global_Object.D) {
            url = filter + "/paas/queryTodayTransactionTypeCallTimesReportByServer";
            unit = "时";
        }
        else if (global_Object.type == global_Object.H) {
            url = filter + "/paas/queryHourTransactionTypeCallTimesReportByServer";
        }

        var datas = {
            serverAppName: global_Object.flname,
            transactionTypeName: $(obj).parents("tr").data("transactiontypeid"),
            serverIpAddress: $(obj).parents("tr").data("serveripaddress"),
            hour: JqCommon.getSimpleDateString(global_Object.time),
            clientAppName:global_Object.clientName
        };
        JqAjax.postByDefaultErrorCatch(url, datas, function (data) {
            var option = JqCommon.getCartogram(unit,data);
            var myChart = echarts.init(document.getElementById("echart"));
            myChart.setOption(option);
        });

    },
    setCommunicationChart:function(){
        var timeType,time;

        if (global_Object.type == global_Object.N)
            timeType = "currentHour";
        if (global_Object.type == global_Object.H){
            timeType = "specifiedHour";
            time = JqCommon.getSimpleDateString(global_Object.time);
        }

        if (global_Object.type == global_Object.D)
            timeType = "today";

        JqAjax.postByDefaultErrorCatch(filter + "/paas/queryAllCommunicationPoints", {flname:global_Object.flname,soc:"SERVER",timeType:timeType,time:time}, function (result) {

            var clientNames = [];
            var chosen = 'image://'+contextPath+'/project-assets/img/monitor/target_click.png',
                formal = 'image://'+contextPath+'/project-assets/img/monitor/target.png';

            var option = JqCommon.generateProviderCommunicationChart(result,global_Object.customName,chosen,formal,clientNames);
            var chart = echarts.init(document.getElementById('relationChart'));
            //chart.clear();

            var down = $("#downDomain").val();
            if (global_Object.isRemote && down != "undefined") {
                global_Object.clientName = down;
                var data = option.series[0].data;
                for (var i = 0; i < data.length; i++) {
                    var each = data[i];
                    var domain = JqCommon.splitStringToArray(each.id);
                    if (each.symbol == chosen) {
                        each.symbol = formal;
                    }
                    if (domain == global_Object.clientName)
                        each.symbol = chosen;
                }
            }else {
                //global_Object.clientName = clientNames[0];
            }
            chart.setOption(option);
            global_Object.option = option;

            if (!global_Object.isRemote && clientNames.length > 0) {
                //global_Object.clientName = clientNames[0];
            }
            global_Object.queryTableData();

            chart.on('click',function (params) {
                var data = params.data;
                var id = data.id;
                var name = data.name;
                var isClient = false;
                if (id.indexOf("consumer") >= 0) {
                    isClient = true;
                }
                if (id === "provider"){
                    var priData = global_Object.option.series[0].data;
                    for (var index = 0;index<priData.length;index++){
                        var each = priData[index];
                        var domain = JqCommon.splitStringToArray(each.id);
                        if (domain == global_Object.clientName){
                            each.symbol = formal;
                        }
                    }
                    chart.setOption(global_Object.option);
                    global_Object.clientName = "";
                    global_Object.queryTableData();
                }
                if (params.dataType == "node" && id != "provider" && isClient){
                    var priData = global_Object.option.series[0].data;
                    for (var index = 0;index<priData.length;index++){
                        var each = priData[index];
                        var domain = JqCommon.splitStringToArray(each.id);
                        if (domain == global_Object.clientName){
                            each.symbol = formal;
                        }
                        if (domain == JqCommon.splitStringToArray(id) && each.id.indexOf("provider") == -1){
                            each.symbol = chosen;
                        }
                    }
                    chart.setOption(global_Object.option);
                    global_Object.clientName = JqCommon.splitStringToArray(id);
                    global_Object.clientAppName = name;

                    global_Object.queryTableData();
                }
            });
        });
    },

    openPostWindow: function (obj) {
        var url = contextPath+"/view/paas/serverrealtime__serversysrealtime";
        var
            datas = {
                "transactionTypeId": $(obj).parents("tr").data("transactiontypeid"),
                "serverIpAddress": $(obj).parents("tr").data("serveripaddress") == undefined ? "" : $(obj).parents("tr").data("serveripaddress"),
                "serverAppId": global_Object.flname,
                "type": global_Object.getTypeLabel(global_Object.type),
                "time": JqCommon.getSimpleDateString(global_Object.time),
                "clientAppId":global_Object.clientName
            };
        JqCommon.openPostWindow(url, datas);
    },

    /**
     * 跳转到详情页面
     */
    openDetailsList: function (obj, status) {
        // console.log($(obj));    //测试
        // console.log($(obj).data("totalCount"));    //测试

        // var url = contextPath+"/view/paas/serverrealtime__serverdetailedrealtime";
        var url = BizStable.CommonURL.serverDetailedRealtime;
        var datas = {
            "transactionTypeId": $(obj).parents("tr").data("transactiontypeid"),
            "transactionTypeName": $(obj).parents("tr").data("transactiontypename"),
            "serverIpAddress": $(obj).parents("tr").data("serveripaddress") == undefined ? "" : $(obj).parents("tr").data("serveripaddress"),
            "serverAppId": global_Object.flname,
            "serverAppName": global_Object.customName,
            "type": global_Object.getTypeLabel(global_Object.type),
            "time": JqCommon.getSimpleDateString(global_Object.time),
            "clientAppId": global_Object.clientName,
            "clientAppName": global_Object.clientAppName,
            "totalCount": $(obj).attr("data-count")
        };
        if(!CommonFunc.isEmpty(status)) {
            datas.status = status;
        }
        // JqCommon.openPostWindow(url, datas);

        //将参数直接拼接在URL中，然后直接跳转
        var urlFinal = CommonFunc.joinUrlParams(datas, url);

        // console.log(urlFinal);  //测试

        window.location.href = urlFinal;
    },

    // openPostFalse: function (obj) {
    //     var url = contextPath+"/view/paas/serverrealtime__serverdetailedrealtime";
    //     var datas = {
    //         "transactionTypeId": $(obj).parents("tr").data("transactiontypeid"),
    //         "serverIpAddress": $(obj).parents("tr").data("serveripaddress") == undefined ? "" : $(obj).parents("tr").data("serveripaddress"),
    //         "serverAppId": global_Object.flname,
    //         "type": global_Object.getTypeLabel(global_Object.type),
    //         "time": JqCommon.getSimpleDateString(global_Object.time),
    //         "clientAppId": global_Object.clientName,
    //         "clientIpAddress": "",
    //         "status": "执行失败"
    //     };
    //     JqCommon.openPostWindow(url, datas);
    // },

    openPostAvg: function (obj) {
        var url = contextPath+"/view/paas/serverrealtime__serversteprealtime";
        var datas = {
            "transactionTypeId": $(obj).parents("tr").data("transactiontypeid"),
            "serverIpAddress": $(obj).parents("tr").data("serveripaddress") == undefined ? "" : $(obj).parents("tr").data("serveripaddress"),
            "serverAppId": global_Object.flname,
            "type": global_Object.getTypeLabel(global_Object.type),
            "time": JqCommon.getSimpleDateString(global_Object.time),
            "clientAppId":global_Object.clientName
        };
        JqCommon.openPostWindow(url, datas);
    },

    getTypeLabel: function (type) {
        if (type == global_Object.N) {
            return "当前一小时";
        } else if (type == global_Object.D) {
            return "当天";
        } else if (type == global_Object.H) {
            return "指定小时";
        }
        return null;
    }

};
