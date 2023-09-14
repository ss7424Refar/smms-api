package com.asv.dao;

import com.asv.entity.Role;
import com.asv.entity.User;
import com.asv.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleDao extends JpaRepository<Role, Long> {
    Role findByName(String roleName);

    Role findById(Integer id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Role r WHERE r.id IN :ids")
    void deleteByIds(@Param("ids") List<Integer> ids);

}


