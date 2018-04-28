<#macro editModal
id=""
title=""
buttonId="btnSave"
buttonName="保存"
buttonType="button"
buttonClick=""
buttonStyle="blue"
buttonId2=""
buttonName2=""
buttonType2="button"
buttonClick2=""
buttonStyle2="default"
buttonId3=""
buttonName3=""
buttonType3="button"
buttonClick3=""
buttonStyle3="default"
buttonName4=""
buttonType4="button"
buttonClick4=""
buttonStyle4="default"
modalBig=""
modalheader=""
modaltitle="usermodal-title"
modalbody="modal-body-padding"
formId=""
buttonCloseVisible="true"
backdrop="true"
keyboard="true"
bodyPaddingLeft=0
bodyPaddingRight=15
bodyPaddingLeft=15>
<div id="${id}" class="modal fade" data-backdrop="${backdrop}" data-keyboard="${keyboard}">
    <div class="modal-dialog ${modalBig}">
        <div class="modal-content">
            <div class="modal-header ${modalheader}" style="height: 40px; padding-top: 10px; padding-bottom: 10px;">
                <#if buttonCloseVisible=="true">
                    <button type="button" class="close" onclick="$('#${id}').modal('hide')" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                </#if>
                <h4 class="modal-title ${modaltitle}">${title}</h4>
            </div>
        <#--<#if action !="" >-->
            <#if formId!="">
            <form id="${formId}">
            </#if>
            <div class="modal-body ${modalbody}" style="padding-left: ${bodyPaddingLeft}px; padding-right: ${bodyPaddingRight}px">
                <#nested />
            </div>

            <div class="modal-footer" style="padding-left: ${bodyPaddingLeft}px; padding-right: ${bodyPaddingRight}px">
                <#if buttonId4!="">
                    <button id="${buttonId4}" type="${buttonType4}" onclick="${buttonClick4}"
                            class="btn ${buttonStyle4}">${buttonName4}</button>
                </#if>
                <#if buttonId3!="">
                    <button id="${buttonId3}" type="${buttonType3}" onclick="${buttonClick3}"
                    	class="btn ${buttonStyle3}">${buttonName3}</button>
                </#if>
                <#if buttonId2!="">
                    <button id="${buttonId2}" type="${buttonType2}" onclick="${buttonClick2}"
                    	class="btn ${buttonStyle2}">${buttonName2}</button>
                </#if>
                <#if buttonId!="">
                    <button id="${buttonId}" type="${buttonType}" onclick="${buttonClick}"
                    	class="btn ${buttonStyle}">${buttonName}</button>
                </#if>
                <#if buttonCloseVisible=="true">
                    <button type="button" class="btn default" onclick="$('#${id}').modal('hide')">关闭</button>
                </#if>
            </div>
            <#if formId!="">
            </form>
            </#if>
        <#--</#if>-->
        </div>
    </div>
</div>
</#macro>

<#macro masteerModal
id=""
modalTitle=""
>
<style type="text/css">
    .modal {
        z-index: 10050;
        outline: none;
        overflow-y: hidden !important;
        height: 95% !important;
    }

    .modal .modal-dialog {
        z-index: 10051;
    }

    .modal .modal-header {
        border-bottom: 1px solid #EFEFEF; }
</style>
<div id="${id}" class="modal fade" data-backdrop=true data-keyboard=true>
    <div class="modal-dialog modal-lg" style="height: inherit">
        <div class="modal-content" style="height: 100%">
            <div class="modal-header" style="padding: 10px 15px 5px">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
                <h4 class="modal-title">${modalTitle}</h4>
            </div>
            <div class="modal-body" style="padding: 0;height: 80%">
            <#nested />
            </div>
            <div class="modal-footer">
                <button type="button" class="btn dark btn-outline" data-dismiss="modal">关闭</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
</div>
</#macro>