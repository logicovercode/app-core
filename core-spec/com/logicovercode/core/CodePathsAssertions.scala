package com.logicovercode.core

import better.files.Dsl.cwd
import com.logicovercode.core.CurrentFileContext

object CodePathsAssertions {

  def main(args : Array[String]) : Unit = {

    val packageDirectory = CurrentFileContext.PACKAGE_SRC_FILE
    println(packageDirectory)
    assert(packageDirectory.equals( (cwd / "core-spec" / "com" / "logicovercode" / "core").toJava ) )

    val srcDirectory = CurrentFileContext.SRC_FILE
    println(srcDirectory)
    assert(srcDirectory.equals( (cwd / "core-spec" ).toJava ) )


    val packagePath = CurrentFileContext.PACKAGE_SRC_PATH
    println(packagePath)
    assert(packagePath.equals("com/logicovercode/core"))
  }
}
