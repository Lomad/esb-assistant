/**
 * Created by xuehao on 2017/11/30.
 */

var LineProvideConsume = {
    //绑定系统的提供与消费数量
    initDomEvent: function (resp) {
        // console.log(resp);  //测试

        if(resp.success) {
            //遍历数据，转为目标格式
            data = resp.data;
            var xData = [];
            var yDataProvide = [];
            var yDataConsume = [];
            var item;
            for(var i in data) {
                item = data[i];
                xData.push(item.time);
                yDataProvide.push(item.totalCount);
                yDataConsume.push(item.failCount);
            }
            //设置chart的配置
            var option = {
                title: {
                    show: false
                },
                legend: {
                    show: false
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        label: {
                            backgroundColor: '#6a7985'
                        }
                    }
                },
                grid: {
                    left: 15,
                    right: 15,
                    bottom: 15,
                    top: 15,
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        // data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                        data: xData
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '提供次数',
                        type: 'line',
                        stack: '总量',
                        // data: [120, 132, 101, 134, 90, 230, 210]
                        data: yDataProvide
                    },
                    {
                        name: '消费次数',
                        type: 'line',
                        stack: '总量',
                        // data: [220, 182, 191, 234, 290, 330, 310]
                        data: yDataConsume
                    }
                ]
            };

            var trendLineEChart = echarts.init($("#lineProvideConsume")[0]);
            trendLineEChart.setOption(option);
        }
    },

}