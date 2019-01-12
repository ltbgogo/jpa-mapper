package org.github.jpaMapper.test.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "t_user")
public class User {

    @Id
    private String id;
    @Column(name = "user_name")
    private String userName;
}