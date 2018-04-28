<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head>
    <#assign contextPath=request.contextPath>
</@list.head>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-12">
            <div id="wrapperId2" class="row common-table margin-top-0">
                <div class="col-md-12">
                    <div class="form-inline">
                        <div class="input-group pull-right">
                            <input type="text" class="form-control input-sm" id="queryWord" placeholder="请输入名称"/>
                            <span class="input-group-btn">
                            <button id="btnQuery" type="button" class="btn btn-primary btn-sm">
                                <i class="fa fa-search margin-right-5"></i> 查询
                            </button>
                        </span>
                        </div>
                    </div>
                </div>
                <div class="col-md-12 col-sm-12 margin-top-bottom-15">
                    <table id="listTable" class="listTable"></table>
                </div>
            </div>
        </div>
    </div>
</div>

<#--START 维护窗口 -->
<@modal.editModal id="editFormContainer" title="基本信息维护"
buttonId="btnSave"
formId="editForm" modalbody="" modalBig="modal-percent-40" bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="code" type="hidden">
    <div class="form-group">
        <label for="name" class="col-sm-2 control-label">名称</label>
        <div class="col-sm-10">
            <label name="name" class="form-control" disabled></label>
        </div>
    </div>
    <div class="form-group">
        <label for="value" class="col-sm-2 control-label">取值</label>
        <div class="col-sm-10">
            <input id="configValueInput" maxlength="8000" type="text" class="form-control">
            <select id="configValueSelect" class="form-control hidden"></select>
        </div>
    </div>
    <div class="form-group">
        <label for="desp" class="col-sm-2 control-label">描述</label>
        <div class="col-sm-10">
            <textarea name="desp" rows="5" class="form-control" disabled></textarea>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 维护窗口  -->

<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/baseManage/configs.js" type="text/javascript"></script>

</@list.foot>