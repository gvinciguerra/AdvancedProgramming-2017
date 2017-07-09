package parser;

import model.Constraint;
import model.ImplicationConstraint;
import model.Variable;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;

public class Parser {
    private Token lookahead;
    private final Lexer lexer;
    private final List<Variable<String>> variables = new ArrayList<>();
    private final List<Constraint> constraints = new ArrayList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    // dsl ::= varsList constraints '\n' '!' constraints
    public void parse() throws IOException, ParseException {
        lookahead = lexer.next();
        varsList();
        checkDomainsDisjointness();
        constraints(true);
        match(Token.Type.LINE);
        match(Token.Type.BANG);
        constraints(false);
    }

    private void match(Token.Type type) throws IOException, ParseException {
        if (lookahead.getType() != type)
            throw new ParseException(type.toString() + " expected at line " + lexer.getLine(), 0);
        lookahead = lexer.next();
    }

    // varsList ::= ide '=' domain '\n' (ide '=' domain '\n')*
    private void varsList() throws IOException, ParseException {
        do {
            String name = lookahead.getLexeme();
            match(Token.Type.NAME);
            match(Token.Type.EQUAL);
            Set<String> domain = domain();
            variables.add(new Variable<>(name, domain));
            match(Token.Type.LINE);
        } while (lookahead.getType() == Token.Type.NAME);
    }

    // domain ::=  '{' ide (',' ide)* '}'
    private Set<String> domain() throws IOException, ParseException {
        List<String> elements = new ArrayList<>();
        match(Token.Type.LBRACE);
        do {
            elements.add(lookahead.getLexeme());
            match(Token.Type.NAME);
            if (lookahead.getType() == Token.Type.COMMA)
                match(Token.Type.COMMA);
            else
                break;
        } while (lookahead.getType() == Token.Type.NAME);
        match(Token.Type.RBRACE);
        return new HashSet<>(elements);
    }

    // constraints ::= '{' '}' | '{' '(' ide ',' ide ')'  (',' '(' ide ',' ide ')')* '}'
    private void constraints(boolean equality) throws IOException, ParseException {
        match(Token.Type.LBRACE);
        if (lookahead.getType() == Token.Type.RBRACE) {
            match(Token.Type.RBRACE);
            return;
        }
        do {
            match(Token.Type.LPAREN);
            String lvalue = lookahead.getLexeme();
            match(Token.Type.NAME);
            match(Token.Type.COMMA);
            String rvalue = lookahead.getLexeme();
            match(Token.Type.NAME);
            match(Token.Type.RPAREN);
            addConstraint(equality, lvalue, rvalue);
            if (lookahead.getType() == Token.Type.COMMA)
                match(Token.Type.COMMA);
            else
                break;
        } while (lookahead.getType() == Token.Type.LPAREN);
        match(Token.Type.RBRACE);
    }

    private void addConstraint(boolean equality, String lvalue, String rvalue) throws ParseException {
        Variable<String> lvariable = null;
        Variable<String> rvariable = null;
        for (Variable<String> v : variables) {
            if (lvariable == null && v.contains(lvalue))
                lvariable = v;
            if (rvariable == null && v.contains(rvalue))
                rvariable = v;
            if (lvariable != null && rvariable != null)
                break;
        }
        if (lvariable == null || rvariable == null)
            throw new ParseException(String.format("Value in (%s, %s) does not belong to any domain", lvalue, rvalue), 0);
        Predicate<String> filter = equality ? u -> !u.equals(rvalue) : v -> v.equals(rvalue);
        constraints.add(new ImplicationConstraint<>(lvariable, w -> w.equals(lvalue), rvariable, filter));
    }

    private void checkDomainsDisjointness() throws ParseException {
        int size = variables.stream().mapToInt(v -> v.getDomain().size()).sum();
        Set<String> union = new HashSet<>(size);
        for (Variable<String> v : variables) {
            if (!Collections.disjoint(union, v.getDomain()))
                throw new ParseException("Domains are not disjoint", 0);
            union.addAll(v.getDomain());
        }
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public List<Variable<String>> getVariables() {
        return variables;
    }
}
