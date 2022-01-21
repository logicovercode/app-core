package com.logicovercode.core;

import java.io.File;

public class CurrentFileContext {

    public static final File SRC_FILE = CodePaths$.MODULE$.sourceFile().toJava() ;
    public static final File PACKAGE_SRC_FILE = CodePaths$.MODULE$.packageFile().toJava() ;
    public static final String PACKAGE_SRC_PATH = CodePaths$.MODULE$.packagePathInContext() ;
}
