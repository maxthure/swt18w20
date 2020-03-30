package ferienhaus_vermietung.holidayHome;

import org.springframework.stereotype.Component;

@Component
public class HouseComponent {
	/**
	 * Gets user input of requested {@Link Features}.
	 *
	 * @param form must not be {@literal null}.
	 * @return requested {@param features}.
	 */
	public Features getAllFeatures(AvailabilityForm form){
		Features features = new Features();
		features.put(Features.Feature.KITCHEN,form.getKitchenSearch());
		features.put(Features.Feature.AC,form.getAcSearch());
		features.put(Features.Feature.WASHING_MACHINE,form.getWashingMachineSearch());
		features.put(Features.Feature.TUMBLE_DRYER,form.getTumbleDryerSearch());
		features.put(Features.Feature.WIFI,form.getWifiSearch());
		features.put(Features.Feature.BABY_CRIB,form.getBabyCribSearch());
		features.put(Features.Feature.TELEVISION,form.getTelevisionSearch());
		return features;
	}

	/**
	 * Gets {@Link Features} available in a {@Link House}.
	 *
	 * @param form must not be {@literal null}.
	 * @return {@param features}.
	 */
	public Features getAllFeatures(HouseForm form){
		Features features = new Features();
		features.put(Features.Feature.KITCHEN,form.getKitchen());
		features.put(Features.Feature.AC,form.getAc());
		features.put(Features.Feature.WASHING_MACHINE,form.getWashingMachine());
		features.put(Features.Feature.TUMBLE_DRYER,form.getTumbleDryer());
		features.put(Features.Feature.WIFI,form.getWifi());
		features.put(Features.Feature.BABY_CRIB,form.getBabyCrib());
		features.put(Features.Feature.TELEVISION,form.getTelevision());
		return features;
	}

}