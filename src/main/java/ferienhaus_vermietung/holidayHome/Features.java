package ferienhaus_vermietung.holidayHome;

import java.io.Serializable;
import java.util.HashMap;

public class Features extends HashMap<Features.Feature, Boolean> implements Serializable {

	/**
	 * Initiating {@Link Features}.
	 */
	public enum Feature{
		KITCHEN,AC,WASHING_MACHINE,TUMBLE_DRYER,WIFI,BABY_CRIB,TELEVISION
	}

	/**
	 * Initiating {@Link Features} by setting the default value to false.
	 */
	public Features(){
		this.put(Features.Feature.KITCHEN,false);
		this.put(Features.Feature.AC,false);
		this.put(Features.Feature.WASHING_MACHINE,false);
		this.put(Features.Feature.TUMBLE_DRYER,false);
		this.put(Features.Feature.WIFI,false);
		this.put(Features.Feature.BABY_CRIB,false);
		this.put(Features.Feature.TELEVISION,false);
	}

	/**
	 * Tests if required {@Link Feature}s are satisfied.
	 *
	 * @param features must not be {@literal null}.
	 * @return true if {@Link Feature}s are satisfied, false otherwise.
	 */
	public boolean satisfies(Features features){
		for (Feature f : features.keySet()) {
			if(features.get(f) && !this.get(f)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if a {@Link Feature} exists.
	 *
	 * @param str must not be {@literal null}.
	 * @return boolean or null.
	 */
	public Boolean get(String str) {
		for (Feature feature : Feature.values()) {
			if(feature.toString().equals(str)){
				return super.get(feature);
			}
		}
		return null;
	}
}
