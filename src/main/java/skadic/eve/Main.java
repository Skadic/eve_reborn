package skadic.eve;

import org.springframework.boot.SpringApplication;
import skadic.commands.util.Utils;
import skadic.eve.main.EveLog;

import java.io.File;

public class Main {
    public static void main(String[] args){
        if(args.length <= 0)
            throw new RuntimeException("Missing token!");

        makeDirs();
        SpringApplication.run(Eve.class, args);
    }



    private static void makeDirs(){
        EveLog.debug("Creating directories");
        File msgcount = Utils.getFileInWorkingDir("data/maps/msgcount");
        if(!msgcount.exists()){
            msgcount.mkdirs();
            EveLog.debug(msgcount.getAbsolutePath() + " has been created successfully");
        }
    }
}
