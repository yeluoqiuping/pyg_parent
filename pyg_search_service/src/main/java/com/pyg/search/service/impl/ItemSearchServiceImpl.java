package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xxx
 * @date 2018/12/4 11:40
 * @description
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> resultMap = new HashMap();

        //处理空格
        String keywords = (String) searchMap.get("keywords");

        searchMap.put("keywords",keywords.replace(" ",""));
        //1.调用高亮查询方法
        resultMap.putAll(searchHiList(searchMap));

        //2.根据关键字查询商品分类
        Map categoryMap = searchCategoryList(searchMap);
        resultMap.putAll(categoryMap);

        //1.按关键字查询（高亮显示）
        //2.根据关键字查询商品分类
        //3.查询品牌和规格列表
        Object category = searchMap.get("category");
        if (category == null || "".equals(category)) {
            List<String> categoryList = (List<String>) categoryMap.get("categoryList");
            category = categoryList.get(0);

        }

        Map searchBrandAndSpecList = searchBrandAndSpecList((String) category);
        resultMap.putAll(searchBrandAndSpecList);
        //返回结果
        return resultMap;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodIds(List goodsIds) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);

        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 查询品牌和规格列表
     *
     * @param category 分类名称
     * @return
     */
    public Map searchBrandAndSpecList(String category) {
        Map bsMap = new HashMap();
        //获取模板ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null) {
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            bsMap.put("brandList", brandList);
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            bsMap.put("specList", specList);
        }
        return bsMap;
    }

    /**
     * 根据关键字查分类列表：分组查询
     *
     * @param searchMap
     * @return K--V
     */
    private Map searchCategoryList(Map searchMap) {
        Map categoryMap = new HashMap();
        List<String> categoryList = new ArrayList<>();
        //关键字条件
        String keywords = (String) searchMap.get("keywords");
        Criteria criteria = new Criteria("item_keywords").is(keywords);

        //设置分组选项
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");

        //组装分组查询条件
        Query query = new SimpleQuery();
        query.addCriteria(criteria);
        query.setGroupOptions(groupOptions);

        //得到分组页
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            categoryList.add(groupValue);
        }
        categoryMap.put("categoryList", categoryList);
        return categoryMap;
    }

    /**
     * 高亮的列表查询
     *
     * @param searchMap
     * @param
     * @return
     */
    private Map searchHiList(Map searchMap) {
        Map highlightMap = new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮选项
        HighlightOptions highlightOptions = new HighlightOptions();

        //高亮的前缀和后缀
        highlightOptions.addField("item_title");//高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);

        //关键字条件
        String keywords = (String) searchMap.get("keywords");
        Criteria criteria = new Criteria("item_keywords").is(keywords);

        //过滤条件的构建
        //分类的过滤
        Object category = searchMap.get("category");
        if (category != null && StringUtils.isNotEmpty((String) category)) {
            FilterQuery categoryFilter = new SimpleFilterQuery();
            Criteria categoryCriteria = new Criteria("item_category").is(category);
            categoryFilter.addCriteria(categoryCriteria);
            query.addFilterQuery(categoryFilter);
        }
        //按品牌筛选
        Object brand = searchMap.get("brand");
        if (brand != null && StringUtils.isNotEmpty((String) brand)) {
            FilterQuery brandFilter = new SimpleFilterQuery();
            Criteria brandCriteria = new Criteria("item_brand").is(brand);
            brandFilter.addCriteria(brandCriteria);
            query.addFilterQuery(brandFilter);
        }
        //按规格筛选
        Object spec = searchMap.get("spec");
        if (spec != null) {
            Map<String, String> specMap = (Map) spec;
            for (String key : specMap.keySet()) {
                FilterQuery specFilter = new SimpleFilterQuery();
                Criteria specCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                specFilter.addCriteria(specCriteria);
                query.addFilterQuery(specFilter);
            }
        }
        //按价格区间过滤
        Object price = searchMap.get("price");
        if (price != null && StringUtils.isNotEmpty((String) price)) {
            String[] splitPrice = ((String) price).split("-");
            if (!splitPrice[0].equals("0")) {
                FilterQuery lowPriceFilter = new SimpleFilterQuery();
                Criteria lowCriteria = new Criteria("item_price").greaterThanEqual(splitPrice[0]);
                lowPriceFilter.addCriteria(lowCriteria);
                query.addFilterQuery(lowPriceFilter);
            }
            if (!splitPrice[1].equals("*")) {
                FilterQuery highPriceFilter = new SimpleFilterQuery();
                Criteria highCriteria = new Criteria("item_price").lessThanEqual(splitPrice[1]);
                highPriceFilter.addCriteria(highCriteria);
                query.addFilterQuery(highPriceFilter);
            }
        }

        //分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");//提取页码

        Integer pageSize = (Integer) searchMap.get("pageSize");//每页记录数

        query.setOffset((pageNo - 1) * pageSize);//从第几条记录查询
        query.setRows(pageSize);

        //排序
        String sortValue = (String) searchMap.get("sort");//排序方式ASC  DESC
        String sortField = (String) searchMap.get("sortField");

        if (sortValue!=null && StringUtils.isNotEmpty(sortValue)){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }
        //调用solrTemplate方法
        query.addCriteria(criteria);
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlightEntries = highlightPage.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlightEntries) {
            TbItem tbItem = highlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            for (HighlightEntry.Highlight highlight : highlights) {
                List<String> snipplets = highlight.getSnipplets();
                String hiTitle = snipplets.get(0);
                tbItem.setTitle(hiTitle);
            }
        }
        List<TbItem> content = highlightPage.getContent();
        highlightMap.put("rows", content);
        long total = highlightPage.getTotalElements();
        highlightMap.put("total", total);//返回总记录数
        highlightMap.put("totalPages", highlightPage.getTotalPages());//返回总页数
        return highlightMap;
    }

}
