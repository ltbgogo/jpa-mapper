package org.github.jpaMapper.test;

import lombok.extern.log4j.Log4j;
import org.github.jpaMapper.test.book.BookMapper;
import org.github.jpaMapper.test.book.BookRepo;
import org.github.jpaMapper.test.user.UserMapper;
import org.github.jpaMapper.test.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

@Log4j
@Service
public class SecondService {

    @Autowired
    private FirstService firstService;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void doJtaRepo() {
        this.printRepo();
        try {
            System.out.println();
            bookRepo.findAll((root, query, cb) -> {
                return cb.and(new Predicate[]{});
            });
            firstService.doJtaRepo();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        this.printRepo();
    }

    @Transactional
    public void doJtaMapper() {
        this.printMapper();
        try {
            firstService.doJtaMapper();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        this.printMapper();
    }

    private void printRepo() {
        System.out.println("book ************* " + bookRepo.findAll());
        System.out.println("user ************* " + userRepo.findAll());
    }

    private void printMapper() {
        System.out.println("book ************* " + bookMapper.findAllBook());
        System.out.println("user ************* " + userMapper.findAllUser());
    }
}
