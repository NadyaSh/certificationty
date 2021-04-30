package org.niitp.repository;

import org.niitp.model.XmlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface XmlRepository extends JpaRepository<XmlEntity, Long> {

    XmlEntity saveAndFlush(XmlEntity xml);

    @Query(value = "select * from xml_certification where filename = ?1",
            nativeQuery = true)
    List<XmlEntity> findByFilename(String filename);

    @Query(value = "select * from xml_certification where application = ?1",
            nativeQuery = true)
    List<XmlEntity> findByApplication(String application);

    Optional<XmlEntity> findById(Long id);
}
