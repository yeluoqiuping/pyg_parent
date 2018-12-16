app.controller('searchController', function ($scope, $location ,searchService) {
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 20,
        'sortField': '',
        'sort': ''
    };
    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果

                $scope.pageList = [];
                var startPage = 1;//开始页码
                var endPage = $scope.resultMap.totalPages;//截止页码

                $scope.firstDot = false;//前面有点
                $scope.lastDot = false;//后边有点
                //计算开始页码和结束页码--根据当前页计算
                if ($scope.resultMap.totalPages > 5) { //如果页码数量大于5
                    if ($scope.searchMap.pageNo < 3) {//如果当前页码小于等于3 ，显示前5页
                        endPage = 5;
                        $scope.lastDot = true;
                    } else if ($scope.searchMap.pageNo > ($scope.resultMap.totalPages - 2)) {//显示后5页
                        startPage = $scope.resultMap.totalPages - 4;
                        $scope.firstDot = true;
                    } else {//显示以当前页为中心的5页
                        startPage = $scope.searchMap.pageNo - 2;
                        endPage = $scope.searchMap.pageNo + 2;

                        $scope.firstDot = true;
                        $scope.lastDot = true;
                    }
                }
                //构建页码

                for (var i = startPage; i < endPage; i++) {
                    $scope.pageList.push(i);
                }
            }
        );
    }

    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    };

    $scope.delSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    };
    $scope.queryByPage = function (page) {
        if (page < 1 || page > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = page;


        $scope.search();
    }

    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    $scope.isKeywordsContainsBrand=function () {
        var brandList = $scope.resultMap.brandList;

        var keywords = $scope.searchMap.keywords;
        for (var i =0 ; i<brandList.length;i++){
            if (keywords.indexOf(brandList[i].text)>=0){
                return true;
            }
        }

        return false;
    }

    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();
    }
});
