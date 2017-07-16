package parser;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {
    @Test
    void parse1() throws ParseException, IOException {
        String input = "x = { x1, x2, x3 }\ny = { y1, y2, y3 }\nz = { z1, z2, z3 }\n "
                + "{ (y1, z1), (y2, z2), (y3, z3) }\n"
                + "!{ (x1, y2), (x1, y3), (x2, y1), (x2, y3), (x3, y1), (x3, y2) }";
        Lexer lexer = new Lexer(new StringReader(input));
        Parser parser = new Parser(lexer);
        parser.parse();
    }

    @Test
    void parse2() {
        String input = "x = {  }\n"
                + "{ }\n"
                + "!{  }";
        Lexer lexer = new Lexer(new StringReader(input));
        Parser parser = new Parser(lexer);
        assertThrows(ParseException.class, parser::parse);
    }

    @Test
    void parse3() {
        String input = "x = { x1 }\ny= { y1 }\nz = { x1 }\n"
                + "{  }\n"
                + "!{  }";
        Lexer lexer = new Lexer(new StringReader(input));
        Parser parser = new Parser(lexer);
        Throwable e = assertThrows(ParseException.class, parser::parse);
        assertEquals("Domains are not disjoint", e.getMessage());
    }

    @Test
    void parse4() throws IOException, ParseException {
        String input = "x = { x1 }\ny= {y1}\n"
                + "{  }\n"
                + "!{  }";
        Lexer lexer = new Lexer(new StringReader(input));
        Parser parser = new Parser(lexer);
        parser.parse();
        assertEquals("x", parser.getVariables().get(0).getName());
        assertEquals("y", parser.getVariables().get(1).getName());
        assertEquals(0, parser.getConstraints().size());
    }

    @Test
    void parse5() {
        String input = "x = { x1 x2 }\n"
                + "{  }\n"
                + "!{  }";
        Lexer lexer = new Lexer(new StringReader(input));
        Parser parser = new Parser(lexer);
        assertThrows(ParseException.class, parser::parse);
    }
}