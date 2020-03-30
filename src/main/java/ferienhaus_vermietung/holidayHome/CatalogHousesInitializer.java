package ferienhaus_vermietung.holidayHome;

import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;



@Component
@Order(20)
public class CatalogHousesInitializer implements DataInitializer{

	private final CatalogHouses catalogHouses;

	CatalogHousesInitializer(CatalogHouses catalogHouses) {
		Assert.notNull(catalogHouses, "The house catalog must not be null!");
		this.catalogHouses = catalogHouses;
	}

	@Override
	public void initialize()  {

		if (catalogHouses.findAll().iterator().hasNext()) {
			return;
		}

		catalogHouses.save(new House("DELETED ITEM","-","-",0,
				new String[]{"-","-","-","-"},"-","",0,0,0,new Features()));

	}

}