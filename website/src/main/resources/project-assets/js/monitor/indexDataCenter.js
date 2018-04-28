$(document).ready(function () {
    // (function () {
    //     Array.prototype.split = function (row) {
    //         var size = this.length;
    //         var j = Math.round(size / row);
    //         var result = [];
    //         for (var i = 0; i < j; i++) {
    //             result.push(this.slice(i * row, i * row + row));
    //         }
    //         return result;
    //     };
    //     Node = window.Node = function (config) {
    //         this.id = config.id;
    //         this.name = config.name;
    //
    //         this.x = this.y = 0;
    //         this.symbol = Node.path.sys;
    //         this.symbolSize = 30;
    //         this.color = Node.colors.success
    //
    //         this.label = {
    //             normal: {
    //                 show: true,
    //                 position: 'bottom'
    //             },
    //             emphasis: {
    //                 position: 'bottom'
    //             }
    //         };
    //
    //         this.itemStyle = {
    //             normal: {
    //                 color: this.color,
    //                 borderWidth: 1
    //             }
    //         };
    //
    //     };
    //
    //     //黄金分割点
    //     //常量
    //     Node.h = 0.618;
    //     Node.colors = {
    //         cdc: '#3AA7F2',
    //         success: '#009900'
    //     };
    //
    //     Node.path = {
    //         sys: 'path://M0 64v640h1024v-640h-1024zM960 640h-896v-512h896v512zM672 768h-320l-32 128-64 64h512l-64-64z'
    //     };
    //
    //     //坐标生成
    //     Node.avgLipes = function (centerPoint, w, h, num) {
    //         var centerX = centerPoint[0];
    //         var centerY = centerPoint[1];
    //         var radiusOuterX = w / 2;
    //         var radiusOuterY = h / 2;
    //         var result = [];
    //         for (var i = 0; i < num; i++) {
    //             var angleOuter = (2 * Math.PI / num) * i;
    //             var x = radiusOuterX * Math.sin(angleOuter);
    //             var y = radiusOuterY * Math.cos(angleOuter);
    //             x += centerX;
    //             y += centerY;
    //             result.push([x, y]);
    //         }
    //         return result;
    //     };
    //
    //     Node.prototype.setPoint = function (point) {
    //         this.x = point[0];
    //         this.y = point[1];
    //     };
    //
    //     Node.prototype.getPoint = function () {
    //         {
    //             return [this.x, this.y];
    //         }
    //     };
    //
    //     Node.prototype.getLinks = function () {
    //         var links = [];
    //         var linkFrom = null;
    //         // var linkTo = null;
    //         linkFrom = {
    //             source: this.id,
    //             target: "CDC",
    //             symbol: ["none", 'arrow'],
    //             symbolSize: [0, 12],
    //             color: '#FFCC00'
    //         };
    //         linkFrom.lineStyle = {
    //             normal: {
    //                 color: this.color, //线条颜色跟随节点颜色
    //                 type: 'dotted',
    //                 opacity: 0.4,
    //                 curveness: 0
    //             }
    //         };
    //         links.push(linkFrom);
    //         return links;
    //     };
    // })();

    /*$(".page-content").slimScroll({height:$(".page-content").height(0)});*/
    global_Object.initDomEvent();

    // global_Object.interval_current = setInterval(function () {
    //     global_Object.countByOverView();
    // }, 6000);
    global_Object.countByOverView();
    global_Object.queryTrendChartData();
    global_Object.queryClientTypeChartData();
    global_Object.querySysData("ODR");
    global_Object.querySysData("ODS");
    global_Object.querySysData("CDR");
    global_Object.querySysData("WDK");
});
var global_Object = {
    interval_current: null,
    countByOverView: function () {
        $.post(contextPath + "/ajax/center/overview/countByOverView", null, function (response) {
            var data = response.data;
            var cdrYear = data.cdrYear || data.cdrYear == 0 ? data.cdrYear : "**";
            var cdrSyss = data.cdrSyss ? data.cdrSyss : [];
            var lcwdCount = data.lcwdCount || data.lcwdCount == 0 ? data.lcwdCount : "**";
            var gxwdCount = data.gxwdCount || data.gxwdCount == 0 ? data.gxwdCount : "**";

            $('div.cdrYear').html(cdrYear);
            $('div.cdrSysNum').html(cdrSyss.length);
            $('div.cdrblwdNum').html(lcwdCount);
            $('div.gxwdNum').html(gxwdCount);

            var html = "";
            for (var i = 0, count = cdrSyss.length; i < count; i++) {
                html += '<div style="margin-left:20px;margin-top: 10px; text-align: left;">' + cdrSyss[i]["sourcename"] + '</div>';
            }
            $('div.cdcSyssDIV').html(html);

            // //新增系统交互图表
            // var chartDom = $('#chart-sys')[0];
            // var mychart = echarts.init(chartDom);
            // //画布宽高
            // var c_w = mychart.getWidth();
            // var c_h = mychart.getHeight();
            // var nodes = [];
            // var links = [];
            // var cdcNodeConfig = {
            //     id: 'CDC',
            //     name: 'CDC'
            // };
            // var cdcNode = new Node(cdcNodeConfig);
            // for (var i = 0; i < cdrSyss.length; i++) {
            //     var data = cdrSyss[i];
            //     var node = new Node({id: data.sourcename, name: data.sourcename});
            //     nodes.push(node);
            //     links = links.concat(node.getLinks());
            // }
            //
            // // var esbNode = nodes.shift();
            // //中心
            // var centerX = c_w / 2;
            // var centerY = c_h / 2;
            // var centerPoint = [centerX, centerY];
            // cdcNode.setPoint(centerPoint);
            //
            // //2个圈
            // var oneLayers = [];
            // var twoLayers = [];
            // var max = nodes.length;
            // if (max <= 8) {
            //     oneLayers = nodes;
            // } else {
            //     twoLayers = nodes.slice(0, parseInt(Node.h * max));
            //     oneLayers = nodes.slice(twoLayers.length);
            // }
            //
            //
            // var points = Node.avgLipes(centerPoint, 350, 125, oneLayers.length);
            // for (var i = 0; i < oneLayers.length; i++) {
            //     oneLayers[i].setPoint(points[i]);
            // }
            //
            // var points = Node.avgLipes(centerPoint, 650, 250, twoLayers.length);
            // for (var i = 0; i < twoLayers.length; i++) {
            //     twoLayers[i].setPoint(points[i]);
            //     twoLayers[i].label.normal.position = 'left';
            //     twoLayers[i].label.emphasis.position = 'left';
            // }
            // var option = {
            //     animation: true,
            //     animationDurationUpdate: 1500,
            //     animationEasingUpdate: 'linear ',//quinticInOut
            //     series: [
            //         {
            //             name: '',
            //             type: 'graph',
            //             layout: 'none',
            //             data: [cdcNode].concat(nodes),
            //             links: links,
            //             effect: {
            //                 show: true,
            //                 period: 6,
            //                 trailLength: 0
            //             },
            //             label: {
            //                 normal: {
            //                     show: true,
            //                     position: 'left'
            //                 },
            //                 emphasis: {
            //                     position: 'left'
            //                 }
            //             },
            //             focusNodeAdjacency: true
            //         }
            //     ]
            // };
            // console.log(JSON.stringify(option))
            // mychart.setOption(option);

        }, "json", {async: false});
    },
    queryTrendChartData: function () {
        $.post(contextPath + "/ajax/center/overview/queryTrendChartData", null, function (response) {
            var datas = response.data;
            var timeline = [];
            var count = [];
            for (var i = 0; i < datas.length; i++) {
                var item = datas[i];
                var time = item.xaxis;
                timeline.push(time);
                count.push(item.nincredata);
            }
            var option = {
                title: {},
                color: CommonFunc.echart.colorSet,
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
                grid: {
                    top: 50,
                    left: 80,
                    right: 80,
                    bottom: 80
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
                    name: '抽取量',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: count,
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值', symbolSize: [100, 100]},
                            {type: 'min', name: '最小值', symbolSize: [80, 80]}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                }]
            };
            // console.log(count,timeline);
            var trendLineEChart = echarts.init($("#trendLineEChartDIV")[0]);
            trendLineEChart.setOption(option);
        }, "json", {async: false});
    },

    queryClientTypeChartData: function () {
        $.post(contextPath + "/ajax/center/overview/queryClientTypeChartData", null, function (response) {
            var datas = response.data;
            var legendData = [];
            var countData = [];
            for (var i = 0; i < datas.length; i++) {
                var item = datas[i];
                var name = item.sourcename;
                // legendData.push(name);
                var nincredata = item.nincredata;
                countData.push({
                    name: name,
                    value: nincredata
                });
            }
            var option = {
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                color: CommonFunc.echart.colorSet,
                // legend: {
                //     right:25,
                //     top:10,
                //     orient:'vertical',
                //     data:legendData
                // },
                series: [
                    {
                        name: '抽取量',
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
            var trendPierEChart = echarts.init($("#trendPierEChartDIV")[0]);
            trendPierEChart.setOption(option);
        }, "json", {async: false});
    },

    querySysData: function (sys) {
        var data = {sys: sys};
        data = {data: JSON.stringify(data)};
        $.post(contextPath + "/ajax/center/overview/querySysData", data, function (response) {
            var data = response.data;
            var ip = data.ip;
            if (ip == null) ip = "";
            var dbType = data.dbType;
            if (dbType == null) dbType = "";

            var dataNumber = data.dataNumber;
            if (dataNumber && dataNumber.length > 0) {
                dataNumber = new Number(dataNumber);
            } else {
                dataNumber = 0;
            }

            var dataNumberSuccess = data.dataNumberSuccess;
            if (dataNumberSuccess && dataNumberSuccess.length > 0) {
                dataNumberSuccess = new Number(dataNumberSuccess);
            } else {
                dataNumberSuccess = 0;
            }

            var dataNumberFailure = data.dataNumberFailure;
            if (dataNumberFailure && dataNumberFailure.length > 0) {
                dataNumberFailure = new Number(dataNumberFailure);
            } else {
                dataNumberFailure = 0;
            }

            var cpuPercent = data.cpuPercent;
            if (cpuPercent && cpuPercent.length > 0) {
                cpuPercent = new Number(cpuPercent);
            } else {
                cpuPercent = 0;
            }

            var memoryPercent = data.memoryPercent;
            if (memoryPercent && memoryPercent.length > 0) {
                memoryPercent = new Number(memoryPercent);
            } else {
                memoryPercent = 0;
            }

            var diskSizePercent = data.diskSizePercent;
            if (diskSizePercent && diskSizePercent.length > 0) {
                diskSizePercent = new Number(diskSizePercent);
            } else {
                diskSizePercent = 0;
            }

            var dbSizePercent = data.dbSizePercent;
            if (dbSizePercent && dbSizePercent.length > 0) {
                dbSizePercent = new Number(dbSizePercent);
            } else {
                dbSizePercent = 0;
            }


            var $sysdivid = "-rect-" + sys.toLowerCase();
            if (sys == "ODS") {
                $sysdivid = ".big" + $sysdivid;
            } else {
                $sysdivid = ".mid" + $sysdivid;
            }
            var $sys = $($sysdivid);
            console.log($sys);
            $($sys).find('.ip>.context').text(ip);
            $($sys).find('.dbtype>.context').text(dbType);

            if (sys == "ODR") {
                $($sys).find('.jbsl>.context').text(dataNumber);
            } else {
                $($sys).find('.cq>.context').text(dataNumber);
                $($sys).find('.cg>.context').text(dataNumberSuccess);
                $($sys).find('.sb>.context').text(dataNumberFailure);
            }

            // cpuPercent = 80;
            // memoryPercent = 20;
            // diskSizePercent = 60;
            // dbSizePercent = 10;
            var $easypie = $($sys).find('.easy-pie-chart.cpu .number');
            $easypie.attr('data-percent', cpuPercent);
            $easypie.find('span').text(cpuPercent);
            $($easypie).data('easyPieChart').update(cpuPercent);

            $easypie = $($sys).find('.easy-pie-chart.memory .number');
            $easypie.attr('data-percent', memoryPercent);
            $easypie.find('span').text(memoryPercent);
            $($easypie).data('easyPieChart').update(memoryPercent);

            $easypie = $($sys).find('.easy-pie-chart.disk .number');
            $easypie.attr('data-percent', diskSizePercent);
            $easypie.find('span').text(diskSizePercent);
            $($easypie).data('easyPieChart').update(diskSizePercent);

            $easypie = $($sys).find('.easy-pie-chart.database .number');
            $easypie.attr('data-percent', dbSizePercent);
            $easypie.find('span').text(dbSizePercent);
            $($easypie).data('easyPieChart').update(dbSizePercent);
        }, "json", {async: false});
    },

    pageResize: function () {
        var $rect = $('.cdc-rect');
        var maxHeight = 0;
        $($rect).children().each(function () {
            var height = $(this).height();
            if (height > maxHeight) maxHeight = height;
        });
        $($rect).children().each(function () {
            $(this).height(maxHeight);
        });
    },

    initDomEvent: function () {
        $('.easy-pie-chart>.number').easyPieChart(global_Object.easyPieConfig);

        $('.cdc-rect').resize(function () {
            global_Object.pageResize();
        });
        global_Object.pageResize();


        $("#sh_zk").off().on("click", function () {
            var $i = $(this).find("i");
            //$("#condition .portlet-body").toggle(600);
            if ($i.hasClass("fa-angle-down")) {
                $("#mychart").slideUp("slow");
                $i.removeClass("fa-angle-down").addClass("fa-angle-up");
                $("#qst").fadeIn();
            }
            else {
                $("#qst").fadeOut();
                $("#mychart").slideDown("slow");
                $i.removeClass("fa-angle-up").addClass("fa-angle-down");
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
    easyPieConfig: {
        size: 50,
        barColor: function (percent) {
            return (percent < 50 ? App.getBrandColor('green') : percent < 70 ? App.getBrandColor('yellow') : App.getBrandColor('red'));
        }
    }
}