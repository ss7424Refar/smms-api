package com.asv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "t_device")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id", columnDefinition = "varchar(11)")
    private String deviceId;

    @Column(name = "device_name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "device_status", columnDefinition = "int(1)")
    private Integer status;

    @Column(name = "capacity", columnDefinition = "varchar(8)")
    private String capacity;

    @Column(name = "program", columnDefinition = "varchar(8)")
    private String program;

    @Column(name = "depart_id")
    private Integer departId;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "pnp_device")
    private String pnDevice;

    @Column(name = "virus", columnDefinition = "int(1)")
    private Integer virus;

    @Column(name = "virus_date")
    private Date virusDate;

    @Column(name = "user_id", columnDefinition = "varchar(8)")
    private String currentUserId;

    @Column(name = "user_name", columnDefinition = "varchar(8)")
    private String currentUserName;

    @Column(name = "using_line", columnDefinition = "varchar(8)")
    private String usingLine;

    @Column(name = "using_post", columnDefinition = "varchar(8)")
    private String usingPost;

    @Column(name = "store_date")
    private Date storeDate;

    @Column(name = "store_user_id")
    private int storeUserId;
    
    @Column(name = "borrow_date")
    private Date borrowDate;

    @Column(name = "scrap_date")
    private Date scrapDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "remark")
    private String remark;

}
