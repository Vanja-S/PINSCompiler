package common;

import java.util.List;

import compiler.seman.type.type.Type;

public class Function {
    public String name;
    public Type returnType;
    public int numOfArgs;
    public List<Type> args;
    public String label;

    public Function(String name, Type type, int numArgs, List<Type> args, String label) {
        this.name = name;
        this.returnType = type;
        this.numOfArgs = numArgs;
        this.args = args;
        this.label = label;
    }
}