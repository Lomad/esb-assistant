/**
 * Created by Admin on 2016/10/20.
 */
$(document).ready(function(){
    $(".page-content").height($(".page-sidebar-menu").height())
    App.init(); // initlayout and core plugins
    Index.init();
    bootbox.setDefaults("locale", "zh_CN");//bootbox默认中文
    $(".getContent").slimScroll({height: (parseInt($(window).height()) - 168).toString() + "px"});
    //$("#time3v").slimScroll({height: "200px"});
});