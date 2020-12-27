package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONLocationManagementRequest {

	@JsonProperty("id")
	private Integer id = null;
	
	@JsonProperty("location")
	private String location = null;
	
	/* ------------------------------------------------ */

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
