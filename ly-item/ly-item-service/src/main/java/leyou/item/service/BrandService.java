package leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import leyou.item.mapper.BrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-17 10:39
 * @desc
 */
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;


    public PageResult<Brand> queryBrandPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {

        //分页
        PageHelper.startPage(page, rows);
        //排序
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + (desc ? " DESC" : " ASC"));

        }
        //查询
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%").orEqualTo("letter", key.toUpperCase());
        }

        List<Brand> list = this.brandMapper.selectByExample(example);

        //获取总条数信息
        PageInfo<Brand> pageInfo = new PageInfo<>(list);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 保存brand
     *
     * @param brand
     * @param ids
     */

    //添加事物
    @Transactional
    public void save(Brand brand, List<Long> ids) {
        //新增品牌 插入成功后就有 bid
        this.brandMapper.insert(brand);
        //新增品牌和分类的中间表
        for (Long cid : ids) {
            this.brandMapper.insertCategoryBrand(cid, brand.getId());
        }

    }

    /**
     * 根据id 查品牌
     *
     * @param id
     * @return
     */

    public Brand queryBrandById(Long id) {
        Brand brand = this.brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        return brand;

    }

    /***
     * 根据 分类id 查询 品牌
     * @param cid
     * @return
     */

    public List<Brand> queryBrandByCid(Long cid) {

        List<Brand> brands = brandMapper.queryByCategoryByCid(cid);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        return brands;
    }
}
