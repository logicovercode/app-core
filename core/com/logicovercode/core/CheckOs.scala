package com.logicovercode.core

trait CheckOs {

  val OS_PROPERTY = "os.name"

  def isWindowsMachine(os: Option[String]): Boolean = (for {
    osName <- os
    os_name = osName.toLowerCase
  } yield os_name.contains("win")).getOrElse(false)
}
