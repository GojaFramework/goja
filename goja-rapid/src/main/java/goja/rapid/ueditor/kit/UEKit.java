package goja.rapid.ueditor.kit;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class UEKit {

    public static String toUnicode(String input) {

        StringBuilder builder = new StringBuilder();
        char[] chars = input.toCharArray();

        for (char ch : chars) {

            if (ch < 256) {
                builder.append(ch);
            } else {
                builder.append("\\u").append(Integer.toHexString(ch & 0xffff));
            }

        }

        return builder.toString();

    }
}
