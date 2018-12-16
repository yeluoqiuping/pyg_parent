package com.pyg.cart.service;

import com.pyg.pojogroup.Cart;

import java.util.List;

/**
 * @author 购物车服务接口
 * @date 2018-12-16 09:28
 * @description
 */
public interface CartService {


    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num );
}
