package org.github.jpaMapper.test.book;

import javax.persistence.*;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "t_book")
public class Book {

    @Id
    private String id;
    @Column(name = "book_name")
    private String bookName;
}