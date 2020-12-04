package com.example.graduate_project.dao;


import com.example.graduate_project.dao.enity.NamoSunUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NamosunUserDao extends JpaRepository<NamoSunUser,String>, JpaSpecificationExecutor<NamoSunUser> {

    List<NamoSunUser> findAllByUserId(String userId);
    NamoSunUser findOneById(String fileId);

}
