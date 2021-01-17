package com.example.graduate_project.dao.enity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table ( name ="tb_settings" )
public class Settings {

  	@Id
	private String id;
  	@Column(name = "key" )
	private String key;
  	@Column(name = "value" )
	private String value;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
