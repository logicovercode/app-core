package com.logicovercode.core

import better.files.Dsl.cwd
import com.logicovercode.core.CurrentFileContext
import org.scalatest.flatspec.AsyncFlatSpecLike
import org.scalatest.matchers.should.Matchers

class CodePathsSpec extends AsyncFlatSpecLike with Matchers{

  "PACKAGE_SRC_FILE" should "return path till package" in{

    val packageDirectory = CurrentFileContext.PACKAGE_SRC_FILE
    println(packageDirectory)
    packageDirectory should be( (cwd / "core-spec" / "com" / "logicovercode" / "core").toJava )
  }

  "SRC_FILE" should "return path till package" in{

    val srcDirectory = CurrentFileContext.SRC_FILE
    println(srcDirectory)
    srcDirectory should be( (cwd / "core-spec").toJava )
  }

  "PACKAGE_SRC_PATH" should "return package path in context" in {
    val packagePath = CurrentFileContext.PACKAGE_SRC_PATH
    println(packagePath)
    packagePath should be("com/logicovercode/core")
  }
}
