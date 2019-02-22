package leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import leyou.item.mapper.SkuMapper;
import leyou.item.mapper.SpuDetailMapper;
import leyou.item.mapper.SpuMapper;
import leyou.item.mapper.StockMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hftang
 * @date 2019-01-22 14:05
 * @desc
 */

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;//发送rabbitmq消息


    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {

        //1分页
        PageHelper.startPage(page, rows);

        //2过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //2.1搜索字段过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //2.2上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //3默认排序
        example.setOrderByClause("last_update_time DESC");

        List<Spu> spuList = spuMapper.selectByExample(example);

        //出现异常时
        if (CollectionUtils.isEmpty(spuList)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //4解析 分类 和 品牌名称
        loadCategoryAndBrandName(spuList);

        //5解析分页结果
        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);


        return new PageResult<>(pageInfo.getTotal(), spuList);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //处理分类的名称
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/"));
            //处理品牌名称
            spu.setBname(brandService.queryBrandById(spu.getBrandId()).getName());
        }
    }

    /**
     * 新增商品
     *
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {

        //1新增sp     恩恩   2看哦2空瓶
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);

        int count = spuMapper.insert(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

        //2 新增 detail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);

        //3 新增sku
        List<Sku> skus = spu.getSkus();
        List<Stock> stockList = new ArrayList<>();//批量插入
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());


            int counter = skuMapper.insert(sku);
            if (counter != 1) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }

            //4 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stockList.add(stock);

//            int insert = stockMapper.insert(stock);
//
//            if (insert != 1) {
//                throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
//            }

        }

        int i = stockMapper.insertList(stockList);
        if (i != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

        //发送rabbitmq消息
        amqpTemplate.convertAndSend("item.insert", spu.getId());


    }

    /***
     * 根据spuid 查询 详情
     * @param spuid
     * @return
     */
    public SpuDetail queryDetailById(Long spuid) {

        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuid);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.GOODS_DETAIL_ERROR);
        }

        return spuDetail;

    }

    /***
     * 根据 spuid 查询到 所有的sku
     * @param spuid
     * @return
     */

    public List<Sku> querySkuListBySpuId(Long spuid) {
        Sku sku = new Sku();
        sku.setSpuId(spuid);
        List<Sku> skuList = skuMapper.select(sku);

        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }

        //查库存
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
        }
        //将stock变成一个map
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));

        skuList.forEach(s -> s.setStock(stockMap.get(s.getId())));


        return skuList;
    }

    /***
     * 更新商品
     * @param spu
     */
    @Transactional
    public void updateGoods(Spu spu) {

        if (spu.getId() == null) {
            throw new LyException(ExceptionEnum.GOODS_ID_ERROR);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //查询sku
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)) {
            //删除sku
            skuMapper.delete(sku);
            //删除stock
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);

        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //修改detail
        int i = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (i != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //新增 sku 和库存
        //2 新增 detail
//        SpuDetail spuDetail = spu.getSpuDetail();
//        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.updateByPrimaryKey(spu.getSpuDetail());
        //3 新增sku
        List<Sku> skus = spu.getSkus();
        List<Stock> stockList = new ArrayList<>();//批量插入
        for (Sku sku02 : skus) {
            sku02.setCreateTime(new Date());
            sku02.setLastUpdateTime(sku02.getCreateTime());
            sku02.setSpuId(spu.getId());

            int counter = skuMapper.insert(sku02);
            if (counter != 1) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            //4 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku02.getId());
            stock.setStock(sku02.getStock());
            stockList.add(stock);
        }
        int o = stockMapper.insertList(stockList);
//        System.out.println("000:"+o);
//        if (o != 1) {
//            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
//        }

        //发送rabbitmq 消息 告诉搜索微服务和静态页生成的微服务 发生了改变
        amqpTemplate.convertAndSend("item.update", spu.getId());


    }

    /***
     * 根据id 查询 spu
     * @param id
     * @return
     */

    public Spu querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //查询sku
        List<Sku> skus = querySkuListBySpuId(id);
        spu.setSkus(skus);
        //查询详情
        SpuDetail spuDetail = queryDetailById(id);
        spu.setSpuDetail(spuDetail);

        return spu;
    }

    public List<Sku> querySkuByIds(List<Long> ids) {

        List<Sku> skus = skuMapper.selectByIdList(ids);

        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }

        //查库存
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stocks)) {
            throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
        }

        Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));

        //把库存数据给组装过去
        skus.forEach(s -> s.setStock(stockMap.get(s.getId())));

        return skus;
    }

    /***
     * 减库存的操作
     * @param list
     */
    @Transactional
    public void decreaseStock(List<CartDTO> list) {
        for (CartDTO cart : list) {
            int i = stockMapper.decreaseStock(cart.getSkuId(), cart.getNum());
            if (i != 1) {
                throw new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }

        }


    }
}
