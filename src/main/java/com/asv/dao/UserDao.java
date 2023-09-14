package com.asv.dao;

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

//@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User findByMail(String mail);

    User findByUserId(Integer userId);

    User findByUserNo(String userNo);

    List<User> findByRoleId(Integer roleId);
    // Integer userId, String mail, String sex, String status, String userName, String userNo, Integer departId, Integer sectionId, Integer roleId, Integer duties
    @Query(value = "SELECT new com.asv.model.UserModel(t.userId, t.mail, t.sex, t.status, t.userName, t.userNo, t.departId, t.sectionId, " +
            "t.roleId, t.duties) FROM User t where t.userId = :userId")
    UserModel getUserUpdateInfo(Integer userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.userId IN :ids")
    void deleteByIds(@Param("ids") List<Integer> ids);


    // 使用的是JPQL
    @Query(value = "SELECT new com.asv.model.UserModel(t.userId, td.name, tp.name, t.icons, t.mail, " +
            "tr.name, ts.name, t.sex, t.status, t.userName, t.userNo) " +
            "FROM User t " +
            "LEFT JOIN Depart td ON td.id = t.departId " +
            "LEFT JOIN Section ts ON ts.id = t.sectionId " +
            "LEFT JOIN Role tr ON tr.id = t.roleId " +
            "LEFT JOIN Post tp ON tp.id = t.duties " +
            "WHERE (:name IS NOT NULL AND t.userName LIKE CONCAT('%', :name, '%') OR :name IS NULL) " +
            "AND (:userNo IS NOT NULL AND t.userNo = :userNo OR :userNo IS NULL) " +
            "AND (:status IS NOT NULL AND t.status = :status OR :status IS NULL) " +
            "AND (:departId IS NOT NULL AND t.departId = :departId OR :departId IS NULL) " +
            "AND (:sectionId IS NOT NULL AND t.sectionId = :sectionId OR :sectionId IS NULL) " +
            "AND (:mail IS NOT NULL AND t.mail LIKE CONCAT('%', :mail, '%') OR :mail IS NULL) " +
            "ORDER BY t.userId"
    )
    Page<UserModel> searchUsersByKeyword(@Param("userNo") String userNo, @Param("name") String name, @Param("status") String status,
                                         @Param("departId") Integer departId, @Param("sectionId") Integer sectionId,
                                         @Param("mail") String mail, Pageable pageable);
//    Page<UserModel> searchUsersByKeyword(@Param("userNo") String userNo, @Param("name") String name, Pageable pageable);

}
