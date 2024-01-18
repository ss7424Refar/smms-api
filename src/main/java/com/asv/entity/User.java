package com.asv.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@Table(name = "t_user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_no", nullable = false, columnDefinition = "varchar(8)")
    private String userNo;

    @Column(name = "user_name", columnDefinition = "varchar(8)")
    private String userName;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "password")
    private String password;

    @Email(message = "邮箱格式不正确")
    @Column(name = "mail", unique = true)
    private String mail;

//    @Column(name = "sex", columnDefinition = "int(1) default 0")
    @Column(name = "sex", columnDefinition = "varchar(1) default 1")
    private String sex;

    @Column(name = "depart_id")
    private Integer departId;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "duties")
    private Integer duties;

    @Column(name = "icons")
    private String icons;

    @Column(name = "status", columnDefinition = "varchar(2) default '在职'")
    private String status;

    @Column(name = "using_line", columnDefinition = "varchar(8)")
    private String usingLine;

    @Column(name = "user_post", columnDefinition = "varchar(8)")
    private String userPost;

}
