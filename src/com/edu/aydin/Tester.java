package com.edu.aydin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Tester {

    public static void main(String[] args) throws Exception {

        Parser parser = new Parser();
        parser.parse();
        System.out.println("Parser tree structure is : ");
        parser.printTree(parser.parserTree.root,"");
        System.out.println("Program result is : ");
        parser.eval();

    }
}

class Parser{

    Scanner scanner;
    Token currentToken;
    ParserTree parserTree;
    ParserTreeNode currentNode;

    static Map<String, Integer> parserMemory = new HashMap<>();

    void printTree(ParserTreeNode parserTreeNode, String leaf){
        System.out.println(leaf+parserTreeNode.nodeName);
        for (ParserTreeNode node: parserTreeNode.children){
            printTree(node,leaf+"\\");
        }
    }

    void eval(){
        parserTree.root.evaluate();
    }

    Parser(){
        this.scanner = new Scanner("program1.txt");
        this.parserTree = new ParserTree();
    }

    void parse() throws Exception {
        p();
        System.out.println("Program is syntaticaly correct.");

    }


    void p() throws Exception {
        this.currentToken = scanner.nextToken();
        currentNode = parserTree.root;
        while (!this.currentToken.getType().equals(TokenType.END_OF_FILE)){
            s();
        }

    }

    void s() throws Exception {
        SNode sNode = new SNode();
        ParserTreeNode tempNode = currentNode;
        currentNode = sNode;

        switch (this.currentToken.tokenType){
            case IF_BEG:
                c();
                break;
            case WHILE_BEG:
                w();
                break;
            case IDENTIFIER:
                a();
                break;
            case INPUT:
                i();
                break;
            case OUTPUT:
                o();
                break;
            default:
                throw new Exception("Program is syntactically incorrect");
        }

        currentNode = tempNode;
        currentNode.children.add(sNode);
    }


    void c() throws Exception {
        CNode cNode = new CNode();
        ParserTreeNode tempNode = currentNode;
        currentNode = cNode;

        if(this.currentToken.tokenType.equals(TokenType.IF_BEG)){
            this.currentToken = scanner.nextToken();
            e();
            if(this.currentToken.tokenType.equals(TokenType.QUESTION_MARK)){
                this.currentToken = scanner.nextToken();
                while (!(this.currentToken.tokenType.equals(TokenType.IF_END)
                        || this.currentToken.tokenType.equals(TokenType.COLON))){
                    s();
                }

                if(this.currentToken.tokenType.equals(TokenType.IF_END)){

                    this.currentToken = scanner.nextToken();
                }else{
                    this.currentToken = scanner.nextToken();

                    while (!this.currentToken.tokenType.equals(TokenType.IF_END))
                        s();

                    this.currentToken = scanner.nextToken();
                }
            }else{
                throw new Exception("program is syntactically INCORRECT: ? is missing");
            }
        }else{
            throw new Exception("program is syntactically INCORRECT: Missing Condition Beginning");
        }

        currentNode = tempNode;
        currentNode.children.add(cNode);
    }

    void w() throws Exception {
        WNode wNode = new WNode();
        ParserTreeNode tempNode = currentNode;
        currentNode = wNode;
        this.currentToken = scanner.nextToken();
        e();

        if (this.currentToken.tokenType.equals(TokenType.QUESTION_MARK)){

            this.currentToken = scanner.nextToken();

            while (! this.currentToken.tokenType.equals(TokenType.WHILE_END)){
                s();
            }

            this.currentToken = scanner.nextToken();

        }else{
            throw new Exception("program is syntactically INCORRECT: ? is missing");
        }

        currentNode = tempNode;
        currentNode.children.add(wNode);

    }

    void a() throws Exception {

        ANode aNode = new ANode();
        ParserTreeNode tempNode = currentNode;
        currentNode = aNode;


        if (this.currentToken.tokenType.equals(TokenType.IDENTIFIER)){

            l();
            this.currentToken = scanner.nextToken();
            if (this.currentToken.tokenType.equals(TokenType.EQUAL)){

                this.currentToken = scanner.nextToken();
                e();

                if (this.currentToken.tokenType.equals(TokenType.SEMICOLON)){
                    this.currentToken = scanner.nextToken();
                }else{
                    throw new Exception("program is syntactically INCORRECT: Missing semicolon");
                }
            }else{
                throw new Exception("program is syntactically INCORRECT: Missing equal");
            }
        }else{
            throw new Exception("program is syntactically INCORRECT: Missing Identifier");
        }
        currentNode = tempNode;
        currentNode.children.add(aNode);
    }

    void o() throws Exception {

        ONode oNode = new ONode();
        ParserTreeNode tempNode = currentNode;
        currentNode = oNode;

        if(this.currentToken.tokenType.equals(TokenType.OUTPUT)){

            this.currentToken = scanner.nextToken();
            e();

            if (this.currentToken.tokenType.equals(TokenType.SEMICOLON)){
                this.currentToken = scanner.nextToken();
            }else{
                throw new Exception("program is syntactically INCORRECT: Missing semicolon");
            }
        }else{
            throw new Exception("program is syntactically INCORRECT: Missing output");
        }
        currentNode = tempNode;
        currentNode.children.add(oNode);
    }

    void i() throws Exception {
        INode iNode = new INode();
        ParserTreeNode tempNode = currentNode;
        currentNode = iNode;

        if(this.currentToken.tokenType.equals(TokenType.INPUT)){

            this.currentToken = scanner.nextToken();
            l();
            this.currentToken = scanner.nextToken();

            if(this.currentToken.tokenType.equals(TokenType.SEMICOLON)){
                this.currentToken = scanner.nextToken();
            }else{
                throw new Exception("program is syntactically INCORRECT: Missing semicolon");
            }
        }else{
            throw new Exception("program is syntactically INCORRECT: Missing input");
        }
        currentNode = tempNode;
        currentNode.children.add(iNode);
    }

    void e() throws Exception {

        ENode eNode = new ENode();
        ParserTreeNode tempNode = currentNode;
        currentNode = eNode;

        t();
        while (this.currentToken.tokenType.equals(TokenType.PLUS) || this.currentToken.tokenType.equals(TokenType.MINUS)){

            eNode.operatorTokenList.add(this.currentToken);
            this.currentToken = scanner.nextToken();
            t();
        }
        currentNode = tempNode;
        currentNode.children.add(eNode);
    }

    void t() throws Exception {
        TNode tNode = new TNode();
        ParserTreeNode tempNode = currentNode;
        currentNode = tNode;

        u();
        while (this.currentToken.tokenType.equals(TokenType.MULTIPLY) || this.currentToken.tokenType.equals(TokenType.DIVISION) || this.currentToken.tokenType.equals(TokenType.REMAINDER)){
            tNode.operatorTokenList.add(this.currentToken);
            this.currentToken = scanner.nextToken();
            u();
        }
        currentNode = tempNode;
        currentNode.children.add(tNode);
    }

    void u() throws Exception {
        UNode uNode = new UNode();
        ParserTreeNode tempNode = currentNode;
        currentNode = uNode;

        f();
        this.currentToken = scanner.nextToken();
        if (this.currentToken.tokenType.equals(TokenType.EXPONENT)){
            u();
        }
        currentNode = tempNode;
        currentNode.children.add(uNode);
    }

    void f() throws Exception {
        FNode fNode = new FNode();
        ParserTreeNode tempNode = currentNode;
        currentNode = fNode;

        if (this.currentToken.tokenType.equals(TokenType.PARENTHESES_BEG)){
            e();
        }else if(this.currentToken.tokenType.equals(TokenType.IDENTIFIER)){
            l();
        }else if(this.currentToken.tokenType.equals(TokenType.NUMBER)){
            d();
        }else {
            throw new Exception("Invalid grammar in F.");
        }
        currentNode = tempNode;
        currentNode.children.add(fNode);
    }

    void l(){
        LNode lNode = new LNode(currentToken);
        ParserTreeNode tempNode = currentNode;
        currentNode = lNode;
        currentNode = tempNode;
        currentNode.children.add(lNode);

    }

    void d(){
        DNode dNode = new DNode(currentToken);
        ParserTreeNode tempNode = currentNode;
        currentNode = dNode;
        currentNode = tempNode;
        currentNode.children.add(dNode);
    }

}

class ParserTreeNode{
    ArrayList<ParserTreeNode> children;
    String nodeName;
    ParserTreeNode(String nodeName){
        this.nodeName = nodeName;
        this.children = new ArrayList<>();
    }

    Token evaluate(){
        return null;
    }

}

class ParserTree{
    PNode root;
    ParserTree(){
        this.root = new PNode();
    }

}

class PNode extends ParserTreeNode{
    PNode() {
        super("P");
    }

    Token evaluate(){
        for(ParserTreeNode node : this.children)
            node.evaluate();
        return null;
    }

}


class SNode extends ParserTreeNode{

    SNode() {
        super("S");

    }
    Token evaluate(){
        return this.children.get(0).evaluate();
    }

}

class CNode extends ParserTreeNode{

    CNode() {
        super("C");

    }
    Token evaluate(){
        if(this.children.size() == 2 && Integer.parseInt(this.children.get(0).evaluate().text) != 0){
            this.children.get(1).evaluate();
        }else if(this.children.size() == 3 && Integer.parseInt(this.children.get(0).evaluate().text) != 0){
            this.children.get(1).evaluate();
        }else{
            this.children.get(2).evaluate();
        }
        return null;
    }

}

class WNode extends ParserTreeNode{

    WNode() {
        super("W");
    }

    Token evaluate(){
        while (Integer.parseInt(this.children.get(0).evaluate().text) != 0){
            for(int i=1; i<this.children.size(); i++){
                this.children.get(i).evaluate();
            }
        }
        return null;
    }
}

class ANode extends ParserTreeNode{

    ANode() {
        super("A");
    }

    Token evaluate(){
        Token token = this.children.get(0).evaluate();
        Parser.parserMemory.put(token.text,Integer.parseInt(this.children.get(1).evaluate().text));
        return token;
    }

}

class ONode extends ParserTreeNode{

    ONode() {
        super("O");
    }

    Token evaluate(){
        Token token = this.children.get(0).evaluate();
        System.out.println(Parser.parserMemory.get(token.text));
        return token;
    }


}

class INode extends ParserTreeNode{

    INode() {
        super("I");
    }

    Token evaluate(){
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int input = scanner.nextInt();
        LNode lNode = (LNode) this.children.get(0);
        Parser.parserMemory.put(lNode.token.text,input);
        return lNode.token;
    }

}

class ENode extends ParserTreeNode{

    ArrayList<Token> operatorTokenList;
    ENode() {
        super("E");
        this.operatorTokenList = new ArrayList<>();
    }

    Token evaluate(){

        if (this.children.size() == 1){
            return this.children.get(0).evaluate();
        }else{
            int result = 0;
            for (int i=0; i<this.children.size(); i++){
                Token token = this.children.get(i).evaluate();
                if (i==0){
                    if (token.tokenType.equals(TokenType.IDENTIFIER)){
                        result = Parser.parserMemory.get(token.text);
                    }else {
                        result = Integer.parseInt(token.text);
                    }
                }else{
                    switch (operatorTokenList.get(i-1).tokenType){
                        case PLUS:
                            if (token.tokenType.equals(TokenType.IDENTIFIER)){
                                result += Parser.parserMemory.get(token.text);
                            }else {
                                result += Integer.parseInt(token.text);
                            }
                            break;
                        case MINUS:
                            if (token.tokenType.equals(TokenType.IDENTIFIER)){
                                result -= Parser.parserMemory.get(token.text);
                            }else {
                                result -= Integer.parseInt(token.text);
                            }
                            break;
                    }
                }
            }
            return new NumberToken(String.valueOf(result));
        }
    }

}

class TNode extends ParserTreeNode{
    ArrayList<Token> operatorTokenList;
    TNode() {
        super("T");
        this.operatorTokenList = new ArrayList<>();
    }

    Token evaluate(){
        if (this.children.size() == 1){
            return this.children.get(0).evaluate();
        }else{
            int result = 0;
            for (int i=0; i<this.children.size(); i++){
                Token token = this.children.get(i).evaluate();
                if (i==0){
                    if (token.tokenType.equals(TokenType.IDENTIFIER)){
                        result = Parser.parserMemory.get(token.text);
                    }else {
                        result = Integer.parseInt(token.text);
                    }
                }else{
                    switch (operatorTokenList.get(i-1).tokenType){

                        case MULTIPLY:
                            if (token.tokenType.equals(TokenType.IDENTIFIER)){
                                result *= Parser.parserMemory.get(token.text);
                            }else {
                                result *= Integer.parseInt(token.text);
                            }
                            break;
                        case DIVISION:
                            if (token.tokenType.equals(TokenType.IDENTIFIER)){
                                result /= Parser.parserMemory.get(token.text);
                            }else {
                                result /= Integer.parseInt(token.text);
                            }
                            break;
                        case REMAINDER:
                            if (token.tokenType.equals(TokenType.IDENTIFIER)){
                                result %= Parser.parserMemory.get(token.text);
                            }else {
                                result %= Integer.parseInt(token.text);
                            }
                            break;
                    }
                }
            }
            return new NumberToken(String.valueOf(result));
        }
    }
}

class UNode extends ParserTreeNode{

    UNode() {
        super("U");
    }

    Token evaluate(){
        if (this.children.size() == 1){
            return this.children.get(0).evaluate();
        }else{

            Token first = this.children.get(0).evaluate();
            Token second = this.children.get(1).evaluate();

            double base;
            double pow;

            if(first.tokenType.equals(TokenType.IDENTIFIER) || second.tokenType.equals(TokenType.IDENTIFIER)){

                if(first.tokenType.equals(TokenType.IDENTIFIER) && second.tokenType.equals(TokenType.IDENTIFIER)){
                    base = Double.parseDouble(String.valueOf(Parser.parserMemory.get(first.text)));
                    pow = Double.parseDouble(String.valueOf(Parser.parserMemory.get(second.text)));
                    return new NumberToken(String.valueOf(Math.pow(base,pow)));
                }else if (first.tokenType.equals(TokenType.IDENTIFIER)){
                    base = Double.parseDouble(String.valueOf(Parser.parserMemory.get(first.text)));
                    pow = Double.parseDouble(second.text);
                    return new NumberToken(String.valueOf(Math.pow(base,pow)));
                }else{
                    base = Double.parseDouble(first.text);
                    pow = Double.parseDouble(String.valueOf(Parser.parserMemory.get(second.text)));
                    return new NumberToken(String.valueOf(Math.pow(base,pow)));
                }

            }else {
                base = Double.parseDouble(first.text);
                pow = Double.parseDouble(second.text);
                return new NumberToken(String.valueOf(Math.pow(base,pow)));
            }

        }
    }

}

class FNode extends ParserTreeNode{

    FNode() {
        super("F");
    }
    Token evaluate(){
        return this.children.get(0).evaluate();
    }
}

class LNode extends ParserTreeNode{

    Token token;

    LNode(Token token) {
        super("L");
        this.token = token;
    }

    Token evaluate(){
        return this.token;
    }
}

class DNode extends ParserTreeNode{

    Token token;
    DNode(Token token) {
        super("D");
        this.token = token;
    }

    Token evaluate(){
        return this.token;
    }

}



class Scanner {
    private String progText;
    private int curPos = 0;
    //private String fileName;

    Scanner(String fileName){
        //Scanners know about the programming language (they do not know anything about
        //the grammar). But they know about token types
        try {
            byte [] allBytes = Files.readAllBytes(Paths.get(fileName));
            progText = new String(allBytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //parser will ask the scanner for the next token. Parser knows about
    //the grammar
    Token nextToken() {


        if(curPos == progText.length())
            return new EOFToken(null);

        char curChar;
        //we skip white space characters
        while(curPos < progText.length() && Character.isWhitespace(progText.charAt(curPos)))
            curPos++;

        if(curPos == progText.length())
            return new EOFToken(null);

        //we are at the first non-whitespace character
        curChar = progText.charAt(curPos);
        curPos++;

        if(Character.isDigit(curChar)){
            return new NumberToken(String.valueOf(curChar));
        }
        else if(Character.isAlphabetic(curChar)) {
            return new IdentifierToken(String.valueOf(curChar));
        }
        else if(curChar == '{') {
            return new WhileBegToken(String.valueOf(curChar));
        }else if(curChar == '}'){
            return new WhileEndToken(String.valueOf(curChar));
        }else if(curChar == '['){
            return new IfBeginToken(String.valueOf(curChar));
        }else if(curChar == ']'){
            return new IfEndToken(String.valueOf(curChar));
        }else if(curChar == '='){
            return new EqualToken(String.valueOf(curChar));
        }else if(curChar == '-'){
            return new MinusToken(String.valueOf(curChar));
        }else if(curChar == '+'){
            return new PlusToken(String.valueOf(curChar));
        }else if(curChar == '*'){
            return new MultiplyToken(String.valueOf(curChar));
        }else if(curChar == '/'){
            return new DivisionToken(String.valueOf(curChar));
        }else if(curChar == '>'){
            return new InputToken(String.valueOf(curChar));
        }else if(curChar == '<'){
            return new OutputToken(String.valueOf(curChar));
        }else if(curChar == ';'){
            return new SemicolonToken(String.valueOf(curChar));
        }else if(curChar == '?'){
            return new QuestionMarkToken(String.valueOf(curChar));
        }else if(curChar == '%'){
            return new RemainderToken(String.valueOf(curChar));
        }else if(curChar == '^'){
            return new ExponentToken(String.valueOf(curChar));
        }else if(curChar == ':'){
            return new ColonToken(String.valueOf(curChar));
        }else if(curChar == '('){
            return new ParenthesesBeg(String.valueOf(curChar));
        }else if(curChar == ')'){
            return new ParenthesesEnd(String.valueOf(curChar));
        }
        return new ErrorToken("NotRecognizedToken");

    }
}
class Token{
    protected String text;
    protected TokenType tokenType;
    Token(String text){
        this.text = text;
    }
    TokenType getType() {
        return tokenType;
    }

    boolean isTerminal(){
        return (this.tokenType.equals(TokenType.IF_END) || this.tokenType.equals(TokenType.WHILE_END) || tokenType.equals(TokenType.SEMICOLON) || this.tokenType.equals(TokenType.END_OF_FILE));
    }


}
class EOFToken extends Token{
    EOFToken(String text){
        super("EOF");
        this.tokenType = TokenType.END_OF_FILE;
    }
}
class NumberToken extends Token{
    NumberToken(String text){
        super(text);
        this.tokenType = TokenType.NUMBER;
    }
}
class IdentifierToken extends Token{
    IdentifierToken(String text){
        super(text);
        this.tokenType = TokenType.IDENTIFIER;
    }
}
class WhileBegToken extends Token{
    WhileBegToken(String text){
        super(text);
        this.tokenType = TokenType.WHILE_BEG;
    }
}
class WhileEndToken extends Token{
    WhileEndToken(String text){
        super(text);
        this.tokenType = TokenType.WHILE_END;
    }
}
class IfBeginToken extends Token{
    IfBeginToken(String text){
        super(text);
        this.tokenType = TokenType.IF_BEG;
    }
}
class IfEndToken extends Token{
    IfEndToken(String text){
        super(text);
        this.tokenType = TokenType.IF_END;
    }
}
class ErrorToken extends Token{
    ErrorToken(String text){
        super(text);
        this.tokenType = TokenType.NRT_ERROR;
    }
}
class EqualToken extends Token{
    EqualToken(String text){
        super(text);
        this.tokenType = TokenType.EQUAL;
    }
}
class SemicolonToken extends Token{
    SemicolonToken(String text){
        super(text);
        this.tokenType = TokenType.SEMICOLON;
    }
}
class MinusToken extends Token{
    MinusToken(String text){
        super(text);
        this.tokenType = TokenType.MINUS;
    }
}
class PlusToken extends Token{
    PlusToken(String text){
        super(text);
        this.tokenType = TokenType.PLUS;
    }
}
class MultiplyToken extends Token{
    MultiplyToken(String text){
        super(text);
        this.tokenType = TokenType.MULTIPLY;
    }
}
class DivisionToken extends Token{
    DivisionToken(String text){
        super(text);
        this.tokenType = TokenType.DIVISION;
    }
}
class OutputToken extends Token{
    OutputToken(String text){
        super(text);
        this.tokenType = TokenType.OUTPUT;
    }
}
class InputToken extends Token{
    InputToken(String text){
        super(text);
        this.tokenType = TokenType.INPUT;
    }
}
class QuestionMarkToken extends Token{
    QuestionMarkToken(String text){
        super(text);
        this.tokenType = TokenType.QUESTION_MARK;
    }
}
class RemainderToken extends Token{
    RemainderToken(String text){
        super(text);
        this.tokenType = TokenType.REMAINDER;
    }
}
class ExponentToken extends Token{
    ExponentToken(String text){
        super(text);
        this.tokenType = TokenType.EXPONENT;
    }
}
class ColonToken extends Token{
    ColonToken(String text){
        super(text);
        this.tokenType = TokenType.COLON;
    }
}
class ParenthesesBeg extends Token{
    ParenthesesBeg(String text){
        super(text);
        this.tokenType = TokenType.PARENTHESES_BEG;
    }
}
class ParenthesesEnd extends Token{
    ParenthesesEnd(String text){
        super(text);
        this.tokenType = TokenType.PARENTHESES_END;
    }
}
enum TokenType{
    IF_BEG("["), IF_END("]"), WHILE_BEG("{"), WHILE_END("}"), EQUAL("="),
    SEMICOLON(";"), EXPONENT("^"), MINUS("-"), PLUS("+"), MULTIPLY("*"), DIVISION("/"),
    QUESTION_MARK("?"), INPUT(">"), OUTPUT("<"), REMAINDER("%"), COLON(":"),
    PARENTHESES_BEG("("), PARENTHESES_END(")"), NUMBER, IDENTIFIER, NRT_ERROR, END_OF_FILE;
    String text;
    TokenType(){
        this.text = this.toString();
    }
    TokenType(String text){
        this.text = text;
    }

}