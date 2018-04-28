<#import "projectCommon/modal.ftl" as modal />

<#--样式资源-->
<#macro head>
<style>
    .queryDate {
        width: 105px !important;
        padding-right: 20px !important;
        background-color: #fff !important;
    }
    .queryTime {
        width: 90px !important;
        padding-right: 20px !important;
        background-color: #fff !important;
    }
    .queryTimeIcon {
        color: #aaa;
    }
</style>

</#macro>

<#--列表-->
<#macro list
id="">
    <div id="${id}" class="row common-table margin-top-0">
        <div class="col-md-12">
            <span id="detailedTitle"></span>
            <div class="input-group pull-right">
                <i class="fa fa-reply shape-link" style="margin-right: 4px" onclick="window.history.back();"></i>
            </div>
        </div>
        <div class="col-md-12 form-inline margin-top-15">
            <select id="queryStatus" class="form-control input-sm">
                <option value="">全部状态</option>
                <option value="0">执行成功</option>
                <option value="-1">执行失败</option>
            </select>
            <select id="queryDuring" class="form-control input-sm hidden">
                <option value="">全部耗时</option>
                <option value="50">前50耗时</option>
            </select>
            <div class="form-inline pull-right">


                <div id="queryDateWrapper" class="input-group hidden">
                    日期
                    <div class="form-group has-feedback">
                        <input id="queryDate" type="text" class="form-control input-sm queryDate"
                               placeholder="选择日期" readonly>
                        <span class="glyphicon glyphicon-calendar form-control-feedback queryTimeIcon"></span>
                    </div>
                </div>
                <div id="timeRange" class="input-group hidden">
                    时间
                    <div class="form-group has-feedback">
                        <input id="queryTimeRangeStart" type="text" class="form-control input-sm queryTime"
                               placeholder="开始时间" readonly>
                        <span class="glyphicon glyphicon-calendar form-control-feedback queryTimeIcon"></span>
                    </div>
                    ->
                    <div class="form-group has-feedback">
                        <input id="queryTimeRangeEnd" type="text" class="form-control input-sm queryTime"
                               placeholder="结束时间" readonly>
                        <span class="glyphicon glyphicon-calendar form-control-feedback queryTimeIcon"></span>
                    </div>
                </div>

                <div class="input-group">
                     <span class="input-group-btn">
                        <select id="queryColumn" class="form-control input-sm" style="margin-right: -1px;">
                            <option value="mainId">关键ID/姓名</option>
                            <option value="MessageID">消息唯一ID</option>
                            <option value="requestEsb">请求消息</option>
                            <option value="responseEsb">应答消息</option>
                        </select>
                    </span>
                    <input id="queryWord" type="text" class="form-control input-sm" placeholder="消息内容关键字">
                    <span class="input-group-btn">
                        <button id="btnQuery" type="button" class="btn btn-primary btn-sm">
                            <i class="fa fa-search margin-right-5"></i> 查询
                        </button>
                        <button id="btnReset" type="button" class="btn btn-default btn-sm">
                            <i class="fa fa-refresh margin-right-5"></i> 重置
                        </button>
                    </span>
                </div>
            </div>
        </div>
        <div class="col-md-12 col-sm-12 margin-top-bottom-15">
            <table id="listTable" class="listTable"></table>
        </div>
    </div>

    <@modal.editModal id="xqEdit" title="详情" buttonId="" modalBig="modal-percent-70">
        <div class="row" style="padding-top: 8px">
            <div id="modalScrollDiv" class="col-md-12" style="height: 400px;">
                <div class="col-md-12 col-sm-12" style="height: 25%;padding: 0px">
                    <span class="panel-title" style="font-size: 14px;color: #444">该服务调用流程：</span>
                    <div id="serviceFlow" class="steps-round-auto steps-7"
                         style="height: 70%;padding-top: 5px;padding-left: 25px">

                    </div>
                </div>
                <div id="detailContent" class="col-md-12" style="display: block;height: 100%;padding: 0">

                </div>
            </div>
        </div>
    </@modal.editModal>

</#macro>

<#--脚本资源-->
<#macro foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/monitor/common/detailedTimes.js" type="text/javascript"></script>
</#macro>