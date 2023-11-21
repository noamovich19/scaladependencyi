import scala.collection.mutable
import scala.reflect._
import scala.reflect.api.Universe
import scala.reflect.runtime.universe._

object Injector {
  val injector = new Injector()
  injector.create[ExampleModel1]
  injector.create[ExampleModel2]
}

class Injector() {
  private val instances = mutable.Map[Type, Any]()

  def create[A : TypeTag : ClassTag]: A = {
    val tpe = typeOf[A]
    instances.get(tpe) match {
      case Some(instance) => instance.asInstanceOf[A]
      case None =>
        val instance = createNewInstance[A]
        instances(tpe) = instance
        instance
    }
  }

  def getAllAnnotatedInstances[A: TypeTag : ClassTag, B : TypeTag : ClassTag]: Seq[B] = {
    val bClass = implicitly[ClassTag[B]].runtimeClass
    instances.collect {
      case (tpe, instance)
        if isClassAnnotatedWithInjectable(tpe, typeOf[A]) && bClass.isAssignableFrom(instance.getClass)=>
        instance.asInstanceOf[B]
    }.toSeq
  }

  private def createNewInstance[A : TypeTag : ClassTag]: A = {
    val tpe = typeOf[A]
    val mirror = runtimeMirror(getClass.getClassLoader)
    val classSymbol = tpe.typeSymbol.asClass
    val classMirror = mirror.reflectClass(classSymbol)
    val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR).asMethod
    val constructorMirror = classMirror.reflectConstructor(constructorSymbol)

    val constructorArgs = constructorSymbol.paramLists.flatten.map { param =>
      val paramType = param.info
      val paramSymbol = param.asTerm
      if (!paramSymbol.annotations.exists(_.tree.tpe =:= typeOf[Dependency])) {
        throw new IllegalArgumentException(s"Parameter ${paramSymbol.name} is not marked as @org.discal.com.Dependency")
      }
      if (!isClassAnnotatedWithInjectable(paramType, typeOf[Injectable])) {
        throw new IllegalArgumentException(s"The type of parameter ${param} is not a class annotated with @org.discal.com.Injectable")
      }
      val method = this.getClass.getMethod("create", classOf[TypeTag[_]], classOf[ClassTag[_]])
      val runtimeClass = mirror.runtimeClass(paramType.typeSymbol.asClass)
      val paramTypeTag = TypeTag(mirror, new api.TypeCreator {
        def apply[U <: Universe with Singleton](m: api.Mirror[U]): U#Type = paramType.asInstanceOf[U#Type]
      })
      val paramClassTag = ClassTag(runtimeClass)
      method.invoke(this, paramTypeTag, paramClassTag)
    }

    constructorMirror(constructorArgs: _*).asInstanceOf[A]
  }

  private def isClassAnnotatedWithInjectable(tpe: Type, annotationType: Type): Boolean = {
    val classSymbol = tpe.typeSymbol.asClass
    classSymbol.annotations.exists(_.tree.tpe =:= annotationType)
  }
}
