/**
 * @ Author: turk
 * @ Description: Preverjanje in razreševanje imen.
 */

package compiler.seman.name;

import static common.RequireNonNull.requireNonNull;

import common.Report;
import common.VoidOperator;
import compiler.common.Visitor;
import compiler.lexer.Position;
import compiler.parser.ast.def.*;
import compiler.parser.ast.def.FunDef.Parameter;
import compiler.parser.ast.expr.*;
import compiler.parser.ast.expr.Binary.Operator;
import compiler.parser.ast.type.*;
import compiler.seman.common.NodeDescription;
import compiler.seman.name.env.SymbolTable;
import compiler.seman.name.env.SymbolTable.DefinitionAlreadyExistsException;

public class NameChecker implements Visitor {
    /**
     * Opis vozlišč, ki jih povežemo z njihovimi
     * definicijami.
     */
    private NodeDescription<Def> definitions;

    /**
     * Simbolna tabela.
     */
    private SymbolTable symbolTable;

    /**
     * Ustvari nov razreševalnik imen.
     */
    public NameChecker(
            NodeDescription<Def> definitions,
            SymbolTable symbolTable) {
        requireNonNull(definitions, symbolTable);
        this.definitions = definitions;
        this.symbolTable = symbolTable;
    }

    @Override
    public void visit(Call call) {
        try {
            if (!(symbolTable.definitionFor(call.name).get() instanceof FunDef)) {
                throw new Exception(call.name + " is not a FunDef, but a "
                        + symbolTable.definitionFor(call.name).get().getClass());
            }
            for (Expr expr : call.arguments) {
                visit(expr);
            }
        } catch (Exception e) {
            Report.error(call.position, e.getMessage());
        }
    }

    @Override
    public void visit(Binary binary) {
        try {
            if(binary.left instanceof Name) {
                if(symbolTable.definitionFor(((Name)binary.left).name).get() instanceof FunDef)
                    throw new Exception(binary.left + " is a FunDef not a VarDef");
            }
            visit(binary.left);
            visit(binary.right);
        } catch (Exception e) {
            Report.error(binary.position, e.getMessage());
        }
    }

    @Override
    public void visit(Block block) {
        for (Expr expr : block.expressions) {
            visit(expr);
        }
    }

    @Override
    public void visit(For forLoop) {
        visit(forLoop.counter);
        visit(forLoop.low);
        visit(forLoop.high);
        visit(forLoop.step);
        visit(forLoop.body);
    }

    @Override
    public void visit(Name name) {
        try {
            definitions.store(symbolTable.definitionFor(name.name).get(), name);
        } catch (Exception e) {
            Report.error(name.position, "The variable or function " + name.name + " is not declared!");
        }
    }

    @Override
    public void visit(IfThenElse ifThenElse) {
        visit(ifThenElse.condition);
        visit(ifThenElse.thenExpression);
        visit(ifThenElse.elseExpression.get());
    }

    @Override
    public void visit(Literal literal) {
    }

    @Override
    public void visit(Unary unary) {
        visit(unary.expr);
    }

    @Override
    public void visit(While whileLoop) {
        visit(whileLoop.condition);
        visit(whileLoop.body);
    }

    @Override
    public void visit(Where where) {
        symbolTable.pushScope();
        visit(where.defs);
        visit(where.expr);
        symbolTable.popScope();
    }

    @Override
    public void visit(Defs defs) {
        // v simbolno insertas definicije in parametre
        /*
         * nek name -> symbolTable.definitionFor
         * definitions.store(defFor, object)
         * 
         * fun, where -> new scope
         * new scope po temu ko pregledas tipe
         */
        for (Def def : defs.definitions) {
            try {
                symbolTable.insert(def);
            } catch (Exception e) {
                Report.error(def.position, "Definition already exists and cannot be duplicated!");
            }
        }

        for (Def def : defs.definitions) {
            if (def instanceof FunDef) {
                visit((FunDef) def);
            } else if (def instanceof TypeDef) {
                visit((TypeDef) def);
            } else if (def instanceof VarDef) {
                visit((VarDef) def);
            }
        }
    }

    @Override
    public void visit(FunDef funDef) {
        try {
            symbolTable.pushScope();
            for (Parameter param : funDef.parameters) {
                visit(param);
            }
            visit(funDef.type);
            visit(funDef.body);
            symbolTable.popScope();
        } catch (Exception e) {
            Report.error(funDef.position, e.getMessage());
        }
    }

    @Override
    public void visit(TypeDef typeDef) {
        visit(typeDef.type);
    }

    @Override
    public void visit(VarDef varDef) {
        visit(varDef.type);
    }

    @Override
    public void visit(Parameter parameter) {
        try {
            visit(parameter.type);
            symbolTable.insert(parameter); 
        } catch (Exception e) {
            Report.error(parameter.position, e.getMessage());
        }
    }

    @Override
    public void visit(Array array) {

    }

    @Override
    public void visit(Atom atom) {
        // prazen
    }

    @Override
    public void visit(TypeName name) {
        try {
            if (!(symbolTable.definitionFor(name.identifier).get() instanceof TypeDef)) {
                throw new Exception(name.identifier + " is not a TypeDef, but a " + name.getClass());
            }
            definitions.store(symbolTable.definitionFor(name.identifier).get(), name);
        } catch (Exception e) {
            Report.error(name.position, e.getMessage());
        }
    }

    private void visit(Type type) {
        if (type instanceof Array) {
            visit((Array) type);
        } else if (type instanceof Atom) {
            visit((Atom) type);
        } else if (type instanceof TypeName) {
            visit((TypeName) type);
        } else
            Report.error(type.position, "Wrong type passed into VarDef (" + type.getClass() + ")");
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
        }
    }
}
