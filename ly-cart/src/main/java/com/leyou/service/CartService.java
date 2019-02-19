package com.leyou.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.filter.UserIntercepter;
import com.leyou.pojo.Cart;
import com.leyou.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author hftang
 * @date 2019-02-19 17:33
 * @desc
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final static String KEY_PREFIX = "cart:uid:";


    /***
     * 添加购物车
     * @param cart
     */
    public void addCart(Cart cart) {
        //获取当前用户
        UserInfo userInfo = UserIntercepter.getUserInfo();

        //key
        String key = KEY_PREFIX + userInfo.getId();
        String hashKey = cart.getSkuId().toString();

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        //判断 当前购物车是否存在
        if (operations.hasKey(hashKey)) {
            // 存在 数量累加
            String json = operations.get(hashKey).toString();
            Cart cacheCart = JsonUtils.parse(json, Cart.class);
            cacheCart.setNum(cacheCart.getNum() + cart.getNum());
            //再写回到redis
            operations.put(hashKey, JsonUtils.serialize(cacheCart));

        } else {
            //不存在 新添加购物车
            operations.put(hashKey, JsonUtils.serialize(cart));
        }


    }
}
