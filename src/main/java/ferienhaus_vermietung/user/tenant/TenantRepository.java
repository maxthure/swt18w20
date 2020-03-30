package ferienhaus_vermietung.user.tenant;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage {@link Tenant} instances.
 */
interface TenantRepository extends CrudRepository<Tenant, Long> {}
