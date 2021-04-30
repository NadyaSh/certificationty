package org.niitp.repository;

import org.niitp.model.JsonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface JsonRepository extends JpaRepository<JsonEntity, Long> {

    JsonEntity saveAndFlush(JsonEntity json);

    @Override
    Optional<JsonEntity> findById(Long id);

    @Query(value = "select * from json_certification order by datetime desc", nativeQuery = true)
    List<JsonEntity> findAll();
}