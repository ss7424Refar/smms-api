package com.asv.dao;

import com.asv.entity.BorrowHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BorrowHistoryDao extends JpaRepository<BorrowHistory, Long> {

    @Query(value = "select * from t_borrow_history where device_id = ?  and end_time is null", nativeQuery = true)
    BorrowHistory findHistoryByDeviceId(String deviceId);

    List<BorrowHistory> findByDeviceId(String deviceId, Sort sort);

    List<BorrowHistory> findByDeviceIdIn(List<String> deviceIds);

}
