package com.logicovercode.file

import better.files.Dsl._
import better.files.File

import java.io.{File => JFile}
import java.nio.file.Path
import scala.util.{Failure, Success, Try}
import cats.implicits._

trait SystemFileFeatures{
  def name : String
  protected final def nameOfFile(jfile : JFile) : String = jfile.getName
}

trait DirectoryOnlyFeatures{

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

case class DirectoryHandle(private val directory : File) extends SystemFileFeatures with DirectoryOnlyFeatures {
  override def name: String = nameOfFile(directory.toJava)

  override def /(name: String): DirectoryHandle = /(directory, name)

  def jDirectory: JFile = directory.toJava
  def pathString: String = jDirectory.getAbsolutePath


  def resolve() : Either[Throwable, ExistingDirectory] = {
    directory.exists match {
      case true => Right(ExistingDirectory(directory))
      case false => Left( throw new RuntimeException(s"${directory.toJava.getAbsolutePath} doesn't exists") )
    }
  }

  def exists : Boolean = directory.exists

  def take() : ExistingDirectory = {
    val dir = mkdirs(directory)
    ExistingDirectory(dir)
  }

  def recreate() : Either[Throwable, ExistingDirectory] = {
    for{
      _ <- Try( rm(directory) ).toEither
      recreatedFile <- Try(take()).toEither
    } yield recreatedFile
  }


  override def %(name: String): FileHandle = %(directory, name)
}

trait FileOnlyFeatures{
  def nameWithoutExtension: String
  protected final def nameOfFileWithoutExtension(jfile : JFile): String = File(jfile.getAbsolutePath).nameWithoutExtension
}

case class FileHandle(private[file] val file : File) extends SystemFileFeatures with FileOnlyFeatures {
  override def name: String = nameOfFile(jFile)

  def pathString: String = jFile.getAbsolutePath

  def touch() : Either[Throwable, ExistingFile] = {
    val tried = Try {
      if (file.exists()) {
        ExistingFile(file)
      } else {
        mkdirs(file.parent)
        file.touch()
        ExistingFile(file)
      }
    }
    tried match {
      case Success(existingFile) => Right(existingFile)
      case Failure(ex) => Left(ex)
    }
  }

  def recreate() : Either[Throwable, ExistingFile] = {
    for{
      _ <- Try( rm(file) ).toEither
      recreatedFile <- touch()
    } yield recreatedFile
  }

  def resolveFileWithExtension() : Either[Throwable, ExistingFileWithExtension] = {
    (file.exists, file.extension(false)) match {
      case (true, Some(extension)) => ExistingFileWithExtension(file.toJava, extension)
      case _ => Left( throw new RuntimeException(s"${file.toJava.getAbsolutePath} doesn't exists") )
    }
  }

  def exists : Boolean = file.exists

  def jFile: JFile = file.toJava

  override def nameWithoutExtension: String = nameOfFileWithoutExtension(jFile)
}

trait ExistingFileFeatures extends SystemFileFeatures with FileOnlyFeatures {
  def append(text : String) : Unit
  protected final def append(jfile : JFile, text : String) : Unit = {
    File(jfile.getAbsolutePath).append(text)
  }
}

case class ExistingFileWithExtension private(private[file] val file : File, extension : String) extends ExistingFileFeatures {
  assert(file.exists, s"file $file doesn't exists")
  override def name : String = nameOfFile(file.toJava)
  def mayBeExtension : Option[String] = file.extension(false)

  def absolutePath : String = jFile.getAbsolutePath

  final def lines(): Seq[String] = {
    val lines: Traversable[String] = File(file.toJava.getAbsolutePath).lines
    lines.toSeq
  }

  final def content() : String = file.contentAsString

  final def move(fileHandle : FileHandle) : ExistingFile = {
    mv(file, fileHandle.file)
    ExistingFile( fileHandle.file )
  }

  def jFile: JFile = file.toJava

  final def parent: ExistingDirectory = ExistingDirectory(file.parent)

  override def nameWithoutExtension: String = nameOfFileWithoutExtension(file.toJava)

  override def append(text: String): Unit = append(file.toJava, text)

  def relativePath(existingFile: ExistingFile) : Path = file.relativize(existingFile.file)
  def relativePath(existingDirectory: ExistingDirectory) : Path = file.relativize(existingDirectory.directory)
}

object ExistingFileWithExtension{
  def apply(jFile : JFile, extension : String) : Either[Throwable, ExistingFileWithExtension] = {
    val tried = Try{
      val expectedFile = File(jFile.getAbsolutePath)
      expectedFile.exists match {
        case true if !expectedFile.isDirectory => ExistingFileWithExtension( expectedFile, extension )
        case true => throw new RuntimeException(s"$expectedFile is a directory. expected file")
        case false => throw new RuntimeException(s"file $expectedFile doesn't exists")
      }
    }
    tried.toEither
  }

  def apply(fileAbsolutePath : String, extension : String) : Either[Throwable, ExistingFileWithExtension] = {
    apply( new JFile(fileAbsolutePath), extension )
  }
}

case class ExistingFile(private[file] val file : File) extends ExistingFileFeatures {
  assert(file.exists, s"file $file doesn't exists")
  override def name : String = nameOfFile(file.toJava)
  def mayBeExtension : Option[String] = file.extension(false)

  final def lines(): Seq[String] = {
    val lines: Traversable[String] = File(file.toJava.getAbsolutePath).lines
    lines.toSeq
  }

  final def content() : String = file.contentAsString

  final def move(fileHandle : FileHandle) : ExistingFile = {
    mv(file, fileHandle.file)
    ExistingFile( fileHandle.file )
  }

  def jFile: JFile = file.toJava
  def absolutePath : String = jFile.getAbsolutePath

  final def parent: ExistingDirectory = ExistingDirectory(file.parent)

  override def nameWithoutExtension: String = nameOfFileWithoutExtension(file.toJava)

  override def append(text: String): Unit = append(file.toJava, text)

  def relativePath(existingFile: ExistingFile) : Path = file.relativize(existingFile.file)
  def relativePath(existingDirectory: ExistingDirectory) : Path = file.relativize(existingDirectory.directory)
}

object ExistingFile{
  def apply(jFile : JFile) : Either[Throwable, ExistingFile] = {
    val expectedFile = File(jFile.getAbsolutePath)
    expectedFile.exists match {
      case true if !expectedFile.isDirectory => Right(ExistingFile( expectedFile ))
      case true => Left( throw new RuntimeException(s"$expectedFile is a directory. expected file"))
      case false => Left( throw new RuntimeException(s"file $expectedFile doesn't exists") )
    }
  }

  def apply(fileAbsolutePath : String) : Either[Throwable, ExistingFile] = {
    apply( new JFile(fileAbsolutePath) )
  }
}

case class ExistingDirectory(private[file] val directory : File) extends SystemFileFeatures with DirectoryOnlyFeatures {
  override def name : String = nameOfFile(directory.toJava)

  override def /(name: String): DirectoryHandle = /(directory, name)

  override def %(name: String): FileHandle = %(directory, name)

  final def parent: ExistingDirectory = ExistingDirectory(directory.parent)

  def jDirectory: JFile = directory.toJava
  def absolutePath : String = jDirectory.getAbsolutePath

  def directoryHandle() : DirectoryHandle = DirectoryHandle(directory)

  private def listBetterFilesRecursively(predicate : File => Boolean, searchDepth : Int) : Seq[File] = {
    directory.list(predicate, searchDepth, File.VisitOptions.follow).toSeq
  }

  private def listAllExtensionExistingFiles(extensionsWithoutDot : Set[String], searchDepth : Int = Integer.MAX_VALUE): Seq[ExistingFile] = {

    def extensionRequired(file : File) : Boolean = {
      extensionsWithoutDot.exists{ requiredExtension =>
        val mayBeCurrentFileExtension = file.extension(false)
        mayBeCurrentFileExtension.map(_.equalsIgnoreCase(requiredExtension)).getOrElse(false)
      }
    }

    listBetterFilesRecursively( extensionRequired, searchDepth ).map( ExistingFile(_) )
  }


  def listAllFiles(searchDepth : Int = Integer.MAX_VALUE): Either[Throwable, Seq[ExistingFile]] = {
    val tried = Try{
      listBetterFilesRecursively(!_.isDirectory, searchDepth).map( bf => ExistingFile(bf) )
    }
    tried match {
      case Success(existingFiles) => Right(existingFiles)
      case Failure(ex) => Left(ex)
    }
  }

  def listAllFilesWithExtension(extensionWithoutDot : String, searchDepth : Int = Integer.MAX_VALUE): Either[Throwable, Seq[ExistingFileWithExtension]] = {
    val tried = Try{
      listAllExtensionExistingFiles( Set(extensionWithoutDot), searchDepth ).map( existingFile => ExistingFileWithExtension(existingFile.file.toJava, extensionWithoutDot) )
    }
    tried match {
      case Success(existingFilesWithExtension) => existingFilesWithExtension.sequence
      case Failure(ex) => Left(ex)
    }
  }


  def listAllDirectories(searchDepth : Int = Integer.MAX_VALUE): Either[Throwable, Seq[ExistingDirectory]] = {
    val tried = Try{
      listBetterFilesRecursively(f => f.isDirectory && !f.equals(directory), searchDepth).map( bf => ExistingDirectory(bf) )
    }
    tried match {
      case Success(existingDirectories) => Right(existingDirectories)
      case Failure(ex) => Left(ex)
    }
  }

  def relativePath(existingFile: ExistingFile) : Path = directory.relativize(existingFile.file)
  def relativePath(existingFileWithExtension: ExistingFileWithExtension) : Path = directory.relativize(existingFileWithExtension.file)
  def relativePath(existingDirectory: ExistingDirectory) : Path = directory.relativize(existingDirectory.directory)
}

object ExistingDirectory{
  def apply(jFile : JFile) : Either[Throwable, ExistingDirectory] = {
    val expectedDirectory = File(jFile.getAbsolutePath)
    expectedDirectory.exists && expectedDirectory.isDirectory match {
      case true => Right(ExistingDirectory( expectedDirectory ))
      case false => Left(throw new RuntimeException(s"directory $expectedDirectory doesn't exists"))
    }
  }

  def apply(directoryAbsolutePath : String) : Either[Throwable, ExistingDirectory] = {
    apply( new JFile(directoryAbsolutePath) )
  }
}

