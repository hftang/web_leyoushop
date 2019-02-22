package leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.additional.insert.InsertListMapper;

/**
 * @author hftang
 * @date 2019-01-22 21:50
 * @desc
 */
public interface StockMapper extends BaseMapper<Stock> {

    //减库存

    @Update("update tb_stock set stock = stock - #{num} where sku_id = #{id} and stock >= #{num}")
    int decreaseStock(@RequestParam("id") Long id, @RequestParam("num") Integer num);

}
