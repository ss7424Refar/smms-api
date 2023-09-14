package com.asv.dao;

import com.asv.entity.MailQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailQueueDao extends JpaRepository<MailQueue, Long> {
    List<MailQueue> findByFlag(Integer flag);

}
