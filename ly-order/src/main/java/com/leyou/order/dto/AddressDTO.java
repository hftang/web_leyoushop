package com.leyou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hftang
 * @date 2019-02-20 20:38
 * @desc
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    private Long id;

    /**
     * 收货人
     */
    private String name;

    /**
     * 收货电话
     */
    private String phone;

    /**
     * 邮编
     */
    private String zipCode;

    /**
     * 省
     */
    private String state;

    /**
     * 市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    private Boolean isDefault;

    /**
     * 是否是默认地址
     */
//    private Boolean defaultAddress;
//
//    /**
//     * 地址标签
//     */
//    private String label;
}
