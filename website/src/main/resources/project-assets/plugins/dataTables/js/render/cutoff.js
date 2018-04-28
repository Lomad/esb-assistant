/**
 *  @name dataTable单元格自动截断
 *  @summary 只需要指定一个列索引号（从0开始），即可返回包含自动截断功能的targets对象
 *  @author zdj
 *  @requires DataTables 1.10+
 *
 * @returns 返回包含自动截断功能的targets对象
 *
 *  @example
 *    // 第2列自动截断在一行
 *    $('#example').DataTable( {
 *      columnDefs: [  $.fn.dataTable.columndefs.cutoff(1)]
 *    } );
 */
jQuery.fn.dataTable.columndefs = jQuery.fn.dataTable.columndefs || {};
jQuery.fn.dataTable.columndefs.cutoff = function (index) {

    var esc = function (t) {
        if (!t) {
            return '';
        }

        var str = t.toString();
        if (str && str.length > 0) {
            return str
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;');
        } else {
            return str;
        }
    };

    return {
        targets: index,
        render: function (d, type, row) {
            if (!d) {
                d = '';
            }
            return '<span class="td-cutoff-span" title="' + esc(d) + '">' + d + '</span>';
        }
    };
};

/**
 * xuehao 2018-03-27：新增，用于显示超出宽度的信息，超出部分使用省略号
 * @param callback  回调函数（前四个参数为当前列的默认参数，最后一个参数为args）
 * @param args  回调函数的参数（json格式）
 * @param wordbreak
 * @param escapeHtml
 */
jQuery.fn.dataTable.render.ellipsisNew = function (callback, args, wordbreak, escapeHtml) {
    var esc = function (t) {
        return t
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    };

    return function (d, type, row, meta) {
        // Order, search and type get the original data
        if (type !== 'display') {
            return d;
        }

        //回调函数
        if(callback) {
            d = callback(d, type, row, meta, args);
        }

        if (typeof d !== 'number' && typeof d !== 'string') {
            return d;
        }

        d = d.toString(); // cast numbers

        // Find the last white space character in the string
        if (wordbreak) {
            d = d.replace(/\s([^\s]*)$/, '');
        }

        // Protect against uncontrolled HTML input
        if (escapeHtml) {
            d = esc(d);
        }

        return '<span class="td-cutoff-span" title="' + d + '">' + d + '</span>';
    };
};
