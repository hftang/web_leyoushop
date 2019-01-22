package leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author hftang
 * @date 2019-01-16 14:17
 * @desc
 *  IdListMapper<Category,Long>
 *      实体类  主键类型
 */
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {

}
