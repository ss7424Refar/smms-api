package com.asv.dao;

import com.asv.entity.Device;
import com.asv.model.DeviceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceDao extends JpaRepository<Device, Long> {

    Device findByDeviceId(String deviceId);
    Device findByPnDevice(String pnDevice);
    Device findByName(String name);

    @Query(value = "SELECT new com.asv.model.DeviceModel(t.deviceId, t.name, t.capacity, t.program, t.departId, t.sectionId, t.remark)" +
            " FROM Device t WHERE t.deviceId = :deviceId")
    DeviceModel getDeviceUpdateInfo(String deviceId);

    @Query(value = "SELECT new com.asv.model.DeviceModel(t.deviceId, t.name, t.status, t.capacity, t.program, " +
            "t.virus, t.virusDate, t.currentUserName, td.name, ts.name, t.borrowDate, t.remark, t.usingLine, t.usingPost, " +
            "t.pnDevice, t.storeDate, t.type, t.scrapDate) " +
            "FROM Device t " +
            "LEFT JOIN Depart td ON td.id = t.departId " +
            "LEFT JOIN Section ts ON ts.id = t.sectionId " +
            "WHERE t.deviceId = :deviceId")
    DeviceModel getDeviceDetail(String deviceId);

    @Query("SELECT MAX(d.id) FROM Device d")
    Long findMaxId();

    Device findByStatusAndDeviceId(Integer status, String deviceId);

    @Query(value = "SELECT new com.asv.model.DeviceModel(t.deviceId, t.name, t.status, t.capacity, t.program, " +
            "t.virus, t.virusDate, t.currentUserName, td.name, ts.name, t.borrowDate, t.remark, t.usingLine, t.usingPost, " +
            "t.pnDevice, t.storeDate, t.type, t.scrapDate) " +
            "FROM Device t " +
            "LEFT JOIN Depart td ON td.id = t.departId " +
            "LEFT JOIN Section ts ON ts.id = t.sectionId " +
            "WHERE (:deviceId IS NOT NULL AND t.deviceId LIKE CONCAT('%', :deviceId, '%') OR :deviceId IS NULL) " +
            "AND (:deviceName IS NOT NULL AND t.name LIKE CONCAT('%', :deviceName, '%') OR :deviceName IS NULL) " +
            "AND (:status IS NOT NULL AND t.status = :status OR :status IS NULL) " +
            "AND (:capacity IS NOT NULL AND t.capacity LIKE CONCAT('%', :capacity, '%') OR :capacity IS NULL) " +
            "AND (:type IS NOT NULL AND t.type LIKE CONCAT('%', :type, '%') OR :type IS NULL) " +
            "AND (:program IS NOT NULL AND t.program LIKE CONCAT('%', :program, '%') OR :program IS NULL) " +
            "AND (:departId IS NOT NULL AND t.departId = :departId OR :departId IS NULL) " +
            "AND (:sectionId IS NOT NULL AND t.sectionId = :sectionId OR :sectionId IS NULL) " +
            "AND (:virus IS NOT NULL AND t.virus = :virus OR :virus IS NULL) " +
            "AND (:currentUserName IS NOT NULL AND t.currentUserName LIKE CONCAT('%', :currentUserName, '%') OR :currentUserName IS NULL) " +
            "AND (:usingLine IS NOT NULL AND t.usingLine LIKE CONCAT('%', :usingLine, '%') OR :usingLine IS NULL) " +
            "AND (:remark IS NOT NULL AND t.remark LIKE CONCAT('%', :remark, '%') OR :remark IS NULL) "
    )
    Page<DeviceModel> searchDevicesByKeyword(@Param("deviceId") String deviceId,
                                             @Param("deviceName") String deviceName,
                                             @Param("status") Integer status,
                                             @Param("capacity") String capacity,
                                             @Param("type") String type,
                                             @Param("program") String program,
                                             @Param("departId") Integer departId,
                                             @Param("sectionId") Integer sectionId,
                                             @Param("virus") Integer virus,
                                             @Param("currentUserName") String currentUserName,
                                             @Param("usingLine") String usingLine,
                                             @Param("remark") String remark,
                                             Pageable pageable);


    @Query(value = "SELECT new com.asv.model.DeviceModel(t.deviceId, t.name, t.status, t.capacity, t.program, " +
            "t.virus, t.virusDate, t.currentUserName, td.name, ts.name, t.borrowDate, t.remark, t.usingLine, t.usingPost, " +
            "t.pnDevice, t.storeDate, t.type, t.scrapDate) " +
            "FROM Device t " +
            "LEFT JOIN Depart td ON td.id = t.departId " +
            "LEFT JOIN Section ts ON ts.id = t.sectionId " +
            "WHERE t.deviceId IN :deviceIds")
    List<DeviceModel> findInDeviceIds(List<String> deviceIds);


    @Query(value = "SELECT new com.asv.model.DeviceModel(t.deviceId, t.name, t.status, t.capacity, t.program, " +
            "t.virus, t.virusDate, t.currentUserName, td.name, ts.name, t.borrowDate, t.remark, t.usingLine, t.usingPost, " +
            "t.pnDevice, t.storeDate, t.type, t.scrapDate) " +
            "FROM Device t " +
            "LEFT JOIN Depart td ON td.id = t.departId " +
            "LEFT JOIN Section ts ON ts.id = t.sectionId " +
            "WHERE (:deviceId IS NOT NULL AND t.deviceId LIKE CONCAT('%', :deviceId, '%') OR :deviceId IS NULL) " +
            "AND (:deviceName IS NOT NULL AND t.name LIKE CONCAT('%', :deviceName, '%') OR :deviceName IS NULL) " +
            "AND (:status IS NOT NULL AND t.status = :status OR :status IS NULL) " +
            "AND (:capacity IS NOT NULL AND t.capacity LIKE CONCAT('%', :capacity, '%') OR :capacity IS NULL) " +
            "AND (:type IS NOT NULL AND t.type LIKE CONCAT('%', :type, '%') OR :type IS NULL) " +
            "AND (:program IS NOT NULL AND t.program LIKE CONCAT('%', :program, '%') OR :program IS NULL) " +
            "AND (:departId IS NOT NULL AND t.departId = :departId OR :departId IS NULL) " +
            "AND (:sectionId IS NOT NULL AND t.sectionId = :sectionId OR :sectionId IS NULL) " +
            "AND (:virus IS NOT NULL AND t.virus = :virus OR :virus IS NULL) " +
            "AND (:currentUserName IS NOT NULL AND t.currentUserName LIKE CONCAT('%', :currentUserName, '%') OR :currentUserName IS NULL) " +
            "AND (:usingLine IS NOT NULL AND t.usingLine LIKE CONCAT('%', :usingLine, '%') OR :usingLine IS NULL) " +
            "AND (:remark IS NOT NULL AND t.remark LIKE CONCAT('%', :remark, '%') OR :remark IS NULL) " +
            "ORDER BY t.storeDate desc"
    )
    List<DeviceModel> exportByForm(@Param("deviceId") String deviceId,
                                             @Param("deviceName") String deviceName,
                                             @Param("status") Integer status,
                                             @Param("capacity") String capacity,
                                             @Param("type") String type,
                                             @Param("program") String program,
                                             @Param("departId") Integer departId,
                                             @Param("sectionId") Integer sectionId,
                                             @Param("virus") Integer virus,
                                             @Param("currentUserName") String currentUserName,
                                             @Param("usingLine") String usingLine,
                                             @Param("remark") String remark);


}
