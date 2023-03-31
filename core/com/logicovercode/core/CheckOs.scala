package com.logicovercode.core

trait CheckOs {

  private val OS_PROPERTY = "os.name"
  private def mayBeOsProperty(): Option[String] = sys.props.get(OS_PROPERTY)

  val OS_SLASH = if(isWindowsMachine()) "\\" else "/"

  def isWindowsMachine(): Boolean = verifyMachine(mayBeOsProperty(), "win")
  def isMacMachine(): Boolean = verifyMachine(mayBeOsProperty(), "mac")
  def isLinux(): Boolean = !(isWindowsMachine() || isMacMachine())

  private def verifyMachine(os: Option[String], machineCode : String): Boolean = (for {
    osName <- os
    os_name = osName.toLowerCase
  } yield os_name.contains(machineCode)).getOrElse(false)
}

sealed trait OperatingSystem

object OperatingSystem extends CheckOs {

  def apply(): OperatingSystem = {
    if(isWindowsMachine()){
      Windows
    }else if(isMacMachine()){
      Mac
    }else{
      Linux
    }
  }
}

case object Windows extends OperatingSystem
case object Linux extends OperatingSystem
case object Mac extends OperatingSystem


