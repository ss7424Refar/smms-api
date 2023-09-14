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

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "device_status")
    private Integer status;

    @Column(name = "capacity")
    private String capacity;

    @Column(name = "program")
    private String program;

    @Column(name = "depart_id")
    private Integer departId;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "pnp_device")
    private String pnDevice;

    @Column(name = "virus")
    private Integer virus;

    @Column(name = "virus_date")
    private Date virusDate;

    @Column(name = "user_id")
    private String currentUserId;

    @Column(name = "user_name")
    private String currentUserName;

    @Column(name = "using_line")
    private String usingLine;

    @Column(name = "using_post")
    private String usingPost;

    @Column(name = "store_date")
    private Date storeDate;

    @Column(name = "store_user_id")
    private int storeUserId;

    @Column(name = "borrow_date")
    private Date borrowDate;

    @Column(name = "scrap_date")
    private Date scrapDate;

    @Column(name = "remark")
    private String remark;
}
