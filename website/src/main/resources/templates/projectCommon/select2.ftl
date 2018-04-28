<#macro head>
<link href="${request.contextPath}/project-assets/plugins/select2/css/select2.min.css" type="text/css" rel="stylesheet"/>
    <style type="text/css">
        .modern-forms .mdn-select:after {border-top-color: transparent;}
        .select2-container {margin-top: 5px;width:100% !important;}
    </style>
</#macro>


<#macro foot>
<script src="${request.contextPath}/project-assets/plugins/select2/js/select2.full.min.js" type="text/javascript"></script>
<script src="${request.contextPath}/project-assets/plugins/select2/js/i18n/zh-CN.js"></script>
    <#--<#if auto>-->
    <#--<script type="text/javascript">-->
        <#--$('select').select2({language: "zh-CN"});-->
    <#--</script>-->
    <#--</#if>-->

    <script type="text/javascript">
        //由于模态框的tabindex="-1"属性，会导致select2的搜索框失效，因此需要加上下句脚本
        $.fn.modal.Constructor.prototype.enforceFocus = function () { };
    </script>

</#macro>