import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by alex on 21/11/2017.
 */

public class BuildBitMap {
    private static BitSet bs = new BitSet();
    private static int size = 0;
    private static BloomFilter<Integer> bloomFilter;

    public void build(String fileName) throws IOException {

        File file = new File(fileName);

        System.out.println("以行为单位读取文件内容，一次读一整行：");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        int line = 1;
        // 一次读入一行，直到读入null为文件结束
        while ((tempString = reader.readLine()) != null) {
            // 获取port
            tempString = tempString.split("\\s+")[0];
          //  System.out.println("line " + line + ": " + tempString);
            line++;
            bs.set(Integer.parseInt(tempString));
        }
        reader.close();
        size = bs.length();
    }

    //
    public void buildBloomFilter() {

        bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size);
        int[] port_list = new int[size]; //保存出现过的端口号，便于测试漏检率和误检率
        int count = 0;

        //获得bitset中取值为true的索引值，即根据bitmap获得所有出现的端口号
        int i = bs.nextSetBit(0);
        if (i != -1) {
            bloomFilter.put(i);
            port_list[count++] = i;
            while (true) {
                if (++i < 0)
                    break;
                if ((i = bs.nextSetBit(i)) < 0)
                    break;
                int endOfRun = bs.nextClearBit(i);
                do {
                    bloomFilter.put(i);
                    port_list[count++] = i;
                }
                while (++i != endOfRun);
            }
        }

        for (i = 0; i < size; i++) {
            if (!bloomFilter.mightContain(port_list[i])) {
                System.out.println("有坏人逃脱了");
            }
        }

        List<Integer> list = new ArrayList<Integer>(1000);
        for (i = 65535 + 10000; i < 65535 + 30000; i++) {
            if (bloomFilter.mightContain(i)) {
                list.add(i);
            }
        }
        System.out.println("有误伤的数量：" + list.size());

    }

    public static void main(String[] args) throws IOException {
        String fileName = "/Users/alex/Documents/test/port.txt";
        BuildBitMap bbm = new BuildBitMap();
        bbm.build(fileName); //建立位图
        bbm.buildBloomFilter();//根据位图建立BloomFilter，当然也可以直接在读取文件时建立BloomFilter

    }
}
