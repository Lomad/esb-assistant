<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head tree=true>
    <#assign contextPath=request.contextPath>
<style>

    #appTree.ztree {
        margin-top: 0px;
        border: 1px solid #c2cad8;
        height: 200px;
        overflow-y: scroll;
        overflow-x: auto;
    }

</style>
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
<@modal.editModal id="editFormContainer" title="用户维护"
buttonId="btnSave" buttonClick="global_Object.saveClick()"
formId="editForm" modalbody="" modalBig="modal-percent-40">
<div class="form-horizontal">
    <input name="id" type="hidden">
    <input name="aidList" type="hidden">
    <div class="form-group">
        <label for="username" class="col-sm-3 control-label">用户名</label>
        <div class="col-sm-7">
            <input name="username" maxlength="100" type="text" placeholder="请输入" class="form-control">
        </div>
    </div>
    <div class="form-group">
        <label for="approve_state" class="col-sm-3 control-label">角色</label>
        <div class="col-sm-7">
            <select id="role" name="role" class="form-control"></select>
        </div>
    </div>
    <div id="appTreeSkin" class="form-group" style="display: none;">
        <label class="col-sm-3 control-label">业务系统</label>
        <div class="col-sm-7">
            <ul id="appTree" class="ztree"></ul>
            <div class="msg-tips">用户可操作的业务系统</div>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 维护窗口  -->

<#--START 修改密码窗口 -->
<@modal.editModal id="editPwdFormContainer" title="修改密码"
buttonId="btnChangePwd" buttonName="修改密码" buttonClick="global_Object.changePwd()"
buttonId2="btnResetPwd" buttonName2="重置密码" buttonClick2="global_Object.resetPwd()"
formId="editPwdForm" modalbody="" modalBig="modal-percent-40">
<div class="form-horizontal">
    <input name="id" type="hidden">
    <input name="passwordOld"  type="password" style="display: none">
    <div class="form-group">
        <label class="col-sm-3 control-label">旧密码</label>
        <div class="col-sm-7">
            <input name="confirmPasswordOld" maxlength="100" type="password" placeholder="请输入" class="form-control">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-3 control-label">新密码</label>
        <div class="col-sm-7">
            <input name="password" maxlength="100" type="password" placeholder="请输入" class="form-control">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-3 control-label">确认新密码</label>
        <div class="col-sm-7">
            <input name="confirmPassword" maxlength="100" placeholder="请输入" type="password" class="form-control">
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 修改密码窗口  -->

<@list.foot tree=true>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/baseManage/user.js" type="text/javascript"></script>

</@list.foot>