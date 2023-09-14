package com.asv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_post")
public class Post {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_name")
    private String name;

}
