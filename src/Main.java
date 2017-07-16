import model.InconsistencyException;
import parser.Lexer;
import parser.Parser;

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
        System.out.println(solver.backtrackingSearch().toString());

        String sudoku = "x11 = {a1, a2, a3, a4}\n"
                + "x12 = {b1, b2, b3, b4}\n"
                + "x21 = {c1, c2, c3, c4}\n"
                + "x22 = {d1, d2, d3, d4}\n"
                + "{}\n!{"
                + "(a1, b1), (a1, c1), (a2, b2), (a2, c2), (a3, b3), (a3, c3), (a4, b4), (a4, c4),"
                + "(b1, a1), (b1, d1), (b2, a2), (b2, d2), (b3, a3), (b3, d3), (b4, a4), (b4, d4),"
                + "(c1, a1), (c1, d1), (c2, a2), (c2, d2), (c3, a3), (c3, d3), (c4, a4), (c4, d4),"
                + "(d1, b1), (d1, c1), (d2, b2), (d2, c2), (d3, b3), (d3, c3), (d4, b4), (d4, c4)}";
        Lexer lexer2 = new Lexer(new StringReader(sudoku));
        Parser parser2 = new Parser(lexer2);
        parser2.parse();
        Solver solver2 = new Solver(parser2.getVariables(), parser2.getConstraints());
        solver2.solutionsIterator().forEachRemaining(s -> System.out.println(s.toString()));
        System.out.println();
        System.out.println(solver2.backtrackingSearch().toString());
    }
}
