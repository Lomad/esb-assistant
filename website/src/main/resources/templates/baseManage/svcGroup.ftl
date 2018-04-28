<#import "projectCommon/list.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head>
    <#assign contextPath=request.contextPath>
</@list.head>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-12">
            <div id="wrapperId2" class="row common-table margin-top-0">
                <div class="col-md-3">
                    <div>
                        <button type="button" id="btnAdd" class="btn btn-default btn-sm">
                            <i class="fa fa-plus"></i> 新增
                        </button>
                        <button id="btnDelete" type="button" class="btn btn-default btn-sm">
                            <i class="fa fa-trash-o"></i> 删除
                        </button>
                    </div>
                </div>
                <div class="col-md-9">
                    <div class="form-inline">
                        <div class="input-group pull-right">
                            <input type="text" class="form-control input-sm" id="queryWord" placeholder="请输入服务代码或名称"/>
                            <span class="input-group-btn">
                            <button id="btnQuery" type="button" class="btn btn-primary btn-sm">
                                <i class="fa fa-search" style="margin-right: 4px"></i> 查询
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
buttonId="btnSave" buttonClick="global_Object.saveClick()"
formId="editForm" modalbody="" modalBig="modal-percent-60" bodyPaddingRight=30>
<div class="form-horizontal">
    <div class="form-group">
        <label for="name" class="col-sm-2 control-label">名称</label>
        <div class="col-sm-7">
            <input name="id" type="hidden">
            <input name="name" maxlength="100" type="text" class="form-control">
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 维护窗口  -->

<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/baseManage/svcGroup.js" type="text/javascript"></script>

</@list.foot>