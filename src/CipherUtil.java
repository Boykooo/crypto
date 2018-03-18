import java.util.Arrays;

/**
 * Created by Andrew Boytsov on 11.03.2018.
 */
public class CipherUtil {

    public static int[] handleOddBlocksSize(int[] blocks) {
        if (blocks.length % 2 != 0) {
            blocks = Arrays.copyOf(blocks, blocks.length + 1);
        }
        return blocks;
    }

}
