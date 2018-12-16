 //控制层 
app.controller('contentController' ,function($scope,contentService){
    $scope.contentList=[null,[],];//广告集合
    $scope.findListByCategoryId=function (categoryId) {
        contentService.findListByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        )
    }

    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }

});	
