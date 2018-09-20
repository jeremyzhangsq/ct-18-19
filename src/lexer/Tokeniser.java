package lexer;

import lexer.Token.TokenClass;

import javax.xml.transform.ErrorListener;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;
    private int error = 0;
    public final static Map<String,TokenClass> RESERVED= new HashMap<>();
    static {
        RESERVED.put("int",TokenClass.INT);
        RESERVED.put("char",TokenClass.CHAR);
        RESERVED.put("void",TokenClass.VOID);
        RESERVED.put("if",TokenClass.IF);
        RESERVED.put("else",TokenClass.ELSE);
        RESERVED.put("while",TokenClass.WHILE);
        RESERVED.put("return",TokenClass.RETURN);
        RESERVED.put("struct",TokenClass.STRUCT);
        RESERVED.put("sizeof",TokenClass.SIZEOF);
    }
    public int getErrorCount() {
	return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    /*
     * To be completed
     */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        // ... to be completed
        // recognises the operator
        if (c == '+')
            return new Token(TokenClass.PLUS, line, column);
        else if (c == '-')
            return new Token(TokenClass.MINUS, line, column);
        else if (c == '*')
            return new Token(TokenClass.ASTERIX, line, column);
        else if (c == '/')
            return new Token(TokenClass.DIV, line, column);
        else if (c == '%')
            return new Token(TokenClass.REM, line, column);
        else if (c == '=')
            return new Token(TokenClass.ASSIGN, line, column);
        else if (c == '<')
            return new Token(TokenClass.LT, line, column);
        else if (c == '>')
            return new Token(TokenClass.GT, line, column);
        else if (c == '{')
            return new Token(TokenClass.LBRA, line, column);
        else if (c == '}')
            return new Token(TokenClass.RBRA, line, column);
        else if (c == '(')
            return new Token(TokenClass.LPAR, line, column);
        else if (c == ')')
            return new Token(TokenClass.RPAR, line, column);
        else if (c == '[')
            return new Token(TokenClass.LSBR, line, column);
        else if (c == ']')
            return new Token(TokenClass.RSBR, line, column);
        else if (c == ';')
            return new Token(TokenClass.SC, line, column);
        else if (c == ',')
            return new Token(TokenClass.COMMA, line, column);
        else if (c == '.')
            return new Token(TokenClass.DOT, line, column);
        else if (c == '&')
            return new Token(TokenClass.BIAND, line, column);
        else if (c == '|')
            return new Token(TokenClass.BIOR, line, column);
        //  recognize #include
        else if (c == '#'){
            StringBuilder sb = new StringBuilder();
            c = scanner.peek();
            while(!Character.isWhitespace(c)){
                sb.append(c);
                scanner.next();
                c = scanner.peek();
            }
            line = scanner.getLine();
            column = scanner.getColumn();
            if(sb.toString().equals("include"))
                return new Token(TokenClass.INCLUDE, line, column);
        }
        //  recognize identifier and keyword
        else if (Character.isLetter(c)){
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            c = scanner.peek();
            while(Character.isLetterOrDigit(c) || c == '_'){
                sb.append(c);
                scanner.next();
                c = scanner.peek();
            }
            line = scanner.getLine();
            column = scanner.getColumn();
            if(RESERVED.containsKey(sb.toString()))
                return new Token(RESERVED.get(sb.toString()), line, column);
            else
                return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
        }
        // recognize number
        else if (Character.isDigit(c)){
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            c = scanner.peek();
            while(Character.isDigit(c)){
                sb.append(c);
                scanner.next();
                c = scanner.peek();
            }
            line = scanner.getLine();
            column = scanner.getColumn();
            return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);
        }
        // recognize string_literal and char_literal
        else if(c == '"'|| c== '\''){
            return getLiteral(c);
        }

        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }

    private Token getLiteral(char ch) throws IOException{
        StringBuilder sb = new StringBuilder();
        sb.append(ch);
        char c = scanner.peek();
        while(c != ch){
            sb.append(c);
            scanner.next();
            c = scanner.peek();
        }
        sb.append(c);
        scanner.next();
        if(ch == '\'')
            return new Token(TokenClass.CHAR_LITERAL, sb.toString(), scanner.getLine(), scanner.getColumn());
        else if(ch == '"')
            return new Token(TokenClass.STRING_LITERAL, sb.toString(), scanner.getLine(), scanner.getColumn());
        return new Token(TokenClass.INVALID, scanner.getLine(), scanner.getColumn());
    }

}
