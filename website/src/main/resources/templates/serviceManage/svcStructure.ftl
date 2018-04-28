<#import "projectCommon/tree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head>
    <#assign contextPath=request.contextPath>
<style>
    .param-title {
        font-weight: bold;
    }
    #svcInTree.ztree,
    #svcAckTree.ztree {
        min-height: 590px;
        padding: 0px;
    }

    #importSvcTree.ztree {
        margin-top: 0px;
        border: 1px solid #c2cad8;
        height: 300px;
        overflow-y: scroll;
        overflow-x: auto;
    }

</style>
</@list.head>

<div class="project-body-container">
    <div id="wrapperId2" class="row common-table margin-top-0">
        <div class="col-md-9">
            <form id="svcForm" class="form-inline">
                <input name="protocalReq" type="hidden"/>
                <input name="protocalAck" type="hidden"/>
                <div class="form-group">
                    <label for="svcCode">服务代码</label>
                    <input type="text" name="code" class="form-control input-sm" readonly/>
                </div>
                <div class="form-group">
                    <label for="svcName">服务名称</label>
                    <input type="text" name="name" class="form-control input-sm" readonly/>
                </div>
                <div class="form-group">
                    <label for="svcName">消息格式</label>
                    <input type="text" name="msgType" class="form-control input-sm" readonly/>
                </div>
            </form>
        </div>
        <div class="col-md-3">
            <div class="input-group pull-right">
                <i class="fa fa-reply shape-link" style="margin-right: 4px" onclick="window.history.back();"></i>
            </div>
        </div>
        <div class="col-md-6 col-sm-6 margin-top-15"
             style="border-top: solid 1px #e2e2e2; border-right: solid 1px #e2e2e2">
            <div class="row font-title-default">
            <#--主要用于文件下载-->
                <a id="downFiles" download="" href="" target="blank" style="display:none"></a>
            <#--主要用于文件上传-->
                <input id="upfile" type="file" style="display:none"/>

                <div class="col-md-12">
                    <span class="param-title">请求参数结构</span>
                    <span style="font-size: 12px">
                        【节点图标说明：
                        <i class="fa fa-meh-o"></i>普通
                        <i class="fa fa-shield"></i>必须
                        <i class="fa fa-recycle"></i>循环
                        <i class="fa fa-at"></i>属性
                        】
                    </span>
                    <div class="pull-right">
                        <#--<i class="glyphicon glyphicon-question-sign shape-link" style="color: #999999"-->
                           <#--title="加粗表示必须字段，斜体表示可循环"></i>-->
                        <i id="btnAddRootIn" class="glyphicon glyphicon-plus-sign shape-link" title="添加根节点"></i>
                        <i id="btnImportIn" class="glyphicon glyphicon-import shape-link" title="复制结构"></i>
                        <i id="btnUploadIn" class="glyphicon glyphicon-arrow-up shape-link" title="上传结构"></i>
                        <i id="btnDownloadIn" class="glyphicon glyphicon-arrow-down shape-link" title="下载结构(不包含数据协议1.1)"></i>
                        <i id="btnDownloadIn_1_1" class="glyphicon glyphicon-download shape-link" title="下载结构(包含数据协议1.1)"></i>
                    </div>
                </div>
            </div>
            <div id="svcInTree" class="ztree"></div>
        </div>
        <div class="col-md-6 col-sm-6 margin-top-15" style="border-top: solid 1px #e2e2e2;">
            <div class="row font-title-default">
                <div class="col-md-12">
                    <span class="param-title">应答参数结构</span>
                    <div class="pull-right">
                        <#--<i class="glyphicon glyphicon-question-sign shape-link" style="color: #999999"-->
                           <#--title="加粗表示必须字段，斜体表示可循环"></i>-->
                        <i id="btnAddRootAck" class="glyphicon glyphicon-plus-sign shape-link" title="添加根节点"></i>
                        <i id="btnImportAck" class="glyphicon glyphicon-import shape-link" title="复制结构"></i>
                        <i id="btnUploadAck" class="glyphicon glyphicon-arrow-up shape-link" title="上传结构"></i>
                        <i id="btnDownloadAck" class="glyphicon glyphicon-arrow-down shape-link" title="下载结构(不包含数据协议1.1)"></i>
                        <i id="btnDownloadAck_1_1" class="glyphicon glyphicon-download shape-link" title="下载结构(包含数据协议1.1)"></i>
                    </div>
                </div>
            </div>
            <div id="svcAckTree" class="ztree"></div>
        </div>
    </div>
</div>

<#--START 维护窗口 -->
<@modal.editModal id="editFormContainer" title="结构维护"
buttonId="btnSave" buttonClick="global_Object.saveClick()"
formId="editForm" modalbody="" modalBig="modal-percent-40" bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="id" type="hidden">
    <input name="sid" type="hidden">
    <input name="direction" type="hidden">
    <input name="pid" type="hidden">
    <input name="order_num" type="hidden">

    <div class="form-group">
        <label class="col-md-3 control-label">代码</label>
        <div class="col-md-9">
            <input name="code" maxlength="100" type="text" class="form-control" placeholder="请输入代码">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">名称</label>
        <div class="col-md-9">
            <input name="name" maxlength="100" type="text" class="form-control" placeholder="请输入名称">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">数据类型</label>
        <div class="col-md-9">
            <select id="data_type" name="data_type" class="form-control edittypeClass"></select>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">是否必须</label>
        <div class="col-md-3">
            <select id="required" name="required" class="form-control edittypeClass"></select>
        </div>
        <label class="col-md-3 control-label">是否循环</label>
        <div class="col-md-3">
            <select id="is_loop" name="is_loop" class="form-control edittypeClass"></select>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">结果标志</label>
        <div class="col-md-3">
            <select id="result_mark" name="result_mark" class="form-control edittypeClass"></select>
        </div>
        <label class="col-md-3 control-label">是否属性</label>
        <div class="col-md-3">
            <select id="is_attr" name="is_attr" class="form-control edittypeClass"></select>
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">默认值</label>
        <div class="col-md-9">
            <input name="value_default" maxlength="4000" type="text" class="form-control">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">描述</label>
        <div class="col-md-9">
            <input name="desp" maxlength="4000" type="text" class="form-control">
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 维护窗口  -->

<#--START 候选值窗口 -->
<@modal.editModal id="valueListFormContainer" title="候选值维护"
buttonId="btnSaveValueList" buttonClick="global_Object.saveValueListClick()"
formId="valueListForm" modalbody="" modalBig="modal-percent-40" bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="ssid" type="hidden">
    <input name="resultMark" type="hidden">
    <div class="form-group">
        <label id="lblSuccessTitle" class="col-md-3 control-label">候选值</label>
        <div class="col-md-9">
            <textarea name="strValueList" class="form-control" rows="5"></textarea>
            <span class="col-sm-7 msg-tips">不同候选值请换行</span>
        </div>
    </div>
    <div id="valueListFailureSkin" class="form-group" style="display: none;">
        <label class="col-md-3 control-label">失败候选值</label>
        <div class="col-md-9">
            <textarea name="strValueListFailure" class="form-control" rows="5"></textarea>
            <span class="col-sm-7 msg-tips">不同候选值请换行</span>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 候选值窗口  -->

<#--START 上传确认窗口 -->
<@modal.editModal id="uploadFormContainer" title="上传确认"
buttonId="btnUpload" buttonName="确认上传" buttonClick="global_Object.upload()" buttonStyle="default"
buttonId2="btnOpenFile" buttonName2="打开文件" buttonClick2="$('#upfile').click();" buttonStyle2="blue"
formId="uploadForm" modalbody="" modalBig="modal-percent-50">
<div class="form-horizontal">
    <input name="contentRaw" type="hidden"/>
    <div class="form-group" style="margin-bottom: 0px">
        <div class="col-md-12">
            <label>
                1、如果符合数据协议1.0或1.1，则导入时，将只保留Body中的节点。<br>
                2、文件内容必须<span style="color: #f00;">“UTF-8”</span>编码格式，支持XML、JSON、HL7(v2.5.1)消息格式。
            </label>
            <textarea name="content" class="form-control" rows="20"></textarea>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 上传确认窗口  -->

<#--START 复制消息结构窗口 -->
<@modal.editModal id="importFormContainer" title="复制消息结构"
buttonId="btnImport" buttonClick="global_Object.importClick()"
formId="importForm" modalbody="" modalBig="modal-percent-40" bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="id" type="hidden">
    <input name="sid" type="hidden">
    <div class="row margin-top-15">
        <div class="col-md-12">
            <div class="form-group">
                <label class="col-sm-3 control-label">
                    服务
                    <i id="btnQuerySvc" title="查询服务" class="fa fa-search shape-link"></i>
                    <i id="btnRereshSvc" title="加载全部服务" class="fa fa-refresh shape-link"></i>
                </label>
                <div class="col-sm-9">
                    <ul id="importSvcTree" class="ztree"></ul>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label">消息方向</label>
                <div class="col-sm-9">
                    <select id="msgDirection" class="form-control"></select>
                </div>
            </div>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 复制消息结构窗口  -->

<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/baseManage/appZTree.js" type="text/javascript"></script>
<script src="${contextPath}/project-assets/js/serviceManage/svcStructure.js" type="text/javascript"></script>

</@list.foot>