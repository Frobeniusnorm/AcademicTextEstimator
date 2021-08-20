import java.io.File
import scala.io.Source
import scala.collection.immutable.HashMap
object WordClasses extends Enumeration {
    type WordClass = Value
    val NOUN, VERB, ADJ, ADV, PP, PRO, CON, INTER, NOTHING = Value
}
/**
  * Consists of the lemmas and individual word forms which are connected to a lemma
  * @param lemmas       Mapping from Lemma -> (acadPM, WordClass)
  * @param wordForms    Mapping from Word form -> Lemma
  */
case class WordDatabase(
    lemmas:HashMap[String, (Double, WordClasses.WordClass)],
    wordForms:HashMap[String, String]){
    /**
     * Not every referenced Lemma in wordForms must be present in the lemmas map (because only a selection of word classes are selected).
     * This Method reduces the word database to only those word forms that reference an existing lemma
     * @return a new consistent word database
     */
    def reduceToMatching():WordDatabase = WordDatabase(lemmas, wordForms.filter(pred => lemmas.contains(pred._2)))

    def estimateAcademical(str:String):Double = {
        0.5
    }
}
object TextEstimator{
    /**
      * Creates the word database with the lemma and word forms table
      * from the lemma and words file (exported csv from excel with ';' as seperators)
      * @param lemmas file containing lemmas with word Frequencies and seperate categories
      * @param words  file containing word forms with references to lemmas
      * @return the word database
      */
    def createDatabase(lemmas:File, words:File):WordDatabase =
        WordDatabase(
            HashMap.from(Source.fromFile(lemmas).getLines()
                .dropWhile(!_.startsWith("rank;lemma;PoS;freq;perMil;%caps;%allC;range;disp;blog;web;TVM;spok;fic;mag;news;acad;blogPM;webPM;TVMPM;spokPM;ficPM;magPM;newsPM;acadPM"))
                .drop(1).map(line => {
                    val parts = line.split(";")
                    val lemma = parts(1)
                    val typec = parts(2).charAt(0)
                   
                    val acapm = (parts(24).replaceAll(",", ".")).toDouble
                    import WordClasses._
                    lemma -> ((acapm, (typec match {
                        case 'n' => NOUN
                        case 'v' => VERB
                        case 'j' => ADJ
                        case 'r' => ADV
                        case 'i' => PP
                        case 'p' | 'a' => PRO
                        case 'c' => CON
                        case 'u' => INTER
                        case _ => NOTHING 
                    })))
                }).filter(_._2._2 != WordClasses.NOTHING)),
            HashMap.from(Source.fromFile(words).getLines()
                .dropWhile(!_.startsWith("lemRank;lemma;PoS;lemFreq;wordFreq;word"))
                .drop(1).map(line => {
                    val parts = line.split(";")
                    if(parts.size <= 5) "" -> ""
                    else{
                        val lemma = parts(1)
                        val wordf = parts(5)
                        wordf -> lemma.toLowerCase()
                    }
                })).removed("")
        )
}