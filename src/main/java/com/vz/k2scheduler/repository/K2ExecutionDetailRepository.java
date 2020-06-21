package com.vz.k2scheduler.repository;

import com.vz.k2scheduler.model.K2ExecutionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface K2ExecutionDetailRepository extends JpaRepository<K2ExecutionDetail, Integer> {
    List<K2ExecutionDetail> findByJobId(Integer jobId);
}
