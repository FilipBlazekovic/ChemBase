package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONErrorResponse {

	@JsonProperty("error")
	String error;

	public String getError()
	{
		return error;
	}
	public void setError(String error)
	{
		this.error = error;
	}
}
