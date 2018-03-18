import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Andrew Boytsov on 11.03.2018.
 */

public class Cipher {

    private final int initVector = 55234;
    private static final int rounds = 3;
    private final int key = 291325325;

    public void executeFeistel() {
        encode();
        decode();
    }

    public void executeCBC() {
        encodeCBC();
        decryptCBC();
    }

    private void encodeCBC() {
        long[] fileBytes = FileUtil.readAsLong("text.txt");
        int[] keys = keygen(rounds, key);

        fileBytes[0] = initVector ^ fileBytes[0];
        long[] result = new long[fileBytes.length];
        for (int i = 0; i < fileBytes.length; i++) {
            result[i] = process(fileBytes[i], keys, false);
            if (i != fileBytes.length - 1) {
                fileBytes[i + 1] ^= result[i];
            }
        }

        System.out.println("bytes after encode = " + Arrays.toString(result));
        FileUtil.writeAsLong(result, "encode.txt");
    }

    private void decryptCBC() {
        long[] fileBytes = FileUtil.readAsLong("encode.txt");
        System.out.println("bytes from file  = " + Arrays.toString(fileBytes));
        int[] keys = keygen(rounds, key);
        long[] result = new long[fileBytes.length];
        for (int i = 0; i < fileBytes.length; i++) {
            result[i] = process(fileBytes[i], keys, true);
            if (i == 0) {
                result[i] ^= initVector;
            } else {
                result[i] ^= fileBytes[i - 1];
            }
        }

        System.out.println("bytes after decode = " + Arrays.toString(result));
        FileUtil.writeAsLong(result, "decode.txt");
    }

    public void executeCBF() {
        encodeCBF();
        decryptCBF();
    }

    private void encodeCBF() {
        long[] fileBytes = FileUtil.readAsLong("text.txt");
        int[] keys = keygen(rounds, key);

        long[] result = new long[fileBytes.length];
        long temp;
        for (int i = 0; i < fileBytes.length; i++) {
            if (i == 0) {
                temp = initVector;
            } else {
                temp = result[i - 1];
            }
            result[i] = fileBytes[i] ^ process(temp, keys, false);
        }

        System.out.println("bytes after encode = " + Arrays.toString(result));
        FileUtil.writeAsLong(result, "encode.txt");
    }

    private void decryptCBF() {
        long[] fileBytes = FileUtil.readAsLong("encode.txt");
        System.out.println("bytes from file  = " + Arrays.toString(fileBytes));
        int[] keys = keygen(rounds, key);

        long[] result = new long[fileBytes.length];
        long temp;
        for (int i = 0; i < fileBytes.length; i++) {
            if (i == 0) {
                temp = initVector;
            } else {
                temp = fileBytes[i - 1];
            }
            result[i] = fileBytes[i] ^ process(temp, keys, false);
        }

        System.out.println("bytes after decode = " + Arrays.toString(result));
        FileUtil.writeAsLong(result, "decode.txt");
    }


    private void encode() {
        long[] fileBytes = FileUtil.readAsLong("text.txt");
        int[] keys = keygen(rounds, key);
        for (int i = 0; i < fileBytes.length; i++) {
            fileBytes[i] = process(fileBytes[i], keys, false);
        }

        System.out.println("bytes after encode = " + Arrays.toString(fileBytes));
        FileUtil.writeAsLong(fileBytes, "encode.txt");
    }

    private int[] keygen(int rounds, int key) {
        int[] keys = new int[rounds];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = BinaryUtil.shiftRight(key, i * 3) & 0xFF; // & 0xFF игнорирует знак
        }
        return keys;
    }

    private void decode() {
        long[] fileBytes = FileUtil.readAsLong("encode.txt");
        int[] keys = keygen(rounds, key);
        for (int i = 0; i < fileBytes.length; i++) {
            fileBytes[i] = process(fileBytes[i], keys, true);
        }
        System.out.println("bytes after decode = " + Arrays.toString(fileBytes));
        FileUtil.writeAsLong(fileBytes, "decode.txt");
    }

    public static long process(long block, int[] keys, boolean reverse) {
        int round = reverse ? rounds - 1 : 0;
        int left = BinaryUtil.getLeftPart(block);
        int right = BinaryUtil.getRightPart(block);
        for (int j = 0; j < rounds; j++) {
            if (j < rounds - 1) {
                int oldLeftBlock = left;
                left = right ^ genFunc(left, keys[round]); // & 0xFF игнорирует знак
                right = oldLeftBlock;
            } else {
                right = right ^ genFunc(left, keys[round]);
            }
            round += reverse ? -1 : 1;
        }

        return ((long) left << 32 | right & 0xffffffffL);
    }

    private static int genFunc(int left, int key) {
        return BinaryUtil.shiftLeft(left, 9) ^ (~(BinaryUtil.shiftRight(key, 11) & left));
    }

}
