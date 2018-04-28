<#import "projectCommon/listAndTree.ftl" as list/>
<@list.head tree=true>
    <#assign contextPath=request.contextPath>
</@list.head>

<input id="loginUserid" type="hidden" value="${Session["userid"]}"/>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-3">
            <div id="treeWrapper" class="row common-box font-size-14 margin-top-0 padding-bottom-15">
                <div class="col-md-12 col-sm-12">
                    <ul id="appTree" class="ztree padding-0"></ul>
                </div>
            </div>
        </div>
        <div class="col-md-9 margin-top-0">
            <div id="wrapperId2" class="row common-box margin-top-0">
                <div class="col-md-6">
                    <div>
                        <button id="btnDownloadSelected" type="button" class="btn btn-default btn-sm">
                            <i class="fa fa-download" style="margin-right: 4px"></i> 下载选中
                        </button>
                        <button id="btnDownloadAll" type="button" class="btn btn-default btn-sm">
                            <i class="fa fa-cloud-download" style="margin-right: 4px"></i> 下载全部
                        </button>
                        <a id="downPdf" download="" href="" target="blank"></a>
                    </div>
                </div>
                <div class="col-md-6">
                    <form class="form-inline pull-right">
                        <div class="form-group">
                            <select id="svcTypeList"class="form-control input-sm" ></select>
                        </div>
                    </form>
                </div>
                <div class="col-md-12 col-sm-12 margin-top-bottom-15">
                    <table id="listTable" class="listTable"></table>
                </div>
            </div>
        </div>
    </div>
</div>

<@list.foot tree=true>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/js/integrationManage/downloadDoc.js" type="text/javascript"></script>

</@list.foot>