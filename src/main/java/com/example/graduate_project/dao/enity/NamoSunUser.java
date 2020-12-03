package com.example.graduate_project.dao.enity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_user" )
public class NamoSunUser {

  	@Id
	private String id;
  	@Column(name = "ori_name" )
	private String oriName;
  	@Column(name = "user_id" )
	private String userId;
  	@Column(name = "complete" )
	private String complete;
  	@Column(name = "create_time" )
	private Date createTime;
  	@Column(name = "count_num" )
	private String countNum;
  	@Column(name = "animal_name" )
	private String animalName;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getCountNum() {
		return countNum;
	}

	public void setCountNum(String countNum) {
		this.countNum = countNum;
	}

	public String getAnimalName() {
		return animalName;
	}

	public void setAnimalName(String animalName) {
		this.animalName = animalName;
	}
}
