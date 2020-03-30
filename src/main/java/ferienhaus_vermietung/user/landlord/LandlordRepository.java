package ferienhaus_vermietung.user.landlord;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage {@link Landlord} instances.
 */
public interface LandlordRepository extends CrudRepository<Landlord, Long> {}
