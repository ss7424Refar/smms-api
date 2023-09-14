package com.asv.dao;

import com.asv.entity.Depart;
import com.asv.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostDao extends JpaRepository<Post, Long> {
//    Depart findById(Integer id);
//
//    @Query(value = "select * from t_depart d where d.depart_name like %:name% ", nativeQuery = true)
//    Page<Depart> selectAllByDepartName(@Param("name") String name, Pageable pageable);


}
