package hr.chembase.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONSearchRequest {

	@JsonProperty("chemicalName")
	private String chemicalName = null;

	@JsonProperty("storageLocation")
	private Integer storageLocation = null;

	@JsonProperty("manufacturer")
	private String manufacturer = null;

	@JsonProperty("supplier")
	private String supplier = null;

	/* ------------------------------------------------ */

	public String getChemicalName()
	{
		return chemicalName;
	}
	public void setChemicalName(String chemicalName)
	{
		this.chemicalName = chemicalName;
	}

	
	public Integer getStorageLocation()
	{
		return storageLocation;
	}
	public void setStorageLocation(Integer storageLocation)
	{
		this.storageLocation = storageLocation;
	}
	
	
	public String getManufacturer()
	{
		return manufacturer;
	}
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}
	
	
	public String getSupplier()
	{
		return supplier;
	}
	public void setSupplier(String supplier)
	{
		this.supplier = supplier;
	}
}
