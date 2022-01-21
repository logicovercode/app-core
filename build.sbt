val githubRepo = githubHosting("logicovercode", "app-core", "techLeadAtLogicOverCode", "techlead@logicovercode.com")

val sBuild = SBuild("com.logicovercode", "app-core", "0.0.001")
  .sourceDirectories("core")
  .testSourceDirectories("core-spec")
  .dependencies( better_files() )
  .testDependencies( scalatest() )
  .scalaVersions(scala_2_13_MaxVersion, Seq(scala_2_13_MaxVersion, scala_2_12_MaxVersion))
  .javaCompatibility("1.8", "1.8")
  .publish(githubRepo.developer, MIT_License, githubRepo, Opts.resolver.sonatypeStaging)

val appCoreProject = (project in file("."))
  .settings(sBuild.settings)
