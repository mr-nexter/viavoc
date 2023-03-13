package com.viavoc.lexpars.parser;

import com.viavoc.Utils;
import com.viavoc.lexpars.exceptions.LexerException;
import com.viavoc.lexpars.utils.WordsToNumbersUtil;

import java.util.*;

public final class Lexer {

    public static List<Token> tokenize(String input) {
        return new Lexer(input).tokenize();
    }

    private static final String OPERATOR_CHARS = "+-*/%()[]{}=<>!&|.,;^~?:";

    private static final Map<String, String> cleans;
    static {
        cleans = new HashMap<String, String>(){
            {
                put("left brace", "{");
                put("right brace", "}");
                put("left bracket", "(");
                put("right bracket", ")");
                put("left parentheses", "(");
                put("right parentheses", ")");
                put("left square bracket", "[");
                put("right square bracket", "]");
                put("left closed bracket", "[");
                put("right closed bracket", "]");
                put("exclamation mark", "!");
                put("underscore", "_");
                put("colon", ":");
                put("semicolon", ";");
                put("question mark", "?");
                put("quotation", "\"");
                put("apostrophe", "'");
                put("comma", ",");
                put("dot", ".");
                put("than ", "");
                put("that ", "");
                put(" is ", " ");
                put(" the ", " ");
                put(" an ", " ");
                put("from ", "");
                put("incremented by", "+=");
                put("increment by", "+=");
                put("decrement by", "-=");
                put("decremented by", "-=");
                put("instance of", "instanceof");
                put("equals to","equals");
                put("lesser or equals to", "<=");
                put("lesser", "<");
                put("lesser or equals", "<=");
                put("greater", ">");
                put("greater or equals", ">=");
                put("multiply", "*");
                put("multiplied", "*");
                put("divided", "/");
                put("divide", "/");
                put("object boolean", "Boolean");
                put("object byte", "Byte");
                put("object character", "Character");
                put("object enum", "Enum");
                put("object integer", "Integer");
                put("object double", "Double");
                put("object float", "Float");
                put("object short", "Short");
                put("object long", "Long");
                put("end of switch", "}");
                put("switch end", "}");
                put("end of block", "}");
                put("block end", "}");
                put("extending", "extends");
                put("implementing", "implements");
            }
        };
    }

    public static final Map<String, TokenType> OPERATORS;
    static {
        OPERATORS = new HashMap<>();
        OPERATORS.put("+", TokenType.PLUS);
        OPERATORS.put("-", TokenType.MINUS);
        OPERATORS.put("*", TokenType.STAR);
        OPERATORS.put("/", TokenType.SLASH);
        OPERATORS.put("%", TokenType.PERCENT);
        OPERATORS.put("(", TokenType.LPAREN);
        OPERATORS.put(")", TokenType.RPAREN);
        OPERATORS.put("[", TokenType.LBRACKET);
        OPERATORS.put("]", TokenType.RBRACKET);
        OPERATORS.put("{", TokenType.LBRACE);
        OPERATORS.put("}", TokenType.RBRACE);
        OPERATORS.put("=", TokenType.EQ);
        OPERATORS.put("<", TokenType.LT);
        OPERATORS.put(">", TokenType.GT);
        OPERATORS.put(".", TokenType.DOT);
        OPERATORS.put(",", TokenType.COMMA);
        OPERATORS.put("^", TokenType.CARET);
        OPERATORS.put("~", TokenType.TILDE);
        OPERATORS.put("?", TokenType.QUESTION);
        OPERATORS.put(":", TokenType.COLON);
        OPERATORS.put(";", TokenType.SEMICOLON);

        OPERATORS.put("!", TokenType.EXCL);
        OPERATORS.put("&", TokenType.AMP);
        OPERATORS.put("|", TokenType.BAR);

        OPERATORS.put("==", TokenType.EQEQ);
        OPERATORS.put("!=", TokenType.EXCLEQ);
        OPERATORS.put("<=", TokenType.LTEQ);
        OPERATORS.put(">=", TokenType.GTEQ);

        OPERATORS.put("+=", TokenType.PLUSEQ);
        OPERATORS.put("-=", TokenType.MINUSEQ);
        OPERATORS.put("*=", TokenType.STAREQ);
        OPERATORS.put("/=", TokenType.SLASHEQ);
        OPERATORS.put("%=", TokenType.PERCENTEQ);
        OPERATORS.put("&=", TokenType.AMPEQ);
        OPERATORS.put("^=", TokenType.CARETEQ);
        OPERATORS.put("|=", TokenType.BAREQ);
        OPERATORS.put("::=", TokenType.COLONCOLONEQ);
        OPERATORS.put("<<=", TokenType.LTLTEQ);
        OPERATORS.put(">>=", TokenType.GTGTEQ);
        OPERATORS.put(">>>=", TokenType.GTGTGTEQ);

        OPERATORS.put("++", TokenType.PLUSPLUS);
        OPERATORS.put("--", TokenType.MINUSMINUS);

        OPERATORS.put("::", TokenType.COLONCOLON);

        OPERATORS.put("&&", TokenType.AMPAMP);
        OPERATORS.put("||", TokenType.BARBAR);

        OPERATORS.put("<<", TokenType.LTLT);
        OPERATORS.put(">>", TokenType.GTGT);
        OPERATORS.put(">>>", TokenType.GTGTGT);

        OPERATORS.put("@", TokenType.AT);
        OPERATORS.put("@=", TokenType.ATEQ);
        OPERATORS.put("..", TokenType.DOTDOT);
        OPERATORS.put("**", TokenType.STARSTAR);
        OPERATORS.put("^^", TokenType.CARETCARET);
        OPERATORS.put("?:", TokenType.QUESTIONCOLON);
    }

    private static final Map<String, TokenType> KEYWORDS;
    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("print", TokenType.PRINT);
        KEYWORDS.put("println", TokenType.PRINTLN);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("each", TokenType.EACH);
        KEYWORDS.put("do", TokenType.DO);
        KEYWORDS.put("break", TokenType.BREAK);
        KEYWORDS.put("continue", TokenType.CONTINUE);
        KEYWORDS.put("define", TokenType.DEFINE);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("use", TokenType.USE);
        KEYWORDS.put("match", TokenType.MATCH);
        KEYWORDS.put("case", TokenType.CASE);
        KEYWORDS.put("extract", TokenType.EXTRACT);
        KEYWORDS.put("include", TokenType.INCLUDE);

        KEYWORDS.put("null", TokenType.NULL);
        KEYWORDS.put("when", TokenType.WHEN);
        KEYWORDS.put("public", TokenType.PUBLIC);
        KEYWORDS.put("private", TokenType.PRIVATE);
        KEYWORDS.put("protected", TokenType.PROTECTED);
        KEYWORDS.put("abstract", TokenType.ABSTRACT);
        KEYWORDS.put("class", TokenType.CLASS);
        KEYWORDS.put("extends", TokenType.EXTENDS);
        KEYWORDS.put("implements", TokenType.IMPLEMENTS);
        KEYWORDS.put("new", TokenType.NEW);
        KEYWORDS.put("super", TokenType.SUPER);
        KEYWORDS.put("this", TokenType.THIS);
        KEYWORDS.put("instanceof", TokenType.INSTANCEOF);
        KEYWORDS.put("interface", TokenType.INTERFACE);
        KEYWORDS.put("native", TokenType.NATIVE);
        KEYWORDS.put("static", TokenType.STATIC);
        KEYWORDS.put("final", TokenType.FINAL);
        KEYWORDS.put("assert", TokenType.ASSERT);
        KEYWORDS.put("strictfp", TokenType.STRICTFP);
        KEYWORDS.put("import", TokenType.IMPORT);
        KEYWORDS.put("package", TokenType.PACKAGE);
        KEYWORDS.put("default", TokenType.DEFAULT);
        KEYWORDS.put("try", TokenType.TRY);
        KEYWORDS.put("catch", TokenType.CATCH);
        KEYWORDS.put("finally", TokenType.FINALLY);
        KEYWORDS.put("throw", TokenType.THROW);
        KEYWORDS.put("throws", TokenType.THROWS);
        KEYWORDS.put("switch", TokenType.SWITCH);
        KEYWORDS.put("synchronized", TokenType.SYNCHRONIZED);
        KEYWORDS.put("transient", TokenType.TRANSIENT);
        KEYWORDS.put("volatile", TokenType.VOLATILE);
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);

        KEYWORDS.put("add", TokenType.ADD);
        KEYWORDS.put("all", TokenType.ALL);
        KEYWORDS.put("and", TokenType.AND);
        KEYWORDS.put("any", TokenType.ANY);
        KEYWORDS.put("argument", TokenType.ARGUMENT);
        KEYWORDS.put("arguments", TokenType.ARGUMENTS);
        KEYWORDS.put("array", TokenType.ARRAY);
        KEYWORDS.put("at", TokenType.AT_COMMAND);
        KEYWORDS.put("by", TokenType.BY);
        KEYWORDS.put("call", TokenType.CALL);
        KEYWORDS.put("called", TokenType.CALLED);
        KEYWORDS.put("casted", TokenType.CAST);
        KEYWORDS.put("change", TokenType.CHANGE);
        KEYWORDS.put("commit", TokenType.COMMIT);
        KEYWORDS.put("complex", TokenType.COMPLEX);
        KEYWORDS.put("constructor", TokenType.CONSTRUCTOR);
        KEYWORDS.put("constructors", TokenType.CONSTRUCTORS);
        KEYWORDS.put("create", TokenType.CREATE);
        KEYWORDS.put("cursor", TokenType.CURSOR);
        KEYWORDS.put("delete", TokenType.DELETE);
        KEYWORDS.put("dimensional", TokenType.DIMENSIONAL);
        KEYWORDS.put("divide", TokenType.DIVIDE);
        KEYWORDS.put("divided", TokenType.DIVIDED);
        KEYWORDS.put("down", TokenType.DOWN);
        KEYWORDS.put("edit", TokenType.EDIT);
        KEYWORDS.put("empty", TokenType.EMPTY);
        KEYWORDS.put("exception", TokenType.EXCEPTION);
        KEYWORDS.put("field", TokenType.FIELD);
        KEYWORDS.put("fields", TokenType.FIELDS);
        KEYWORDS.put("file", TokenType.FILE);
        KEYWORDS.put("files", TokenType.FILES);
        KEYWORDS.put("from", TokenType.FROM);
        KEYWORDS.put("function", TokenType.FUNCTION);
        KEYWORDS.put("generate", TokenType.GENERATE);
        KEYWORDS.put("generic", TokenType.GENERIC);
        KEYWORDS.put("get", TokenType.GET);
        KEYWORDS.put("getter", TokenType.GETTER);
        KEYWORDS.put("getters", TokenType.GETTERS);
        KEYWORDS.put("global", TokenType.GLOBAL);
        KEYWORDS.put("GO", TokenType.GO);
        KEYWORDS.put("in", TokenType.IN);
        KEYWORDS.put("initialise", TokenType.INITIALISE);
        KEYWORDS.put("initialised", TokenType.INITIALISED);
        KEYWORDS.put("inner", TokenType.INNER);
        KEYWORDS.put("it", TokenType.IT);
        KEYWORDS.put("line", TokenType.LINE);
        KEYWORDS.put("main", TokenType.MAIN);
        KEYWORDS.put("method", TokenType.METHOD);
        KEYWORDS.put("more", TokenType.MORE);  // ????????????
        KEYWORDS.put("move", TokenType.MOVE);
        KEYWORDS.put("multiplied", TokenType.MULTIPLIED);
        KEYWORDS.put("multiply", TokenType.MULTIPLY);
        KEYWORDS.put("name", TokenType.NAME);
        KEYWORDS.put("names", TokenType.NAMES);
        KEYWORDS.put("or", TokenType.OR);
        KEYWORDS.put("of", TokenType.OF);
        KEYWORDS.put("objects", TokenType.OBJECTS);
        KEYWORDS.put("parameter", TokenType.PARAMETER);
        KEYWORDS.put("parameters", TokenType.PARAMETERS);
        KEYWORDS.put("please", TokenType.PLEASE);
        KEYWORDS.put("push", TokenType.PUSH);
        KEYWORDS.put("range", TokenType.RANGE);
        KEYWORDS.put("remove", TokenType.REMOVE);
        KEYWORDS.put("repository", TokenType.REPOSITORY);
        KEYWORDS.put("returning", TokenType.RETURNING);
        KEYWORDS.put("setter", TokenType.SETTER);
        KEYWORDS.put("setters", TokenType.SETTERS);
        KEYWORDS.put("sort", TokenType.SORT);
        KEYWORDS.put("sout", TokenType.SOUT);
        KEYWORDS.put("ternary", TokenType.TERNARY);
        KEYWORDS.put("than", TokenType.THAN);
        KEYWORDS.put("then", TokenType.THEN);
        KEYWORDS.put("to", TokenType.TO);
        KEYWORDS.put("type", TokenType.TYPE);
        KEYWORDS.put("up", TokenType.UP);
        KEYWORDS.put("undo", TokenType.UNDO);
        KEYWORDS.put("variable", TokenType.VARIABLE);
        KEYWORDS.put("with", TokenType.WITH);
        KEYWORDS.put("object", TokenType.OBJECT);
        KEYWORDS.put("optional", TokenType.OPTIONAL);
    }

    public static Set<String> getKeywords() {
        return KEYWORDS.keySet();
    }

    public final String input;
    private final int length;

    private final List<Token> tokens;
    private final StringBuilder buffer;

    private int pos;
    private int row, col;

    public Lexer(String input) {
        this.input = clearOverloading(input);
        length = this.input.length();
        
        tokens = new ArrayList<>();
        buffer = new StringBuilder();
        row = col = 1;
    }

    public List<Token> tokenize() {
        while (pos < length) {
            final char current = peek(0);
            if (Character.isDigit(current)) tokenizeNumber();
            else if (isOwnLangIdentifierStart(current)) tokenizeWord();
            else if (current == '`') tokenizeExtendedWord();
            else if (current == '"') tokenizeText();
            else if (current == '#') {
                next();
                tokenizeHexNumber();
            }
            else if (OPERATOR_CHARS.indexOf(current) != -1) {
                tokenizeOperator();
            } else {
                // whitespaces
                next();
            }
        }
        return tokens;
    }

    private void tokenizeNumber() {
        clearBuffer();
        char current = peek(0);
        if (current == '0' && (peek(1) == 'x' || (peek(1) == 'X'))) {
            next();
            next();
            tokenizeHexNumber();
            return;
        }
        while (true) {
            if (current == '.') {
                if (buffer.indexOf(".") != -1) throw error("Invalid float number");
            } else if (!Character.isDigit(current)) {
                break;
            }
            buffer.append(current);
            current = next();
        }
        addToken(TokenType.NUMBER, buffer.toString());
    }

    private void tokenizeHexNumber() {
        clearBuffer();
        char current = peek(0);
        while (isHexNumber(current) || (current == '_')) {
            if (current != '_') {
                // allow _ symbol
                buffer.append(current);
            }
            current = next();
        }
        if (buffer.length() > 0) {
            addToken(TokenType.HEX_NUMBER, buffer.toString());
        }
    }

    private static boolean isHexNumber(char current) {
        return Character.isDigit(current)
                || ('a' <= current && current <= 'f')
                || ('A' <= current && current <= 'F');
    }

    private void tokenizeOperator() {
        char current = peek(0);
        if (current == '/') {
            if (peek(1) == '/') {
                next();
                next();
                tokenizeComment();
                return;
            } else if (peek(1) == '*') {
                next();
                next();
                tokenizeMultilineComment();
                return;
            }
        }
        clearBuffer();
        while (true) {
            final String text = buffer.toString();
            if (!text.isEmpty() && !OPERATORS.containsKey(text + current)) {
                addToken(OPERATORS.get(text));
                return;
            }
            buffer.append(current);
            current = next();
        }
    }

    private void tokenizeWord() {
        clearBuffer();
        buffer.append(peek(0));
        char current = next();
        while (true) {
            if (!isOwnLangIdentifierPart(current)) {
                break;
            }
            buffer.append(current);
            current = next();
        }

        final String word = buffer.toString();
        if (word.equals("initialize")) {
            addToken(TokenType.INITIALISE);
        } else if (KEYWORDS.containsKey(word)) {
            addToken(KEYWORDS.get(word));
        } else {
            addToken(TokenType.WORD, word);
        }
    }

    private void tokenizeExtendedWord() {
        next();// skip `
        clearBuffer();
        char current = peek(0);
        while (true) {
            if (current == '`') break;
            if (current == '\0') throw error("Reached end of file while parsing extended word.");
            if (current == '\n' || current == '\r') throw error("Reached end of line while parsing extended word.");
            buffer.append(current);
            current = next();
        }
        next(); // skip closing `
        addToken(TokenType.WORD, buffer.toString());
    }

    private void tokenizeText() {
        next();// skip "
        clearBuffer();
        char current = peek(0);
        while (true) {
            if (current == '\\') {
                current = next();
                switch (current) {
                    case '"': current = next(); buffer.append('"'); continue;
                    case '0': current = next(); buffer.append('\0'); continue;
                    case 'b': current = next(); buffer.append('\b'); continue;
                    case 'f': current = next(); buffer.append('\f'); continue;
                    case 'n': current = next(); buffer.append('\n'); continue;
                    case 'r': current = next(); buffer.append('\r'); continue;
                    case 't': current = next(); buffer.append('\t'); continue;
                    case 'u': // http://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.3
                        int rollbackPosition = pos;
                        while (current == 'u') current = next();
                        int escapedValue = 0;
                        for (int i = 12; i >= 0 && escapedValue != -1; i -= 4) {
                            if (isHexNumber(current)) {
                                escapedValue |= (Character.digit(current, 16) << i);
                            } else {
                                escapedValue = -1;
                            }
                            current = next();
                        }
                        if (escapedValue >= 0) {
                            buffer.append((char) escapedValue);
                        } else {
                            // rollback
                            buffer.append("\\u");
                            pos = rollbackPosition;
                        }
                        continue;
                }
                buffer.append('\\');
                continue;
            }
            if (current == '"') break;
            if (current == '\0') throw error("Reached end of file while parsing text string.");
            buffer.append(current);
            current = next();
        }
        next(); // skip closing "

        addToken(TokenType.TEXT, buffer.toString());
    }

    private void tokenizeComment() {
        char current = peek(0);
        while ("\r\n\0".indexOf(current) == -1) {
            current = next();
        }
    }

    private void tokenizeMultilineComment() {
        char current = peek(0);
        while (true) {
            if (current == '*' && peek(1) == '/') break;
            if (current == '\0') throw error("Reached end of file while parsing multiline comment");
            current = next();
        }
        next(); // *
        next(); // /
    }

    private boolean isOwnLangIdentifierStart(char current) {
        return (Character.isLetter(current) || (current == '_') || (current == '$'));
    }

    private boolean isOwnLangIdentifierPart(char current) {
        return (Character.isLetterOrDigit(current) || (current == '_') || (current == '$'));
    }

    private void clearBuffer() {
        buffer.setLength(0);
    }

    private char next() {
        pos++;
        final char result = peek(0);
        if (result == '\n') {
            row++;
            col = 1;
        } else col++;
        return result;
    }

    private char peek(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= length) return '\0';
        return input.charAt(position);
    }

    private void addToken(TokenType type) {
        addToken(type, "");
    }

    private void addToken(TokenType type, String text) {
        tokens.add(new Token(type, text, row, col));
    }

    private LexerException error(String text) {
        return new LexerException(row, col, text);
    }

    public String clearOverloading(String string){
        string = string.toLowerCase();

        for (Map.Entry<String, String> entry : cleans.entrySet()) {
            string = string.replaceAll(entry.getKey(), entry.getValue());
        }

        string = string.replaceAll("increment", "++");
        string = string.replaceAll("decrement", "--");
        string = string.replaceAll("block start", "{");
        string = string.replaceAll("block end", "}");
        string = string.replaceAll( "new row ", ";\n");
        string = string.replaceAll( "new row", ";\n");
        string = string.replaceAll("integer", "int");
        string = string.replaceAll("s out", "sout");

        string = WordsToNumbersUtil.convertTextualNumbersInDocument(string);

        // (for [a-z0-9 ]+ of [a-z0-9= ]+ when)
        string = clearEquals(string);
        return string;

    }

    private String clearEquals(String string){
        string = string.replaceAll("not equals", "!=");
        List<String> words = Arrays.asList(string.split("\\s+"));

        for(int i = 1; i < words.size(); i++){
            if (words.get(i).equals("equals") && words.get(i-1).equals("call")) {
                words.set(i-1, ".");
            } else if (words.get(i).equals("equals")) {
                words.set(i, "=");
            }
        }
        return Utils.toStringDelFree(words);
    }
}
