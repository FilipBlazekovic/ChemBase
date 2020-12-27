package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectUser {

	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("username")
	private String username;

	@JsonProperty("locked")
	private String locked;
	
	@JsonProperty("lockDate")
	private String lockDate;
	
	/* -------------------------------------- */
	
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
	
	public String getLocked()
	{
		return locked;
	}
	public void setLocked(String locked)
	{
		this.locked = locked;
	}
	
	public String getLockDate()
	{
		return lockDate;
	}
	public void setLockDate(String lockDate)
	{
		this.lockDate = lockDate;
	}
}
