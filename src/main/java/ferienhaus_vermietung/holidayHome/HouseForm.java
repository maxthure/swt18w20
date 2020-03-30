package ferienhaus_vermietung.holidayHome;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

interface HouseForm {

	@NotEmpty(message = "{houseForm.nameHouse.NotEmpty}") //
	String getNameHouse();

	@NotNull(message = "{houseForm.beds.NotEmpty}")//
	Integer getBeds();

	@NotEmpty(message = "{Description.NotEmpty}") //
	String getDescription();

	@NotEmpty(message = "{houseForm.street.NotEmpty}") //
	String getStreet();

	@NotEmpty(message = "{houseForm.postcode.NotEmpty}") //
	String getPostcode();

	@NotEmpty(message = "{houseForm.city.NotEmpty}") //
	String getCity();

	@NotNull(message = "{houseForm.rent.NotEmpty}") //
	Double getRent();

	@NotNull(message = "{houseForm.minStay.NotEmpty}") //
	Integer getMinStay();

	@NotNull(message = "{houseForm.maxStay.NotEmpty}") //
	Integer getMaxStay();

	String getImage();

	boolean getKitchen();

	boolean getAc();

	boolean getWashingMachine();

	boolean getTumbleDryer();

	boolean getWifi();

	boolean getBabyCrib();

	boolean getTelevision();
}