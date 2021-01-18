package com.example.graduate_project.dao;


import com.example.graduate_project.dao.enity.Settings;
import com.example.graduate_project.dao.enity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpecialDao extends JpaRepository<Species, String>,
        JpaSpecificationExecutor<Species> {

    Species findOneById(String id);

    List<Species> findAllByFileId(String fileId);

//    @Modifying
//    @Query(value="update tb_settings set `value`=`value`+1 where `id` = 1",nativeQuery=true)
//    void updateViewCount();
}
