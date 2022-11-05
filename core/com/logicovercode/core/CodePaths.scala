package com.logicovercode.core

import better.files.Dsl.cwd
import better.files.File

object CodePaths extends CheckOs {

  case class StackInfo(index : Int, className : String, fileName : String){
    val packagePrefix = {
      val arr = className.split("\\.")
      arr.take(2).mkString(".")
    }
  }

  private def effectiveStackTraceElement(): StackTraceElement = {
    val stacks = Thread.currentThread.getStackTrace.reverse

    val excludedPackagePrefixes = Seq("org.scalatest", "org.jetbrains", "scala.collection", "java.lang", "jdk.internal")

    val stackInfoSeq = stacks.zipWithIndex.map{case (s, i) => StackInfo(i, s.getClassName, s.getFileName)}
    val mayBeScalaTestRunner = stackInfoSeq.find(_.fileName.equals("ScalaTestRunner.java"))

    if (mayBeScalaTestRunner.isDefined) {

      val mayBeStackInfo = stackInfoSeq.find{ stackInfo =>
        !excludedPackagePrefixes.contains(stackInfo.packagePrefix)
      }
      mayBeStackInfo match {
        case Some(stackInfo) => stacks(stackInfo.index)
        case None => throw new RuntimeException("spec class not found")
      }
    } else {
      stacks(0)
    }
  }

  def packageDirectory(): File = {
    val effectiveStack = effectiveStackTraceElement()
    val fileName = effectiveStack.getFileName
    val map = allScalaAndJavaFiles(cwd).map(f => (f.name, f)).toMap
    map(fileName).parent
  }

  def classPackagePath(klass : Class[_]) : String = {
    val kpPath = klass.getPackage.getName.replace(".", OS_SLASH)
    OS_SLASH + kpPath
  }

  def packagePathInContext(): String = {
    val effectiveStack = effectiveStackTraceElement()
    val className = effectiveStack.getClassName
    val i = className.lastIndexOf(".")
    className.substring(0, i).replace(".", OS_SLASH)
  }

  def currentSourceDirectory(): File = {
    val absolutePath = packageDirectory().toJava.getAbsolutePath
    val i = absolutePath.indexOf(packagePathInContext())
    if(i == -1){
      File(absolutePath)
    }else{
      File(absolutePath.substring(0, i))
    }

  }

  private def allScalaAndJavaFiles(directory: File): Seq[File] = {
    val allFiles = directory.listRecursively.toSeq
    val javaFiles = allFiles.filter(f => f.extension.equals(Some(".java")))
    val scalaFiles = allFiles.filter(f => f.extension.equals(Some(".scala")))
    javaFiles ++ scalaFiles
  }
}
