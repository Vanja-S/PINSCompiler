/**
 * @ Author: turk
 * @ Description: Preverjanje tipov.
 */

package compiler.seman.type;

import static common.RequireNonNull.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import common.Report;
import compiler.common.Visitor;
import compiler.parser.ast.def.*;
import compiler.parser.ast.def.FunDef.Parameter;
import compiler.parser.ast.expr.*;
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
    }

    @Override
    public void visit(Binary binary) {
    }

    @Override
    public void visit(Block block) {
    }

    @Override
    public void visit(For forLoop) {
    }

    @Override
    public void visit(Name name) {
    }

    @Override
    public void visit(IfThenElse ifThenElse) {
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
    }

    @Override
    public void visit(While whileLoop) {
    }

    @Override
    public void visit(Where where) {
    }

    @Override
    public void visit(Defs defs) {
        defs.definitions.forEach(def -> {
            if (def instanceof FunDef) {
                visit((FunDef) def);
            } else if (def instanceof TypeDef) {
                visit((TypeDef) def);
            } else if (def instanceof VarDef) {
                visit((VarDef) def);
            } else {
                Report.error(def.position, "Non-valid definition!");
            }
        });
    }

    @Override
    public void visit(FunDef funDef) {
        List<Type> paramTypes = new ArrayList<Type>();
        funDef.parameters.forEach(param -> {
            visit(param);
            paramTypes.add(types.valueFor(param).get());
        });
        visit(funDef.body);
        if (funDef.type instanceof compiler.parser.ast.type.Array array) {
            visit(array);
            types.store(new Type.Function(paramTypes, types.valueFor(array).get()), funDef);
        } else if (funDef.type instanceof compiler.parser.ast.type.Atom atom) {
            visit(atom);
            types.store(new Type.Function(paramTypes, types.valueFor(atom).get()) , funDef);
        } else if (funDef.type instanceof compiler.parser.ast.type.TypeName typename) {
            visit(typename);
            types.store(new Type.Function(paramTypes, types.valueFor(typename).get()) , funDef);
        } else
            Report.error(funDef.position, "Wrong return type");
    }

    @Override
    public void visit(TypeDef typeDef) {
        if (typeDef.type instanceof compiler.parser.ast.type.Atom B) {
            visit(B);
            types.store(types.valueFor(B).get(), typeDef);
        }
        definitions.store(typeDef, typeDef.type);
    }

    @Override
    public void visit(VarDef varDef) {
        if (varDef.type instanceof compiler.parser.ast.type.Atom atom) {
            visit(atom);
            types.store(types.valueFor(atom).get(), varDef);
        } else if (varDef.type instanceof compiler.parser.ast.type.Array array) {
            visit(array);
            types.store(types.valueFor(array).get(), varDef);
        } else if (varDef.type instanceof compiler.parser.ast.type.TypeName typename) {
            visit(typename);
            types.store(types.valueFor(typename).get(), varDef);
        } else {
            Report.error(varDef.position, "Unknown type");
        }
    }

    @Override
    public void visit(Parameter parameter) {
        if (parameter.type instanceof compiler.parser.ast.type.Array array) {
            visit(array);
            types.store(types.valueFor(array).get(), parameter);
        } else if (parameter.type instanceof compiler.parser.ast.type.Atom atom) {
            visit(atom);
            types.store(types.valueFor(atom).get(), parameter);
        } else if (parameter.type instanceof compiler.parser.ast.type.TypeName typename) {
            visit(typename);
            types.store(types.valueFor(definitions.valueFor(typename).get()).get(), parameter);
        } else
            Report.error(parameter.position, "Wrong parameter type");
    }

    @Override
    public void visit(Array array) {
        if (array.type instanceof compiler.parser.ast.type.Array array2) {
            visit(array2);
            types.store(types.valueFor(array2).get(), array);
        } else if (array.type instanceof compiler.parser.ast.type.Atom atom) {
            visit(atom);
            if (atom.type == compiler.parser.ast.type.Atom.Type.INT) {
                types.store(new Type.Array(array.size, new Type.Atom(Type.Atom.Kind.INT)), array);
            } else if (atom.type == compiler.parser.ast.type.Atom.Type.LOG) {
                types.store(new Type.Array(array.size, new Type.Atom(Type.Atom.Kind.LOG)), array);
            } else if (atom.type == compiler.parser.ast.type.Atom.Type.STR) {
                types.store(new Type.Array(array.size, new Type.Atom(Type.Atom.Kind.STR)), array);
            } else {
                Report.error(atom.position, "Wrong Type");
            }
        } else if (array.type instanceof compiler.parser.ast.type.TypeName typename) {
            visit(typename);
            types.store(types.valueFor(definitions.valueFor(typename).get()).get(), array);
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
        types.store(types.valueFor(definitions.valueFor(name).get()).get(), name);
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
        } else {
            Report.error(expr.position, "Unknown expression!");
        }
    }
}
