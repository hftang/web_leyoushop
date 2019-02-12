package com.leyou.service;

import com.leyou.client.BrankClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-02-11 20:50
 * @desc
 */
@Service
public class PageService {

    @Autowired
    private BrankClient brankClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;


    public Map<String, Object> loadModel(Long spuId) {

        Map<String, Object> model = new HashMap<>();

        Spu spu = goodsClient.querySpuById(spuId);
        List<Sku> skus = spu.getSkus();

        SpuDetail detail = spu.getSpuDetail();

        Brand brand = brankClient.queryBrandById(spu.getBrandId());

        List<Category> categories = categoryClient.queryCategorysByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));


        List<SpecGroup> specs = specificationClient.queryGroupByCid(spu.getCid3());


        model.put("spu", spu);
        model.put("title", spu.getTitle());
        model.put("subTitle", spu.getSubTitle());
        model.put("skus", skus);
        model.put("detail", detail);
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("specs", specs);


        return model;
    }
}
