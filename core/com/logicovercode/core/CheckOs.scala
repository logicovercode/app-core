package com.logicovercode.core

trait CheckOs {

  val OS_PROPERTY = "os.name"

  val OS_SLASH = if(isWindowsMachine( sys.props.get(OS_PROPERTY) )) "\\" else "/"

  def isWindowsMachine(os: Option[String]): Boolean = (for {
    osName <- os
    os_name = osName.toLowerCase
  } yield os_name.contains("win")).getOrElse(false)
}


