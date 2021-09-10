# AcademicalPaperEstimator
Project for my linguistic pragmatics seminar.
A program which uses Corpora data (in this repository a sample of the COCA word frequency list) to rate a given text on how academical it is by its vocabulary.
## Use
You need Java 8 or higher installed on your computer. Then you can either build the project yourself or just use the precompiled jar.
### Precompiled Executable
Download the [precompiled.zip](https://github.com/Frobeniusnorm/AcademicTextEstimator/raw/main/precompiled.zip) file, unzip it and simply run the included jar file (depending on the os via double click or running `java -jar AcademicPaperEstimator-assembly-1.0.jar`). The jar file requires the two `.csv` files.
### Changing the Corpus Data
Currently the program is only able to parse the data from the COCA corpus, i.e. it needs the two files `wordForms.csv` (word form list) and `wordFrequency.csv` (lemma list) with those exact names. They are generated from Excel and the program expects as seperator char ';' and as decimal seperator ','. If you have different Corpus data with another format, let me know and i may adapt the parser.
## Building
You need the JDK 8 or higher, the Scala Compiler and sbt. Then just clone the project and execute `sbt run` to build and run it.
If you want to generate executables use the command `assembly` in sbt i.e. `sbt assembly`, the executables will be written to "target/scala-x.y.z/AcademicPaperEstimator-assembly-1.0,jar".
## Corpora Source
The used samples from the COCA corpus can be found at https://www.wordfrequency.info/. Huge thank you for providing that information.
