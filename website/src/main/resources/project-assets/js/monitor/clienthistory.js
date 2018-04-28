/**
 * Created by Evan on 2016/10/27.
 */

var filter = contextPath+"/ajax";

var idTreeWrapper = '#treeDiv';
var idWrapperId2 = '#relationChartsDiv';
var idWrapperId3 = '#tableDiv';

$(document).ready(function(){

    // Tree_App.initConsumer(global_Object.treeChange);
    global_Object.bindTree();

    global_Object.flname=$("#domain").val();
    global_Object.type=$("#type").val();
    global_Object.status=$("#status").val();
    global_Object.isRemote=$("#isRemote").val();

    //激活Bootstrap的tooltip
    $('[data-toggle="tooltip"]').tooltip();

    global_Object.cookie_client_name = "app.config.client";

    global_Object.initDomEvent();

});

var global_Object={
    tableData:[],
    totalSize: 0,
    flname:"",
    clientName:"",
    formatdate:"",
    type:"day",
    value:"",
    url: filter+"/paas/queryDayClientReportByClient",
    isRemote:"",
    option:"",
    customName:'',

    //绑定业务系统树
    bindTree: function () {
        var params = {
            callback: global_Object.treeChange,
            sysDirection: 2,
            triggerLevel: 1,
            triggerNullToOtherLevel: true
        };
        CommonTree.init(params);
    },
    treeChange: function (sysID,obj) {
        global_Object.flname = obj.appId;
        global_Object.customName = obj.appName;
        global_Object.setCommunicationChart();
        global_Object.setHeight();
    },

    setHeight:function () {
        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId2, idWrapperId3);
    },

    initDomEvent:function(){
        $("#date_picker").datepicker({
            language: "zh-CN",
            autoclose: true,//选中之后自动隐藏日期选择框
            format: "yyyy-mm-dd",//日期格式
            weekStart:1,
            showWeekNumbers:true,
            endDate:"-1d"
        });
        //var date = new Date();
        //var yesterday = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+(date.getDate()-1);
        //$("#date_picker").datepicker('update',yesterday);
        if (global_Object.isRemote && global_Object.type == "week"){
            $("#selbtn").html("周查询"+' <i class="fa  fa-caret-down"></i>');
            var date = new Date();
            date = date.valueOf() - 7*24*60*60*1000;
            var beforeWeek = new Date(date);
            var week = beforeWeek.getFullYear() + "-" + (beforeWeek.getMonth()+1) + "-" + beforeWeek.getDate();
            $("#datevalue").val(JqCommon.getNewDay(week));

            var oldDate = $("#datevalue").val().split("-")[0];
            var newDate = oldDate.split("/");
            global_Object.formatdate = newDate[0]+"-"+newDate[1]+"-"+newDate[2];
            global_Object.url = filter+"/paas/queryWeekClientReportByClient";
            global_Object.setCommunicationChart();
            global_Object.isRemote=null;
        }else {
            if (!global_Object.type){
                global_Object.type = "day";
            }
            global_Object.formatdate = JqCommon.getYesterdayFormatDate();
            $("#date_picker").datepicker('update',global_Object.formatdate);
            $("#sel a").on('click',function(){
                var data =$(this).attr("data");
                $("#selbtn").html($(this).text()+' <i class="fa  fa-caret-down"></i>');
                global_Object.selectType(data);
            });
            $("#date_picker").datepicker().on('hide', function(){
                var date = $('#datevalue').val();
                if(global_Object.type=="week"&&date!=""){
                    var newdate = JqCommon.getNewDay(date);
                    $('#datevalue').val(newdate);

                }else if(global_Object.type=="week"&&date==""){
                    var date = new Date();
                    var week = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
                    $("#datevalue").val(JqCommon.getNewDay(week));

                }
                if(global_Object.type=="day"){
                    global_Object.formatdate = $("#datevalue").val();
                }else if(global_Object.type=="week"){
                    var oldDate = $("#datevalue").val().split("-")[0];
                    var newDate = oldDate.split("/");
                    global_Object.formatdate = newDate[0]+"-"+newDate[1]+"-"+newDate[2];
                }else if(global_Object.type=="month"){
                    global_Object.formatdate = $("#datevalue").val()+"-01";
                }
                global_Object.setCommunicationChart();
            });
        }
        JqCommon.setScrollBar($("#treeDiv"));
        JqCommon.setScrollBar($("#tableDiv"));
    },
    setCommunicationChart:function(){
        var timeType = global_Object.type,time = global_Object.formatdate;

        JqAjax.postByDefaultErrorCatch(filter+"/paas/queryAllCommunicationPoints", {flname:global_Object.flname,soc:"CLIENT",timeType:timeType,time:time}, function (result) {

            var clientNames = [];
            var chosen = 'image://'+contextPath+'/project-assets/img/monitor/target_click.png',
                formal = 'image://'+contextPath+'/project-assets/img/monitor/target.png';

            var option = JqCommon.generateConsumerCommunicationChart(result,global_Object.customName,chosen,formal,clientNames);
            var chart = echarts.init(document.getElementById('relationChart'));
            chart.clear();
            chart.setOption(option);
            global_Object.option = option;

            if (clientNames.length > 0) {
                global_Object.clientName = clientNames[0];
            }
            global_Object.queryTableData();


            chart.on('click',function (params) {
                var data = params.data;
                var id = data.id;
                var isClient = false;
                if (id.indexOf("consumer") >= 0) {
                    isClient = true;
                }
                if (params.dataType == "node" && id != "provider" && isClient){
                    var priData = global_Object.option.series[0].data;
                    for (var index = 0;index<priData.length;index++){
                        var each = priData[index];
                        var domain = JqCommon.splitStringToArray(each.id);
                        if (domain == global_Object.clientName){
                            each.symbol = formal;
                        }
                        if (domain == JqCommon.splitStringToArray(id)){
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
    selectType:function(data){
        global_Object.type = data;
        if(data == "day"){
            $('#date_picker').datepicker('update','');
            $('#date_picker').datepicker('destroy');
            $("#date_picker").datepicker({
                language: "zh-CN",
                autoclose: true,//选中之后自动隐藏日期选择框
                format: "yyyy-mm-dd",//日期格式
                weekStart:1,
                showWeekNumbers:true,
                endDate:"-1d"
            });
            global_Object.url = filter+"/paas/queryDayClientReportByClient";
            global_Object.formatdate = JqCommon.getYesterdayFormatDate();
            $("#date_picker").datepicker('update',global_Object.formatdate);
            global_Object.setCommunicationChart();
        }else if(data == "week"){
            $('#date_picker').datepicker('update','');
            $('#date_picker').datepicker('destroy');
            $("#date_picker").datepicker({
                language: "zh-CN",
                autoclose: true,//选中之后自动隐藏日期选择框
                format: "yyyy/mm/dd",//日期格式
                weekStart:1,
                showWeekNumbers:true,
                calendarWeeks: true,
                todayHighlight: true,
                endDate:"-1d"
            });
            global_Object.url = filter+"/paas/queryWeekClientReportByClient";
            var date = new Date();
            var week = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
            $("#datevalue").val(JqCommon.getNewDay(week));
            var oldDate = JqCommon.getNewDay(week).split("-")[0];
            var newDate = oldDate.split("/");
            global_Object.formatdate = newDate[0]+"-"+newDate[1]+"-"+newDate[2];
            global_Object.setCommunicationChart();
        }else if(data == "month"){
            $('#date_picker').datepicker('update','');
            $('#date_picker').datepicker('destroy');
            $("#date_picker").datepicker({
                language: "zh-CN",
                autoclose: true,//选中之后自动隐藏日期选择框
                format: "yyyy-mm",//日期格式
                weekStart:1,
                showWeekNumbers:true,
                startView: 'year',
                minViewMode:1,
                endDate:new Date()
            });
            global_Object.url = filter+"/paas/queryMonthClientReportByClient";
            var date = new Date();
            var month = date.getFullYear()+"-"+(date.getMonth()+1);
            $("#date_picker").datepicker('update',month);
            global_Object.formatdate = month +"-01";
            global_Object.setCommunicationChart();
        }
    },

    queryTableData:function(){
        var datas = {clientAppName:global_Object.flname,serverAppName:global_Object.clientName,date:global_Object.formatdate,status:global_Object.status};

        JqAjax.postByDefaultErrorCatch(global_Object.url,datas,function(data){
            global_Object.totalSize = data.totalSize;
            global_Object.tableData = data.transactionStatisticDatas;
            if(global_Object.totalSize>0){
                global_Object.setTable();
            }else{
                $("#fTable tbody").html('<tr class="odd"><td valign="top" colspan="13" class="dataTables_empty">表中数据为空</td></tr>');
            }
        });
    },
    setTable: function () {
        var alltr = function (length, i, data) {
            var tr = '<tr data-transactiontypeid="'+data.transactionTypeId+'" data-serveripaddress="'+data.serverIpAddress + '" data-serverappid="'+data.serverAppId +'">';
            if(i==0){
                tr+='<td rowspan='+length+' class="vam">'+data.serverAppName+'</td>';
            }
            tr += '<td style="padding-left: 0px">' + data.transactionTypeName + '</td>';
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
            tr += '<td  style="padding-left: 30px"><i class="fa  fa-bar-chart-o cp" onclick="global_Object.queryPic(this)"></i></td>';
            tr += '</tr>';
            return tr;
        };

        var html = [],appName=[];
        var StatisticDatas = global_Object.tableData;
        var length = StatisticDatas.length;
        for (var i=0;i<length;i++){
            appName.push(StatisticDatas[i].serverAppName)
        }
        var nameList = removeDuplicated(appName);
        for (var m=0;m<nameList.length;m++){
            var app=nameList[m];
            var dataList=[];
            for (var n=0;n<length;n++){
                var data=StatisticDatas[n].serverAppName;
                if (app == data){
                    dataList.push(StatisticDatas[n])
                }
            }
            if (dataList != null && dataList.length>0){
                $.each(dataList,function (i,v) {
                    html.push(alltr(dataList.length,i,v));
                })
            }
        }
        $("#fTable tbody").html(html.join(""));
        global_Object.setHeight();

        function removeDuplicated(ar) {
            var ret = [];

            ar.forEach(function(e, i, ar) {
                if (ar.indexOf(e) === i) {
                    ret.push(e);
                }
            });
            return ret;
        }

    },
    openPostFalse:function(obj){
        global_Object.value = $("#datevalue").val();
        var url = contextPath+"/view/paas/clienthistory__serverdetailedhistory";

        var datas={
            "transactionTypeId":$(obj).parents("tr").data("transactiontypeid"),
            "serverIpAddress":"",
            "serverAppId":$(obj).parents("tr").data("serverappid")==undefined?"":$(obj).parents("tr").data("serverappid"),
            "type":(global_Object.type==undefined?"":global_Object.type),
            value:global_Object.value,
            "clientAppId":global_Object.flname,
            "clientIpAddress":"",
            "status":"执行失败",
            "historyPageType":"client",
            "dateValue":global_Object.formatdate
        };
        JqCommon.openPostWindow(url,datas);
    },
    queryPic: function (obj) {
        $("#echart").css("width", $("#picEdit").width() * 0.6 - 30);
        $("#picEdit").modal("show");

        var url = "", unit = "天";
        if(global_Object.type=="day"){
            url =filter+"/paas/queryDayTransactionTypeCallTimesReportByClient";
            unit = "时";
        }
        else if(global_Object.type=="week"){
            url =filter+"/paas/queryWeekTransactionTypeCallTimesReportByClient";
        }
        else if(global_Object.type=="month"){
            url =filter+"/paas/queryMonthTransactionTypeCallTimesReportByClient";
        }
        var datas = {clientAppName: global_Object.flname,transactionTypeName:$(obj).parents("tr").data("transactiontypeid"),serverAppName:$(obj).parents("tr").data("serverappid"),date:global_Object.formatdate};

        JqAjax.postByDefaultErrorCatch(url, datas, function (data) {
            var option = JqCommon.getCartogram(unit,data);
            var myChart = echarts.init(document.getElementById("echart"));
            myChart.setOption(option);
        });
    },
    openPostAvg:function(obj){
        global_Object.value = $("#datevalue").val();
        var url = contextPath+"/view/paas/clienthistory__serverstephistory";
        var datas={
            "transactionTypeId":$(obj).parents("tr").data("transactiontypeid"),
            "serverIpAddress":$(obj).parents("tr").data("serveripaddress")==undefined?"":$(obj).parents("tr").data("serveripaddress"),
            "clientAppId":global_Object.flname,
            "type":(global_Object.type==undefined?"":global_Object.type),
            "value":global_Object.value,
            "historyPageType":"client",
            "dateValue":global_Object.formatdate,
            "serverAppId":$(obj).parents("tr").data("serverappid")==undefined?"":$(obj).parents("tr").data("serverappid")
        };
        JqCommon.openPostWindow(url,datas);
    },
    openPostTotalCount:function(obj){
        global_Object.value = $("#datevalue").val();
        var url = contextPath+"/view/paas/clienthistory__serverdetailedhistory";
        var datas={
            "transactionTypeId":$(obj).parents("tr").data("transactiontypeid"),
            "serverIpAddress":"",
            "serverAppId":$(obj).parents("tr").data("serverappid")==undefined?"":$(obj).parents("tr").data("serverappid"),
            "type":(global_Object.type==undefined?"":global_Object.type),
            value:global_Object.value,
            "clientAppId":global_Object.flname,
            "clientIpAddress":"",
            "status":"",
            "historyPageType":"client",
            "dateValue":global_Object.formatdate
            };
        JqCommon.openPostWindow(url,datas);
    }

}
