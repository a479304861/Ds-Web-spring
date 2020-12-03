package com.example.graduate_project.dao;


import com.example.graduate_project.dao.enity.NamoSunFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NamosunFileDao extends JpaRepository<NamoSunFile,String >, JpaSpecificationExecutor<NamoSunFile> {

    List<NamoSunFile> findAllByUserId(String userId);
    NamoSunFile findOneByFileId(String fileId);
}
