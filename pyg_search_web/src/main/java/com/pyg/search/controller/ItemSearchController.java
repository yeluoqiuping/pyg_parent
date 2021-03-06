package com.pyg.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author xxx
 * @date 2018/12/4 11:47
 * @description
 */

@RestController
@RequestMapping("itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 查询方法
     * @param searchMap
     * @return
     */
    @RequestMapping("search")
    public Map<String,Object> search(@RequestBody Map searchMap){

        return itemSearchService.search(searchMap);
    }

}
