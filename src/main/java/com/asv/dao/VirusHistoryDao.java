package com.asv.dao;

import com.asv.entity.VirusHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VirusHistoryDao extends JpaRepository<VirusHistory, Long> {

    List<VirusHistory> findByDeviceId(String deviceId, Sort sort);

    List<VirusHistory> findByDeviceIdIn(List<String> deviceIds);
}
