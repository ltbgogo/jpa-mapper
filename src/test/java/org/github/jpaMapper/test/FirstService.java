package org.github.jpaMapper.test;

import org.github.jpaMapper.test.book.Book;
import org.github.jpaMapper.test.book.BookMapper;
import org.github.jpaMapper.test.book.BookRepo;
import org.github.jpaMapper.test.user.User;
import org.github.jpaMapper.test.user.UserMapper;
import org.github.jpaMapper.test.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;

@Service
public class FirstService {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doJtaRepo() {
        System.out.println();
        bookRepo.findAll((root, query, cb) -> {
            return cb.and(new Predicate[]{});
        });
        bookRepo.saveAndFlush(new Book("book1", "11"));
        userRepo.saveAndFlush(new User("user1", "11"));
//        throw new TestException();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doJtaMapper() {
        bookMapper.saveBook("bookMapper", "bookMapper");
        userMapper.saveUser("userMapper", "userMapper");
//        throw new TestException();
    }
}
