package com.logicovercode.file

import better.files.File

import java.io.{File => JFile}
import better.files.Dsl._

trait JFileExtensions {

  implicit class JavaIoFileExtensions(jfile: JFile) {




    //def touch(): Boolean = jfile.createNewFile()
  }
}
