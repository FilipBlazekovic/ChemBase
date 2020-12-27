package hr.chembase.web.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectChemical {

	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("chemicalName")
	private String chemicalName;
	
	@JsonProperty("bruttoFormula")
	private String bruttoFormula;
	
	@JsonProperty("moralMass")
	private String moralMass;
	
	@JsonProperty("quantity")
	private BigDecimal quantity;
	
	@JsonProperty("unit")
	private String unit;

	@JsonProperty("storageLocation")
	private String storageLocation;

	@JsonProperty("manufacturer")
	private String manufacturer;
	
	@JsonProperty("supplier")
	private String supplier;

	@JsonProperty("dateOfEntry")
	private String dateOfEntry;
	
	@JsonProperty("additionalInfo")
	private String additionalInfo;

	/* ---------------------------------------------- */

	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getChemicalName()
	{
		return chemicalName;
	}
	public void setChemicalName(String chemicalName)
	{
		this.chemicalName = chemicalName;
	}

	public String getBruttoFormula()
	{
		return bruttoFormula;
	}
	public void setBruttoFormula(String bruttoFormula)
	{
		this.bruttoFormula = bruttoFormula;
	}

	public String getMoralMass()
	{
		return moralMass;
	}
	public void setMoralMass(String moralMass)
	{
		this.moralMass = moralMass;
	}

	public BigDecimal getQuantity()
	{
		return quantity;
	}
	public void setQuantity(BigDecimal quantity)
	{
		this.quantity = quantity;
	}
	
	public String getUnit()
	{
		return unit;
	}
	public void setUnit(String unit)
	{
		this.unit = unit;
	}
	
	public String getStorageLocation()
	{
		return storageLocation;
	}
	public void setStorageLocation(String storageLocation)
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

	public String getDateOfEntry()
	{
		return dateOfEntry;
	}
	public void setDateOfEntry(String dateOfEntry)
	{
		this.dateOfEntry = dateOfEntry;
	}

	public String getAdditionalInfo()
	{
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo)
	{
		this.additionalInfo = additionalInfo;
	}
}
