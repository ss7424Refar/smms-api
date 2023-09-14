package com.asv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_depart")
public class Depart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "depart_id")
    private Integer id;

    @Column(name = "depart_name")
    private String name;

}
