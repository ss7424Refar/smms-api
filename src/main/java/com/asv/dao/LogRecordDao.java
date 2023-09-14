package com.asv.dao;

import com.asv.entity.LogRecord;
import com.asv.model.LogRecordModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogRecordDao extends JpaRepository<LogRecord, Long> {
    List<LogRecord> findByBusinessKey(String businessKey);

    @Query("SELECT new com.asv.model.LogRecordModel(businessKey, logType, operatorName, operatorDate, remark) " +
            "From LogRecord WHERE deviceId = :deviceId")
    List<LogRecordModel> findLogDetail(Sort sort, String deviceId);

    @Query("SELECT new com.asv.model.LogRecordModel(deviceId, deviceName, logType, operatorName, operatorDate, remark) " +
            "From LogRecord WHERE businessKey = :businessKey")
    List<LogRecordModel> findBusinessDetail(Sort sort, String businessKey);
}
