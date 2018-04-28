<#import "projectCommon/listAndTree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head tree=true>
    <#assign contextPath=request.contextPath>
<style>
    .modeWrapper {
        margin-top: 5px;
        padding: 20px 15px;
        text-align: center;
    }

    .modeWrapper .title {
        font-weight: bold;
        font-size: 16px;
        margin-bottom: 15px;
    }

    .modeWrapper .btnSelect {
        margin-top: 20px;
    }

    .modeWrapper .sys {
        margin-top: 5px;
        border: solid 5px #ccc;
        padding: 10px 15px;
        -webkit-border-radius: 15px !important;
        -moz-border-radius: 15px !important;
        border-radius: 15px !important;
        text-align: center;
        overflow: hidden;
    }

    .modeOverview {
        border-bottom: 1px solid #ccc;
        margin-bottom: 15px
    }

</style>
</@list.head>

<input id="loginUserid" type="hidden" value="${Session["userid"]}"/>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-3">
            <div id="treeWrapper" class="row common-box font-size-14 margin-top-0 padding-bottom-15">
                <div class="col-md-12 col-sm-12">
                    <div class="input-group">
                        <label><input type="radio" name="option" id="allTest" checked>全部</label>
                        <label><input type="radio" name="option" id="tested">已测试</label>
                        <label><input type="radio" name="option" id="untested">未测试</label>
                    </div>
                    <ul id="appTree" class="ztree padding-0"></ul>
                </div>
            </div>
        </div>
        <div class="col-md-9 padding-left-0">
            <div id="wrapperId3" class="row common-box margin-top-0 padding-top-0">
                <div class="col-md-6 padding-right-0 border-right border-bottom margin-bottom-15">
                    <div class="modeWrapper">
                        <div class="title">集成平台作为接收端</div>
                        <div class="msg-tips">该模式下，集成平台模拟服务提供方的角色。<br>
                            待测系统作为信息发送端，主动将信息发送到集成平台。
                        </div>
                        <div class="row margin-top-30">
                            <div class="col-md-3 col-md-offset-2 sys">集成<br>平台</div>
                            <div class="col-md-2">
                                <i class="fa fa-long-arrow-left fa-3x margin-top-30"></i>
                            </div>
                            <div class="col-md-3 sys">待测<br>系统</div>
                        </div>
                        <div class="row margin-top-30">
                            <div id="esbReceiverUrlInfo" class="col-md-10 col-md-offset-2 msg-tips"
                                style="overflow: hidden; text-align: left">
                                通信方式及服务地址信息
                            </div>
                        </div>
                        <div class="btnSelect margin-top-30">
                            <button id="btnEsbReceiver" type="button" class="btn btn-primary">进入测试</button>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 padding-left-0 border-bottom margin-bottom-15">
                    <div class="modeWrapper">
                        <div class="title">集成平台作为发送端</div>
                        <div class="msg-tips">该模式下，集成平台模拟服务消费方的角色。<br>
                            待测系统作为信息接收端，集成平台主动将信息发送到待测系统。
                        </div>
                        <div class="row margin-top-30">
                            <div class="col-md-3 col-md-offset-2 sys">集成<br>平台</div>
                            <div class="col-md-2">
                                <i class="fa fa-long-arrow-right fa-3x margin-top-30"></i>
                            </div>
                            <div class="col-md-3 sys">待测<br>系统</div>
                        </div>
                        <div class="row margin-top-30">
                            <div id="esbSenderUrlInfo" class="col-md-10 col-md-offset-2 msg-tips"
                                 style="overflow: hidden; text-align: left">
                                通信方式及服务地址信息
                            </div>
                        </div>
                        <div class="btnSelect margin-top-30">
                            <button id="btnEsbSender" type="button" class="btn btn-primary">进入测试</button>
                        </div>
                    </div>
                </div>
                <div class="col-md-12">
                    <form id="svcOverview" class="row">
                        <div class="form-group col-md-12">
                            <div class="row">
                                <label class="col-sm-2">消息格式：</label>
                                <label name="msgType" class="col-sm-10"></label>
                            </div>
                        </div>
                        <div class="form-group col-md-12">
                            <div class="row">
                                <label class="col-sm-2">服务代码：</label>
                                <label name="code" class="col-sm-10"></label>
                            </div>
                        </div>
                        <div class="form-group col-md-12">
                            <div class="row">
                                <label class="col-sm-2">服务名称：</label>
                                <label name="name" class="col-sm-10"></label>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div id="wrapperId2" class="row common-box margin-top-0">
                <div class="col-md-12 modeOverview">
                    <form id="testOverview" class="row">
                        <div class="col-md-6">
                            <div class="row">
                                <div class="form-group col-md-7">
                                    <div class="input-group">
                                        <div class="input-group-addon border-0 padding-left-0">当前模式</div>
                                        <input name="modeName" type="text" class="form-control input-sm" readonly>
                                        <div id="btnToggleMode" class="input-group-addon shape-link" style="padding-left: 5px; padding-right: 5px">
                                            <i class="fa fa-exchange" title="切换调试模式"></i>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group col-md-5 padding-left-0">
                                    <div class="input-group">
                                        <div class="input-group-addon border-0 padding-left-0">通信方式</div>
                                        <input name="urlType" type="text" class="form-control input-sm" readonly>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group col-md-6 padding-left-0">
                            <div class="input-group">
                                <div class="input-group-addon border-0 padding-left-0">服务地址</div>
                                <input name="url" type="text" class="form-control input-sm" readonly>
                            </div>
                        </div>
                    </form>
                </div>
                <form id="logForm">
                    <div class="col-md-5 padding-right-0">
                        <div class="form-group">
                            <label>
                                请求内容
                                <i id="btnCreateMsgIn" class="fa fa-plus shape-link margin-left-5" title="生成请求内容"></i>
                                <i id="btnClearMsg" class="fa fa-trash shape-link margin-left-5" title="清空请求与应答内容"></i>
                            </label>
                            <label id="svcUrlState" class="pull-right">
                                服务地址<span class="badge margin-left-5">待检测</span>
                            </label>
                            <textarea name="out_msg" class="form-control"
                                      placeholder="请求内容可自动生成或手工输入"></textarea>
                        </div>
                    </div>
                    <!-- Single button -->
                    <div class="col-md-2">
                        <div style="text-align: center;margin-top: 230px">
                            <div class="btn-group">
                                <button id="btnPost" type="button" class="btn btn-primary">发送</button>
                            <#--<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">-->
                            <#--<span class="caret"></span>-->
                            <#--<span class="sr-only">Toggle Dropdown</span>-->
                            <#--</button>-->
                            <#--<ul class="dropdown-menu">-->
                            <#--<li><a id="btnPost" type="button">发送</a></li>-->
                            <#--<li><a id="btnReceive" type="button">接收</a></li>-->
                            <#--<li><a id="btnLog" type="button">日志</a></li>-->
                            <#--<!--<li role="separator" class="divider"></li>&ndash;&gt;-->
                            <#--<li><a id="btnDownloadAll" type="button">下载</a></li>-->
                            <#--</ul>-->
                            </div>
                        </div>
                    </div>

                    <a id="downLog" download="" href="" target="blank"></a>
                    <div class="col-md-5 padding-left-0">
                        <div class="form-group">
                            <label>
                                应答内容
                                <i id="btnDownloadMsgAck" class="fa fa-download shape-link margin-left-5"
                                   title="下载应答内容"></i>
                            </label>
                            <label id="latestTestState" class="pull-right">
                                最近测试<span class="badge margin-left-5">未知</span>
                            </label>

                            <a id="downAck" download="" href="" target="blank"></a>
                            <textarea name="ack_msg" class="form-control" readonly></textarea>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<@list.foot tree=true>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/integrationManage/svcUnitTest.js" type="text/javascript"></script>
</@list.foot>