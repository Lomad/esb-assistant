/**
 * Created by Evan on 2016/10/25.
 */

var filter = contextPath+"/ajax";
$(document).ready(function () {

    if($("#clientAppName").val()!= undefined){
        global_Object.clientAppName = $("#clientAppName").val();
    }
    global_Object.historypagetype = $("#historypagetype").val();
    global_Object.initDomEvent();
    if (global_Object.type == "day") {
        global_Object.url = filter+"/paas/queryDayTransactionNameReportByServer"
    }
    else if (global_Object.type == "week") {
        global_Object.url = filter+"/paas/queryWeekTransactionNameReportByServer"
    }
    else if (global_Object.type == "month") {
        global_Object.url = filter+"/paas/queryMonthTransactionNameReportByServer"
    }

    global_Object.queryTableData();

});

var global_Object = {
    tableDataOld: [],
    tableData: [],
    serverIpAddress: $("#serverIpAddresshidden").val(),
    url: filter+"/paas/queryHourTransactionNameReportByServer",
    totalSize: 0,
    type: $("#type").val(),
    value:$("#value").val(),
    dateValue:$("#dateValue").val(),
    serverAppName: $("#serverAppName").val(),
    serverAppId:$("#serverAppId").val(),
    transactionTypeName: $("#transactionTypeName").val(),
    transactionTypeId:$("#transactionTypeId").val(),
    clientAppName:"",
    clientAppId:$("#clientAppId").val(),
    historypagetype:"",
    initDomEvent: function () {

    },
    queryTableData: function () {
        var datas = {
            flname: global_Object.serverAppId,
            transactionTypeName: global_Object.transactionTypeId,
            serverIpAddress: global_Object.serverIpAddress,
            date:global_Object.dateValue,
            clientAppName:global_Object.clientAppId
        };
        JqAjax.postByDefaultErrorCatch(global_Object.url,datas,function (data) {

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

