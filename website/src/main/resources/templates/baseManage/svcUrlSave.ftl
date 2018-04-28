<#macro svcUrlSave>
<div class="modal fade" id="editFormContainer" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" style="width: 50%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    地址信息
                </h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form" id="editForm">
                    <div class="form-group">
                        <label for="svcType" class="col-sm-3 control-label">服务类型</label>
                        <div class="col-sm-8">
                            <input name="id" type="hidden">
                            <select id="svcType" name="svcType" class="form-control"></select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="url" class="col-sm-3 control-label">服务地址(Url)</label>
                        <div class="col-sm-8">
                            <input name="url" maxlength="1024" type="text" class="form-control">
                        </div>
                    </div>
                    <div class="form-group" style="margin-top: -10px">
                        <label for="url" class="col-sm-3 control-label"></label>
                        <div id="urlTips" class="col-sm-8 msg-tips"></div>
                    </div>
                    <div class="form-group">
                        <label for="name" class="col-sm-3 control-label">地址简称</label>
                        <div class="col-sm-8">
                            <input name="name" maxlength="100" type="text" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="desp" class="col-sm-3 control-label">描述</label>
                        <div class="col-sm-8">
                            <input name="desp" maxlength="300" type="text" class="form-control">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary submit" id="saveBtn">
                    <i class="fa fa-save" style="margin-right: 4px"></i> 保存
                </button>
                <button type="submit" class="btn btn-primary submit" id="linkBtn">
                    <i class="fa fa-link" aria-hidden="true"></i> 测试链接
                </button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>
</#macro>