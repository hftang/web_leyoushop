package leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-17 10:38
 * @desc
 */
public interface BrandMapper extends BaseMapper<Brand> {

    @Insert("insert into tb_category_brand (category_id,brand_id) values (#{cid},${bid})")
    void insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("select b.* from tb_category_brand cb inner join tb_brand b on b.id = cb.brand_id where cb.category_id = #{cid}")
    List<Brand> queryByCategoryByCid(@Param("cid") Long cid);


}
