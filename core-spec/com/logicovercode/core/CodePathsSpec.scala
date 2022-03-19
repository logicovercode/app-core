package com.logicovercode.core

import better.files.Dsl.cwd
import org.scalatest.flatspec.AsyncFlatSpecLike
import org.scalatest.matchers.should.Matchers

class CodePathsSpec extends AsyncFlatSpecLike with Matchers{

  "PACKAGE_SRC_FILE" should "return path till package" in{

    val packageDirectory = SourceDirectory.PACKAGE_DIRECTORY
    println(packageDirectory)
    packageDirectory should be( (cwd / "core-spec" / "com" / "logicovercode" / "core").toJava )
  }

  "SRC_FILE" should "return path till package" in{

    val srcDirectory = SourceDirectory.CURRENT_SRC_DIRECTORY
    println(srcDirectory)
    srcDirectory should be( (cwd / "core-spec").toJava )
  }

  "PACKAGE_SRC_PATH" should "return package path in context" in {
    val packagePath = SourceDirectory.PACKAGE_SRC_PATH
    println(packagePath)
    packagePath should be("com/logicovercode/core")
  }
}
