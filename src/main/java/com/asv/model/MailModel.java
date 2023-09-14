package com.asv.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailModel {
    private String to;
    private String from;
    private String subject;
    private String content;
}
