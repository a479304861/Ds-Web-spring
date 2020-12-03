package com.example.graduate_project.dao.enity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_file" )
public class NamoSunFile
{

  	@Id
	private String fileId;
  	@Column(name = "ori_name" )
	private String oriName;
  	@Column(name = "user_id" )
	private String userId;
  	@Column(name = "complete" )
	private String complete;
  	@Column(name = "index" )
	private String index;
  	@Column(name = "animal_name" )
	private String animalName;
  	@Column(name = "create_time" )
	private Date createTime;

	public String getAnimalName() {
		return animalName;
	}

	public void setAnimalName(String animalName) {
		this.animalName = animalName;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public NamoSunFile() {

	}


	public String getOriName() {
		return oriName;
	}

	public void setOriName(String oriName) {
		this.oriName = oriName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getComplete() {
		return complete;
	}

	public void setComplete(String complete) {
		this.complete = complete;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
