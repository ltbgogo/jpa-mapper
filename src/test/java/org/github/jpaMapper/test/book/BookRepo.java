package org.github.jpaMapper.test.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepo extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {

}