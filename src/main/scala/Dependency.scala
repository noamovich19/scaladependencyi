@Injectable
class Dependency2 extends LazyDependency[Int] {
  override protected def calculateValue: Int = {
    println("calculating f2")
    14 + 1
  }
}


@Injectable
class Dependency1(
                   @Dependency d3: Dependency3,
                   @Dependency d4: Dependency4,
                 ) extends LazyDependency[Int] {
  override protected def calculateValue: Int = {
    println("calculating f1")
    println(d3.value)
    println(d4.not_lazy)
    144 + 1
  }
}


@Injectable
class Dependency3(@Dependency d4: Dependency4) extends LazyDependency[String] {
  override protected def calculateValue: String = {
    println("calculating f3")
    println(d4.not_lazy)
    "hello"
  }
}

@Injectable
class Dependency4 {
  val not_lazy = {
    "not lazy!"
  }
}
