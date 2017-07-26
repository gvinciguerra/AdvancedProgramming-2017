import model.InconsistencyException;
import model.Variable;
import parser.Lexer;
import parser.Parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException, InconsistencyException {
        String input = "x = { x1, x2, x3 }\ny = { y1, y2, y3 }\nz = { z1, z2, z3 }\n "
                + "{ (y1, z1), (y2, z2), (y3, z3) }\n"
                + "!{ (x1, y2), (x1, y3), (x2, y1), (x2, y3), (x3, y1), (x3, y2) }";
        Lexer lexer = new Lexer(new StringReader(input));
        Parser parser = new Parser(lexer);
        parser.parse();
        Solver solver = new Solver(parser.getVariables(), parser.getConstraints());
        solver.solutionsIterator().forEachRemaining(s -> System.out.println(s.toString()));
        System.out.println();

        Lexer lexer2 = new Lexer(new FileReader("test/data/sudoku2x2.txt"));
        Parser parser2 = new Parser(lexer2);
        parser2.parse();
        Variable<String> x11 = parser2.getVariables().stream().filter(v -> v.getName().equals("x11")).findFirst().get();
        x11.removeIf(s -> !s.equals("x11@1"));
        Variable<String> x13 = parser2.getVariables().stream().filter(v -> v.getName().equals("x13")).findFirst().get();
        x13.removeIf(s -> !s.equals("x13@4"));
        Variable<String> x31 = parser2.getVariables().stream().filter(v -> v.getName().equals("x31")).findFirst().get();
        x31.removeIf(s -> !s.equals("x31@4"));
        Variable<String> x33 = parser2.getVariables().stream().filter(v -> v.getName().equals("x33")).findFirst().get();
        x33.removeIf(s -> !s.equals("x33@2"));
        Solver solver2 = new Solver(parser2.getVariables(), parser2.getConstraints());
        solver2.solutionsIterator().forEachRemaining(s -> System.out.println(s.toString()));
    }
}
