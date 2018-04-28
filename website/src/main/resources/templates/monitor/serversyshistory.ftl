<#--<#import "projectMaster/list.ftl" as list/>-->
<#--<#import "common/modal.ftl" as modal/>-->
<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>

<@list.head>
    <#assign contextPath=request.contextPath>

</@list.head>
<!-- BEGIN PAGE -->
<div class="row" style="margin: 0px -20px 0px -20px;">
    <div id="wrapperId2" class="col bgf" style="min-height: 568px;margin: 15px 15px 0px 15px">
        <div class="x_panel">
            <div class="x_header">
                <i class="fa fa-reply shape-link pull-right" style="margin: 4px 4px 0px 4px" onclick="window.history.back();"></i>
            </div>
            <div class="x_title">
                <div class="col-md-10" style="padding: 0px;">
                    <span>${serverAppName} > ${transactionTypeName} > ${value}</span>
                    <div class="btn-group pull-right">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" id="serverIpAddress" style="border: 0px;padding: 0px;margin: 6px 15px">
                        <#if serverIpAddress = "">所有主机<#else>${serverIpAddress}</#if>
                            <span class="fa  fa-caret-down"></span>
                        </button>
                        <ul class="dropdown-menu pull-right"  id="serverIpAddress2" role="menu" aria-labelledby="dropdownMenu1">
                        </ul>
                    </div>
                </div>
                <div class="col-md-2" style="padding: 0px">
                    <div class="input-group input-group-sm">
                        <input type="text" class="form-control" id="keyword" placeholder="系统名称">
                        <span style="cursor: pointer;" class="input-group-btn"><button type="button"
                                                                                       class="btn btn-primary"
                                                                                       id="querybtn">查询
                    </button></span>
                    </div>
                </div>
            </div>
            <div class="x_content">
                <table id="fTable" class="mytable">
                    <thead>
                    <tr>
                        <th class="firstChild numeric">消费方</th>
                        <th class="numeric" data-id="totalCount">调用次数</th>
                        <th class="numeric" data-id="avg">平均耗时</th>
                        <#--<th class="numeric">99%</th>
                        <th class="numeric">95%</th>-->
                        <th class="numeric">最短耗时</th>
                        <th class="numeric">最大耗时</th>
                        <th class="numeric">吞吐量</th>
                        <th class="numeric" data-id="failCount">失败次数</th>
                        <th class="numeric lastChild">失败率</th>
                        <#--<th class="lastChild">方差</th>-->
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<input type="hidden" id="transactionTypeName" value="${transactionTypeName}">
<input type="hidden" id="transactionTypeId" value="${transactionTypeId}">
<input type="hidden" id="serverAppName" value="${serverAppName}">
<input type="hidden" id="serverAppId" value="${serverAppId}">
<input type="hidden" id="clientAppName" value="${clientAppName}">
<input type="hidden" id="clientAppId" value="${clientAppId}">
<input type="hidden" id="type" value="${type}">
<input type="hidden" id="value" value="${value}">
<input type="hidden" id="dateValue" value="${dateValue}">
<input type="hidden" id="serverIpAddr" value="${serverIpAddr}">
<!-- END PAGE -->
<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/monitor/serversyshistory.js" type="text/javascript"></script>
</@list.foot>