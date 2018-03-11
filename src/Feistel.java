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

public class Feistel {

    private final int rounds = 6;
    private final int key = 291325325;

    public void execute() {
        encode();
        decode();
    }

    private void encode() {
        int[] fileBytes = FileUtil.read("text.txt");
        if (fileBytes.length % 2 != 0) {
            fileBytes = addBytes(fileBytes);
        }
        int[] keys = keygen(rounds, key);
        System.out.println("keys = " + Arrays.toString(keys));
        int[] encode = feistel(fileBytes, keys, false);
        System.out.println("bytes after encode = " + Arrays.toString(encode));
        FileUtil.write(encode, "encode.txt");
    }

    private int[] keygen(int rounds, int key) {
        int[] keys = new int[rounds];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = shiftRight(key, i * 3) & 0xFF; // & 0xFF игнорирует знак
        }
        return keys;
    }

    private int[] addBytes(int[] bytes) {
        int[] result = Arrays.copyOf(bytes, bytes.length + 1);
        result[result.length - 1] = 0x00000000;
        return result;
    }

    private void decode() {
        int[] fileBytes = FileUtil.read("encode.txt");
        if (fileBytes.length % 2 != 0) {
            fileBytes = addBytes(fileBytes);
        }

        int[] decode = feistel(fileBytes, keygen(rounds, key), true);
        System.out.println("bytes after decode = " + Arrays.toString(decode));
        FileUtil.write(decode, "decode.txt");
    }

    private int[] feistel(int[] bytes, int[] keys, boolean reverse) {
        System.out.println("start bytes = " + Arrays.toString(bytes));

        for (int i = 0; i < bytes.length - 1; i += 2) {
            int left = bytes[i];
            int right = bytes[i + 1];
            int round = reverse ? rounds - 1 : 0;
            for (int j = 0; j < rounds; j++) {
                if (j < rounds - 1) {
                    int oldLeftBlock = left;
                    left = right ^ genFunc(left, keys[round]) & 0xFF; // & 0xFF игнорирует знак
                    right = oldLeftBlock;
                } else {
                    right = right ^ genFunc(left, keys[round]) & 0xFF;
                }
                round += reverse ? -1 : 1;
            }

            bytes[i] = left;
            bytes[i + 1] = right;
        }

        return bytes;
    }

    private int genFunc(int left, int key) {
        return shiftLeft(left, 9) ^ (~(shiftRight(key, 11) & left));
    }

    private int shiftLeft(int value, int shift) {
        return (value << shift) | (value >> 32 - shift);
    }

    private int shiftRight(int value, int shift) {
        return (value >> shift) | (value << 32 - shift);
    }


}
