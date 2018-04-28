/**
 * Created by xuehao on 2017/11/30.
 */

var CpuMemory = {
    cpuData:[],
    initDomEvent: function () {
        CpuMemory.timeIntervalCpu();
        CpuMemory.initMemory();
        CpuMemory.initDisk();
    },
    //定时设置CPU折线图
    timeIntervalCpu: function () {
        CpuMemory.cpuData = [];
        var myDate;
        for(var i = 60; i>0; i--) {
            // myDate = DateUtils.addSeconds(-i);
            // myDateString = DateUtils.format(DateUtils.formatBase, myDate);
            // console.log(myDate);    //测试
            // CpuMemory.cpuData.push({
            //     name : myDate,
            //     value : [myDate, parseInt(30 + Math.random()*30)]
            // });

            CpuMemory.cpuData.push({});
        }

        //定时获取CPU负载信息（由于影响性能，暂时注释）
        // var chartCpu = CpuMemory.initCpu();
        // setInterval(function () {
        //     var firstData = CpuMemory.cpuData.shift();
        //     myDate = DateUtils.format(DateUtils.formatBase, new Date());
        //     CpuMemory.cpuData.push({
        //         name : myDate,
        //         value : [myDate, parseInt(30 + Math.random()*30)]
        //     });
        //
        //     chartCpu.setOption({
        //         series: [{
        //             data: CpuMemory.cpuData
        //         }]
        //     });
        // }, 5000);
    },
    //初始化折线图：CPU
    initCpu: function () {
        var option = {
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    params = params[0];
                    var date = new Date(params.name);
                    return date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear() + ' : ' + params.value[1];
                },
                axisPointer: {
                    animation: false
                }
            },
            grid: {
                left: 15,
                right: 40,
                bottom: 15,
                top: 15,
                containLabel: true
            },
            xAxis: {
                type: 'time',
                splitLine: {
                    show: false
                }
            },
            yAxis: {
                type: 'value',
                boundaryGap: [0, '100%'],
                splitLine: {
                    show: true
                }
            },
            series: [{
                name: '模拟数据',
                type: 'line',
                showSymbol: false,
                hoverAnimation: false,
                data: CpuMemory.cpuData
            }]
        };

        var trendLineEChart = echarts.init($("#lineCpuMemory")[0]);
        trendLineEChart.setOption(option);
        return trendLineEChart;
    },
    //初始化饼图：堆内存
    initMemory: function () {
        var option = {
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data:['可用','平台','其他']
                },
                series: [
                    {
                        name:'访问来源',
                        type:'pie',
                        radius: ['50%', '70%'],
                        avoidLabelOverlap: false,
                        label: {
                            normal: {
                                show: false,
                                position: 'center'
                            },
                            emphasis: {
                                show: true,
                                textStyle: {
                                    fontSize: '30',
                                    fontWeight: 'bold'
                                }
                            }
                        },
                        labelLine: {
                            normal: {
                                show: false
                            }
                        },
                        data:[
                            {value:335, name:'可用'},
                            {value:310, name:'平台'},
                            {value:234, name:'其他'}
                        ]
                    }
                ]
            };

        var trendLineEChart = echarts.init($("#pieMemory")[0]);
        trendLineEChart.setOption(option);
    },
    //初始化饼图：磁盘空间
    initDisk: function () {
        var option = {
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                x: 'left',
                data:['可用','平台','其他']
            },
            series: [
                {
                    name:'访问来源',
                    type:'pie',
                    radius: ['50%', '70%'],
                    avoidLabelOverlap: false,
                    label: {
                        normal: {
                            show: false,
                            position: 'center'
                        },
                        emphasis: {
                            show: true,
                            textStyle: {
                                fontSize: '30',
                                fontWeight: 'bold'
                            }
                        }
                    },
                    labelLine: {
                        normal: {
                            show: false
                        }
                    },
                    data:[
                        {value:435, name:'可用'},
                        {value:210, name:'平台'},
                        {value:234, name:'其他'}
                    ]
                }
            ]
        };

        var trendLineEChart = echarts.init($("#pieDisk")[0]);
        trendLineEChart.setOption(option);
    }
}