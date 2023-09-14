package com.asv.dao;

import com.asv.entity.Depart;
import com.asv.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionDao extends JpaRepository<Section, Long> {

    @Query(value = "select * from t_section where department_id = :id", nativeQuery = true)
    List<Section> findAllByDepartId(@Param("id") Integer id);

    Section findById(Integer id);
}
