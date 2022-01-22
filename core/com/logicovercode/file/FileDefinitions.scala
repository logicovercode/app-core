package com.logicovercode.file

import better.files.File

import java.io.{File => JFile}
import better.files.Dsl._

trait SystemFileFeatures{
  def name : String
  protected final def nameOfFile(jfile : JFile) : String = jfile.getName
}

trait DirectoryFeatures{

  def /(name : String) : DirectoryHandle
  protected final def /(jDirectory : JFile, name : String) : DirectoryHandle = {
    val dir = new JFile(jDirectory, name)
    DirectoryHandle(dir)
  }
  def %(name : String) : FileHandle
  protected final def %(jDirectory : JFile, name : String) : FileHandle = {
    val dir = new JFile(jDirectory, name)
    FileHandle(dir)
  }
}

case class DirectoryHandle(jDirectory : JFile) extends SystemFileFeatures with DirectoryFeatures {
  override def name: String = nameOfFile(jDirectory)

  override def /(name: String): DirectoryHandle = /(jDirectory, name)

  def take() : ExistingDirectory = {
    val dir = mkdirs(File(jDirectory.getAbsolutePath))
    ExistingDirectory(dir.toJava)
  }

  override def %(name: String): FileHandle = %(jDirectory, name)
}

trait FileOnlyFeatures{
  def nameWithoutExtension: String
  protected final def nameOfFileWithoutExtension(jfile : JFile): String = File(jfile.getAbsolutePath).nameWithoutExtension
}

case class FileHandle(jFile : JFile) extends SystemFileFeatures with FileOnlyFeatures {
  override def name: String = nameOfFile(jFile)
  def touch() : ExistingFile = {
    if(jFile.exists()){
      ExistingFile(jFile)
    }else{
      val b = jFile.createNewFile()
      if(b){
        ExistingFile(jFile)
      }else{
        throw new RuntimeException(s"not able to create $jFile")
      }
    }
  }

  override def nameWithoutExtension: String = nameOfFileWithoutExtension(jFile)
}

trait ExistingFileFeatures extends SystemFileFeatures with FileOnlyFeatures {
  def append(text : String) : Unit
  protected final def append(jfile : JFile, text : String) : Unit = {
    File(jfile.getAbsolutePath).append(text)
  }
}
case class ExistingFile(jfile : JFile) extends ExistingFileFeatures {
  assert(jfile.exists, s"file $jfile doesn't exists")
  override def name : String = nameOfFile(jfile)

  final def lines(): Seq[String] = {
    val lines: Traversable[String] = File(jfile.getAbsolutePath).lines
    lines.toSeq
  }

  final def content() : String = {
    File(jfile.getAbsolutePath).contentAsString
  }

  final def parent: ExistingDirectory = ExistingDirectory(jfile.getParentFile)

  override def nameWithoutExtension: String = nameOfFileWithoutExtension(jfile)

  override def append(text: String): Unit = append(jfile, text)
}



trait ExistingDirectoryFeatures extends SystemFileFeatures with DirectoryFeatures {
  def listFilesRecursively(): Seq[ExistingFile]
  protected final def listAllFilesRecursively(existingDirectory: JFile) : Seq[ExistingFile] = {
    File(existingDirectory.getAbsolutePath).listRecursively.toSeq.filter(!_.isDirectory).map( bf => ExistingFile(bf.toJava) )
  }
  def listAllDirectoriesRecursively(existingDirectory: JFile) : Seq[ExistingDirectory] = {
    File(existingDirectory.getAbsolutePath).listRecursively.toSeq.filter(_.isDirectory).map( bf => ExistingDirectory(bf.toJava) )
  }
  def listDirectoriesRecursively(): Seq[ExistingDirectory]
}


case class ExistingDirectory(jDirectory : JFile) extends ExistingDirectoryFeatures {
  assert(jDirectory.exists && jDirectory.isDirectory, s"directory $jDirectory doesn't exists")

  override def name : String = nameOfFile(jDirectory)

  override def /(name: String): DirectoryHandle = /(jDirectory, name)

  override def %(name: String): FileHandle = %(jDirectory, name)

  override def listFilesRecursively(): Seq[ExistingFile] = listAllFilesRecursively(jDirectory)

  override def listDirectoriesRecursively(): Seq[ExistingDirectory] = listAllDirectoriesRecursively(jDirectory)
}

