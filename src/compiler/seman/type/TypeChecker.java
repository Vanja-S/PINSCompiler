/**
 * @ Author: turk
 * @ Description: Preverjanje tipov.
 */

package compiler.seman.type;

import static common.RequireNonNull.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.lang.model.type.TypeKind;

import java.util.HashMap;
import java.util.HashSet;

import common.Report;
import compiler.common.Visitor;
import compiler.parser.ast.def.*;
import compiler.parser.ast.def.FunDef.Parameter;
import compiler.parser.ast.expr.*;
import compiler.parser.ast.expr.Binary.Operator;
import compiler.parser.ast.type.*;
import compiler.seman.common.NodeDescription;
import compiler.seman.type.type.Type;
import compiler.seman.type.type.Type.Atom.Kind;

public class TypeChecker implements Visitor {
    /**
     * Opis vozlišč in njihovih definicij.
     */
    private final NodeDescription<Def> definitions;

    /**
     * Opis vozlišč, ki jim priredimo podatkovne tipe.
     */
    private NodeDescription<Type> types;

    public TypeChecker(NodeDescription<Def> definitions, NodeDescription<Type> types) {
        requireNonNull(definitions, types);
        this.definitions = definitions;
        this.types = types;
    }

    @Override
    public void visit(Call call) {
        definitions.valueFor(call).get();
        types.store(types.valueFor(definitions.valueFor(call).get()).get(), call);
    }

    @Override
    public void visit(Binary binary) {
        binary.left.accept(this);
        binary.right.accept(this);

        // Type of Binary Expression
        Optional<Type> binaryType = binaryExprTypes.get(binary.operator);
        if (binaryType.isEmpty()) {
            if (binary.operator == Binary.Operator.ARR) {
                if (!(types.valueFor(definitions.valueFor(binary.left).get()).get().isArray()
                        && types.valueFor(binary.right).get().isInt())) {
                    Report.error(binary.position, "Operand of types " + types.valueFor(binary.left).get() + " and "
                            + types.valueFor(binary.right).get() + " are not applicable to operation "
                            + binary.operator);
                }
                types.store(types.valueFor(definitions.valueFor(binary.left).get()).get().asArray().get().type, binary);
            } else {
                System.out.println("Do tukaj pridemo");
                // It's an assign statement
                if (!(types.valueFor(binary.left).get().equals(types.valueFor(binary.right).get())
                        && (types.valueFor(binary.left).get().isInt() || types.valueFor(binary.left).get().isLog()
                                || types.valueFor(binary.left).get().isStr()))) {
                    Report.error(binary.position, "Operand of types " + types.valueFor(binary.left).get() + " and "
                            + types.valueFor(binary.right).get() + " are not applicable to operation "
                            + binary.operator);
                }
                types.store(types.valueFor(binary.left).get(), binary);
            }
        } else if (binaryType.get().isInt()) {
            if (!(types.valueFor(binary.left).get().isInt() && types.valueFor(binary.right).get().isInt())) {
                Report.error(binary.position, "Operand of types " + types.valueFor(binary.left).get() + " and "
                        + types.valueFor(binary.right).get() + " are not applicable to operation " + binary.operator);
            }
            types.store(binaryType.get(), binary);
        } else if (binaryType.get().isLog()) {
            // Compare types
            if (binaryCompareTypes.contains(binary.operator)) {
                if (!(types.valueFor(binary.left).equals(types.valueFor(binary.right))
                        && (types.valueFor(binary.left).get().isInt() || types.valueFor(binary.left).get().isLog()))) {
                    Report.error(binary.position, "Operand of types " + types.valueFor(binary.left).get() + " and "
                            + types.valueFor(binary.right).get() + " are not applicable to operation "
                            + binary.operator);
                }
            }
            // Logical type
            else {
                if (!(types.valueFor(binary.left).get().isLog() && types.valueFor(binary.right).get().isLog())) {
                    Report.error(binary.position, "Operand of types " + types.valueFor(binary.left).get() + " and "
                            + types.valueFor(binary.right).get() + " are not applicable to operation "
                            + binary.operator);
                }
            }
            types.store(binaryType.get(), binary);
        }

    }

    private final static HashMap<Operator, Optional<Type>> binaryExprTypes;

    static {
        binaryExprTypes = new HashMap<Operator, Optional<Type>>();
        // Integer types
        binaryExprTypes.put(Operator.ADD, Optional.of(new Type.Atom(Kind.INT)));
        binaryExprTypes.put(Operator.SUB, Optional.of(new Type.Atom(Kind.INT)));
        binaryExprTypes.put(Operator.MUL, Optional.of(new Type.Atom(Kind.INT)));
        binaryExprTypes.put(Operator.DIV, Optional.of(new Type.Atom(Kind.INT)));
        binaryExprTypes.put(Operator.MOD, Optional.of(new Type.Atom(Kind.INT)));

        // Logical types
        binaryExprTypes.put(Operator.AND, Optional.of(new Type.Atom(Kind.LOG)));
        binaryExprTypes.put(Operator.OR, Optional.of(new Type.Atom(Kind.LOG)));

        // Logical compare types
        binaryExprTypes.put(Operator.EQ, Optional.of(new Type.Atom(Kind.LOG)));
        binaryExprTypes.put(Operator.NEQ, Optional.of(new Type.Atom(Kind.LOG)));
        binaryExprTypes.put(Operator.LT, Optional.of(new Type.Atom(Kind.LOG)));
        binaryExprTypes.put(Operator.GT, Optional.of(new Type.Atom(Kind.LOG)));
        binaryExprTypes.put(Operator.LEQ, Optional.of(new Type.Atom(Kind.LOG)));
        binaryExprTypes.put(Operator.GEQ, Optional.of(new Type.Atom(Kind.LOG)));

        // Assignment operator
        binaryExprTypes.put(Operator.ASSIGN, Optional.empty());

        // Array operator
        binaryExprTypes.put(Operator.ARR, Optional.empty());
    }

    private final static HashSet<Operator> binaryCompareTypes;

    static {
        binaryCompareTypes = new HashSet<Operator>();

        // Logical compare types
        binaryCompareTypes.add(Operator.EQ);
        binaryCompareTypes.add(Operator.NEQ);
        binaryCompareTypes.add(Operator.LT);
        binaryCompareTypes.add(Operator.GT);
        binaryCompareTypes.add(Operator.LEQ);
        binaryCompareTypes.add(Operator.GEQ);
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

        if (types.valueFor(forLoop.low).isPresent() && types.valueFor(forLoop.high).isPresent()
                && types.valueFor(forLoop.step).isPresent()) {
            if (types.valueFor(forLoop.low).get().isInt() && types.valueFor(forLoop.high).get().isInt()
                    && types.valueFor(forLoop.step).get().isInt()) {
                types.store(new Type.Atom(Type.Atom.Kind.VOID), forLoop);
            }
        } else
            Report.error(forLoop.position,
                    "Wrong type in for loop in one of these values: high -> " + types.valueFor(forLoop.high).get()
                            + ", low -> " + types.valueFor(forLoop.low).get() + ", step -> "
                            + types.valueFor(forLoop.step).get() + "\nThey should all be integer typed");
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
        if(unary.operator == Unary.Operator.ADD || unary.operator == Unary.Operator.SUB) {
            if(!types.valueFor(unary.expr).get().isInt()) {
                Report.error(unary.position, "Expression type " + types.valueFor(unary.expr).get() + " is not valid for - or + unary operation!");
            }
            types.store(new Type.Atom(Type.Atom.Kind.INT), unary);
        }
        else {
            if(!types.valueFor(unary.expr).get().isLog()) {
                Report.error(unary.position, "Expression type " + types.valueFor(unary.expr).get() + " is not valid for ! unary operation!");
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
            if (def instanceof TypeDef typeDef) {
                typeDef.accept(this);
            }
        });

        defs.definitions.forEach(def -> {
            if (def instanceof FunDef funDef) {
                funDef.accept(this);
            } else if (def instanceof TypeDef typeDef) {

            } else if (def instanceof VarDef varDef) {
                varDef.accept(this);
            } else {
                Report.error(def.position, "Non-valid definition!");
            }
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
        definitions.store(funDef, funDef.type);
        funDef.body.accept(this);
        types.store(new Type.Function(paramTypes, types.valueFor(funDef.type).get()), funDef);
        if (!(types.valueFor(funDef.body).get().equals(types.valueFor(funDef.type).get()))) {
            Report.error(funDef.position, "Function return type " + types.valueFor(funDef.type).get()
                    + " does not match the return type of the body " + types.valueFor(funDef.body).get());
        }
    }

    @Override
    public void visit(TypeDef typeDef) {
        typeDef.type.accept(this);
        types.store(types.valueFor(typeDef.type).get(), typeDef);
        definitions.store(typeDef, typeDef.type);
    }

    @Override
    public void visit(VarDef varDef) {
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
        try {
            types.store(types.valueFor(definitions.valueFor(name).get()).get(), name);
        } catch (Exception e) {
            Report.error(name.position, "The type is not defined!");
        }
    }

    private void visit(Expr expr) {
        if (expr instanceof Binary) {
            visit((Binary) expr);
        } else if (expr instanceof Block) {
            visit((Block) expr);
        } else if (expr instanceof Call) {
            visit((Call) expr);
        } else if (expr instanceof For) {
            visit((For) expr);
        } else if (expr instanceof IfThenElse) {
            visit((IfThenElse) expr);
        } else if (expr instanceof Literal) {
            visit((Literal) expr);
        } else if (expr instanceof Name) {
            visit((Name) expr);
        } else if (expr instanceof Unary) {
            visit((Unary) expr);
        } else if (expr instanceof Where) {
            visit((Where) expr);
        } else if (expr instanceof While) {
            visit((While) expr);
        } else
            Report.error(expr.position, "Unknown expression!");
    }
}
