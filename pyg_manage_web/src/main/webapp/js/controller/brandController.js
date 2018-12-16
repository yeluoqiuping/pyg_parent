app.controller('brandController', function ($scope,$controller,brandService) {

    //继承控制器 1被继承的名字，2{}
    $controller('baseController',{$scope:$scope});

    /*$scope.findAll=function () {
            //查询所有品牌数据
        $http.get('../brand/findAll.do').success(
            function (response) {
                $scope.list=response
            }
        )
    }*/

    //分页查询
    $scope.findPage = function (page, rows) {
        brandService.findPage(page,rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total
            }
        )
    }

    $scope.entity = {};//品牌对象 json对象
    $scope.save = function () {
        var method;
        if ($scope.entity.id != null){
            method=brandService.update($scope.entity)
        }else {
            method=brandService.add($scope.entity)
        }
        method.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message)
                }
            }
        )
    };

    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (data) {
                $scope.entity = data;
            }
        )
    }



    $scope.dele=function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message)
                }
                $scope.selectIds=[];
            }
        )
    }

    $scope.searchEntity={};

    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems=response.total;
                $scope.list=response.rows;
            }
        )
    }
})