package leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author hftang
 * @date 2019-01-17 10:38
 * @desc
 */
public interface BrandMapper extends Mapper<Brand> {
    @Insert("insert into tb_category_brand (category_id,brand_id) values (#{cid},${bid})")
    void insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);


}
