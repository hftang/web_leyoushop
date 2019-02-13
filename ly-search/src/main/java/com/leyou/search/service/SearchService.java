package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.GoodsRespository;
import com.leyou.search.client.BrankClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hftang
 * @date 2019-01-25 14:38
 * @desc
 */
@Slf4j
@Service
public class SearchService {
    @Autowired
    private BrankClient brankClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRespository goodsRespository;
    @Autowired
    private ElasticsearchTemplate template; //使用 聚合

    public Goods buildGoods(Spu spu) {

        //1.1查询分类
        List<Category> categoryList = categoryClient.queryCategorysByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> names = categoryList.stream().map(Category::getName).collect(Collectors.toList());
        //1.2查询品牌
        Brand brand = brankClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //1.3搜素字段
        String all = spu.getTitle() + StringUtils.join(names, ",") + brand.getName();

        //2.0查sku

        List<Sku> skuList = goodsClient.querySkuById(spu.getId());
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //一次遍历就可以了
//        Set<Long> priceSet = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());

        Set priceSet = new HashSet();

        //2.1对sku进行处理：
        List<Map<String, Object>> skus = new ArrayList<>();
        skuList.forEach(item -> {
            //我们只需要其中四个字段
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("title", item.getTitle());
            Long price = item.getPrice();
            map.put("price", price);
            priceSet.add(price);
            map.put("iamge", StringUtils.substringBefore(item.getImages(), ","));
            skus.add(map);
        });

        //3 规格参数 key是规格参数的名字 value是规格参数的值
        Map<String, Object> specs = new HashMap<>();

        //3.1查询规格参数
        List<SpecParam> params = specificationClient.queryParamByList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        //3.2查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spu.getId());

        //3.2.1 获取通用规格参数
        String genericSpecJson = spuDetail.getGenericSpec();
        //转成map
        Map<Long, String> genericSpec = JsonUtils.parseMap(genericSpecJson, Long.class, String.class);
        //3.2.2 获取特有规格参数
        String specialSpecJson = spuDetail.getSpecialSpec();
        //转成map
        Map<Long, List<String>> specialSpecMap = JsonUtils.nativeRead(specialSpecJson, new TypeReference<Map<Long, List<String>>>() {
        });

        //4向 specs中填值：
        params.forEach(item -> {
            String key = item.getName();
            Object value = "";

            //4.1 判断是通用规格参数 还是 特有规格参数
            if (item.getGeneric()) {
                value = genericSpec.get(item.getId());
                //判断数值类型
                if (item.getNumeric()) {
                    //处理成段
                    value = chooseSegment(value.toString(), item);
                }
            } else {
                //特有的
                value = specialSpecMap.get(item.getId());
            }

            specs.put(key, value);

        });


        //构建goods
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());

        //1.4
        goods.setAll(all);// 搜索字段 包含标题 分类 品牌 规格等
        goods.setPrice(priceSet); // 所有sku的价格集合
        goods.setSkus(JsonUtils.serialize(skus));//  sku的json格式
        goods.setSpecs(specs);// TODO 所有可以搜素的规格参数
        goods.setSubTitle(spu.getSubTitle());


        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /***
     * 商品搜索 功能
     * @param searchRequest
     * @return
     */

    public PageResult<Goods> search(SearchRequest searchRequest) {
        Integer page = searchRequest.getPage() - 1;//currentpage
        Integer size = searchRequest.getSize();//rows num
        //查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //过滤结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
        //做聚合：

        // 分类的聚合
        String categoryAggName = "categoryAggName";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //品牌聚合
        String brandAggName = "brandAggName";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        AggregatedPage<Goods> aggregatedPage = template.queryForPage(queryBuilder.build(), Goods.class);

        //聚合结果：解析
        Aggregations aggregations = aggregatedPage.getAggregations(); //获取到所有聚合结果

        Aggregation categoryAgg = aggregations.get(categoryAggName); //根据名字获取不同的 聚合结果
        Aggregation brandAgg = aggregations.get(brandAggName);

        List<Category> categories = parsedCategoryAgg((LongTerms) categoryAgg); //解析
        List<Brand> brands = parseBrandAgg((LongTerms) brandAgg);

        //分页 分页结果
        queryBuilder.withPageable(PageRequest.of(page, size));

        //封装成一个查询 带Boolean的

//        MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", searchRequest.getKey());
        QueryBuilder basicQuery = buildBasicQuery(searchRequest);
        //过滤
        queryBuilder.withQuery(basicQuery);
        //查询
        Page<Goods> result = goodsRespository.search(queryBuilder.build());
        long total = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
//        return new PageResult<Goods>(total, (long) totalPages, goodsList);

        //完成 规格参数的聚合
        List<Map<String, Object>> specs = null;

        if (categories != null && categories.size() == 1) {
            //分类存在 就组织 规格参数聚合
            specs = buildSpecificationAgg(categories.get(0).getId(), basicQuery);
        }


        return new SearchResult(total, (long) totalPages, goodsList, categories, brands, specs);
    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {

        //1 创建boolean 查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //2 创建查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
        //3 过滤条件
        Map<String, String> filters = request.getFilter();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            //对key 做处理 如果只是 分类 和 品牌的 不做处理 否则要做处理
            if (!"cid3".equals(key) && !"brandId".equals(key)) {
                key = "specs." + key + ".keyword";
            }
            String value = entry.getValue();
            queryBuilder.filter(QueryBuilders.termQuery(key, value));
        }


        return queryBuilder;
    }

    /**
     * @param id
     * @param basicQuery
     * @return
     */

    private List<Map<String, Object>> buildSpecificationAgg(Long id, QueryBuilder basicQuery) {

        List<Map<String, Object>> specs = new ArrayList<>();

        //1查询需要聚合的规格参数
        List<SpecParam> specParams = specificationClient.queryParamByList(null, id, true);

        //2聚合
        NativeSearchQueryBuilder queryBuild = new NativeSearchQueryBuilder();
        queryBuild.withQuery(basicQuery);
        specParams.forEach(item -> {
            String name = item.getName();
            queryBuild.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
        });
        //3获取结果
        AggregatedPage<Goods> aggregatedPage = template.queryForPage(queryBuild.build(), Goods.class);
        //4解析聚合
        Aggregations aggregations = aggregatedPage.getAggregations();

        specParams.forEach(params -> {
            String name = params.getName();
            StringTerms terms = aggregations.get(name);
            List<String> options =
                    terms.getBuckets()
                            .stream().map(b -> b.getKeyAsString())
                            .collect(Collectors.toList());

            //准备map

            Map<String, Object> map = new HashMap<>();
            map.put("k", name);
            map.put("options", options);

            specs.add(map);
        });


        return specs;
    }


    /**
     * 解析聚合
     *
     * @param brandAgg
     * @return
     */

    private List<Brand> parseBrandAgg(LongTerms brandAgg) {

        try {
            List<Long> ids = brandAgg.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brankClient.queryBrandsByIds(ids);
            return brands;

        } catch (Exception e) {
            log.error("搜索查询异常：brand:::" + e.toString());
            return null;
        }

    }

    private List<Category> parsedCategoryAgg(LongTerms categoryAgg) {
        try {
            List<Long> ids = categoryAgg.getBuckets().stream().map(a -> a.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategorysByIds(ids);
            return categories;
        } catch (Exception e) {
            log.error("搜索查询异常：category:::" + e.toString());
            return null;
        }
    }

    /**
     * 增加或者修改 索引库
     *
     * @param spuId
     */
    public void createOrUpdate(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //构建good
        Goods goods = buildGoods(spu);
        //存入索引库
        goodsRespository.save(goods);


    }

    /**
     * 处理删除的索引
     * @param spuId
     */
    public void deleteIndex(Long spuId) {

        goodsRespository.deleteById(spuId);
    }
}
