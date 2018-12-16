package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.cart.service.CartService;
import com.pyg.pojogroup.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author xxx
 * @date 2018-12-16 09:30
 * @description
 */

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 查询购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //1.从浏览器的cookie中取出购物车列表
        String cartList_string = CookieUtil.getCookieValue(request, "cartList", "utf-8");

        if (StringUtils.isEmpty(cartList_string)){
            cartList_string = "[]";
        }

        List<Cart> cartList = JSON.parseArray(cartList_string, Cart.class);
        return cartList;
    }



    @RequestMapping("addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){
        try {
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList,itemId,num);
            CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");

            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, "添加失败");
        }

    }
}
