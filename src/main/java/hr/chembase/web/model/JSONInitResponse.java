package hr.chembase.web.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONInitResponse {

	@JsonProperty("users")
	List<ObjectUser> users;

	@JsonProperty("locations")
	List<ObjectLocation> locations;
	
	@JsonProperty("chemicals")
	List<ObjectChemical> chemicals;

	@JsonProperty("selectableLocations")
	List<SelectableLocation> selectableLocations;
	
	@JsonProperty("adminUser")
	Integer adminUser;
	
	/* --------------------------------------------------- */

	public List<ObjectUser> getUsers()
	{
		return users;
	}
	public void setUsers(List<ObjectUser> users)
	{
		this.users = users;
	}
	
	
	public List<ObjectLocation> getLocations()
	{
		return locations;
	}
	public void setLocations(List<ObjectLocation> locations)
	{
		this.locations = locations;
	}
	
	
	public List<ObjectChemical> getChemicals()
	{
		return chemicals;
	}
	public void setChemicals(List<ObjectChemical> chemicals)
	{
		this.chemicals = chemicals;
	}
	
	
	public List<SelectableLocation> getSelectableLocations()
	{
		return selectableLocations;
	}
	public void setSelectableLocations(List<SelectableLocation> selectableLocations)
	{
		this.selectableLocations = selectableLocations;
	}
	
	
	public Integer getAdminUser()
	{
		return adminUser;
	}
	public void setAdminUser(Integer adminUser)
	{
		this.adminUser = adminUser;
	}
}
