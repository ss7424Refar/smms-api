package com.asv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_mail_queue")
@Data
public class MailQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "flag", columnDefinition = "int(1)")
    private Integer flag;

    @Column(name = "subject")
    private String subject;

    @Column(name = "mail_to")
    private String mailTo;

    @Column(name = "mail_from")
    private String mailFrom;

    @Column(name = "email", columnDefinition = "MEDIUMTEXT")
    private String emailInfo;

}
