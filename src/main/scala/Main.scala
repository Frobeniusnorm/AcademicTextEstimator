import java.io.File
object Main extends App {
  val minimal = TextEstimator.createDatabase(
      new File("wordFrequency.csv"), new File("wordForms.csv")
    ).reduceToMatching()
  new Gui(minimal, WordClassEstimator.estimateWordClasses(minimal))
}