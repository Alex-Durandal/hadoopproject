import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;

/**
 * Created by alex on 21/11/2017.
 */

public class BuildBitMap {
    private static BitSet bs = new BitSet();
    public void build(String fileName) throws IOException {

        File file = new File(fileName);
        BufferedReader reader = null;

        System.out.println("以行为单位读取文件内容，一次读一整行：");
        reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        int line = 1;
        // 一次读入一行，直到读入null为文件结束
        while ((tempString = reader.readLine()) != null) {
            // 显示行号
            tempString = tempString.split("\\s+")[0];
            System.out.println("line " + line + ": " + tempString);
            line++;
            bs.set(Integer.parseInt(tempString));
        }
        reader.close();
    }

    public static void main(String[] args) throws IOException {
        String fileName = "/Users/alex/Documents/test/port.txt";
        BuildBitMap bbm = new BuildBitMap();
        bbm.build(fileName);
        System.out.println(bs);
        System.out.println(bs.size());
        System.out.println(bs.length());
    }

}
