package com.leyou.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.filter.UserIntercepter;
import com.leyou.pojo.Cart;
import com.leyou.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hftang
 * @date 2019-02-19 17:33
 * @desc
 */
@Slf4j
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

            log.info("[cartservice] 添加商品个数累加 hashKey：" + hashKey);

        } else {
            //不存在 新添加购物车
            operations.put(hashKey, JsonUtils.serialize(cart));
            log.info("[cartservice] 添加商品新产品 hashKey：" + hashKey);
        }


    }

    public List<Cart> queryCartList() {

        //获取用户
        UserInfo userInfo = UserIntercepter.getUserInfo();

        String key = KEY_PREFIX + userInfo.getId();
        //判断 是否存在
        if (!this.redisTemplate.hasKey(key)) {
            return null;
        }

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        //获取所有的值
        List<Object> carts = hashOps.values();

        if (CollectionUtils.isEmpty(carts)) {
            return null;
        }

        List<Cart> list = carts.stream().map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());

        return list;
    }

    /**
     * 更新 购物车 中商品的数量 在 redis中
     *
     * @param skuId
     * @param num
     */

    public void updateCartNum(Long skuId, Integer num) {

        //获取用户
        UserInfo userInfo = UserIntercepter.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        //判断是否存在
        if (!redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }

        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        //获取购物车 根据 skuid
        Object o = hashOps.get(skuId.toString());
        System.out.println("[updateCartNum]:" + o);
        String json = o.toString();


        Cart cart = JsonUtils.parse(json, Cart.class);
        //修改num
        cart.setNum(num);
        hashOps.put(skuId.toString(), JsonUtils.serialize(cart));


    }

    /***
     * 根据skuid 删除对应的 cart 购物车
     * @param skuId
     */
    public void deleteCartByskuId(Long skuId) {
        //获取用户
        UserInfo userInfo = UserIntercepter.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        if (!this.redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }

        BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate.boundHashOps(key);
        boundHashOps.delete(skuId.toString());

    }
}
