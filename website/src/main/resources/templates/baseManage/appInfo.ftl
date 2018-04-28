<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head>
    <#assign contextPath=request.contextPath>
</@list.head>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-12">
            <div id="wrapperId2" class="row common-table margin-top-0">
                <div class="col-md-4">
                    <button type="button" id="btnAdd" class="btn btn-default btn-sm">
                        <i class="fa fa-plus"></i> 新增
                    </button>
                    <button id="btnDelete" type="button" class="btn btn-default btn-sm">
                        <i class="fa fa-trash-o"></i> 删除
                    </button>
                    <button id="btnEnable" type="button" class="btn btn-default btn-sm">
                        <i class="fa fa-check-circle"></i> 启用
                    </button>
                    <button id="btnDisable" type="button" class="btn btn-default btn-sm">
                        <i class="fa fa-times-circle"></i> 停用
                    </button>
                </div>
                <div class="col-md-8">
                    <div class="form-inline">
                        <div class="pull-right">
                            <select id="queryOrg" class="form-control input-sm"></select>
                            <select id="queryStatus" class="form-control input-sm"></select>
                            <div class="input-group">
                                <input type="text" class="form-control input-sm" id="queryWord" placeholder="请输入服务代码或名称"/>
                                <span class="input-group-btn">
                                <button id="btnQuery" type="button" class="btn btn-primary btn-sm">
                                    <i class="fa fa-search margin-right-5"></i> 查询
                                </button>
                            </span>
                            </div>
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
<@modal.editModal id="editFormContainer" title="系统基本信息维护"
buttonId="btnSave"
formId="editForm" modalbody="" modalBig="modal-percent-40" bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="id" type="hidden">
    <input name="appIdOld" type="hidden">
    <div class="form-group">
        <label for="orgId" class="col-sm-3 control-label">机构</label>
        <div class="col-sm-9">
            <select id="orgId" name="orgId" class="form-control"></select>
        </div>
    </div>
    <div class="form-group">
        <label for="appId" class="col-sm-3 control-label">代码(AppId)</label>
        <div class="col-sm-9">
            <input name="appId" maxlength="100" type="text" class="form-control">
        </div>
    </div>
    <div class="form-group">
        <label for="appName" class="col-sm-3 control-label">名称</label>
        <div class="col-sm-9">
            <input name="appName" maxlength="100" type="text" class="form-control">
        </div>
    </div>
    <div class="form-group">
        <label for="appType" class="col-sm-3 control-label">类型</label>
        <div class="col-sm-9">
            <select id="appType" name="appType" class="form-control"></select>
        </div>
    </div>
    <div class="form-group">
        <label for="direction" class="col-sm-3 control-label">方向</label>
        <div class="col-sm-9">
            <select id="direction" name="direction" class="form-control"></select>
        </div>
    </div>
    <#--<div class="form-group">-->
        <#--<label for="status" class="col-sm-3 control-label">状态</label>-->
        <#--<div class="col-sm-9">-->
            <#--<select id="appStatus" name="status" class="form-control"></select>-->
        <#--</div>-->
    <#--</div>-->
<#--<div class="form-group">-->
<#--<label for="appIdCurrent" class="col-sm-3 control-label">在用系统</label>-->
<#--<div class="col-sm-9">-->
<#--<select id="appIdCurrent" name="appIdCurrent" placeholder="如果因为修改代码停用，则可以关联在用的系统" class="form-control"></select>-->
<#--</div>-->
<#--</div>-->
    <div class="form-group">
        <label class="col-sm-3 control-label">排序</label>
        <div class="col-sm-9">
            <input name="order_num" maxlength="8" type="text" class="form-control">
        </div>
    </div>
    <div class="form-group">
        <label maxlength="300" class="col-sm-3 control-label">描述</label>
        <div class="col-sm-9">
            <input name="desp" type="text" class="form-control">
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 维护窗口  -->

<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/baseManage/appInfo.js" type="text/javascript"></script>
</@list.foot>