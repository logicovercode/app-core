package com.logicovercode

package object file {
  type EMsgDirectory = Either[Throwable, ExistingDirectory]
  type EThrowDirectory = Either[Throwable, ExistingDirectory]
  type EThrowExistingFile = Either[Throwable, ExistingFile]
  type EThrowExistingFileWithExtension = Either[Throwable, ExistingFileWithExtension]
}
