package parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

public class Lexer {
    private StreamTokenizer tokenizer;

    public Lexer(Reader reader) {
        tokenizer = new StreamTokenizer(reader);
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('_', '_');
        tokenizer.ordinaryChar('!');
        tokenizer.ordinaryChar('=');
        tokenizer.ordinaryChar(',');
        tokenizer.ordinaryChar('{');
        tokenizer.ordinaryChar('}');
        tokenizer.ordinaryChar('(');
        tokenizer.ordinaryChar(')');
        tokenizer.eolIsSignificant(true);
    }

    public Token next() throws IOException {
        switch (tokenizer.nextToken()) {
            case '!': return new Token(Token.Type.BANG);
            case '=': return new Token(Token.Type.EQUAL);
            case ',': return new Token(Token.Type.COMMA);
            case '{': return new Token(Token.Type.LBRACE);
            case '}': return new Token(Token.Type.RBRACE);
            case '(': return new Token(Token.Type.LPAREN);
            case ')': return new Token(Token.Type.RPAREN);
            case StreamTokenizer.TT_EOL: return new Token(Token.Type.LINE);
            case StreamTokenizer.TT_WORD: return new Token(Token.Type.NAME, tokenizer.sval);
            default: return null;
        }
    }
}
