package com.lox.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.err.println("Usage: generate_ast <outpout_directory>") ;
            System.exit(64);
        } else {
            String outputDir = args[0];
            defineAst(outputDir, "Expr", Arrays.asList(
                    "Binary   : Expr left, Token operator, Expr right",
                    "Grouping : Expr expression",
                    "Literal  : Object value",
                    "Unary    : Token operator, Expr right"
            ));
        }
    }

    private static void defineAst(
            String outputDir,
            String baseName,
            List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        try (PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package com.lox;");
            writer.println();
            writer.println("import java.util.List;") ;
            writer.println();
            writer.println("abstract class " + baseName + " {");

            defineVisitor(writer, baseName, types);

            // The AST classes
            for(String type: types) {
                String className = type.split(":")[0].trim() ;
                String fields = type.split(":")[1].trim() ;
                defineType(writer, baseName, className, fields);
            }

            // The base accept() method
            writer.println() ;
            writer.println(indents(1) + "abstract <R> R accept(Visitor<R> visitor);");

            writer.println("}") ;
        }
    }

    private static String indents(int numIndents) {
        if (numIndents <= 0) return "";
        return "    ".repeat(numIndents);
    }

    private static void defineType(
            PrintWriter writer,
            String baseName,
            String className,
            String fieldList) {

        writer.println("\n" + indents(1) + "// " + className + " Expression Class");
        writer.println(indents(1) + "static class " + className + " extends " + baseName + " { ");

        String[] fields = fieldList.split(", ") ;

        // Fields
        writer.println(indents(2) + "// Fields");
        for(String field: fields) {
            writer.println(indents(2) + "final " + field + ";");
        }
        writer.println();

        // Constructor
        writer.println(indents(2) + "// Constructor");
        writer.println(indents(2) + className + "(" + fieldList + ") {") ;

        // Store parameters in fields.
//        String[] fields = fieldList.split(", ") ;
        for(String field: fields) {
            String name = field.split(" ")[1];
            writer.println(indents(3) + "this." + name + " = " + name + ";");
        }

        writer.println(indents(2) + "}") ;

        // Visitor pattern
        writer.println() ;
        writer.println(indents(2) + "@Override");
        writer.println(indents(2) + "<R> R accept(Visitor<R> visitor) {");
        writer.println(indents(3) + "return visitor.visit" + className + baseName + "(this);");
        writer.println(indents(2) +"}");

//        // Fields
//        writer.println();
//        for(String field: fields) {
//            writer.println(indents(2) + "final " + field + ";");
//        }

        writer.println(indents(1) + "}");

    }

    private static void defineVisitor(
            PrintWriter writer,
            String baseName,
            List<String> types) {

        writer.println(indents(1) + "interface Visitor<R> {");

        for(String type: types) {
            String typeName = type.split(":")[0].trim() ;
            writer.println(indents(2) + "R visit" + typeName + baseName + "(" +
                    typeName + " " + baseName.toLowerCase() + ");") ;
        }
        writer.println(indents(1) +"}");
    }

}
