package com.asv.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel {
    private Integer userId;
    private String departName;
    private String postName;
    private String icons;
    private String mail;
    private String roleName;
    private String sectionName;
    private String sex;
    private String status;
    private String userName;
    private String userNo;

    private Integer departId;
    private Integer sectionId;
    private Integer roleId;
    private Integer duties;

    public UserModel(Integer userId, String mail, String sex, String status, String userName, String userNo, Integer departId, Integer sectionId, Integer roleId, Integer duties) {
        this.userId = userId;
        this.mail = mail;
        this.sex = sex;
        this.status = status;
        this.userName = userName;
        this.userNo = userNo;
        this.departId = departId;
        this.sectionId = sectionId;
        this.roleId = roleId;
        this.duties = duties;
    }

    public UserModel(Integer userId, String departName, String postName, String icons, String mail, String roleName, String sectionName, String sex, String status, String userName, String userNo) {
        this.userId = userId;
        this.departName = departName;
        this.postName = postName;
        this.icons = icons;
        this.mail = mail;
        this.roleName = roleName;
        this.sectionName = sectionName;
        this.sex = sex;
        this.status = status;
        this.userName = userName;
        this.userNo = userNo;
    }
}
