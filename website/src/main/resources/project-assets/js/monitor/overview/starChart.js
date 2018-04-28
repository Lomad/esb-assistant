/**
 * Created by xuehao on 2017/08/26.
 */

var ajaxReqPre = "/ajax/overview/";

var StarChart = {
    idChartSkin: '#starChart',
    ajaxQueryGroupByAppInfo: contextPath + ajaxReqPre + 'queryGroupByAppInfo',
    showErrorLower : 0, //监控概览显错下限值
    //常量
    ESB_ID : "ESB",
    /**
     * @param params {
     * callback,  //回调函数
     * circleSize,  //取值必须不小于1，可以为小数，等于1，表示圆形，其他值为椭圆，数字越大，椭圆横向直径越大
     * pageSize,  //返回的最大记录数
     * type    //时间类型：21 - 今天
     * }
     */
    initDomEvent: function (params) {
        // console.log('init');  //测试

        //初始化Node
        StarChart.initNode();

        //绑定星状图
        if (CommonFunc.isEmpty(params.circleSize)) {
            params.circleSize = 1;
        }
        CommonFunc.ajaxPostJson(StarChart.ajaxQueryGroupByAppInfo, params, function (resp) {
            // console.log(resp);  //测试

            StarChart.bindChart(resp, params);
        });
    },
    //初始化Node对象
    initNode: function () {
        Array.prototype.split = function (row) {
            var size = this.length;
            var j = Math.round(size / row);
            var result = [];
            for (var i = 0; i < j; i++) {
                result.push(this.slice(i * row, i * row + row));
            }
            return result;
        };
        Node = window.Node = function (config) {
            this.id = config.app.id;
            this.name = config.app.name;
            this.server = config.server || {totalCount: 0, failCount: 0};
            this.consumer = config.consumer || {totalCount: 0, failCount: 0};
            this.totalCount = this.server.totalCount + this.consumer.totalCount;
            this.failCount = this.server.failCount + this.consumer.failCount;
            this.server.successCount = this.server.totalCount - this.server.failCount;
            this.consumer.successCount = this.consumer.totalCount - this.consumer.failCount;
            this.successCount = this.totalCount - this.failCount;

            this.x = this.y = 0;

            if (this.id.toUpperCase() == StarChart.ESB_ID) {
                this.symbol = Node.path.esb;
                this.symbolSize = 60;
                this.color = Node.colors.esb;
                this.label = {
                    normal: {
                        show: true,
                        position: 'inside'
                    },
                    emphasis: {
                        position: 'inside'
                    }
                };
            } else {
                this.symbol = Node.path.sys;
                if (this.totalCount < Node.values[0]) {
                    this.symbolSize = Node.symbolSizes[0];
                } else if (this.totalCount < Node.values[1]) {
                    this.symbolSize = Node.symbolSizes[1];
                } else {
                    this.symbolSize = Node.symbolSizes[2];
                }

                if (this.totalCount <= 0) {
                    this.color = Node.colors.idle;
                } else if (this.failCount == 0 || this.failCount < StarChart.showErrorLower) {
                    if (this.symbolSize == Node.symbolSizes[0]) {
                        this.color = Node.colors.success[0]
                    } else if (this.symbolSize == Node.symbolSizes[1]) {
                        this.color = Node.colors.success[1]
                    } else {
                        this.color = Node.colors.success[2]
                    }
                } else {
                    this.color = Node.colors.fail;
                }

                this.label = {
                    normal: {
                        show: true,
                        position: 'bottom'
                    },
                    emphasis: {
                        position: 'bottom'
                    }
                };
            }

            this.itemStyle = {
                normal: {
                    color: this.color,
                    borderWidth: 1
                }
            };

        };

        //黄金分割点
        //常量
        Node.h = 0.618;
        // Node.values = [500, 2000];
        Node.values = [999999999, 999999999];   //设为很大，效果是所有的圆球都是一样大
        Node.symbolSizes = [30, 40, 50];
        Node.colors = {
            esb: '#3AA7F2',
            success: ['#009900', '#9FDABF', '#a6ce39'],
            fail: ['#FF0000'],
            idle: ['#999999']   //闲置（暂无请求）
        };

        Node.path = {
            //电脑图案
            // sys: 'path://M0 64v640h1024v-640h-1024zM672 768h-320l-32 128-64 64h512l-64-64z',
            // selectsys: 'path://M0 64v640h1024v-640h-1024zM672 768h-320l-32 128-64 64h512l-64-64zM600 500v128h-128v-128h-128v-128h128v-128h128v128h128v128h-128z',
            // esb: 'path://M0 64v640h1024v-640h-1024zM672 768h-320l-32 128-64 64h512l-64-64z'

            //圆形图案
            sys: 'path://M512 0c-282.77 0-512 229.23-512 512s229.23 512 512 512 512-229.23 512-512-229.23-512-512-512zM512 640c-70.692 0-128-57.306-128-128 0-70.692 57.308-128 128-128 70.694 0 128 57.308 128 128 0 70.694-57.306 128-128 128zM512 0l-320 512 320 512 320-512z',
            selectsys: 'path://M512 0c-282.77 0-512 229.23-512 512s229.23 512 512 512 512-229.23 512-512-229.23-512-512-512zM512 640c-70.692 0-128-57.306-128-128 0-70.692 57.308-128 128-128 70.694 0 128 57.308 128 128 0 70.694-57.306 128-128 128z',
            esb: 'path://M512 0c-282.77 0-512 229.23-512 512s229.23 512 512 512 512-229.23 512-512-229.23-512-512-512zM512 640c-70.692 0-128-57.306-128-128 0-70.692 57.308-128 128-128 70.694 0 128 57.308 128 128 0 70.694-57.306 128-128 128zM512 0l-320 512 320 512 320-512z',
            selectesb: 'path://M512 0c-282.77 0-512 229.23-512 512s229.23 512 512 512 512-229.23 512-512-229.23-512-512-512zM512 640c-70.692 0-128-57.306-128-128 0-70.692 57.308-128 128-128 70.694 0 128 57.308 128 128 0 70.694-57.306 128-128 128z'
        };

        //坐标生成
        Node.avgLipes = function (centerPoint, w, h, num) {
            var centerX = centerPoint[0];
            var centerY = centerPoint[1];
            var radiusOuterX = w / 2;
            var radiusOuterY = h / 2;
            var result = [];
            for (var i = 0; i < num; i++) {
                var angleOuter = (2 * Math.PI / num) * i;
                var x = radiusOuterX * Math.sin(angleOuter);
                var y = radiusOuterY * Math.cos(angleOuter);
                x += centerX;
                y += centerY;
                result.push([x, y]);
            }
            return result;
        };

        Node.yc = function (p1, p2, r) {
            // x=x2+(x2-x1)/√[(x2-x1)^2+(y2-y1)^2]
            // y=y2+(y2-y1)/√[(x2-x1)^2+(y2-y1)^2]
            var p = [];
            p.push(p2[0] + (p2[0] - p1[0]) * r / Math.sqrt(Math.pow(p2[0] - p1[0], 2) + Math.pow(p2[1] - p1[1], 2)));
            p.push(p2[1] + (p2[1] - p1[1]) * r / Math.sqrt(Math.pow(p2[0] - p1[0], 2) + Math.pow(p2[1] - p1[1], 2)));
            return p;
        }

        Node.prototype.setPoint = function (point) {
            this.x = point[0];
            this.y = point[1];
        };

        Node.prototype.getPoint = function () {
            {
                return [this.x, this.y];
            }
        };

        Node.prototype.getLinks = function () {
            var link = {
                target: this.id,
                source: StarChart.ESB_ID,
                symbol: ["none", 'none'],
                symbolSize: [8, 8],
                lineStyle: {
                    normal: {
                        color: this.color,
                        type: 'dotted',
                        opacity: 0.5,
                        curveness: 0
                    }
                }
            };
            if (this.consumer.totalCount > 0) {
                link.symbol[0] = "arrow";
            }
            if (this.server.totalCount > 0) {
                link.symbol[1] = "arrow";
            }
            var links = [link];
            return links;
        };
    },
    //绑定星状图
    bindChart: function (response, params) {
        // console.log(response);  //测试

        if(response.success == false) {
            CommonFunc.msgFa(response.errorMsg);
            return;
        }

        var datas = response.data;
        var chartDom = $(StarChart.idChartSkin)[0];
        var mychart = echarts.init(chartDom);
        //画布宽高
        var c_w = mychart.getWidth();
        var c_h = mychart.getHeight();

        var nodes = [];
        var links = [];
        var esbNodeConfig = {
            app: {
                id: StarChart.ESB_ID,
                name: '平台'
            },
            server: {
                totalCount: 0,
                failCount: 0
            },
            consumer: {
                totalCount: 0,
                failCount: 0
            }
        };
        var esbNode = new Node(esbNodeConfig);
        for (var i = 0; i < datas.length; i++) {
            var data = datas[i];
            var node = new Node(data);
            esbNode.serverTotalCount += node.server.totalCount;
            esbNode.serverFailCount += node.server.failCount;
            esbNode.consumerTotalCount += node.consumer.totalCount;
            esbNode.consumerFailCount += node.consumer.failCount;
            nodes.push(node);
            links = links.concat(node.getLinks());
        }

        // var esbNode = nodes.shift();
        //中心
        var centerX = c_w / 2;
        var centerY = c_h / 2;
        var centerPoint = [centerX, centerY];
        esbNode.setPoint(centerPoint);

        //2个圈
        var oneLayers = [];
        var twoLayers = [];
        var max = nodes.length;
        if (max <= 30) {
            oneLayers = nodes;
        } else {
            twoLayers = nodes.slice(0, parseInt(Node.h * max));
            oneLayers = nodes.slice(twoLayers.length);
        }

        var points = Node.avgLipes(centerPoint, 350, Math.ceil(350 / params.circleSize), oneLayers.length);
        for (var i = 0; i < oneLayers.length; i++) {
            oneLayers[i].setPoint(points[i]);
        }

        var points = Node.avgLipes(centerPoint, 650, Math.ceil(650 / params.circleSize), twoLayers.length);
        for (var i = 0; i < twoLayers.length; i++) {
            twoLayers[i].setPoint(points[i]);
            // twoLayers[i].label.normal.position='left';
            // twoLayers[i].label.emphasis.position='left';
        }

        nodes[0].symbol = Node.path.selectsys;
        var option = {
            tooltip: {
                formatter: function (params, ticket, callback) {
                    if (params.data.id != StarChart.ESB_ID && params.dataType == 'node') {
                        var node = params.data;
                        var html = "作为服务提供方<br>";
                        html += "成功:" + node.server.successCount + "<br>";
                        html += "失败:" + node.server.failCount + "<br>";
                        html += "作为服务消费方<br>";
                        html += "成功:" + node.consumer.successCount + "<br>";
                        html += "失败:" + node.consumer.failCount;
                        return html;
                    }
                }
                // triggerOn: 'click',
                , enterable: true
            },
            animation: true,
            animationDurationUpdate: 1500,
            animationEasingUpdate: 'linear ',//quinticInOut
            series: [
                {
                    name: '',
                    type: 'graph',
                    layout: 'none',
                    data: [esbNode].concat(nodes),
                    links: links,
                    //roam: true, //鼠标缩放
                    effect: {
                        show: true,
                        period: 6,
                        trailLength: 0
                    },
                    focusNodeAdjacency: true
                }
            ]
        };
        mychart.setOption(option);

        //回调：传入第一个业务系统
        var callback = params.callback;
        if (!CommonFunc.isEmpty(callback)) {
            callback(nodes[0]);
        }

        //绑定环形图的点击事件
        mychart.on('click', function (e) {
            // console.log(e); //测试

            var data = e.data;
            if(e.dataType == 'node') {

                // if (data.id != StarChart.ESB_ID) {
                //     for (var i = 0; i < nodes.length; i++) {
                //         var node = nodes[i];
                //         if (node.id != data.id) {
                //             node.symbol = Node.path.sys;
                //         } else {
                //             node.symbol = Node.path.selectsys;
                //         }
                //     }
                //     mychart.setOption(option);
                //
                //     //回调
                //     if (!CommonFunc.isEmpty(callback)) {
                //         callback(data);
                //     }
                // }

                //设置显示的图标
                for (var i = 0; i < nodes.length; i++) {
                    var node = nodes[i];
                    if (node.id != data.id) {
                        node.symbol = (data.id == StarChart.ESB_ID) ? Node.path.esb : Node.path.sys;
                    } else {
                        node.symbol = (data.id == StarChart.ESB_ID) ? Node.path.selectesb : Node.path.selectsys;
                    }
                }
                mychart.setOption(option);
                //回调
                if (!CommonFunc.isEmpty(callback)) {
                    if(data.id == StarChart.ESB_ID) {
                        data.id = '';
                    }
                    callback(data);
                }
            }
        });
    },
    // //获取ESB
    // getEsbNode: function (datas) {
    //     var esbNodeConfig = {};
    //     for (var i = 0; i < datas.length; i++) {
    //         var dataRaw = datas[i];
    //         if (dataRaw.obj.appType == 1) {
    //             esbNodeConfig = {
    //                 app: {
    //                     id: 0,  //应该替换为ESB实际的ID，但由于实际ID会导致连接线无法显示，暂时由0代替
    //                     type: dataRaw.obj.appType,
    //                     direction: dataRaw.obj.direction,
    //                     appId: dataRaw.obj.appId,
    //                     name: dataRaw.obj.appName
    //                 },
    //                 service: {
    //                     failCount: dataRaw.serviceFailCount,
    //                     totalCount: dataRaw.serviceCount
    //                 },
    //                 server: {
    //                     failCount: 0,
    //                     totalCount: 0
    //                 },
    //                 consumer: {
    //                     failCount: 0,
    //                     totalCount: 0
    //                 }
    //             };
    //             datas.splice(i, 1); //将ESB从数组中删除
    //             break;
    //         }
    //     }
    //     return esbNodeConfig;
    // }

};