<#macro step>
<div class="col-md-4 col-sm-4" style="height: 400px;">
    <label>测试服务清单</label>
    <ul id="svcList" class="list-group"></ul>
</div>
<div class="col-md-8 col-sm-8" style="height: 400px;">
    <form id="stepLogForm">
        <div class="form-group">
            <label>
                输入消息<i id="btnCreateMsgIn" class="fa fa-plus shape-link" style="margin-left: 4px" title="生成输入消息"></i>
            </label>
            <textarea name="out_msg" class="form-control" rows="6"></textarea>
        </div>
        <div class="form-group">
            <label>应答消息</label>
            <textarea name="ack_msg" class="form-control" rows="6"></textarea>
        </div>
        <div class="form-group">
            <label>测试结果</label>
            <input id="stepLogResult" name="result" class="form-control" readonly></input>
        </div>
        <div class="pull-right">
            <button id="btnPost" type="button" class="btn btn-primary btn-sm">
                <i class="fa fa-paper-plane" style="margin-right: 4px"></i>发送
            </button>
            <button id="btnSaveAndGoNext" type="button" class="btn btn-default btn-sm hide">
                保存结果，测试下一个服务<i class="fa fa-arrow-circle-right" style="margin-left: 4px"></i>
            </button>
        </div>
    </form>
</div>
</#macro>