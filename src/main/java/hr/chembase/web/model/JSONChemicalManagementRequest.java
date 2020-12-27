package hr.chembase.web.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONChemicalManagementRequest {

	@JsonProperty("id")
	private Integer id = null;

	@JsonProperty("chemicalName")
	private String chemicalName = null;

	@JsonProperty("bruttoFormula")
	private String bruttoFormula = null;

	@JsonProperty("molarMass")
	private String molarMass = null;

	@JsonProperty("amount")
	private BigDecimal amount = null;
	
	@JsonProperty("unit")
	private String unit = null;

	@JsonProperty("storageLocation")
	private Integer storageLocation = null;

	@JsonProperty("manufacturer")
	private String manufacturer = null;

	@JsonProperty("supplier")
	private String supplier = null;

	@JsonProperty("additionalInfo")
	private String additionalInfo = null;

	/* ------------------------------------------------ */

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


	public String getMolarMass()
	{
		return molarMass;
	}
	public void setMolarMass(String molarMass)
	{
		this.molarMass = molarMass;
	}


	public BigDecimal getAmount()
	{
		return amount;
	}
	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}


	public String getUnit()
	{
		return unit;
	}
	public void setUnit(String unit)
	{
		this.unit = unit;
	}


	public String getManufacturer()
	{
		return manufacturer;
	}
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}


	public Integer getStorageLocation()
	{
		return storageLocation;
	}
	public void setStorageLocation(Integer storageLocation)
	{
		this.storageLocation = storageLocation;
	}


	public String getSupplier()
	{
		return supplier;
	}
	public void setSupplier(String supplier)
	{
		this.supplier = supplier;
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
