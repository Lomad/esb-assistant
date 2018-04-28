/**
 * Created by Admin on 2016/10/21.
 */

var filter = contextPath+"/ajax";
var idWrapperId2 = '#wrapperId2';

$(document).ready(function () {

    global_Object.initDomEvent();
    if (global_Object.type == "当前一小时") {
        global_Object.url = filter+"/paas/queryLastHourTransactionTypeReportByClient"
    }
    else if (global_Object.type == "当天") {
        global_Object.url = filter+"/paas/queryTodayTransactionTypeReportByClient"
    }
    else if (global_Object.type == "指定小时") {
        global_Object.url = filter+"/paas/queryHourTransactionTypeReportByClient"
    }
    JqAjax.postByDefaultErrorCatch(filter+"/paas/getAllServerIpAddress", {serverAppName: global_Object.serverAppId}, function (data) {
        if (global_Object.serverIpAddress == "") {
            $("#serverIpAddress").html("所有主机" + ' <i class="fa  fa-caret-down"></i>');
        }
        else {
            $("#serverIpAddress").html(global_Object.serverIpAddress + ' <i class="fa  fa-caret-down"></i>');
        }
        global_Object.queryTableData();
        var li = ['<li role="presentation"><a role="menuitem" tabindex="-1">所有主机</a></li>'];
        $.each(data, function (i, v) {
            var option = '<li role="presentation"><a role="menuitem" tabindex="-1">' + v + '</a></li>';
            li.push(option);
        });
        $("#serverIpAddress2").html(li.join(""));
        $("#serverIpAddress2 a").on("click", function () {
            $("#serverIpAddress").html($(this).text() + ' <i class="fa  fa-caret-down"></i>');
            global_Object.serverIpAddress = $(this).text();
            $("#serverIpAddress").html(global_Object.serverIpAddress + ' <i class="fa  fa-caret-down"></i>');
            if ($(this).text() == "所有主机") {
                global_Object.serverIpAddress = "";
            }
            global_Object.queryTableData();
        });
    });
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
    serverAppId:$("#serverAppId").val(),
    transactionTypeName: $("#transactionTypeName").val(),
    transactionTypeId:$("#transactionTypeId").val(),
    clientAppName:$("#clientAppName").val(),
    clientAppId:$("#clientAppId").val(),

    initDomEvent: function () {
        $("#querybtn").on("click", function () {

            global_Object.setTableData();
        });
    },
    queryTableData: function () {
        JqAjax.postByDefaultErrorCatch(global_Object.url, {
            serverAppName: global_Object.serverAppId,
            transactionTypeName: global_Object.transactionTypeId,
            serverIpAddress: global_Object.serverIpAddress,
            time:global_Object.time,
            clientAppName:global_Object.clientAppId
        }, function (data) {
            global_Object.tableDataOld = data.transactionStatisticDatas;
            global_Object.tableData = data.transactionStatisticDatas;
            global_Object.totalSize = data.totalSize;
            if(global_Object.totalSize>0){
                global_Object.setTable();
            }else{
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
            var tr = '<tr  data-clientappname="' + data.clientAppName +
                '" data-clientipaddress="' + data.clientIpAddress + '" data-clientappid="' + data.clientAppId + '">';
            if (type == "clientAppName") {
                tr += '<td><i class="fa  icon cp fa-plus-square-o"></i> ' + data.clientAppName + '</td>';
            }
            else if (type == "clientIpAddress") {
                tr += '<td>' + data.clientIpAddress + '</td>';
            }
            tr += '<td><a onclick="global_Object.openDetailsList(this)" data-count="' + data.totalCount +
                '" href="javascript:void(0)">' + data.totalCount + '次</a></td>';
            tr += '<td>' + data.avg + 'ms</td>';
            //tr += '<td><a onclick="global_Object.openPostAvg(this)" href="javascript:void(0)">' + data.avg + 'ms</a></td>';
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
            /*tr += '<td>' + data.std + 'ms²</td>';*/
            return tr;
        };
        var html = [];
        $.each(global_Object.tableData, function (i, v) {
            html.push(alltr(v, "clientAppName"));
            var tableHtml = '<tr style="display: none"><td colspan="12"><div> <table class="mytable"> <thead>';
            tableHtml += '<tr>';
            tableHtml += '<th class="firstChild numeric">客户端地址</th>';
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
            tableHtml += '</tr></thead><tbody>';
            if (v.transactionStatisticDataDetails != null && v.transactionStatisticDataDetails.length > 0) {
                $.each(v.transactionStatisticDataDetails, function (i2, v2) {
                    tableHtml += alltr(v2, "clientIpAddress");
                });
            }
            tableHtml += ' </tbody></table></div></td></tr>';
            html.push(tableHtml);
        });
        //console.log(html);
        $("#fTable tbody").html(html.join(""));
        $("#fTable .icon").on("click", function () {
            var tr = $(this).parents("tr");
            if ($(this).hasClass("fa-plus-square-o")) {
                $(tr).next("tr").fadeIn();
                $(this).addClass("fa-minus-square-o").removeClass("fa-plus-square-o");
            }
            else {
                $(tr).next("tr").fadeOut();
                $(this).addClass("fa-plus-square-o").removeClass("fa-minus-square-o");
            }
        });

        //xuehao 2018-03-23：调整高度
        CommonFunc.maxHeightToFrame(idWrapperId2);
    },

    // openPostFalse: function (obj) {
    //     var url =contextPath+"/view/paas/serverrealtime__serverdetailedrealtime";
    //     var datas = {
    //         "transactionTypeId": global_Object.transactionTypeId,
    //         "serverIpAddress": global_Object.serverIpAddress,
    //         "serverAppId": global_Object.serverAppId,
    //         "type": global_Object.type,
    //         "time": global_Object.time,
    //         "clientAppId": $(obj).parents("tr").data("clientappid"),
    //         "clientIpAddress": $(obj).parents("tr").data("clientipaddress") == undefined ? "" : $(obj).parents("tr").data("clientipaddress"),
    //         "status":"执行失败"
    //     };
    //     JqCommon.openPostWindow(url, datas);
    // },
    // openPostTotalCount: function (obj) {
    //     var url =contextPath+"/view/paas/serverrealtime__serverdetailedrealtime";
    //     var datas = {
    //         "transactionTypeId": global_Object.transactionTypeId,
    //         "serverIpAddress": global_Object.serverIpAddress,
    //         "serverAppId": global_Object.serverAppId,
    //         "type": global_Object.type,
    //         "time": global_Object.time,
    //         "clientAppId": $(obj).parents("tr").data("clientappid"),
    //         "clientIpAddress": $(obj).parents("tr").data("clientipaddress") == undefined ? "" : $(obj).parents("tr").data("clientipaddress"),
    //         "status":""
    //     };
    //     //console.log(datas);
    //     JqCommon.openPostWindow(url, datas);
    // },

    /**
     * 跳转到详情页面
     */
    openDetailsList: function (obj, status) {
        // console.log($(obj));    //测试
        // console.log($(obj).data("totalCount"));    //测试

        var url = BizStable.CommonURL.serverDetailedRealtime;
        var datas = {
            "transactionTypeId": global_Object.transactionTypeId,
            "transactionTypeName": global_Object.transactionTypeName,
            "serverIpAddress": global_Object.serverIpAddress,
            "serverAppId": global_Object.serverAppId,
            "serverAppName": global_Object.serverAppName,
            "type": global_Object.type,
            "time": global_Object.time,
            "clientAppId": $(obj).parents("tr").data("clientappid"),
            "clientAppName": $(obj).parents("tr").data("clientappname"),
            "clientIpAddress": $(obj).parents("tr").data("clientipaddress") == undefined ? "" : $(obj).parents("tr").data("clientipaddress"),
            "totalCount": $(obj).attr("data-count")
        };

        // console.log(datas); //测试
        // return;
        
        if(!CommonFunc.isEmpty(status)) {
            datas.status = status;
        }
        // JqCommon.openPostWindow(url, datas);

        //将参数直接拼接在URL中，然后直接跳转
        var urlFinal = CommonFunc.joinUrlParams(datas, url);

        // console.log(urlFinal);  //测试

        window.location.href = urlFinal;
    }

    // openPostAvg:function(obj){
    //     var url =contextPath+"/paas/serverstephistory";
    //     var datas={"transactionTypeName":$(obj).parents("tr").data("transactiontypename"),
    //         "serverIpAddress":$(obj).parents("tr").data("serveripaddress")==undefined?"":$(obj).parents("tr").data("serveripaddress"),
    //         "clientAppName":$(obj).parents("tr").data("clientappname")==undefined?"":$(obj).parents("tr").data("clientappname"),
    //         "type":(global_Object.type==undefined?"":global_Object.type),
    //         "value":global_Object.value,
    //         "historyPageType":"client",
    //         "dateValue":global_Object.formatdate,
    //         "serverAppName":global_Object.serverAppId
    //     };
    //     JqCommon.openPostWindow(url,datas);
    // }
}
