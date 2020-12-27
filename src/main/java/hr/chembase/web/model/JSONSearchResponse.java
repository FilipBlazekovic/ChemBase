package hr.chembase.web.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONSearchResponse {

	@JsonProperty("chemicals")
	List<ObjectChemical> chemicals;

	public List<ObjectChemical> getChemicals()
	{
		return chemicals;
	}
	public void setChemicals(List<ObjectChemical> chemicals)
	{
		this.chemicals = chemicals;
	}
}
