<#--文本框自带弹出业务系统树的功能-->
<#macro textboxTree
idTree=""
size = 12
width = 0
inline = false
placeHolder = ""
onchange = ""
>
    <#assign idTextbox="${idTree}-textbox">
    <#assign idHidden="${idTree}-hidden">
    <#assign idTreeSkin="${idTree}-skin">

    <#--<#if inline=="true" >-->
    <#--<div class="form-group">-->
    <#--<#elseif (width>0)>-->
    <#--<div class="" style="width: ${width}px">-->
    <#--<#else>-->
    <#--<div class="">-->
    <#--</#if>-->

    <div class="input-group"
    <#if (width>0)>
    style="width: ${width}px"
    </#if>
    >
        <input id="${idTextbox}" type="text" class="form-control" placeholder="${placeHolder}" readonly
               style="background-color: #fff">
        <span class="input-group-btn">
            <button class="btn btn-default" type="button" style="margin-left: -1px">
                <i class="fa fa-filter cp btnImg prject-color-default"></i>
            </button>
        </span>
        <div id="${idTreeSkin}" class="popTreeSkin treeFilterSkin" style="display: none;
            position: absolute;
            z-index: 999999;
            left: 0px;
            top: 34px;
            background-color: #fff;">
            <ul id="${idTree}" class="ztree" style="margin-top: 0px;
            border: 1px solid #c2cad8;
            height: 200px;
            overflow-y: scroll;
            overflow-x: auto;"></ul>
        </div>
        <input id="${idHidden}" type="hidden">

    <#--</div>-->
</div>
</#macro>