package skadic.eve.database.repos;

import org.springframework.data.repository.CrudRepository;
import skadic.eve.database.entities.MsgCount;

public interface MsgCountRepo extends CrudRepository<MsgCount, Long> {

}
