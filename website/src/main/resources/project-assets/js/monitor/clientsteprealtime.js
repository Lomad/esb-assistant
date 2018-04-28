/**
 * Created by InnerPeace on 2016/10/27.
 */

var filter = contextPath+"/ajax";
$(document).ready(function () {

    global_Object.initDomEvent();
    if (global_Object.type == "当前一小时") {
        global_Object.url = filter+"/paas/queryLastHourTransactionNameReportByServer"
    }
    else if (global_Object.type == "当天") {
        global_Object.url = filter+"/paas/queryTodayTransactionNameReportByServer"
    }
    else if (global_Object.type == "指定小时") {
        global_Object.url = filter+"/paas/queryHourTransactionNameReportByServer"
    }

    global_Object.queryTableData();

});


var global_Object = {
    tableDataOld: [],
    tableData: [],
    serverIpAddress: $("#serverIpAddresshidden").val(),
    url: filter+"/paas/queryLastHourTransactionTypeReportByClient",
    totalSize: 0,
    type: $("#type").val(),
    time: $("#time").val(),
    serverAppName: $("#serverAppName").val(),
    serverAppId: $("#serverAppId").val(),
    transactionTypeId: $("#transactionTypeId").val(),
    transactionTypeName: $("#transactionTypeName").val(),
    clientAppName: $("#clientAppName").val(),
    clientAppId: $("#clientAppId").val(),
    initDomEvent: function () {

    },
    queryTableData: function () {
        JqAjax.postByDefaultErrorCatch(global_Object.url, {
            serverAppName: global_Object.serverAppId,
            transactionTypeName: global_Object.transactionTypeId,
            serverIpAddress: global_Object.serverIpAddress,
            clientAppName: global_Object.clientAppId,
            time:global_Object.time
        }, function (data) {

            global_Object.tableDataOld = data.transactionStatisticDatas;
            global_Object.tableData = data.transactionStatisticDatas;
            global_Object.totalSize = data.totalSize;
            global_Object.setTable();
            global_Object.setPic();
        });
    },

    setTable: function () {
        var alltr = function (data, type, index) {
            var tr = '<tr>';
            tr += '<td>' + (index + 1) + '</td>';
            tr += '<td>' + data.transactionName + '</td>';

            tr += '<td>' + data.totalCount + '次</td>';
            tr += '<td>' + data.avg + 'ms</td>';
            /*tr += '<td>' + data.line99Value + 'ms</td>';
            tr += '<td>' + data.line95Value + 'ms</td>';*/
            tr += '<td>' + data.min + 'ms</td>';
            tr += '<td>' + data.max + 'ms</td>';
            tr += '<td>' + data.tps + '</td>';
            tr += '<td>' + data.failCount + '次</td>';
            tr += '<td>' + data.failPercent + '%</td>';
            /*tr += '<td>' + data.std + 'ms²</td>';*/
            //tr += '<td><i class="fa  fa-bar-chart-o cp" data-toggle="modal" href="#picEdit"></i></td>';
            return tr;
        };
        var html = [];
        $.each(global_Object.tableData, function (i, v) {
            html.push(alltr(v, "transactionName", i));
        });
        //console.log(html);
        $("#fTable tbody").html(html.join(""));
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
    setPic: function () {
        $("#echart").css("width", $("#fTable").width());
        var name=[];
        var json=[];
        $.each(global_Object.tableData,function(i,v){
            name.push(v.transactionName);
            json.push({value: v.avg,name: v.transactionName});
        });

        var newOption ={
            tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            series : [
                {
                    name:'平均耗时占比',
                    type:'pie',
                    radius : ['50%', '70%'],

                    // for funnel
                    x: '50%',
                    y: '50%',
                    width: '35%',
                    funnelAlign: 'left',
                    max: 1048,

                    data:json
                }
            ]
        };
        var myChart = echarts.init(document.getElementById("echart"));
        myChart.clear();
        myChart.setOption(newOption);
    }
}
