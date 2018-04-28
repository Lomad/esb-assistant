<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head>
    <#assign contextPath=request.contextPath>
</@list.head>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-12">
            <div id="wrapperId2" class="row common-table margin-top-0">
                <div class="col-md-3">
                    <select id="typeList" name="typeList" class="input-sm edittypeClass"></select>
                </div>
                <div class="col-md-9">
                    <div class="form-inline">
                        <div class="input-group pull-right">
                            <input type="text" class="form-control input-sm" id="queryWord" placeholder="请输入服务代码或名称"/>
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
<#--<@modal.editModal id="editFormContainer" title="系统基本信息维护"-->
<#--buttonId="btnSave" buttonClick="global_Object.saveClick()"-->
<#--formId="editForm" modalbody="" modalBig="modal-percent-60" bodyPaddingRight=30>-->
<#--<div class="form-horizontal">-->
    <#--<input name="code" type="hidden">-->
    <#--<div class="form-group">-->
        <#--<label for="name" class="col-sm-2 control-label">名称</label>-->
        <#--<div class="col-sm-9">-->
            <#--<label name="name" class="form-control" disabled></label>-->
        <#--</div>-->
    <#--</div>-->
    <#--<div class="form-group">-->
        <#--<label for="value" class="col-sm-2 control-label">取值</label>-->
        <#--<div class="col-sm-9">-->
            <#--<input name="value" maxlength="8000" type="text" class="form-control">-->
        <#--</div>-->
    <#--</div>-->
    <#--<div class="form-group">-->
        <#--<label for="desp" class="col-sm-2 control-label">描述</label>-->
        <#--<div class="col-sm-9">-->
            <#--<textarea name="desp" rows="5" class="form-control" disabled></textarea>-->
        <#--</div>-->
    <#--</div>-->
<#--</div>-->
<#--</@modal.editModal>-->
<#--END 维护窗口  -->


<#--START 数据库连接维护 -->
<@modal.editModal id="editFormDbContainer" title="数据库连接参数配置"
buttonId="btnSaveDB" buttonClick="global_Object.saveDB"
buttonId2="btnTestDB" buttonName2="测试连接" buttonClick2="global_Object.testConnect()"
formId="editDbForm" modalbody="" modalBig="modal-wide">
<div class="form-horizontal">
    <input name="code" type="hidden">
    <div class="form-group">
        <label class="col-md-3 control-label">数据库类型</label>
        <div class="col-md-7">
            <select id="dbType" name="dbType" class="form-control edittypeClass"></select>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">服务器</label>
        <div class="col-md-7">
            <input name="ip" type="text" class="form-control" placeholder="请输入IP地址或名称">
            <span style="color: #aaaaaa;"><em>对于Sqlserver数据库，如果通过实例名连接，则端口号可以为空，格式：127.0.0.1\MSSQL2012</em></span>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">端口号</label>
        <div class="col-md-7">
            <input name="port" type="text" class="form-control" placeholder="请输入端口号">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">数据库名称/SID</label>
        <div class="col-md-7">
            <input name="dbName" type="text" class="form-control" placeholder="请输入数据库名称">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">用户名</label>
        <div class="col-md-7">
            <input name="username" type="text" class="form-control" placeholder="请输入用户名">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">密码</label>
        <div class="col-md-7">
            <input name="password" type="password" class="form-control" placeholder="请输入密码">
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 数据库连接维护  -->

<#--START web维护 -->
<@modal.editModal id="editFormWebContainer" title="Web连接参数配置"
buttonId="btnSaveWeb" buttonClick="global_Object.saveWeb"
formId="editWebForm" modalbody="" modalBig="modal-wide">
<div class="form-horizontal">
    <input name="code" type="hidden">
    <div class="form-group">
        <label class="col-md-3 control-label">类型</label>
        <div class="col-md-7">
            <select id="type" name="type" class="form-control edittypeClass"></select>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">服务器</label>
        <div class="col-md-7">
            <input name="ip" type="text" class="form-control" placeholder="请输入url或IP地址">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">端口号</label>
        <div class="col-md-7">
            <input name="port" type="text" class="form-control" placeholder="请输入端口号">
            <span style="color: #aaaaaa;"><em>对于web或restful，则端口号可以为空</em></span>
        </div>
    </div>
    <#--<div class="form-group">-->
        <#--<label class="col-md-3 control-label">用户名</label>-->
        <#--<div class="col-md-7">-->
            <#--<input name="username" type="text" class="form-control" placeholder="请输入用户名">-->
        <#--</div>-->
    <#--</div>-->
    <#--<div class="form-group">-->
        <#--<label class="col-md-3 control-label">密码</label>-->
        <#--<div class="col-md-7">-->
            <#--<input name="password" type="password" class="form-control" placeholder="请输入密码">-->
        <#--</div>-->
    <#--</div>-->
</div>
</@modal.editModal>
<#--END web维护  -->

<#--START ESB地址维护 -->
<@modal.editModal id="editFormESBContainer" title="ESB地址连接参数配置"
buttonId="btnSaveWeb1" buttonClick="global_Object.saveWeb"
formId="editESBForm" modalbody="" modalBig="modal-wide">
<div class="form-horizontal">
    <input name="code" type="hidden">
    <div class="form-group">
        <label class="col-md-3 control-label">类型</label>
        <div class="col-md-7">
            <#--<select id="type2" name="type" class="form-control edittypeClass"></select>-->
            <input name = "type" type="text" class="form-control" value="Socket" readonly>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">服务器</label>
        <div class="col-md-7">
            <input name="ip" type="text" class="form-control" placeholder="请输入url或IP地址">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">端口号</label>
        <div class="col-md-7">
            <input name="port" type="text" class="form-control" placeholder="请输入端口号">
            <span style="color: #aaaaaa;"><em>对于web或restful，则端口号可以为空</em></span>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">用户名</label>
        <div class="col-md-7">
            <input name="username" type="text" class="form-control" placeholder="请输入用户名">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">密码</label>
        <div class="col-md-7">
            <input name="password" type="password" class="form-control" placeholder="请输入密码">
        </div>
    </div>
</div>
</@modal.editModal>
<#--END web维护  -->

<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/baseManage/dataSource.js" type="text/javascript"></script>

</@list.foot>