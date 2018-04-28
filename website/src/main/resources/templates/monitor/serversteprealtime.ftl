<#--<#import "projectMaster/list.ftl" as list/>-->
<#--<#import "common/modal.ftl" as modal/>-->
<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>

<@list.head>
    <#assign contextPath=request.contextPath>

</@list.head>
<!-- BEGIN PAGE -->
<div class="row" style="margin: 0px -20px 0px -20px;">
    <#--<div class="col bgf" style="line-height: 30px;margin: 15px">
        <span style="margin-left: 15px">
            ${serverAppName} /耗时占比（提供方: ${serverAppName}>
            <#if type = "指定小时">
            ${time}
            <#else>
            ${type}
            </#if>>${clientAppName}>${transactionTypeName}>
            <#if serverIpAddress = "">
                所有主机
            <#else>
            ${serverIpAddress}
            </#if>
            <#if clientIpAddress =="">
                >所有客户端
            <#else>
                >${clientIpAddress}
            </#if>）
         </span>
    </div>-->
    <div class="col bgf" style="min-height: 264px;margin: 15px 15px 0px 15px">
        <div class="x_panel">
            <div class="x_header">
                <i class="fa fa-reply shape-link pull-right" style="margin: 4px 4px 0px 4px" onclick="window.history.back();"></i>
            </div>
            <div class="x_title">
                <div class="col-md-9" style="padding: 0px">
                    <span>耗时占比-提供方: ${serverAppName}><#if type = "指定小时">${time}<#else>${type}</#if>>${clientAppName}>${transactionTypeName}>
                    <#if serverIpAddress = "">所有主机<#else>${serverIpAddress}</#if>
                    <#if clientIpAddress =="">>所有客户端<#else>>${clientIpAddress}</#if>
                    </span>
                </div>
            </div>
            <div class="x_content">
                <div class="col-md-12" style="min-height: 250px;padding: 0px" id="echart"></div>
            </div>
        </div>
    </div>
    <div class="col bgf margin-top-15" style="min-height: 290px;margin: 0px 15px">
        <div class="x_panel">
            <div class="x_title_without-header">
                <div class="col-md-12" style="padding: 0px;">
                    <span>服务步骤明细</span>
                </div>
            </div>
            <div class="x_content">
                <table id="fTable" class="mytable">
                    <thead>
                    <tr>
                        <th class="firstChild numeric">序号</th>
                        <th class="numeric">步骤名称</th>
                        <th class="numeric" >调用次数</th>
                        <th class="numeric" >平均耗时</th>
                        <#--<th class="numeric">99%</th>
                        <th class="numeric">95%</th>-->
                        <th class="numeric">最短耗时</th>
                        <th class="numeric">最长耗时</th>
                        <th class="numeric">吞吐量</th>
                        <th class="numeric">失败次数</th>
                        <th class="numeric">失败率</th>
                        <#--<th class="numeric">方差</th>-->
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<#--<div class="row bgf bb1" style="margin-top:-15px;">
    <div class="col-md-12  lh40">
        <span>
        ${serverAppName} /耗时占比（提供方: ${serverAppName}>
        <#if type = "指定小时">
        ${time}
        <#else>
        ${type}
        </#if>>${clientAppName}>${transactionTypeName}>
        <#if serverIpAddress = "">
            所有主机
        <#else>
        ${serverIpAddress}
        </#if>
        <#if clientIpAddress =="">
            >所有客户端
        <#else>
            >${clientIpAddress}
        </#if>）
         </span>
    </div>

</div>
<div class="row mt15 ml0 mr0 bgf b1" style="height: 50%">
    <div class="col-md-12 lh34">
        <span>服务步骤耗时占比</span>
    </div>
    <div class=" pl0 pr0" id="echart" style="width:1000px;height:200px">

    </div>
</div>
<div class="row mt15 ml0 mr0 bgf b1 mb0" style="height: 236px">
    <div class="col-md-12 lh34">
        <span>服务步骤明细</span>
    </div>
    <div class=" pl0 pr0">
        <table id="fTable" class="table table-head  table-condensed flip-content">
            <thead class="flip-content">
            <tr>
                <th>序号</th>
                <th>步骤名称</th>
                <th class="numeric" >调用次数</th>
                <th class="numeric" >平均耗时</th>
                <th class="numeric">99%</th>
                <th class="numeric">95%</th>
                <th class="numeric">最短耗时</th>
                <th class="numeric">最大耗时</th>
                <th class="numeric">吞吐量</th>
                <th class="numeric">失败次数</th>
                <th class="numeric">失败率</th>
                <th class="numeric">方差</th>
            </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>
</div>-->

<input type="hidden" id="transactionTypeName" value="${transactionTypeName}">
<input type="hidden" id="transactionTypeId" value="${transactionTypeId}">
<input type="hidden" id="serverAppName" value="${serverAppName}">
<input type="hidden" id="serverAppId" value="${serverAppId}">
<input type="hidden" id="type" value="${type}">
<input type="hidden" id="time" value="${time}">
<input type="hidden" id="serverIpAddresshidden" value="${serverIpAddress}">
<input type="hidden" id="clientAppName" value="${clientAppName}">
<input type="hidden" id="clientAppId" value="${clientAppId}">

<@list.foot echart=true>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/monitor/serversteprealtime.js" type="text/javascript"></script>
</@list.foot>