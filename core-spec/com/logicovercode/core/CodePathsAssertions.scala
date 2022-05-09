package com.logicovercode.core

import better.files.Dsl.cwd

object CodePathsAssertions {

  def main(args : Array[String]) : Unit = {

    val packageDirectory = SourceDirectory.PACKAGE_DIRECTORY
    println(packageDirectory)
    assert(packageDirectory.equals( (cwd / "core-spec" / "com" / "logicovercode" / "core").toJava ) )

    val srcDirectory = SourceDirectory.CURRENT_SRC_DIRECTORY
    println(srcDirectory)
    assert(srcDirectory.equals( (cwd / "core-spec" ).toJava ) )


    val packagePath = SourceDirectory.PACKAGE_SRC_PATH
    println(packagePath)
    assert(packagePath.equals("com/logicovercode/core"))
  }
}
