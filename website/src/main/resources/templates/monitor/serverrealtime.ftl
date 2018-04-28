<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>

<@list.head tree=true>
    <#assign contextPath=request.contextPath>
<style>
</style>
</@list.head>
<!-- BEGIN PAGE -->

<div class="row" style="margin: 0px -20px 0px -20px;">
    <div class="col-md-3" style="padding-right: 0px;margin-top: 15px;height: 600px">
        <div class="row common-box font-size-14" id="treeDiv" style="margin-top: 0px;height: inherit">
            <div class="col-md-12 col-sm-12">
                <ul id="appTree" class="ztree"></ul>
            </div>
        </div>
    </div>
    <div class="col-md-9 margin-top-15">
        <div id="relationChartsDiv" class="col-md-12 col-sm-12 bgf" style="padding-right: 0px;padding-left: 0px">
            <div class="x_panel">
                <div class="x_title_without-header">
                    <span class="panel-title" style="font-size: 14px;color: #444">提供方监控运行情况</span>
                    <div class="btn-group pull-right">
                        <button type="button" class="btn btn-default pt2 blue" id="time1" style="height:25px">
                            当前一小时
                        </button>
                        <button type="button" class="btn btn-default pt2" id="time2" style="height:25px">当天</button>
                        <button type="button" data-toggle="dropdown" class="btn  btn-default pt2" id="time3"
                                style="height:25px;border-radius:0px 4px 4px 0px">指定小时 <i class="fa  fa-caret-down"></i>
                        </button>

                        <ul class="dropdown-menu" role="menu" id="time3v" aria-labelledby="dropdownMenu1"
                            style="max-height: 200px;margin-top:12px;overflow:scroll;">
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">00:00-00:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">01:00-01:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">02:00-02:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">03:00-03:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">04:00-04:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">05:00-05:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">06:00-06:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">07:00-07:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">08:00-08:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">09:00-09:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">10:00-10:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">11:00-11:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">12:00-12:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">13:00-13:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">14:00-14:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">15:00-15:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">16:00-16:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">17:00-17:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">18:00-18:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">19:00-19:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">20:00-20:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">21:00-21:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">22:00-22:59</a>
                            </li>
                            <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)">23:00-23:59</a>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="x_content">
                    <div id="relationChart" style="position: relative;height: 200px"></div>
                </div>
            </div>
        </div>
        <div class="col-md-12 col-sm-12 margin-top-15 bgf" id="tableDiv" style="padding-right: 0px;padding-left: 0px">
            <div class="x_panel">
                <div class="x_title_without-header">
                    <div class="col-md-9" style="padding-left: 0px">
                        <span>服务列表</span>
                    </div>
                    <div class="col-md-3" style="padding-right: 0px">
                        <div class="input-group">
                            <input type="text" class="form-control input-sm" id="keyword" placeholder="请输入服务名称">
                            <span style="cursor: pointer;" class="input-group-btn">
                                <button type="button" class="btn btn-primary" id="querybtn" style="height: 30px;border: 0px">查询</button>
                            </span>
                        </div>
                    </div>
                </div>
                <div class="x_content">
                    <table id="fTable" class="mytable">
                        <thead>
                        <tr>
                            <th>服务名称</th>
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

<@modal.editModal id="picEdit" modaltitle="" modalBig="modal-percent-60"  title="趋势图" buttonId="">
<div class="row p15" id="echartRow">
    <div class="col-md-12" style="height:400px;" id="echart">

    </div>
</div>
</@modal.editModal>
<input type="hidden" id="domain" value="${domain}">
<input type="hidden" id="type" value="${type}">
<input type="hidden" id="status" value="${status}">
<input type="hidden" id="isRemote" value="${isRemote}">
<input type="hidden" id="downDomain" value="${downDomain}">
<@list.foot tree=true echart=true>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/monitor/serverrealtime.js" type="text/javascript"></script>
</@list.foot>