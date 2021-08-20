import java.io.File
object Main extends App {
  val database = TextEstimator.createDatabase(new File("wordFrequency.csv"), new File("wordForms.csv"))
  val minimal = database.reduceToMatching()
  println("database lemmas: " + database.lemmas.size + ", word forms: " + database.wordForms.size)
  println("reduced database lemmas: " + minimal.lemmas.size + ", word forms: " + minimal.wordForms.size)
  val str = """
It was said in 13.1.1 that generative transformational grammar constitutes
one of the most important research paradigms in the history of linguistics.
Indeed it would be difficult to deny the great intellectual appeal of the
model and the value of an enormous number of contributions to descriptive
linguistics carried out within the Chomskyan framework. Furthermore, it is
an indication of the outstanding importance of Chomsky’s work that it is
not only referred to by people working within the framework or frame-
works outlined by him, but that many who work within alternative frame-
works also refer to Chomsky’s work in order to demonstrate in what way
their approach differs from his.
One of the main points of criticism raised against Chomsky’s approach
concerns the question of evidence. Thus Sampson (2001: 141) speaks of
“Noam Chomsky’s fairly unempirical theory of ‘Transformational Gram-
mar’” and criticizes that"""
  println("Herbst: " + minimal.estimateAcademical(str))
}