//xuehao 2017-07-27 ：封装表格的公共的js操作

var CommonTable = {
    /**
     * 创建带分页的表格
     * @param tableID   页面中的table元素的ID
     */
    createTable: function (tableID, ajax, columns, columnDefs) {
        var myAttrs = {
            paging: true
        };
        CommonTable.createTableAdvanced(tableID, ajax, columns, columnDefs, myAttrs);
    },
    /**
     * 创建不带分页的表格
     * @param tableID   页面中的table元素的ID
     */
    createTableNoPaging: function (tableID, ajax, columns, columnDefs, myAttrs) {
        if (CommonFunc.isEmpty(myAttrs)) {
            myAttrs = {};
        }
        myAttrs.paging = false;
        myAttrs.info = false;
        CommonTable.createTableAdvanced(tableID, ajax, columns, columnDefs, myAttrs);
    },
    /**
     * 创建表格
     * @param tableID   页面中的table元素的ID
     * @param myAttrs   自定义的属性
     */
    createTableAdvanced: function (tableID, ajax, columns, columnDefs, myAttrs) {
        if (CommonFunc.isEmpty(myAttrs)) {
            myAttrs = {};
        }
        //设置复选框（含有两个属性：checkbox、checkIndex）
        if (myAttrs && myAttrs.checkbox == true) {
            var checkIndex = myAttrs.checkIndex;
            if (CommonFunc.isEmpty(checkIndex))
                checkIndex = 0;
            columns.splice(checkIndex, 0, {
                title: '<input type="checkbox" name="select_all" onclick="CommonTable.selectAll(this)">',
                data: null, width: "7%"
            });
            columnDefs.splice(checkIndex, 0, {　　//为每一行数据添加一个checkbox，
                'targets': checkIndex,
                'searchable': false,
                'orderable': false,
                'className': 'dt-body-center',
                'render': function (data, type, row) {
                    return '<input class="checkbox_select" type="checkbox" name="select_' + row.id +
                        '" value="' + row.id + '" onclick="CommonTable.selectAll(this)">';
                }
            });
        }
        //设置表格属性
        var attrs = {
            serverSide: true,
            ordering: false,
            searching: false,
            lengthChange: false,
            responsive: true,
            // stateSave: true,
            paging: (myAttrs && myAttrs.paging == false) ? false : true,
            pageLength: 10,
            pagingType: (myAttrs && myAttrs.paging == false) ? "" : "full_numbers",//分页样式的类型
            destroy: true, //Cannot reinitialise DataTable,解决重新加载表格内容问题
            info: (myAttrs && myAttrs.info == false) ? false : true,
            language: CommonTable.pagerInfo(),
            ajax: ajax,
            //使用对象数组，一定要配置columns，告诉 DataTables 每列对应的属性
            columns: columns,
            columnDefs: columnDefs
        };
        $(CommonFunc.formatIdForJquery(tableID)).DataTable(attrs);

        //重置全选复选框
        $('[name="select_all"]').removeAttr("checked");
    },
    //分页信息
    pagerInfo: function (myAttrs) {
        return {
            zeroRecords: "暂无记录",
            infoEmpty: '',
            info: '当前显示 _START_ 至 _END_ 项(共 _TOTAL_ 项 / _PAGES_ 页)',
            paginate: {
                first: "首页",
                previous: "上页",
                next: "下页",
                last: "末页"
            }
        }
    },
    selectAll: function (me) {
        // console.log('selectAll');   //测试
        // console.log(me);   //测试

        if (me.name == "select_all") {
            if (me.checked) {
                $('.checkbox_select').each(function () {
                    this.checked = true;
                });
            } else {
                $('.checkbox_select').each(function () {
                    this.checked = false;
                });
            }
        } else {
            var selectAllState = $('[name="select_all"]').is(':checked');
            var isSelectAll = true;
            $('.checkbox_select').each(function () {
                if (!this.checked)
                    isSelectAll = false;
            });
            if (isSelectAll && !selectAllState) {
                // 在实现全选功能的时候用了attr之后，第一次全选可以实现，可是第二次全选却没办法实现的现象,
                // 会出现这样的情况，在于attributes和properties之间的差异，函数attr获取的值来自于attributes,
                // 然而当我们在控制台查看checkbox对象的时候会发现checked的值不是在attributes中，而是在properties,
                // 综上所述，如果使用jquery,应使用prop方法来获取和设置checked属性，不应使用attr.
                $('[name="select_all"]').prop("checked", "true");
            }
            else if (!isSelectAll && selectAllState) {
                $('[name="select_all"]').removeAttr("checked");
            }
        }
    },
    /**
     * 获取复选框的值列表(即主键ID列表)
     */
    getSelectedData: function (tableID) {
        tableID = CommonFunc.formatIdForJquery(tableID);
        var idList = [];
        $(tableID + ' .checkbox_select').each(function () {
            if (this.checked) {
                idList.push($(this).val());
            }
        });
        return idList;
    },
    /**
     * 获取复选框所在行数据列表
     */
    getSelectedRows: function (tableID) {
        tableID = CommonFunc.formatIdForJquery(tableID);
        var rows = [];
        var table = $(tableID).DataTable();
        $(tableID + ' .checkbox_select').each(function () {
            if (this.checked) {
                rows.push(table.row($(this).parents("tr")).data());
            }
        });
        return rows;
    }
}