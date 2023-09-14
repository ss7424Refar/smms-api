package com.asv.dao;

import com.asv.entity.Depart;
import com.asv.entity.Permission;
import com.asv.model.PermissionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionDao extends JpaRepository<Permission, Long> {

    @Query("SELECT DISTINCT new com.asv.model.PermissionModel(p.code, p.desc) FROM Permission p")
    List<PermissionModel> findDistinctCode();

    List<Permission> findByRoleId(Integer roleId);

    Permission findByRoleIdAndCode(Integer roleId, String code);

}
