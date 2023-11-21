abstract class LazyDependency[A] {
  lazy val value: A = calculateValue
  protected def calculateValue: A
}
