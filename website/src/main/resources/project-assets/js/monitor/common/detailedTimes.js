/**
 * Created by xuehao on 2017/12/15.
 */

//ajax前缀
var DetailedTimesAjaxReqPre = contextPath + "/ajax/paas/";

var DetailedTimes = {
    ajaxQueryMessageList: null,
    ajaxQueryMessageListLastHour: DetailedTimesAjaxReqPre + "queryCommonTransactionMessageList",
    ajaxQueryMessageListToday: DetailedTimesAjaxReqPre + "queryCommonTransactionMessageList",
    ajaxQueryMessageListHour: DetailedTimesAjaxReqPre + "queryCommonTransactionMessageList",
    ajaxQueryMessageListErrorQueue: contextPath + "/ajax/error/" + "queryTodayErrorTransactionMessageList",
    ajaxQueryTransactionMessageListDetail: DetailedTimesAjaxReqPre + 'queryTransactionMessageListDetail',

    //页面对象ID
    tableID: '#listTable',
    queryStatusID: '#queryStatus',
    queryDuringID: '#queryDuring',
    queryColumnID: '#queryColumn',
    queryWordID: '#queryWord',
    queryDateWrapperID: '#queryDateWrapper',
    queryDateID: '#queryDate',
    timeRangeID: '#timeRange',
    queryTimeRangeStartID: '#queryTimeRangeStart',
    queryTimeRangeEndID: '#queryTimeRangeEnd',
    idTreeWrapper: '#treeWrapper',
    idWrapperId2: '#wrapperId2',
    idModalScrollDiv: '#modalScrollDiv',

    //常量
    LIMIT_HOURS: 2, //限制的小时数

    //全局变量
    serverAppId: null, //服务方系统代码
    serverAppName: null, //服务方系统名称
    serverIpAddress: null,  //服务方IP
    clientAppId: null, //消费方系统代码
    clientAppName: null, //消费方系统名称
    clientIpAddress: null,  //消费方IP
    transactionTypeId: null,     //服务代码
    transactionTypeName: null,     //服务名称
    type: null,    //时间类型：11 - 当前一小时、21 - 当天、31 - 指定小时
    time: null,     //如果“type=31(即指定小时)”，则使用具体格式，例如：2017-12-15 13:00:00
    limitStartTime: null, //筛选的开始时间（格式：2017-12-15 13:01:04）
    limitEndTime: null,   //筛选的结束时间（格式：2017-12-15 13:01:04）
    status: null,   //状态：0 - 执行成功、 -1 - 执行失败
    totalCount: null,   //目标总数，即报告中统计的数量，可以传到后端，可避免从数据库中实时统计，提高性能
    role: null,    //来源角色：服务提供方或消费方

    queryDateShow: false, //日期是否显示，默认不显示
    statusSelectShow: true, //状态下拉列表是否显示，默认显示
    statusSelectEnable: true, //状态下拉列表是否可选，默认可选
    duringSelectShow: true, //耗时下拉列表是否显示，默认显示

    limitTimeChanged: false,   //筛选时间是否更改过，如果改过，则传到后台的totalCount应该为空，以便后台统计真正的数量

    //初始化
    initDomEvent: function () {
        //初始化URL参数
        DetailedTimes.initUrlParams();
        //初始化标题
        DetailedTimes.initTitle();
        //初始化搜索条件
        DetailedTimes.initQuery();

        //绑定详情列表
        DetailedTimes.bindTableData();

        $(DetailedTimes.queryStatusID).on("change", function () {
            //绑定详情列表
            DetailedTimes.bindTableData();
        });

        $(DetailedTimes.queryDuringID).on("change", function () {
            //绑定详情列表
            DetailedTimes.bindTableData();
        });

        $(DetailedTimes.queryColumnID).on("change", function () {
            DetailedTimes.queryColumnChange();
        });

        $(DetailedTimes.queryWordID).on('keypress', function (event) {
            if (event.keyCode == "13") {
                //绑定详情列表
                DetailedTimes.bindTableData();
            }
        });

        $('#btnQuery').on("click", function () {
            //绑定详情列表
            DetailedTimes.bindTableData();
        });

        $('#btnReset').on("click", function () {
            //初始化搜索条件
            DetailedTimes.initQuery();
            //重置条件
            CommonFunc.setSelectedByIndex(DetailedTimes.queryStatusID);
            CommonFunc.setSelectedByIndex(DetailedTimes.queryDuringID);
            CommonFunc.setSelectedByIndex(DetailedTimes.queryColumnID);
            $(DetailedTimes.queryWordID).val('');
            DetailedTimes.limitTimeChanged = false;
            //绑定详情列表
            DetailedTimes.bindTableData();
        });
    },
    //搜索列变化事件
    queryColumnChange: function () {
        //如果选择的不是mainId和MessageID，则必须限制时间范围
        var columnName = $(DetailedTimes.queryColumnID).val();
        if (BizStable.MonitorDatalistKey.MessageID.key != columnName && BizStable.MonitorDatalistKey.mainId.key != columnName) {
            DetailedTimes.initTimepicker(true);
        } else {
            DetailedTimes.initTimepicker();
        }
    },
    //初始化URL参数
    initUrlParams: function () {
        if (CommonFunc.isEmpty(DetailedTimes.serverAppId)) {
            DetailedTimes.serverAppId = CommonFunc.getQueryString("serverAppId");
        }
        if (CommonFunc.isEmpty(DetailedTimes.serverAppName)) {
            DetailedTimes.serverAppName = CommonFunc.getQueryString("serverAppName");
        }
        DetailedTimes.transactionTypeId = CommonFunc.getQueryString("transactionTypeId");
        DetailedTimes.transactionTypeName = CommonFunc.getQueryString("transactionTypeName");
        if (CommonFunc.isEmpty(DetailedTimes.type)) {
            DetailedTimes.type = CommonFunc.getQueryString("type");
        }
        DetailedTimes.time = CommonFunc.getQueryString("time");
        DetailedTimes.clientAppId = CommonFunc.getQueryString("clientAppId");
        DetailedTimes.clientAppName = CommonFunc.getQueryString("clientAppName");
        DetailedTimes.serverIpAddress = CommonFunc.getQueryString("serverIpAddress");
        DetailedTimes.clientIpAddress = CommonFunc.getQueryString("clientIpAddress");
        if (CommonFunc.isEmpty(DetailedTimes.status)) {
            DetailedTimes.status = CommonFunc.getQueryString("status");
        }
        DetailedTimes.totalCount = CommonFunc.getQueryString("totalCount");
        DetailedTimes.role = CommonFunc.getQueryString("role");
        if (CommonFunc.isEmpty(DetailedTimes.role)) {
            DetailedTimes.role = BizStable.MonitorRole.Provider;
        }

        // console.log(DetailedTimes.type);    //测试
        // console.log(DetailedTimes.time);    //测试
    },
    /**
     * 初始化筛选日期
     */
    initDatepicker: function () {
        var todayDate = DateUtils.format(DateUtils.formatDateBase, new Date());
        $(DetailedTimes.queryDateID).val(todayDate);

        laydate.render({
            elem: DetailedTimes.queryDateID
            , trigger: 'click'
            , format: 'yyyy-MM-dd'
            , btns: ['now']
            , value: todayDate
            , max: todayDate
        });
    },

    // /**
    //  * 初始化筛选时间
    //  * @param isLimitHours  是否限制小时数：true - 是，false或空或未定义 - 否
    //  */
    // initTimepicker: function (isLimitHours) {
    //     //设置初始化时间
    //     var now = new Date();
    //     var beginTime = (isLimitHours == true) ? DateUtils.addHours(-DetailedTimes.LIMIT_HOURS, now)
    //         : DateUtils.getTodayZeroOclock();
    //
    //     //设置开始时间控件
    //     DetailedTimes.setTimepicker("#queryTimeRangeStart", beginTime, 0);
    //     //设置结束时间控件
    //     DetailedTimes.setTimepicker("#queryTimeRangeEnd", now, 1);
    // },
    // /**
    //  * 设置时间控件
    //  * @param triggerElem   触发弹出时间窗口的元素
    //  * @param defaultValue  默认时间
    //  * @param type   0 - 设置开始时间， 1 - 设置结束时间
    //  */
    // setTimepicker: function (triggerElem, defaultValue, type) {
    //     if (type == 0) {
    //         DetailedTimes.limitStartTime = DateUtils.format(DateUtils.formatBase, defaultValue);
    //     } else {
    //         DetailedTimes.limitEndTime = DateUtils.format(DateUtils.formatBase, defaultValue);
    //     }
    //
    //     //设置开始时间
    //     laydate.render({
    //         elem: triggerElem
    //         // , eventElem: '#btnQueryTimeRange'
    //         , trigger: 'click'
    //         , type: 'time'
    //         , format: 'HH:mm:ss'
    //         , btns: ['now', 'confirm']
    //         , value: defaultValue
    //         , done: function (value, date) {
    //             // console.log(value); //得到日期生成的值，如：2017-08-18
    //             // console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
    //
    //             DetailedTimes.limitTimeChanged = true;
    //             if (type == 0) {
    //                 if (CommonFunc.isEmpty(date)) {
    //                     DetailedTimes.limitStartTime = "";
    //                 } else {
    //                     DetailedTimes.limitStartTime = DateUtils.format(DateUtils.formatDateBase, new Date())
    //                         + ' ' + CommonFunc.addPreZero(date.hours, 2) + ':'
    //                         + CommonFunc.addPreZero(date.minutes, 2) + ':'
    //                         + CommonFunc.addPreZero(date.seconds, 2);
    //                 }
    //             } else {
    //                 if (CommonFunc.isEmpty(date)) {
    //                     DetailedTimes.limitEndTime = "";
    //                 } else {
    //                     DetailedTimes.limitEndTime = DateUtils.format(DateUtils.formatDateBase, new Date())
    //                         + ' ' + CommonFunc.addPreZero(date.hours, 2) + ':'
    //                         + CommonFunc.addPreZero(date.minutes, 2) + ':'
    //                         + CommonFunc.addPreZero(date.seconds, 2);
    //                 }
    //             }
    //
    //             // console.log(DetailedTimes.limitStartTime);    //测试
    //             // console.log(DetailedTimes.limitEndTime);    //测试
    //         }
    //     });
    // },

    /**
     * 初始化筛选时间
     * @param isLimitHours  是否限制小时数：true - 是，false或空或未定义 - 否
     */
    initTimepicker: function (isLimitHours) {
        var startTime = (isLimitHours == true)
            ? DateUtils.format(DateUtils.formatTimeBase, DateUtils.addHours(-DetailedTimes.LIMIT_HOURS))
            : "00:00:00";
        $(DetailedTimes.queryTimeRangeStartID).val(startTime);

        var now = DateUtils.format(DateUtils.formatTimeBase, new Date());
        $(DetailedTimes.queryTimeRangeEndID).val(now);

        //设置开始时间控件
        DetailedTimes.setTimepicker(DetailedTimes.queryTimeRangeStartID, startTime);
        //设置结束时间控件
        DetailedTimes.setTimepicker(DetailedTimes.queryTimeRangeEndID, now);
    },
    /**
     * 设置时间控件
     * @param triggerElem   触发弹出时间窗口的元素
     * @param defaultValue  默认时间
     */
    setTimepicker: function (triggerElem, defaultValue) {
        //设置开始时间
        laydate.render({
            elem: triggerElem
            , trigger: 'click'
            , type: 'time'
            , format: 'HH:mm:ss'
            , btns: ['now', 'confirm']
            , value: defaultValue
        });
    },

    //初始化标题
    initTitle: function () {
        // console.log(DetailedTimes.type);    //测试

        var html = '日志详情列表：';
        //提供方
        var providerName = CommonFunc.isEmpty(DetailedTimes.serverAppName) ? '所有提供方' : DetailedTimes.serverAppName;
        //消费方
        var consumerName = CommonFunc.isEmpty(DetailedTimes.clientAppName) ? '所有消费方' : DetailedTimes.clientAppName;

        //设置来源系统
        html += (DetailedTimes.role == BizStable.MonitorRole.Provider) ? providerName : consumerName;

        //设置时间相关
        if (DetailedTimes.type == "最近一小时" || DetailedTimes.type == "当前一小时" || DetailedTimes.type == "当前小时"
            || DetailedTimes.type == BizStable.MonitorDateType.CUR_HOUR) {
            DetailedTimes.ajaxQueryMessageList = DetailedTimes.ajaxQueryMessageListLastHour;
            html += " > 当前小时";
            DetailedTimes.type = BizStable.MonitorDateType.CUR_HOUR;
        } else if (DetailedTimes.type == "当天" || DetailedTimes.type == BizStable.MonitorDateType.TODAY) {
            DetailedTimes.ajaxQueryMessageList = DetailedTimes.ajaxQueryMessageListToday;
            html += " > 当天";
            DetailedTimes.type = BizStable.MonitorDateType.TODAY;
        } else if (DetailedTimes.type == "指定小时" || DetailedTimes.type == BizStable.MonitorDateType.HOUR) {
            DetailedTimes.ajaxQueryMessageList = DetailedTimes.ajaxQueryMessageListHour;
            html += ' > ' + DetailedTimes.time;
            DetailedTimes.type = BizStable.MonitorDateType.HOUR;
        } else if (DetailedTimes.type == "告警下钻") {
            DetailedTimes.ajaxQueryMessageList = DetailedTimes.ajaxQueryMessageListHour;
            html += ' > ' + DetailedTimes.type;
        } else if (DetailedTimes.type == BizStable.MonitorDateType.ERROR_QUEUE) {
            DetailedTimes.ajaxQueryMessageList = DetailedTimes.ajaxQueryMessageListErrorQueue;
            html += ' > 错误列表';
        }

        //设置目标系统
        html += " > ";
        html += (DetailedTimes.role == BizStable.MonitorRole.Provider) ? consumerName : providerName;

        //服务名称
        if (CommonFunc.isEmpty(DetailedTimes.transactionTypeName)) {
            html += " > 所有服务";
        } else {
            html += ' > ' + DetailedTimes.transactionTypeName;
        }
        //提供方主机
        if (CommonFunc.isEmpty(DetailedTimes.serverIpAddress)) {
            html += " > 所有主机";
        } else {
            html += ' > ' + DetailedTimes.serverIpAddress;
        }
        //消费方主机
        if (CommonFunc.isEmpty(DetailedTimes.clientIpAddress)) {
            html += " > 所有客户端";
        } else {
            html += ' > ' + DetailedTimes.clientIpAddress;
        }
        $('#detailedTitle').html(html);
    },
    //初始化搜索条件
    initQuery: function () {
        //初始化筛选日期
        DetailedTimes.initDatepicker();
        //初始化筛选时间
        DetailedTimes.initTimepicker();

        //设置日期是否显示
        if (DetailedTimes.queryDateShow == true) {
            $(DetailedTimes.queryDateWrapperID).removeClass("hidden");
        } else {
            $(DetailedTimes.queryDateWrapperID).addClass("hidden");
        }

        //设置结果状态的下拉列表
        if (DetailedTimes.status == '执行成功') {
            DetailedTimes.status = BizStable.MonitorStatus.SUCCESS;
        } else if (DetailedTimes.status == '执行失败') {
            DetailedTimes.status = BizStable.MonitorStatus.FAILURE;
        }
        if (!CommonFunc.isEmpty(DetailedTimes.status)) {
            // CommonFunc.setSelected(DetailedTimes.queryStatusID, DetailedTimes.status);
            $(DetailedTimes.queryStatusID).val(DetailedTimes.status);
        }
        if (DetailedTimes.statusSelectShow) {
            $(DetailedTimes.queryStatusID).removeClass("hidden");
            if (DetailedTimes.statusSelectEnable) {
                $(DetailedTimes.queryStatusID).removeAttr("disabled");
            } else {
                $(DetailedTimes.queryStatusID).attr("disabled", true);
            }
        } else {
            $(DetailedTimes.queryStatusID).addClass("hidden");
        }

        // //设置耗时的下拉列表
        // if (DetailedTimes.duringSelectShow) {
        //     $(DetailedTimes.queryDuringID).removeClass("hidden");
        // } else {
        //     $(DetailedTimes.queryDuringID).addClass("hidden");
        // }

        //设置时间范围控件是否显示
        if (DetailedTimes.type == BizStable.MonitorDateType.TODAY) {
            $(DetailedTimes.timeRangeID).removeClass("hidden");
        } else {
            $(DetailedTimes.timeRangeID).addClass("hidden");
        }
    },
    //绑定详情列表
    bindTableData: function () {
        //获取查询字段
        var columnName = $(DetailedTimes.queryColumnID).val();
        //获取关键字
        var keyWords = $(DetailedTimes.queryWordID).val();
        if (!CommonFunc.isEmpty(keyWords)) {
            keyWords = columnName + "=" + keyWords;
        }

        //日期
        var queryDate = $(DetailedTimes.queryDateID).val();
        if (CommonFunc.isEmpty(queryDate)) {
            CommonFunc.msgFa("请选择日期！");
            return;
        }
        //开始时间
        var startTime = $(DetailedTimes.queryTimeRangeStartID).val();
        if (CommonFunc.isEmpty(startTime)) {
            CommonFunc.msgFa("请选择开始时间！");
            return;
        } else {
            DetailedTimes.limitStartTime = queryDate + ' ' + startTime;
        }
        //结束时间
        var endTime = $(DetailedTimes.queryTimeRangeEndID).val();
        if (CommonFunc.isEmpty(endTime)) {
            CommonFunc.msgFa("请选择结束时间！");
            return;
        } else {
            DetailedTimes.limitEndTime = queryDate + ' ' + endTime;
        }

        //如果选择的不是mainId和MessageID，则必须限制时间范围
        if (!CommonFunc.isEmpty(keyWords)
            && BizStable.MonitorDatalistKey.MessageID.key != columnName && BizStable.MonitorDatalistKey.mainId.key != columnName) {
            var startTime = new Date(DetailedTimes.limitStartTime).getTime(),
                endTime = new Date(DetailedTimes.limitEndTime).getTime();
            var timeGap = endTime - startTime, HOUR = 1000 * 60 * 60;
            if (timeGap > HOUR * DetailedTimes.LIMIT_HOURS) {
                CommonFunc.msgFa("按“" + BizStable.MonitorDatalistKey[columnName].name + "”关键字查询时，采用了模糊查询模式，速度较慢，时间范围须在"
                    + DetailedTimes.LIMIT_HOURS + "小时内！");
                return;
            }
        }
        if (!CommonFunc.isEmpty(keyWords)) {
            DetailedTimes.limitTimeChanged = true;
        }

        //保存状态
        DetailedTimes.status = $(DetailedTimes.queryStatusID).val();
        if (!CommonFunc.isEmpty(DetailedTimes.status)) {
            DetailedTimes.limitTimeChanged = true;
        }

        //前n条记录
        var durationTop = $(DetailedTimes.queryDuringID).val();
        durationTop = CommonFunc.isEmpty(durationTop) ? "" : true;
        if (!CommonFunc.isEmpty(durationTop)) {
            DetailedTimes.limitTimeChanged = true;
        }

        var columns = [
            {
                title: '服务名称', data: 'svcName', width: "18%",
                render: function (data, type, row, meta) {

                    var id = row.messageId;
                    if (!CommonFunc.isEmpty(data) && data.length > 10) {
                        data = data.substr(0, 10) + '..';
                    }
                    var html = '<i class="fa icon cp fa-plus-square-o" onclick="DetailedTimes.showChildList(this,'
                        + meta.row + ')"></i> ' + data;

                    return html;
                }
            },
            {title: '提供方IP', data: 'serverIpAddress', render: $.fn.dataTable.render.ellipsisNew()},
            {title: '消费方名称', data: 'clientAppName', width: "15%", render: $.fn.dataTable.render.ellipsisNew()},
            {title: '消费方IP', data: 'clientIpAddress', render: $.fn.dataTable.render.ellipsisNew()},
            {
                title: '耗时', data: 'useTime',
                render: function (data) {
                    return data + "ms";
                }
            }, {
                title: '状态', data: 'status',
                render: function (data) {
                    var html;
                    if (data == "成功") {
                        html = '<i class="fa s-icon fa-check-circle prject-color-success"></i>'
                            + '<span class="prject-color-success"> ' + data + '</span>'
                    } else {
                        html = '<i class="fa s-icon fa-times-circle prject-color-failure"></i>'
                            + '<span class="prject-color-failure"> ' + data + '</span>'
                    }
                    return html;
                }
            },
            {title: '时间', data: 'startTime', width: '130px', render: $.fn.dataTable.render.ellipsisNew()},
            {
                title: '详情', data: null, className: 'optColumn', width: '40px',
                render: function (data, type, row, meta) {
                    // console.log(data);  //测试
                    // console.log(meta);  //测试

                    var html = '<a class="cp" onclick="DetailedTimes.detail(this, -1, ' + meta.row + ')">详情</a> ';
                    return html;
                }
            }
        ];
        CommonTable.createTableAdvanced(DetailedTimes.tableID, function (data, callback, settings) {

            //拼接Ajax参数
            var datas = {
                serverAppName: DetailedTimes.serverAppId,
                transactionTypeName: DetailedTimes.transactionTypeId,
                serverIpAddress: DetailedTimes.serverIpAddress,
                clientAppName: DetailedTimes.clientAppId,
                clientIpAddress: DetailedTimes.clientIpAddress,
                status: DetailedTimes.status,
                datetype: DetailedTimes.type,
                time: DetailedTimes.time,
                limitStartTime: DetailedTimes.limitStartTime,
                limitEndTime: DetailedTimes.limitEndTime,
                keyWords: keyWords,
                durationTop: durationTop,
                start: data.start,
                pageSize: data.length
            };
            if (DetailedTimes.limitTimeChanged == false) {
                datas.totalCount = DetailedTimes.totalCount;
            }
            var reqData = {
                datas: JSON.stringify(datas)
            };

            //ajax请求数据
            CommonFunc.ajaxPostForm(DetailedTimes.ajaxQueryMessageList, reqData, function (respData) {
                // console.log(respData);  //测试

                if (CommonFunc.isEmpty(respData.transactionMessages)) {
                    respData.transactionMessages = [];
                }
                callback({
                    recordsTotal: respData.totalSize,//过滤之前的总数据量
                    recordsFiltered: respData.totalSize,//过滤之后的总数据量
                    data: respData.transactionMessages
                });

                //调整高度
                CommonFunc.maxHeightToFrame(DetailedTimes.idTreeWrapper, DetailedTimes.idWrapperId2);
            });

        }, columns);
    },
    //展开子列表内容
    showChildList: function (obj, rowIndex) {
        if ($(obj).hasClass("fa-plus-square-o")) {
            var tableHtml = '<tr style="display: none"><td colspan="12" style="padding: 10px 10px 10px 10px"><div>' +
                '<table class="mytable" style="border:1px solid #E5E5E5"> <thead>';
            tableHtml += '<tr>';
            tableHtml += '<th class="firstChild numeric">序号</th>';
            tableHtml += '<th class="numeric">服务步骤</th>';
            tableHtml += ' <th class="numeric">耗时</th>';
            tableHtml += ' <th class="numeric">状态</th>';
            tableHtml += ' <th class="numeric">开始时间</th>';
            tableHtml += ' <th class="numeric optColumn">详情</th>';
            tableHtml += '</tr></thead><tbody>';

            //获取父级表的对象
            var rowData = $(obj).parent().parent().parent().parent().dataTable().fnGetData()[rowIndex];
            var children = rowData.children;
            //遍历子级表的内容
            if (children != null && children.length > 0) {
                $.each(children, function (i, v) {
                    var statusHtml;
                    if (v.status == "成功") {
                        statusHtml = '<i class="fa s-icon fa-check-circle prject-color-success"></i> '
                            + '<span class="prject-color-success"> ' + v.status + '</span>'
                    } else {
                        statusHtml = '<i class="fa s-icon fa-times-circle prject-color-failure"></i> '
                            + '<span class="prject-color-failure"> ' + v.status + '</span>'
                    }
                    tableHtml += '<tr><td>' + (i + 1) + '</td><td>' + v.transactionName + '</td><td>' + v.useTime + 'ms</td><td>'
                        + statusHtml + '</td><td>' + v.startTime + '</td><td class="optColumn">'
                        + '<a class="cp" onclick="DetailedTimes.detail(this, ' + i + ', ' + rowIndex + ')">详情</a></td></tr>';
                });
            }
            $(obj).parents("tr").after(tableHtml);
            $(obj).parents("tr").next("tr").fadeIn();
            $(obj).addClass("fa-minus-square-o").removeClass("fa-plus-square-o");
        } else {
            $(obj).parents("tr").next("tr").fadeOut();
            $(obj).parents("tr").next("tr").remove();
            $(obj).addClass("fa-plus-square-o").removeClass("fa-minus-square-o");
        }

        //调整高度
        CommonFunc.maxHeightToFrame(DetailedTimes.idTreeWrapper, DetailedTimes.idWrapperId2);
    },
    /**
     * 查看消息流程与明细
     * @param obj   产生动作的对象
     * @param idx   -1 父级， 其他为子级明细的索引号（从0开始）
     * @param rowIndex
     */
    detail: function (obj, idx, rowIndex) {
        // console.log(obj);    //测试
        // console.log($(obj).parent().parent().parent().parent());    //测试
        // console.log(idx);    //测试
        // console.log(rowIndex);    //测试

        //弹出窗口
        $("#xqEdit").modal("show");

        var html;
        //获取行数据
        var tableObj;
        if (idx < 0) {
            //父表对象
            tableObj = $(obj).parent().parent().parent().parent();
        } else {
            //子表对象
            tableObj = $(obj).parent().parent().parent().parent().parent().parent().parent().parent().parent();
        }
        var rowData = tableObj.dataTable().fnGetData()[rowIndex];

        // console.log(rowData);    //测试

        //生成明细信息
        html = "";
        var dataList;
        if (idx < 0) {
            dataList = rowData.datas;
        } else {
            dataList = rowData.children[idx].datas;
        }

        // console.log(idx);    //测试
        // console.log(dataList);    //测试

        for (var key in dataList) {
            var content = JqCommon.parseXMLString(dataList[key]);
            //根据内容设置编辑框高度
            var len = CommonFunc.isEmpty(content) ? 0 : content.length;
            var height;
            if (len < 100) {
                height = 60;
            } else if (len < 500) {
                height = 130;
            } else {
                height = 250;
            }

            //设置key的友好名称
            if (BizStable.MonitorDatalistKey.hasOwnProperty(key)) {
                key = BizStable.MonitorDatalistKey[key].name;
            }

            html += '<div class="portlet box blue-sharp" style="1px solid #666666">' +
                '<div class="portlet-title">' +
                '<div class="caption">' + key + '</div>' +
                '<div class="tools">' +
                '<a href="javascript:void(0);" onclick="DetailedTimes.format(this)" ' +
                '   class="glyphicon glyphicon-transfer prject-color-white" title="格式化显示"></a>' +
                '<a href="javascript:;" class="collapse" data-original-title="" title=""></a>' +
                '</div></div>' +
                '<div class="portlet-body" style="height: ' + height + 'px;padding: 0">' +
                '<textarea style="width: 100%;border: 0;height: ' + height + 'px;resize: none" readonly="readonly">' +
                content + '</textarea></div>' +
                '</div>';
        }
        html = html + "";
        $("#detailContent").html(html);

        //显示消息流程（只在父级对象显示流程）
        if (idx < 0) {
            html = DetailedTimes.showServiceFlow(DetailedTimes.serverAppName, rowData.clientAppName,
                dataList[BizStable.MonitorDatalistKey.router.key], rowData.timestamp, rowData.useTime,
                dataList[BizStable.MonitorDatalistKey.sourceTime.key]);
            $("#serviceFlow").html(html);
            $("#serviceFlow").prev().removeClass('hidden');
        } else {
            $("#serviceFlow").html('');
            $("#serviceFlow").prev().addClass('hidden');
        }

        //获取框架容器的高度
        var heightPageContent = CommonFunc.getFrameHeight();
        //根据框架容器高度，设置弹窗高度(上下空白共预留120像素)
        var heightModal = heightPageContent - 120;
        $(DetailedTimes.idModalScrollDiv).css('height', heightModal + 'px');

        // console.log(heightModal);   //测试

        //设置滚动条
        CommonFunc.setScrollBarYByElement(DetailedTimes.idModalScrollDiv);

        // var datas = {serverAppName: DetailedTimes.serverAppId, messageId: messageId, index: idx};
        // //生成消息内容列表
        // JqAjax.postByDefaultErrorCatch(DetailedTimes.ajaxQueryTransactionMessageListDetail, datas, function (result) {
        //     console.log(result);    //测试
        //
        //     var html;
        //
        //     //生成明细信息
        //     html = "";
        //     var json = result.data;
        //     for (var key in json) {
        //         var content = JqCommon.parseXMLString(json[key]);
        //         //根据内容设置编辑框高度
        //         var len = CommonFunc.isEmpty(content) ? 0 : content.length;
        //         var height = 100;
        //         if (len < 200) {
        //             height = 50;
        //         } else if (len < 500) {
        //             height = 100;
        //         } else if (len < 1000) {
        //             height = 180;
        //         } else {
        //             height = 250;
        //         }
        //
        //         //设置key的友好名称
        //         if (BizStable.MonitorDatalistKey.hasOwnProperty(key)) {
        //             key = BizStable.MonitorDatalistKey[key].name;
        //         }
        //
        //         html += '<div class="portlet box blue-sharp" style="1px solid #666666">' +
        //             '<div class="portlet-title">' +
        //             '<div class="caption">' + key + '</div>' +
        //             '<div class="tools">' +
        //             '<a href="javascript:void(0);" onclick="DetailedTimes.format(this)" ' +
        //             '   class="glyphicon glyphicon-transfer prject-color-white" title="格式化显示"></a>' +
        //             '<a href="javascript:;" class="collapse" data-original-title="" title=""></a>' +
        //             '</div></div>' +
        //             '<div class="portlet-body" style="height: ' + height + 'px;padding: 0">' +
        //             '<textarea style="width: 100%;border: 0;height: ' + height + 'px;resize: none" readonly="readonly">' +
        //             content + '</textarea></div>' +
        //             '</div>';
        //     }
        //     html = html + "";
        //     $("#detailContent").html(html);
        //
        //     //设置滚动条
        //     CommonFunc.setScrollBarYByElement("#modalScrollDiv");
        // });

        // JqAjax.postByDefaultErrorCatch(DetailedTimes.ajaxGetServiceFlowShow, {
        //     serverAppName: DetailedTimes.serverAppId,
        //     messageId: messageId
        // }, function (obj) {
        //     // console.log(obj);   //测试
        //
        //     var html = JqCommon.getServiceFlowShow(obj);
        //     $("#serviceFlow").html(html);
        // });
    },
    /**
     * 显示消息流程
     * @param provider 提供方名称
     * @param consumer 消费方名称
     * @param router 路由名称
     * @param startTimestamp 路由收到的开始时间戳
     * @param during    总耗时
     * @param wrapperCallTime   路由内部调用提供方服务的开始时间与结束时间，格式：2018-03-27 16:45:11.313&2018-03-27 16:45:11.323
     */
    showServiceFlow: function (provider, consumer, router, startTimestamp, during, wrapperCallTime) {
        // console.log(wrapperCallTime);   //测试
        // console.log(wrapperCallTime.indexOf("&"));   //测试

        var resultFlow = [];

        //消费方请求
        resultFlow.push({
            name: consumer + "请求"
        });

        //根据是否有路由名称，决定流程图的展示方式
        if (!CommonFunc.isEmpty(router)) {
            var wrapperSplitIndex = wrapperCallTime.indexOf("&");
            var wrapperStartTimestamp = new Date(wrapperCallTime.substring(0, wrapperSplitIndex)).getTime();
            var wrapperEndTimestamp = new Date(wrapperCallTime.substring(wrapperSplitIndex + 1)).getTime();

            //路由接收
            resultFlow.push({
                name: router + "接收并转发",
                during: wrapperStartTimestamp - startTimestamp
            });

            //提供方接收
            resultFlow.push({
                name: provider + "接收",
                during: wrapperEndTimestamp - wrapperStartTimestamp
            });

            //提供方应答
            resultFlow.push({
                name: provider + "应答",
                during: during - (wrapperEndTimestamp - startTimestamp)
            });

            //路由应答
            resultFlow.push({
                name: router + "应答"
            });
        } else {
            //提供方接收
            resultFlow.push({
                name: provider + "接收",
                during: during
            });

            //提供方应答
            resultFlow.push({
                name: provider + "应答"
            });
        }

        //消费方接收
        resultFlow.push({
            name: consumer + "接收"
        });

        //生成流程图的HTML
        var html = [];
        var htmlItem, flowItem;
        for (var i = 1, len = resultFlow.length; i <= len; i++) {
            flowItem = resultFlow[i - 1];

            //序号与圆圈
            htmlItem = '<div class="steps-wrap"><div class="steps-round cp">' + i + '</div>';
            //耗时与横线
            if (i < len) {
                if (!CommonFunc.isEmpty(flowItem.during)) {
                    htmlItem += '<div class="steps-bar"><small style="margin-left: 25px; position: relative; top: -30px;">耗时'
                        + flowItem.during + 'ms</small></div>';
                } else {
                    htmlItem += '<div class="steps-bar"></div>';
                }
            }
            //右箭头
            if (i < len) {
                htmlItem += '<i class="fa  step-icon  fa-caret-right fa-2x pull-right"></i>';
            }
            //服务名称
            htmlItem += '<label class="steps-label"';
            if (i == len) {
                htmlItem += ' style="position: relative; top: 18px;"';
            }
            htmlItem += '>' + flowItem.name + '</label>';
            htmlItem += '</div>';
            html.push(htmlItem);
        }

        return html;
    },

    //到后台格式化消息（支持XML、JSON，后续需要支持HL7）
    format: function (me) {
        // console.log(me);    //测试

        var rawData = $($(me).parent().parent().next().children()[0]).val();
        //ajax请求数据
        CommonFunc.ajaxPostForm(BizStable.CommonURL.dataFormat, {
            rawData: rawData
        }, function (resp) {
            // console.log(resp);  //测试

            if (resp.success) {
                CommonFunc.msgShowInfo("格式化信息", resp.data, true);
            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }
        });
    },

};