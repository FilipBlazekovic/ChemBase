package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectLocation {

	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("location")
	private String location;

	/* -------------------------------------- */

	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getLocation()
	{
		return location;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
}
