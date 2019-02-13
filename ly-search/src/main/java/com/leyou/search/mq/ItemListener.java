package com.leyou.search.mq;

import com.leyou.search.service.SearchService;
import com.rabbitmq.http.client.domain.ExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hftang
 * @date 2019-02-13 10:27
 * @desc 接受mq消息的类
 * <p>
 * 1，因为新增和修改的逻辑一样的
 * 2，删除的逻辑
 * 这里面就这两个逻辑
 */
@Slf4j
@Component
public class ItemListener {
    @Autowired
    private SearchService searchService;

    /***
     * 监听添加或者修改
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "search.item.insert.queue", durable = "true"),
                    exchange = @Exchange(name = "leyou.item.exchange", type = ExchangeTypes.TOPIC),
                    key = {"item.update", "item.insert"}
            )
    )
    public void listenInsertOrUpdate(Long spuId) {
        if (spuId == null) {
            return;
        }
        log.info("search 接受到消息：" + spuId);
        //处理接受到的消息
        searchService.createOrUpdate(spuId);

    }

    /**
     * 监听删除
     *
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "search.item.delete.queue", durable = "true"),
                    exchange = @Exchange(name = "leyou.item.exchange", type = ExchangeTypes.TOPIC),
                    key = {"item.delete"}
            )
    )
    public void listenDelete(Long spuId) {
        if (spuId == null) {
            return;
        }
        log.info("search接受到删除的消息：" + spuId);

        //处理消息
        searchService.deleteIndex(spuId);

    }


}
