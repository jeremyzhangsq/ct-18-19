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
        List<StructTypeDecl> stds = new ArrayList<>();
        List<VarDecl> vds = new ArrayList<>();
        List<FunDecl> fds = new ArrayList<>();
        Type t;
        String name;
        while (!accept(TokenClass.EOF)){
            if (accept(TokenClass.INCLUDE))
                parseIncludes();
            else if (accept(TokenClass.STRUCT) && lookAhead(1).tokenClass.equals(TokenClass.IDENTIFIER)
                    && lookAhead(2).tokenClass.equals(TokenClass.LBRA)){
                stds.add(parseStructDecls());
            }
            else{
                t = parseType();
                Token tok = expect(TokenClass.IDENTIFIER);
                if (tok!=null)
                    name = tok.data;
                else
                    name = "invalid";
                if (accept(TokenClass.SC)){
                    vds.add(new VarDecl(t,name));
                    expect(TokenClass.SC);
                }
                else if (accept(TokenClass.LPAR))
                    fds.add(parseFunDecls(t,name));
                else {
                    error(token.tokenClass);
                    if (!accept(TokenClass.EOF)) nextToken();
                }
            }
        }
        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }
    //    private void parseProgram() {
//        while (!accept(TokenClass.EOF)){
//            if (accept(TokenClass.INCLUDE))
//                parseIncludes();
//            else {
//                parseType();
//                Token t = lookAhead(1);
//                if (accept(TokenClass.LBRA))
//                    parseStructDecls();
//                else if (accept(TokenClass.IDENTIFIER) && (t.tokenClass.equals(TokenClass.SC)||t.tokenClass.equals(TokenClass.LSBR)))
//                    OneVarDecls();
//                else if (accept(TokenClass.IDENTIFIER) && t.tokenClass.equals(TokenClass.LPAR))
//                    parseFunDecls();
//                else {
//                    error(token.tokenClass);
//                    return;
//                }
//            }
//        }
//        expect(TokenClass.EOF);
//    }
//

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        nextToken();
        expect(TokenClass.STRING_LITERAL);

    }

    private StructTypeDecl parseStructDecls() {
        // to be completed ...
        StructType st = parseStructType();
        expect(TokenClass.LBRA);
        List<VarDecl> vds = new ArrayList<>();
        while (!accept(TokenClass.RBRA)){
            vds.add(parseVarDecls());
            expect(TokenClass.SC);
        }
        expect(TokenClass.RBRA);
        expect(TokenClass.SC);

        return new StructTypeDecl(st,vds);
    }
//    private void parseStructDecls() {
//        // to be completed ...
//        expect(TokenClass.LBRA);
//        parseType();
//        OneVarDecls();
//        while (!accept(TokenClass.RBRA)){
//            parseType();
//            OneVarDecls();
//        }
//        expect(TokenClass.RBRA);
//        expect(TokenClass.SC);
//
//    }

    private VarDecl parseVarDecls(){
        // to be completed ...
        Type t;
        Token cur;
        t = parseType();
        cur = expect(TokenClass.IDENTIFIER);
        if(cur != null)
            return new VarDecl(t, cur.data);
        else
            return null;
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
            if (cur!=null)
                return new StructType(cur.data);
            else
                return new StructType("Invalid");
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

    private FunDecl parseFunDecls(Type t, String name){
        // to be completed ...
        List<VarDecl> vds = new ArrayList<>();
        expect(TokenClass.LPAR);
        if (accept(TokenClass.INT,TokenClass.VOID,TokenClass.CHAR,TokenClass.STRUCT)){
            vds.add(parseVarDecls());
            while (accept(TokenClass.COMMA)){
                nextToken();
                vds.add(parseVarDecls());
            }
        }
        expect(TokenClass.RPAR);
        Block b = parseBlock();
        return new FunDecl(t,name,vds,b);
    }
    private Block parseBlock(){
        expect(TokenClass.LBRA);
        List<VarDecl> vds = new ArrayList<>();
        List<Stmt> stmts = new ArrayList<>();
        while (!accept(TokenClass.RBRA,TokenClass.EOF)){
            if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT)){
                vds.add(parseVarDecls());
                expect(TokenClass.SC);
            }

            else
                stmts.add(parseStatement());
        }
        expect(TokenClass.RBRA);
        return new Block(vds,stmts);
    }
    private Stmt parseStatement(){
        if(accept(TokenClass.LBRA)){
            return parseBlock();
        }
        else if (accept(TokenClass.WHILE, TokenClass.IF)){
            TokenClass cur = expect(TokenClass.WHILE, TokenClass.IF).tokenClass;
            expect(TokenClass.LPAR);
            Expr e = parseExp();
            expect(TokenClass.RPAR);
            Stmt st = parseStatement();
            if (cur.equals(TokenClass.WHILE))
                return new While(e,st);
            else {
                if (accept(TokenClass.ELSE)){
                    nextToken();
                    Stmt op = parseStatement();
                    return new If(e,st,op);
                }
                else return new If(e,st);
            }
        }
        else if (accept(TokenClass.RETURN)){
            nextToken();
            if (accept(TokenClass.SC)){
                nextToken();
                return new Return();
            }
            else {
                Expr e = parseExp();
                expect(TokenClass.SC);
                return new Return(e);
            }
        }
        else {
            Expr e1 = parseExp();
            if (accept(TokenClass.ASSIGN)){
                nextToken();
                Expr e2 = parseExp();
                expect(TokenClass.SC);
                return new Assign(e1,e2);
            }
            else if (accept(TokenClass.SC)){
                nextToken();
                return new ExprStmt(e1);
            }
            else {
                error(token.tokenClass);
                if(!accept(TokenClass.RBRA)) nextToken();
                return null;
            }
        }
    }

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
    private Expr parseExp(){
        Token ahead;
        Expr expr;
        if(accept(TokenClass.LPAR)){
            nextToken();
            if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID, TokenClass.STRUCT)){
                Type t = parseType();
                expect(TokenClass.RPAR);
                Expr e = parseExp();
                expr = new TypecastExpr(t,e);
            }
            else {
                expr = parseExp();
                expect(TokenClass.RPAR);
            }
        }
        else if(accept(TokenClass.CHAR_LITERAL)){
            Token t = expect(TokenClass.CHAR_LITERAL);
            expr = new ChrLiteral(t.data.charAt(0));
        }
        else if(accept(TokenClass.INT_LITERAL)) {
            Token t = expect(TokenClass.INT_LITERAL);
            expr = new IntLiteral(Integer.valueOf(t.data));
        }
        else if(accept(TokenClass.STRING_LITERAL)) {
            Token t = expect(TokenClass.STRING_LITERAL);
            expr = new StrLiteral(t.data);
        }
        else if(accept(TokenClass.IDENTIFIER)) {
            ahead = lookAhead(1);
            if(ahead.tokenClass.equals(TokenClass.LPAR)){
                expr = parseFunCall();
            }
            else {
                Token t = expect(TokenClass.IDENTIFIER);
                expr = new VarExpr(t.data);
            }
        }
        else if (accept(TokenClass.SIZEOF)){
            nextToken();
            expect(TokenClass.LPAR);
            Type t = parseType();
            expect(TokenClass.RPAR);
            expr = new SizeOfExpr(t);
        }
        else if (accept(TokenClass.ASTERIX)){
            nextToken();
            Expr e = parseExp();
            expr = new ValueAtExpr(e);
        }
        else if (accept(TokenClass.MINUS)) {
            nextToken();
            Expr e = parseExp();
            expr = new BinOp(new IntLiteral(0), Op.SUB, e);
        }
        else {
            error(token.tokenClass);
            nextToken();
            return null;
        }

        return parseExprStar(expr);

    }
    private Expr parseTerm(){
        Expr lhs = parseFactor();
        if (accept(TokenClass.ASTERIX,TokenClass.DIV)){
            Op op;
            if (accept(TokenClass.ASTERIX))
                op = Op.MUL;
            else
                op = Op.DIV;
            nextToken();
            Expr rhs = parseTerm();
            return new BinOp(lhs,op,rhs);
        }
        return lhs;
    }
    private Expr parseFactor(){
        if (accept(TokenClass.LPAR)
                && !lookAhead(1).tokenClass.equals(TokenClass.INT)
                && !lookAhead(1).tokenClass.equals(TokenClass.CHAR)
                && !lookAhead(1).tokenClass.equals(TokenClass.VOID)
                && !lookAhead(1).tokenClass.equals(TokenClass.STRUCT)){
            Expr e = parseExp();
            expect(TokenClass.RPAR);
            return e;
        }
        else if (accept(TokenClass.INT_LITERAL)){
            Token t = expect(TokenClass.INT_LITERAL);
            return new IntLiteral(Integer.valueOf(t.data));
        }
        else if (accept(TokenClass.IDENTIFIER)){
            Token t = expect(TokenClass.IDENTIFIER);
            return new VarExpr(t.data);

        }
        else return null;
    }
//    private void parseExprStar(){
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

    private Expr parseExprStar(Expr expr){
        Expr newExpr;
        if(accept(TokenClass.PLUS,TokenClass.DIV,TokenClass.MINUS,
                TokenClass.ASTERIX,TokenClass.GT,TokenClass.GE,TokenClass.LE,
                TokenClass.LT,TokenClass.NE,TokenClass.EQ,TokenClass.OR,
                TokenClass.AND,TokenClass.REM,TokenClass.LSBR,TokenClass.DOT )){
            if (accept(TokenClass.LSBR)){
                nextToken();
                Expr e = parseExp();
                expect(TokenClass.RSBR);
                newExpr = new ArrayAccessExpr(expr, e);

            }
            else if (accept(TokenClass.DOT)){
                nextToken();
                Token t = expect(TokenClass.IDENTIFIER);
                newExpr = new FieldAccessExpr(expr, t.data);
            }
            else{
                Token t = expect(TokenClass.PLUS,TokenClass.DIV,TokenClass.MINUS,
                        TokenClass.ASTERIX,TokenClass.GT,TokenClass.GE,TokenClass.LE,
                        TokenClass.LT,TokenClass.NE,TokenClass.EQ,TokenClass.OR,
                        TokenClass.AND,TokenClass.REM);
                Op op;
                if (t.tokenClass == TokenClass.PLUS)  op = Op.ADD;
                else if (t.tokenClass == TokenClass.MINUS) op = Op.SUB;
                else if (t.tokenClass == TokenClass.ASTERIX) op = Op.MUL;
                else if (t.tokenClass == TokenClass.DIV) op = Op.DIV;
                else if (t.tokenClass == TokenClass.GT) op = Op.GT;
                else if (t.tokenClass == TokenClass.GE) op = Op.GE;
                else if (t.tokenClass == TokenClass.LE) op = Op.LE;
                else if (t.tokenClass == TokenClass.LT) op = Op.LT;
                else if (t.tokenClass == TokenClass.NE) op = Op.NE;
                else if (t.tokenClass == TokenClass.EQ) op = Op.EQ;
                else if (t.tokenClass == TokenClass.OR) op = Op.OR;
                else if (t.tokenClass == TokenClass.AND) op = Op.AND;
                else if (t.tokenClass == TokenClass.REM) op = Op.MOD;
                else op = Op.ADD;
                Expr rhs = parseExp();
                newExpr = new BinOp(expr, op, rhs);
            }
            return parseExprStar(newExpr);
        }
        return expr;
    }

    private FunCallExpr parseFunCall(){
        Token name = expect(TokenClass.IDENTIFIER);
        List<Expr> exps = new ArrayList<>();
        expect(TokenClass.LPAR);
        if (!accept(TokenClass.RPAR)){
            exps.add(parseExp());
            while (accept(TokenClass.COMMA)){
                nextToken();
                exps.add(parseExp());
            }
        }
        expect(TokenClass.RPAR);
        return new FunCallExpr(name.data,exps);
    }


//
//    private void parseStructType(){
//        expect(TokenClass.STRUCT);
//        expect(TokenClass.IDENTIFIER);
//    }
//
//    private void parseType(){
//        if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID)) nextToken();
//        else if (accept(TokenClass.STRUCT)) parseStructType();
//        else {
//            error(token.tokenClass);
//            if (!accept(TokenClass.RBRA))nextToken();
//            return;
//        }
//        if (accept(TokenClass.ASTERIX)){
//            nextToken();
//        }
//    }
//
//

//
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
//
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
////            if(accept(TokenClass.IDENTIFIER) && lookAhead(1).tokenClass.equals(TokenClass.LPAR)){
////                parseFunCall();
////                expect(TokenClass.SC);
////            }
////            else {
//            parseExp();
//            if (accept(TokenClass.ASSIGN)){
//                nextToken();
//                parseExp();
//                expect(TokenClass.SC);
//            }
//            else {
//                error(token.tokenClass);
//                if(!accept(TokenClass.RBRA)) nextToken();
//            }
//        }
//    }
//}
//
//
//
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
//
//    private void parseArgRep(){
//        if (accept(TokenClass.COMMA)){
//            nextToken();
//            parseExp();
//            parseArgRep();
//        }
//    }
//
//    private void parseFunDecls() {
//        // to be completed ...
//        expect(TokenClass.IDENTIFIER);
//        expect(TokenClass.LPAR);
//        parseParameter();
//        expect(TokenClass.RPAR);
//        parseBlock();
//
//    }
//
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
//
//
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

}
