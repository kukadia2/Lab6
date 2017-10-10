import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Implement front compression.
 * <p>
 * Front compression (also called, strangely, back compression, and, less strangely, front coding)
 * is a compression algorithm used for reducing the size of certain kinds of textual structured
 * data. Instead of storing an entire string value, we use a prefix from the previous value in a
 * list.
 * <p>
 * Front compression is particularly useful when compressing lists of words where each successive
 * element has a great deal of similarity with the previous. One example is a search (or book)
 * index. Another example is a dictionary.
 * <p>
 * This starter code will help walk you through the process of implementing front compression.
 *
 * @see <a href="https://cs125.cs.illinois.edu/lab/6/">Lab 6 Description</a>
 * @see <a href="https://en.wikipedia.org/wiki/Incremental_encoding"> Incremental Encoding on
 *      Wikipedia </a>
 */

public class FrontCompression {

    /**
     * Compress a newline-separated list of words using simple front compression.
     *
     * @param corpus the newline-separated list of words to compress
     * @return the input compressed using front encoding
     */
    public static String compress(final String corpus) {
        /*
         * Defend against bad inputs.
         */
        if (corpus == null) {
            return null;
        } else if (corpus.length() == 0) {
            return "";
        }

        String[] words = corpus.split("\n");
        String compressed = "";

        for (int i = 1; i < words.length; i++) {
            int prefix = longestPrefix(words[i], words[i - 1]);
            compressed += (prefix + " " + words[i].substring(prefix) + "\n");
        }

        return "0 " + words[0] + "\n" + compressed;
    }

    /**
     * Decompress a newline-separated list of words using simple front compression.
     *
     * @param corpus the newline-separated list of words to decompress
     * @return the input decompressed using front encoding
     */
    public static String decompress(final String corpus) {
        /*
         * Defend against bad inputs.
         */
        if (corpus == null) {
            return null;
        } else if (corpus.length() == 0) {
            return "";
        }

        String[] words = corpus.split("\n");
        String decompressed = "";

        for (int i = 1; i < words.length; i++) {
            int space = words[i].indexOf(" ");
            int prefixLength = Integer.valueOf(words[i].substring(0, space));
            words[i] = words[i - 1].substring(words[i - 1].indexOf(" ") + 1, prefixLength)
                    + words[i].substring(space + 1);
        }

        return words[0].substring(words[0].indexOf(" ") + 1) + "\n" + decompressed;
    }

    /**
     * Compute the length of the common prefix between two strings.
     *
     * @param firstString the first string
     * @param secondString the second string
     * @return the length of the common prefix between the two strings
     */
    private static int longestPrefix(final String firstString, final String secondString) {
        String min = firstString;
        String max = secondString;

        if (max.length() < min.length()) {
            min = secondString;
            max = firstString;
        }

        int prefixLength = 0;

        for (int i = 0; i < min.length(); i++) {
            if (min.charAt(i) == max.charAt(i)) {
                prefixLength++;
            } else {
                break;
            }
        }

        return prefixLength;
    }

    /**
     * Test your compression and decompression algorithm.
     *
     * @param unused unused input arguments
     * @throws URISyntaxException thrown if the file URI is invalid
     * @throws FileNotFoundException thrown if the file cannot be found
     */
    public static void main(final String[] unused)
            throws URISyntaxException, FileNotFoundException {

        /*
         * The magic 6 lines that you need in Java to read stuff from a file.
         */
        String words = null;
        String wordsFilePath = FrontCompression.class.getClassLoader().getResource("words.txt")
                .getFile();
        wordsFilePath = new URI(wordsFilePath).getPath();
        File wordsFile = new File(wordsFilePath);
        Scanner wordsScanner = new Scanner(wordsFile, "UTF-8");
        words = wordsScanner.useDelimiter("\\A").next();
        wordsScanner.close();

        String originalWords = words;
        String compressedWords = compress(words);
        String decompressedWords = decompress(compressedWords);

        if (decompressedWords.equals(originalWords)) {
            System.out.println("Original length: " + originalWords.length());
            System.out.println("Compressed length: " + compressedWords.length());
        } else {
            System.out.println("Your compression or decompression is broken!");
            String[] originalWordsArray = originalWords.split("\\R");
            String[] decompressedWordsArray = decompressedWords.split("\\R");
            boolean foundMismatch = false;
            for (int stringIndex = 0; //
                    stringIndex < Math.min(originalWordsArray.length,
                            decompressedWordsArray.length); //
                    stringIndex++) {
                if (!(originalWordsArray[stringIndex]
                        .equals(decompressedWordsArray[stringIndex]))) {
                    System.out.println("Line " + stringIndex + ": " //
                            + originalWordsArray[stringIndex] //
                            + " != " + decompressedWordsArray[stringIndex]);
                    foundMismatch = true;
                    break;
                }
            }
            if (!foundMismatch) {
                if (originalWordsArray.length != decompressedWordsArray.length) {
                    System.out.println("Original and decompressed files have different lengths");
                } else {
                    System.out.println("Original and decompressed files " //
                            + "have different line endings.");
                }
            }
        }
    }
}
