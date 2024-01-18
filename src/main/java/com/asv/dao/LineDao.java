package com.asv.dao;

import com.asv.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LineDao extends JpaRepository<Line, Long> {
    Line findByName(String name);

    Line findById(Integer id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Line l WHERE l.id IN :ids")
    void deleteByIds(@Param("ids") List<Integer> ids);
}
