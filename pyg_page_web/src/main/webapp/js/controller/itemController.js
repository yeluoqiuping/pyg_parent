﻿app.controller('itemController',function($scope){
	
	$scope.num=1;
	$scope.addnum=function(num){
		$scope.num = $scope.num+num;
		
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	$scope.specificationItems={};//记录用户选择的规格
	//用户选择规格
	$scope.selectSpecification=function(name,value){	
		$scope.specificationItems[name]=value;
		for(var i =0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
			$scope.sku={id:0,title:"--------",price:0};
		}
	}

	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		return true;
	}
	//判断某规格选项是否被用户选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}		
	}
	
	//默认加载的title
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	//添加商品到购物车
	$scope.addToCart=function(){
		alert('skuid:'+$scope.sku.id+"数量"+$scope.num);
	}
});