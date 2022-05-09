package com.logicovercode.core;

import java.io.File;

public class SourceDirectory {

    public static final File CURRENT_SRC_DIRECTORY = CodePaths$.MODULE$.currentSourceDirectory().toJava() ;
    public static final File PACKAGE_DIRECTORY = CodePaths$.MODULE$.packageDirectory().toJava() ;
    public static final String PACKAGE_SRC_PATH = CodePaths$.MODULE$.packagePathInContext() ;
}
