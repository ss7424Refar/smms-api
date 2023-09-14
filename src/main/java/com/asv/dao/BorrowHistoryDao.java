package com.asv.dao;

import com.asv.entity.BorrowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BorrowHistoryDao extends JpaRepository<BorrowHistory, Long> {

    @Query(value = "select * from t_borrow_history where device_id = ?  and end_time is null", nativeQuery = true)
    BorrowHistory findByDeviceId(String deviceId);

}
