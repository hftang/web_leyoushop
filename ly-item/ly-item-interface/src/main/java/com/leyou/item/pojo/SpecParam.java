package com.leyou.item.pojo;

import com.sun.xml.internal.fastinfoset.algorithm.BooleanEncodingAlgorithm;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author hftang
 * @date 2019-01-22 9:35
 * @desc
 */
@Table(name = "tb_spec_param")
@Data
public class SpecParam {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;

    @Column(name = "`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String  segments;


}
