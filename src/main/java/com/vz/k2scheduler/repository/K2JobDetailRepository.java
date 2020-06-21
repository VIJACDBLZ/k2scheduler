package com.vz.k2scheduler.repository;

import com.vz.k2scheduler.model.K2JobDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface K2JobDetailRepository extends JpaRepository<K2JobDetail, Integer> {

}
