<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>

<@list.head tree=true select2="true">
    <#assign contextPath=request.contextPath>
<style>
    #targetAidTree.ztree {
        margin-top: 0px;
        border: 1px solid #c2cad8;
        height: 340px;
        overflow-y: scroll;
        overflow-x: auto;
    }
</style>
</@list.head>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-3">
            <div id="treeWrapper" class="row common-box font-size-14 margin-top-0 padding-bottom-15">
                <div class="col-md-12 col-sm-12">
                    <ul id="appTree" class="ztree padding-0"></ul>
                </div>
            </div>
        </div>
        <div class="col-md-9 padding-left-0">
            <div id="wrapperId2" class="row common-table margin-top-0">
                <div class="col-md-5">
                    <div>
                        <button type="button" id="btnAdd" class="btn btn-default btn-sm">
                            <i class="fa fa-plus"></i> 新增
                        </button>
                        <button id="btnDelete" type="button" class="btn btn-default btn-sm">
                            <i class="fa fa-trash-o"></i> 删除
                        </button>
                        <button id="btnPublish" type="button" class="btn btn-default btn-sm">
                            <i class="fa fa-check-circle"></i> 发布
                        </button>
                        <button id="btnRollback" type="button" class="btn btn-default btn-sm">
                            <i class="fa fa-times-circle"></i> 下线
                        </button>
                        <button id="btnCopy" type="button" class="btn btn-default btn-sm">
                            <i class="fa fa-copy"></i> 复制
                        </button>
                    </div>
                </div>
                <div class="col-md-7">
                    <div class="form-inline">
                        <div class="input-group pull-right">
                            <input type="text" class="form-control input-sm" id="queryWord" placeholder="请输入服务代码或名称"/>
                            <span class="input-group-btn">
                                <button id="btnQuery" type="button" class="btn btn-primary btn-sm">
                                    <i class="fa fa-search margin-right-5"></i> 查询
                                </button>
                            </span>
                        </div>
                        <div class="pull-right">
                            <select id="queryStatus" name="status" class="form-control input-sm margin-right-5"></select>
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
<@modal.editModal id="editFormContainer" title="服务基本信息维护"
buttonId="btnSave" buttonClick="global_Object.saveClick()"
formId="editForm" modalbody="" modalBig="modal-percent-60" bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="id" type="hidden">
    <input name="code" type="hidden">
    <div class="form-group">
        <label class="col-sm-2 control-label">提供方</label>
        <div class="col-sm-4">
            <select id="aid" name="aid" class="form-control"></select>
        </div>
        <label for="msgType" class="col-sm-2 control-label">参数格式</label>
        <div class="col-sm-4">
            <select id="msgType" name="msgType" class="form-control"></select>
        </div>
    </div>
    <div class="form-group">
        <label for="code" class="col-sm-2 control-label">代码</label>
        <div class="col-sm-4">
            <div class="input-group">
                <div id="appId" class="input-group-addon"
                     style="padding: 7px 1px 5px 5px; background-color: #efefef"></div>
                <input id="svcCodeNotContainSysCode" maxlength="80" type="text" class="form-control"
                    style="border-left-width: 0; padding-left: 0px;">
            </div>
        </div>
        <label for="name" class="col-sm-2 control-label">名称</label>
        <div class="col-sm-4">
            <input name="name" maxlength="100" type="text" class="form-control">
        </div>
    </div>
    <div class="form-group">
        <label for="version" class="col-sm-2 control-label">版本号</label>
        <div class="col-sm-4">
            <input name="version" maxlength="100" type="text" class="form-control">
        </div>
        <label class="col-sm-2 control-label">数据协议</label>
        <div class="col-sm-4">
            <select id="dataProtocal" name="dataProtocal" class="form-control"></select>
        </div>
    </div>
    <div class="form-group">
    <#--<label for="groupId" class="col-sm-2 control-label">分组</label>-->
    <#--<div class="col-sm-4">-->
    <#--<select id="groupId" name="groupId" class="form-control"></select>-->
    <#--</div>-->
        <label class="col-sm-2 control-label">
            源地址
            <i id="btnToggleUrlInput" class="fa fa-external-link shape-link" title="切换地址输入方式" data-url-input="url"></i>
        </label>
        <div class="col-sm-4">
            <input name="url" maxlength="1024" type="text" class="form-control">
            <select id="urlId" name="urlId" class="form-control hidden"></select>
        </div>
        <label class="col-sm-2 control-label">ESB代理地址</label>
        <div class="col-sm-4">
            <select id="urlAgentId" name="urlAgentId" class="form-control"></select>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">其他标志</label>
        <div class="col-sm-4">
            <select id="otherMark" name="otherMark" class="form-control"></select>
        </div>
    </div>
    <div class="form-group">
        <label for="desp" class="col-sm-2 control-label">描述</label>
        <div class="col-sm-10">
            <textarea name="desp" maxlength="300" class="form-control" rows="6"></textarea>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 维护窗口  -->

<#--START 复制服务窗口 -->
<@modal.editModal id="importSvcFormContainer" title="复制服务"
buttonId="btnCopySave" buttonClick="global_Object.saveCopyClick()"
formId="importSvcForm" modalbody="" modalBig="modal-percent-30" bodyPaddingRight=30>
<div class="form-horizontal">
    <div class="row">
        <div class="col-md-12">
            <label>请选择目标系统</label>
            <ul id="targetAidTree" class="ztree"></ul>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 复制消息结构窗口  -->

<@list.foot tree=true select2="true">
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/serviceManage/svcInfo.js" type="text/javascript"></script>

</@list.foot>