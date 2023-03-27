/**
 * @ Author: turk
 * @ Description: Preverjanje in razreševanje imen.
 */

package compiler.seman.name;

import static common.RequireNonNull.requireNonNull;

import common.Report;
import compiler.common.Visitor;
import compiler.lexer.Position;
import compiler.parser.ast.def.*;
import compiler.parser.ast.def.FunDef.Parameter;
import compiler.parser.ast.expr.*;
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
    }

    @Override
    public void visit(Binary binary) {

    }

    @Override
    public void visit(Block block) {
        symbolTable.pushScope();
    }

    @Override
    public void visit(For forLoop) {
        symbolTable.pushScope();
    }

    @Override
    public void visit(Name name) {
        // store
    }

    @Override
    public void visit(IfThenElse ifThenElse) {
    }

    @Override
    public void visit(Literal literal) {
        // prazen
    }

    @Override
    public void visit(Unary unary) {
    }

    @Override
    public void visit(While whileLoop) {
    }

    @Override
    public void visit(Where where) {
        symbolTable.pushScope();
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
            if(definitions.store(funDef, funDef)) {
                symbolTable.insert(funDef);
            }
        } catch(Exception e) {
            Report.error(funDef.position, e.getMessage());
        }
    }

    @Override
    public void visit(TypeDef typeDef) {
    }

    @Override
    public void visit(VarDef varDef) {
    }

    @Override
    public void visit(Parameter parameter) {
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
        // insert
    }

    private void visitType(Type type) {
        // if stavki za instance od typa ast.type
    }

    private void visitExpr(Expr expr) {
        // if stavki za innstance od ast.expr
    }
}
