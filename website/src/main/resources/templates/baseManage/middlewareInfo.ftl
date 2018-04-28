<#import "projectCommon/list.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<@list.head>
    <#assign contextPath=request.contextPath>
<style>
    div.radio {
        padding-top: 1px !important;
    }

    .radio-inline {
        margin-left: 50px !important;
    }

    .radio-inline:first-child {
        padding-left: 0 !important;
        margin-left: 0 !important;
    }

</style>
</@list.head>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-12">
            <div id="wrapperId2" class="row common-table margin-top-0">
                <form id="ESBFormID" class="form-horizontal">
                    <div class="form-group">
                        <label class="col-md-3 control-label">中间件类型:</label>
                        <div class="col-md-7">
                            <div class="radio-list">
                                <lable id="selectOdin" class="radio-inline">
                                    <input type="radio" id="Odin" value="Odin" name="ESB_Type" checked>Odin
                                </lable>
                                <label class="radio-inline">
                                    <input type="radio" id="Rhapsody" value="Rhapsody" name="ESB_Type">Rhapsody
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-3 control-label">中间件IP:</label>
                        <div class="col-md-7">
                            <input type="text" id="ESB_IP" name="ESB_IP" class="form-control" maxlength="100">
                            <span class="msg-tips">中间件服务器的IP地址</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-3 control-label">中间件端口号:</label>
                        <div class="col-md-7">
                            <input type="text" id="ESB_Port" name="ESB_Port" class="form-control" maxlength="100">
                            <span class="msg-tips">中间件管理界面显示的端口号，如8080</span>
                        </div>
                    </div>
                    <input type="text" name="ESB_UserName" value="admin" style="position: absolute;z-index: -1;" disabled autocomplete = "off"/><!-- 这个username会被浏览器记住，我随便用个admin-->
                    <input type="password" name="ESB_Password" value=" " style="position: absolute;z-index: -1;" disabled autocomplete = "off"/>
                    <div class="form-group">
                        <label class="col-md-3 control-label">中间件用户名:</label>
                        <div class="col-md-7">
                            <input type="text" id="ESB_UserName" name="ESB_UserName" class="form-control" maxlength="100" autocomplete="new user">
                            <span class="msg-tips">中间件管理界面登录的用户名，如admin</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-3 control-label">中间件密码:</label>
                        <div class="col-md-7">
                            <input type="password" id="ESB_Password" name="ESB_Password" class="form-control" maxlength="100" autocomplete="new password">
                            <span class="msg-tips">中间件管理界面登录的密码</span>
                        </div>
                    </div>
                    <#--<div class="form-group">-->
                        <#--<label class="col-md-3 control-label">中间件测试地址:</label>-->
                        <#--<div class="col-md-7">-->
                            <#--<input type="text" id="ESB_TestUrl" name="ESB_TestUrl" class="form-control" maxlength="100">-->
                            <#--<span class="msg-tips">中间件用于测试的Restful地址，如http://127.0.0.1:15001/esbtest</span>-->
                        <#--</div>-->
                    <#--</div>-->
                    <div class="form-group">
                        <div class="col-md-3 col-md-offset-3">
                            <button id="btnSave" type="button" class="btn btn-primary">
                                <i class="fa fa-search margin-right-5"></i> 保存
                            </button>
                        </div>
                    </div>
                </form>

            </div>
        </div>
    </div>
</div>




<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/baseManage/middlewareInfo.js" type="text/javascript"></script>

</@list.foot>