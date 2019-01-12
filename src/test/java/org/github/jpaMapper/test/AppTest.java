package org.github.jpaMapper.test;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.github.jpaMapper.test.book.Book;
import org.github.jpaMapper.test.book.BookMapper;
import org.github.jpaMapper.test.book.BookRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(classes = AppStarter.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AppTest {

    @Autowired
    private SecondService secondService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookRepo bookRepo;

    @Test
    public void testJtaRepo() throws InterruptedException {
        secondService.doJtaRepo();
    }

    @Test
    public void testJtaMapper() {
        secondService.doJtaMapper();
    }

    @Test
    public void testMapperPage() {
        bookRepo.saveAndFlush(new Book("book1", "11"));
        bookRepo.saveAndFlush(new Book("book1", "11"));
        bookRepo.saveAndFlush(new Book("book1", "11"));
        System.out.println(JSON.toJSON(bookMapper.findByAnno()));
//        System.out.println(JSON.toJSONString(bookMapper.findAllBook(new PageRequest(0, 1)), true));
    }
}
