package com.asv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "t_virus_history")
public class VirusHistory {
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

    @Column(name = "log_path")
    private String logPath;

    @Column(name = "return_code")
    private String returnCode;

    @Column(name = "code_string")
    private String codeString;

    @Column(name = "upload_date")
    private Date uploadDate;

    @Column(name = "remark")
    private String remark;
}
