<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head layui=true>
    <#assign contextPath=request.contextPath>
    <style>
        .project-title {
            font-size: 16px;
            font-weight: bold;
            height: 59px;
            line-height: 59px;
        }

        .layui-timeline-title {
            padding-top: 2px;
            font-size: 14px !important;
        }
        .layui-btn-radius {
            border-radius: 100px !important;
        }

        .timeline-normal {
            color: #444444 !important;
        }
        .timeline-success {
            color: #009933 !important;
        }
        .timeline-error {
            color: #CC0000 !important;
        }

        .progress {
            margin-bottom: 0;
        }
        .progress-skin {

        }
        .progress-item {
            margin-bottom: 15px;
        }
        .progress-title {
            font-size: 14px;
            font-weight: bold;
            line-height: 26px;
        }
        .progress-desp {
            font-size: 12px;
            color: #444444 !important;
            line-height: 26px;
        }
        .progress-img-normal {
            color: #666666 !important;
            line-height: 20px !important;
            font-size: 20px;
        }
        .progress-img-success {
            color: #009933 !important;
            line-height: 20px !important;
            font-size: 20px;
        }
        .progress-img-error {
            color: #CC0000 !important;
            line-height: 20px !important;
            font-size: 20px;
        }
        .progress-info-normal {
            color: #666666 !important;
            line-height: 18px !important;
            font-size: 18px;
        }
    </style>
</@list.head>

<div class="project-body-container">
    <div class="row ">
    <div class="col-md-8">
        <div id="divLeftWrapper" class="row common-box padding-top-0 margin-top-0">
            <div class="col-md-12 col-sm-12">
                <span class="project-title"><i class="fa fa-clock-o" style="margin-right: 4px; color: #6c8dae;"></i>每日巡检</span>
                <div class="pull-right" style="margin-top: 10px">
                    <button id="btnBeginInspection" class="layui-btn layui-btn-primary layui-btn-radius"
                            style="color: #ffffff; background-color: #286090">
                        <#--<i id="pagerNext" class="fa fa-hourglass-start"></i>-->
                        <span>开始巡检</span>
                    </button>
                </div>
            </div>
            <div class="col-md-12 col-sm-12">
                <div id="progressSkin" class="progress-skin"></div>
            </div>
        </div>
    </div>
    <div class="col-md-4 padding-left-0">
        <div id="divRightWrapper" class="row common-box padding-top-0 margin-top-0">
            <div class="col-md-12 col-sm-12">
                <span class="project-title"><i class="fa fa-history" style="margin-right: 4px; color: #6c8dae;"></i>巡检历史</span>
                <nav class="pull-right">
                    <ul class="pager">
                        <li><i onclick="global_Object.initHistory(1)" class="fa fa-chevron-circle-left fa-lg prject-color-default shape-link"></i></li>
                        <li><i onclick="global_Object.initHistory(2)" class="fa fa-chevron-circle-right fa-lg prject-color-default shape-link"></i></li>
                    </ul>
                </nav>
            </div>
            <div class="col-md-12 col-sm-12">
                <ul id="insList" class="layui-timeline"></ul>
            </div>
        </div>
    </div>
    </div>
</div>

<#--START 显示巡检结果窗口 -->
<@modal.editModal id="editFormContainer" title="巡检结果"
buttonId="btnSave" buttonClick="global_Object.saveClick(0)"
buttonId2="btnAbandon" buttonName2="舍弃结果" buttonClick2="global_Object.abandonClick()"
formId="editForm" modalbody="" modalBig="modal-percent-50"
buttonCloseVisible="false" backdrop="static" keyboard="false">
<div class="form-horizontal">
    <input name="id" type="hidden">
    <div class="form-group">
        <div class="col-md-2">
            <p class="form-control-static pull-right"><label>巡检耗时</label></p>
        </div>
        <div class="col-md-10">
            <p class="form-control-static"><label name="time_len_SHOW"></label>秒</p>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-2 control-label">巡检明细</label>
        <div class="col-md-10">
            <textarea name="details" class="form-control" rows="8" readonly></textarea>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-2 control-label">巡检结果</label>
        <div class="col-md-5">
            <select id="result" name="result" class="form-control edittypeClass" disabled></select>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-2 control-label">处理描述</label>
        <div class="col-md-10">
            <textarea name="result_desp" class="form-control" rows="5"></textarea>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 显示巡检结果窗口  -->

<#--START 处理巡检结果窗口 -->
<@modal.editModal id="handleFormContainer" title="处理巡检错误"
buttonId="btnSaveHandle" buttonClick="global_Object.saveClick(1)"
formId="handleForm" modalbody="" modalBig="modal-percent-50">
<div class="form-horizontal">
    <input name="id" type="hidden">
    <div class="form-group">
        <div class="col-md-2">
            <p class="form-control-static pull-right"><label>巡检耗时</label></p>
        </div>
        <div class="col-md-10">
            <p class="form-control-static"><label name="time_len_SHOW"></label>秒</p>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-2 control-label">巡检明细</label>
        <div class="col-md-10">
            <textarea name="details" class="form-control" rows="8" readonly></textarea>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-2 control-label">巡检结果</label>
        <div class="col-md-5">
            <select id="resultHandle" name="result" class="form-control edittypeClass" disabled></select>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-2 control-label">处理描述</label>
        <div class="col-md-10">
            <textarea name="result_desp" class="form-control" rows="5"></textarea>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 处理巡检结果窗口  -->

<@list.foot layui=true echart=true>
    <#assign contextPath=request.contextPath>
    <script src="${contextPath}/project-assets/js/inspection/main.js" type="text/javascript"></script>
</@list.foot>