/**
 * @ Author: turk
 * @ Description: Navidezni stroj (intepreter).
 */

package compiler.interpret;

import static common.RequireNonNull.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Random;

import javax.swing.plaf.metal.MetalRadioButtonUI;

import common.Constants;
import compiler.frm.Frame;
import compiler.gen.Memory;
import compiler.ir.chunk.Chunk.CodeChunk;
import compiler.ir.code.IRNode;
import compiler.ir.code.expr.*;
import compiler.ir.code.stmt.*;
import compiler.ir.IRPrettyPrint;

public class Interpreter {
    /**
     * Pomnilnik navideznega stroja.
     */
    private Memory memory;
    
    /**
     * Izhodni tok, kamor izpisujemo rezultate izvajanja programa.
     * 
     * V primeru, da rezultatov ne želimo izpisovati, nastavimo na `Optional.empty()`.
     */
    private Optional<PrintStream> outputStream;

    /**
     * Generator naključnih števil.
     */
    private Random random;

    /**
     * Skladovni kazalec (kaže na dno sklada).
     */
    private int stackPointer;

    /**
     * Klicni kazalec (kaže na vrh aktivnega klicnega zapisa).
     */
    private int framePointer;

    public Interpreter(Memory memory, Optional<PrintStream> outputStream) {
        requireNonNull(memory, outputStream);
        this.memory = memory;
        this.outputStream = outputStream;
        this.stackPointer = memory.size - Constants.WordSize;
        this.framePointer = memory.size - Constants.WordSize;
    }

    // --------- izvajanje navideznega stroja ----------

    public void interpret(CodeChunk chunk) {
        memory.stM(framePointer + Constants.WordSize, 0); // argument v funkcijo main
        memory.stM(framePointer - chunk.frame.oldFPOffset(), framePointer); // oldFP
        internalInterpret(chunk);
    }

    private void internalInterpret(CodeChunk chunk) {
        // @TODO: Nastavi FP in SP na nove vrednosti!
        stackPointer = framePointer;
        framePointer += chunk.frame.size();

        Object result = null;
        if (chunk.code instanceof SeqStmt seq) {
            for (int pc = 0; pc < seq.statements.size(); pc++) {
                var stmt = seq.statements.get(pc);
                result = execute(stmt);
                if (result instanceof Frame.Label label) {
                    for (int q = 0; q < seq.statements.size(); q++) {
                        if (seq.statements.get(q) instanceof LabelStmt labelStmt && labelStmt.label.equals(label)) {
                            pc = q;
                            break;
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("Linearize code!");
        }

        framePointer = stackPointer;
        stackPointer -= chunk.frame.size();
    }

    private Object execute(IRStmt stmt) {
        if (stmt instanceof CJumpStmt cjump) {
            return execute(cjump);
        } else if (stmt instanceof ExpStmt exp) {
            return execute(exp);
        } else if (stmt instanceof JumpStmt jump) {
            return execute(jump);
        } else if (stmt instanceof LabelStmt label) {
            return null;
        } else if (stmt instanceof MoveStmt move) {
            return execute(move);
        } else {
            throw new RuntimeException("Cannot execute this statement!");
        }
    }

    private Object execute(CJumpStmt cjump) {
        var cond = toBool((boolean)execute(cjump.condition));
        if(cond)
            return memory.ldM(cjump.thenLabel);
        else
            return memory.ldM(cjump.elseLabel);
    }

    private Object execute(ExpStmt exp) {
        return execute(exp.expr);
    }

    private Object execute(JumpStmt jump) {
        return memory.ldM(jump.label);
    }

    private Object execute(MoveStmt move) {
        var dest = move.dst;
        var src = move.src;
        src = dest;
        return src;
    }

    private Object execute(IRExpr expr) {
        if (expr instanceof BinopExpr binopExpr) {
            return execute(binopExpr);
        } else if (expr instanceof CallExpr callExpr) {
            return execute(callExpr);
        } else if (expr instanceof ConstantExpr constantExpr) {
            return execute(constantExpr);
        } else if (expr instanceof EseqExpr eseqExpr) {
            throw new RuntimeException("Cannot execute ESEQ; linearize code!");
        } else if (expr instanceof MemExpr memExpr) {
            return execute(memExpr);
        } else if (expr instanceof NameExpr nameExpr) {
            return execute(nameExpr);
        } else if (expr instanceof TempExpr tempExpr) {
            return execute(tempExpr);
        } else {
            throw new IllegalArgumentException("Unknown expr type");
        }
    }

    private Object execute(BinopExpr binop) {
        var temp1 = execute(binop.lhs);
        var temp2 = execute(binop.rhs);

        switch(binop.op) {
            case ADD:
                return (int)temp1 + (int)temp2;
            case SUB:
                return (int)temp1 - (int)temp2;
            case MUL:
                return (int)temp1 * (int)temp2;
            case DIV:
                return (int)temp1 / (int)temp2;
            case MOD:
                return (int)temp1 % (int)temp2;
            
            case AND:
                return (boolean)temp1 && (boolean)temp2;
            case OR:
                return (boolean)temp1 || (boolean)temp2;

            case EQ:
                return temp1 == temp2;
            case NEQ:
                return temp1 != temp2;
            case GEQ:
                return (int)temp1 >= (int)temp2;
            case LEQ:
                return (int)temp1 <= (int)temp2;
            case GT:
                return (int)temp1 > (int)temp2;
            default:
                return (int)temp1 < (int)temp2;
        }
    }

    private Object execute(CallExpr call) {
        if (call.label.name.equals(Constants.printIntLabel)) {
            if (call.args.size() != 2) { throw new RuntimeException("Invalid argument count!"); }
            var arg = execute(call.args.get(1));
            outputStream.ifPresent(stream -> stream.println(arg));
            return 0;
        } else if (call.label.name.equals(Constants.printStringLabel)) {
            if (call.args.size() != 2) { throw new RuntimeException("Invalid argument count!"); }
            var address = execute(call.args.get(1));
            var res = memory.ldM(toInt(address));
            outputStream.ifPresent(stream -> stream.println("\""+res+"\""));
            return 0;
        } else if (call.label.name.equals(Constants.printLogLabel)) {
            if (call.args.size() != 2) { throw new RuntimeException("Invalid argument count!"); }
            var arg = execute(call.args.get(1));
            outputStream.ifPresent(stream -> stream.println(toBool(arg)));
            return 0;
        } else if (call.label.name.equals(Constants.randIntLabel)) {
            if (call.args.size() != 3) { throw new RuntimeException("Invalid argument count!"); }
            var min = toInt(execute(call.args.get(1)));
            var max = toInt(execute(call.args.get(2)));
            return random.nextInt(min, max);
        } else if (call.label.name.equals(Constants.seedLabel)) {
            if (call.args.size() != 2) { throw new RuntimeException("Invalid argument count!"); }
            var seed = toInt(execute(call.args.get(1)));
            random = new Random(seed);
            return 0;
        } else if (memory.ldM(call.label) instanceof CodeChunk chunk) {
            execute(call.args.get(1));
            return 0;
        } else {
            throw new RuntimeException("Only functions can be called!");
        }
    }

    private Object execute(ConstantExpr constant) {
        return constant;
    }

    private Object execute(MemExpr mem) {
        return execute(mem.expr);
    }

    private Object execute(NameExpr name) {
        return memory.ldM(name.label);
    }

    private Object execute(TempExpr temp) {
        return memory.ldT(temp.temp);
    }

    // ----------- pomožne funkcije -----------

    private int toInt(Object obj) {
        if (obj instanceof Integer integer) {
            return integer;
        }
        throw new IllegalArgumentException("Could not convert obj to integer!");
    }

    private boolean toBool(Object obj) {
        return toInt(obj) == 0 ? false : true;
    }

    private int toInt(boolean bool) {
        return bool ? 1 : 0;
    }

    private String prettyDescription(IRNode ir, int indent) {
        var os = new ByteArrayOutputStream();
        var ps = new PrintStream(os);
        new IRPrettyPrint(ps, indent).print(ir);
        return os.toString(Charset.defaultCharset());
    }

    private String prettyDescription(IRNode ir) {
        return prettyDescription(ir, 2);
    }

    private void prettyPrint(IRNode ir, int indent) {
        System.out.println(prettyDescription(ir, indent));
    }

    private void prettyPrint(IRNode ir) {
        System.out.println(prettyDescription(ir));
    }
}
