package pt.ua.deti.codespell.syntax_tree.handlers.chapter1;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.Tree;
import pt.ua.deti.codespell.syntax_tree.handlers.AbstractLevelSyntaxTree;
import pt.ua.deti.codespell.syntax_tree.handlers.LevelSyntaxTreeHandler;
import pt.ua.deti.codespell.syntax_tree.scanners.ClassScanner;
import pt.ua.deti.codespell.syntax_tree.scanners.MethodScanner;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Level1SyntaxTree extends AbstractLevelSyntaxTree {

    public Level1SyntaxTree() {
        super(LevelSyntaxTreeHandler.getLevelClassFile(1,1), 3);
    }

    public boolean isStepValid(int step) {

        switch (step) {
            case 1:
                return testStep1();
            case 2:
                return testStep2();
            case 3:
                return testStep3();
            default:
                return false;
        }

    }

    public List<Object> getStepArgs(int step) {
        if (step == 3)
            return Collections.singletonList(getStep3Args());
        return new ArrayList<>();
    }

    private boolean testStep1() {
        ClassScanner[] classScanners = LevelSyntaxTreeHandler.scanFile(fileToAnalyze, new ClassScanner());
        return classScanners[0].getClassesCounter() >= 2 && classScanners[0].getClassesNames().contains("HelloWorldApp");
    }

    private boolean testStep2() {

        if (!testStep1()) return false;

        MethodScanner[] methodScanners = LevelSyntaxTreeHandler.scanFile(fileToAnalyze, new MethodScanner());

        boolean isCorrectCount = methodScanners[0].getMethodsCounter() >= 1;

        if (!isCorrectCount) {
            stepTips.add("No methods were found in your class.");
            return false;
        }

        boolean hasMainMethod = methodScanners[0].getMethodsNames()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList())
                .contains("main");

        if (!hasMainMethod) {
            stepTips.add("No main method was found in your class.");
            return false;
        }

        int mainMethodIdx = methodScanners[0].getMethodsNames()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList()).indexOf("main");

        boolean isMainMethodPublic = methodScanners[0].getMethodsModifiers().get(mainMethodIdx).contains(Modifier.PUBLIC);

        if (!isMainMethodPublic) {
            stepTips.add("The main method should be public.");
            return false;
        }

        boolean isMainMethodStatic = methodScanners[0].getMethodsModifiers().get(mainMethodIdx).contains(Modifier.STATIC);

        if (!isMainMethodStatic) {
            stepTips.add("The main method should be static.");
            return false;
        }

        boolean doesMainReturnVoid = methodScanners[0].getMethodsReturnTypes().get(mainMethodIdx).toString().equalsIgnoreCase("void");

        if (!doesMainReturnVoid) {
            stepTips.add(String.format("The main method should return void. It returns %s.", methodScanners[0].getMethodsReturnTypes().get(mainMethodIdx).toString()));
            return false;
        }

        return true;

    }

    private boolean testStep3() {

        if (!testStep1() || !testStep2()) return false;

        MethodScanner[] methodScanners = LevelSyntaxTreeHandler.scanFile(fileToAnalyze, new MethodScanner());

        int mainMethodIdx = methodScanners[0].getMethodsNames()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList()).indexOf("main");

        BlockTree mainMethodBody = methodScanners[0].getMethodsBody().get(mainMethodIdx);

        if (mainMethodBody.getStatements().isEmpty()) {
            stepTips.add("You should have statements inside the main function.");
            return false;
        }

        boolean hasExpressionStatement = mainMethodBody.getStatements()
                .stream()
                .map(Tree::getKind)
                .anyMatch(kind -> kind == Tree.Kind.EXPRESSION_STATEMENT);

        if (!hasExpressionStatement) {
            stepTips.add("Your main function must have at least one expression statement.");
            return false;
        }

        List<String> output = new ArrayList<>();
        File outputFile = new File(File.separator + "output.txt");

        try {
            output = Files.readAllLines(outputFile.toPath());
        } catch (IOException ignored) {
            System.out.println("Error reading output lines");
        }

        if (output.isEmpty()) {
            stepTips.add("Your main function must print something.");
            return false;
        }

        return true;

    }

    private List<String> getStep3Args() {

        List<String> output = new ArrayList<>();
        File outputFile = new File(File.separator + "output.txt");

        try {
            output = Files.readAllLines(outputFile.toPath());
        } catch (IOException ignored) {
            System.out.println("Error reading output lines");
        }

        return output.isEmpty() ? output : output.subList(0,1);

    }

}
