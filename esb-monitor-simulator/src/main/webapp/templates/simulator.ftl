<#import "common/common.ftl" as common/>

<@common.head>
    <#assign contextPath=request.contextPath>
<style></style>
</@common.head>

<div class="row" style="margin: 15px">
    <div class="col-md-3" style="padding: 0px;display: flex">
        <span style="padding-top: 7px">样本时间：</span>
        <div id="samplePicker" class="input-group date form_datetime" style="width: 45%">
            <input id="sampleDate" title="" class="form-control" size="16" type="text" value="" readonly="">
            <span class="input-group-addon" style="background-color: #eef1f5"><span
                    class="fa fa-calendar"></span></span>
        </div>
    </div>
</div>
<div class="row" style="margin: 15px">
    <div class="col-md-3" style="padding: 0px;display: flex">
        <span style="padding-top: 7px">生成比例：</span>
        <span class="input-group-btn" style="width: 45%">
            <select id="percent" class="form-control input-sm" style="font-size: 14px;">
                <option value="0.1">0.1</option>
                <option value="0.3">0.3</option>
                <option value="0.5">0.5</option>
            </select>
        </span>
    </div>
</div>
<div class="row" style="margin: 15px">
    <div class="col-md-3" style="padding: 0px;display: flex">
        <span style="padding-top: 7px">随机范围：</span>
        <input id="gap" type="text" class="form-control" value="100" style="height: 30px;width: 45%">
    </div>
</div>
<div class="row" style="margin: 15px">
    <div class="col-md-3" style="padding: 0px;display: flex">
        <button id="startLaunch" type="button" class="btn btn-default">开始生成</button>
    </div>
</div>

<input type="hidden" id="contextPath" value="${contextPath}">

<@common.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/static/js/simulator.js" type="text/javascript"></script>
</@common.foot>