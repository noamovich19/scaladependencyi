object Main extends App {
  val injector = Injector.injector
  val models = injector.getAllAnnotatedInstances[Model, Runnable]
  models.foreach(m => m.run())
}

@Model(name = "ex1")
class ExampleModel1(@Dependency val dependency_bla2: Dependency2) extends Runnable {
  override def run(): Unit = println(dependency_bla2.value)
}

@Model(name = "ex2")
class ExampleModel2(
                     @Dependency val dependency_bla2: Dependency2,
                     @Dependency val dependency_bla1: Dependency1
                   ) extends Runnable {
  override def run(): Unit = {
    println(dependency_bla2.value + dependency_bla1.value)
  }
}

