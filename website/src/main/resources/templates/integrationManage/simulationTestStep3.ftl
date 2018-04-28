<#macro step>
<div class="col-md-4 col-sm-4" style="height: 400px;">
    <label>服务测试结果</label>
    <ul id="svcTestResultList" class="list-group"></ul>
</div>
<div class="col-md-8 col-sm-8">
    <form id="stepResultForm">
        <div class="form-group">
            <label>测试结果</label>
            <select id="logResult" name="result" class="form-control"></select>
        </div>
        <div class="form-group">
            <label>结果描述</label>
            <textarea name="desp" class="form-control" rows="6"></textarea>
        </div>
        <div class="pull-right">
            <button id="btnFinishTest" type="button" class="btn btn-primary btn-sm">
                <i class="fa fa-save" style="margin-right: 4px"></i>保存结果
            </button>
            <button id="btnExport" type="button" class="btn btn-primary btn-sm">
                <i class="fa fa-download" style="margin-right: 4px"></i>导出测试报告
            </button>
            <a id="downReport" download="" href="" target="blank"></a>
        </div>
    </form>
</div>
</#macro>