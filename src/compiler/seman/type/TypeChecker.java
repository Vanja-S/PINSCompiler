/**
 * @ Author: turk
 * @ Description: Preverjanje tipov.
 */

package compiler.seman.type;

import static common.RequireNonNull.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import common.Report;
import common.StandardLibrary;
import compiler.common.Visitor;
import compiler.parser.ast.def.*;
import compiler.parser.ast.def.FunDef.Parameter;
import compiler.parser.ast.expr.*;
import compiler.parser.ast.type.*;
import compiler.seman.common.NodeDescription;
import compiler.seman.type.type.Type;

public class TypeChecker implements Visitor {
    /**
     * Opis vozlišč in njihovih definicij.
     */
    private final NodeDescription<Def> definitions;

    /**
     * Opis vozlišč, ki jim priredimo podatkovne tipe.
     */
    private NodeDescription<Type> types;
    private List<Def> visited;

    public TypeChecker(NodeDescription<Def> definitions, NodeDescription<Type> types) {
        requireNonNull(definitions, types);
        this.definitions = definitions;
        this.types = types;
        this.visited = new ArrayList<>();
    }

    @Override
    public void visit(Call call) {
        if (StandardLibrary.functions.containsKey(call.name)) {
            types.store(StandardLibrary.functions.get(call.name).returnType, call);
            call.arguments.forEach(arg -> {
                arg.accept(this);
            });

            if (call.arguments.size() != StandardLibrary.functions.get(call.name).numOfArgs) {
                Report.error(call.position,
                        "Number of arguments does not comply with the standard function definition for " + call.name);
            }

            for (int i = 0; i < call.arguments.size(); i++) {
                if(!types.valueFor(call.arguments.get(i)).get().equals(StandardLibrary.functions.get(call.name).args.get(i))) {
                    Report.error(call.arguments.get(i).position,
                            "This argument does not match the paramter type in function definition: "
                                    + types.valueFor(call.arguments.get(i)).get() + " cannot be assigned to "
                                    + StandardLibrary.functions.get(call.name).args.get(i));
                }
            }
        } else {
            Optional<Type> funDefReturnType = types.valueFor(definitions.valueFor(call).get());
            if (funDefReturnType.isEmpty()) {
                ((FunDef) definitions.valueFor(call).get()).accept(this);
                funDefReturnType = types.valueFor(definitions.valueFor(call).get());
            }
            types.store(funDefReturnType.get().asFunction().get().returnType, call);
            call.arguments.forEach(arg -> {
                arg.accept(this);
            });
            if (call.arguments.size() != ((FunDef) definitions.valueFor(call).get()).parameters.size()) {
                Report.error(call.position, "Number of arguments does not comply with function definition");
            }
            for (int i = 0; i < call.arguments.size(); i++) {
                if (!types.valueFor(call.arguments.get(i)).get()
                        .equals(types.valueFor(((FunDef) definitions.valueFor(call).get()).parameters.get(i)).get())) {
                    Report.error(call.arguments.get(i).position,
                            "This argument does not match the paramter type in function definition: "
                                    + types.valueFor(call.arguments.get(i)).get() + " cannot be assigned to "
                                    + types.valueFor(((FunDef) definitions.valueFor(call).get()).parameters.get(i))
                                            .get());
                }
            }
        }
    }

    @Override
    public void visit(Binary binary) {
        binary.left.accept(this);
        binary.right.accept(this);

        var opleftType = types.valueFor(binary.left);
        var oprightType = types.valueFor(binary.right);

        if (oprightType.isEmpty() || opleftType.isEmpty())
            Report.error(binary.position, "Illegal types in binary expression");

        var leftType = opleftType.get();
        var rightType = oprightType.get();
        if (rightType.isFunction() && leftType.isFunction()) {
            leftType = leftType.asFunction().get().returnType;
            rightType = rightType.asFunction().get().returnType;
        } else if (rightType.isFunction()) {
            rightType = rightType.asFunction().get().returnType;
        } else if (leftType.isFunction()) {
            leftType = leftType.asFunction().get().returnType;
        }

        if (binary.operator.isArithmetic()) {
            if (leftType.equals(rightType) && leftType.isInt()) {
                types.store(new Type.Atom(Type.Atom.Kind.INT), binary);
            } else
                Report.error(binary.position,
                        "Non-valid types " + leftType + " and " + rightType + " for operation " + binary.operator);
        } else if (binary.operator.isAndOr()) {
            if (leftType.equals(rightType) && leftType.isLog()) {
                types.store(new Type.Atom(Type.Atom.Kind.LOG), binary);
            } else
                Report.error(binary.position,
                        "Non-valid types " + leftType + " and " + rightType + " for operation " + binary.operator);
        } else if (binary.operator.isComparison()) {
            if (leftType.equals(rightType) && (leftType.isInt() || leftType.isLog())) {
                types.store(new Type.Atom(Type.Atom.Kind.LOG), binary);
            } else
                Report.error(binary.position, "Non-valid types " + leftType + " and " + rightType
                        + " for operation " + binary.operator);
        } else if (binary.operator == Binary.Operator.ASSIGN) {
            if (leftType.equals(rightType)
                    && (leftType.isInt() || leftType.isLog() || leftType.isStr())) {
                types.store(rightType, binary);
            } else
                Report.error(binary.position, "Non-valid types " + leftType + " and " + rightType
                        + " for operation " + binary.operator);
        } else if (binary.operator == Binary.Operator.ARR) {
            if (leftType.isArray() && rightType.isInt()) {
                types.store(leftType.asArray().get().type, binary);
            } else
                Report.error(binary.position, "Non-valid types " + leftType + " and " + rightType
                        + " for operation " + binary.operator);
        } else
            Report.error(binary.position, "Wrong expression");
    }

    @Override
    public void visit(Block block) {
        block.expressions.forEach(expr -> {
            expr.accept(this);
        });
        types.store(types.valueFor(block.expressions.get(block.expressions.size() - 1)).get(), block);
    }

    @Override
    public void visit(For forLoop) {
        forLoop.counter.accept(this);
        forLoop.low.accept(this);
        forLoop.high.accept(this);
        forLoop.step.accept(this);
        forLoop.body.accept(this);

        if (types.valueFor(forLoop.counter).isEmpty()) {
            Report.error(forLoop.counter.position, "This variable is not defined, and cannot be used for a counter");
        }

        if (types.valueFor(forLoop.low).get().isInt() && types.valueFor(forLoop.high).get().isInt()
                && types.valueFor(forLoop.step).get().isInt() && types.valueFor(forLoop.counter).get().isInt()) {
            types.store(new Type.Atom(Type.Atom.Kind.VOID), forLoop);
            return;
        }
        Report.error(forLoop.position,
                "Wrong type in for loop in one of these values: high -> " + types.valueFor(forLoop.high).get()
                        + ", low -> " + types.valueFor(forLoop.low).get() + ", step -> "
                        + types.valueFor(forLoop.step).get() + ", counter -> " + types.valueFor(forLoop.counter).get()
                        + "\nThey should all be integer typed");
    }

    @Override
    public void visit(Name name) {
        types.store(types.valueFor(definitions.valueFor(name).get()).get(), name);
    }

    @Override
    public void visit(IfThenElse ifThenElse) {
        ifThenElse.condition.accept(this);
        ifThenElse.thenExpression.accept(this);
        if (ifThenElse.elseExpression.isPresent())
            ifThenElse.elseExpression.get().accept(this);
        if (!types.valueFor(ifThenElse.condition).get().isLog()) {
            Report.error(ifThenElse.position,
                    "Condition statement should be log type not " + types.valueFor(ifThenElse.condition).get());
        }
        types.store(new Type.Atom(Type.Atom.Kind.VOID), ifThenElse);
    }

    @Override
    public void visit(Literal literal) {
        if (literal.type == compiler.parser.ast.type.Atom.Type.INT) {
            types.store(new Type.Atom(Type.Atom.Kind.INT), literal);
        } else if (literal.type == compiler.parser.ast.type.Atom.Type.LOG) {
            types.store(new Type.Atom(Type.Atom.Kind.LOG), literal);
        } else if (literal.type == compiler.parser.ast.type.Atom.Type.STR) {
            types.store(new Type.Atom(Type.Atom.Kind.STR), literal);
        } else {
            Report.error(literal.position, "Invalid literal type");
        }
    }

    @Override
    public void visit(Unary unary) {
        unary.expr.accept(this);
        if (unary.operator == Unary.Operator.ADD || unary.operator == Unary.Operator.SUB) {
            if (!types.valueFor(unary.expr).get().isInt()) {
                Report.error(unary.position, "Expression type " + types.valueFor(unary.expr).get()
                        + " is not valid for - or + unary operation!");
            }
            types.store(new Type.Atom(Type.Atom.Kind.INT), unary);
        } else {
            if (!types.valueFor(unary.expr).get().isLog()) {
                Report.error(unary.position,
                        "Expression type " + types.valueFor(unary.expr).get() + " is not valid for ! unary operation!");
            }
            types.store(new Type.Atom(Type.Atom.Kind.LOG), unary);
        }
    }

    @Override
    public void visit(While whileLoop) {
        whileLoop.condition.accept(this);
        whileLoop.body.accept(this);
        if (!types.valueFor(whileLoop.condition).get().isLog()) {
            Report.error(whileLoop.position,
                    "Condition statement should be log type not " + types.valueFor(whileLoop.condition).get());
        }
        types.store(new Type.Atom(Type.Atom.Kind.VOID), whileLoop);
    }

    @Override
    public void visit(Where where) {
        where.defs.accept(this);
        where.expr.accept(this);

        types.store(types.valueFor(where.expr).get(), where);
    }

    @Override
    public void visit(Defs defs) {
        defs.definitions.forEach(def -> {
            if (!visited.contains(def))
                def.accept(this);
        });
    }

    @Override
    public void visit(FunDef funDef) {
        List<Type> paramTypes = new ArrayList<Type>();
        funDef.parameters.forEach(param -> {
            param.accept(this);
            paramTypes.add(types.valueFor(param).get());
        });
        funDef.type.accept(this);
        types.store(new Type.Function(paramTypes, types.valueFor(funDef.type).get()), funDef);
        funDef.body.accept(this);
        if (!(types.valueFor(funDef.body).get().equals(types.valueFor(funDef.type).get()))) {
            if (types.valueFor(funDef.body).get() instanceof Type.Function funDefBody) {
                if (funDefBody.returnType.equals(types.valueFor(funDef.type).get()))
                    return;
            }
            Report.error(funDef.position, "Function return type " + types.valueFor(funDef.type).get()
                    + " does not match the return type of the body " + types.valueFor(funDef.body).get());
        }
    }

    @Override
    public void visit(TypeDef typeDef) {
        checkRecursion(typeDef);
        typeDef.type.accept(this);
        types.store(types.valueFor(typeDef.type).get(), typeDef);
    }

    @Override
    public void visit(VarDef varDef) {
        checkRecursion(varDef);
        varDef.type.accept(this);
        types.store(types.valueFor(varDef.type).get(), varDef);
    }

    @Override
    public void visit(Parameter parameter) {
        parameter.type.accept(this);
        types.store(types.valueFor(parameter.type).get(), parameter);
    }

    @Override
    public void visit(Array array) {
        array.type.accept(this);
        if (types.valueFor(array.type).isPresent()) {
            types.store(new Type.Array(array.size, types.valueFor(array.type).get()), array);
        } else
            Report.error(array.position, "Wrong array type");
    }

    @Override
    public void visit(Atom atom) {
        if (atom.type == compiler.parser.ast.type.Atom.Type.INT) {
            types.store(new Type.Atom(Type.Atom.Kind.INT), atom);
        } else if (atom.type == compiler.parser.ast.type.Atom.Type.LOG) {
            types.store(new Type.Atom(Type.Atom.Kind.LOG), atom);
        } else if (atom.type == compiler.parser.ast.type.Atom.Type.STR) {
            types.store(new Type.Atom(Type.Atom.Kind.STR), atom);
        } else {
            Report.error(atom.position, "Wrong type");
        }
    }

    @Override
    public void visit(TypeName name) {
        Def nameDef = definitions.valueFor(name).get();
        if (types.valueFor(nameDef).isEmpty()) {
            nameDef.accept(this);
        }
        types.store(types.valueFor(nameDef).get(), name);
    }

    private void checkRecursion(Def def) {
        if (visited.contains(def)) {
            Report.error(def.position, "Definition recursion detected!");
        }
        visited.add(def);
    }
}
