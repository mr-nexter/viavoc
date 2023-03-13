package com.viavoc.lexpars.parser;

import android.support.design.widget.Snackbar;

import com.viavoc.R;
import com.viavoc.Utils;
import com.viavoc.VioLauncher;
import com.viavoc.lexpars.exceptions.ParseException;
import com.viavoc.lexpars.lib.*;
import com.viavoc.lexpars.models.Pair;
import com.viavoc.lexpars.parser.ast.*;
import com.viavoc.lexpars.parser.ast.Arguments;

import java.util.*;

public final class Parser {

    private class Stopper implements Statement {
        @Override
        public void execute() { }

        @Override
        public int linesCount() { return 0; }

        @Override
        public void accept(Visitor visitor) {}

        @Override
        public <R, T> R accept(ResultVisitor<R, T> visitor, T input) { return null; }
    }

    private static final Token EOF = new Token(TokenType.EOF, "", -1, -1);

    private static final EnumMap<TokenType, BinaryExpression.Operator> ASSIGN_OPERATORS;
    static {
        ASSIGN_OPERATORS = new EnumMap<>(TokenType.class);
        ASSIGN_OPERATORS.put(TokenType.EQ, null);
        ASSIGN_OPERATORS.put(TokenType.PLUSEQ, BinaryExpression.Operator.ADD);
        ASSIGN_OPERATORS.put(TokenType.MINUSEQ, BinaryExpression.Operator.SUBTRACT);
        ASSIGN_OPERATORS.put(TokenType.STAREQ, BinaryExpression.Operator.MULTIPLY);
        ASSIGN_OPERATORS.put(TokenType.SLASHEQ, BinaryExpression.Operator.DIVIDE);
        ASSIGN_OPERATORS.put(TokenType.PERCENTEQ, BinaryExpression.Operator.REMAINDER);
        ASSIGN_OPERATORS.put(TokenType.AMPEQ, BinaryExpression.Operator.AND);
        ASSIGN_OPERATORS.put(TokenType.CARETEQ, BinaryExpression.Operator.XOR);
        ASSIGN_OPERATORS.put(TokenType.BAREQ, BinaryExpression.Operator.OR);
        ASSIGN_OPERATORS.put(TokenType.COLONCOLONEQ, BinaryExpression.Operator.PUSH);
        ASSIGN_OPERATORS.put(TokenType.LTLTEQ, BinaryExpression.Operator.LSHIFT);
        ASSIGN_OPERATORS.put(TokenType.GTGTEQ, BinaryExpression.Operator.RSHIFT);
        ASSIGN_OPERATORS.put(TokenType.GTGTGTEQ, BinaryExpression.Operator.URSHIFT);
        ASSIGN_OPERATORS.put(TokenType.ATEQ, BinaryExpression.Operator.AT);
    }

    private final List<Token> tokens;
    private int size;
    private final ParseErrors parseErrors;
    private LinkedList<ClassStatement> classesHierarchy;
    private boolean addToLastMode = false;
    private VioLauncher context;
    private Stack<Statement> lastStatement = new Stack<>();

    private int pos;

    private static String toUpper(String s) {
        return  s.length() == 1 ? s.toUpperCase()
                : s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    private static String toLower(String s) {
        if (!s.isEmpty()) {
            return s.length() == 1 ? s.toLowerCase()
                    : s.substring(0, 1).toLowerCase() + s.substring(1);
        } else return "";
    }

    public Parser(VioLauncher launcher){
        this.tokens = new ArrayList<>();
        this.size = tokens.size();
        this.parseErrors = new ParseErrors();
        this.classesHierarchy = new LinkedList<>();
        this.context = launcher;
    }

    public Parser(List<Token> tokens, VioLauncher activity) {
        this.tokens = tokens;
        size = tokens.size();
        parseErrors = new ParseErrors();
        this.classesHierarchy = new LinkedList<>();
        this.context = activity;
    }

    public boolean isFirst(){
        return this.classesHierarchy.isEmpty();
    }

    public void addTokens(List<Token> newTokens){
        if (!newTokens.isEmpty()) {
            tokens.addAll(newTokens);
            size = tokens.size();
        }
    }
    
    public ClassStatement getParsedStatement() {
        return classesHierarchy != null && !classesHierarchy.isEmpty() ? classesHierarchy.getFirst() : null;
    }
    
    public ParseErrors getParseErrors() {
        return parseErrors;
    }
    
    public Statement parse() {
        parseErrors.clear();
        BlockStatement result = null;
        ClassStatement classIsntance = (ClassStatement) classStatement(null);

        if (classIsntance == null) {
            result = new BlockStatement();
            result = (BlockStatement) parseStartingFromBlock(result);
        }
        if (classIsntance != null) {
            return classIsntance;
        } else return result;
    }

    private Statement printLN() {
        match(TokenType.SOUT);
        if (lookMatch(0, TokenType.EOF)) {
            return new PrintlnStatement(null);
        } else return new PrintlnStatement(expression());
    }

    public Statement continueParsing(){
        addToLastMode = true;
        if (lookMatch(0, TokenType.LBRACE)) consume(TokenType.LBRACE);
        while (true) {
            if (get(0) == EOF) break;
            checkCommands();
            if (get(0) == EOF) break;
            checkPartial();
            if (get(0) == EOF) break;
            Statement statement = statement();
            if  (statement == null) continue;
            if (statement instanceof Stopper) break;
            addToLastMethod(statement);
        }
        if (lookMatch(0, TokenType.RBRACE)) consume(TokenType.RBRACE);

        return classesHierarchy.getFirst();
    }

    private void checkPartial() {
        Statement last = null;
        Statement preLast = null;
        if (!lastStatement.isEmpty()) {
            last = lastStatement.peek();
            preLast = lastStatement.size() >= 2 ? lastStatement.elementAt(lastStatement.size()-2) : null;
        } else return;

        // initialize it by
        if (lookMatch(0, TokenType.INITIALISE) && lookMatch(1, TokenType.IT) && lookMatch(2, TokenType.BY)) {
            consume(TokenType.INITIALISE); consume(TokenType.IT); consume(TokenType.BY);
            if (last instanceof ExprStatement) {
                ExprStatement t = (ExprStatement) last;
                AssignmentExpression a = new AssignmentExpression(null, (Accessible) ((ExprStatement)t).expr, expression());
                while (true) {
                    if (lastStatement.isEmpty()) break;
                    else {
                        lastStatement.pop();
                        if (lastStatement.isEmpty()) break;
                        else {
                            Statement lastS = lastStatement.peek();
                            if (lastS instanceof BlockStatement) {
                                List<Statement> sss = ((BlockStatement) lastS).statements;
                                for (int i = 0; i < sss.size(); i++) {
                                    if (sss.get(i).equals(t)) {
                                        sss.set(i,a);
                                        break;
                                    }
                                }
                                break;
                            } else if (lastS instanceof ClassStatement) {
                                ClassStatement c = (ClassStatement) lastS;
                                for (int i = 0; i < c.fields.size(); i++) {
                                    if (c.fields.get(i).equals(t)) {
                                        c.fields.set(i, a);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            } else if (last instanceof AssignmentExpression) {
                AssignmentExpression assignment = (AssignmentExpression) last;
                if (assignment.hideDetails = true) assignment.hideDetails = false;
                if (!lookMatch(0, TokenType.EOF)) assignment.expression = expression();
            }
        }
        // if and ternary
        if (lookMatch(0, TokenType.THEN)) {
            if (!lookMatch(0, TokenType.EOF)) consume(TokenType.THEN);
            else return;

            while (true) {
                if (last instanceof IfStatement) {
                    if (!lookMatch(0, TokenType.EOF)) {
                        ((IfStatement) last).ifStatement = statementOrBlock();
                    }
                    break;
                } else if (last instanceof ExprStatement) {
                    ExprStatement expr = (ExprStatement) last;
                    if (expr.expr instanceof TernaryExpression && !lookMatch(0, TokenType.EOF)) {
                        TernaryExpression ternary = (TernaryExpression) expr.expr;
                        ternary.trueExpr = expression();
                    }
                    break;
                } else if (last instanceof AssignmentExpression && !lookMatch(0, TokenType.EOF)) {
                    AssignmentExpression assignment = (AssignmentExpression) last;
                    if (assignment.expression instanceof TernaryExpression) {
                        TernaryExpression ternary = (TernaryExpression) assignment.expression;
                        ternary.trueExpr = expression();
                    }
                    break;
                } else {
                    if (lastStatement.isEmpty()) break;
                    else {
                        lastStatement.pop();
                    }
                }
            }
        }
        // if and ternary
        if (lookMatch(0, TokenType.ELSE)) {
            consume(TokenType.ELSE);
            while (true) {
                if (last instanceof IfStatement) {
                    IfStatement ifElse = (IfStatement) last;
                    if (ifElse.elseStatement != null) {
                        if (ifElse.elseStatement instanceof BlockStatement) {
                            Statement elser = statement();
                            ((BlockStatement) ifElse.elseStatement).add(elser);
                            lastStatement.push(ifElse.elseStatement);
                            lastStatement.push(elser);
                        } else {
                            BlockStatement block = new BlockStatement();
                            block.add(ifElse.elseStatement);
                            ifElse.elseStatement = block;
                            Statement nextLast = statement();
                            block.add(nextLast);
                            lastStatement.push(block);
                            lastStatement.push(nextLast);
                        }
                    } else {
                        ifElse.elseStatement = statementOrBlock();
                        lastStatement.push(ifElse.elseStatement);
                    }
                    break;
                } else {
                    if (last instanceof ExprStatement && ((ExprStatement) last).expr instanceof TernaryExpression) {
                        TernaryExpression ternary = (TernaryExpression) ((ExprStatement) last).expr;
                        ternary.falseExpr = expression();
                        break;
                    } else if (last instanceof AssignmentExpression
                            && ((AssignmentExpression) last).expression instanceof TernaryExpression) {
                        TernaryExpression ternary = (TernaryExpression) ((AssignmentExpression) last).expression;
                        ternary.falseExpr = expression();
                        break;
                    } else {
                        // else find any of listed above statements
                        if (lastStatement.isEmpty()) break;
                        else {
                            lastStatement.pop();
                        }
                    }
                }
            }
        }
        // do ... while
        if (last instanceof DoWhileStatement && lookMatch(0, TokenType.WHILE)
                || (last instanceof BlockStatement && preLast != null && preLast instanceof DoWhileStatement)) {
            DoWhileStatement doWhile;
            if (last instanceof DoWhileStatement && lookMatch(0, TokenType.WHILE)) {
                consume(TokenType.WHILE);
                doWhile = (DoWhileStatement) last;
            } else {
                lastStatement.pop();
                doWhile = (DoWhileStatement) lastStatement.pop();
            }
            doWhile.condition = expression();
        }
        // try catch
        if (lookMatch(0, TokenType.CATCH)) {
            if (last instanceof TryCatchStatement) {
                TryCatchStatement tryCath = (TryCatchStatement) last;
                tryCath.catchs.addAll(getCatchers());
            } else if (last instanceof BlockStatement && preLast != null && preLast instanceof TryCatchStatement) {
                lastStatement.pop();
                TryCatchStatement tryCath = (TryCatchStatement) lastStatement.peek();
                tryCath.catchs.addAll(getCatchers());
            }
        }
        if (match(TokenType.FINALLY)) {
            while (true) {
                if (last instanceof TryCatchStatement) {
                    TryCatchStatement tryCath = (TryCatchStatement) last;
                    if (tryCath.finallyBody != null) {
                        if (tryCath.finallyBody instanceof BlockStatement) {
                            BlockStatement block = block(null);
                            block.statements.addAll(0, ((BlockStatement) tryCath.finallyBody).statements);
                            tryCath.finallyBody = block;
                        } else {
                            BlockStatement block = block(null);
                            block.statements.add(0, tryCath.finallyBody);
                            tryCath.finallyBody = block;
                        }
                    } else {
                        BlockStatement block = block(null);
                        tryCath.finallyBody = block;
                    }
                    break;
                } else if (last instanceof BlockStatement && preLast != null && preLast instanceof TryCatchStatement) {
                    lastStatement.pop();
                } else break;
            }
        }
        // return
        if (lookMatch(0, TokenType.RETURN)) {
            if (last instanceof FunctionDefineStatement) {
                consume(TokenType.RETURN);
                FunctionDefineStatement function = (FunctionDefineStatement) last;
                if (function.body == null) function.body = new BlockStatement();
                if (function.body instanceof BlockStatement) {
                    ((BlockStatement) function.body).add(new ReturnStatement(expression()));
                } else {
                    BlockStatement block = new BlockStatement();
                    block.add(function.body);
                    block.add(new ReturnStatement(expression()));
                    function.body = block;
                }
            } else if (last instanceof BlockStatement
                    && preLast != null && preLast instanceof FunctionDefineStatement) {
                consume(TokenType.RETURN);
                FunctionDefineStatement function = (FunctionDefineStatement) preLast;
                if (function.body == null) function.body = new BlockStatement();
                if (last.equals(function.body)) {
                    ((BlockStatement) last).add(new ReturnStatement(expression()));
                } else {
                    lastStatement.pop();
                    if (function.body instanceof BlockStatement) {
                        ((BlockStatement) function.body).add(new ReturnStatement(expression()));
                    } else {
                        BlockStatement block = new BlockStatement();
                        block.add(function.body);
                        block.add(new ReturnStatement(expression()));
                        function.body = block;
                    }
                }
            }
        }
        // switch
        if (last instanceof MatchExpression) {
            MatchExpression switcher = (MatchExpression) last;
            Statement statement = null;
            // in case
            if (lookMatch(0, TokenType.IN) && lookMatch(1, TokenType.CASE)) {
                MatchExpression.Pattern pattern = getCase();
                switcher.addCase(pattern);
            }
            // default
            if (lookMatch(0, TokenType.DEFAULT)) {
                MatchExpression.Pattern pattern = getCase();
                switcher.addDefault(pattern);
            }
        }
    }

    private void continueTo() {
        if (lookMatch(0, TokenType.CONTINUE) && lookMatch(1, TokenType.TO)) {
            consume(TokenType.CONTINUE); consume(TokenType.TO);
            if (match(TokenType.METHOD)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof FunctionDefineStatement) break;
                        else lastStatement.pop();
                    } else break;
                }
            } else if (match(TokenType.DO)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof DoWhileStatement) break;
                        else lastStatement.pop();
                    } else break;
                }
            } else if (match(TokenType.FOR)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        Statement last = lastStatement.peek();
                        if (last instanceof ForStatement
                                || last instanceof ForEachStatement
                                || last instanceof ForeachArrayStatement
                                || last instanceof ForeachMapStatement) break;
                        else lastStatement.pop();
                    } else break;
                }
            } else if (match(TokenType.IF)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof IfStatement) {
                            IfStatement last = (IfStatement) lastStatement.peek();
                            if (last.ifStatement instanceof BlockStatement) {
                                lastStatement.push(last.ifStatement);
                            } else {
                                BlockStatement block = new BlockStatement();
                                block.add(last.ifStatement);
                                last.ifStatement = block;
                                lastStatement.push(block);
                            }
                            break;
                        }
                        else lastStatement.pop();
                    } else break;
                }
            } else if (match(TokenType.ELSE)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof IfStatement) {
                            IfStatement last = (IfStatement) lastStatement.peek();
                            if (last.elseStatement != null && last.elseStatement instanceof BlockStatement) {
                                lastStatement.push(last.elseStatement);
                            } else {
                                BlockStatement block = new BlockStatement();
                                block.add(last.elseStatement);
                                last.elseStatement = block;
                                lastStatement.push(block);
                            }
                            break;
                        }
                        else lastStatement.pop();
                    } else break;
                }
            } else if (match(TokenType.SWITCH)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof MatchExpression) break;
                        else lastStatement.pop();
                    } else break;
                }
            } else if (lookMatch(0, TokenType.CASE)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        Statement last = lastStatement.peek();
                        Statement preLast = null;
                        if (lastStatement.size()>=2) {
                            preLast = lastStatement.elementAt(lastStatement.size()-2);
                        }

                        if (last instanceof MatchExpression) {
                            MatchExpression switcher = (MatchExpression) last;
                            MatchExpression.Pattern pattern;
                            if (switcher.patterns.size() == 1) {
                                pattern = switcher.patterns.get(0);
                            } else {
                                pattern = getCase();
                                pattern = switcher.getPattern(pattern);
                            }

                            if (pattern.result != null) {
                                if (!(pattern.result instanceof BlockStatement)) {
                                    BlockStatement block = new BlockStatement();
                                    block.add(pattern.result);
                                    pattern.result = block;
                                }
                                lastStatement.push(pattern.result);
                            } else {
                                pattern.result = new BlockStatement();
                                lastStatement.push(pattern.result);
                            }
                            break;
                        } else lastStatement.pop();
                    } else break;
                }
            } else if (match(TokenType.DEFAULT)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof MatchExpression) {
                            MatchExpression switcher = (MatchExpression) lastStatement.peek();
                            if (switcher.containsDefault()) {
                                MatchExpression.DefaultPattern switcherDefault = switcher.getDefault();
                                lastStatement.push(switcherDefault.result);
                            } else {
                                MatchExpression.DefaultPattern switchDefault = new MatchExpression.DefaultPattern();
                                lastStatement.push(switchDefault.result);
                            }

                            break;
                        } else lastStatement.pop();
                    } else break;
                }
            } else if (match(TokenType.TRY)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof TryCatchStatement) {
                            TryCatchStatement tryCatch = (TryCatchStatement) lastStatement.peek();
                            if (tryCatch.tryBody != null && tryCatch.tryBody instanceof BlockStatement) {
                                lastStatement.push(tryCatch.tryBody);
                            } else {
                                if (tryCatch.tryBody != null) {
                                    BlockStatement block = new BlockStatement();
                                    block.add(tryCatch.tryBody);
                                    tryCatch.tryBody = block;
                                    lastStatement.push(tryCatch.tryBody);
                                } else {
                                    tryCatch.tryBody = new BlockStatement();
                                    lastStatement.push(tryCatch.tryBody);
                                }
                            }
                            break;
                        } else lastStatement.pop();
                    } else break;
                }
            } else if (match(TokenType.CATCH)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof BlockStatement) {
                            if (lastStatement.size() >= 2 && lastStatement.elementAt(lastStatement.size()-2) instanceof TryCatchStatement) {
                                BlockStatement block = (BlockStatement) lastStatement.peek();
                                TryCatchStatement tryCatch = (TryCatchStatement) lastStatement.elementAt(lastStatement.size()-2);
                                if (tryCatch.containsCatchBody(block)) break;
                                else {
                                    lastStatement.pop();
                                    Statement s = tryCatch.getLastCatchBody();
                                    if (s != null) lastStatement.push(s);
                                    break;
                                }
                            }
                            break;
                        } else if (lastStatement.peek() instanceof TryCatchStatement) {
                            TryCatchStatement tryCatch = (TryCatchStatement) lastStatement.peek();
                            Statement s = tryCatch.getLastCatchBody();
                            if (s != null) lastStatement.push(s);
                            break;
                        }
                    } else break;
                }
            } else if (match(TokenType.FINALLY)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof TryCatchStatement) {
                            TryCatchStatement tryCatch = (TryCatchStatement) lastStatement.peek();
                            if (tryCatch.finallyBody != null) {
                                if (tryCatch.finallyBody instanceof BlockStatement) {
                                    lastStatement.push(tryCatch.finallyBody);
                                } else {
                                    BlockStatement block = new BlockStatement();
                                    block.add(tryCatch.finallyBody);
                                    tryCatch.finallyBody = block;
                                    lastStatement.push(block);
                                }
                            } else {
                                BlockStatement block = new BlockStatement();
                                tryCatch.finallyBody = block;
                                lastStatement.push(block);
                            }
                            break;
                        } else if (lastStatement.peek() instanceof BlockStatement
                                && lastStatement.elementAt(lastStatement.size()-2) instanceof TryCatchStatement) {
                            if (lastStatement.peek().equals(lastStatement.elementAt(lastStatement.size()-2))) {
                                break;
                            } else {
                                lastStatement.pop();
                                TryCatchStatement tryCatch = (TryCatchStatement) lastStatement.peek();
                                if (tryCatch.finallyBody != null) {
                                    if (tryCatch.finallyBody instanceof BlockStatement) {
                                        lastStatement.push(tryCatch.finallyBody);
                                    } else {
                                        BlockStatement block = new BlockStatement();
                                        tryCatch.finallyBody = block;
                                        lastStatement.push(block);
                                    }
                                } else {
                                    tryCatch.finallyBody = new BlockStatement();
                                    lastStatement.push(tryCatch.finallyBody);
                                }
                            }
                            break;
                        }
                    } else break;
                }
            } else if (match(TokenType.WHILE)) {
                while (true) {
                    if (!lastStatement.isEmpty()) {
                        if (lastStatement.peek() instanceof WhileStatement) {
                            WhileStatement whiles = (WhileStatement) lastStatement.peek();
                            if (whiles.statement instanceof BlockStatement) {
                                lastStatement.push(whiles.statement);
                            } else {
                                BlockStatement block = new BlockStatement();
                                block.add(whiles.statement);
                                whiles.statement = block;
                                lastStatement.push(block);
                            }
                            break;
                        } else lastStatement.pop();
                    } else break;
                }
            }
        }
    }

    private void checkCommands() {
        removeIt();
        continueTo();
    }

    private void removePreviousFunction() {
        boolean firstFunctionPopped = false;
        while (true) {
            if (!lastStatement.isEmpty()) {
                if (firstFunctionPopped) {
                    if (lastStatement.peek() instanceof FunctionDefineStatement) {
                        break;
                    }
                } else {
                    if (lastStatement.peek() instanceof FunctionDefineStatement) {
                        firstFunctionPopped = true;
                        lastStatement.pop();
                    }
                }
            } else break;
        }
    }

    private void findPreviousScope() {
        while (true) {
            if (lastStatement.peek() instanceof ExprStatement
                    || lastStatement.peek() instanceof AssignmentExpression) {
                if (!lastStatement.isEmpty()) lastStatement.pop();
            } else break;
        }
    }

    private Statement addToLastMethod(Statement statement){
        if (addToLastMode) {
            // remove unnecessary statements from stack
            findPreviousScope();

            // getting the scope or the scope-including statement
            Statement last = lastStatement.peek();

            if (statement instanceof FunctionDefineStatement) {
                // if received statement is function definition we push it to
                // classes hierarchy and to last statements stack
                classesHierarchy.getLast().methods.add(statement);
                removePreviousFunction();
                lastStatement.push(statement);
            } else if (last instanceof BlockStatement) {
                if (statement instanceof ClassStatement) {
                    classesHierarchy.getLast().addClass((ClassStatement) statement);
                } else {
                    ((BlockStatement) last).add(statement);
                    if (statement instanceof DoWhileStatement){
                        lastStatement.push(statement);
                        lastStatement.push(((DoWhileStatement) statement).statement);
                    } else if (statement instanceof ForEachStatement) {
                        lastStatement.push(statement);
                        lastStatement.push(((ForEachStatement) statement).body);
                    } else if (statement instanceof ForStatement) {
                        lastStatement.push(statement);
                        lastStatement.push(((ForStatement) statement).statement);
                    } else if (statement instanceof IfStatement) {
                        lastStatement.push(statement);
                        lastStatement.push(((IfStatement) statement).ifStatement);
                    } else if (statement instanceof TryCatchStatement) {
                        lastStatement.push(statement);
                        lastStatement.push(((TryCatchStatement) statement).tryBody);
                    } else if (statement instanceof WhileStatement) {
                        lastStatement.push(statement);
                        lastStatement.push(((WhileStatement) statement).statement);
                    }
                }
            } else if (last instanceof FunctionDefineStatement) {
                // if the received statement is not the function definition and
                // the last statement is function
                // we add it to the function and to the last stack
                FunctionDefineStatement lastMethod = (FunctionDefineStatement) last;
                if (lastMethod.body instanceof BlockStatement) {
                    BlockStatement block = (BlockStatement) lastMethod.body;
                    if (block.getLast() instanceof ReturnStatement) {
                        block.addPrelast(statement);
                    } else {
                        block.add(statement);
                    }
                } else {
                    Statement temp = lastMethod.body;
                    BlockStatement newBody = new BlockStatement();
                    if (temp instanceof ReturnStatement) {
                        newBody.add(statement);
                        newBody.add(temp);
                    } else {
                        newBody.add(temp);
                        newBody.add(statement);
                    }
                    lastMethod.body = newBody;
                }
                lastStatement.push(statement);
            } else if (last instanceof DoWhileStatement) {
                DoWhileStatement doWhile = (DoWhileStatement) last;
                if (doWhile.statement != null) {
                    if (doWhile.statement instanceof BlockStatement) {
                        ((BlockStatement) doWhile.statement).add(statement);
                    } else {
                        BlockStatement block = new BlockStatement();
                        block.add(doWhile.statement);
                        block.add(statement);
                        doWhile.statement = block;
                    }
                } else {
                    BlockStatement block = new BlockStatement();
                    block.add(statement);
                    doWhile.statement = block;
                }
            } else if (last instanceof ForEachStatement) {
                ForEachStatement forEach = (ForEachStatement) last;
                if (forEach.body != null) {
                    if (forEach.body instanceof BlockStatement) {
                        ((BlockStatement) forEach.body).add(statement);
                    } else {
                        BlockStatement block = new BlockStatement();
                        block.add(forEach.body);
                        block.add(statement);
                        forEach.body = block;
                    }
                } else {
                    BlockStatement block = new BlockStatement();
                    block.add(statement);
                    forEach.body = block;
                }
            } else if (last instanceof ForStatement) {
                ForStatement forLoop = (ForStatement) last;
                if (forLoop.statement != null) {
                    if (forLoop.statement instanceof BlockStatement) {
                        ((BlockStatement) forLoop.statement).add(statement);
                    } else {
                        BlockStatement block = new BlockStatement();
                        block.add(forLoop.statement);
                        block.add(statement);
                        forLoop.statement = block;
                    }
                } else {
                    BlockStatement block = new BlockStatement();
                    block.add(statement);
                    forLoop.statement = block;
                }
            } else if (last instanceof IfStatement) {
                IfStatement ifElse = (IfStatement) last;
                if (ifElse.ifStatement != null) {
                    if (ifElse.ifStatement instanceof BlockStatement) {
                        ((BlockStatement) ifElse.ifStatement).add(statement);
                    } else {
                        BlockStatement block = new BlockStatement();
                        block.add(ifElse.ifStatement);
                        block.add(statement);
                        ifElse.ifStatement = block;
                    }
                } else {
                    BlockStatement block = new BlockStatement();
                    block.add(statement);
                    ifElse.ifStatement = block;
                }
            } else if (last instanceof TryCatchStatement) {
                TryCatchStatement tryCatch = (TryCatchStatement) last;
                if (tryCatch.tryBody != null) {
                    if (tryCatch.tryBody instanceof BlockStatement) {
                        ((BlockStatement) tryCatch.tryBody).add(statement);
                    } else {
                        BlockStatement block = new BlockStatement();
                        block.add(tryCatch.tryBody);
                        block.add(statement);
                        tryCatch.tryBody = block;
                    }
                } else {
                    BlockStatement block = new BlockStatement();
                    block.add(statement);
                    tryCatch.tryBody = block;
                }
            } else if (last instanceof WhileStatement) {
                WhileStatement whiler = (WhileStatement) last;
                if (whiler.statement != null) {
                    if (whiler.statement instanceof BlockStatement) {
                        ((BlockStatement) whiler.statement).add(statement);
                    } else {
                        BlockStatement block = new BlockStatement();
                        block.add(whiler.statement);
                        block.add(statement);
                        whiler.statement = block;
                    }
                } else {
                    BlockStatement block = new BlockStatement();
                    block.add(statement);
                    whiler.statement = block;
                }
            } else {
                // if cannot define where to push the statement
                // push it to the last method body
                FunctionDefineStatement lastMethod = (FunctionDefineStatement) classesHierarchy.getLast().methods.getLast();
                if (lastMethod.body instanceof BlockStatement) {
                    ((BlockStatement) lastMethod.body).add(statement);
                } else {
                    Statement temp = lastMethod.body;
                    BlockStatement newBody = new BlockStatement();
                    newBody.add(temp);
                    newBody.add(statement);
                    lastMethod.body = newBody;
                }
            }
        }
        return statement;
    }

    private Statement parseStartingFromBlock(BlockStatement result){
        pos = 0;
        while (!match(TokenType.EOF)) {
            try {
                result.add(statementOrBlock());
            } catch (Exception ex) {
                parseErrors.add(ex, getErrorLine());
                recover();
            }
        }
        return result;
    }
    
    private int getErrorLine() {
        if (size == 0) return 0;
        if (pos >= size) return tokens.get(size - 1).getRow();
        return tokens.get(pos).getRow();
    }
    
    private void recover() {
        int preRecoverPosition = pos;
        for (int i = preRecoverPosition; i <= size; i++) {
            pos = i;
            try {
                statement();
                // successfully parsed,
                pos = i; // restore position
                return;
            } catch (Exception ex) {
                // fail
            }
        }
    }

    private BlockStatement block(BlockStatement block) {
        if (block == null) block = new BlockStatement();

        match(TokenType.LBRACE);
        boolean wasAddToLast = addToLastMode;
        addToLastMode = false;
        while (!match(TokenType.RBRACE) && !lookMatch(0, TokenType.CATCH)) {
            if (get(0) == EOF) break;
            if (lookMatch(0, TokenType.CATCH) || lookMatch(0, TokenType.EOF)) {
                break;
            }
            if (lookMatch(0, TokenType.RBRACE)) {
                consume(TokenType.RBRACE);
                break;
            }

            block.add(statementOrBlock());
        }
        addToLastMode = wasAddToLast;
        return block;
    }

    private Statement classStatement(List<TokenType> modifiers){
        if (lookMatch(0, TokenType.ADD)) consume(TokenType.ADD);
        if (modifiers == null) {
            modifiers = modifiers();
        }

        ClassName name = null;
        ClassName extending = null;
        List<ClassName> implementing = null;

        if (lookMatch(0, TokenType.GENERIC)) consume(TokenType.GENERIC);
        if (lookMatch(0, TokenType.CLASS)) consume(TokenType.CLASS);
        if (!lookMatch(0, TokenType.EOF)) name = className();
        if (lookMatch(0, TokenType.EXTENDS)) {
            consume(TokenType.EXTENDS);
            extending = className();
        }

        if (lookMatch(0, TokenType.IMPLEMENTS)) {
            consume(TokenType.IMPLEMENTS);
            implementing = new ArrayList<>();

            while (true) {
                if (lookMatch(0, TokenType.WORD) || lookMatch(0, TokenType.NUMBER)) {
                    ClassName className = className();
                    implementing.add(className);
                } else break;
            }
        }

        ClassStatement classStatement = new ClassStatement(modifiers, name, extending, implementing);
        classesHierarchy.add(classStatement);
        if (lookMatch(0, TokenType.WITH)) {
            consume(TokenType.WITH); consume(TokenType.MAIN);
            List<TokenType> modifs = new ArrayList<>(Arrays.asList(TokenType.PUBLIC, TokenType.STATIC));
            ClassName className = new ClassName("String");
            className.setDimension(new DimensionValue().addDimen(DimensionValue.GENERIC));
            VariableExpression var = new VariableExpression("arguments");
            var.set(className);

            Arguments arguments = new Arguments();
            arguments.addOptional(null, var);

            classesHierarchy.getLast()
                    .methods.add(new FunctionDefineStatement(modifs, null, "main", arguments, new BlockStatement()));
        }
        if (lookMatch(0, TokenType.FROM) && lookMatch(1, TokenType.PACKAGE)) {
            Expression pack = qualifiedName();
            classesHierarchy.getFirst().packageName = pack.toString();
        }
        if (lookMatch(0, TokenType.LBRACE)) consume(TokenType.LBRACE);
        while (true) {
            if (get(0) == EOF) break;
            Statement statement = statement();
            if (statement instanceof Stopper) break;
        }
        if (lookMatch(0, TokenType.RBRACE)) consume(TokenType.RBRACE);

        return classStatement;
    }
    
    private Statement statementOrBlock() {
        if (lookMatch(0, TokenType.EOF)) return new BlockStatement();
        if (lookMatch(0, TokenType.LBRACE)) return block(new BlockStatement());  //     <<< ========== ??????????????
        return statement();
    }

    private List<TokenType> modifiers(){
        List<TokenType> modifiers = new ArrayList<>();
        while (true) {
            Token current = get(0);
            if (Tokens.isModifier(current.getType())) {
                modifiers.add(current.getType());
                consume(current.getType());
            } else break;
        }
        if (modifiers.isEmpty()) modifiers = null;
        return modifiers;
    }

    private boolean stopLoop() {
        if ((lookMatch(0, TokenType.GENERATE) && (lookMatch(1, TokenType.CONSTRUCTOR)
                || lookMatch(1, TokenType.CONSTRUCTORS) || lookMatch(1, TokenType.GETTER)
                || lookMatch(1, TokenType.GETTERS) || lookMatch(1, TokenType.SETTER)
                || lookMatch(1, TokenType.SETTERS)))
                || (lookMatch(0, TokenType.ADD)
                && (Tokens.isModifier(get(1).getType()) || lookMatch(1, TokenType.FIELD)
                || lookMatch(1, TokenType.VARIABLE) || lookMatch(1, TokenType.METHOD)
                || lookMatch(1, TokenType.GLOBAL)))
                || (lookMatch(0, TokenType.GET) && lookMatch(1, TokenType.FIELD))
                || (lookMatch(0, TokenType.CALL) && lookMatch(1, TokenType.METHOD))
                || (lookMatch(0, TokenType.CAST) && lookMatch(1, TokenType.TO))
                || (lookMatch(0, TokenType.INITIALISED) && lookMatch(1, TokenType.BY))
                || (lookMatch(0, TokenType.AT_COMMAND) && lookMatch(1, TokenType.NUMBER))
                || (lookMatch(0, TokenType.AT_COMMAND) && !Tokens.restrictedForVariables(get(1).getType()))
                || (lookMatch(0, TokenType.IN) && lookMatch(1, TokenType.CASE))
                || lookMatch(0, TokenType.DEFAULT) || lookMatch(0, TokenType.CASE)
                || lookMatch(0, TokenType.RBRACE)
                || (lookMatch(0, TokenType.REMOVE) && lookMatch(1, TokenType.IT))
                || (lookMatch(0, TokenType.DELETE) && lookMatch(1, TokenType.IT))
                || (lookMatch(0, TokenType.INITIALISE) && lookMatch(1, TokenType.IT) && lookMatch(2, TokenType.BY))
                || (lookMatch(0, TokenType.CONTINUE) && lookMatch(1, TokenType.TO))
                || lookMatch(0, TokenType.EOF)
                || (get(0) == EOF)
                || isGoToLine()) {
            return true;
        } else return false;
    }

    private boolean isGoToLine() {
        if ((lookMatch(0, TokenType.GO) && lookMatch(1, TokenType.TO) && lookMatch(2, TokenType.LINE))
                || (lookMatch(0, TokenType.MOVE) && lookMatch(1, TokenType.TO) && lookMatch(2, TokenType.LINE))
                || (lookMatch(0, TokenType.MOVE) && lookMatch(1, TokenType.CURSOR) && lookMatch(2, TokenType.TO) && lookMatch(3, TokenType.LINE))) {
            return true;
        } else return false;
    }

    private void goToLine() {
        if (lookMatch(0, TokenType.GO) && lookMatch(1, TokenType.TO) && lookMatch(2, TokenType.LINE)) {
            consume(TokenType.GO); consume(TokenType.TO); consume(TokenType.LINE);
        }
        if (lookMatch(0, TokenType.MOVE) && lookMatch(1, TokenType.TO) && lookMatch(2, TokenType.LINE)) {
            consume(TokenType.MOVE); consume(TokenType.TO); consume(TokenType.LINE);
        }
        if (lookMatch(0, TokenType.MOVE) && lookMatch(1, TokenType.CURSOR) && lookMatch(2, TokenType.TO) && lookMatch(3, TokenType.LINE)) {
            consume(TokenType.MOVE); consume(TokenType.CURSOR); consume(TokenType.TO); consume(TokenType.LINE);
        }
        if (lookMatch(0, TokenType.NUMBER)) {
            int number = ((ValueExpression) value()).value.asInt();
        }
    }

    private void parseImport(){
        String toImport;
        if (match(TokenType.ALL)) {
            toImport = "*";
        } else {
            ClassName className = className();
            className.suppressTemplatingTypes = true;
            toImport = className.toString();
        }
        if (match(TokenType.FROM)) {
            List<Expression> indicies = variableSuffix();
            String imp = Utils.toStringExpression(indicies, ".");
            imp = imp + "." + toImport;
            classesHierarchy.getFirst().imports.add(imp);
        }
    }

    private void removeIt() {
        if ((lookMatch(0, TokenType.REMOVE) && lookMatch(1, TokenType.IT))
                || (lookMatch(0, TokenType.DELETE) && lookMatch(1, TokenType.IT))) {
            if (lookMatch(0, TokenType.REMOVE) && lookMatch(1, TokenType.IT)) {
                consume(TokenType.REMOVE); consume(TokenType.IT);
            } else if (lookMatch(0, TokenType.DELETE) && lookMatch(1, TokenType.IT)) {
                consume(TokenType.DELETE); consume(TokenType.IT);
            }

            if (!lastStatement.isEmpty()) {
                Statement statement = lastStatement.pop();
                if (statement instanceof FunctionDefineStatement) {
                    if (classesHierarchy.getLast().methods.contains(statement)) {
                        classesHierarchy.getLast().methods.remove(statement);
                    } else if (classesHierarchy.getFirst().methods.contains(statement)) {
                        classesHierarchy.getFirst().methods.remove(statement);
                    }
                    return;
                } else if (classesHierarchy.getLast().fields.contains(statement)) {
                    classesHierarchy.getLast().fields.remove(statement);
                    return;
                } else if (classesHierarchy.getFirst().fields.contains(statement)) {
                    classesHierarchy.getFirst().fields.remove(statement);
                    return;
                }

                for (Statement current : lastStatement) {
                    if (current instanceof BlockStatement) {
                        if (((BlockStatement) current).statements.remove(statement)) break;
                    }
                }
            }
        }
    }

    private void addMain(){
        boolean containsMainAlready = false;
        for (Statement method : classesHierarchy.getFirst().methods) {
            if (method instanceof FunctionDefineStatement) {
                FunctionDefineStatement m = (FunctionDefineStatement) method;
                if (m.name.equals("main")) {
                    containsMainAlready = true;
                    break;
                }
            }
        }

        if (containsMainAlready) {
            Snackbar.make(context.convertButton, context.getString(R.string.main_exist),
                    Snackbar.LENGTH_LONG).show();
        } else {
            List<TokenType> modifs = new ArrayList<>(Arrays.asList(TokenType.PUBLIC, TokenType.STATIC));
            ClassName className = new ClassName("String");
            className.setDimension(new DimensionValue().addDimen(DimensionValue.GENERIC));
            VariableExpression var = new VariableExpression("arguments");
            var.set(className);

            Arguments arguments = new Arguments();
            arguments.addOptional(null, var);

            classesHierarchy.getFirst()
                    .methods.add(new FunctionDefineStatement(modifs, null, "main", arguments, new BlockStatement()));
        }
    }

    private Statement statement() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        if (match(TokenType.PRINT)) {
            return new PrintStatement(expression());
        }
        if (lookMatch(0, TokenType.RBRACE)) return new Stopper();
        if (match(TokenType.ADD) || lookMatch(0, TokenType.VARIABLE)
                || lookMatch(0, TokenType.CLASS) || lookMatch(0, TokenType.METHOD)) {
            List<TokenType> modifiers = modifiers();
            boolean isGlobal = match(TokenType.GLOBAL);

            if (lookMatch(0, TokenType.FIELD)) {
                return addNew(modifiers, isGlobal);
            }

            if (lookMatch(0, TokenType.VARIABLE)) {
                return addNew(modifiers, isGlobal);
            }

            if (lookMatch(0, TokenType.CLASS)) {
                return classStatement(modifiers);
            }

            if (lookMatch(0, TokenType.METHOD)) {
                return addNew(modifiers, isGlobal);
            }

            if (lookMatch(0, TokenType.NEW)) {
                consume(TokenType.NEW);
                return addNew(modifiers, isGlobal);
            }
        }
        if (match(TokenType.IMPORT)) {
            parseImport();
        }
        if (match(TokenType.GENERATE)) {
            generate();
            return null;
        }
        if (match(TokenType.CREATE)) {
            match(TokenType.NEW);
            return classStatement(null);
        }
        if (lookMatch(0, TokenType.SOUT)) {
            return printLN();
        }
        if (match(TokenType.PRINTLN)) {
            return new PrintlnStatement(expression());
        }
        if (match(TokenType.IF)) {
            return ifElse();
        }
        if (match(TokenType.WHILE)) {
            return whileStatement();
        }
        if (match(TokenType.DO)) {
            return doWhileStatement();
        }
        if (match(TokenType.BREAK)) {
            return new BreakStatement();
        }
        if (match(TokenType.CONTINUE)) {
            return new ContinueStatement();
        }
        if (lookMatch(0, TokenType.STATIC) && lookMatch(1, TokenType.CLASS)) {
            consume(TokenType.STATIC);
            consume(TokenType.CLASS);
            return callStaticClass();
        }
        if (match(TokenType.TRY)) {
            return tryCatchStatement();
        }
        if (match(TokenType.RETURN)) {
            return new ReturnStatement(expression());
        }
        if (match(TokenType.FOR)) {
            return forStatement();
        }
        if (match(TokenType.DEFINE)) {
            return addNew(null, false);
        }
        if (match(TokenType.SWITCH)) {
            return switcher();
        }
        if (lookMatch(0, TokenType.INITIALISED) && lookMatch(1, TokenType.BY)) {
            return null;
        }
        if (isGoToLine()) {
            goToLine();
        }
        if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LPAREN)) {
            return new ExprStatement(functionChain(qualifiedName()));
        }
        return assignmentStatement();
    }

    private Statement callStaticClass() {
        ClassName className = className();
        List<Expression> indicies = new ArrayList<>();
        while (true) {
            Expression expression = null;
            if (lookMatch(0, TokenType.GET) && lookMatch(1, TokenType.FIELD)) {
                consume(TokenType.GET); consume(TokenType.FIELD);
                expression = parseVariableName();
            } else if (lookMatch(0, TokenType.CALL) && lookMatch(1, TokenType.METHOD)) {
                consume(TokenType.CALL); consume(TokenType.METHOD);
                Expression e = parseVariableName();
                expression = new FunctionalExpression(e);
            }

            indicies.add(expression);

            if ((lookMatch(0, TokenType.GET) && lookMatch(1, TokenType.FIELD))
                    || (lookMatch(0, TokenType.CALL) && lookMatch(1, TokenType.METHOD))) {
                continue;
            } else {
                break;
            }
        }

        ContainerAccessExpression c = new ContainerAccessExpression(new ValueExpression(className), indicies);
        //if (addToLastMode) return addToLastMethod(new ExprStatement(c));
        return new ExprStatement(c);
    }

    private void generate(){
        boolean isGlobal = match(TokenType.GLOBAL);
        if (lookMatch(0, TokenType.CONSTRUCTOR) || lookMatch(0, TokenType.CONSTRUCTORS)) {
            boolean constructors = match(TokenType.CONSTRUCTORS);
            if (!constructors) consume(TokenType.CONSTRUCTOR);

            ClassStatement classStatement;
            if (isGlobal) classStatement = classesHierarchy.getFirst();
            else classStatement = classesHierarchy.getLast();
            ClassName name = classStatement.name;

            Arguments arguments = new Arguments();
            if (!classStatement.fields.isEmpty()) {
                for (Statement statement : classStatement.fields) {
                    if (statement instanceof AssignmentExpression) {
                        VariableExpression var = new VariableExpression((VariableExpression) ((AssignmentExpression) statement).target);
                        var.showModifiers = false;
                        arguments.addOptional(null, var);
                    }
                }
            }

            BlockStatement body = new BlockStatement();
            if (!classStatement.fields.isEmpty()) {
                for (Statement statement : classStatement.fields) {
                    if (statement instanceof AssignmentExpression) {
                        try {
                            VariableExpression variable = (VariableExpression) ((AssignmentExpression) statement).target;
                            ContainerAccessExpression theses =
                                    new ContainerAccessExpression(new VariableExpression("this"),
                                            new ArrayList<Expression>(Arrays.asList(new VariableExpression(variable.name))),
                                            true);
                            theses.rootIsVariable = true;
                            AssignmentExpression anotherField =
                                    new AssignmentExpression(null, (Accessible) theses,
                                            new VariableExpression(variable.name));
                            body.statements.add(anotherField);
                        } catch (Exception e) { }
                    }
                }
            }
            List<TokenType> mod = new ArrayList<>(Arrays.asList(TokenType.PUBLIC));
            FunctionDefineStatement fds = new FunctionDefineStatement(mod, null, name.toString(), arguments, body);
            fds.isConstructor = true;
            classStatement.construstos.add(fds);
            if (constructors && !classStatement.fields.isEmpty()) {
                ClassName className = classStatement.name;
                FunctionDefineStatement defaultConstr =
                        new FunctionDefineStatement(new ArrayList<TokenType>(Arrays.asList(TokenType.PUBLIC)), null,
                                className.toString(), new Arguments(), new BlockStatement());
                defaultConstr.isConstructor = true;
                classStatement.construstos.add(0, defaultConstr);
            }
        }
        match(TokenType.AND);
        if (match(TokenType.GETTERS)) {
            ClassStatement classStatement;
            if (isGlobal) classStatement = classesHierarchy.getFirst();
            else classStatement = classesHierarchy.getLast();

            if (!classStatement.fields.isEmpty()) {
                for (Statement statement : classStatement.fields) {
                    if (statement instanceof AssignmentExpression) {
                        try {
                            VariableExpression variable = (VariableExpression) ((AssignmentExpression) statement).target;
                            // this.<field name>
                            ContainerAccessExpression theses =
                                    new ContainerAccessExpression(new VariableExpression("this"),
                                            new ArrayList<Expression>(Arrays.asList(new VariableExpression(variable.name))),
                                            true);
                            theses.rootIsVariable = true;
                            List<TokenType> modifs = new ArrayList<>(Arrays.asList(TokenType.PUBLIC));
                            String functionName = toUpper(variable.name);
                            Arguments arguments = new Arguments();
                            BlockStatement body = new BlockStatement();
                            body.statements.add(new ReturnStatement(theses));
                            classStatement.methods.add(new FunctionDefineStatement(modifs, (ClassName) variable.get(),
                                    "get" + functionName, arguments, body));
                        } catch (Exception e) { }
                    }
                }
            }
        }
        match(TokenType.AND);
        if (match(TokenType.SETTERS)) {
            ClassStatement classStatement;
            if (isGlobal) classStatement = classesHierarchy.getFirst();
            else classStatement = classesHierarchy.getLast();

            if (!classStatement.fields.isEmpty()) {
                for (Statement statement : classStatement.fields) {
                    if (statement instanceof AssignmentExpression) {
                        try {
                            VariableExpression variable = new VariableExpression((VariableExpression) ((AssignmentExpression) statement).target);
                            ContainerAccessExpression theses =
                                    new ContainerAccessExpression(new VariableExpression("this"),
                                            new ArrayList<Expression>(Arrays.asList(new VariableExpression(variable.name))),
                                            true);
                            theses.rootIsVariable = true;
                            AssignmentExpression anotherField =
                                    new AssignmentExpression(null, (Accessible) theses,
                                            new VariableExpression(variable.name));
                            List<TokenType> modifs = new ArrayList<>(Arrays.asList(TokenType.PUBLIC));
                            String functionName = toUpper(variable.name);
                            Arguments arguments = new Arguments();
                            variable.showModifiers = false;
                            arguments.addOptional(null, variable);
                            BlockStatement body = new BlockStatement();
                            body.statements.add(anotherField);
                            classStatement.methods.add(new FunctionDefineStatement(modifs, null,
                                    "set" + functionName, arguments, body));
                        } catch (Exception e) { }
                    }
                }
            }
        }
        match(TokenType.AND);
        List<TokenType> modifiers = modifiers();
        if (match(TokenType.GETTER)) {
            match(TokenType.FOR);
            VariableExpression var = (VariableExpression) parseVariableName();
            String functionName = "get" + toUpper(var.name);
            BlockStatement body = new BlockStatement();
            body.add(new ReturnStatement(var));

            if (modifiers == null) modifiers = new ArrayList<>(Arrays.asList(TokenType.PUBLIC));

            try {
                Value className = Variables.get(var.name);
                FunctionDefineStatement function =
                        new FunctionDefineStatement(modifiers, className, functionName,
                                new Arguments(), body);
                classesHierarchy.getLast().methods.add(function);
            } catch (Exception e) {
                System.out.println("Error: no such variable with name " + var.name);
            }
        }
        match(TokenType.AND);
        if (match(TokenType.SETTER)) {

            match(TokenType.FOR);
            VariableExpression var = (VariableExpression) parseVariableName();
            String functionName = "set" + toUpper(var.name);

            BlockStatement body = new BlockStatement();
            List<Expression> indicies = new ArrayList<>();
            indicies.add(var);
            ContainerAccessExpression thisField =
                    new ContainerAccessExpression(new VariableExpression("this"), indicies);
            AssignmentExpression a = new AssignmentExpression(null, thisField, var);
            body.add(a);

            VariableExpression param = new VariableExpression(var);
            param.isDefinition = true;
            Arguments args = new Arguments();
            args.addOptional(null, param);

            if (modifiers == null) modifiers = new ArrayList<>(Arrays.asList(TokenType.PUBLIC));

            try {
                FunctionDefineStatement function =
                        new FunctionDefineStatement(modifiers, null, functionName,
                                args, body);
                classesHierarchy.getLast().methods.add(function);
            } catch (Exception e) {
                System.out.println("Error: no such variable with name " + var.name);
            }
        }
    }

    private Statement tryCatchStatement(){
        if (lookMatch(0, TokenType.TRY)) consume(TokenType.TRY);
        Statement tryParams = null;
        if (lookMatch(0, TokenType.WITH)){
            consume(TokenType.WITH);
            tryParams = statement();
        }

        Statement tryBody = block(new BlockStatement());
        List<Pair<Expression, Statement>> catchs = getCatchers();

        if (match(TokenType.FINALLY)) return new TryCatchStatement(tryParams, tryBody, catchs, statementOrBlock());
        return new TryCatchStatement(tryParams, tryBody, catchs);
    }

    private List<Pair<Expression, Statement>> getCatchers() {
        List<Pair<Expression, Statement>> catchs = new ArrayList<>();

        Token curr = get(0);
        while (match(TokenType.CATCH)) {
            boolean autoFilling = false;
            if ((match(TokenType.AND) && match(TokenType.PRINT)) || match(TokenType.PRINT)) {
                VariableExpression variable = new VariableExpression("e" + catchs.size());
                variable.set(new StringValue("Exception"));

                // making body expression
                // this one: ex.printStackTrace();
                List<Expression> e = new ArrayList<>();
                e.add(new FunctionalExpression(new ValueExpression("printStackTrace")));
                ContainerAccessExpression bodyExpression = new ContainerAccessExpression(variable.name, e);

                catchs.add(new Pair(variable, new BlockStatement().add(new ExprStatement(bodyExpression))));
            } else

                // parsing Java's extremely large exception names
                // like: IndexOutOfBoundException
                if ((lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.AND) && lookMatch(2, TokenType.PRINT))
                        || (!Tokens.restrictedForClasses(get(0).getType()) && !Tokens.restrictedForClasses(get(1).getType())
                        && lookMatch(2, TokenType.AND) && lookMatch(3, TokenType.PRINT))
                        || (!Tokens.restrictedForClasses(get(0).getType()) && !Tokens.restrictedForClasses(get(1).getType())
                        && !Tokens.restrictedForClasses(get(2).getType())
                        && lookMatch(3, TokenType.AND) && lookMatch(4, TokenType.PRINT))
                        || (!Tokens.restrictedForClasses(get(0).getType()) && !Tokens.restrictedForClasses(get(1).getType())
                        && !Tokens.restrictedForClasses(get(2).getType()) && !Tokens.restrictedForClasses(get(3).getType())
                        && lookMatch(4, TokenType.AND) && lookMatch(5, TokenType.PRINT))
                        || (!Tokens.restrictedForClasses(get(0).getType()) && !Tokens.restrictedForClasses(get(1).getType())
                        && !Tokens.restrictedForClasses(get(2).getType()) && !Tokens.restrictedForClasses(get(3).getType())
                        && !Tokens.restrictedForClasses(get(4).getType()) && lookMatch(5, TokenType.AND)
                        && lookMatch(6, TokenType.PRINT))
                        || (!Tokens.restrictedForClasses(get(0).getType()) && !Tokens.restrictedForClasses(get(1).getType())
                        && !Tokens.restrictedForClasses(get(2).getType()) && !Tokens.restrictedForClasses(get(3).getType())
                        && !Tokens.restrictedForClasses(get(4).getType()) && !Tokens.restrictedForClasses(get(5).getType())
                        && lookMatch(6, TokenType.AND) && lookMatch(7, TokenType.PRINT))
                        || (!Tokens.restrictedForClasses(get(0).getType()) && !Tokens.restrictedForClasses(get(1).getType())
                        && !Tokens.restrictedForClasses(get(2).getType()) && !Tokens.restrictedForClasses(get(3).getType())
                        && !Tokens.restrictedForClasses(get(4).getType()) && !Tokens.restrictedForClasses(get(5).getType())
                        && !Tokens.restrictedForClasses(get(6).getType())
                        && lookMatch(7, TokenType.AND) && lookMatch(8, TokenType.PRINT))
                        || (!Tokens.restrictedForClasses(get(0).getType()) && !Tokens.restrictedForClasses(get(1).getType())
                        && !Tokens.restrictedForClasses(get(2).getType()) && !Tokens.restrictedForClasses(get(3).getType())
                        && !Tokens.restrictedForClasses(get(4).getType()) && !Tokens.restrictedForClasses(get(5).getType())
                        && !Tokens.restrictedForClasses(get(6).getType()) && !Tokens.restrictedForClasses(get(7).getType())
                        && lookMatch(8, TokenType.AND) && lookMatch(9, TokenType.PRINT))
                        || (!Tokens.restrictedForClasses(get(0).getType()) && !Tokens.restrictedForClasses(get(1).getType())
                        && !Tokens.restrictedForClasses(get(2).getType()) && !Tokens.restrictedForClasses(get(3).getType())
                        && !Tokens.restrictedForClasses(get(4).getType()) && !Tokens.restrictedForClasses(get(5).getType())
                        && !Tokens.restrictedForClasses(get(6).getType()) && !Tokens.restrictedForClasses(get(7).getType())
                        && !Tokens.restrictedForClasses(get(8).getType())
                        && lookMatch(9, TokenType.AND) && lookMatch(10, TokenType.PRINT))
                ) {
                    // making catch parameter -> ( IllegalStateException ex ) <-
                    Value className = parseClassName();
                    VariableExpression variable = new VariableExpression("e" + catchs.size());
                    variable.set(className);

                    // making body expression
                    // this one: ex.printStackTrace();
                    List<Expression> e = new ArrayList<>();
                    e.add(new FunctionalExpression(new ValueExpression("printStackTrace")));
                    ContainerAccessExpression bodyExpression = new ContainerAccessExpression(variable.name, e);
                    BlockStatement block = new BlockStatement().add(new ExprStatement(bodyExpression));
                    catchs.add(new Pair<>((Expression) variable, (Statement) block));
                } else {
                    VariableExpression variable = (VariableExpression) parseVariableName();
                    Statement catchBody = null;
                    if (lookMatch(0, TokenType.LBRACE)) {
                        catchBody = block(new BlockStatement());
                    } else {
                        catchBody = new BlockStatement();
                    }
                    catchs.add(new Pair<>((Expression) variable, (Statement) catchBody));
                }

            if (catchs.isEmpty()) {
                Expression variable = null;
                if (lookMatch(0, TokenType.WORD)) variable = parseVariableName();
                Statement block = block(new BlockStatement());
                catchs.add(new Pair<>(variable, block));
            }
            if (lookMatch(0, TokenType.AND)) consume(TokenType.AND);
            if (lookMatch(0, TokenType.PRINT)) consume(TokenType.PRINT);
            curr = get(0);
        }

        return catchs;
    }

    private Statement assignmentStatement() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        if (match(TokenType.EXTRACT)) {
            return destructuringAssignment();
        }
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        final Expression expression = expression();
        if (expression instanceof Statement) {
            return (Statement) expression;
        }
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        throw new ParseException("Unknown statement: " + get(0));
    }

    private DestructuringAssignmentStatement destructuringAssignment() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        // extract(var1, var2, ...) = ...
        consume(TokenType.LPAREN);
        final List<String> variables = new ArrayList<>();
        while (!match(TokenType.RPAREN)) {
            if (lookMatch(0, TokenType.WORD)) {
                variables.add(consume(TokenType.WORD).getText());
            } else {
                variables.add(null);
            }
            match(TokenType.COMMA);
        }
        consume(TokenType.EQ);
        return new DestructuringAssignmentStatement(variables, expression());
    }

    private Statement ifElse() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        final Expression condition = conditional();
        match(TokenType.THEN);
        if (lookMatch(0, TokenType.EOF)) {
            return new IfStatement(condition, new BlockStatement(), null);
        } else {
            if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
            Statement ifStatement = statementOrBlock();
            if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
            Statement elseStatement = match(TokenType.ELSE) ? statementOrBlock() : null;
            return new IfStatement(condition, ifStatement, elseStatement);
        }
    }

    private Statement whileStatement() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        final Expression condition = expression();
        if (lookMatch(0, TokenType.DO)) consume(TokenType.DO);
        if (lookMatch(0, TokenType.EOF)) {
            return new WhileStatement(condition, new BlockStatement());
        } else {
            if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
            Statement statement = statementOrBlock();
            return new WhileStatement(condition, statement);
        }
    }
    
    private Statement doWhileStatement() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        match(TokenType.DO);
        Statement statement = null;
        Expression condition = null;
        if (lookMatch(0, TokenType.EOF)) {
            return new DoWhileStatement(null, new BlockStatement());
        } else {
            if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
            statement = statementOrBlock();
        }

        match(TokenType.WHILE);
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        if (!lookMatch(0, TokenType.EOF)) condition = expression();
        return new DoWhileStatement(condition, statement);
    }

    private Expression parseVariableName(){
        StringBuilder name = new StringBuilder();
        int num = 0;
        while (!Tokens.restrictedForVariables(get(0).getType())){
            String text = null;

            Token current = get(0);
            consume(current.getType());
            if (!Tokens.restrictedForVariables(current.getType())) {
                if (current.getType() == TokenType.WORD || current.getType() == TokenType.NUMBER) {
                    text = current.getText();
                } else {
                    text = Tokens.toString(current.getType());
                }
            }

            if (num > 0) {
                name.append(toUpper(text));
                num++;
            } else {
                name.append(toLower(text));
                num++;
            }

            if (match(TokenType.SEMICOLON)) break;
            if (stopLoop()) break;
            if (lookMatch(0, TokenType.OF)) break;
        }
        if (Character.isDigit(name.charAt(0))) {
            return new ValueExpression(createNumber(name.toString(), 10));
        }

        VariableExpression variable = new VariableExpression(name.toString());
        if (lookMatch(0, TokenType.OF)) {
            consume(TokenType.OF);
            //consume(TokenType.TYPE);
            Value className = className();
            variable.set(className);
            variable.isDefinition = true;
        }
        return variable;
    }

    private String parseFunctionName(){
        StringBuilder name = new StringBuilder();
        int num = 0;

        while (!Tokens.restrictedForVariables(get(0).getType())){
            String text = null;

            Token current = get(0);
            consume(current.getType());
            if (!Tokens.restrictedForVariables(current.getType())) {
                if (current.getType() == TokenType.WORD || current.getType() == TokenType.NUMBER) {
                    text = current.getText();
                } else {
                    text = Tokens.toString(current.getType());
                }
            }

            if (num > 0) {
                name.append(toUpper(text));
                num++;
            } else {
                name.append(toLower(text));
                num++;
            }

            if (match(TokenType.SEMICOLON)) break;
            if (stopLoop()) break;
            if (lookMatch(0, TokenType.WITH)
                    && (lookMatch(1, TokenType.ARGUMENT) || lookMatch(1, TokenType.ARGUMENTS))) break;
            if (lookMatch(0, TokenType.RETURNING)) break;
            if (Tokens.restrictedForVariables(get(0).getType())) break;
        }
        return name.toString();
    }

    private List<Value> parseComplexClassName(){
        List<Value> names = new ArrayList<>();

        while (true){
            if (lookMatch(0, TokenType.WORD) || !Tokens.restrictedForClasses(get(0).getType())){
                // (map of) string and integer  ==> Map<String, Integer>
                names.add(parseClassName());
            } else {
                if (lookMatch(0, TokenType.AND)) {
                    consume(TokenType.AND);
                    continue;
                }
                if (lookMatch(0, TokenType.SEPARATOR)) {
                    consume(TokenType.SEPARATOR);
                    continue;
                }
                return names;
            }
        }
    }

    private ClassName className(){
        DimensionValue dimension = dimension();

        if (lookMatch(0, TokenType.OF)) consume(TokenType.OF);
        ClassName name = parseClassName();
        name.setDimension(dimension);
        return name;
    }

    private ClassName parseClassName(){
        StringBuilder className = new StringBuilder();

        int randomIndex = 0;
        while(true){
            if (lookMatch(0, TokenType.BY)) {
                consume(TokenType.BY);
                List<Value> types = parseComplexClassName();
                return new ClassName(className.toString(), types);
            } else if (match(TokenType.SEMICOLON) || match(TokenType.CALLED)
                    || lookMatch(0, TokenType.WITH) || lookMatch(0, TokenType.SEPARATOR)
                    || lookMatch(0, TokenType.EOF)
                    || stopLoop()
                    || lookMatch(0, TokenType.EXTENDS) || lookMatch(0, TokenType.IMPLEMENTS)) {
                // pair of entry of string and string separator list of string ==> _Pair<Entry<String, String>, List<String>>
                return new ClassName(className.toString());
            } else if (lookMatch(0, TokenType.AND)) {
                consume(TokenType.AND);
                return new ClassName(className.toString());
            } else if (lookMatch(0, TokenType.NUMBER)) {
                Number number = createNumber(consume(TokenType.NUMBER).getText(), 10);
                className.append(number.intValue());
            } else if (lookMatch(0, TokenType.ANY) && lookMatch(1, TokenType.EXTENDS)) {
                consume(TokenType.ANY);
                consume(TokenType.EXTENDS);
                className.append("? extends ");
                className.append(className());
                return new ClassName(className.toString());
            } else if (!Tokens.restrictedForClasses(get(0).getType()) && (get(0).getType() != TokenType.NUMBER)){
                // pair of string and string ==> _Pair<String, String>
                // word of {string} ==> {String} word
                String str;
                TokenType current = get(0).getType();
                if (current == TokenType.WORD) {
                    str = consume(TokenType.WORD).getText();
                } else {
                    consume(current);
                    str = Tokens.toString(current);
                }
                if (str.equals("int") || str.equals("double") || str.equals("float")
                        || str.equals("long") || str.equals("short") || str.equals("boolean")){
                    className.append(str);
                } else {
                    try {
                        className.append(toUpper(str));
                    } catch (StringIndexOutOfBoundsException ex) {
                        System.out.println("Current: " + get(0));
                        System.out.println("Step: " + randomIndex + "; Str := " + str);
                    }
                }
            } else {

                return new ClassName(className.toString());
            }
            randomIndex++;
        }
    }

    private Statement addNew(List<TokenType> modifiers, boolean isGlobal) {
        if (lookMatch(0, TokenType.VARIABLE) || lookMatch(0, TokenType.FIELD)) {
            boolean isField = lookMatch(0, TokenType.FIELD);
            boolean isVariable = match(TokenType.VARIABLE);
            if (isField) consume(TokenType.FIELD);

            VariableExpression variable = ((VariableExpression) parseVariableName()).setModifiers(modifiers);
            Expression value = null;
            if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
            Statement statement = null;
            if (lookMatch(0, TokenType.EQ)) {
                consume(TokenType.EQ);
                value = castTo(null);
                statement = new AssignmentExpression(null, variable, value);
            } else if (((ClassName) variable.get()).dimension != null
                    && !(((ClassName) variable.get()).dimension.dimens).contains(DimensionValue.GENERIC)) {
                ClassName className = new ClassName((ClassName) variable.get());
                ClassName initName = new ClassName(className);
                initName.setDimensionDeclarative(className.dimension);
                variable.set(initName);
                Expression initialisedBy = null;

                if (lookMatch(0, TokenType.INITIALISED) && lookMatch(1, TokenType.BY)) {
                    consume(TokenType.INITIALISED);
                    consume(TokenType.BY);
                    initialisedBy = expression();
                }
                if (initialisedBy != null) {
                    className = initName;
                }
                AssignmentExpression expression = new AssignmentExpression(null, variable, new NewExpression(className).setInitialisedBy(initialisedBy));
                if (isField) {
                    classesHierarchy.getFirst().fields.add(expression);
                    lastStatement.push(expression);
                    return null;
                } else if (isVariable) {
                    lastStatement.push(expression);
                    return expression;
                } else {
                    lastStatement.push(expression);
                    classesHierarchy.getLast().fields.add(expression);
                }
                return expression;
            } else {
                statement = new AssignmentExpression(null, variable, value);
                ((AssignmentExpression) statement).hideDetails = true;
            }
            if (isField) {
                if (isGlobal) {
                    classesHierarchy.getFirst().fields.add(statement);
                } else classesHierarchy.getLast().fields.add(statement);
                lastStatement.push(statement);
                return null;
            }
            //if (addToLastMode) return addToLastMethod(statement);
            return statement;
        } else if (lookMatch(0, TokenType.CLASS)) {
            match(TokenType.CLASS);
            //if (addToLastMode) return addToLastMethod(classStatement(modifiers));
            return classStatement(modifiers);
        }
        /**
         * check if this is an array
         * one dimensional array of int called i ==> int[] i = {};
         * 4 by 4 array of integer called array ==> int[][] array = new int[4][4]
         * 4 by generic/empty of int called array ==> int[][] array = new int[4][];
         * */
        else if ((lookMatch(0, TokenType.NUMBER) && lookMatch(1, TokenType.DIMENSIONAL))
                || (lookMatch(0, TokenType.NUMBER) && lookMatch(1, TokenType.BY))
                || lookMatch(0, TokenType.ARRAY) || lookMatch(0, TokenType.GENERIC)) {
            DimensionValue dimension = dimension();

            if (lookMatch(0, TokenType.ARRAY)) consume(TokenType.ARRAY);
            if (lookMatch(0, TokenType.OF)) consume(TokenType.OF);

            ClassName name = parseClassName();
            VariableExpression variable = (VariableExpression) parseVariableName();
            variable.set(new ClassName(name).setDimensionDeclarative(dimension));

            Expression initialisedBy = null;

            if (lookMatch(0, TokenType.INITIALISED) && lookMatch(1, TokenType.BY)) {
                consume(TokenType.INITIALISED);
                consume(TokenType.BY);
                initialisedBy = expression();
            }
            name.setDimension(dimension);
            AssignmentExpression expression = new AssignmentExpression(null, variable, new NewExpression(name).setInitialisedBy(initialisedBy));
            if (addToLastMode) return addToLastMethod(expression);
            return expression;
        } else if (match(TokenType.METHOD)) {
            Statement statement = functionDefine(modifiers);
            if (isGlobal) {
                classesHierarchy.getFirst().methods.add(statement);
            } else classesHierarchy.getLast().methods.add(statement);
            removePreviousFunction();
            lastStatement.push(statement);
            return null;
        }
        return null;
    }

    private Statement forStatement() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        int foreachIndex = lookMatch(0, TokenType.LPAREN) ? 1 : 0;
        if (lookMatch(foreachIndex, TokenType.WORD) && lookMatch(foreachIndex + 1, TokenType.COLON)) {
            // for v : arr || for (v : arr)
            return foreachArrayStatement();
        }

        if (lookMatch(0, TokenType.EACH)) consume(TokenType.EACH);

        if (lookMatch(foreachIndex, TokenType.WORD) &&
                (lookMatch(foreachIndex+1, TokenType.WORD) || lookMatch(foreachIndex+1, TokenType.NUMBER) || lookMatch(foreachIndex + 1, TokenType.IN))
                && (lookMatch(foreachIndex+2, TokenType.WORD) || lookMatch(foreachIndex+2, TokenType.NUMBER)
                || lookMatch(foreachIndex + 2, TokenType.IN) || lookMatch(foreachIndex + 2, TokenType.RANGE))
                && (lookMatch(foreachIndex+3, TokenType.WORD) || lookMatch(foreachIndex+3, TokenType.NUMBER)
                || lookMatch(foreachIndex + 3, TokenType.IN) || lookMatch(foreachIndex + 3, TokenType.RANGE) || lookMatch(foreachIndex + 3, TokenType.NUMBER))
                && (lookMatch(foreachIndex+4, TokenType.WORD) || lookMatch(foreachIndex+4, TokenType.NUMBER)
                || lookMatch(foreachIndex + 4, TokenType.IN) || lookMatch(foreachIndex + 4, TokenType.RANGE)
                || lookMatch(foreachIndex + 4, TokenType.NUMBER) || lookMatch(foreachIndex + 4, TokenType.TO))
                && (lookMatch(foreachIndex+5, TokenType.WORD) || lookMatch(foreachIndex+5, TokenType.NUMBER)
                || lookMatch(foreachIndex + 5, TokenType.IN) || lookMatch(foreachIndex + 5, TokenType.RANGE)
                || lookMatch(foreachIndex + 5, TokenType.NUMBER) || lookMatch(foreachIndex + 5, TokenType.TO) || lookMatch(foreachIndex + 5, TokenType.NUMBER))
        ){
            // for index in range 0 to 10
            return forEachInRange();
        }

        // for entry of Map.Entry<String, String> in map
        if (lookMatch(foreachIndex, TokenType.WORD)
                && (lookMatch(foreachIndex+1, TokenType.WORD) || lookMatch(foreachIndex+1, TokenType.NUMBER)
                || lookMatch(foreachIndex+1, TokenType.OF))
                && (lookMatch(foreachIndex+2, TokenType.WORD) || lookMatch(foreachIndex+2, TokenType.NUMBER)
                || lookMatch(foreachIndex+2, TokenType.OF))
                && (lookMatch(foreachIndex+3, TokenType.WORD) || lookMatch(foreachIndex+3, TokenType.NUMBER)
                || lookMatch(foreachIndex+3, TokenType.OF) || lookMatch(foreachIndex+3, TokenType.CALL))
                && (lookMatch(foreachIndex+4, TokenType.WORD) || lookMatch(foreachIndex+4, TokenType.NUMBER)
                || lookMatch(foreachIndex+4, TokenType.OF) || lookMatch(foreachIndex+4, TokenType.CALL))){
            return foreachStatement();
        }

        if (lookMatch(foreachIndex, TokenType.WORD) && lookMatch(foreachIndex + 1, TokenType.COMMA)
                && lookMatch(foreachIndex + 2, TokenType.WORD) && lookMatch(foreachIndex + 3, TokenType.COLON)) {
            // for key, value : arr || for (key, value : arr)
            return foreachMapStatement();
        }

        if (lookMatch(foreachIndex, TokenType.WORD)
                && (lookMatch(foreachIndex+1, TokenType.WORD) || lookMatch(foreachIndex+1, TokenType.NUMBER)
                || lookMatch(foreachIndex+1, TokenType.OF))
                && (lookMatch(foreachIndex+2, TokenType.WORD) || lookMatch(foreachIndex+2, TokenType.NUMBER)
                || lookMatch(foreachIndex+2, TokenType.WHEN))){
            //System.out.println("Entered custom foreach");
            //"for a1 of integer = 2 when a1 lesser or equals to ten then a one increment"
            //VariableExpression variableName = parseVariableName();
            Statement initialisation = null;
            if (!lookMatch(0, TokenType.EOF)) initialisation = assignmentStatement();
            Expression termination = null;
            if (lookMatch(0, TokenType.WHEN)) {
                consume(TokenType.WHEN);
                termination = expression();
            }
            Statement increment = null;
            if (lookMatch(0, TokenType.THEN)) {
                consume(TokenType.THEN);
                increment = assignmentStatement();
            }
            Statement statement = null;
            if (lookMatch(0, TokenType.EOF)) {
                statement = new BlockStatement();
            } else statement = statementOrBlock();
            return new ForStatement(initialisation, termination, increment, statement);
        }

        // for (init, condition, increment) body
        boolean optParentheses = match(TokenType.LPAREN);
        final Statement initialization = assignmentStatement();
        consume(TokenType.COMMA);
        final Expression termination = expression();
        consume(TokenType.COMMA);
        final Statement increment = assignmentStatement();
        if (optParentheses) consume(TokenType.RPAREN); // close opt parentheses
        final Statement statement = statementOrBlock();
        return new ForStatement(initialization, termination, increment, statement);
    }

    private ForEachStatement foreachStatement(){
        return new ForEachStatement(null, null, null);
    }

    private ForStatement forEachInRange() {
        // for each index in range 0 to 10
        // for index in range 0 to 10 incremented by 2

        VariableExpression variable = (VariableExpression) parseVariableName();
        if (lookMatch(0, TokenType.IN)) consume(TokenType.IN);
        if (lookMatch(0, TokenType.RANGE)) consume(TokenType.RANGE);
        if (lookMatch(0, TokenType.OF)) consume(TokenType.OF);
        if (lookMatch(0, TokenType.FROM)) consume(TokenType.FROM);

        ValueExpression firstArgument = null;
        if (lookMatch(0, TokenType.NUMBER)) {
            firstArgument = (ValueExpression) value();

            if (!Variables.isExists(variable.name)){
                System.out.println("doesn't exist");
                try {
                    switch (((NumberValue) ((ValueExpression) firstArgument).value).getType()) {
                        case DOUBLE:
                            System.out.println("double");
                            variable.set(firstArgument.value, new StringValue("double"));
                            break;
                        case INT:
                            System.out.println("int");
                            variable.set(firstArgument.value, new StringValue("int"));
                            break;
                        default:
                            System.out.println("default");
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Exception in forEachInRange()");
                }
            } else variable.set(firstArgument.value);

        } else if (lookMatch(0, TokenType.WORD)) {
            String firstArg = consume(TokenType.WORD).getText();
            firstArgument = new ValueExpression("'" + firstArg + "'");
            if (!Variables.isExists(variable.name)) {
                variable.set(firstArgument.value, new StringValue("char"));
            } else variable.set(firstArgument.value);
        }

        ConditionalExpression.Operator operator = null;
        if (lookMatch(0, TokenType.TO)) consume(TokenType.TO);
        if (lookMatch(0, TokenType.LT)) {
            operator = ConditionalExpression.Operator.LT;
            consume(TokenType.LT);
        }
        if (lookMatch(0, TokenType.LTEQ)) {
            operator = ConditionalExpression.Operator.LTEQ;
            consume(TokenType.LTEQ);
        }
        if (lookMatch(0, TokenType.GT)) {
            operator = ConditionalExpression.Operator.GT;
            consume(TokenType.GT);
        }
        if (lookMatch(0, TokenType.GTEQ)) {
            operator = ConditionalExpression.Operator.GTEQ;
            consume(TokenType.GTEQ);
        }

        Expression secondArgument = null;
        if (lookMatch(0, TokenType.NUMBER)) {
            secondArgument = value();
        } else if (lookMatch(0, TokenType.WORD)) {
            secondArgument = new ValueExpression("'" + consume(TokenType.WORD).getText() + "'");
        }

        final Statement initialisation =
                new AssignmentExpression(null, (Accessible) variable, firstArgument);
        //System.out.println(initialisation.toString());


        Expression termination = null;
        if (operator == null) {
            termination = new ConditionalExpression(ConditionalExpression.Operator.LTEQ,
                    new VariableExpression(variable.name), secondArgument);
        } else termination = new ConditionalExpression(operator, new VariableExpression(variable.name), secondArgument);
        // System.out.println(termination.toString());

        Statement increment = null;

        if ((lookMatch(0, TokenType.PLUSEQ) || lookMatch(0, TokenType.MINUSEQ)
                || lookMatch(0, TokenType.STAREQ) || lookMatch(0, TokenType.SLASHEQ)
                || lookMatch(0, TokenType.PLUSPLUS) || lookMatch(0, TokenType.MINUSMINUS))
                && (lookMatch(1, TokenType.NUMBER))) {
            BinaryExpression.Operator incrementOperator = null;
            if (lookMatch(0, TokenType.PLUSEQ)) {
                incrementOperator = BinaryExpression.Operator.PLUSEQ;
                consume(TokenType.PLUSEQ);
            }
            if (lookMatch(0, TokenType.PLUSPLUS)){
                incrementOperator = BinaryExpression.Operator.PLUSEQ;
                consume(TokenType.PLUSPLUS);
            }
            if (lookMatch(0, TokenType.MINUSEQ)) {
                incrementOperator = BinaryExpression.Operator.MINUSEQ;
                consume(TokenType.MINUSEQ);
            }
            if (lookMatch(0, TokenType.MINUSMINUS)) {
                incrementOperator = BinaryExpression.Operator.MINUSEQ;
                consume(TokenType.MINUSMINUS);
            }
            if (lookMatch(0, TokenType.STAREQ)){
                incrementOperator = BinaryExpression.Operator.STAREQ;
                consume(TokenType.STAREQ);
            }
            if (lookMatch(0, TokenType.SLASHEQ)){
                incrementOperator = BinaryExpression.Operator.SLASHEQ;
                consume(TokenType.SLASHEQ);
            }

            Expression incrementBy = value();
            increment = new AssignmentExpression(incrementOperator, new VariableExpression(variable.name), incrementBy);

            final Statement statement = statementOrBlock();
            return new ForStatement(initialisation, termination, increment, statement);
        } else {
            if (firstArgument.value.asInt() < ((ValueExpression)secondArgument).value.asInt()) {
                increment = new UnaryExpression(UnaryExpression.Operator.INCREMENT_POSTFIX,
                        new VariableExpression(variable.name));
            } else {
                increment = new UnaryExpression(UnaryExpression.Operator.DECREMENT_POSTFIX,
                        new VariableExpression(variable.name));
            }
            final Statement statement = statementOrBlock();
            return new ForStatement(initialisation, termination, increment, statement);
        }
    }

    private ForeachArrayStatement foreachArrayStatement() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        boolean optParentheses = match(TokenType.LPAREN);
        final String variable = consume(TokenType.WORD).getText();
        consume(TokenType.COLON);
        final Expression container = expression();
        if (optParentheses) consume(TokenType.RPAREN); // close opt parentheses
        final Statement statement = statementOrBlock();
        return new ForeachArrayStatement(variable, container, statement);
    }
    
    private ForeachMapStatement foreachMapStatement() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        boolean optParentheses = match(TokenType.LPAREN);
        final String key = consume(TokenType.WORD).getText();
        consume(TokenType.COMMA);
        final String value = consume(TokenType.WORD).getText();
        consume(TokenType.COLON);
        final Expression container = expression();
        if (optParentheses) consume(TokenType.RPAREN); // close opt parentheses
        final Statement statement = statementOrBlock();
        return new ForeachMapStatement(key, value, container, statement);
    }

    private FunctionDefineStatement functionDefine(List<TokenType> modifiers) {
        // add/create/define [new] method <name> [with argument(s) <args>] [returning <type>] block
        // add method chase returning integer block block end
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        if (modifiers == null) {
            modifiers = modifiers();
        }
        // def name(arg1, arg2 = value) { ... }  ||  def name(args) = expr
        final String name = parseFunctionName();
        final Arguments arguments = arguments();
        ClassName returnType = null;
        if (match(TokenType.RETURNING)) returnType = className();
        Statement body;
        if (stopLoop()) {
            body = new BlockStatement();
        } else body = statementBody();
        FunctionDefineStatement defineStatement = new FunctionDefineStatement(modifiers, returnType, name, arguments, body);
        return defineStatement;
    }

    private Arguments arguments() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        // (arg1, arg2, arg3 = expr1, arg4 = expr2)
        if (lookMatch(0, TokenType.WITH)
                && (lookMatch(1, TokenType.PARAMETER) || lookMatch(1, TokenType.PARAMETERS))) {
            consume(TokenType.WITH);
            consume(get(0).getType());
        } else if ((lookMatch(0, TokenType.WITH) && lookMatch(1, TokenType.ARGUMENT))
                || (lookMatch(0, TokenType.WITH) && lookMatch(1, TokenType.ARGUMENTS))
                || lookMatch(0, TokenType.ARGUMENT) || lookMatch(0, TokenType.ARGUMENTS)) {
            match(TokenType.WITH); match(TokenType.ARGUMENT); match(TokenType.ARGUMENTS);
        } else return new Arguments();

        final Arguments arguments = new Arguments();
        boolean startsOptionalArgs = false;
        if (lookMatch(0, TokenType.LPAREN)) consume(TokenType.LPAREN);
        while (!match(TokenType.RPAREN)) {
            if (lookMatch(0, TokenType.INITIALISED) && lookMatch(1, TokenType.BY)) break;
            if (match(TokenType.SEMICOLON)) break;
            if (lookMatch(0, TokenType.RETURNING) || lookMatch(0, TokenType.RETURN)) break;
            if (lookMatch(0, TokenType.LBRACE)) break;
            if ((lookMatch(0, TokenType.ADD) && lookMatch(1 , TokenType.VARIABLE))
                    || (lookMatch(0, TokenType.ADD) && lookMatch(1, TokenType.METHOD))
                    || (lookMatch(0, TokenType.ADD) && Tokens.isModifier(get(1).getType()))) {
                break;
            }

            String name = null;
            Expression arg = qualifiedName();

            if (arg == null) {
                arg = expression();

            } else if (arg instanceof VariableExpression) {
                VariableExpression variable = (VariableExpression) arg;
                if (!variable.isDefinition) {
                    name = ((VariableExpression) arg).name;
                }
            }

            if (match(TokenType.EQ)) {
                startsOptionalArgs = true;
                arguments.addOptional(new StringValue(name), variable());
            } else if (!startsOptionalArgs) {
                if (name != null) {
                    arguments.addRequired(name);
                } else arguments.addOptional(null, arg);
            } else {
                throw new ParseException("Required argument cannot be after optional");
            }
            if (lookMatch(0, TokenType.COMMA)) consume(TokenType.COMMA);
            if (lookMatch(0, TokenType.AND)) consume(TokenType.AND);
            if (match(TokenType.SEMICOLON)) break;
            if (lookMatch(0, TokenType.RETURNING) || lookMatch(0, TokenType.RETURN)) break;
            Token current = get(0);
            if (Tokens.restrictedForVariables(get(0).getType())) break;
        }
        return arguments;
    }
    
    private Statement statementBody() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        if (match(TokenType.RETURN)) {
            return new ReturnStatement(expression());
        }
        return statementOrBlock();
    }
    
    private Expression functionChain(Expression qualifiedNameExpr) {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        // f1()()() || f1().f2().f3() || f1().key
        final Expression expr = function(qualifiedNameExpr);
        if (lookMatch(0, TokenType.LPAREN)) {
            return functionChain(expr);
        }
        if (lookMatch(0, TokenType.DOT) || lookMatch(0, TokenType.CALL)) {
            final List<Expression> indices = variableSuffix();
            if (indices.isEmpty()) return expr;

            if (lookMatch(0, TokenType.LPAREN)) {
                // next function call
                return functionChain(new ContainerAccessExpression(expr, indices));
            }
            // container access
            return new ContainerAccessExpression(expr, indices);
        }
        return expr;
    }

    private FunctionalExpression function(Expression qualifiedNameExpr) {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        // function(arg1, arg2, ...)
        consume(TokenType.LPAREN);
        final FunctionalExpression function = new FunctionalExpression(qualifiedNameExpr);
        while (!match(TokenType.RPAREN)) {
            function.addArgument(expression());
            match(TokenType.COMMA);
            match(TokenType.SEPARATOR);
            match(TokenType.AND);
        }
        return function;
    }
    
    private Expression array() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        // [value1, value2, ...]
        consume(TokenType.LBRACE);
        final List<Expression> elements = new ArrayList<>();
        while (!match(TokenType.RBRACE)) {
            elements.add(expression());
            match(TokenType.COMMA);
        }
        return new ArrayExpression(elements);
    }
    
    private Expression map() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        // {key1 : value1, key2 : value2, ...}
        consume(TokenType.LBRACE);
        final Map<Expression, Expression> elements = new HashMap<>();
        while (!match(TokenType.RBRACE)) {
            final Expression key = primary();
            consume(TokenType.COLON);
            final Expression value = expression();
            elements.put(key, value);
            match(TokenType.COMMA);
        }
        return new MapExpression(elements);
    }

    private MatchExpression.Pattern getCase() {
        match(TokenType.IN); match(TokenType.CASE);
        MatchExpression.Pattern pattern = null;
        final Token current = get(0);
        if (match(TokenType.NUMBER)) {
            // case 0.5:
            pattern = new MatchExpression.ConstantPattern(
                    NumberValue.of(createNumber(current.getText(), 10))
            );
        } else if (match(TokenType.HEX_NUMBER)) {
            // case #FF:
            pattern = new MatchExpression.ConstantPattern(
                    NumberValue.of(createNumber(current.getText(), 16))
            );
        } else if (match(TokenType.TEXT)) {
            // case "text":
            pattern = new MatchExpression.ConstantPattern(
                    new StringValue(current.getText())
            );
        } else if (match(TokenType.DEFAULT)) {
            pattern = new MatchExpression.DefaultPattern();
        } else if (lookMatch(0, TokenType.WORD) || !Tokens.restrictedForVariables(current.getType())) {
            // case value:
            pattern = new MatchExpression.VariablePattern(expression());
        }
        if (pattern == null) {
            throw new ParseException("Wrong pattern in match expression: " + current);
        }

        match(TokenType.COLON);
        if (!lookMatch(0, TokenType.EOF)) pattern.result = statementOrBlock();
        return pattern;
    }

    private MatchExpression switcher() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        // match expression {
        //  case pattern1: result1
        //  case pattern2 if extr: result2
        // }
        Expression expression = null;
        if (!lookMatch(0, TokenType.EOF)) expression = expression();
        match(TokenType.LBRACE);
        final List<MatchExpression.Pattern> patterns = new ArrayList<>();

        boolean wasAddToLastMode = addToLastMode;
        addToLastMode = false;
        do {
            if (lookMatch(0, TokenType.EOF)) break;
            boolean includesCase = match(TokenType.CASE);
            if (!includesCase && !lookMatch(0, TokenType.DEFAULT)) break;
            MatchExpression.Pattern pattern = getCase();
            patterns.add(pattern);
        } while (stopLoop() || !match(TokenType.RBRACE));
        addToLastMode = wasAddToLastMode;
        return new MatchExpression(expression, patterns);
    }
    
    private Expression expression() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        return assignment();
    }

    private Expression assignment() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        if (lookMatch(0, TokenType.TERNARY)) return ternary();
        final Expression assignment = assignmentStrict(null);
        if (assignment != null) {
            return assignment;
        }
        return ternary();
    }

    private Expression assignmentStrict(Expression targetExpr) {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        final int position = pos;
        if (targetExpr == null){
            targetExpr = qualifiedName();
            if (!(targetExpr instanceof Accessible)) {
                pos = position;
                return null;
            }
        }

        final TokenType currentType = get(0).getType();
        if (!ASSIGN_OPERATORS.containsKey(currentType) && !lookMatch(0, TokenType.RBRACE)) {
            pos = position;
            return null;
        }
        match(currentType);

        final BinaryExpression.Operator op = ASSIGN_OPERATORS.get(currentType);
        final Expression expression;
        if (currentType == TokenType.RBRACE) return targetExpr;
        else expression = castTo(null);

        return new AssignmentExpression(op, (Accessible) targetExpr, expression);
    }

    private Expression ternary() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        boolean ternary = match(TokenType.TERNARY);
        match(TokenType.IF);

        Expression result = logicalOr();
        if (ternary) {
            Expression trueExpr = null;
            if (!lookMatch(0, TokenType.EOF)) trueExpr = expression();

            if (lookMatch(0, TokenType.COLON)) consume(TokenType.COLON);
            else match(TokenType.ELSE);

            Expression falseExpr = null;
            if (!lookMatch(0, TokenType.EOF)) falseExpr = expression();
            return new TernaryExpression(result, trueExpr, falseExpr);
        }

        if (match(TokenType.QUESTIONCOLON)) {
            return new BinaryExpression(BinaryExpression.Operator.ELVIS, result, expression());
        }
        return result;
    }
    
    private Expression logicalOr() {
        Expression result = logicalAnd();

        while (true) {
            if (match(TokenType.BARBAR) || match(TokenType.OR)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.OR, result, logicalAnd());
                continue;
            }
            break;
        }

        return result;
    }
    
    private Expression logicalAnd() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression result = bitwiseOr();

        while (true) {
            if (match(TokenType.AMPAMP) || match(TokenType.AND)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.AND, result, bitwiseOr());
                continue;
            }
            break;
        }

        return result;
    }
    
    private Expression bitwiseOr() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression expression = bitwiseXor();

        while (true) {
            if (match(TokenType.BAR)) {
                expression = new BinaryExpression(BinaryExpression.Operator.OR, expression, bitwiseXor());
                continue;
            }
            break;
        }

        return expression;
    }
    
    private Expression bitwiseXor() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression expression = bitwiseAnd();

        while (true) {
            if (match(TokenType.CARET)) {
                expression = new BinaryExpression(BinaryExpression.Operator.XOR, expression, bitwiseAnd());
                continue;
            }
            break;
        }

        return expression;
    }
    
    private Expression bitwiseAnd() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression expression = equality();

        while (true) {
            if (match(TokenType.AMP)) {
                expression = new BinaryExpression(BinaryExpression.Operator.AND, expression, equality());
                continue;
            }
            break;
        }

        return expression;
    }
    
    private Expression equality() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression result = conditional();

        if (match(TokenType.EQ)) {
            return new ConditionalExpression(ConditionalExpression.Operator.EQUALS, result, conditional());
        }
        if (match(TokenType.EXCLEQ)) {
            return new ConditionalExpression(ConditionalExpression.Operator.NOT_EQUALS, result, conditional());
        }

        return result;
    }
    
    private Expression conditional() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression result = shift();

        while (true) {
            if (match(TokenType.LT)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.LT, result, shift());
                continue;
            }
            if (match(TokenType.LTEQ)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.LTEQ, result, shift());
                continue;
            }
            if (match(TokenType.GT)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.GT, result, shift());
                continue;
            }
            if (match(TokenType.GTEQ)) {
                result = new ConditionalExpression(ConditionalExpression.Operator.GTEQ, result, shift());
                continue;
            }
            break;
        }

        return result;
    }
    
    private Expression shift() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression expression = additive();

        while (true) {
            if (match(TokenType.LTLT)) {
                expression = new BinaryExpression(BinaryExpression.Operator.LSHIFT, expression, additive());
                continue;
            }
            if (match(TokenType.GTGT)) {
                expression = new BinaryExpression(BinaryExpression.Operator.RSHIFT, expression, additive());
                continue;
            }
            if (match(TokenType.GTGTGT)) {
                expression = new BinaryExpression(BinaryExpression.Operator.URSHIFT, expression, additive());
                continue;
            }
            if (match(TokenType.DOTDOT)) {
                expression = new BinaryExpression(BinaryExpression.Operator.RANGE, expression, additive());
                continue;
            }
            break;
        }
        return expression;
    }
    
    private Expression additive() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression result = multiplicative();

        while (true) {
            if (match(TokenType.PLUS)) {
                result = new BinaryExpression(BinaryExpression.Operator.ADD, result, multiplicative());
                continue;
            }
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression(BinaryExpression.Operator.SUBTRACT, result, multiplicative());
                continue;
            }
            if (match(TokenType.COLONCOLON)) {
                result = new BinaryExpression(BinaryExpression.Operator.PUSH, result, multiplicative());
                continue;
            }
            if (match(TokenType.AT)) {
                result = new BinaryExpression(BinaryExpression.Operator.AT, result, multiplicative());
                continue;
            }
            if (match(TokenType.CARETCARET)) {
                result = new BinaryExpression(BinaryExpression.Operator.CARETCARET, result, multiplicative());
                continue;
            }
            break;
        }

        return result;
    }
    
    private Expression multiplicative() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        Expression result = unary();

        while (true) {
            if (match(TokenType.STAR)) {
                result = new BinaryExpression(BinaryExpression.Operator.MULTIPLY, result, unary());
                continue;
            }
            if (match(TokenType.SLASH)) {
                result = new BinaryExpression(BinaryExpression.Operator.DIVIDE, result, unary());
                continue;
            }
            if (match(TokenType.PERCENT)) {
                result = new BinaryExpression(BinaryExpression.Operator.REMAINDER, result, unary());
                continue;
            }
            if (match(TokenType.STARSTAR)) {
                result = new BinaryExpression(BinaryExpression.Operator.POWER, result, unary());
                continue;
            }
            break;
        }

        return result;
    }
    
    private Expression unary() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        if (match(TokenType.PLUSPLUS)) {
            return new UnaryExpression(UnaryExpression.Operator.INCREMENT_PREFIX, primary());
        }
        if (match(TokenType.MINUSMINUS)) {
            return new UnaryExpression(UnaryExpression.Operator.DECREMENT_PREFIX, primary());
        }
        if (match(TokenType.MINUS)) {
            return new UnaryExpression(UnaryExpression.Operator.NEGATE, primary());
        }
        if (match(TokenType.EXCL)) {
            return new UnaryExpression(UnaryExpression.Operator.NOT, primary());
        }
        if (match(TokenType.TILDE)) {
            return new UnaryExpression(UnaryExpression.Operator.COMPLEMENT, primary());
        }
        if (match(TokenType.PLUS)) {
            return primary();
        }
        return primary();
    }
    
    private Expression primary() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        if (match(TokenType.LPAREN)) {
            Expression result = expression();
            match(TokenType.RPAREN);
            return result;
        }

        if (match(TokenType.COLONCOLON)) {
            final String functionName = consume(TokenType.WORD).getText();
            return new FunctionReferenceExpression(functionName);
        }
        if (match(TokenType.SWITCH)) {
            return switcher();
        }
        if (match(TokenType.DEFINE)) {
            final Arguments arguments = arguments();
            final Statement statement = statementBody();
            return new ValueExpression(new UserDefinedFunction(arguments, statement));
        }
        return variable();
    }

    private Expression variable() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        // function(...
        if ((lookMatch(0, TokenType.CALL) && lookMatch(1, TokenType.WORD))
                || (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LPAREN))) {
            match(TokenType.CALL);
            return functionChain(qualifiedName());
        }

        final Expression qualifiedNameExpr = qualifiedName();
        if (qualifiedNameExpr != null) {
            // postfix increment/decrement
            if (match(TokenType.PLUSPLUS)) {
                return new UnaryExpression(UnaryExpression.Operator.INCREMENT_POSTFIX, qualifiedNameExpr);
            }
            if (match(TokenType.MINUSMINUS)) {
                return new UnaryExpression(UnaryExpression.Operator.DECREMENT_POSTFIX, qualifiedNameExpr);
            }
            return qualifiedNameExpr;
        }
        if (lookMatch(0, TokenType.LBRACE) && lookMatch(1, TokenType.LBRACE)) {
            return map();
        }
        if (lookMatch(0, TokenType.LBRACE)) {
            return array();
        }
        return value();
    }

    private Expression qualifiedName() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        Expression var;
        // var || var.key[index].key2
        final Token current = get(0);
        if (Tokens.restrictedForVariables(current.getType()) && !lookMatch(0, TokenType.WORD)) return null;
        else {
            var = parseVariableName();
        }
        if (var instanceof ValueExpression) return var;

        final List<Expression> indices = variableSuffix();
        if (indices.isEmpty()) {
            return var == null ? parseVariableName() : var;
        }

        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        return new ContainerAccessExpression(var, indices);
    }

    private List<Expression> variableSuffix() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        // .key1.arr1[expr1][expr2].key2
        if (!lookMatch(0, TokenType.DOT) && !lookMatch(0, TokenType.CALL)
                && !lookMatch(0, TokenType.LBRACKET) && !lookMatch(0, TokenType.AT_COMMAND)) {
            return Collections.emptyList();
        }
        final List<Expression> indices = new ArrayList<>();
        while (lookMatch(0, TokenType.DOT) || lookMatch(0, TokenType.GET)
                || lookMatch(0, TokenType.CALL) || lookMatch(0, TokenType.AT_COMMAND)) {
            if (lookMatch(0, TokenType.DOT) || lookMatch(0, TokenType.CALL) || lookMatch(0, TokenType.GET)) {
                Expression key = null;
                if (lookMatch(0, TokenType.CALL) && lookMatch(1, TokenType.METHOD)) {
                    consume(TokenType.CALL);
                    consume(TokenType.METHOD);
                    key = function(parseVariableName());
                } else if ((lookMatch(0, TokenType.GET) && lookMatch(1, TokenType.FIELD))
                        || match(TokenType.DOT) || match(TokenType.INNER)) {
                    key = qualifiedName();
                }
                indices.add(key);
            }
            if (match(TokenType.AT_COMMAND)) {
                Expression ind;
                if (lookMatch(0, TokenType.NUMBER)) ind = value();
                else ind = expression();
                indices.add(new ElementAccessExpression(ind));
            }
        }
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);
        return indices;
    }

    private Expression castTo(final Expression toCast) {
        Expression expression = null; // what should be casted
        if (toCast == null) {
            expression = expression();
        } else expression = toCast;

        if (lookMatch(0, TokenType.CAST) && lookMatch(1, TokenType.TO)) {
            consume(TokenType.CAST);
            consume(TokenType.TO);

            ClassName className = className();    // type to cast to

            return new CastExpression(className, expression);
        } else return expression;
    }

    private Expression value() {
        if (lookMatch(0, TokenType.SEMICOLON)) consume(TokenType.SEMICOLON);

        final Token current = get(0);
        if (isGoToLine()) {
            goToLine();
        }
        if (match(TokenType.TERNARY)) {
            return ternary();
        }
        if (match(TokenType.NUMBER)) {
            return new ValueExpression(createNumber(current.getText(), 10));
        }
        if (match(TokenType.HEX_NUMBER)) {
            return new ValueExpression(createNumber(current.getText(), 16));
        }
        if (match(TokenType.TEXT)) {
            return new ValueExpression(current.getText());
        }
        if (match(TokenType.NULL)) {
            return new ValueExpression(new NullValue());
        }
        if (match(TokenType.TRUE)) {
            return new ValueExpression(new TrueValue());
        }
        if (match(TokenType.FALSE)) {
            return new ValueExpression(new FalseValue());
        }
        if (match(TokenType.NEW)) {
            return newExpression();
        }
        if (lookMatch(0, TokenType.EMPTY) && lookMatch(1, TokenType.ARRAY)) {
            /**
             * one dimensional array of int called i equals to empty array ==> int[] i = {};
             */
            consume(TokenType.EMPTY); consume(TokenType.ARRAY);
            return new ValueExpression(new ArrayValue());
        }
        if (lookMatch(0, TokenType.LBRACE)) {
            return array();
        }
        if (Tokens.inManagingTokens(get(0).getType())) {
            return null;
        }
        throw new ParseException("Unknown expression: " + current);
    }

    private DimensionValue dimension(){
        DimensionValue dimensions = new DimensionValue();
        ValueExpression dim = null;

        if (lookMatch(0, TokenType.OF)) consume(TokenType.OF);
        while (true) {
            if (lookMatch(0, TokenType.WORD) || lookMatch(0, TokenType.TEXT)
                    || Tokens.restrictedForVariables(get(0).getType())) {
                break;
            }
            if (lookMatch(0, TokenType.NUMBER) && lookMatch(1, TokenType.DIMENSIONAL)) {
                dim = (ValueExpression) value();
                for (int i = 1; i <= dim.value.asInt(); i++) {
                    dimensions.addDimen(null);
                }
            } else if (lookMatch(0, TokenType.GENERIC)) {
                dimensions.addDimen(null);
            } else {
                dim = (ValueExpression) value();
                if (dim != null) {
                    dimensions.addDimen(dim.value.asInt());
                }
            }

            if (!lookMatch(0, TokenType.BY) || lookMatch(0, TokenType.ARRAY)
                    || lookMatch(0, TokenType.DIMENSIONAL)) {
                match(TokenType.DIMENSIONAL);
                break;
            }
            match(TokenType.BY);
        }
        match(TokenType.ARRAY);
        return dimensions.isEmpty() ? null : dimensions;
    }

    private Expression newExpression(){
        Value className = parseClassName();
        List<Expression> args = new ArrayList<>();
        Expression initializedBy = null;

        if ((lookMatch(0, TokenType.WITH) && lookMatch(1, TokenType.ARGUMENT))
                || (lookMatch(0, TokenType.WITH) && lookMatch(1, TokenType.ARGUMENTS))
                || lookMatch(0, TokenType.ARGUMENT) || lookMatch(0, TokenType.ARGUMENTS)) {
            match(TokenType.WITH); match(TokenType.ARGUMENT); match(TokenType.ARGUMENTS);
            while (true) {
                args.add(expression());
                if (!lookMatch(0, TokenType.COMMA) || !lookMatch(0, TokenType.AND)) break;
            }
        }

        if (lookMatch(0, TokenType.INITIALISED) && lookMatch(1, TokenType.BY)) {
            consume(TokenType.INITIALISED);
            consume(TokenType.BY);
            initializedBy = expression();
        }

        return new NewExpression(className, args, initializedBy);
    }
    
    private Number createNumber(String text, int radix) {
        if (text.contains(".")) {
            return Double.parseDouble(text);
        }
        try {
            return Integer.parseInt(text, radix);
        } catch (NumberFormatException nfe) {
            return Long.parseLong(text, radix);
        }
    }
    
    private Token consume(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) throw new ParseException("Token " + current + " doesn't match " + type);
        pos++;
        return current;
    }
    
    private boolean match(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }
    
    private boolean lookMatch(int pos, TokenType type) {
        return get(pos).getType() == type;
    }
    
    private Token get(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= size) return EOF;
        return tokens.get(position);
    }
}
