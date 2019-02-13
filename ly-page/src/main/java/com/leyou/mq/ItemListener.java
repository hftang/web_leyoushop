package com.leyou.mq;

import com.leyou.service.PageService;
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
 * @date 2019-02-13 11:25
 * @desc 接受mq消息 然后做相应的处理
 */
@Slf4j
@Component
public class ItemListener {

    @Autowired
    private PageService pageService;

    /**
     * 监听增加或者修改的操作
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "page.item.insert.queue",durable = "true"),
                    exchange = @Exchange(name = "leyou.item.exchange",type = ExchangeTypes.TOPIC),
                    key = {"item.update","item.insert"}
            )
    )
    public void listenInsertOrUpdate(Long spuId){

        if(spuId==null){
            return;
        }
        log.info("pageservice接受到消息："+spuId);

        pageService.createHtml(spuId);


    }

    /***
     * 监听删除的操作
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "page.item.delete.queue",durable = "true"),
                    exchange = @Exchange(name = "leyou.item.exchange",type = ExchangeTypes.TOPIC),
                    key = {"item.delete"}
            )
    )
    public void listenDelete(Long spuId){
        pageService.deleteHtml(spuId);
    }


}
