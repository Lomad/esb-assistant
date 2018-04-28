<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>

<@list.head tree=true>
    <#assign contextPath=request.contextPath>
<link rel="stylesheet" type="text/css" href="${contextPath}/project-assets/plugins/bootstrap-datepicker/css/datepicker.css"/>
<style>
    .datepicker .cw {
        font-size: 14px;
        color: #332cc6;
        font-weight: bold !important;
    }
</style>
</@list.head>
<!-- BEGIN PAGE -->
<div class="row" style="margin: 0px -20px 0px -20px;">
    <div class="col-md-3" style="padding-right: 0px;margin-top: 15px;height: 600px">
        <div id="treeDiv" class="row common-box font-size-14" style="height: inherit;margin-top: 0px">
            <div class="col-md-12 col-sm-12">
            <#--<div id="appTree" class="margin-top-bottom-10"></div>-->
                <ul id="appTree" class="ztree"></ul>
            </div>
        </div>
    </div>
    <div class="col-md-9 margin-top-15">
        <div id="relationChartsDiv" class="col-md-12 col-sm-12 bgf" style="padding-right: 0px;padding-left: 0px">
            <div class="x_panel">
                <div class="x_title_without-header">
                    <span class="panel-title" style="font-size: 14px;color: #444">提供方监控运行情况</span>
                    <div class="input-group pull-right col-md-3" style="padding-right: 0px;">
                        <div class="input-group-btn dropdown">
                            <button id="selbtn" type="button" class="btn dropdown-toggle" data-toggle="dropdown">日查询 <i
                                    class="fa fa-angle-down"></i></button>
                            <ul id="sel" class="dropdown-menu">
                                <li><a data="day" href="#">日查询</a></li>
                                <li><a data="week" href="#">周查询</a></li>
                                <li><a data="month" href="#">月查询</a></li>
                            </ul>
                        </div>
                        <div id="date_picker" class="input-group input-medium date date-picker">
                            <input id="datevalue" type="text" class="form-control" style="border-radius:0;border:0"
                                   readonly>
                            <span class="input-group-btn">
                <button class="btn default" type="button"><i class="fa fa-calendar"></i></button>
                </span>
                        </div>
                    </div>
                </div>
                <div class="x_content">
                    <div id="relationChart" style="position: relative;height: 200px"></div>
                </div>
            </div>
        </div>
        <div class="col-md-12 col-sm-12 margin-top-15 bgf" style="padding-right: 0px;padding-left: 0px" id="tableDiv">
            <div class="x_panel">
                <div class="x_title_without-header">
                    <div class="col-md-9" style="padding-left: 0px">
                        <span>服务列表</span>
                    </div>
                    <div class="col-md-3" style="padding-right: 0px">
                        <div class="input-group">
                            <input type="text" class="form-control input-sm" id="keyword" placeholder="请输入服务名称">
                            <span style="cursor: pointer;" class="input-group-btn">
                                <button type="button" class="btn btn-primary" id="querybtn"
                                        style="height: 30px;border: 0px">查询</button>
                            </span>
                        </div>
                    </div>
                </div>
                <div class="x_content">
                    <table id="fTable" class="mytable">
                        <thead>
                        <tr>
                            <th class="">服务名称</th>
                            <th class="numeric" data-id="totalCount">调用次数</th>
                            <th class="numeric" data-id="avg">平均耗时</th>
                        <#--<th class="numeric">99%<span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"
                                                     data-placement="right"
                                                     title="置信度99%：对应值为总体真值，表示99%的样本置信区间覆盖此真值"></span></th>
                        <th class="numeric">95%<span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"
                                                     data-placement="right"
                                                     title="置信度95%：对应值为总体真值，表示95%的样本置信区间覆盖此真值"></span></th>-->
                            <th class="numeric">最短耗时</th>
                            <th class="numeric">最大耗时</th>
                            <th class="numeric">吞吐量</th>
                            <th class="numeric" data-id="failCount">失败次数</th>
                            <th class="numeric">失败率</th>
                        <#--<th class="numeric">方差</th>-->
                            <th class="numeric lastChild">显示图表</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

</div>

<@modal.editModal id="picEdit" modaltitle=""  modalBig="modal-percent-60"  title="趋势图"   buttonId="">
<div class="row p15" id="echartRow">
    <div class="col-md-12" style="height:400px;" id="echart">

    </div>
</div>
</@modal.editModal>

<@list.foot tree=true echart=true>
    <#assign contextPath=request.contextPath>
<#--概览界面下钻传入数据-->
<input type="hidden" id="domain" value="${domain}">
<input type="hidden" id="type" value="${type}">
<input type="hidden" id="status" value="${status}">
<input type="hidden" id="isRemote" value="${isRemote}">
<input type="hidden" id="serverAppName" value="${serverAppName}">
<input type="hidden" id="dateTimeType" value="${dateTimeType}">
<input type="hidden" id="dateTimeValue" value="${dateTimeValue}">

<script src="${contextPath}/project-assets/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="${contextPath}/project-assets/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js" type="text/javascript"></script>
<script src="${contextPath}/project-assets/js/monitor/serverhistory.js" type="text/javascript"></script>
</@list.foot>