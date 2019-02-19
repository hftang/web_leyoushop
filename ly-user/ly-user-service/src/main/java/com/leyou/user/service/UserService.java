package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.utils.CodecUtils;
import com.leyou.utils.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hftang
 * @date 2019-02-15 10:48
 * @desc
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate rabbitMessagingTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    private String KEY_PREFIX = "user:verify:phone";

    /**
     * 校验数据是否存在
     *
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {

        User record = new User();
        record.setUsername(data);
        switch (type) {
            case 1: //用户姓名
                record.setUsername(data);
                break;
            case 2: //用户电话
                record.setPhone(data);
                break;
            default:
                return null;
        }

        log.info("[usermapper] data:" + data + "type:" + type);

        return this.userMapper.selectCount(record) == 0;


    }

    /**
     * 1、设置好短信模板码
     * 2、生成随机的6位数字 内容
     * 3、使用rabbitmq send给 SMS service
     *
     * @param phone
     */

    public void sendVerifyCode(String phone) {

        log.info("[userservice] phone:" + phone);

        String key = KEY_PREFIX + phone;
        String code = NumberUtils.generateCode(6);

        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        rabbitMessagingTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code", msg);

        //保存 验证码 五分钟有效
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        log.info("[短信已发出：] code：" + code);
    }

    public Boolean regist(User user, String code) {
        //1、先校验短信验证码对不对
        String key = KEY_PREFIX + user.getPhone();
        String cacheCode = redisTemplate.opsForValue().get(key);

        if (!StringUtils.equals(code, cacheCode)) {
            throw new LyException(ExceptionEnum.VERIFY_CODE_NOT_MATCHING);
        }

        user.setId(null);
        user.setCreated(new Date());

        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        Boolean boo = this.userMapper.insertSelective(user) == 1;
        if (boo) {
            try {

                this.redisTemplate.delete(key);
                log.info("[regist]:注册成功！");
            } catch (Exception e) {
                log.info("删除缓存的code验证码失败 code ：{}", code, e);
                log.info("[regist]:注册失败！");
            }
        }


        return boo;
    }

    /**
     * 查询 用户
     *
     * @param username
     * @param password
     * @return
     */

    public User query(String username, String password) {

        /**
         * 因为用户名使用了索引 所以通过用户名来查询 而不是 同时通过 用户名密码来查询
         */
        User record = new User();
        record.setUsername(username);

        User user = userMapper.selectOne(record);

        if (user == null) {
            throw new LyException(ExceptionEnum.PASSWORD_NOT_MATCHING);
        }

        log.info("password:::"+password);
        String hex_pwd = CodecUtils.md5Hex(password, user.getSalt());

        log.info("user:password:" + user.getPassword() + "hex_pwd:" + hex_pwd);

        if (!StringUtils.equals(user.getPassword(), hex_pwd)) {
            throw new LyException(ExceptionEnum.PASSWORD_NOT_MATCHING);
        }

        return user;

    }
}
