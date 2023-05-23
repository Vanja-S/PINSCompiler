package common;

/**
 * @ Author: Vanja Stojanović
 * @ Description: Standard Library Name checkings and Type checkings
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compiler.seman.type.type.Type;

public abstract class StandardLibrary {
    public static HashMap<String, Function> functions = new HashMap<String, Function>();

    static {
        // Print functions
        ArrayList<Type> types = new ArrayList<Type>();

        types.add(new Type.Atom(Type.Atom.Kind.INT));
        functions.put("print_int", new Function("print_int", new Type.Atom(Type.Atom.Kind.VOID), 1, List.copyOf(types), Constants.printIntLabel));
        types.clear();
        
        types.add(new Type.Atom(Type.Atom.Kind.LOG));
        functions.put("print_log", new Function("print_log", new Type.Atom(Type.Atom.Kind.VOID), 1, List.copyOf(types), Constants.printLogLabel));
        types.clear();
        
        types.add(new Type.Atom(Type.Atom.Kind.STR));
        functions.put("print_str", new Function("print_str", new Type.Atom(Type.Atom.Kind.VOID), 1, List.copyOf(types), Constants.printStringLabel));
        types.clear();

        // Rand and seed functions
        types.add(new Type.Atom(Type.Atom.Kind.INT));
        types.add(new Type.Atom(Type.Atom.Kind.INT));
        functions.put("rand_int", new Function("rand_int", new Type.Atom(Type.Atom.Kind.INT), 2, List.copyOf(types), Constants.randIntLabel));
        types.clear();
        
        types.add(new Type.Atom(Type.Atom.Kind.INT));
        functions.put("seed", new Function("seed", new Type.Atom(Type.Atom.Kind.VOID), 1, List.copyOf(types), Constants.seedLabel));
        types.clear();
    }

}
