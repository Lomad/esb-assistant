$(document).ready(function () {
    /*$(".page-content").slimScroll({height:$(".page-content").height(0)});*/
    global_Object.initDomEvent();

    // global_Object.interval_current = setInterval(function () {
    //     global_Object.countByOverView();
    // }, 6000);
    global_Object.countByOverView("MDM");
    global_Object.countByOverView("EMPI");
    global_Object.countByOverView("WDK");
    global_Object.queryPatRegdataByEMPI();
    global_Object.queryAreaDataByEMPI();
    global_Object.queryRegdataByWDK();
    global_Object.queryCategoryDatasetByWDK();
});
var global_Object = {
    interval_current: null,
    countByOverView: function (sys) {
        var data = {sys: sys};
        data = {data: JSON.stringify(data)};
        $.post(contextPath + "/ajax/basicapp/overview/countByOverView", data, function (response) {
            var data = response.data;
            if (data) {
                if (sys == "MDM") {
                   var  $this = $('.mdm');
                    var bzcount = data.bzcount || data.bzcount == 0 ? data.bzcount : "**";
                    var yycount = data.yycount || data.yycount == 0 ? data.yycount : "**";
                    var syscount = data.syscount || data.syscount == 0 ? data.syscount : "**";
                    var noticecount = data.noticecount || data.noticecount == 0 ? data.noticecount : "**";
                    var regdata = data.regdata || data.regdata == 0 ? data.regdata : "**";
                    var xz = data.xz || data.xz == 0 ? data.xz : "**";
                    var xg = data.xg || data.xg == 0 ? data.xg : "**";
                    var cg = data.cg || data.cg == 0 ? data.cg : "**";
                    var sb = data.sb || data.sb == 0 ? data.sb : "**";

                    $($this).find('.bzcount').html(bzcount);
                    $($this).find('.yycount').html(yycount);
                    $($this).find('.syscount').html(syscount);
                    $($this).find('.notice').html(noticecount);
                    $($this).find('.regdata').html(regdata);

                    $($this).find('.xz').html(xz);
                    $($this).find('.xg').html(xg);
                    $($this).find('.cg').html(cg);
                    $($this).find('.sb').html(sb);

                    //构建饼图
                    //新增修改字典
                    var legendData = ["新增", "修改"];
                    var countData = [{name: '新增', value: xz}, {name: '修改', value: xg}];
                    var option = {
                        tooltip: {
                            trigger: 'item',
                            formatter: "{a} <br/>{b}: {c} ({d}%)"
                        },
                        color:CommonFunc.echart.colorSet,
                        // legend: {
                        //     right:25,
                        //     top:10,
                        //     orient:'vertical',
                        //     data:legendData
                        // },
                        series: [
                            {
                                type: 'pie',
                                selectedMode: 'single',
                                radius: ['30%', '60%'],
                                label: {
                                    normal: {
                                        position: 'inner'
                                    }
                                },
                                data: countData
                            }
                        ]
                    };
                    var trendPierEChart = echarts.init($($this).find(".xzxgEchartDIV")[0]);
                    trendPierEChart.setOption(option);


                    //新增修改字典
                    var legendData = ["成功", "失败"];
                    var countData = [{name: '成功', value: cg}, {name: '失败', value: sb}];
                    var option = {
                        tooltip: {
                            trigger: 'item',
                            formatter: "{a} <br/>{b}: {c} ({d}%)"
                        },
                        color:CommonFunc.echart.colorSet,
                        // legend: {
                        //     right:25,
                        //     top:10,
                        //     orient:'vertical',
                        //     data:legendData
                        // },
                        series: [
                            {
                                type: 'pie',
                                selectedMode: 'single',
                                radius: ['30%', '60%'],
                                label: {
                                    normal: {
                                        position: 'inner'
                                    }
                                },
                                data: countData
                            }
                        ]
                    };
                    var trendPierEChart = echarts.init($($this).find(".cgsbEchartDIV")[0]);
                    trendPierEChart.setOption(option);
                } else if (sys == "EMPI") {
                    var $this = $('.empi');
                    var today_patient_count = data.today_patient_count || data.today_patient_count == 0 ? data.today_patient_count : "**";
                    var today_patient_merge_count = data.today_patient_merge_count || data.today_patient_merge_count == 0 ? data.today_patient_merge_count : "**";
                    var today_patient_rate = data.today_patient_rate || data.today_patient_rate == 0 ? data.today_patient_rate : "**";
                    var history_patient_count = data.history_patient_count || data.history_patient_count == 0 ? data.history_patient_count : "**";
                    var history_patient_merge_count = data.history_patient_merge_count || data.history_patient_merge_count == 0 ? data.history_patient_merge_count : "**";
                    var history_patient_rate = data.history_patient_rate || data.history_patient_rate == 0 ? data.history_patient_rate : "**";


                    $($this).find('.today_patient_count').html(today_patient_count);
                    $($this).find('.today_patient_merge_count').html(today_patient_merge_count);
                    $($this).find('.today_patient_rate').html(today_patient_rate);
                    $($this).find('.history_patient_count').html(history_patient_count);
                    $($this).find('.history_patient_merge_count').html(history_patient_merge_count);
                    $($this).find('.history_patient_rate').html(history_patient_rate);
                } else if (sys == "WDK") {
                    var $this = $('.wdk');
                    var gxwdCount = data.gxwdCount || data.gxwdCount == 0 ? data.gxwdCount : "**";
                    var datasetCount = data.sjjCount || data.sjjCount == 0 ? data.sjjCount : "**";
                    var sysCount = data.sysCount || data.sysCount == 0 ? data.sysCount : "**";

                    $($this).find('.gxwdCount').html(gxwdCount);
                    $($this).find('.datasetCount').html(datasetCount);
                    $($this).find('.sysCount').html(sysCount);
                }
            }
        }, "json", {async: false});
    },
    queryPatRegdataByEMPI: function () {
        var type = global_Object.getEMPISelectTimeType();
        var data = {type: type};
        data = {data: JSON.stringify(data)};
        var $this = $('.empi');
        $.post(contextPath + "/ajax/basicapp/overview/queryPatRegdataByEMPI", data, function (response) {
            var datas = response.data;
            var timeline = [];
            var countNums = [];
            var mergeNums = [];
            var newNums = [];
            var legendData = ["患者总数", "新增患者总数", "合并患者总数"];
            for (var idx in datas) {
                var item = datas[idx];
                var count_date = item.count_date;
                var num_count = item.num_count;
                var num_merge = item.num_merge;
                var num_new = item.num_new;
                timeline.push(count_date);
                countNums.push(num_count);
                mergeNums.push(num_merge);
                newNums.push(num_new);
            }
            var option = {
                title: {},
                color:CommonFunc.echart.colorSet,
                tooltip: {
                    trigger: 'axis',
                    // formatter: function (params) {
                    //     console.log(params);
                    //     return "";
                    // },
                    axisPointer: {
                        animation: false
                    }
                },
                legend: {
                    right: 25,
                    top: 10,
                    //orient:'vertical',
                    data: legendData
                },
                grid: {
                    left: 80,
                    right: 40,
                    bottom:80
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    splitLine: {
                        show: false
                    },
                    data: timeline
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, '100%'],
                    splitLine: {
                        show: false
                    }
                },
                series: [{
                    name: '患者总数',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: countNums,
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值', symbolSize:[80, 80]},
                            {type: 'min', name: '最小值', symbolSize:[60, 60]}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                }, {
                    name: '新增患者总数',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: newNums
                }, {
                    name: '合并患者总数',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: mergeNums
                }]
            };
            var trendPierEChart = echarts.init($($this).find('.countEchartDIV')[0]);
            trendPierEChart.setOption(option);
        }, "json", {async: false});
    },

    queryAreaDataByEMPI: function () {
        var type = global_Object.getEMPISelectTimeType();
        var data = {type: type};
        data = {data: JSON.stringify(data)};
        var $this = $('.empi');
        $.post(contextPath + "/ajax/basicapp/overview/queryAreaDataByEMPI", data, function (response) {
            var datas = response.data;
            var category = [];
            var countData = [];
            for (var idx in datas) {
                var item = datas[idx];
                var areaName = item.areaName;
                var areaCount = item.areaCount;
                category.push(areaName);
                countData.push(areaCount);
            }
            option = {
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    left: 80,
                    right: 40,
                    bottom:50
                },
                color:['#003366'],
                calculable: true,
                xAxis: [
                    {
                        type: 'category',
                        data: category
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '患者数',
                        type: 'bar',
                        data: countData
                    }
                ]
            };

            var trendPierEChart = echarts.init($($this).find('.areaEchartDIV')[0]);
            trendPierEChart.setOption(option);
        }, "json", {async: false});
    },

    //共享文档注册时段分析
    queryRegdataByWDK: function () {
        var type = global_Object.getWDKSelectTimeType();
        var data = {type: type};
        data = {data: JSON.stringify(data)};
        var $this = $('.wdk');
        $.post(contextPath + "/ajax/basicapp/overview/queryRegdataByWDK", data, function (response) {
            var datas = response.data;
            var timeline = [];
            var xaxisSets = [];
            var nincredataSets = [];
            for (var idx in datas) {
                var item = datas[idx];
                var xaxis = item.xaxis;
                var nincredata = item.nincredata;
                xaxisSets.push(xaxis);
                nincredataSets.push(nincredata);
            }
            var option = {
                title: {},
                tooltip: {
                    trigger: 'axis',
                    // formatter: function (params) {
                    //     console.log(params);
                    //     return "";
                    // },
                    axisPointer: {
                        animation: false
                    }
                },
                color: CommonFunc.echart.colorSet,
                // legend: {
                //     right: 25,
                //     top: 10,
                //     //orient:'vertical',
                //     data: legendData
                // },
                grid: {
                    top:30,
                    left: 80,
                    right: 50,
                    bottom:80
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    splitLine: {
                        show: false
                    },
                    data: xaxisSets
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, '100%'],
                    splitLine: {
                        show: false
                    }
                },
                series: [{
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: nincredataSets,
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值', symbolSize:[80, 80]},
                            {type: 'min', name: '最小值', symbolSize:[60, 60]}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                }]
            };
            var trendPierEChart = echarts.init($($this).find('.regdataEchartDIV')[0]);
            trendPierEChart.setOption(option);
        }, "json", {async: false});
    },

    queryCategoryDatasetByWDK: function () {
        var type = global_Object.getWDKSelectTimeType();
        var data = {type: type};
        data = {data: JSON.stringify(data)};
        var $this = $('.wdk');
        $.post(contextPath + "/ajax/basicapp/overview/queryCategoryDatasetByWDK", data, function (response) {
            var datas = response.data;
            var category = [];
            var countData = [];
            var legendData = [];
            var other = 0;
            for (var idx in datas) {
                // console.log(idx);
                var item = datas[idx];
                var sjjmc = item.sjjmc;
                var num = item.num;
                if(idx<=10){
                    category.push(sjjmc);
                    countData.push({name:sjjmc,value:num});
                } else {
                    other+=num;
                }
            }
            if(other){
                category.push("其他");
                countData.push({name:"其他",value:other});
            }
            //新增修改字典
            var option = {
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                color:CommonFunc.echart.colorSet,
                grid: {
                    top:0,
                    left: 0,
                    right: 0,
                    bottom:0
                },
                // legend: {
                //     right:25,
                //     top:10,
                //     orient:'vertical',
                //     data:legendData
                // },
                series: [
                    {
                        type: 'pie',
                        selectedMode: 'single',
                        // radius: ['30%', '60%'],
                        label: {
                            normal: {
                                // position: 'inner'
                            }
                        },
                        data: countData
                    }
                ]
            };
            var trendPierEChart = echarts.init($($this).find(".sjjEchartDIV")[0]);
            trendPierEChart.setOption(option);
        }, "json", {async: false});
    },

    initDomEvent: function () {
        $('.empi button.btn-time').click(function () {
            if (!$(this).hasClass('blue')) {
                $(this).addClass('blue').siblings().removeClass('blue');
                global_Object.queryPatRegdataByEMPI();
                global_Object.queryAreaDataByEMPI();
            }
        });

        $('.wdk button.btn-time').click(function () {
            if (!$(this).hasClass('blue')) {
                $(this).addClass('blue').siblings().removeClass('blue');
                global_Object.queryRegdataByWDK();
                global_Object.queryCategoryDatasetByWDK();
            }
        });
    },

    language: {
        "sProcessing": "处理中...",
        "sLengthMenu": "显示 _MENU_ 项结果",
        "sZeroRecords": "没有匹配结果",
        "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
        "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
        /*"sInfoFiltered": "(由 _MAX_ 项结果过滤)",*/
        "sInfoFiltered": "",
        "sInfoPostFix": "",
        "sSearch": "搜索:",
        "sUrl": "",
        "sEmptyTable": "表中数据为空",
        "sLoadingRecords": "载入中...",
        "sInfoThousands": ",",
        "oPaginate": {
            "sFirst": "首页",
            "sPrevious": "上页",
            "sNext": "下页",
            "sLast": "末页"
        },
        "oAria": {
            "sSortAscending": ": 以升序排列此列",
            "sSortDescending": ": 以降序排列此列"
        }
    },
    getEMPISelectTimeType: function () {
        return $('.empi button.btn-time.blue').children('input[name="type"]').val();
    },
    getWDKSelectTimeType: function () {
        return $('.wdk button.btn-time.blue').children('input[name="type"]').val();
    }
}