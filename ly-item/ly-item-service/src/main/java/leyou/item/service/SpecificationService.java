package leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import leyou.item.mapper.SpecGroupMapper;
import leyou.item.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-21 20:44
 * @desc
 */

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id 查 规格组
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupByCid(long cid) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    /**
     * 根据组gid 查询参数
     *
     * @param gid
     * @return
     */
    public List<SpecParam> queryParamsByGid(Long gid) {

        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        List<SpecParam> paramList = specParamMapper.select(specParam);

        if (CollectionUtils.isEmpty(paramList)) {

            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        return paramList;

    }
}
