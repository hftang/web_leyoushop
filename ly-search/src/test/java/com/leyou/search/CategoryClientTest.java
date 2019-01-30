package com.leyou.search;

import com.leyou.item.pojo.Category;
import com.leyou.search.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author hftang
 * @date 2019-01-25 10:22
 * @desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testCategoryClient() {
        List<Category> categories = categoryClient.queryCategorysByIds(Arrays.asList(1L, 2L, 3L));
        for (Category category : categories) {
            System.out.println(category);

        }


    }

}