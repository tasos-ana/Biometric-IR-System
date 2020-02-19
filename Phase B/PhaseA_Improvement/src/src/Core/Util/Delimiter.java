package Core.Util;

public class Delimiter {

    public static String getDelimeters() {
        StringBuilder delimitersBuilder = new StringBuilder();

        // english
        delimitersBuilder.append("\t\n\r\f")
                .append(" ") // space
                .append(",") // comma
                .append(".") // period
                .append("?") // question mark
                .append("!") // exclamation mark
                .append(":") // colon
                .append(";") // semicolon
                .append("\"") // quotation mark
                .append("'") // apostrophe
                .append("-") // dash (hyphen)
                .append("(") // left parenthesis
                .append(")") // right parenthesis
                .append("[") // left square bracket
                .append("]") // right square bracket
                .append("{") // left curly bracket
                .append("}") // right curly bracket
                .append("<") // left angle bracket
                .append(">"); // right angle bracket

        delimitersBuilder.append(".")
                .append("·")
                .append(",")
                .append(":")
                .append("–")
                .append("(")
                .append(")")
                .append("«")
                .append("»")
                .append("‘")
                .append("’")
                .append("“")
                .append("”")
                .append("„")
                .append(";")
                .append("!")
                .append("…")
                .append("†")
                .append("*")
                .append(",")
                .append("'")
                .append("§")
                .append("±");

        return delimitersBuilder.toString();
    }
}
