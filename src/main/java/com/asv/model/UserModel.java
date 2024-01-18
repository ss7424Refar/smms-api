package com.asv.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)
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

    private String usingLine;
    private String userPost;

    public UserModel(Integer userId, String mail, String sex, String status, String userName, String userNo,
                     Integer departId, Integer sectionId, Integer roleId, Integer duties, String usingLine, String userPost) {
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
        this.usingLine = usingLine;
        this.userPost = userPost;
    }

    public UserModel(Integer userId, String departName, String postName, String icons, String mail, String roleName,
                     String sectionName, String sex, String status, String userName, String userNo, String usingLine, String userPost) {
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
        this.usingLine = usingLine;
        this.userPost = userPost;
    }
}
