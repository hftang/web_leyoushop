package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author hftang
 * @date 2019-01-22 13:49
 * @desc 查商品用
 */
@Table(name = "tb_spu")
@Data
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1; //一级类目
    private Long cid2;//二级类目
    private Long cid3;//三级类目

    private String title;//标题
    private String subTitle;//子标题

    @JsonIgnore
    private Boolean saleable;//是否上架
    private Boolean valid;//是否删除 逻辑删除
    private Date createTime;//创建时间
    //不想要的字段 可以用  @JsonIgnore 注解 忽略此字段
    @JsonIgnore
    private Date lastUpdateTime;//最后修改时间

    //下面是添加的额外字段 不是数据库表中的字段 所有要过滤掉 transient 瞬态
    @Transient
    private String cname;
    @Transient
    private String bname;


}
