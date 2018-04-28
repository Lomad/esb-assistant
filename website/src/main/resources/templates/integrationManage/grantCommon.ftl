<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<#import "projectCommon/textboxTree.ftl" as textboxTree/>

<#macro head>
    <@list.head tree=true>
        <#assign contextPath=request.contextPath>
    <style>

        #appTree.ztree,
        #svcTree.ztree,
        #applySvcTree.ztree,
        #applySvcReqTree.ztree,
        #applySvcAckTree.ztree {
            margin-top: 0px;
            border: 1px solid #c2cad8;
            height: 200px;
            overflow-y: scroll;
            overflow-x: auto;
        }

        #svcStructureTree.ztree {
            margin-top: 0px;
            border: 1px solid #c2cad8;
            height: 254px;
            overflow-y: scroll;
            overflow-x: auto;
        }

        #appTree,
        #svcTree {
            height: 240px !important;
        }

        .popTreeSkin {
            display: none;
            position: absolute;
            z-index: 999999;
            left: 15px;
            top: 34px;
            background-color: #fff;
        }

        .treeFilterSkin {
            left: 0px;
            top: 30px;
        }
    </style>
    </@list.head>
</#macro>

<#macro body>
<input id="loginUserid" type="hidden" value="${Session["userid"]}"/>
<input id="pageType" type="hidden" value="${pageType}"/>

<div class="project-body-container">
    <div id="wrapperId2" class="row common-table margin-top-0">
        <div class="col-md-4">
            <div>
                <button id="btnAdd" type="button" class="btn btn-default btn-sm hidden">
                    <i class="fa fa-key" style="margin-right: 4px"></i> 添加授权
                </button>
                <button id="btnApply" type="button" class="btn btn-default btn-sm hidden">
                    <i class="fa fa-key" style="margin-right: 4px"></i> 申请授权
                </button>
                <button id="btnApplyCancel" type="button" class="btn btn-default btn-sm hidden">
                    <i class="fa fa-close" style="margin-right: 4px"></i> 取消申请
                </button>
            </div>
        </div>
        <div class="col-md-8">
            <div class="form-inline pull-right">
                <div class="input-group">
                    <select id="approve_state" class="form-control input-sm"></select>
                </div>
                <div class="input-group">
                    <input id="queryWord" type="text" class="form-control input-sm" placeholder="请输入服务代码或名称">
                    <span class="input-group-btn">
                        <button id="btnQuery" type="button" class="btn btn-primary btn-sm">
                            <i class="fa fa-search" style="margin-right: 4px"></i> 查询
                        </button>
                    </span>
                </div>
            </div>
        </div>
        <div class="col-md-12 col-sm-12 margin-top-15">
            <table id="listTable" class="listTable"></table>
        </div>
    </div>
</div>

<#--START 授权维护窗口 -->
    <@modal.editModal id="editFormContainer" title="授权维护"
    buttonId="btnSave" buttonClick="global_Object.saveClick()"
    formId="editForm" modalbody="" modalBig="modal-percent-60" bodyPaddingLeft=30 bodyPaddingRight=30>
    <div class="form-horizontal">
        <input name="id" type="hidden"/>
        <input name="aid" type="hidden"/>
        <input name="sid" type="hidden"/>
        <div class="row">
            <div class="col-sm-6">
                <div id="appInfoSkin" class="form-group">
                    <label class="col-sm-12">申请方</label>
                    <div class="col-sm-12">
                        <@textboxTree.textboxTree idTree="appTree" placeHolder="选择申请方" />
                    </div>
                </div>
                <div id="svcInfoSkin" class="form-group">
                    <label class="col-sm-12">服务</label>
                    <div class="col-sm-12">
                        <@textboxTree.textboxTree idTree="svcTree" placeHolder="选择待授权的服务" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-12">授权码</label>
                    <div class="col-sm-12">
                        <div class="input-group">
                            <input id="lic_key" name="lic_key" class="form-control" placeholder="请输入">
                            <span class="input-group-btn">
                                <button id="btnLic" class="btn btn-default" onclick="global_Object.creatLicKey()" type="button">
                                    <i class="fa fa-plus cp btnImg prject-color-default" title="随机生成授权码"></i>
                                </button>
                            </span>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-12">加密密钥</label>
                    <div class="col-sm-12">
                        <div class="input-group">
                            <input id="secret_key" type="text" name="secret_key" class="form-control">
                            <span class="input-group-btn">
                                <button id="btnSecret" class="btn btn-default" onclick="global_Object.creatSecretKey()" type="button">
                                    <i class="fa fa-plus cp btnImg prject-color-default" title="随机生成密钥"></i>
                                </button>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div id="svcStructureTreeSkin" class="form-group">
                    <label class="col-sm-12">应答结构</label>
                    <div class="col-sm-12">
                        <ul id="svcStructureTree" class="ztree"></ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </@modal.editModal>
<#--END 授权维护窗口  -->

<#--START 申请授权窗口 -->
<@modal.editModal id="applyFormContainer" title="申请授权"
buttonId="btnSaveApply" buttonClick="global_Object.saveApplyClick()"
formId="applyForm" modalbody="" modalBig="modal-percent-90" bodyPaddingLeft=30 bodyPaddingRight=30>
    <div class="form-horizontal">
        <input name="id" type="hidden"/>
        <input name="sid" type="hidden"/>
        <div class="row">
            <div class="col-md-4">
                <#--<div class="form-group">-->
                    <#--<label class="col-sm-3 control-label">申请方</label>-->
                    <#--<div class="col-sm-9">-->
                        <#--<@textboxTree.textboxTree idTree="applyApp" placeHolder="选择申请方" />-->
                    <#--</div>-->
                <#--</div>-->
                <label>
                    服务
                    <i id="btnQuerySvc" title="查询服务" class="fa fa-search shape-link"></i>
                    <i id="btnRereshSvc" title="加载全部服务" class="fa fa-refresh shape-link"></i>
                </label>
                <ul id="applySvcTree" class="ztree" style="height: 400px"></ul>
            </div>
            <div class="col-md-4">
                <label>请求消息结构</label>
                <ul id="applySvcReqTree" class="ztree" style="height: 400px; background-color: #e2e2e2"></ul>
            </div>
            <div class="col-md-4">
                <label>应答消息结构</label>
                <ul id="applySvcAckTree" class="ztree" style="height: 400px; background-color: #e2e2e2"></ul>
            </div>
        </div>
    </div>
</@modal.editModal>
<#--END 申请授权窗口  -->

</#macro>

<#macro foot>
    <@list.foot tree=true>
        <#assign contextPath=request.contextPath>
    <script src="${contextPath}/project-assets/js-common/textbox-tree.js" type="text/javascript"></script>
    <script src="${contextPath}/project-assets/js/integrationManage/grant.js" type="text/javascript"></script>
    </@list.foot>
</#macro>
