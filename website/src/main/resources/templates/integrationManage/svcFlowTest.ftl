<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<#import "projectCommon/textboxTree.ftl" as textboxTree/>

<@list.head tree=true>
    <#assign contextPath=request.contextPath>
<style>

    #flowSvcTree.ztree {
        margin-top: 0px;
        border: 1px solid #c2cad8;
        width: 100%;
        height: 300px;
        overflow-y: scroll;
        overflow-x: auto;
    }

    .modeOverview {
        border-bottom: 1px solid #ccc;
        margin-bottom: 15px
    }

    .modeWrapper {
        padding: 0px 15px;
        text-align: center;
        margin-bottom: 30px;
    }

    .modeWrapper .title {
        font-weight: bold;
        font-size: 16px;
        margin-bottom: 15px;
    }

    .modeWrapper .sys {
        height: 70px;
        margin-top: 5px;
        border: solid 5px #ccc;
        padding: 10px 15px;
        -webkit-border-radius: 15px !important;
        -moz-border-radius: 15px !important;
        border-radius: 15px !important;
        text-align: center;
        overflow: hidden;
    }

</style>
</@list.head>

<input id="loginUserid" type="hidden" value="${Session["userid"]}"/>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-3">
            <div id="wrapperId1" class="row common-box font-size-14 margin-top-0 padding-bottom-15 padding-top-10">
                <div class="col-md-12 col-sm-12">
                    集成测试场景
                    <span class="margin-left-5">
                        <i id="btnCreateFlow" class="fa fa-plus shape-link" title="新建测试场景"></i>
                    </span>
                    <span class="margin-left-5">
                        <i class="fa fa-question-circle" title="您可以拖动场景中的步骤，进行排序"></i>
                    </span>
                </div>
                <div class="col-md-12 col-sm-12 margin-top-5">
                    <ul id="flowTree" class="ztree padding-0"></ul>
                </div>
            </div>
        </div>
        <div class="col-md-9" style="padding-left: 0px;">
            <div id="wrapperId2" class="row common-box margin-top-0">
                <div class="col-md-12 modeOverview">
                    <div class="modeWrapper">
                        <div id="flowImgTitle" class="title">集成测试 - 数据流转图</div>
                        <div id="flowImgInfo" class="msg-tips margin-top-10">服务信息</div>
                        <div class="row margin-top-30">
                            <div id="testStepImgConsumer" class="col-md-2 col-md-offset-1 sys">消费方<br>系统</div>
                            <div class="col-md-2">
                                <div class="row" style="text-align: center">
                                    <div class="col-md-12 prject-color-normal2">请求<span id="msgTypeReq"></span></div>
                                    <i class="fa fa-long-arrow-right fa-3x prject-color-normal2 col-md-12 margin-top-5"></i>
                                    <i class="fa fa-long-arrow-left fa-3x prject-color-normal3 col-md-12 margin-top-10 margin-bottom-5"></i>
                                    <div class="col-md-12 prject-color-normal3">转发应答<span
                                            id="msgTypeAckTransfer"></span></div>
                                </div>
                            </div>
                            <div class="col-md-2 sys"><b>集成<br>平台</b></div>
                            <div class="col-md-2">
                                <div class="row" style="text-align: center">
                                    <div class="col-md-12 prject-color-normal2">转发请求<span
                                            id="msgTypeReqTransfer"></span></div>
                                    <i class="fa fa-long-arrow-right fa-3x prject-color-normal2 col-md-12 margin-top-5"></i>
                                    <i class="fa fa-long-arrow-left fa-3x prject-color-normal3 col-md-12 margin-top-10 margin-bottom-5"></i>
                                    <div class="col-md-12 prject-color-normal3">应答<span id="msgTypeAck"></span></div>
                                </div>
                            </div>
                            <div id="testStepImgProvider" class="col-md-2 sys">提供方<br>系统</div>
                        </div>
                    </div>
                </div>
                <div class="col-md-12">
                    <div class="row">
                        <form id="logForm">
                            <a id="downLog" download="" href="" target="blank"></a>

                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>
                                        请求内容
                                        <i id="btnDownloadMsgReq" class="fa fa-download shape-link margin-left-5"
                                           title="下载请求内容"></i>
                                    </label>
                                    <label class="pull-right">
                                <span id="msgReqTips" class="badge">
                                    正在等待消费方发送
                                    <i class="fa fa-spinner fa-pulse"></i>
                                </span>
                                    </label>

                                    <textarea name="out_msg" class="form-control" readonly></textarea>
                                </div>
                            </div>
                            <div class="col-md-6 padding-left-0">
                                <div class="form-group">
                                    <label>
                                        应答内容
                                        <i id="btnDownloadMsgAck" class="fa fa-download shape-link margin-left-5"
                                           title="下载应答内容"></i>
                                    </label>
                                    <label class="pull-right">
                                <span id="msgAckTips" class="badge">
                                    正在等待提供方应答
                                    <i class="fa fa-spinner fa-pulse"></i>
                                </span>
                                    </label>

                                    <a id="downAck" download="" href="" target="blank"></a>
                                    <textarea name="ack_msg" class="form-control" readonly></textarea>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <div id="wrapperId3" class="row common-box margin-top-0 prject-backcolor-default hidden">
                <div class="col-md-12">
                    <div style="text-align: center;margin-top: 200px">
                        <label id="testStatusDesp">尚未开启新的测试。</label>
                        <br>
                        <button id="btnStartTest" type="button" class="btn btn-primary">立即开启</button>
                        <button id="btnShowLogList" type="button" class="btn btn-primary hidden">查看日志</button>
                    </div>
                </div>
            </div>
            <div id="wrapperId4" class="row common-box margin-top-0 hidden">
                <div class="col-md-12">
                    <button id="btnTestLogDesp" class="btn btn-primary btn-sm">
                        <i class="fa fa-commenting"></i> 结果说明
                    </button>
                    <button id="btnDownLogPdf" class="btn btn-default btn-sm">
                        <i class="fa fa-download"></i> 下载日志
                    </button>
                    <button id="btnSignName" class="btn btn-default btn-sm hidden">
                        <i class="fa fa-pencil"></i> 签名
                    </button>
                <#--<div class="form-inline pull-right">-->
                <#--<div class="input-group">-->
                <#--<select id="queryStatus" name="status" class="form-control input-sm"></select>-->
                <#--<span class="input-group-btn">-->
                <#--<button id="btnQuery" type="button" class="btn btn-primary btn-sm">-->
                <#--<i class="fa fa-search margin-right-5"></i> 查询-->
                <#--</button>-->
                <#--</span>-->
                <#--</div>-->
                <#--</div>-->
                    <div class="input-group pull-right">
                        <i id="btnBackMainBoard" class="fa fa-reply shape-link"></i>
                    </div>
                </div>
                <div class="col-md-12 col-sm-12 margin-top-bottom-15">
                    <table id="listTable" class="listTable"></table>
                </div>
            </div>
        </div>
    </div>
</div>

<#--START 流程概要信息窗口 -->
<@modal.editModal id="flowFormContainer" title="场景概要信息"
buttonId="btnSaveFlow" buttonClick="global_Object.saveFlowClick()"
formId="flowForm" modalbody="" modalBig="modal-percent-40" bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="id" type="hidden">
    <div class="form-group">
        <label class="col-md-3 control-label">场景名称</label>
        <div class="col-md-9">
            <input name="name" type="text" class="form-control" placeholder="请输入场景名称">
        </div>
    </div>
    <div class="form-group">
        <label class="col-md-3 control-label">场景描述</label>
        <div class="col-md-9">
            <textarea name="desp" class="form-control" rows="5"></textarea>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 流程概要信息窗口  -->

<#--START 添加流程步骤窗口 -->
<@modal.editModal id="flowSvcFormContainer" title="添加服务步骤"
buttonId="btnSaveFlowSvc" buttonClick="global_Object.saveFlowSvcClick()"
formId="flowSvcForm" modalbody="" modalBig="modal-percent-40" bodyPaddingLeft=30 bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="fid" type="hidden">
    <div class="row">
        <div class="col-md-12">
            <div id="appInfoSkin" class="form-group">
                <label class="col-sm-12">消费方</label>
                <div class="col-sm-12">
                    <@textboxTree.textboxTree idTree="appTree" placeHolder="请选择消费方" />
                </div>
            </div>
        </div>
        <div class="col-md-12">
            <div>
                目标服务
                <i id="btnQuerySvc" title="查询服务" class="fa fa-search shape-link"></i>
                <i id="btnRereshSvc" title="清除查询条件，重新加载服务" class="fa fa-refresh shape-link"></i>
                <span class="pull-right msg-tips">提示：下框只显示“已发布”服务！</span>
            </div>
            <ul id="flowSvcTree" class="ztree"></ul>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 添加流程步骤窗口  -->

<#--START 查看测试日志 -->
<@modal.editModal id="viewLogFormContainer" title="查看测试日志"
buttonId="" buttonClick=""
formId="viewLogForm" modalbody="" modalBig="modal-percent-60" bodyPaddingLeft=30 bodyPaddingRight=30>
<div class="form-horizontal">
    <div class="row">
        <div class="col-md-4">
            开始时间
            <input name="btime" type="text" class="form-control" readonly>
        </div>
        <div class="col-md-4">
            结束时间
            <input name="etime" type="text" class="form-control" readonly>
        </div>
        <div class="col-md-4">
            耗时(ms)
            <input name="time_len" type="text" class="form-control" readonly>
        </div>
    </div>
    <div class="row margin-top-15">
        <div class="col-md-6">
            请求内容
            <textarea name="out_msg" class="form-control" style="height: 340px" readonly></textarea>
        </div>
        <div class="col-md-6">
            应答内容
            <textarea name="ack_msg" class="form-control" style="height: 340px" readonly></textarea>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 查看测试日志  -->

<#--START 最后一步测试结束 -->
<@modal.editModal id="testLogFormContainer" title="测试结果说明"
buttonId="btnFinishTestLog"
formId="testLogForm" modalbody="" modalBig="modal-percent-40" bodyPaddingLeft=30 bodyPaddingRight=30>
<div class="form-horizontal">
    <input name="id" type="hidden">
    <div class="row">
        <div class="col-md-12">
            测试已结束，请输入测试结果描述：
            <textarea name="desp" class="form-control" style="height: 200px"></textarea>
        </div>
    </div>
</div>
</@modal.editModal>
<#--END 最后一步测试结束  -->

<@list.foot tree=true>
    <#assign contextPath=request.contextPath>
    <script src="${contextPath}/project-assets/js-common/textbox-tree.js" type="text/javascript"></script>
    <script src="${contextPath}/project-assets/js/integrationManage/svcFlowTest.js" type="text/javascript"></script>
</@list.foot>