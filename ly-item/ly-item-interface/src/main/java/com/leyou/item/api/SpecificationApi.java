package com.leyou.item.api;

import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-25 13:55
 * @desc
 */
public interface SpecificationApi {

    @GetMapping("/spec/params")
    List<SpecParam> queryParamByList(@RequestParam(value = "gid", required = false) Long gid,
                                     @RequestParam(value = "cid", required = false) Long cid,
                                     @RequestParam(value = "searching", required = false) Boolean searching);


}