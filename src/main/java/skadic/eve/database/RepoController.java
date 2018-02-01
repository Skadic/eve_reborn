package skadic.eve.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import skadic.eve.database.repos.UserRepo;
import skadic.eve.database.repos.MsgCountRepo;
import skadic.eve.database.repos.TitleRepo;

@Controller
public class RepoController {


    @Autowired
    private MsgCountRepo msgCountRepo;

    @Autowired
    private TitleRepo titleRepo;

    @Autowired
    private UserRepo userRepo;
}
