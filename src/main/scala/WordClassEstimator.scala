import WordClasses._
import scala.collection.immutable.HashMap
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
object WordClassEstimator{
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    def estimateWordClass(data:WordDatabase)(cl:WordClass):Double = {
        val wv = data.lemmas.filter(word => word._2._2 == cl)
        wv.map(word => word._2._1).foldLeft(0.0)(_ + _ / wv.size.toDouble)
    }

    def estimateWordClasses(data:WordDatabase):HashMap[WordClass, Double] = {
        val est : WordClass => Double = estimateWordClass(data)
        val n = Future{est(NOUN)}
        val v = Future{est(VERB)}
        val aj = Future{est(ADJ)}
        val av = Future{est(ADV)}
        val i = Future{est(INTER)}
        val pp = Future{est(PP)}
        val pr = Future{est(PRO)}
        val c = Future{est(CON)}
        HashMap(
            NOUN -> Await.result(n, Duration.Inf),
            VERB -> Await.result(v, Duration.Inf),
            ADJ -> Await.result(aj, Duration.Inf),
            ADV -> Await.result(av, Duration.Inf),
            INTER -> Await.result(i, Duration.Inf),
            PP -> Await.result(pp, Duration.Inf),
            PRO -> Await.result(pr, Duration.Inf),
            CON -> Await.result(c, Duration.Inf),
        )
    }
}