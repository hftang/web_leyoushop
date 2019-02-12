package com.leyou.page.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hftang
 * @date 2019-02-11 15:27
 * @desc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    String name;
    int age;
    User friend;
}
