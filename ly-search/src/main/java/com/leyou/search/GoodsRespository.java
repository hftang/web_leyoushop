package com.leyou.search;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author hftang
 * @date 2019-01-25 14:08
 * @desc
 */
public interface GoodsRespository extends ElasticsearchRepository<Goods,Long> {

}
