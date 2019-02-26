package utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {


    public static final Path FILE_PATH = Paths.get("/Users/cernescustefan/Documents/Facultate/PCD/HomeWork1Client/testfile");
    public static final Integer ONE_KYLOBYTE = 1024;
    public static final Integer ONE_MEGABYTE = ONE_KYLOBYTE * 1024;
    public static final Integer ONE_GYGABYTE = ONE_MEGABYTE * 1024;
    public static final Integer FILE_SIZE = 500 * ONE_MEGABYTE;
    public static final Integer TIMEOUT = 1;
    public static final String IP_ADDRESS = "127.0.0.1";
    public static final Integer PORT = 65000;
    public static final Integer LENGTH_SIZE = 4;
}
