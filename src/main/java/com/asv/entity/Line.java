package com.asv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_line")
public class Line {
    @Id
    @Column(name = "line_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "line_name", columnDefinition = "varchar(8)")
    private String name;

}
