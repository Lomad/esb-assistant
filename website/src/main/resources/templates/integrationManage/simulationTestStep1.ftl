<#macro step>
<div class="panel panel-default">
    <div class="panel-heading">平台服务<span>(Socket)</span></div>
    <div class="panel-body">
        <form class="form-inline">
            <div class="form-group">
                <label for="svcCode">服务地址</label>
                <select id="urlEsbID" class="form-control input-sm margin-left-right-15"
                        style="width: 400px"></select>
            </div>
            <button id="startEsbService" type="button" class="btn btn-default btn-sm">开启服务</button>
        </form>
        <form class="form-inline">
            <div class="form-group">
                <label>说明:</label>
                <label>请选择用于模拟测试的平台服务地址！</label>
            </div>
        </form>
    </div>
</div>
<div class="panel panel-default" style="margin-bottom: 0">
    <div class="panel-heading">业务系统服务<span>(Socket)</span></div>
    <div class="panel-body">
        <form class="form-inline">
            <div class="form-group">
                <label for="svcCode">服务地址</label>
                <select id="urlAppID" class="form-control input-sm margin-left-right-15"
                        style="width: 400px"></select>
            </div>
            <button id="testAppService" type="button" class="btn btn-default btn-sm">连接服务</button>
        </form>
        <form class="form-inline">
            <div class="form-group">
                <label>说明:</label>
                <label>请选择用于模拟测试的业务系统服务地址(请勿使用正式地址)！</label>
            </div>
        </form>
    </div>
</div>
</#macro>