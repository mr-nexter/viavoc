package com.viavoc.lexpars.parser;

public enum TokenType {

    NUMBER,
    HEX_NUMBER,
    WORD,
    TEXT,
    NULL,

    // keyword
    PRINT,
    PRINTLN,
    IF,
    ELSE,
    BREAK,
    CONTINUE,
    DEFINE,
    RETURN,
    USE,
    MATCH,
    CASE,
    EXTRACT,
    INCLUDE,

    PLUS, // +
    MINUS, // -
    STAR, // *
    SLASH, // /
    PERCENT,// %
    AT, // @

    EQ, // =
    EQEQ, // ==
    EXCL, // !
    EXCLEQ, // !=
    LTEQ, // <=
    LT, // <
    GT, // >
    GTEQ, // >=

    PLUSEQ, // +=
    MINUSEQ, // -=
    STAREQ, // *=
    SLASHEQ, // /=
    PERCENTEQ, // %=
    ATEQ, // @=
    AMPEQ, // &=
    CARETEQ, // ^=
    BAREQ, // |=
    COLONCOLONEQ, // ::=
    LTLTEQ, // <<=
    GTGTEQ, // >>=
    GTGTGTEQ, // >>>=

    PLUSPLUS, // ++
    MINUSMINUS, // --

    LTLT, // <<
    GTGT, // >>
    GTGTGT, // >>>

    DOTDOT, // ..
    STARSTAR, // **
    QUESTIONCOLON, // ?:

    TILDE, // ~
    CARET, // ^
    CARETCARET, // ^^
    BAR, // |
    BARBAR, // ||
    AMP, // &
    AMPAMP, // &&

    QUESTION, // ?
    COLON, // :
    COLONCOLON, // ::
    SEMICOLON,

    LPAREN, // (
    RPAREN, // )
    LBRACKET, // [
    RBRACKET, // ]
    LBRACE, // {
    RBRACE, // }
    COMMA, // ,
    DOT, // .

    EOF,

    // inner service commands
    TYPE,
    VOID_TYPE,
    BOOLEAN_TYPE,
    BYTE_TYPE,
    CHAR_TYPE,
    SHORT_TYPE,
    INT_TYPE,
    LONG_TYPE,
    FLOAT_TYPE,
    DOUBLE_TYPE,
    ENUM_TYPE,

    /**
     * Object types
     * */
    OB_BOOLEAN_TYPE,
    OB_BYTE_TYPE,
    OB_CHAR_TYPE,
    OB_SHORT_TYPE,
    OB_INT_TYPE,
    OB_LONG_TYPE,
    OB_FLOAT_TYPE,
    OB_DOUBLE_TYPE,

    /**
     * Access modifiers
     * */
    PUBLIC,
    PRIVATE,
    PROTECTED,

    /**
     * Class related
     * */
    ABSTRACT,
    CLASS,
    EXTENDS,
    IMPLEMENTS,
    NEW,
    SUPER,
    THIS,
    INSTANCEOF,
    INTERFACE,
    NATIVE,

    /**
     * Modifying qualifiers
     * */
    STATIC,
    FINAL,
    ASSERT,
    STRICTFP,

    /**
     * Importing and packaging
     * */
    IMPORT,
    PACKAGE,

    /***
     * Managing flow commands
     */
    DEFAULT,

    /**
     * Statements if, switch, try/catch
     * */
    TRY,
    CATCH,
    FINALLY,
    THROW,
    THROWS,
    SWITCH,

    /**
     * LOOPS
     * */
    DO,
    FOR,
    WHILE,

    /**
     * Threads
     * */
    SYNCHRONIZED,
    TRANSIENT,
    VOLATILE,

    TRUE,
    FALSE,
    /**
     * Own program's commands
     * */
    ADD,
    ALL,
    AND,
    ANY,
    ARGUMENT,
    ARGUMENTS,
    ARRAY,
    AT_COMMAND,
    BY,
    CALL,
    CALLED,
    CAST,
    CHANGE,
    COMMIT,
    CONSTRUCTOR,
    CONSTRUCTORS,
    CREATE,
    CURSOR,
    DECREMENT,
    DELETE,
    DIMENSIONAL,
    DIVIDE,
    DIVIDED,
    DOWN,
    EACH,
    EDIT,
    EMPTY,
    EXCEPTION,
    FIELD,
    FIELDS,
    FILE,
    FILES,
    FROM,
    FUNCTION,
    GO,
    GENERATE,
    GENERIC,
    GET,
    GETTER,
    GETTERS,
    GLOBAL,
    IN,
    INCREMENT,
    INITIALISE,
    INITIALISED,
    INNER,
    LESSER,
    LINE,
    MAIN,
    METHOD,
    MORE,
    MOVE,
    MULTIPLIED,
    MULTIPLY,
    NAME,
    NAMES,
    OR,
    OF,
    OBJECTS,
    PARAMETER,
    PARAMETERS,
    PLEASE,
    PUSH,
    RANGE,
    REMOVE,
    REPOSITORY,
    RETURNING,
    SETTER,
    SETTERS,
    SEPARATOR,
    SORT,
    SOUT,
    TERNARY,
    THAN,
    THEN,
    TO,
    UP,
    UNDO,
    VARIABLE,
    WITH,
    WHEN,
    OBJECT,
    OPTIONAL,
    COMPLEX,
    IT
}
