package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONChemicalManagementResponse {

	@JsonProperty("chemical")
	ObjectChemical chemical;
	
	public ObjectChemical getChemical()
	{
		return chemical;
	}
	public void setChemicals(ObjectChemical chemical)
	{
		this.chemical = chemical;
	}
}
