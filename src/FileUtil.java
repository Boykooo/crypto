import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Andrew Boytsov on 11.03.2018.
 */
public final class FileUtil {

    public static void write(int[] encode, String path) {
        try (FileOutputStream writer = new FileOutputStream(path)) {
            for (int bytes : encode) {
                writer.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeAsLong(long[] longs, String path) {
        List<Byte> list = new ArrayList<>();
        byte[] oldBytes = longToBytes(longs[0]);
        for (int i = 1; i < longs.length; i++) {
            byte[] newBytes = longToBytes(longs[i]);
            byte[] resultBytes = new byte[oldBytes.length + newBytes.length];
            System.arraycopy(oldBytes, 0, resultBytes, 0, oldBytes.length);
            System.arraycopy(newBytes, 0, resultBytes, oldBytes.length, newBytes.length);
            oldBytes = resultBytes;
        }

        try (FileOutputStream stream = new FileOutputStream(path)) {
            stream.write(oldBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static int[] read(String path) {
        List<Integer> fileBytes = new ArrayList<>();
        try (FileInputStream reader = new FileInputStream(path)) {
            while (reader.available() > 0) {
                int read = reader.read();
                fileBytes.add(read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        int[] result = new int[fileBytes.size()];
        for (int i = 0; i < fileBytes.size(); i++) {
            result[i] = fileBytes.get(i);
        }

        return result;
    }

    public static long[] readAsLong(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            long[] longs = new long[(int) Math.ceil(bytes.length / 8.0)];
            int k = 0;
            for (int i = 0; i < longs.length; i++) {
                byte[] temp;
                if (bytes.length - k < 8) {
                    temp = Arrays.copyOfRange(bytes, k, bytes.length);
                    temp = Arrays.copyOf(temp, 8);
                } else {
                    temp = Arrays.copyOfRange(bytes, k, k + 8);
                    k += 8;
                }
                longs[i] = bytesToLong(temp);
            }

            return longs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long[] getAsLong(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        long[] longs = new long[(int) Math.ceil(bytes.length / 8.0)];
        int k = 0;
        for (int i = 0; i < longs.length; i++) {
            byte[] temp;
            if (bytes.length - k < 8) {
                temp = Arrays.copyOfRange(bytes, k, bytes.length);
                temp = Arrays.copyOf(temp, 8);
            } else {
                temp = Arrays.copyOfRange(bytes, k, k + 8);
                k += 8;
            }
            longs[i] = bytesToLong(temp);
        }
        return longs;
    }

    private static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
}
