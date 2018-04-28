//公用的请求前缀
var ajaxReqPre = "/ajax/overview/";
var echartColor = {
    red: '#CC3333',
    green: '#13cc0a',
    colorSet: ['#3598dc', '#CC9966', '#fec20e', '#a6ce39', '#003366']
};
var ThreeTop5 = {
    forConsumer: '#E7505A',
    forService: '#FEC20E',
    forDetail: '#44B6AE'
};

$(document).ready(function () {
    global_Object.initDomEvent();
});
var global_Object = {
    //数据请求链接
    ajaxInit: contextPath + ajaxReqPre + 'init',
    ajaxCountByOverView: contextPath + ajaxReqPre + 'countByOverView',
    ajaxQueryTrendChartData: contextPath + ajaxReqPre + 'queryTrendChartData',
    ajaxQueryClientTypeChartData: contextPath + ajaxReqPre + 'queryClientTypeChartData',
    ajaxQueryDetailsByAppInfo: contextPath + ajaxReqPre + 'queryDetailsByAppInfo',
    ajaxHistoryCountStatistic: contextPath + ajaxReqPre + 'historyCountStatistic',

    /*三个Top5链接*/
    ajaxForErrorConsumers: contextPath + ajaxReqPre + "queryErrorConsumers",
    ajaxForTop5Services: contextPath + ajaxReqPre + "queryTop5Services",
    ajaxForTop5Details: contextPath + ajaxReqPre + "queryTop5Details",

    //页面链接
    urlErrorOverview: contextPath + "/view/errorOverview",
    urlServerRealtime: contextPath + "/view/paas/serverrealtime",
    urlClientRealtime: contextPath + "/view/paas/clientrealtime",

    // urlServerRealtimeDetails: contextPath + "/view/paas/serverrealtime__serverdetailedrealtime",
    // urlClientRealtimeDetails: contextPath + "/view/paas/clientrealtime__clientdetailedrealtime",

    // //常量
    // ROLE_P: 1, //提供方角色
    // ROLE_C: 2, //消费方角色

    //变量
    // interval_current: null,

    appIdSelectedInStar: null,   //星状图选择的业务系统

    //初始化
    initDomEvent: function () {
        $("#date_picker").datepicker({
            language: "zh-CN",
            autoclose: true,//选中之后自动隐藏日期选择框
            format: "yyyy-mm-dd",//日期格式
            weekStart: 1,
            showWeekNumbers: true,
            endDate: "0d"
        });
        $("#date_picker").datepicker("update", JqCommon.getTodayFormatDate());
        $("#date_picker").datepicker().on('hide', function () {
            global_Object.queryHistoryStatistic();
        });

        //初始化数据
        CommonFunc.ajaxPostForm(global_Object.ajaxInit, null, global_Object.initData);

        $('button.btn-time').click(function () {
            if (!$(this).hasClass('blue')) {
                $(this).addClass('blue').siblings().removeClass('blue');
                global_Object.queryTrendChartData();
                global_Object.queryClientTypeChartData();
            }
        });
        $("#historyTotalCountModel").click(function () {
            global_Object.queryHistoryStatistic();
        });

        global_Object.countByOverView();
        global_Object.queryTrendChartData();
        global_Object.queryClientTypeChartData();

        // //概览界面三个Top5
/*
        global_Object.getErrorConsumers();
        global_Object.getServiceDurations();
        global_Object.getDetailsDuration();
*/

        //服务调用的tab切换事件
        $('#svcTab a').mouseover(function () {
            $(this).tab('show');
            //“提供服务”标签激活时，才能看到显示全部服务的复选框
            if ($(this).attr('href') == '#svcProvider') {
                $('#showAllProvidedSvc').parent().parent().parent().parent().removeClass('hidden');
            } else {
                $('#showAllProvidedSvc').parent().parent().parent().parent().addClass('hidden');
            }
        });

        $('#showAllProvidedSvc').change(function () {
            var params = {
                id: global_Object.appIdSelectedInStar
            };
            global_Object.queryDetailsByAppInfo(params);
        });
    },
    //初始化数据
    initData: function (resp) {
        if (resp.success) {
            var showTipContent = '', iIndex = 0;

            //设置显示错误状态的最低值
            var showErrorLower = resp.data.showErrorLower;
            if (showErrorLower > 0) {
                iIndex++;
                StarChart.showErrorLower = showErrorLower;
                showTipContent += '\n' + iIndex + '、小于' + showErrorLower + '条错误信息时图标显示绿色，否则，图标显示红色。';
            }
            //监控概览显示的系统最大数量
            var showSysUpper = resp.data.showSysUpper;
            if (showSysUpper > 0) {
                iIndex++;
                showTipContent += '\n' + iIndex + '、当前配置为显示的系统数量最大值为' + showSysUpper + '。';
            }
            //监控概览显示无数据的系统
            var showSysNoData = resp.data.showSysNoData;
            if (showSysNoData == 0) {
                iIndex++;
                showTipContent += '\n' + iIndex + '、当前配置为隐藏无数据的系统(如果所有系统都无交互，该配置无效)。';

                //绑定接入系统提示信息
                $('#appSizeTip').removeClass("hidden");
                $('#appSizeTip').on("mouseover", function () {
                    var showTip = '由于系统配置为隐藏无数据的系统，因此，接入系统只统计发生业务交互的系统。';
                    CommonFunc.msgSuTips(showTip, '#appSizeTip', {time: 0});
                });
                //绑定接入服务提示信息
                $('#serviceSizeTip').removeClass("hidden");
                $('#serviceSizeTip').on("mouseover", function () {
                    var showTip = '由于系统配置为隐藏无数据的系统，因此，接入服务只统计发生业务交互的服务。';
                    CommonFunc.msgSuTips(showTip, '#serviceSizeTip', {time: 0});
                });

            }

            //显示提示信息
            if (!CommonFunc.isEmpty(showTipContent)) {
                iIndex++;
                showTipContent += '\n' + iIndex + '、相关配置可根据要求自行修改，配置菜单：服务管理平台 > 基础管理 > 参数管理。';
                showTipContent = '说明：' + showTipContent;
                $('#showTipsImg').removeClass("hidden");
                $('#showTipsImg').on("mouseover", function () {
                    CommonFunc.msgSuTips(showTipContent.replace(/\n/g, '<br>'), '#showTipsImg', {time: 0});
                });
            } else {
                $('#showTips').addClass("hidden");
            }

            //绑定星状图
            var params = {
                circleSize: 1.3,
                callback: global_Object.queryDetailsByAppInfo,
                type: 21    //时间类型：21 - 今天
            };
            StarChart.initDomEvent(params);
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },
    countByOverView: function () {
        $.post(global_Object.ajaxCountByOverView, null, function (response) {
            // console.log(response);  //测试

            var data = response.data;
            var totalCount = data.totalCount ? data.totalCount : "0";
            var failCount;
            if (!CommonFunc.isEmpty(data.failCount) && data.failCount > 0) {
                failCount = '<span onclick="JqCommon.openPostWindow(\''
                    + global_Object.urlErrorOverview + '\')" class="cp prject-color-failure">'
                    + data.failCount + '<span class="valueUnit prject-color-failure">次</span></span>';
            } else {
                failCount = 0;
            }
            var appSize = data.appSize ? data.appSize : "0";
            var serviceSize = data.serviceSize ? data.serviceSize : "0";
            var runTime = data.runTime ? data.runTime : "";
            var runDay = data.runDay ? data.runDay : "";
            var historyTotalCount = data.historyTotalCount ? data.historyTotalCount : "0";

            $('span.totalCount').html(totalCount);
            $('span.failCount').html(failCount);
            $('span.appSize').html(appSize);
            $('span.serviceSize').html(serviceSize);
            $runDay = $('span.runTime');
            $runDay.html(runDay);
            $parent = $runDay.parent();
            $runTime = $parent.find("i");
            $runTime.attr("title", "平台最近一次启动距今的天数，平台最近一次启动时间：" + runTime);
            $('span.historyTotalCount').html(historyTotalCount);
        });
    },
    queryHistoryStatistic: function () {
        // console.log('123');

        $("#historyCountModel").modal("show");
        // var ajaxUrl = "historyCountStatistic";
        // var params = $("#historyDate").val();
        // historyCountTable.queryHistoryInPage(
        //     ajaxUrl, params, $("#targetDayTotal"), $("#targetDayFail")
        // );

        var columns = [
            {title: '提供方名称', data: 'type', width: "50%", render: $.fn.dataTable.render.ellipsisNew()},
            {title: '调用次数', data: 'totalCount', width: "25%"},
            {title: '失败次数', data: 'failCount', width: "25%"}
        ];
        var myAttrs = {
            paging : false,
            info : false
        };
        CommonTable.createTableAdvanced('#listTable', function (data, callback, settings) {
            var reqData = {
                targetDate : $("#historyDate").val()
            };
            //ajax请求数据
            CommonFunc.ajaxPostJson(global_Object.ajaxHistoryCountStatistic, reqData, function (respData) {
                // console.log(respData);  //测试

                var result = respData.data;

                //绑定总数
                $("#targetDayTotal").text(result.dailyTotalCount);
                $("#targetDayFail").text(result.dailyFailCount);
                //绑定数据到列表
                var appCountList = result.appCountList;
                callback({
                    recordsTotal: appCountList.length,//过滤之前的总数据量
                    recordsFiltered: appCountList.length,//过滤之后的总数据量
                    data: appCountList
                });

                //设置列表的滚动条
                CommonFunc.setScrollBar({
                    idWrapper : '#listTableWrapper',
                    axis : 'y'
                });
            });
        }, columns, null, myAttrs);
    },
    queryTrendChartData: function () {
        var type = global_Object.getSelectTimeType();
        var data = {type: type};
        data = {data: JSON.stringify(data)};
        $.post(global_Object.ajaxQueryTrendChartData, data, function (response) {

            var datas = response.data;
            //console.log("ssss", response, datas);
            var timeline = [];
            var totalCount = [];
            var failCount = [];
            for (var i = 0; i < datas.length; i++) {
                var item = datas[i];
                var time = item.time;
                timeline.push(time);
                totalCount.push(item.totalCount);
                failCount.push(item.failCount);
            }
            //console.log(timeline, totalCount, failCount);
            var option = {
                title: {},
                color: echartColor.colorSet,
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
                    left: 80,
                    right: 40
                },
                legend: {
                    top: 10,
                    right: '7%',
                    data: ['请求次数', '异常次数']
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
                    name: '请求次数',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: totalCount,
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值', symbolSize: [80, 80]},
                            {type: 'min', name: '最小值', symbolSize: [60, 60]}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                }, {
                    name: '异常次数',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: failCount
                }]
            };
            var trendLineEChart = echarts.init($("#trendLineEChartDIV")[0]);
            trendLineEChart.setOption(option);
        });
    },

    queryClientTypeChartData: function () {
        var data = {type: global_Object.getSelectTimeType()};
        data = {data: JSON.stringify(data)};
        $.post(global_Object.ajaxQueryClientTypeChartData, data, function (response) {
            var datas = response.data;
            var totalCountData = [];
            var failCountData = [];
            var legendData = [];
            for (var i = 0; i < datas.length; i++) {
                var item = datas[i];
                var type = item.type;
                legendData.push(type);
                var totalCount = item.totalCount;
                var failCount = item.failCount;
                totalCountData.push({
                    name: type,
                    value: totalCount
                });
                failCountData.push({
                    name: type,
                    value: failCount
                })
            }
            var option = {
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                color: echartColor.colorSet,
                legend: {
                    type: 'scroll',
                    left: 0,
                    top: 5,
                    orient: 'horizontal',
                    data: legendData
                },
                series: [
                    // {
                    //     name:'异常',
                    //     type:'pie',
                    //     radius: [0, '30%'],
                    //     data:failCountData,
                    //
                    // },
                    {
                        name: '请求次数',
                        type: 'pie',
                        selectedMode: 'single',
                        radius: ['20%', '50%'],
                        center: ['50%', '55%'],
                        itemStyle: {
                            normal: {
                                label: {
                                    show: true,
                                    // position: 'outer',
                                    formatter: "{b}\n{d}%"
                                }
                            }
                        },
                        data: totalCountData
                    }
                ]
            };
            var trendPierEChart = echarts.init($("#trendPierEChartDIV")[0]);
            trendPierEChart.setOption(option);
        });
    },

    queryDetailsByAppInfo: function (params) {
        // console.log(params);    //测试

        var appId = params.id;
        var appName = params.name;
        global_Object.appIdSelectedInStar = appId;
        // var appName = params.name;
        var $details = $('#chart-details');
        $details.find('div.unslider').html('');
        var data = {
            appId: appId,
            showAllProvidedSvc: $('#showAllProvidedSvc').prop("checked")
        };
        data = {data: JSON.stringify(data)};
        $.post(global_Object.ajaxQueryDetailsByAppInfo, data, function (response) {
            // console.log(response);  //测试

            var datas = response.data;
            var servers = datas.server;     //提供方角色时的统计信息
            var consumers = datas.consumer; //消费方角色时的统计信息

            //提供方
            var pageNum = 12;   //每页个数
            var rowLines = 4;       //行数
            var rowNum = pageNum / rowLines;   //每行个数
            var rowClassName = "col-md-" + (12 / rowNum);   //每个单元格的样式
            global_Object.generateSvcInfo($('#svcProvider'), BizStable.MonitorRole.Provider, servers, rowClassName, appId, appName, pageNum, rowNum);

            //消费方
            pageNum = 9;   //每页个数
            rowLines = 3;       //行数
            rowNum = pageNum / rowLines;   //每行个数
            rowClassName = "col-md-" + (12 / rowNum);   //每个单元格的样式
            global_Object.generateSvcInfo($('#svcConsumer'), BizStable.MonitorRole.Consumer, consumers, rowClassName, appId, appName, pageNum, rowNum);
        });
    },

    /**
     * 生成服务信息
     * @param $wrapper  容器对象
     * @param role  角色类型
     */
    generateSvcInfo: function ($wrapper, role, svcStat, rowClassName, appId, appName, pageNum, rowNum) {
        var html;
        var unsliderConfigs = {autoplay: false, arrows: false, dots: true, fluid: true};
        if (svcStat.length > 0) {
            //补位数据
            var space = pageNum - svcStat.length % pageNum;
            if (space != pageNum) {
                for (var i = 0; i < space; i++) {
                    svcStat.push(null);
                }
            }

            //页数
            var pages = svcStat.split(pageNum);
            //设置是否出现轮播
            if (pages.length < 2) {
                unsliderConfigs.dots = false;
            }

            //生成服务元素
            var appNameShow, svcNameShow, badgeLen, totalCountValue, onClickValue;
            html = '<ul style="padding-top: 10px !important;">';
            for (var i = 0; i < pages.length; i++) {
                html += '<li style="width:95%;">';
                var rows = pages[i].split(rowNum);
                for (var j = 0; j < rows.length; j++) {
                    html += '<div class="col-md-12 itemRow">';
                    for (var k = 0; k < rows[j].length; k++) {
                        var data = rows[j][k];
                        html += '<div class="' + rowClassName + '">';
                        if (data) {
                            var totalCount = data.totalCount;
                            var failCount = data.failCount;
                            var percent = CommonFunc.calPercent(failCount, totalCount);

                            var serverAppId, serverAppName, clientAppId, clientAppName;
                            if(role == BizStable.MonitorRole.Provider) {
                                serverAppId = appId;
                                serverAppName = appName;
                                clientAppId = '';
                                clientAppName = '';
                            } else {
                                serverAppId = data.domain;
                                serverAppName = data.appName;
                                clientAppId = appId;
                                clientAppName = appName;
                            }

                            html += '<div class="itemCell" data-toggle="tooltip" title="';
                            html += '服务名称：' + data.serviceName;
                            if (!CommonFunc.isEmpty(data.appName)) {
                                html += '\n服务提供方：' + data.appName;
                            }
                            html += '\n请求次数：' + totalCount
                                + '\n异常次数：' + failCount + '\n异常占比：' + percent + '%">';
                            //如果服务名称超过9个字，则使用省略号
                            svcNameShow = (data.serviceName.length > 9) ? data.serviceName.substr(0, 9) + '..' : data.serviceName;
                            //单击事件
                            // onClickValue = (totalCount > 0) ? ' onclick="global_Object.openDetailList(\''
                            //     + role + '\',\'' + appId + '\',\'' + data.domain + '\')"' : '';
                            onClickValue = (totalCount > 0) ? ' onclick="global_Object.openDetailList(' +
                                '\'' + role + '\',' +
                                '\'' + serverAppId + '\',' +
                                '\'' + serverAppName + '\',' +
                                '\'' + clientAppId + '\',' +
                                '\'' + clientAppName + '\',' +
                                '\'' + data.serviceCode + '\',' +
                                '\'' + data.serviceName + '\',' +
                                '\'\',' +
                                '\'' + totalCount + '\')"' : '';
                            //显示服务名称
                            html += '<div class="title">' + svcNameShow;
                            //显示所消费服务的提供方系统名称
                            if (!CommonFunc.isEmpty(data.appName)) {
                                //如果提供方名称超过8个字，则使用省略号
                                appNameShow = (data.appName.length > 8) ? data.appName.substr(0, 8) + '..' : data.appName;
                                html += '<div class="msg-tips">提供方:' + appNameShow + '</div>';
                            }
                            //显示请求总数，如果总数大于0，则使用加粗样式
                            if (totalCount > 0) {
                                totalCountValue = '请求次数:<span class="totalCount prject-color-success">'
                                    + totalCount + '</span>';
                            } else {
                                totalCountValue = '暂无请求';
                            }
                            html += '<div class="msg-tips' + (((totalCount > 0)) ? ' cp' : '') + '"'
                                + onClickValue + '>' + totalCountValue + '</div></div>';

                            // console.log(appId);  //测试
                            // console.log(data);  //测试

                            //徽标：显示错误数
                            if (failCount > 0) {
                                // var serverAppId = (role == BizStable.MonitorRole.Provider) ? appId : data.domain;
                                // var clientAppId = (role == BizStable.MonitorRole.Provider) ? '' : appId;

                                html += '<span class="badge prject-backcolor-failure cp"' +
                                    ' onclick="global_Object.openDetailList(' +
                                    '\'' + role + '\',' +
                                    '\'' + serverAppId + '\',' +
                                    '\'' + serverAppName + '\',' +
                                    '\'' + clientAppId + '\',' +
                                    '\'' + clientAppName + '\',' +
                                    '\'' + data.serviceCode + '\',' +
                                    '\'' + data.serviceName + '\',' +
                                    '\'' + BizStable.MonitorStatus.FAILURE + '\',' +
                                    '\'' + failCount + '\')">'
                                    + failCount + '</span>';
                            }
                            html += '</div>';
                        } else {
                            html += '<div style="height:116px;"></div>';
                        }
                        html += "</div>";
                    }
                    html += "</div>";
                }
                html += "</li>";
            }
            html += "</ul>";
        } else {
            html = '<div class="emptytip">暂无服务</div>';
        }
        $wrapper.html(html);
        $wrapper.unslider(unsliderConfigs);
    },

    // /**
    //  * 下钻到服务列表级别页面
    //  * @param role  角色类型（提供方、消费方）
    //  * @param domain    系统的appId
    //  * @param downDomain
    //  * @param status   空值或0 - 成功， -1 - 错误
    //  */
    // openPostSvcList: function (role, domain, downDomain, status) {
    //     var url = (role == BizStable.MonitorRole.Provider) ? global_Object.urlServerRealtime : global_Object.urlClientRealtime;
    //     var datas = {
    //         "domain": domain,
    //         "type": "D",
    //         "downDomain": downDomain,
    //         "isRemote": true
    //     };
    //     JqCommon.openPostWindow(url, datas);
    // },

    /**
     * 定位到服务的错误详情页面
     * @param role  角色类型（提供方、消费方）
     * @param serverAppId  提供方系统代码
     * @param serverAppName  提供方系统名称
     * @param clientAppId  消费方系统代码
     * @param clientAppName  消费方系统名称
     * @param svcCode  服务代码
     * @param svcName  服务名称
     * @param status  状态
     * @param totalCount  信息总数
     */
    openDetailList: function (role, serverAppId, serverAppName, clientAppId, clientAppName, svcCode, svcName, status, totalCount) {
        var url = (role == BizStable.MonitorRole.Provider) ?
            BizStable.CommonURL.serverDetailedRealtime : BizStable.CommonURL.clientDetailedRealtime;
        //设置参数
        var datas = {
            "transactionTypeId": svcCode,
            "transactionTypeName": svcName,
            "serverAppId": serverAppId,
            "serverAppName": serverAppName,
            "clientAppId": clientAppId,
            "clientAppName": clientAppName,
            "type": BizStable.MonitorDateType.TODAY,
            "status": status,
            "totalCount": totalCount,
            "role" : role
        };
        //将参数直接拼接在URL中，然后直接跳转
        var urlFinal = CommonFunc.joinUrlParams(datas, url);

        // console.log(urlFinal);  //测试

        window.location.href = urlFinal;
    },
    getSelectTimeType: function () {
        return $('button.btn-time.blue').children('input[name="type"]').val();
    },

    //异常调用统计  消费方异常次数Top5
    getErrorConsumers: function () {
        JqAjax.postByDefaultErrorCatch(global_Object.ajaxForErrorConsumers,
            {}, function (result) {
                var html = "";
                $.each(result.data, function (index, element) {
                    index++;
                    html += '<li class="mt-list-item">' +
                        '<div class="list-icon-container" style="color: ' + ThreeTop5.forConsumer + '">' +
                        '<div class="mt-element-top-number">' + index + '</div>' +
                        '</div>' +
                        '<div class="list-datetime">' + element.failCount + ' 次异常</div>' +
                        '<div class="list-item-content">' +
                        '<h3 class="uppercase">' +
                        '<a href="javascript:;">' + element.type + '</a>' +
                        '</h3>' +
                        '</div></li>';
                });
                html += mtListItem.setUlContent(result.data.length);
                $("#ulForConsumers").html(html);
            })
    },

    getServiceDurations: function () {
        JqAjax.postByDefaultErrorCatch(global_Object.ajaxForTop5Services,
            {}, function (result) {
                var html = "";
                $.each(result.data, function (index, element) {
                    index++;
                    html += '<li class="mt-list-item">' +
                        '<div class="list-icon-container" style="color: ' + ThreeTop5.forService + '">' +
                        '<div class="mt-element-top-number">' + index + '</div>' +
                        '</div>' +
                        '<div class="list-datetime">' + element.duration + ' ms</div>' +
                        '<div class="list-item-content">' +
                        '<h3 class="uppercase">' +
                        '<a href="javascript:;">' + element.serviceName + '</a>' +
                        '</h3>' +
                        '</div></li>';
                });
                html += mtListItem.setUlContent(result.data.length);
                $("#ulForServices").html(html);
            })
    },

    getDetailsDuration: function () {
        JqAjax.postByDefaultErrorCatch(global_Object.ajaxForTop5Details,
            {}, function (result) {
                var html = "";
                $.each(result.data, function (index, element) {
                    index++;
                    html += '<li class="mt-list-item">' +
                        '<div class="list-icon-container" style="color: ' + ThreeTop5.forDetail + '">' +
                        '<div class="mt-element-top-number">' + index + '</div>' +
                        '</div>' +
                        '<div class="list-datetime">' + element.useTime + ' ms</div>' +
                        '<div class="list-item-content"><span>' + element.serverAppName +
                        '<i class="fa fa-angle-right" aria-hidden="true"></i>'
                        + element.clientAppName + '</span><h3 class="uppercase">' +
                        '<a href="javascript:;">' + element.svcName + '</a>' +
                        '</h3>' +
                        '</div></li>';
                });
                html += mtListItem.setUlContent(result.data.length);
                $("#ulForDetails").html(html);
                $("#topDetails").mCustomScrollbar({
                    autoHideScrollbar: true,	//自动隐藏滚动条
                    axis: "y", //水平与竖直都出现滚动条
                    theme: "minimal-dark"
                });
            })
    }

    // getEcharts: function () {
    //     function randomData() {
    //         now = new Date(+now + oneDay);
    //         value = value + Math.random() * 21 - 10;
    //         return {
    //             name: now.toString(),
    //             value: [
    //                 [now.getFullYear(), now.getMonth() + 1, now.getDate()].join('/'),
    //                 Math.round(value)
    //             ]
    //         }
    //     }
    //
    //     var data = [];
    //     var now = +new Date(1997, 9, 3);
    //     var oneDay = 24 * 3600 * 1000;
    //     var value = Math.random() * 1000;
    //     for (var i = 0; i < 1000; i++) {
    //         data.push(randomData());
    //     }
    //
    //     var option = {
    //         title: {},
    //         tooltip: {
    //             trigger: 'axis',
    //             formatter: function (params) {
    //                 params = params[0];
    //                 var date = new Date(params.name);
    //                 return date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear() + ' : ' + params.value[1];
    //             },
    //             axisPointer: {
    //                 animation: false
    //             }
    //         },
    //         xAxis: {
    //             type: 'time',
    //             splitLine: {
    //                 show: false
    //             }
    //         },
    //         yAxis: {
    //             type: 'value',
    //             boundaryGap: [0, '100%'],
    //             splitLine: {
    //                 show: false
    //             }
    //         },
    //         series: [{
    //             name: '模拟数据',
    //             type: 'line',
    //             showSymbol: false,
    //             hoverAnimation: false,
    //             data: data
    //         }]
    //     };
    //     global_Object.mychart1 = echarts.init($("#mychart1")[0]);
    //     global_Object.mychart1.setOption(option);
    //     setInterval(function () {
    //
    //         for (var i = 0; i < 5; i++) {
    //             data.shift();
    //             data.push(randomData());
    //         }
    //
    //         global_Object.mychart1.setOption({
    //             series: [{
    //                 data: data
    //             }]
    //         });
    //     }, 1000);
    //
    //
    //     var option2 = {
    //
    //         tooltip: {
    //             trigger: 'item',
    //             formatter: "{a} <br/>{b} : {c} ({d}%)"
    //         },
    //
    //         series: [
    //             {
    //                 name: '访问来源',
    //                 type: 'pie',
    //                 radius: ['50%', '70%'],
    //                 center: ['50%', '50%'],
    //                 data: [
    //                     {value: 335, name: '手机'},
    //                     {value: 310, name: '电脑'},
    //                     {value: 234, name: '其他'}
    //                 ],
    //                 itemStyle: {
    //                     emphasis: {
    //                         shadowBlur: 10,
    //                         shadowOffsetX: 0,
    //                         shadowColor: 'rgba(0, 0, 0, 0.5)'
    //                     }
    //                 }
    //             }
    //         ]
    //     };
    //     var mychart2 = echarts.init($("#mychart2")[0]);
    //     mychart2.setOption(option2);
    // },
    // getHuan: function () {
    //     var json = [{
    //         "id": "08",
    //         "name": "His",
    //         "nodeState": "1",
    //         "jr": 12,
    //         "zr": 13,
    //         "sum": 111,
    //         "yc": 2
    //     }, {
    //         "id": "05",
    //         "name": "Lis",
    //         "nodeState": "1",
    //         "jr": 12,
    //         "zr": 13,
    //         "sum": 111,
    //         "yc": 2
    //     }, {
    //         "id": "03",
    //         "name": "Cis",
    //         "nodeState": "1",
    //         "jr": 12,
    //         "zr": 13,
    //         "sum": 111,
    //         "yc": 2
    //     }, {
    //         "id": "04",
    //         "name": "医保控费",
    //         "nodeState": "1",
    //         "jr": 12,
    //         "zr": 13,
    //         "sum": 111,
    //         "yc": 2
    //     }, {
    //         "id": "10",
    //         "name": "CDSS",
    //         "nodeState": "1",
    //         "jr": 12,
    //         "zr": 13,
    //         "sum": 111,
    //         "yc": 2
    //     },
    //         {
    //             "id": "12", "name": "APP", "nodeState": "2", "jr": 12,
    //             "zr": 13,
    //             "sum": 111,
    //             "yc": 2
    //         },
    //         {
    //             "id": "12", "name": "APP", "nodeState": "2", "jr": 12,
    //             "zr": 13,
    //             "sum": 111,
    //             "yc": 2
    //         },
    //         {
    //             "id": "12", "name": "APP", "nodeState": "2", "jr": 12,
    //             "zr": 13,
    //             "sum": 111,
    //             "yc": 2
    //         },
    //         {
    //             "id": "12", "name": "APP", "nodeState": "2", "jr": 12,
    //             "zr": 13,
    //             "sum": 111,
    //             "yc": 2
    //         },
    //         {
    //             "id": "12", "name": "APP", "nodeState": "2", "jr": 12,
    //             "zr": 13,
    //             "sum": 111,
    //             "yc": 2
    //         }];
    //     var boxDom = $('#circles-wrap').closeLoop({
    //         title: "ESB",
    //         //url:contextPath +"/CdssIndexController/getHjd",
    //         //paramValue:{"syxh":that.jzlsh,"zl_lclcxh":that.xh,"yzxh":that.yzxh},
    //         data: json,
    //         currentDis: 30,
    //         outsidePadding: 3,
    //         //animate: true,
    //         loadSuccess: function () {
    //             $(".clircle:first").addClass("active");
    //             $(".clircle").on("click", function () {
    //                 if (!$(this).hasClass("active")) {
    //                     $(this).addClass("active").siblings().removeClass("active");
    //                     global_Object.getPie();
    //                 }
    //             });
    //             //
    //             ////设置环的标题
    //             //// $('#circles-wrap').closeLoop("setTitle",global_Object.circleTitle);
    //             ////{"syxh":"82187","zl_lclcxh":"6","yzxh":"8192707"}
    //             /////!*绑定顶头分析结果*!/
    //             //$.post( contextPath + "/CdssIndexController/getLcjdsj",{"syxh":that.jzlsh,"zl_lclcxh":that.xh,"yzxh":that.yzxh},function (result) {
    //             //    if(result.length>0){
    //             //        that.getAnalyticalResultsTop(result[0])
    //             //    }
    //             //
    //             //})
    //         }
    //     });
    //
    // },
    // getPie: function () {
    //
    //
    // },
    // getList: function () {
    //     var col_json = [
    //         {
    //             "title": "系统ID", "data": "id"
    //         },
    //         {
    //             "title": "系统名称", "data": "name"
    //         }
    //         ,
    //         // {
    //         //     "title": "调用", "data": "serverTotalCount"
    //         // },
    //         // {
    //         //     "title": "异常", "data": "serverFailCount"
    //         // },
    //         // {
    //         //     "title": "调用", "data": "consumerTotalCount"
    //         // },
    //         // {
    //         //     "title": "异常", "data": "consumerFailCount"
    //         // },
    //         {
    //             "title": "详情", "data": "id",
    //             "render": function (data, type, full, meta) {
    //                 return '<i class="fa fa-bar-chart fa-lg qs cp"  data-id="' + data + '"></i>';
    //             }
    //         },
    //     ]
    //     global_Object.mytable = $("#mytable").DataTable({
    //         "serverSide": true,
    //         "pageLength": 10,
    //         "bPaginate": true, //翻页功能
    //         "processing": false,
    //         "bSort": false,
    //         "bFilter": false, //过滤功能
    //         "bInfo": true,//页脚信息
    //         "bLengthChange": false, //改变每页显示数据数量
    //         "pagingType": "full_numbers",//分页样式的类型
    //         "language": global_Object.language,
    //         "ajax": function (data, callback, settings) {
    //             var json = {
    //                 "recordsTotal": 123,
    //                 data: [{
    //                     "name": "His",
    //                     "id": 1,
    //                     "jr": 100,
    //                     "zr": 110,
    //                     "sum": 11321,
    //                     "yc": 10,
    //                     "jg": 21
    //                 }, {"name": "Cis", "id": 2, "jr": 100, "zr": 110, "sum": 11321, "yc": 10, "jg": 21}, {
    //                     "name": "Lis",
    //                     "id": 3,
    //                     "jr": 100,
    //                     "zr": 110,
    //                     "sum": 11321,
    //                     "yc": 10,
    //                     "jg": 21
    //                 }]
    //             }
    //             //var start_date = global_Object.date + "-01";
    //             //$.post("../../tools/get_chart_dialog.ashx", { "action": "4", "zbmx_id": global_Object.zbmx_id, "year": start_date, "sys_id": global_Object.sys_id, "kskey": global_Object.kskey, "start": data.start, "length": data.length }, function (data) {
    //             //    var objResult = JSON.parse(data);
    //             callback({
    //                 recordsTotal: json.recordsTotal,//过滤之前的总数据量
    //                 recordsFiltered: json.recordsTotal,//过滤之后的总数据量
    //                 data: json.data
    //             });
    //             //
    //             //});
    //
    //         },
    //         "columns": col_json,
    //         "drawCallback": function (a) {
    //             global_Object.table_icon_init();
    //         },
    //         "autoWidth": false,
    //         "responsive": true,
    //         "width": "100%"
    //     }).on('init.dt', function () {
    //         $(".fixed").hide();
    //     });
    // },
    // table_icon_init: function () {
    //     $(".qs").off().on("click", function () {
    //         var $td = $(this).parent("td");
    //         var $tr = $td.parent("tr");
    //         if ($td.hasClass("active")) {
    //             $td.removeClass("active")
    //             $tr.next("tr").slideUp();
    //         }
    //         else {
    //             $td.addClass("active")
    //             if ($tr.next("tr").hasClass("count-details")) {
    //                 $tr.next("tr").slideDown();
    //             }
    //             else {
    //                 var html = '<tr class="echart" style="display:none;"><td colspan="8">';
    //                 html += $(".count-details").prop("outerHTML");
    //                 html += '</td></tr>';
    //                 $tr.after(html)
    //                 global_Object.getPie.apply($tr.next("tr"), [])
    //                 $tr.next("tr").slideDown();
    //                 $tr.next("tr").find(".count-details").show();
    //
    //             }
    //         }
    //     });
    // },
};

var mtListItem = {
    /*当Top5内容不足5个时，补上空行*/
    setUlContent: function (arrayLength) {
        var html = "", gap = 5 - arrayLength;
        for (var i = 0; i < gap; i++) {
            html += '<li class="mt-list-item">' +
                '<div class="list-icon-container done"></div>' +
                '<div class="list-datetime"></div>' +
                '<div class="list-item-content">' +
                '<h3 class="uppercase">' + '&nbsp;&nbsp;' + '</h3>' +
                '</div></li>';
        }
        return html;
    }
};