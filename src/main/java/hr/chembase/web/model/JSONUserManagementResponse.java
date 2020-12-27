package hr.chembase.web.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONUserManagementResponse {

	@JsonProperty("users")
	List<ObjectUser> users;

	public List<ObjectUser> getUsers()
	{
		return users;
	}
	public void setUsers(List<ObjectUser> users)
	{
		this.users = users;
	}
}
