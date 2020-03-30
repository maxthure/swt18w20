package ferienhaus_vermietung.user.eventmanager;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage {@link Eventmanager} instances.
 */
public interface EventmanagerRepository extends CrudRepository<Eventmanager, Long> {}
