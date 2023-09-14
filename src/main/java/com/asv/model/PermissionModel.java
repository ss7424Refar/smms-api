package com.asv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class PermissionModel {
    private String permissionCode;
    private String permissionDesc;
}
