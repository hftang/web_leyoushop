package leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hftang
 * @date 2019-01-21 20:45
 * @desc
 */


@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") long cid) {


        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 根据组id 查询 参数
     *
     * @param gid
     * @return
     */

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByList(@RequestParam(value = "gid", required = false) Long gid,
                                                            @RequestParam(value = "cid", required = false) Long cid,
                                                            @RequestParam(value = "searching", required = false) Boolean searching) {


        return ResponseEntity.ok(specificationService.queryParamsByList(gid, cid, searching));

    }

    /**
     * 根据分类id 查询规格组以及组内参数
     * @param cid
     * @return
     */

    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@RequestParam("cid") Long cid){

        return ResponseEntity.ok(specificationService.queryListByCid(cid));
    }

}
