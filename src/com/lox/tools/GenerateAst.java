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

            // The AST classes
            for(String type: types) {
                String className = type.split(":")[0].trim() ;
                String fields = type.split(":")[1].trim() ;
                defineType(writer, baseName, className, fields);
            }

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
        writer.println("\n" + indents(1) + "static class " + className + " extends " + baseName + " { ");

        String[] fields = fieldList.split(", ") ;

        // Fields
        writer.println();
        for(String field: fields) {
            writer.println(indents(2) + "final " + field + ";");
        }
        writer.println();

        // Constructor
        writer.println(indents(2) + className + "(" + fieldList + ") {") ;

        // Store parameters in fields.

        for(String field: fields) {
            String name = field.split(" ")[1];
            writer.println(indents(3) + "this." + name + " = " + name + ";");
        }

        writer.println(indents(2) + "}") ;

        writer.println(indents(1) +"}");
    }


}
