package com.leyou.service;

import com.leyou.client.BrankClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-02-11 20:50
 * @desc
 */
@Slf4j
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

    @Autowired
    private TemplateEngine templateEngine; //生成静态网页


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

    /***
     * 生成html
     * @param spuId  这个静态页生成应该放到nginx中  所以后期要把网页静态页生成的微服务跟nginx部署到一起
     */

    public void createHtml(Long spuId) {
        Context context = new Context();
        context.setVariables(loadModel(spuId));

        //输出流
        File file = new File("D:\\javaweb\\upload", spuId + ".html");
        //删除重复存在的
        if (file.exists()) {
            file.delete();
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            log.error("[生成静态html异常]：" + e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }


    }

    /**
     * 删除操作
     * @param spuId
     */
    public void deleteHtml(Long spuId) {

        //输出流
        File file = new File("D:\\javaweb\\upload", spuId + ".html");
        //删除重复存在的
        if (file.exists()) {
            file.delete();
        }
    }
}
