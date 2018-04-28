<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>

<@list.head>
    <#assign contextPath=request.contextPath>
<link rel="stylesheet" type="text/css"
      href="${contextPath}/project-assets/plugins/bootstrap-datepicker/css/datepicker.css"/>
<style type="text/css">

    .center-block {
        float: none;
        display: block;
        margin-left: auto;
        margin-right: auto;
    }

    .col-xs-8ths, .col-sm-8ths, .col-md-8ths, .col-lg-8ths {
        position: relative;
        min-height: 1px;
        padding-right: 10px;
        padding-left: 10px;
    }

    @media ( min-width: 768px) {
        .col-sm-8ths {
            width: 12.5%;
            float: left;
        }
    }

    @media ( min-width: 992px) {
        .col-md-8ths {
            width: 12.5%;
            float: left;
        }
    }

    @media ( min-width: 1200px) {
        .col-lg-5ths {
            width: 12.5%;
            float: left;
        }
    }

    .rightborder {
        border-right: 1px solid #f0f0f0;
    }

    .m-l-10 {
        margin-left: 10px;
    }

    .f-s-14 {
        font-size: 14px;
    }

    .f-s-12 {
        font-size: 12px;
    }

    .color-888 {
        color: #888;
    }

    .btn-circle {
    }

    .btn-group-sm > .btn, .btn-sm {
        font-size: 13px;
    }

    .btn-icon-only > [class^="icon-"], .btn-icon-only > i {
        text-align: center;
        margin-top: 1px;
    }

    .div_overflowhidden {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .mychart {
        height: 300px;
        width: 100%;
        position: relative
    }

    .trendLineEChartDIV {
        width: 70%;
        height: 100%
    }

    .trendPierEChartDIV {
        width: 30%;
        height: 100%
    }

    .set-btn-group {
        position: absolute;
        top: 10px;
        left: 50px;

    }

    .set-btn-group > button:first-child {

        border-radius: 2px 0px 0px 2px !important;
    }

    .set-btn-group > button:last-child {

        border-radius: 0px 2px 2px 0px !important;
    }

    .qst {
        margin-left: 20px;
        color: #666;
        line-height: 30px;
        display: none;
    }

    .ss_div {
        width: 40px;
        top: 1px;
        position: absolute;
        right: 10px;
        top: 120px;
    }

    #sh_zk {
        height: 20px;
        width: 20px;
        padding: 1px 3px 2px 3px;
        margin-left: 5px;
    }

    .fixed {
        width: 95px;
        height: 75px;
        margin: 0 auto;
        left: 0;
        right: 0;
        top: 20%;
        position: fixed;
        z-index: 100000;
        text-align: center;
        background: rgba(0, 0, 0, 0.55);
        box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.10);
    }

    .fixed span {
        height: 100%;
        display: inline-block;
        vertical-align: middle;
    }

    .fixed div {
        margin-top: -20px;
        color: #fff;
    }

    .bb1 {
        border-bottom: 1px solid #e2e2e2;
    }

    .main {
        margin-bottom: 15px;
        background: #fff;
    }

    #summary {
        padding: 30px 0;
        border-top: 1px solid #f0f0f0;
        border-bottom: 1px solid #f0f0f0;
    }

    table.summary {
        table-layout: fixed;
        width: 100%;
    }

    table.summary tr td {
        text-align: center;
        border-right: 1px solid #f0f0f0;
        padding: 0 20px;
    }

    table.summary tr td:last-child {
        border-right-width: 0;
    }

    table.summary tr td div.text {
        color: #888;
        font-size: 14px;
    }

    table.summary tr td span.summary-ellipsis {
        white-space: nowrap;
    }

    table.summary tr td span.value {
        margin-top: 8px;
        font-size: 20px;
        color: #444;
    }

    table.summary tr td span.valueUnit {
        color: #aaa;
        font-size: 14px;
    }

    table.summary tr td span.summary-ellipsis {
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .tooltip-inner {
        border-radius: 4px !important;
    }

    .portlet {
        margin-bottom: 10px;
    }

    .cont-title {
        display: inline-block;
        margin-top: 0;
        font-size: 14px;
        line-height: 20px;
        color: #333;
        margin-left: 5px;
        padding-left: 10px;
        -ms-border-left: 4px solid #14AF8D;
        -moz-border-left: 4px solid #14AF8D;
        -webkit-border-left: 4px solid #14AF8D;
        border-left: 4px solid #14AF8D;
        behavior: url(../images/ie-css3.htc);
        font-weight: bold;
    }

    .col-md-1 {
        width: 10% !important;
    }

    .clircle.active {
        color: #FFFFFF !important;
        font-size: 16px;
        background-color: #16B08E !important;
    }

    .unslider ul, .unslider ol {
        padding: 0;
    }

    .unslider {
        position: relative;
        overflow: auto;
        width: 100%;
    }

    .unslider li {
        list-style: none;
    }

    .unslider ul li {
        float: left;
    }

    .unslider .arrow {
        position: absolute;
        top: 100px;
    }

    .unslider .al {
        left: 15px;
    }

    .unslider .ar {
        right: 15px;
    }

    .unslider .dots {
        position: absolute;
        left: 0;
        right: 0;
        text-align: center;
        bottom: 10px;
        margin-bottom: 0px;
    }

    .unslider .dots li {
        display: inline-block;
        width: 12px;
        height: 12px;
        margin: 0 4px;
        text-indent: -999em;
        border: 2px solid #434343;
        border-radius: 6px !important;
        cursor: pointer;
        opacity: .4;
        -webkit-transition: background .5s, opacity .5s;
        -moz-transition: background .5s, opacity .5s;
        transition: background .5s, opacity .5s;
    }

    .unslider .dots li.active {
        background: #fff;
        opacity: 1;
    }

    .block-title {
        padding-top: 10px;
        font-size: 14px;
        color: #444;
    }

    .clear {
        padding: 0px !important;
        padding: 0px !important;
    }

    .nav-tabs, .nav-pills {
        margin-bottom: 0px !important;
    }

    #svcTab.nav > li > a,
    #svcTab.nav > li > a:hover {
        color: #4d6b8a !important;
        background-color: #fff !important;
    }

    #svcProvider,
    #svcConsumer {
        width: 100% !important;
        height: 360px !important;
    }

    #svcProvider .itemRow,
    #svcConsumer .itemRow {
        padding-left: 0;
        padding-right: 0;
        margin-bottom: 10px;
    }

    #svcProvider .itemCell {
        height: 65px;
    }

    #svcConsumer .itemCell {
        height: 87px;
    }

    #svcProvider .title,
    #svcConsumer .title {
        margin-top: 5px;
        border: solid 1px #ccc;
        padding: 10px 3px;
        -webkit-border-radius: 15px !important;
        -moz-border-radius: 15px !important;
        border-radius: 15px !important;
        text-align: center;
        overflow: hidden;
    }

    #svcProvider .totalCount,
    #svcConsumer .totalCount {
        font-weight: 700;
    }

    #svcProvider .badge,
    #svcConsumer .badge {
        position: absolute;
        top: -3px;
        right: 10px;
    }

    #svcProvider .emptytip,
    #svcConsumer .emptytip {
        text-align: center;
        margin-top: 130px;
    }

</style>
</@list.head>

<!-- BEGIN PAGE -->
<div class="project-body-container">
    <div class="row " style="margin-top: 15px;">
        <div class="col-md-12 col-sm-12">
            <div class="main">
                <div id="summary" class="section ">
                    <table class="summary">
                        <tbody>
                        <tr>
                            <td>
                                <div class="text">今日请求</div>
                                <span class="value summary-ellipsis totalCount">**</span>
                                <span class="valueUnit">次</span>
                            </td>
                            <td>
                                <div class="text">今日异常</div>
                                <span class="value summary-ellipsis failCount">0<span class="valueUnit">次</span></span>
                            </td>
                            <td>
                                <div class="text">
                                    接入系统
                                    <i id="appSizeTip" class="fa fa-question-circle shape-link hidden"></i>
                                </div>
                                <span class="value summary-ellipsis appSize">**</span>
                                <span class="valueUnit">个</span>
                            </td>
                            <td>
                                <div class="text">
                                    接入服务
                                    <i id="serviceSizeTip" class="fa fa-question-circle shape-link hidden"></i>
                                </div>
                                <span class="value summary-ellipsis serviceSize">**</span>
                                <span class="valueUnit">个</span>
                            </td>
                            <td>
                                <div class="text">平台运行</div>
                                <span class="value summary-ellipsis runTime">**</span>
                                <span class="valueUnit">天</span>
                            </td>
                            <td class="last">
                                <div class="text">历史请求</div>
                                <span class="value summary-ellipsis historyTotalCount">**</span>
                                <span class="valueUnit">次</span>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="row" style="padding: 0px 15px 0px 0;">
                    <div class="col-md-6 col-sm-6">
                        <i id="showTipsImg" class="fa fa-question-circle shape-link hidden"
                           style="position: absolute;left:30px;top:10px; z-index: 999999;"></i>
                        <div id="starChart" style="width:100%;height: 400px;"></div>
                    </div>

                    <div id="chart-details" class="col-md-6 col-sm-6 padding-top-15 padding-left-0">
                        <ul id="svcTab" class="nav nav-tabs">
                            <li class="active">
                                <a href="#svcProvider" data-toggle="tab">提供服务</a>
                            </li>
                            <li>
                                <a href="#svcConsumer" data-toggle="tab">消费服务</a>
                            </li>
                        </ul>
                        <div class="checkbox" style="position: absolute; right: 15px; top: 5px;">
                            <label>
                                <input id="showAllProvidedSvc" type="checkbox"> 显示全部服务
                            </label>
                        </div>
                        <div class="tab-content">
                            <div id="svcProvider" class="tab-pane active unslider clear"></div>
                            <div id="svcConsumer" class="tab-pane unslider clear"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

<#--<div class="row" style="margin-bottom: 15px">
    <div class="col-md-4">
        <div class="mt-element-list">
            <div class="mt-list-head list-simple font-white bg-red">
                <div class="list-head-title-container">
                    <div class="list-date">消费方,Top5</div>
                    <span class="list-title">异常调用统计</span>
                </div>
            </div>
            <div class="mt-list-container list-simple">
                <ul id="ulForConsumers"></ul>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="mt-element-list">
            <div class="mt-list-head list-simple font-white yellow-warn">
                <div class="list-head-title-container">
                    <div class="list-date">服务名,Top5</div>
                    <span class="list-title">服务平均耗时</span>
                </div>
            </div>
            <div class="mt-list-container list-simple">
                <ul id="ulForServices"></ul>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="mt-element-list">
            <div class="mt-list-head list-simple font-white green-haze">
                <div class="list-head-title-container">
                    <div class="list-date">单次详情,Top10</div>
                    <span class="list-title">单次服务耗时</span>
                </div>
            </div>
            <div class="mt-list-container list-simple" id="topDetails" style="max-height: 250px">
                <ul id="ulForDetails"></ul>
            </div>
        </div>
    </div>
</div>-->

    <div class="row">
        <div class="col-md-12 col-sm-12">
            <div class="portlet light ">
                <div class="mychart row" id="mychart">
                    <div id="trendLineEChartDIV" class="pull-left trendLineEChartDIV">&nbsp;</div>
                    <div class="pull-left trendPierEChartDIV">
                        <div class="block-title">消费方调用统计</div>
                        <div id="trendPierEChartDIV" style="width: 100%;height: 100%;">&nbsp;</div>
                    </div>

                    <div style="" class="set-btn-group btn-group  btn-group-sm btn-group-solid m-l-10">
                        <button type="button" data-label="bar" class="btn btn-default btn-time blue">
                            当天
                            <input type="hidden" name="type" value="21"/>
                        </button>
                        <button type="button" data-label="line" class="btn btn-default btn-time">
                            本周
                            <input type="hidden" name="type" value="23"/>
                        </button>
                        <button type="button" data-label="line" class="btn btn-default btn-time">
                            本月
                            <input type="hidden" name="type" value="24"/>
                        </button>

                        <button type="button" data-label="line" class="btn btn-default" id="historyTotalCountModel">
                            系统调用量
                            <input type="hidden" name="type" value="25"/>
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

<!-- BEGIN 显示系统调用次数 -->
<@modal.editModal id="historyCountModel" modaltitle="" modalBig="modal-percent-60"  title="调用次数统计" buttonId="">
<div class="form-horizontal">
    <div class="row">
        <div class="col-md-6 form-inline">
            <span style="margin-right: 10px;margin-top: 7px">统计日期：</span>
            <div id="date_picker" class="input-group date date-picker" style="width: 45%">
                <input id="historyDate" type="text" class="form-control" style="border-radius:0;border:0" readonly>
                <span class="input-group-btn">
                    <button class="btn default" type="button">
                        <i class="fa fa-calendar"></i>
                    </button>
                </span>
            </div>
        </div>
        <div class="col-md-6" style="padding-right: 0">
            <div class="center-block" style="display: flex;margin-top: 7px">
                <span>该日调用总数：</span>
                <span id="targetDayTotal">0</span>
                <span style="margin-left: 20px">失败数：</span>
                <span id="targetDayFail">0</span>
            </div>
        </div>
        <div id="listTableWrapper" class="col-md-12 margin-top-5" style="height:380px">
            <table id="listTable" class="listTable"></table>
        </div>
    </div>
</div>
</@modal.editModal>
<!-- END 显示系统调用次数 -->

<!-- END PAGE -->

<@list.foot echart=true>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js"
        type="text/javascript"></script>
<script src="${contextPath}/project-assets/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js"
        type="text/javascript"></script>
<script src="${contextPath}/project-assets/plugins/unslider/js/unslider.min.js" type="text/javascript"></script>
<script src="${contextPath}/project-assets/js/monitor/overview/starChart.js" type="text/javascript"></script>
<script src="${contextPath}/project-assets/js/monitor/overview/overview.js" type="text/javascript"></script>
</@list.foot>
