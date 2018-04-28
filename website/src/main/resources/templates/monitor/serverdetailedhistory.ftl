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
                <div class="col-md-8" style="padding: 0px">
                    <span>调用次数-提供方:${serverAppName}>${value}
                    <#if clientAppName =="">${clientAppName}<#else>>${clientAppName}</#if>>${transactionTypeName}
                    <#if serverIpAddress =="">>所有主机<#else>>${serverIpAddress}</#if>
                    <#if clientIpAddress =="">>所有客户端<#else>>${clientIpAddress}</#if>
                    </span>
                </div>
                <div class="col-md-4" style="padding: 0px 0px 0px 30px">
                    <button type="button" class="btn btn-default cp" id="statusvalue" data-toggle="dropdown" style="height:30px;border-radius:0px 0px 0px 0px;margin-left: 20px;width: inherit">
                    <#if status =="">
                        全部
                    <#else>
                    ${status}
                    </#if>
                        <i class="fa  fa-caret-down pull-right"></i>
                    </button>
                    <ul class="dropdown-menu"  id="statusselect" role="menu" aria-labelledby="dropdownMenu1">
                        <li role="presentation"><a role="menuitem" tabindex="-1">全部</a></li>
                        <li role="presentation"><a role="menuitem" tabindex="-1">执行成功</a></li>
                        <li role="presentation"><a role="menuitem" tabindex="-1">执行失败</a></li>
                    </ul>
                    <div class="input-group pull-right" style="width: 60%">
                        <input type="text" class="form-control" style="height: 30px;border-radius: 0px 0px 0px 0px"
                               id="inputKeyWords" placeholder="IP/名称">
                        <span style="cursor: pointer;" class="input-group-btn"><button type="button"
                                                                                       class="btn btn-primary"
                                                                                       style="height: 30px"
                                                                                       id="btnQuery">查询
                    </button></span>
                    </div>
                </div>
            </div>
            <div class="x_content">
                <table id="fTable" class="listTable"></table>
            </div>
        </div>
    </div>
</div>

<@modal.editModal id="xqEdit" title="详情" buttonId="" modalBig="modal-percent-60">
<div class="row" style="padding-top: 8px" >
    <div id="modalScrollDiv" class="col-md-12" style="height: 400px;">
        <div class="col-md-12 col-sm-12" style="height: 25%;padding: 0px">
            <span class="panel-title" style="font-size: 14px;color: #444">该服务调用流程：</span>
            <div id="serviceFlow" class="steps-round-auto steps-7"
                 style="height: 70%;padding-top: 5px;padding-left: 25px">

            </div>
        </div>
        <div class="col-md-12" id="detailContent" style="display: block;height: 100%;padding: 0">

        </div>
    </div>
</div>
</@modal.editModal>

<input type="hidden" id="transactionTypeName" value="${transactionTypeName}">
<input type="hidden" id="transactionTypeId" value="${transactionTypeId}">
<input type="hidden" id="serverAppName" value="${serverAppName}">
<input type="hidden" id="serverAppId" value="${serverAppId}">
<input type="hidden" id="type" value="${type}">
<input type="hidden" id="value" value="${value}">
<input type="hidden" id="clientAppName" value="${clientAppName}">
<input type="hidden" id="clientAppId" value="${clientAppId}">
<input type="hidden" id="clientIpAddress" value="${clientIpAddress}">
<input type="hidden" id="serverIpAddress" value="${serverIpAddress}">
<input type="hidden" id="status" value="${status}">
<input type="hidden" id="dateValue" value="${dateValue}">
<input type="hidden" id="historypagetype" value="${historyPageType}">

<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js-common/winning-table.js" type="text/javascript"></script>
<script src="${contextPath}/project-assets/js/monitor/serverdetailedhistory.js" type="text/javascript"></script>
</@list.foot>