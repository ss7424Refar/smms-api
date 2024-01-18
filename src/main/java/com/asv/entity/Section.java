package com.asv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="t_section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Integer id;

    @Column(name = "department_id")
    private Integer departId;

    @Column(name = "section_name", columnDefinition = "varchar(8)")
    private String name;

}
