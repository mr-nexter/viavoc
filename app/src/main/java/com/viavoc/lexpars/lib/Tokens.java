package com.viavoc.lexpars.lib;

import com.viavoc.lexpars.parser.Token;
import com.viavoc.lexpars.parser.TokenType;

import java.util.*;

import static com.viavoc.lexpars.parser.TokenType.USE;

public class Tokens {

    private static Map<TokenType, String> tokens;
    private static List<TokenType> neverEver;
    private static List<TokenType> neverInVariables;
    private static List<TokenType> neverInClasses;
    private static List<TokenType> modifiers;

    static {

        neverEver = new ArrayList<>(Arrays.asList(
                TokenType.PLUS, // +
                TokenType.MINUS, // -
                TokenType.STAR, // *
                TokenType.SLASH, // /
                TokenType.PERCENT,// %
                TokenType.AT, // @

                TokenType.EQ, // =
                TokenType.EQEQ, // ==
                TokenType.EXCL, // !
                TokenType.EXCLEQ, // !=
                TokenType.LTEQ, // <=
                TokenType.LT, // <
                TokenType.GT, // >
                TokenType.GTEQ, // >=

                TokenType.PLUSEQ, // +=
                TokenType.MINUSEQ, // -=
                TokenType.STAREQ, // *=
                TokenType.SLASHEQ, // /=
                TokenType.PERCENTEQ, // %=
                TokenType.ATEQ, // @=
                TokenType.AMPEQ, // &=
                TokenType.CARETEQ, // ^=
                TokenType.BAREQ, // |=
                TokenType.COLONCOLONEQ, // ::=
                TokenType.LTLTEQ, // <<=
                TokenType.GTGTEQ, // >>=
                TokenType.GTGTGTEQ, // >>>=

                TokenType.PLUSPLUS, // ++
                TokenType.MINUSMINUS, // --

                TokenType.LTLT, // <<
                TokenType.GTGT, // >>
                TokenType.GTGTGT, // >>>

                TokenType.DOTDOT, // ..
                TokenType.STARSTAR, // **
                TokenType.QUESTIONCOLON, // ?:

                TokenType.TILDE, // ~
                TokenType.CARET, // ^
                TokenType.CARETCARET, // ^^
                TokenType.BAR, // |
                TokenType.BARBAR, // ||
                TokenType.AMP, // &
                TokenType.AMPAMP, // &&

                TokenType.QUESTION, // ?
                TokenType.COLON, // :
                TokenType.COLONCOLON, // ::
                TokenType.SEMICOLON,

                TokenType.LPAREN, // (
                TokenType.RPAREN, // )
                TokenType.LBRACKET, // [
                TokenType.RBRACKET, // ]
                TokenType.LBRACE, // {
                TokenType.RBRACE, // }
                TokenType.COMMA, // ,
                TokenType.DOT, // .

                TokenType.EOF
        ));

        neverInClasses = new ArrayList<>(Arrays.asList(
                TokenType.SWITCH,
                TokenType.CASE,
                TokenType.TRY,
                TokenType.CATCH,
                TokenType.FINALLY,
                TokenType.PUBLIC,
                TokenType.PRIVATE,
                TokenType.PROTECTED,
                TokenType.ABSTRACT,
                TokenType.CLASS,
                TokenType.EXTENDS,
                TokenType.IMPLEMENTS,
                TokenType.SUPER,
                TokenType.THIS,
                TokenType.NATIVE,
                TokenType.FINAL,
                TokenType.IMPORT,
                TokenType.ADD,
                TokenType.AND,
                TokenType.DO,
                TokenType.FOR,
                TokenType.WHILE,
                TokenType.CALL,
                TokenType.CHANGE,
                TokenType.DECREMENT,
                TokenType.FROM,
                TokenType.FUNCTION,
                TokenType.IN,
                TokenType.INCREMENT,
                TokenType.INNER
        ));

        neverInVariables = new ArrayList<TokenType>(Arrays.asList(
                TokenType.SWITCH,
                TokenType.CASE,
                TokenType.TRY,
                TokenType.CATCH,
                TokenType.FINALLY,
                TokenType.PUBLIC,
                TokenType.PRIVATE,
                TokenType.PROTECTED,
                TokenType.ABSTRACT,
                TokenType.CLASS,
                TokenType.EXTENDS,
                TokenType.IMPLEMENTS,
                TokenType.NEW,
                TokenType.SUPER,
                TokenType.THIS,
                TokenType.NATIVE,
                TokenType.FINAL,
                TokenType.IMPORT,
                TokenType.PACKAGE,
                TokenType.AND,
                TokenType.DO,
                TokenType.OF,
                TokenType.FOR,
                TokenType.WHILE,
                TokenType.TRANSIENT,
                TokenType.VOLATILE,
                TokenType.CALL,
                TokenType.CHANGE,
                TokenType.DECREMENT,
                TokenType.EXCEPTION,
                TokenType.FROM,
                TokenType.FUNCTION,
                TokenType.IN,
                TokenType.INCREMENT,
                TokenType.INNER,
                TokenType.LESSER,
                TokenType.METHOD,
                TokenType.MULTIPLY,
                TokenType.OR,
                TokenType.RANGE,
                TokenType.REPOSITORY,
                TokenType.SEPARATOR,
                TokenType.SORT,
                TokenType.THAN,
                TokenType.THEN,
                TokenType.TO,
                TokenType.WITH,
                TokenType.WHEN,

                TokenType.TRUE,
                TokenType.FALSE
        ));

        tokens = new HashMap<TokenType, String>(){{
            put(TokenType.BREAK, "break");
            put(TokenType.CONTINUE, "continue");
            put(TokenType.SWITCH, "switch");
            put(TokenType.CASE, "case");
            put(TokenType.DEFAULT, "default");
            put(TokenType.TRY, "try");
            put(TokenType.CATCH, "catch");
            put(TokenType.FINALLY, "finally");
            put(TokenType.PUBLIC, "public");
            put(TokenType.PRIVATE, "private");
            put(TokenType.PROTECTED, "protected");

            put(TokenType.ABSTRACT, "abstract");
            put(TokenType.CLASS, "class");
            put(TokenType.COMPLEX, "complex");
            put(TokenType.EXTENDS, "extends");
            put(TokenType.IMPLEMENTS, "implements");
            put(TokenType.NEW, "new");
            put(TokenType.SUPER, "super");
            put(TokenType.THIS, "this");
            put(TokenType.NATIVE, "native");
            put(TokenType.FINAL, "final");
            put(TokenType.ASSERT, "assert");
            put(TokenType.IMPORT, "import");
            put(TokenType.PACKAGE, "package");
            put(TokenType.DO, "do");
            put(TokenType.OF, "of");
            put(TokenType.FOR, "for");
            put(TokenType.WHILE, "while");
            put(TokenType.TRANSIENT, "transient");
            put(TokenType.VOLATILE, "volatile");
            put(TokenType.ADD, "add");
            put(TokenType.ALL, "all");
            put(TokenType.AND, "and");
            put(TokenType.ARGUMENT, "argument");
            put(TokenType.ARGUMENTS, "arguments");
            put(TokenType.ARRAY, "array");
            put(TokenType.AT_COMMAND, "at");
            put(TokenType.BY, "by");
            put(TokenType.CALL, "call");
            put(TokenType.CALLED, "called");
            put(TokenType.CHANGE, "change");
            put(TokenType.COMMIT, "commit");
            put(TokenType.CONSTRUCTOR, "constructor");
            put(TokenType.CONSTRUCTORS, "constructors");
            put(TokenType.CREATE, "create");
            put(TokenType.CURSOR, "cursor");
            put(TokenType.DECREMENT, "decrement");
            put(TokenType.DEFINE, "define");
            put(TokenType.DELETE, "delete");
            put(TokenType.DIMENSIONAL, "dimensional");
            put(TokenType.DIVIDE, "divide");
            put(TokenType.DIVIDED, "divided");
            put(TokenType.DOWN, "down");
            put(TokenType.EACH, "each");
            put(TokenType.EDIT, "edit");
            put(TokenType.EXCEPTION, "exception");
            put(TokenType.FIELD, "field");
            put(TokenType.FIELDS, "fields");
            put(TokenType.FILE, "file");
            put(TokenType.FILES, "files");
            put(TokenType.FROM, "from");
            put(TokenType.FUNCTION, "function");
            put(TokenType.GENERATE, "generate");
            put(TokenType.GENERIC, "generic");
            put(TokenType.GET, "get");
            put(TokenType.GETTER, "getter");
            put(TokenType.GETTERS, "getters");
            put(TokenType.GLOBAL, "global");
            put(TokenType.IN, "in");
            put(TokenType.INITIALISE, "initialise");
            put(TokenType.INITIALISED, "initialised");
            put(TokenType.INCREMENT, "increment");
            put(TokenType.INNER, "inner");
            put(TokenType.IT, "it");
            put(TokenType.LESSER, "lesser");
            put(TokenType.MAIN, "main");
            put(TokenType.METHOD, "method");
            put(TokenType.MORE, "more");
            put(TokenType.MOVE, "move");
            put(TokenType.MULTIPLIED, "multiplied");
            put(TokenType.MULTIPLY, "multiply");
            put(TokenType.NAME, "name");
            put(TokenType.NAMES, "names");
            put(TokenType.OBJECTS, "objects");
            put(TokenType.OR, "or");
            put(TokenType.PARAMETER, "parameter");
            put(TokenType.PARAMETERS, "parameters");
            put(TokenType.PRINT, "print");
            put(TokenType.PRINTLN, "println");
            put(TokenType.PLEASE, "please");
            put(TokenType.PUSH, "push");
            put(TokenType.RANGE, "range");
            put(TokenType.REMOVE, "remove");
            put(TokenType.REPOSITORY, "repository");
            put(TokenType.RETURNING, "returning");
            put(TokenType.SETTER, "setter");
            put(TokenType.SETTERS, "setters");
            put(TokenType.SEPARATOR, "separator");
            put(TokenType.SORT, "sort");
            put(TokenType.STATIC, "static");
            put(TokenType.TERNARY, "ternary");
            put(TokenType.THAN, "than");
            put(TokenType.THEN, "then");
            put(TokenType.TYPE, "type");
            put(TokenType.TO, "to");
            put(TokenType.UP, "up");
            put(TokenType.UNDO, "undo");
            put(TokenType.VARIABLE, "variable");
            put(TokenType.WITH, "with");
            put(TokenType.WHEN, "when");
            put(TokenType.OBJECT, "object");
            put(TokenType.OPTIONAL, "optional");
        }};

        modifiers = new ArrayList<>(Arrays.asList(
                TokenType.PUBLIC,
                TokenType.PRIVATE,
                TokenType.PROTECTED,
                TokenType.ABSTRACT,
                TokenType.NATIVE,
                TokenType.STATIC,
                TokenType.FINAL,
                TokenType.ASSERT,
                TokenType.SYNCHRONIZED,
                TokenType.TRANSIENT,
                TokenType.VOLATILE
        ));
    }

    public static boolean possibleConvetion(TokenType token) {
        return tokens.containsKey(token);
    }

    public static boolean restrictedForVariables(TokenType token) {
        if (neverEver.contains(token)) return true;
        return neverInVariables.contains(token);
    }

    public static boolean restrictedForClasses(TokenType token) {
        if (neverEver.contains(token)) return true;
        return neverInClasses.contains(token);
    }

    public static String toString(TokenType token){
        if (tokens.containsKey(token)) {
            return tokens.get(token);
        } else return "";
    }

    public static boolean isModifier(TokenType token){
        return modifiers.contains(token);
    }

    public static boolean inManagingTokens(TokenType token) {
        return tokens.containsKey(token);
    }
}
