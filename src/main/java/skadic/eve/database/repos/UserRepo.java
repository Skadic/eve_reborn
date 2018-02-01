package skadic.eve.database.repos;

import org.springframework.data.repository.CrudRepository;
import skadic.eve.database.entities.User;

public interface UserRepo extends CrudRepository<User, Long>{

}
