package parser;

public class Token {
    public enum Type {LBRACE, RBRACE, LPAREN, RPAREN, LINE, BANG, EQUAL, COMMA, NAME}

    private String lexeme;
    private Type type;

    public Token(Type type) {
        this.type = type;
    }

    public Token(Type type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public Type getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }
}
