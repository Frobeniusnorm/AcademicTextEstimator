import java.io.File
import scala.io.Source
import scala.collection.immutable.HashMap
object WordClasses extends Enumeration {
    type WordClass = Value
    val NOUN, VERB, ADJ, ADV, PP, PRO, CON, INTER, NOTHING = Value
}
/**
  * Consists of the lemmas and individual word forms which are connected to a lemma.
  * The distribution of the lemma is given in occurrence per million words in academic texts divided by overall occurrence per million words.
  * @param lemmas       Mapping from Lemma -> (acadPM/per million, WordClass)
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
    def lookUpWord(str:String):Option[(Double, WordClasses.WordClass)] = {
        val s = str.toLowerCase()
        if(!lemmas.contains(s) && !wordForms.contains(s)) None
        else{
            val lemma = if(lemmas.contains(s)) s else wordForms(s)
            Some(lemmas(lemma))
        }
    }

    def estimateAcademical(str:String):(Double, List[(String, Double)]) = {
        val foo = str.split("[,.\"`;:./&\n –-]").foldLeft((0.0, 0, List.empty[(String, Double)]))((old, e) => 
            if(e.contains("'") && e.split("'").size == 2){
                val parts = e.split("'")
                val w1 = lookUpWord(parts(0))
                val w2 = lookUpWord("'" + parts(1))
                var fl = old._3
                if(!w1.isEmpty) fl = (parts(0), w1.get._1) :: fl
                if(!w2.isEmpty) fl = ("'" + parts(1), w2.get._1) :: fl
                (old._1 + (if(w1.isEmpty) 0.0 else w1.get._1) + (if(w2.isEmpty) 0.0 else w2.get._1),
                    old._2 + (if(w1.isEmpty) 0 else 1) + (if(w2.isEmpty) 0 else 1),
                    fl)
            }else{
                val w = lookUpWord(e)
                var fl = old._3
                if(!w.isEmpty) fl = (e, w.get._1) :: fl
                (old._1 + (if(w.isEmpty) 0.0 else w.get._1),
                    old._2 + (if(w.isEmpty) 0 else 1),
                    fl)
            }
        )
        (foo._1/foo._2, foo._3)
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
                    val permi = (parts(4).replaceAll(",", ".")).toDouble
                    val acapm = (parts(24).replaceAll(",", ".")).toDouble
                    import WordClasses._
                    lemma -> ((acapm/permi, (typec match {
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
