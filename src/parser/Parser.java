package parser;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.LinkedList;
import java.util.Queue;


/**
 * @author cdubach
 */
public class Parser {

    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public void parse() {
        // get the first token
        nextToken();

        parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }
        error(expected);
        return null;
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }


    private void parseProgram() {
        parseIncludes();
        parseStructDecls();
        Token t = lookAhead(2);
        if(t.tokenClass.equals(TokenClass.SC)||t.tokenClass.equals(TokenClass.LSBR))
            parseVarDecls();
        if(t.tokenClass.equals(TokenClass.LPAR))
            parseFunDecls();
        expect(TokenClass.EOF);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private void parseStructType(){
        expect(TokenClass.STRUCT);
        expect(TokenClass.IDENTIFIER);
    }

    private void parseType(){
        if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT)) nextToken();
        else if (accept(TokenClass.STRUCT)) parseStructType();
        else error(token.tokenClass);
        if (accept(TokenClass.ASTERIX)){
            nextToken();
        }
    }

    private void parseStructDecls() {
        // to be completed ...
        if (accept(TokenClass.STRUCT)){
            parseStructType();
            expect(TokenClass.LBRA);
            parseType();
            OneVarDecls();
            parseVarDecls();
            expect(TokenClass.RBRA);
            expect(TokenClass.SC);
            parseStructDecls();
        }

    }


    private void parseVarDecls() {
        // to be completed ...
        if (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT)){
            parseType();
            OneVarDecls();
            parseVarDecls();
        }
    }
    private void OneVarDecls() {
        try{
            Token ahead = lookAhead(1);
            expect(TokenClass.IDENTIFIER);
            if (ahead.tokenClass.equals(TokenClass.LSBR)){
                nextToken();
                expect(TokenClass.INT_LITERAL);
                expect(TokenClass.RSBR);
                expect(TokenClass.SC);
            }
            else if (ahead.tokenClass.equals(TokenClass.SC)) expect(TokenClass.SC);
            else error(ahead.tokenClass);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void parseStatement(){

    }


    private void parseFunDecls() {
        // to be completed ...
        if (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT)){
            parseType();
            expect(TokenClass.IDENTIFIER);
            expect(TokenClass.LPAR);
            parseParameter();
            expect(TokenClass.RPAR);
            parseBlock();
        }
    }

    private void parseParameter(){
        if (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT)) {
            parseType();
            expect(TokenClass.IDENTIFIER);
            parseArgs();
        }
    }
    private void parseArgs(){
        if (accept(TokenClass.COMMA)){
            nextToken();
            parseType();
            expect(TokenClass.IDENTIFIER);
            parseArgs();
        }
    }
    private void parseBlock() {
        if (accept(TokenClass.LBRA)){
            nextToken();
            parseVarDecls();
            parseStatement();
            expect(TokenClass.RBRA);
        }
    }

    // to be completed ...

}
