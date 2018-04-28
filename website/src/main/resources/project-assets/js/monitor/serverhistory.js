/**
 * Created by Evan on 2016/10/21.
 */

var isSetHeight = false;
var filter = contextPath + "/ajax";

var idTreeWrapper = '#treeDiv';
var idWrapperId2 = '#relationChartsDiv';
var idWrapperId3 = '#tableDiv';

$(document).ready(function () {

    // Tree_App.initProvider(global_Object.treeChange);
    global_Object.bindTree();

    global_Object.flname = $("#domain").val();
    global_Object.type = $("#type").val();
    global_Object.status = $("#status").val();
    global_Object.isRemote = $("#isRemote").val();

    //激活Bootstrap的tooltip
    $('[data-toggle="tooltip"]').tooltip();

    global_Object.cookie_domain_name = "app.config.domain";

    global_Object.initDomEvent();

    $('#tableDiv').resize(function () {
        global_Object.setHeight();
    });

});
var global_Object = {
    tableDataOld: [],
    tableData: [],
    flname: "",
    clientName: '',
    url: filter + "/paas/queryDayTransactionTypeReportByServer",
    totalSize: 0,
    type: "day",
    value: "",
    formatdate: "",
    isRemote: '',
    customName: '',

    //绑定业务系统树
    bindTree: function () {
        var params = {
            callback: global_Object.treeChange,
            sysDirection: 1,
            triggerLevel: 1,
            triggerNullToOtherLevel: true
        };
        CommonTree.init(params);
    },
    treeChange: function (sysID, obj) {
        global_Object.flname = obj.appId;
        global_Object.customName = obj.appName;
        global_Object.setCommunicationChart();
        global_Object.setHeight();
    },

    setHeight: function () {
        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId2, idWrapperId3);
    },

    initDomEvent: function () {
        $("#date_picker").datepicker({
            language: "zh-CN",
            autoclose: true,//选中之后自动隐藏日期选择框
            format: "yyyy-mm-dd",//日期格式
            weekStart: 1,
            showWeekNumbers: true,
            endDate: "-1d"
        });
        if (global_Object.isRemote && global_Object.type == "week") {
            $("#selbtn").html("周查询" + ' <i class="fa  fa-caret-down"></i>');
            var date = new Date();
            date = date.valueOf() - 7 * 24 * 60 * 60 * 1000;
            var beforeWeek = new Date(date);
            var week = beforeWeek.getFullYear() + "-" + (beforeWeek.getMonth() + 1) + "-" + beforeWeek.getDate();
            $("#datevalue").val(JqCommon.getNewDay(week));

            var oldDate = $("#datevalue").val().split("-")[0];
            var newDate = oldDate.split("/");
            global_Object.formatdate = newDate[0] + "-" + newDate[1] + "-" + newDate[2];
            global_Object.url = filter + "/paas/queryWeekTransactionTypeReportByServer";
            global_Object.setCommunicationChart();
            global_Object.isRemote = null;
        } else {
            if (!global_Object.type) {
                global_Object.type = "day";
            }
            global_Object.formatdate = JqCommon.getYesterdayFormatDate();
            $("#date_picker").datepicker('update', global_Object.formatdate);
            $("#sel a").on('click', function () {
                var data = $(this).attr("data");
                $("#selbtn").html($(this).text() + ' <i class="fa  fa-caret-down"></i>');
                global_Object.selectType(data);
            });
            $("#date_picker").datepicker().on('hide', function () {
                var date = $('#datevalue').val();
                if (global_Object.type == "week" && date != "") {
                    var newdate = JqCommon.getNewDay(date);
                    $('#datevalue').val(newdate);

                } else if (global_Object.type == "week" && date == "") {
                    var date = new Date();
                    var week = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
                    $("#datevalue").val(JqCommon.getNewDay(week));

                }
                if (global_Object.type == "day") {
                    global_Object.formatdate = $("#datevalue").val();
                } else if (global_Object.type == "week") {
                    var oldDate = $("#datevalue").val().split("-")[0];
                    var newDate = oldDate.split("/");
                    global_Object.formatdate = newDate[0] + "-" + newDate[1] + "-" + newDate[2];
                } else if (global_Object.type == "month") {
                    global_Object.formatdate = $("#datevalue").val() + "-01";
                }
                global_Object.setCommunicationChart();
            });
        }

        $("#querybtn").on("click", function () {
            global_Object.setTableData();
        });
        JqCommon.setScrollBar($("#treeDiv"));
        JqCommon.setScrollBar($("#tableDiv"));
    },

    setCommunicationChart: function () {
        var timeType = global_Object.type, time = global_Object.formatdate;

        JqAjax.postByDefaultErrorCatch(filter + "/paas/queryAllCommunicationPoints", {
            flname: global_Object.flname,
            soc: "SERVER",
            timeType: timeType,
            time: time
        }, function (result) {
            var clientNames = [];
            var chosen = 'image://' + contextPath + '/project-assets/img/monitor/target_click.png',
                formal = 'image://' + contextPath + '/project-assets/img/monitor/target.png';

            var option = JqCommon.generateProviderCommunicationChart(result, global_Object.customName, chosen, formal, clientNames);
            var chart = echarts.init(document.getElementById('relationChart'));
            chart.clear();
            chart.setOption(option);
            global_Object.option = option;

            if (clientNames.length > 0) {
                //global_Object.clientName = clientNames[0];
            }
            global_Object.queryTableData();

            chart.on('click', function (params) {
                var data = params.data;
                var id = data.id;
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
                if (params.dataType == "node" && id != "provider" && isClient) {
                    var priData = global_Object.option.series[0].data;
                    for (var index = 0; index < priData.length; index++) {
                        var each = priData[index];
                        var domain = JqCommon.splitStringToArray(each.id);
                        if (domain == global_Object.clientName) {
                            each.symbol = formal;
                        }
                        if (domain == JqCommon.splitStringToArray(id) && each.id.indexOf("provider") == -1) {
                            each.symbol = chosen;
                        }
                    }
                    chart.setOption(global_Object.option);
                    global_Object.clientName = JqCommon.splitStringToArray(id);
                    global_Object.queryTableData();
                }
            });
        });
    },

    selectType: function (data) {
        global_Object.type = data;
        if (data == "day") {
            $('#date_picker').datepicker('update', '');
            $('#date_picker').datepicker('destroy');
            $("#date_picker").datepicker({
                language: "zh-CN",
                autoclose: true,//选中之后自动隐藏日期选择框
                format: "yyyy-mm-dd",//日期格式
                weekStart: 1,
                showWeekNumbers: true,
                endDate: "-1d"

            });
            global_Object.url = filter + "/paas/queryDayTransactionTypeReportByServer";
            global_Object.formatdate = JqCommon.getYesterdayFormatDate();
            $("#date_picker").datepicker('update', global_Object.formatdate);
            global_Object.setCommunicationChart();
        } else if (data == "week") {
            $('#date_picker').datepicker('update', '');
            $('#date_picker').datepicker('destroy');
            $("#date_picker").datepicker({
                language: "zh-CN",
                autoclose: true,//选中之后自动隐藏日期选择框
                format: "yyyy/mm/dd",//日期格式
                weekStart: 1,
                showWeekNumbers: true,
                calendarWeeks: true,
                todayHighlight: true,
                endDate: "-1d"
            });
            global_Object.url = filter + "/paas/queryWeekTransactionTypeReportByServer";
            var date = new Date();
            var week = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
            $("#datevalue").val(JqCommon.getNewDay(week));
            var oldDate = JqCommon.getNewDay(week).split("-")[0];
            var newDate = oldDate.split("/");
            global_Object.formatdate = newDate[0] + "-" + newDate[1] + "-" + newDate[2];
            global_Object.setCommunicationChart();
        } else if (data == "month") {
            $('#date_picker').datepicker('update', '');
            $('#date_picker').datepicker('destroy');
            $("#date_picker").datepicker({
                language: "zh-CN",
                autoclose: true,//选中之后自动隐藏日期选择框
                format: "yyyy-mm",//日期格式
                weekStart: 1,
                showWeekNumbers: true,
                startView: 'year',
                minViewMode: 1,
                endDate: new Date()
            });
            global_Object.url = filter + "/paas/queryMonthTransactionTypeReportByServer";
            var date = new Date();
            var month = date.getFullYear() + "-" + (date.getMonth() + 1);
            $("#date_picker").datepicker('update', month);
            global_Object.formatdate = month + "-01";
            global_Object.setCommunicationChart();
        }
    },

    queryTableData: function () {
        JqAjax.postByDefaultErrorCatch(global_Object.url, {
            flname: global_Object.flname,
            clientAppName: global_Object.clientName,
            date: global_Object.formatdate,
            status: global_Object.status
        }, function (data) {
            global_Object.tableDataOld = data.transactionStatisticDatas;
            global_Object.tableData = data.transactionStatisticDatas;
            global_Object.totalSize = data.totalSize;
            if (global_Object.totalSize > 0) {
                global_Object.setTable();
            } else {
                $("#fTable tbody").html('<tr class="odd"><td valign="top" colspan="13" class="dataTables_empty">表中数据为空</td></tr>');
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
            var tr = '<tr data-transactiontypename="' + data.transactionTypeName + '" data-serveripaddress="' + data.serverIpAddress + '"data-transactiontypeid="' + data.transactionTypeId + '">';
            if (type == "transactionTypeName") {
                tr += '<td><i class="fa  icon cp fa-chevron-down"></i><a onclick="global_Object.openPostWindow(this)" href="javascript:void(0)">' + data.transactionTypeName + '</a></td>';
            }
            else if (type == "serverIpAddress") {
                tr += '<td><a onclick="global_Object.openPostWindow(this)" href="javascript:void(0)" >' + data.serverIpAddress + '</a></td>';
            }
            tr += '<td><a onclick="global_Object.openPostTotalCount(this)" href="javascript:void(0)">' + data.totalCount + '次</a></td>';
            tr += '<td><a onclick="global_Object.openPostAvg(this)" href="javascript:void(0)">' + data.avg + 'ms</a></td>';
            /*tr += '<td>' + data.line99Value + 'ms</td>';
             tr += '<td>' + data.line95Value + 'ms</td>';*/
            tr += '<td>' + data.min + 'ms</td>';
            tr += '<td>' + data.max + 'ms</td>';
            tr += '<td>' + data.tps + '</td>';
            tr += '<td><a onclick="global_Object.openPostFalse(this)" href="javascript:void(0)">' + data.failCount + '次</a></td>';
            tr += '<td>' + data.failPercent + '%</td>';
            /*tr += '<td>' + data.std + '</td>';*/
            tr += '<td><i class="fa  fa-bar-chart-o cp" onclick="global_Object.queryPic(this)"></i></td>';
            return tr;
        };
        var html = [];
        $.each(global_Object.tableData, function (i, v) {
            html.push(alltr(v, "transactionTypeName"));
            var tableHtml = '<tr class="" style="display: none"><td colspan="12" style="padding: 10px"><div> <table class="mytable subtable" style="border: 0px"> <thead>';
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
        var url = "", unit = "天";
        if (global_Object.type == "day") {
            url = filter + "/paas/queryDayTransactionTypeCallTimesReportByServer";
            unit = "时";
        } else if (global_Object.type == "week") {
            url = filter + "/paas/queryWeekTransactionTypeCallTimesReportByServer";
        } else if (global_Object.type == "month") {
            url = filter + "/paas/queryMonthTransactionTypeCallTimesReportByServer";
        }
        var datas = {
            flname: global_Object.flname,
            transactionTypeName: $(obj).parents("tr").data("transactiontypeid"),
            serverIpAddress: $(obj).parents("tr").data("serveripaddress"),
            date: global_Object.formatdate
        };

        JqAjax.postByDefaultErrorCatch(url, datas, function (data) {
            var option = JqCommon.getCartogram(unit, data);
            var myChart = echarts.init(document.getElementById("echart"));
            myChart.setOption(option);
        });
    },
    openPostWindow: function (obj) {
        global_Object.value = $("#datevalue").val();
        var url = contextPath + "/view/paas/serverhistory__serversyshistory";
        var datas = {
            "transactionTypeId": $(obj).parents("tr").data("transactiontypeid"),
            "serverIpAddr": $(obj).parents("tr").data("serveripaddress") == undefined ? "" : $(obj).parents("tr").data("serveripaddress"),
            "serverAppId": global_Object.flname,
            "clientAppId": global_Object.clientName,
            "type": global_Object.type,
            value: global_Object.value,
            historyPageType: "server",
            dateValue: global_Object.formatdate
        };
        JqCommon.openPostWindow(url, datas);
    },
    openPostAvg: function (obj) {
        global_Object.value = $("#datevalue").val();
        var url = contextPath + "/view/paas/serverhistory__serverstephistory";
        var datas = {
            "transactionTypeId": $(obj).parents("tr").data("transactiontypeid"),
            "serverIpAddress": $(obj).parents("tr").data("serveripaddress") == undefined ? "" : $(obj).parents("tr").data("serveripaddress"),
            "serverAppId": global_Object.flname,
            "clientAppId": global_Object.clientName,
            "type": global_Object.type,
            value: global_Object.value,
            historyPageType: "server",
            dateValue: global_Object.formatdate
        };
        //console.log(datas);
        JqCommon.openPostWindow(url, datas);
    },
    openPostTotalCount: function (obj) {
        global_Object.value = $("#datevalue").val();
        var url = contextPath + "/view/paas/serverhistory__serverdetailedhistory";
        var datas = {
            "transactionTypeId": $(obj).parents("tr").data("transactiontypeid"),
            "serverIpAddress": $(obj).parents("tr").data("serveripaddress") == undefined ? "" : $(obj).parents("tr").data("serveripaddress"),
            "serverAppId": global_Object.flname,
            "clientAppId": global_Object.clientName,
            "type": global_Object.type,
            value: global_Object.value,
            "clientIpAddress": "",
            "status": "",
            historyPageType: "server",
            dateValue: global_Object.formatdate,
            "searchKeywords": $.trim($("#keyword").val())
        };
        JqCommon.openPostWindow(url, datas);
    },
    openPostFalse: function (obj) {
        global_Object.value = $("#datevalue").val();
        var url = contextPath + "/view/paas/serverhistory__serverdetailedhistory";
        var datas = {
            "transactionTypeId": $(obj).parents("tr").data("transactiontypeid"),
            "serverIpAddress": $(obj).parents("tr").data("serveripaddress") == undefined ? "" : $(obj).parents("tr").data("serveripaddress"),
            "serverAppId": global_Object.flname,
            "clientAppId": global_Object.clientName,
            "type": global_Object.type,
            "value": global_Object.value,
            "clientIpAddress": "",
            "status": "执行失败",
            historyPageType: "server",
            dateValue: global_Object.formatdate
        };
        JqCommon.openPostWindow(url, datas);
    }
};

