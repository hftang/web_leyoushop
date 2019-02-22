package leyou.item.service;

import com.leyou.common.dto.CartDTO;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author hftang
 * @date 2019-02-21 13:38
 * @desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceTest {

    @Autowired
    private GoodsService goodsService;



    @org.junit.Test
    public void decreaseStock() {
        List<CartDTO> dtoList = Arrays.asList(new CartDTO(2600242L, 2), new CartDTO(2600248L, 2));
        goodsService.decreaseStock(dtoList);
    }
}