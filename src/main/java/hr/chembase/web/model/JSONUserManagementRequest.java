package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONUserManagementRequest {

	@JsonProperty("id")
	private Integer id = null;

	@JsonProperty("username")
	private String username = null;

	@JsonProperty("password")
	private String password = null;

	/* ------------------------------------------------ */

	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}


	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}


	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
}
