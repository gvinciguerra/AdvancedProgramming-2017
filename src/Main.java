import parser.Lexer;
import parser.Token;

import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws IOException {
        String input = "x = { x1, x2, x3 }\ny = { y1, y2, y3 }\nz = { z1, z2, z3 }\n "
                + "{ (y1, z1), (y2, z2), (y3, z3) }\n"
                + "!{ (x1, y2), (x1, y3), (x2, y1), (x2, y3), (x3, y1), (x3, y2) }";

        Lexer lexer = new Lexer(new StringReader(input));
        Token t;
        while ((t = lexer.next()) != null) {
            System.out.printf("%s {%s}\n", t.getType().toString(), t.getLexeme());
        }
    }
}
