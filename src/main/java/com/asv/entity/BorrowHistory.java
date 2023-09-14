package com.asv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "t_borrow_history")
public class BorrowHistory {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "user_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "using_line")
    private String usingLine;

    @Column(name = "user_post")
    private String userPost;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "remark")
    private String remark;
}
