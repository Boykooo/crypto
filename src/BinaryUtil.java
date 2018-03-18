/**
 * Created by Andrew Boytsov on 11.03.2018.
 */
public class BinaryUtil {

    public static int shiftLeft(int value, int shift) {
        return (value << shift) | (value >> 32 - shift);
    }

    public static int shiftRight(int value, int shift) {
        return (value >> shift) | (value << 32 - shift);
    }

    public static int getLeftPart(long block) {
        return (int) (block >> 32);
    }

    public static int getRightPart(long block) {
        return (int) block;
    }
}
