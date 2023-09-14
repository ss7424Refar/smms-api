package com.asv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="t_role")
@Data
public class Role {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name")
    private String name;

    @Column(name = "role_desc")
    private String desc;
}
