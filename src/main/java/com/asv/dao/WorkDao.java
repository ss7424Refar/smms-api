package com.asv.dao;


import com.asv.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WorkDao extends JpaRepository<Work, Long> {
    Work findByName(String name);

    Work findById(Integer id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Work w WHERE w.id IN :ids")
    void deleteByIds(@Param("ids") List<Integer> ids);

}
