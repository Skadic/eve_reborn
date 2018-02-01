package skadic.eve.database.repos;

import org.springframework.data.repository.CrudRepository;
import skadic.eve.database.entities.Title;

public interface TitleRepo extends CrudRepository<Title, Long>{
}
