package com.asv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_permission")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer id;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "permission_code", columnDefinition = "varchar(30)")
    private String code;

    @Column(name = "permission_desc", columnDefinition = "varchar(20)")
    private String desc;

}
