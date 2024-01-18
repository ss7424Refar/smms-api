package com.asv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_work")
public class Work {
    @Id
    @Column(name = "work_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "work_name", columnDefinition = "varchar(8)")
    private String name;

}
