//没有分页的模块
var app = angular.module('pyg', []);

//过滤器
app.filter('trustHtml',['$sce',function ($sce) {
    return function (title) {
        return $sce.trustAsHtml(title);
    }

}])