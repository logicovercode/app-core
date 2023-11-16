import com.codogenic.mbuild._
import com.logicovercode.cadts._
import mill.scalalib.PublishModule
import mill.scalalib.publish.{Developer, License, PomSettings, VersionControl}
import coursier.maven.MavenRepository


object `app-core` extends CodogenicMavenScalaModule with RootModule with PublishModule{

  override def internalMavenRepos() : Seq[MavenRepository] = Seq()

  def sourceStrings(): Seq[String] = Seq("core")

  override def scalaDependencies(): Seq[SDependency] = Seq(
    better_files(), cats_core()
  )

  override def codogenicArtifactOrg: String = "com.logicovercode"

  override def codogenicArtifactName: String = "app-core"

  override def codogenicArtifactVersion: String = "0.0.003"
}