/**
 * Created by Evan on 2016/10/26.
 */

var fTable;
var index=0;
var json=[];
var index2=0;
var json2=[];
var filter = contextPath+"/ajax";
var idWrapperId2 = '#wrapperId2';

$(document).ready(function () {

    if (global_Object.type == "day") {
        global_Object.url = filter+"/paas/queryDayTransactionMessageList"
    }
    else if (global_Object.type == "week") {
        global_Object.url = filter+"/paas/queryWeekTransactionMessageList"
    }
    else if (global_Object.type == "month") {
        global_Object.url = filter+"/paas/queryMonthTransactionMessageList"
    }
    global_Object.initDomEvent();

    fTable = $("#fTable").winningTable({
        "pageLength": 10,
        "processing": false,
        "ordering": false, //排序功能
        "columns": [


            {"title": "服务名称", "data": "transactionTypeName",
                "render":function(data, type, full, meta){
                    json.push(full.children);
                    var id = JSON.stringify(full.messageId);
                    var html = '<i class="fa  icon cp fa-plus-square-o"  onclick=global_Object.bzClick(this,'+index+ ','+id+ ')></i> '+global_Object.transactionTypeName;
                    index++;
                    return html;
                }
            },
            {"title": "提供方IP", "data": "serverIpAddress"},
            {"title": "消费方名称", "data": "clientAppName", "orderable": false},
            {"title": "消费方IP", "data": "clientIpAddress"},
            {"title": "耗时", "data": "useTime",
                "render": function (val) {
                    return val + "ms";
                }
            },
            {"title": "状态", "data": "status",
                "render": function (data) {
                    var html;
                    if (data == "成功"){
                        html = '<i class="fa  s-icon fa-check-circle" style="color: #29D798"></i> '+'<span style="color: #29D798"> '+data+'</span>'
                    }else {
                        html = '<i class="fa  s-icon fa-times-circle" style="color: #FD6A6E"></i> '+'<span style="color: #FD6A6E"> '+data+'</span>'
                    }
                    return html;
                }
            },
            {"title": "时间", "data": "startTime"},
            {
                "title": "详情", "data": "startTime",
                "render": function (data, type, full, meta) {
                    json2.push(full.datas);

                    var messageId = JSON.stringify(full.messageId);
                    var html = '<a onclick=global_Object.detail(this,'+ messageId +',-1) href="javascript:void(0)">详情</a> ';
                    index2++;
                    return html;
                }
            }

        ],
        "drawCallback": function (a) {
        },
        "autoWidth": false,
        "responsive": true,
        "width": "100%"
    });

    var datas = {
        serverAppName: global_Object.serverAppId,
        transactionTypeName: global_Object.transactionTypeId,
        serverIpAddress: global_Object.serverIpAddress,
        clientAppName: global_Object.clientAppId,
        clientIpAddress: global_Object.clientIpAddress,
        status: global_Object.status,
        date: global_Object.dateValue
    };
    fTable.queryDataInPage(global_Object.url, datas, global_Object.setHeight);
});
var global_Object = {
    serverAppName:$("#serverAppName").val(),
    serverAppId:$("#serverAppId").val(),
    transactionTypeName:$("#transactionTypeName").val(),
    transactionTypeId:$("#transactionTypeId").val(),
    serverIpAddress:$("#serverIpAddress").val(),
    clientAppName:$("#clientAppName").val(),
    clientAppId:$("#clientAppId").val(),
    clientIpAddress:$("#clientIpAddress").val(),
    status:$("#status").val(),
    type:$("#type").val(),
    value:$("#value").val(),
    dateValue:$("#dateValue").val(),
    historypagetype :$("#historypagetype").val(),
    url:filter+"/paas/queryDayTransactionMessageList",
    initDomEvent:function(){
        $("#statusselect a").on("click", function () {
            $("#statusvalue").html($(this).text() + ' <i class="fa  fa-caret-down pull-right"></i>');
            global_Object.status = $(this).text();
            /*if(global_Object.status=="全部"){
                global_Object.status="全部";
            }*/
            global_Object.queryTableData();
        });

        $("#btnQuery").on("click", function () {
            global_Object.queryTableData();
        });
        $('#inputKeyWords').on('keypress',function(event){
            if(event.keyCode == "13")
            {
                global_Object.queryTableData();
            }
        });
    },
    //xuehao 2018-03-23：调整高度
    setHeight: function () {
        CommonFunc.maxHeightToFrame(idWrapperId2);
    },
    queryTableData:function(){
        index=0;
        json=[];
        index2=0;
        json2=[];
        var datas = {date:global_Object.dateValue,serverAppName:global_Object.serverAppId,transactionTypeName:global_Object.transactionTypeId,serverIpAddress:global_Object.serverIpAddress,clientAppName:global_Object.clientAppId,clientIpAddress:global_Object.clientIpAddress,status:global_Object.status};
        var keyWords = $.trim($("#inputKeyWords").val());
        if(keyWords&&keyWords.length>0){
            datas.keyWords = keyWords;
        }
        fTable.queryDataInPage(global_Object.url,datas, global_Object.setHeight);
    },
    bzClick:function(obj,index,messageId){

        if ($(obj).hasClass("fa-plus-square-o")) {
            var tableHtml = '<tr class="" style="display: none"><td colspan="8"><div> <table class="mytable"> <thead>';
            tableHtml += '<tr>';
            tableHtml += '<th class="firstChild numeric">序号</th>';
            tableHtml += '<th class="numeric">服务步骤</th>';
            tableHtml += ' <th class="numeric">耗时</th>';
            tableHtml += ' <th class="numeric">状态</th>';
            tableHtml += ' <th class="numeric">开始时间</th>';
            tableHtml += ' <th class="numeric">详情</th>';

            tableHtml += '</tr></thead><tbody>';
            if (json[index] != null && json[index].length > 0) {
                $.each(json[index],function (i, v) {
                    var statusHtml;
                    if (v.status == "成功"){
                        statusHtml = '<i class="fa  s-icon fa-check-circle" style="color: #29D798"></i> '+'<span style="color: #29D798"> '+v.status+'</span>'
                    }else {
                        statusHtml = '<i class="fa  s-icon fa-times-circle" style="color: #FD6A6E"></i> '+'<span style="color: #FD6A6E"> '+v.status+'</span>'
                    }
                    tableHtml += '<tr><td>'+(i+1)+'</td><td>'+ v.transactionName+'</td><td>'+v.useTime+'ms</td><td>'+statusHtml+'</td><td>'+v.startTime+'</td><td>'
                        +'<a href="javascript:void(0)" onclick=global_Object.detail(this,"'+messageId+'",'+i+')>详情</a> '+'</td></tr>';
                });
            }
            $(obj).parents("tr").after(tableHtml);
            $(obj).parents("tr").next("tr").fadeIn();
            $(obj).addClass("fa-minus-square-o").removeClass("fa-plus-square-o");
        }
        else{
            $(obj).parents("tr").next("tr").fadeOut();
            $(obj).parents("tr").next("tr").remove();
            $(obj).addClass("fa-plus-square-o").removeClass("fa-minus-square-o");
        }
    },

    detail: function (obj, messageId,idx) {
        $("#xqEdit").modal("show");
        $("#modalScrollDiv").mCustomScrollbar("destroy"); //Destroy

        var url = filter+"/paas/queryTransactionMessageListDetail";
        var datas = { serverAppName:global_Object.serverAppId,messageId: messageId,index:idx};
        JqAjax.postByDefaultErrorCatch(url, datas, function(result) {
            /*var html = "";
            var json = result.data;
            for (var key in json) {
                var temp = json[key];
                temp = JqCommon.parseXMLString(temp);
                html += '<tr><td style="min-width: 90px;vertical-align: middle">' + key + '</td><td><pre style="max-height: 120px;overflow-x: auto;overflow-y: auto;padding: 0px 5px 0px 5px;margin: 0px;"><xmp>' + temp + '</xmp></pre></td></tr>';
            }
            html = html+"";
            $("#xqTable tbody").html(html);*/
            var html = "";
            var json = result.data;
            for (var key in json) {
                var content = JqCommon.parseXMLString(json[key]);//JqCommon.parseXMLString(json[key]);
                html += '<div class="portlet box yellow" style="1px solid #666666">' +
                    '<div class="portlet-title">' +
                    '<div class="caption">' + key + '</div>' +
                    '<div class="tools">' +
                    '<a href="javascript:;" class="collapse" data-original-title="" title=""></a>' +
                    '<a href="javascript:;" class="reload" data-original-title="" title=""> </a>' +
                    '<a href="" class="fullscreen" data-original-title="" title=""></a>' +
                    '</div></div>' +
                    '<div class="portlet-body" style="height: 250px;padding: 0"><textarea style="width: 100%;border: 0;height: 250px;resize: none" readonly="readonly">' + content + '</textarea></div>' +
                    '</div>';
            }
            html = html + "";
            $("#detailContent").html(html);
            $("#modalScrollDiv").mCustomScrollbar({
                autoHideScrollbar: true,	//自动隐藏滚动条
                axis: "y", //水平与竖直都出现滚动条
                theme: "minimal-dark"
            });
        });

        JqAjax.postByDefaultErrorCatch(filter+"/paas/getServiceFlowShow", {serverAppName:global_Object.serverAppId,messageId:messageId}, function (obj) {
            var html = JqCommon.getServiceFlowShow(obj);
            $("#serviceFlow").html(html);
        });
    }
};
