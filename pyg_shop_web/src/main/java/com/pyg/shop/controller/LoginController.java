package com.pyg.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 获取登录用户名
 * @date 2018/11/26 10:14
 * @description
 */

@RestController
public class LoginController {
    @RequestMapping("loginName")
    public Map loginName(){
        Map map = new HashMap();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName",username);

        return map;
    }
}
