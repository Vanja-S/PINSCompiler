/**
 * @ Author: turk
 * @ Description: Generator vmesne kode.
 */

package compiler.ir;

import static common.RequireNonNull.requireNonNull;

import java.lang.Character.UnicodeScript;
import java.util.ArrayList;
import java.util.List;

import common.Constants;
import common.StandardLibrary;
import compiler.common.Visitor;
import compiler.frm.Access;
import compiler.frm.Frame;
import compiler.frm.Access.Global;
import compiler.frm.Frame.Label;
import compiler.ir.chunk.Chunk;
import compiler.ir.code.IRNode;
import compiler.ir.code.expr.*;
import compiler.ir.code.stmt.*;
import compiler.parser.ast.Ast;
import compiler.parser.ast.def.*;
import compiler.parser.ast.def.FunDef.Parameter;
import compiler.parser.ast.expr.*;
import compiler.parser.ast.type.Array;
import compiler.parser.ast.type.Atom;
import compiler.parser.ast.type.TypeName;
import compiler.seman.common.NodeDescription;
import compiler.seman.type.type.Type;

public class IRCodeGenerator implements Visitor {
    /**
     * Preslikava iz vozlišč AST v vmesno kodo.
     */
    private NodeDescription<IRNode> imcCode;

    /**
     * Razrešeni klicni zapisi.
     */
    private final NodeDescription<Frame> frames;

    /**
     * Razrešeni dostopi.
     */
    private final NodeDescription<Access> accesses;

    /**
     * Razrešene definicije.
     */
    private final NodeDescription<Def> definitions;

    /**
     * Razrešeni tipi.
     */
    private final NodeDescription<Type> types;

    /**
     * **Rezultat generiranja vmesne kode** - seznam fragmentov.
     */
    public List<Chunk> chunks = new ArrayList<>();

    private int staticLevel;

    public IRCodeGenerator(
            NodeDescription<IRNode> imcCode,
            NodeDescription<Frame> frames,
            NodeDescription<Access> accesses,
            NodeDescription<Def> definitions,
            NodeDescription<Type> types) {
        requireNonNull(imcCode, frames, accesses, definitions, types);
        this.types = types;
        this.imcCode = imcCode;
        this.frames = frames;
        this.accesses = accesses;
        this.definitions = definitions;
        this.staticLevel = 0;
    }

    @Override
    public void visit(Call call) {
        List<IRExpr> argsExprs = new ArrayList<>();
        for (Expr expr : call.arguments) {
            expr.accept(this);
            argsExprs.add((IRExpr) imcCode.valueFor(expr).get());
        }
        if (StandardLibrary.functions.containsKey(call.name)) {
            imcCode.store(new CallExpr(Label.named(StandardLibrary.functions.get(call.name).label), argsExprs), call);
        } else {
            imcCode.store(new CallExpr(frames.valueFor(call).get().label, argsExprs), call);
        }
    }

    @Override
    public void visit(Binary binary) {
        var leftExpr = getIRExpr(binary.left);
        var rightExpr = getIRExpr(binary.right);

        var operator = BinopExpr.Operator.valueOf(binary.operator.name());
        var binaryExpr = new BinopExpr(leftExpr, rightExpr, operator);

        imcCode.store(binaryExpr, binary);
    }

    @Override
    public void visit(Block block) {
        var statements = new ArrayList<IRStmt>();
        block.expressions.forEach(def -> {
            def.accept(this);
            if (imcCode.valueFor(def).get() instanceof IRExpr s) {
                statements.add(new ExpStmt(s));
            } else if (imcCode.valueFor(def).get() instanceof IRStmt s) {
                statements.add(s);
            }
        });
        imcCode.store(new SeqStmt(statements), block);
    }

    @Override
    public void visit(For forLoop) {
        var stmtList = new ArrayList<IRStmt>();

        var counter = getIRExpr(forLoop.counter);
        var low = getIRExpr(forLoop.low);
        var high = getIRExpr(forLoop.high);

        var ctrLowMove = new MoveStmt(counter, low);

        var labelCondition = new LabelStmt(Label.nextAnonymous());
        var labelStart = new LabelStmt(Label.nextAnonymous());
        var labelEnd = new LabelStmt(Label.nextAnonymous());

        var condition = new BinopExpr(counter, high, BinopExpr.Operator.LT);

        var cJump = new CJumpStmt(condition, labelStart.label, labelEnd.label);

        var body = new ExpStmt(getIRExpr(forLoop.body));

        var increment = new BinopExpr(counter, new ConstantExpr(1), BinopExpr.Operator.ADD);
        var moveIncrement = new MoveStmt(counter, increment);

        var jump = new JumpStmt(labelCondition.label);

        stmtList.add(ctrLowMove);
        stmtList.add(labelCondition);
        stmtList.add(cJump);
        stmtList.add(labelStart);
        stmtList.add(body);
        stmtList.add(moveIncrement);
        stmtList.add(jump);
        stmtList.add(labelEnd);

        imcCode.store(new SeqStmt(stmtList), forLoop);
    }

    @Override
    public void visit(Name name) {
        Access access = accesses.valueFor(definitions.valueFor(name).get()).get();
        if (access instanceof Access.Stack a) {
            IRExpr staticTraversal = NameExpr.FP();
            for (int i = 0; i < (staticLevel - a.staticLevel); i++) {
                staticTraversal = new MemExpr(staticTraversal);
            }
            imcCode.store(
                    new MemExpr(new BinopExpr(staticTraversal, new ConstantExpr(a.offset), BinopExpr.Operator.ADD)),
                    name);
        } else if (access instanceof Global a) {
            imcCode.store(new NameExpr(a.label), name);
        }
    }

    @Override
    public void visit(IfThenElse ifThenElse) {
        var labelThen = new LabelStmt(Label.nextAnonymous());
        var labelElse = new LabelStmt(Label.nextAnonymous());
        var labelEnd = new LabelStmt(Label.nextAnonymous());

        var condition = getIRExpr(ifThenElse.condition);

        var cJump = new CJumpStmt(condition, labelThen.label, labelElse.label);

        var thenExpression = new ExpStmt(getIRExpr(ifThenElse.thenExpression));

        var statements = SeqStmt.empty();

        statements.statements.add(cJump);
        statements.statements.add(labelThen);
        statements.statements.add(thenExpression);

        if (ifThenElse.elseExpression.isPresent()) {
            var elseExpression = new ExpStmt(getIRExpr(ifThenElse.elseExpression.get()));
            statements.statements.add(elseExpression);
        }

        statements.statements.add(labelEnd);
        imcCode.store(statements, ifThenElse);
    }

    @Override
    public void visit(Literal literal) {
        if (literal.type == compiler.parser.ast.type.Atom.Type.INT) {
            imcCode.store(new ConstantExpr(Integer.parseInt(literal.value)), literal);
        } else if (literal.type == compiler.parser.ast.type.Atom.Type.LOG) {
            if (literal.value.equals("true"))
                imcCode.store(new ConstantExpr(1), literal);
            else
                imcCode.store(new ConstantExpr(0), literal);
        } else if (literal.type == compiler.parser.ast.type.Atom.Type.STR) {
            chunks.add(new Chunk.DataChunk(null, literal.value));
        }
    }

    @Override
    public void visit(Unary unary) {
        var unaryExpr = getIRExpr(unary.expr);
        if (unary.operator == Unary.Operator.ADD) {
            imcCode.store(new BinopExpr(new ConstantExpr(0), unaryExpr, BinopExpr.Operator.ADD), unary);
        } else if (unary.operator == Unary.Operator.SUB) {
            imcCode.store(new BinopExpr(new ConstantExpr(0), unaryExpr, BinopExpr.Operator.SUB), unary);
        } else {
            imcCode.store(unaryExpr, unary);
        }
    }

    @Override
    public void visit(While whileLoop) {
        var labelStart = new LabelStmt(Label.nextAnonymous());
        var labelTrue = new LabelStmt(Label.nextAnonymous());
        var labelFalse = new LabelStmt(Label.nextAnonymous());

        var condition = getIRExpr(whileLoop.condition);

        var cJump = new CJumpStmt(condition, labelTrue.label, labelFalse.label);

        var expressions = new ExpStmt(getIRExpr(whileLoop.body));

        var jumpToCondition = new JumpStmt(labelStart.label);

        var statements = new ArrayList<IRStmt>();

        statements.add(labelStart);
        statements.add(cJump);
        statements.add(labelTrue);
        statements.add(expressions);
        statements.add(jumpToCondition);
        statements.add(labelFalse);

        var seq = new SeqStmt(statements);
        imcCode.store(seq, whileLoop);
    }

    @Override
    public void visit(Where where) {
        var exprWhere = getIRExpr(where.expr);
        imcCode.store(exprWhere, where);
    }

    @Override
    public void visit(Defs defs) {
        defs.definitions.forEach(def -> {
            if (def instanceof FunDef funDef) {
                funDef.accept(this);
            } else if (def instanceof TypeDef typeDef) {
                typeDef.accept(this);
            } else if (def instanceof VarDef varDef) {
                varDef.accept(this);
            }
        });
    }

    @Override
    public void visit(FunDef funDef) {
        // Get the label for the function
        ++staticLevel;
        var funLabel = new LabelStmt(frames.valueFor(funDef).get().label);

        // Koda funkcije je mov vrednosti celotnega telesa v vrednost return value, ki
        // je del klicnega zapisa
        var bodyExpr = getIRExpr(funDef.body);
        var RV = NameExpr.FP();

        var move = new MoveStmt(RV, bodyExpr);

        var statements = new ArrayList<IRStmt>();
        statements.add(funLabel);
        statements.add(move);
        // Add the function code to chunks
        chunks.add(new Chunk.CodeChunk(frames.valueFor(funDef).get(), new SeqStmt(statements)));
        --staticLevel;
    }

    @Override
    public void visit(TypeDef typeDef) {

    }

    @Override
    public void visit(VarDef varDef) {
        if (staticLevel == 0)
            chunks.add(new Chunk.GlobalChunk((Access.Global) accesses.valueFor(varDef).get()));
    }

    @Override
    public void visit(Parameter parameter) {
    }

    @Override
    public void visit(Array array) {
    }

    @Override
    public void visit(Atom atom) {

    }

    @Override
    public void visit(TypeName name) {
    }

    private IRExpr getIRExpr(Ast node) {
        node.accept(this);
        return (IRExpr) imcCode.valueFor(node).get();
    }
}
