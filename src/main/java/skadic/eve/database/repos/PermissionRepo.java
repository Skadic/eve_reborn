package skadic.eve.database.repos;

import org.springframework.data.repository.CrudRepository;
import skadic.eve.database.entities.Permission;

public interface PermissionRepo extends CrudRepository<Permission, Long> {

}
