import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * All the hashing functions which are required to read from index file into a TreeMap data Structure.
 * It also has all the functions required to write from a TreeMap back to an index file.
 *
 * @author Salil Kansal
 * @version 1.0
 * @since 2016-04-09
 */
public class HashingFunctions {
    static void getHashfromFile(RandomAccessFile indexFile, TreeMap<String, List<Integer>> hashMap) throws IOException {
        int index = 0;
        indexFile.seek(0);
        while (index < indexFile.length()) {
            String key = readVarChar(indexFile);
            index = index + key.length() + 1;
            int n = readInt(indexFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(indexFile));
                index += 4;
            }
            hashMap.put(key, l1);
        }
    }

    static void saveHashtoFile(RandomAccessFile indexFile, TreeMap<String, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        for (String key : hashMap.keySet()) {
            indexFile.writeByte(key.length());
            indexFile.writeBytes(key);
            indexFile.writeInt(hashMap.get(key).size());
            for (int i : hashMap.get(key)) {
                indexFile.writeInt(i);
            }
        }
    }

    static void getLongHashFromFile(RandomAccessFile indexFile, TreeMap<Long, List<Integer>> hashMap) throws IOException {
        int index = 0;
        indexFile.seek(0);
        while (index < indexFile.length()) {
            Long key = readLong(indexFile);
            index += 8;
            int n = readInt(indexFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(indexFile));
                index += 4;
            }
            hashMap.put(key, l1);
        }
    }

    static void saveLongHashtoFile(RandomAccessFile indexFile, TreeMap<Long, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        for (Long key : hashMap.keySet()) {
            indexFile.writeLong(key);
            indexFile.writeInt(hashMap.get(key).size());
            for (int i : hashMap.get(key)) {
                indexFile.writeInt(i);
            }
        }
    }

    static void getIntHashFromFile(RandomAccessFile indexFile, TreeMap<Integer, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        int index = 0;
        indexFile.seek(0);
        while (index < indexFile.length()) {
            Integer key = readInt(indexFile);
            index += 4;
            int n = readInt(indexFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(indexFile));
                index += 4;
            }
            hashMap.put(key, l1);
        }
    }

    static void saveIntHashtoFile(RandomAccessFile indexFile, TreeMap<Integer, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        for (Integer key : hashMap.keySet()) {
            indexFile.writeInt(key);
            indexFile.writeInt(hashMap.get(key).size());
            for (int i : hashMap.get(key)) {
                indexFile.writeInt(i);
            }
        }
    }

    static void getDoubleHashfromFile(RandomAccessFile indexFile, TreeMap<Double, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        int index = 0;
        while (index < indexFile.length()) {
            Double key = indexFile.readDouble();
            index += 8;
            int n = readInt(indexFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(indexFile));
                index += 4;
            }
            hashMap.put(key, l1);
        }
    }

    static void saveDoubleHashtoFile(RandomAccessFile indexFile, TreeMap<Double, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        for (Double key : hashMap.keySet()) {
            indexFile.writeDouble(key);
            indexFile.writeInt(hashMap.get(key).size());
            for (int i : hashMap.get(key)) {
                indexFile.writeInt(i);
            }
        }
    }

    static void getFloatHashfromFile(RandomAccessFile indexFile, TreeMap<Float, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        int index = 0;
        while (index < indexFile.length()) {
            Float key = indexFile.readFloat();
            index += 4;
            int n = readInt(indexFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(indexFile));
                index += 4;
            }
            hashMap.put(key, l1);
        }
    }

    static void saveFloatHashFromFile(RandomAccessFile indexFile, TreeMap<Float, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        for (Float key : hashMap.keySet()) {
            indexFile.writeFloat(key);
            indexFile.writeInt(hashMap.get(key).size());
            for (int i : hashMap.get(key)) {
                indexFile.writeInt(i);
            }
        }
    }

    static void getShortHashFromFile(RandomAccessFile indexFile, TreeMap<Short, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        int index = 0;
        while (index < indexFile.length()) {
            Short key = indexFile.readShort();
            index = index + 2;
            int n = readInt(indexFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(indexFile));
                index += 4;
            }
            hashMap.put(key, l1);
        }
    }

    static void saveShortHashtoFile(RandomAccessFile indexFile, TreeMap<Short, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        for (Short key : hashMap.keySet()) {
            indexFile.writeShort(key);
            indexFile.writeInt(hashMap.get(key).size());
            for (int i : hashMap.get(key)) {
                indexFile.writeInt(i);
            }
        }
    }

    static void getByteHashFromFile(RandomAccessFile indexFile, TreeMap<Byte, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        int index = 0;
        while (index < indexFile.length()) {
            Byte key = indexFile.readByte();
            index = index + 1;
            int n = readInt(indexFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(indexFile));
                index += 4;
            }
            hashMap.put(key, l1);
        }
    }

    static void saveByteHashtoFile(RandomAccessFile indexFile, TreeMap<Byte, List<Integer>> hashMap) throws IOException {
        indexFile.seek(0);
        for (Byte key : hashMap.keySet()) {
            indexFile.writeByte(key);
            indexFile.writeInt(hashMap.get(key).size());
            for (int i : hashMap.get(key)) {
                indexFile.writeInt(i);
            }
        }
    }

    static void readCharHashFromFile(RandomAccessFile indexFile, TreeMap<String, List<Integer>> hashMap, int length) throws IOException {
        indexFile.seek(0);
        int index = 0;
        while (index < indexFile.length()) {
            String key = readChar(indexFile, length);
            index = index + key.length() + 1;
            int n = readInt(indexFile);
            index += 4;
            List<Integer> l1 = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                l1.add(readInt(indexFile));
                index += 4;
            }
            hashMap.put(key, l1);
        }
    }

    static void saveCharHashtoFile(RandomAccessFile indexFile, TreeMap<String, List<Integer>> hashMap, int length) throws IOException {
        indexFile.seek(0);
        for (String key : hashMap.keySet()) {
            writeChar(indexFile, key, length);
            indexFile.writeInt(hashMap.get(key).size());
            for (int i : hashMap.get(key)) {
                indexFile.writeInt(i);
            }
        }
    }


    static private void writeChar(RandomAccessFile raf, String data, int n) throws IOException {
        raf.writeBytes(data);
        for (int i = 0; i < n - data.length(); i++) {
            raf.writeByte(0x00);
        }

    }

    static private Long readLong(RandomAccessFile raf) throws IOException {
        return raf.readLong();
    }

    static private Integer readInt(RandomAccessFile raf) throws IOException {
        return raf.readInt();
    }

    static private String readChar(RandomAccessFile raf, int n) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            byte b = raf.readByte();
            if (b == 0x00) break;
            sb.append((char) b);
        }
        return sb.toString();
    }

    static private String readVarChar(RandomAccessFile raf) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte length = raf.readByte();
        for (int i = 0; i < length; i++) {
            sb.append((char) raf.readByte());
        }
        return sb.toString();
    }
}
