package com.asv.dao;

import com.asv.entity.Depart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartDao extends JpaRepository<Depart, Long> {
    Depart findById(Integer id);

    @Query(value = "select * from t_depart d where d.depart_name like %:name% ", nativeQuery = true)
    Page<Depart> selectAllByDepartName(@Param("name") String name, Pageable pageable);


}
