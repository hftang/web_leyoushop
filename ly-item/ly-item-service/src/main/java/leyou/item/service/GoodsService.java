package leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Spu;
import leyou.item.mapper.SpuDetailMapper;
import leyou.item.mapper.SpuMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
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
}
