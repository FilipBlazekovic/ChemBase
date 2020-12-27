package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONLoginResponse {

	@JsonProperty("status")
	private String status;

	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
}
