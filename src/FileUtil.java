import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
}
