package com.example.graduate_project.dao;


import com.example.graduate_project.dao.enity.NamoSunUser;
import com.example.graduate_project.dao.enity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingDao extends JpaRepository<Settings,String>,
        JpaSpecificationExecutor<Settings> {

    Settings findOneById(String id);

    @Modifying
    @Query(value="update tb_settings set `value`=`value`+1 where `id` = 1",nativeQuery=true)
    void updateViewCount();
}
