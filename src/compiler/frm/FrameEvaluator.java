/**
 * @ Author: turk
 * @ Description: Analizator klicnih zapisov.
 */

package compiler.frm;

import static common.RequireNonNull.requireNonNull;

import compiler.common.Visitor;
import compiler.parser.ast.def.*;
import compiler.parser.ast.def.FunDef.Parameter;
import compiler.parser.ast.expr.*;
import compiler.parser.ast.type.Array;
import compiler.parser.ast.type.Atom;
import compiler.parser.ast.type.TypeName;
import compiler.seman.common.NodeDescription;
import compiler.seman.type.type.Type;

import java.util.ArrayList;
import java.util.List;

public class FrameEvaluator implements Visitor {
    /**
     * Opis definicij funkcij in njihovih klicnih zapisov.
     */
    private NodeDescription<Frame> frames;

    /**
     * Opis definicij spremenljivk in njihovih dostopov.
     */
    private NodeDescription<Access> accesses;

    /**
     * Opis vozlišč in njihovih definicij.
     */
    private final NodeDescription<Def> definitions;

    /**
     * Opis vozlišč in njihovih podatkovnih tipov.
     */
    private final NodeDescription<Type> types;

    /*
     * Števec za statični nivo
     */
    private int staticLevel;

    /*
     * Lista stackOffseta trenutnega staticLevel-a oz. funkcijskega klica
     */
    private List<StackOffsets> stackOffset;

    /*
     * Lista funkcijskih klicev
     */
    private List<Integer> functionCalls;

    public FrameEvaluator(
            NodeDescription<Frame> frames,
            NodeDescription<Access> accesses,
            NodeDescription<Def> definitions,
            NodeDescription<Type> types) {
        requireNonNull(frames, accesses, definitions, types);
        this.frames = frames;
        this.accesses = accesses;
        this.definitions = definitions;
        this.types = types;

        staticLevel = 0;
        stackOffset = new ArrayList<StackOffsets>();

        functionCalls = new ArrayList<Integer>();
    }

    @Override
    public void visit(Call call) {
        if (staticLevel == 0) {
            accesses.store(new Access.Global(types.valueFor(definitions.valueFor(call).get()).get().sizeInBytes(),
                    frames.valueFor(definitions.valueFor(call).get()).get().label), call);
        } else {
            accesses.store(new Access.Local(types.valueFor(definitions.valueFor(call).get()).get().sizeInBytes(),
                    stackOffset.get(staticLevel - 1).getTopLocVarOffset(), staticLevel), call);
        }

        int argSize = call.arguments.stream()
                .map(argument -> types.valueFor(argument).get().sizeInBytesAsParam())
                .reduce(0, Integer::sum);
        functionCalls.set(staticLevel - 1, Math.max(functionCalls.get(staticLevel - 1).intValue(), argSize));
    }

    @Override
    public void visit(Binary binary) {
        binary.left.accept(this);
        binary.right.accept(this);
    }

    @Override
    public void visit(Block block) {
        block.expressions.forEach(expr -> {
            expr.accept(this);
        });
    }

    @Override
    public void visit(For forLoop) {
        forLoop.counter.accept(this);
        forLoop.low.accept(this);
        forLoop.high.accept(this);
        forLoop.step.accept(this);
        forLoop.body.accept(this);
    }

    @Override
    public void visit(Name name) {
        accesses.store(accesses.valueFor(definitions.valueFor(name).get()).get(), name);
    }

    @Override
    public void visit(IfThenElse ifThenElse) {
        ifThenElse.condition.accept(this);
        ifThenElse.thenExpression.accept(this);
        if (ifThenElse.elseExpression.isPresent())
            ifThenElse.elseExpression.get().accept(this);
    }

    @Override
    public void visit(Literal literal) {
    }

    @Override
    public void visit(Unary unary) {
        unary.expr.accept(this);
    }

    @Override
    public void visit(While whileLoop) {
        whileLoop.condition.accept(this);
        whileLoop.body.accept(this);
    }

    @Override
    public void visit(Where where) {
        where.defs.accept(this);
        where.expr.accept(this);
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
        stackOffset.add(new StackOffsets());
        functionCalls.add(Integer.valueOf(0));
        compiler.frm.Frame.Builder Builder = null;
        if (staticLevel == 0) {
            Builder = new Frame.Builder(Frame.Label.named(funDef.name), ++staticLevel);
        } else {
            Builder = new Frame.Builder(Frame.Label.nextAnonymous(), ++staticLevel);
        }

        for (Parameter param : funDef.parameters) {
            param.accept(this);
            Builder.addParameter(types.valueFor(param).get().sizeInBytesAsParam());
        }
        funDef.body.accept(this);

        for (Integer Int : stackOffset.get(staticLevel - 1).locVarStackOffset) {
            Builder.addLocalVariable(-Int.intValue());
        }
        // StaticLink
        Builder.addParameter(4);
        Builder.addFunctionCall(functionCalls.get(staticLevel - 1).intValue());

        staticLevel--;
        frames.store(Builder.build(), funDef);
        stackOffset.remove(staticLevel);
        functionCalls.remove(staticLevel);
    }

    @Override
    public void visit(TypeDef typeDef) {
    }

    @Override
    public void visit(VarDef varDef) {
        if (staticLevel == 0) {
            accesses.store(
                    new Access.Global(types.valueFor(varDef).get().sizeInBytes(), Frame.Label.named(varDef.name)),
                    varDef);
        } else {
            stackOffset.get(staticLevel - 1).addLocVarToStack(types.valueFor(varDef).get().sizeInBytes());
            accesses.store(
                    new Access.Local(types.valueFor(varDef).get().sizeInBytes(),
                            stackOffset.get(staticLevel - 1).getTopLocVarOffset(),
                            staticLevel),
                    varDef);
        }
    }

    @Override
    public void visit(Parameter parameter) {
        stackOffset.get(staticLevel - 1).addParamToStack(types.valueFor(parameter).get().sizeInBytesAsParam());
        accesses.store(
                new Access.Parameter(types.valueFor(parameter).get().sizeInBytesAsParam(),
                        stackOffset.get(staticLevel - 1).paramStackOffset,
                        staticLevel),
                parameter);
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

    /*
     * Razred za hranjenje podatke o skladu vsake funkcije
     */
    private class StackOffsets {
        public int paramStackOffset;
        public ArrayList<Integer> locVarStackOffset;

        public StackOffsets() {
            paramStackOffset = 0;
            locVarStackOffset = new ArrayList<Integer>();
        }

        public void addParamToStack(int size) {
            paramStackOffset += size;
        }

        public void addLocVarToStack(int size) {
            locVarStackOffset.add(Integer.valueOf(-size));
        }

        public int getTopLocVarOffset() {
            return locVarStackOffset.stream().reduce(0, Integer::sum);
        }
    }
}
