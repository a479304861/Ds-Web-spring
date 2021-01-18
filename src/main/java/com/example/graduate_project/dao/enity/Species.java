package com.example.graduate_project.dao.enity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_species")
public class Species {

    @Id
    private String id;
    @Column(name = "file_id")
    private String fileId;
    @Column(name = "species_name")
    private String speciesName;
    @Column(name = "chromo_num")
    private long chromoNum;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public long getChromoNum() {
        return chromoNum;
    }

    public void setChromoNum(long chromoNum) {
        this.chromoNum = chromoNum;
    }
}
