package parser;

import ast.*;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
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


    private Program parseProgram() {
        parseIncludes();
        List<StructTypeDecl> stds = parseStructDecls();
        List<VarDecl> vds = parseVarDecls();
        List<FunDecl> fds = parseFunDecls();
        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)){
            nextToken();
            expect(TokenClass.STRING_LITERAL);
        }
    }

    private List<StructTypeDecl> parseStructDecls() {
        // to be completed ...
        List<StructTypeDecl> sds = new ArrayList<>();
        while (accept(TokenClass.STRUCT) && lookAhead(1).tokenClass.equals(TokenClass.IDENTIFIER)
                && lookAhead(2).tokenClass.equals(TokenClass.LBRA)){
            StructType st = parseStructType();
            expect(TokenClass.LBRA);
            List<VarDecl> vds = parseVarDecls();
            expect(TokenClass.RBRA);
            expect(TokenClass.SC);
            sds.add(new StructTypeDecl(st,vds));
        }

        return sds;
    }


    private List<VarDecl> parseVarDecls(){
        // to be completed ...
        List<VarDecl> vds = new ArrayList<>();
        Type t;
        Token cur;
        if (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT)){
            while(!accept(TokenClass.RBRA)){
                t = parseType();
                cur = expect(TokenClass.IDENTIFIER);
                expect(TokenClass.SC);
                vds.add(new VarDecl(t, cur.data));
            }

        }
        return vds;
    }
    private BaseType parseBaseType() {
        if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID)){
            Token cur = expect(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID);
            switch (cur.tokenClass){
                case INT: return BaseType.INT;
                case CHAR: return BaseType.CHAR;
                case VOID: return BaseType.VOID;
                default: return null;
            }
        }
        return null;
    }
    private StructType parseStructType(){
        if(accept(TokenClass.STRUCT)){
            nextToken();
            Token cur = expect(TokenClass.IDENTIFIER);
            return new StructType(cur.data);
        }
        return null;
    }
    private Type parseType(){
        Type t;
        if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID)){
            t =  parseBaseType();
        }
        else if (accept(TokenClass.STRUCT)) {
            t = parseStructType();
        }
        else {
            error(token.tokenClass);
            if (!accept(TokenClass.RBRA))nextToken();
            return null;
        }
        if (accept(TokenClass.ASTERIX)){
            nextToken();
            t = new PointerType(t);
        }
        else if (accept(TokenClass.LSBR)){
            nextToken();
            Token cur = expect(TokenClass.INT_LITERAL);
            expect(TokenClass.RSBR);
            t = new ArrayType(t, Integer.valueOf(cur.data));
        }
        return t;
    }

    private List<FunDecl> parseFunDecls(){
        // to be completed ...
        List<FunDecl> fds = new ArrayList<>();
        return fds;
    }





//    private void OneVarDecls() {
//        Token ahead = lookAhead(1);
//        if(accept(TokenClass.IDENTIFIER)){
//            nextToken();
//            if (ahead.tokenClass.equals(TokenClass.LSBR)){
//                nextToken();
//                expect(TokenClass.INT_LITERAL);
//                expect(TokenClass.RSBR);
//                expect(TokenClass.SC);
//            }
//            else if (ahead.tokenClass.equals(TokenClass.SC)) nextToken();
//            else {
//                error(token.tokenClass);
//                if (!accept(TokenClass.RBRA)) nextToken();
//            }
//        }
//        else {
//            error(token.tokenClass);
//            if (!accept(TokenClass.RBRA)) nextToken();
//        }
//
//    }

//    private void parseStatement(){
//        if(accept(TokenClass.LBRA)){
//            parseBlock();
//        }
//        else if (accept(TokenClass.WHILE, TokenClass.IF)){
//            TokenClass cur = expect(TokenClass.WHILE, TokenClass.IF).tokenClass;
//            expect(TokenClass.LPAR);
//            parseExp();
//            expect(TokenClass.RPAR);
//            parseStatement();
//            if (cur.equals(TokenClass.IF) && accept(TokenClass.ELSE)){
//                nextToken();
//                parseStatement();
//            }
//        }
//
//        else if (accept(TokenClass.RETURN)){
//            nextToken();
//            if (accept(TokenClass.SC)){
//                nextToken();
//            }
//            else {
//                parseExp();
//                expect(TokenClass.SC);
//            }
//        }
//        //exp "=" exp ";"   |    exp ";"
//        else {
//
//            parseExp();
//            if (accept(TokenClass.ASSIGN)){
//                nextToken();
//                parseExp();
//                expect(TokenClass.SC);
//            }
//            else if(accept(TokenClass.SC)){
//                nextToken();
//            }
//            else {
//                error(token.tokenClass);
//                if(!accept(TokenClass.RBRA)) nextToken();
//            }
//        }
////        }
//    }


//    private void parseExp(){
//        Token ahead;
//        if(accept(TokenClass.LPAR)){
//            nextToken();
//            if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT)){
//                parseType();
//                expect(TokenClass.RPAR);
//                parseExp();
//            }
//            else {
//                parseExp();
//                expect(TokenClass.RPAR);
//            }
//        }
//        else if(accept(TokenClass.CHAR_LITERAL,TokenClass.STRING_LITERAL,TokenClass.INT_LITERAL)) nextToken();
//        else if(accept(TokenClass.IDENTIFIER)) {
//            ahead = lookAhead(1);
//            if(ahead.tokenClass.equals(TokenClass.LPAR)){
//                parseFunCall();;
//            }
//            else nextToken();
//        }
//        else if (accept(TokenClass.SIZEOF)){
//            nextToken();
//            expect(TokenClass.LPAR);
//            parseType();
//            expect(TokenClass.RPAR);
//        }
//        else if (accept(TokenClass.MINUS,TokenClass.ASTERIX)){
//            nextToken();
//            parseExp();
//        }
//        else {
//            error(token.tokenClass);
//            nextToken();
//            return;
//        }
//
//        parseExprStar();
//
//    }
//
////    private void parseExprStar(){
//        if(accept(TokenClass.PLUS,TokenClass.DIV,TokenClass.MINUS,
//                TokenClass.ASTERIX,TokenClass.GT,TokenClass.GE,TokenClass.LE,
//                TokenClass.LT,TokenClass.NE,TokenClass.EQ,TokenClass.OR,
//                TokenClass.AND,TokenClass.REM,TokenClass.LSBR,TokenClass.DOT )){
//            if (accept(TokenClass.LSBR)){
//                nextToken();
//                parseExp();
//                expect(TokenClass.RSBR);
//            }
//            else if (accept(TokenClass.DOT)){
//                nextToken();
//                expect(TokenClass.IDENTIFIER);
//            }
//            else{
//                nextToken();
//                parseExp();
//            }
//            parseExprStar();
//        }
//    }

//    private void parseFunCall(){
//        expect(TokenClass.IDENTIFIER);
//        expect(TokenClass.LPAR);
//        if (!accept(TokenClass.RPAR)){
//            parseExp();
//            parseArgRep();
//        }
//        expect(TokenClass.RPAR);
//
//    }

//    private void parseArgRep() {
//        if (accept(TokenClass.COMMA)) {
//            nextToken();
//            parseExp();
//            parseArgRep();
//        }
//    }
//    private void parseParameter(){
//        if (accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT)) {
//            parseType();
//            expect(TokenClass.IDENTIFIER);
//            parseArgs();
//        }
//    }
//    private void parseArgs(){
//        if (accept(TokenClass.COMMA)){
//            nextToken();
//            parseType();
//            expect(TokenClass.IDENTIFIER);
//            parseArgs();
//        }
//    }


//    private void parseBlock() {
//        if (accept(TokenClass.LBRA)){
//            nextToken();
//            while (!accept(TokenClass.RBRA,TokenClass.EOF)){
//                if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT))
//                {
//                    parseType();
//                    OneVarDecls();
//                }
//                else
//                    parseStatement();
//            }
//            expect(TokenClass.RBRA);
//        }
//    }

    // to be completed ...

}
