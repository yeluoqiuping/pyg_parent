package com.pyg.search.service;

import java.util.List;
import java.util.Map;

/**
 * @author xxx
 * @date 2018/12/4 11:38
 * @description
 */
public interface ItemSearchService {
    /**
     *
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 根据itemList导入solr索引库
     * @param list
     */
    public void importList(List list);

    /**
     * 根据传递的spu列表，删除solr索引库
     * @param goodsIds
     */
    public void deleteByGoodIds(List goodsIds);

}
