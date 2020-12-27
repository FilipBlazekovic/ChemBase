package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SelectableLocation {

	@JsonProperty("selectval")
	private Integer selectval;
	
	@JsonProperty("displayval")
	private String displayval;

	/* ---------------------------------------- */

	public Integer getSelectval()
	{
		return selectval;
	}
	public void setSelectval(Integer selectval)
	{
		this.selectval = selectval;
	}

	public String getDisplayval()
	{
		return displayval;
	}
	public void setDisplayval(String displayval)
	{
		this.displayval = displayval;
	}
}
