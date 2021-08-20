import java.io.File
object Main extends App {
  val database = TextEstimator.createDatabase(new File("wordFrequency.csv"), new File("wordForms.csv"))
  val minimal = database.reduceToMatching()
  println("database lemmas: " + database.lemmas.size + ", word forms: " + database.wordForms.size)
  println("reduced database lemmas: " + minimal.lemmas.size + ", word forms: " + minimal.wordForms.size)

  new Gui(minimal)
}