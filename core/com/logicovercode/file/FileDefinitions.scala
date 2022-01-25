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
  protected final def /(directory : File, name : String) : DirectoryHandle = {
    val dir = directory / name
    DirectoryHandle(dir)
  }
  def %(name : String) : FileHandle
  protected final def %(directory : File, name : String) : FileHandle = {
    val dir = directory / name
    FileHandle( dir )
  }
}

case class DirectoryHandle(private val directory : File) extends SystemFileFeatures with DirectoryFeatures {
  override def name: String = nameOfFile(directory.toJava)

  override def /(name: String): DirectoryHandle = /(directory, name)

  def take() : ExistingDirectory = {
    val dir = mkdirs(directory)
    ExistingDirectory(dir)
  }

  override def %(name: String): FileHandle = %(directory, name)
}

trait FileOnlyFeatures{
  def nameWithoutExtension: String
  protected final def nameOfFileWithoutExtension(jfile : JFile): String = File(jfile.getAbsolutePath).nameWithoutExtension
}

case class FileHandle(private[file] val file : File) extends SystemFileFeatures with FileOnlyFeatures {
  override def name: String = nameOfFile(jFile)
  def touch() : ExistingFile = {
    if(jFile.exists()){
      ExistingFile(file)
    }else{
      val b = jFile.createNewFile()
      if(b){
        ExistingFile(file)
      }else{
        throw new RuntimeException(s"not able to create $jFile")
      }
    }
  }

  def jFile: JFile = file.toJava

  override def nameWithoutExtension: String = nameOfFileWithoutExtension(jFile)
}

trait ExistingFileFeatures extends SystemFileFeatures with FileOnlyFeatures {
  def append(text : String) : Unit
  protected final def append(jfile : JFile, text : String) : Unit = {
    File(jfile.getAbsolutePath).append(text)
  }
}

case class ExistingFile(private[file] val file : File) extends ExistingFileFeatures {
  assert(file.exists, s"file $file doesn't exists")
  override def name : String = nameOfFile(file.toJava)

  final def lines(): Seq[String] = {
    val lines: Traversable[String] = File(file.toJava.getAbsolutePath).lines
    lines.toSeq
  }

  final def content() : String = {
    File(file.toJava.getAbsolutePath).contentAsString
  }

  final def move(fileHandle : FileHandle) : ExistingFile = {
    mv(file, fileHandle.file)
    ExistingFile( fileHandle.file )
  }

  def jFile: JFile = file.toJava

  final def parent: ExistingDirectory = ExistingDirectory(file.parent)

  override def nameWithoutExtension: String = nameOfFileWithoutExtension(file.toJava)

  override def append(text: String): Unit = append(file.toJava, text)
}



trait ExistingDirectoryFeatures extends SystemFileFeatures with DirectoryFeatures {
  def listFilesRecursively(): Seq[ExistingFile]
  protected final def listAllFilesRecursively(existingDirectory: File) : Seq[ExistingFile] = {
    existingDirectory.listRecursively.toSeq.filter(!_.isDirectory).map( bf => ExistingFile(bf) )
  }
  def listAllDirectoriesRecursively(existingDirectory: File) : Seq[ExistingDirectory] = {
    existingDirectory.listRecursively.toSeq.filter(_.isDirectory).map( bf => ExistingDirectory(bf) )
  }
  def listDirectoriesRecursively(): Seq[ExistingDirectory]
}


case class ExistingDirectory(private[file] val directory : File) extends ExistingDirectoryFeatures {
  assert(directory.exists && directory.isDirectory, s"directory $directory doesn't exists")

  override def name : String = nameOfFile(directory.toJava)

  override def /(name: String): DirectoryHandle = /(directory, name)

  override def %(name: String): FileHandle = %(directory, name)

  override def listFilesRecursively(): Seq[ExistingFile] = listAllFilesRecursively(directory)

  override def listDirectoriesRecursively(): Seq[ExistingDirectory] = listAllDirectoriesRecursively(directory)
}

object ExistingDirectory{
  def apply(jFile : JFile) : ExistingDirectory = {
    ExistingDirectory( File( jFile.getAbsolutePath ) )
  }
}

