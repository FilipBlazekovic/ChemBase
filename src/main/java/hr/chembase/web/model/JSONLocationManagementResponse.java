package hr.chembase.web.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JSONLocationManagementResponse {

	@JsonProperty("locations")
	List<ObjectLocation> locations;

	@JsonProperty("selectableLocations")
	List<SelectableLocation> selectableLocations;


	public List<ObjectLocation> getLocations()
	{
		return locations;
	}
	public void setLocations(List<ObjectLocation> locations)
	{
		this.locations = locations;
	}


	public List<SelectableLocation> getSelectableLocations()
	{
		return selectableLocations;
	}
	public void setSelectableLocations(List<SelectableLocation> selectableLocations)
	{
		this.selectableLocations = selectableLocations;
	}
}
