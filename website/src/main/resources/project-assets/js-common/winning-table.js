(function ($) {
    'use strict';

    var WinningTable = function (el, options) {
        this.options = $.extend({language: WinningTable.Language}, WinningTable.DEFAULTS, options);
        this.$el = $(el);
        //this.$dataTable = null;
        this.dataTables = null;

        this.init();
    };

    WinningTable.DEFAULTS = {
        classes: 'table',
        select: {style: 'os'},
        dom: "<'row'<'col-sm-6 button-toolbar'><'col-sm-6'Bf>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        pageLength: 50,
        lengthChange: false,
        ordering: false,
        searching: false,
        info: true,
        processing: true,
        fixedHeader: {
            headerOffset: 50
        },
        "pagingType":   "full_numbers"
    };


    WinningTable.Language = {
        sProcessing: "处理中...",
        sLengthMenu: "显示 _MENU_ 项结果",
        sZeroRecords: "没有匹配结果",
        sInfo: "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
        sInfoEmpty: "显示第 0 至 0 项结果，共 0 项",
        sInfoFiltered: "(由 _MAX_ 项结果过滤)",
        sInfoPostFix: "",
        sSearch: "搜索：",
        sUrl: "",
        sEmptyTable: "表中数据为空",
        sLoadingRecords: "载入中...",
        // old, obsolete, using sThousands instead
        sInfoThousands: ",",
        sThousands: ",",
        oPaginate: {
            sFirst: "首页",
            sPrevious: "上页",
            sNext: "下页",
            sLast: "末页"
        },
        oAria: {
            sSortAscending: ": 以升序排列此列",
            sSortDescending: ": 以降序排列此列"
        },
        select: {
            rows: {
                _: "已选择 %d 行",
                0: "点击选择行",
                1: "已选择 1 行"

            }
        }
    };

    WinningTable.prototype.init = function () {
        this.refreshDataTable(this.options);
        //$.fn.dataTable.FixedHeader(this.dataTables);
    };

    WinningTable.prototype.refreshDataTable = function (option) {
        this.dataTables = $(this.$el).DataTable(option);
        if (this.options.toolbar != null) {
            var toolbar = $(this.options.toolbar).clone(true);
            (toolbar).appendTo($('.button-toolbar'));
            toolbar.removeClass("hidden");
            $(this.options.toolbar).addClass("hidden");
        }
    };

    WinningTable.prototype.destoryDataTable = function () {
        var newOptions = jQuery.extend(true, {}, this.options);
        this.dataTables.destroy();
        this.options = newOptions;
        $(this.$el.selector).empty(); //清除表头
    }
    //sysTable: serversyshistory
    // xuehao - 2018-03-23 : 新增了callback参数，表示回调函数
    WinningTable.prototype.queryDataInPage = function (ajaxurl, reqdatas, callback) {
        this.destoryDataTable();
        var previousPageST = "", previousPage = "";

        var queryOption = {
            serverSide: true,
            ajax: function (data, callback, settings) {

                var reqParams = {
                    pageSize: data.length,
                    start: data.start,
                    search: data.search.value,
                    previousPage: previousPage,
                    previousPageTS: previousPageST
                };
                if(data.order !=undefined && data.order !=null && data.order.length>0){
                    reqParams.ordernum=data.order[0].column;
                    reqParams.ordervalue=data.order[0].dir;
                }
                var params = $.extend(reqParams, reqdatas);
                JqAjax.postByDefaultErrorCatch(ajaxurl, {datas:JSON.stringify(params)}, function (res) {
                    //console.log(res);
                    $("#loading").hide();
                    callback({
                        recordsTotal: res.totalSize,
                        recordsFiltered: res.totalSize,
                        data: res.transactionMessages
                    });
                    //上一次页码中最大、最小时间戳
                    previousPageST = JqCommon.getTSInPrevious(res.transactionMessages);
                });
                //上一次选中页码
                previousPage = data.start / 10 + 1;
            }
        };

        queryOption = $.extend(queryOption, this.options);

        this.refreshDataTable(queryOption);

        // xuehao - 2018-03-23 : 新增
        if(callback) {
            callback();
        }
    };

    WinningTable.prototype.queryHistoryInPage = function (ajaxurl, reqdatas, total,fail) {
        this.destoryDataTable();

        var queryOption = {
            serverSide: true,
            ajax: function (data, callback, settings) {
                var params = {
                    pageSize: data.length,
                    start: data.start,
                    targetDate:reqdatas
                };
                JqAjax.postByDefaultErrorCatch(ajaxurl, {params:JSON.stringify(params)}, function (res) {
                    $("#loading").hide();
                    callback({
                        recordsTotal: res === "" ? 0 : res.serverList.length,
                        recordsFiltered: res === "" ? 0 : res.serverList.length,
                        data: res === "" ? "" : res.appCountList
                    });
                    if (res){
                        total.text(res.dailyTotalCount);
                        fail.text(res.dailyFailCount);
                    }
                });
            }
        };
        queryOption = $.extend(queryOption, this.options);
        this.refreshDataTable(queryOption);
    };

    WinningTable.prototype.queryServiceInfoInPage = function (ajaxurl, reqdatas) {
        this.destoryDataTable();

        var queryOption = {
            serverSide: true,
            ajax: function (data, callback, settings) {

                var reqParams = {
                    pageSize: data.length,
                    start: data.start,
                    search: data.search.value
                };
                if(data.order !=undefined && data.order !=null && data.order.length>0){
                    reqParams.ordernum=data.order[0].column;
                    reqParams.ordervalue=data.order[0].dir;
                }
                var params = $.extend(reqParams, reqdatas);
                JqAjax.postByDefaultErrorCatch(ajaxurl, {datas:JSON.stringify(params)}, function (res) {
                    //$("#loading").show();
                    callback({
                        recordsTotal: res.data == null ? 0 : res.data.totalSize,
                        recordsFiltered: res.data == null ? 0 : res.data.totalSize,
                        data: res.data == null ? "" : res.data.transactionMessages
                    });
                });
            }
        };
        queryOption = $.extend(queryOption, this.options);

        this.refreshDataTable(queryOption);
    };

    WinningTable.prototype.queryData = function (ajaxurl, reqdatas) {
        //{}
        this.destoryDataTable();
        var queryOption = {
            //ajax: {
            //    url: ajaxurl,
            //    dataSrc: 'datas',
            //    data: reqdatas
            //}
            ajax: function (data, callback, settings) {
                var reqParams = {
                    ordernum:data.order[0].column,
                    ordervalue: data.order[0].dir
                };
                var params = $.extend(reqParams, reqdatas);
                JqAjax.postByDefaultErrorCatch(ajaxurl, {datas:JSON.stringify(params)}, function (res) {
                    if (!res.success) {
                        swal("发生错误", res.errorMsg, "error");
                        callback({
                            recordsTotal: 0,
                            data: []
                        });
                        return;
                    }
                    callback({
                        recordsTotal: res.datas.length,
                        data: res.datas
                    });
                });
            }
        };
        queryOption = $.extend(queryOption, this.options);
        this.refreshDataTable(queryOption);
    }

    WinningTable.prototype.getSelectedDatas = function () {
        return this.dataTables.rows({selected: true}).data();
    };

    WinningTable.prototype.getSelectedRows = function () {
        return this.dataTables.rows({selected: true});
    };

    WinningTable.prototype.draw = function () {
        return this.dataTables.draw();
    };

    $.fn.winningTable = function (option) {
        var $this = $(this);
        return new WinningTable($this, option);
    }

})(jQuery);