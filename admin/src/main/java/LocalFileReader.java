import java.io.*;

/**
 * Created by MK33 on 2016/9/20.
 */
public class LocalFileReader {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Please input file name");
            System.exit(-1);
        }
        FileInputStream fis = new FileInputStream(args[0]);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        String tmp = null;
        while ((tmp = reader.readLine()) != null) {
            System.out.println(tmp);
        }
        System.out.println("========== end =======");


    }
}
