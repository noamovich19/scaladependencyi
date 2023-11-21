
import java.lang.annotation.{Retention, RetentionPolicy}
import scala.annotation.StaticAnnotation

@Retention(RetentionPolicy.RUNTIME)
class Injectable extends StaticAnnotation

@Retention(RetentionPolicy.RUNTIME)
class Dependency extends StaticAnnotation

@Retention(RetentionPolicy.RUNTIME)
class Model(name: String) extends StaticAnnotation
