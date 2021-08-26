import javax.swing.JFrame
import java.awt.Toolkit
import java.awt.event.WindowListener
import java.awt.event.WindowEvent
import java.awt.event.WindowAdapter
import javax.swing.WindowConstants
import javax.swing.JTextArea
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.FlowLayout
import javax.swing.JPanel
import javax.swing.JLabel
import java.awt.Color
import java.awt.event.WindowStateListener
import java.awt.event.ComponentListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.UIManager
import javax.swing.BorderFactory
import javax.swing.border.Border
import java.awt.Component
import java.awt.Insets
import java.awt.Graphics
import java.awt.Cursor
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import javax.swing.SwingConstants
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JCheckBox
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.DefaultListModel
import scala.collection.immutable.HashSet
import javax.swing.JToggleButton
import scala.collection.immutable.HashMap
class RoundedBorder(radius:Int) extends Border {
    def getBorderInsets(c:Component):Insets = new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
    def isBorderOpaque() = true
    def paintBorder(c:Component, g:Graphics, x:Int, y:Int, width:Int, height:Int):Unit = g.drawRoundRect(x, y, width-1, height-1, radius, radius);
}
class AcademicalOMeter extends JPanel{
    var g:Graphics2D = null
    var puffer:BufferedImage = null
    var value:Double = 1.0
    override def paint(x: Graphics): Unit = {
        val realval = if(value > 2.0) 2.0 else if(value < 0.0) 0.0 else value
        if(g == null || getWidth != puffer.getWidth() || getHeight() != puffer.getHeight()){
            puffer = createImage(getWidth(), getHeight()).asInstanceOf[BufferedImage]
            g = puffer.createGraphics()
        }
        val middleper = 16
        g.clearRect(0, 0, getWidth(), getHeight())

        g.setColor(new Color(245, 210, 200))
        g.fillRect(0, 4, getWidth()/2 - getWidth()/middleper, getHeight()-8)
        g.setColor(new Color(250, 245, 200))
        g.fillRect(getWidth()/2 - getWidth()/middleper, 4, 2*(getWidth()/middleper), getHeight()-8)
        g.setColor(new Color(200, 235, 190))
        g.fillRect(getWidth()/2 + getWidth()/middleper, 4, getWidth()/2 - getWidth()/middleper, getHeight()-8)

        g.setColor(new Color(50, 100, 180))
        g.fillRect(((realval/2.0) * getWidth()).toInt - 2, 0, 4, getHeight()-4)
        x.drawImage(puffer, 0, 0, getWidth(), getHeight(), this)
    }
}
class Gui(db:WordDatabase, wordclassratings:HashMap[WordClasses.WordClass, Double]) extends JFrame("Academic Text Estimator"){
    
    val (screenwidth, screenheight) = {
        val size = Toolkit.getDefaultToolkit().getScreenSize()
        (size.width, size.height)
    }
    setSize(1100, 750)
    setMinimumSize(new Dimension(800, 400))
    setLocationRelativeTo(null); 
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    
    setLayout(null)
    val topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT))
    topPanel.setBounds(10, 10, 130, 30)
    val label1 = new JLabel("academicalness:")
    label1.setVerticalAlignment(SwingConstants.CENTER)
    topPanel.add(label1)
    
    val rightPanel = new JPanel(new GridLayout(2, 2))
    rightPanel.setBackground(new Color(210, 220, 230))
    rightPanel.setBorder(new RoundedBorder(10))

    val optPanel = new JPanel()
    optPanel.setBackground(new Color(230, 220, 230))
    optPanel.setBorder(new RoundedBorder(10))
    optPanel.setLayout(null)

    val subpanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT))
    subpanel1.add(new JLabel("wordforms to investigate:"))
    subpanel1.setBounds(0,0, 195, 20)
    subpanel1.setOpaque(false)

    val optCont = new JPanel(new FlowLayout(FlowLayout.LEFT))
    optCont.setOpaque(false)
    val check_noun = new JCheckBox("nouns", true)
    check_noun.setOpaque(false)
    check_noun.setFocusable(false)
    val check_verb = new JCheckBox("verbs", true)
    check_verb.setOpaque(false)
    check_verb.setFocusable(false)
    val check_adj = new JCheckBox("adjectives", true)
    check_adj.setOpaque(false)
    check_adj.setFocusable(false)
    val check_adv = new JCheckBox("adverbs", true)
    check_adv.setOpaque(false)
    check_adv.setFocusable(false)
    val check_pp = new JCheckBox("prepositions", true)
    check_pp.setOpaque(false)
    check_pp.setFocusable(false)
    val check_pro = new JCheckBox("pronouns", true)
    check_pro.setOpaque(false)
    check_pro.setFocusable(false)
    val check_con = new JCheckBox("conjunctions", true)
    check_con.setOpaque(false)
    check_con.setFocusable(false)
    val check_int = new JCheckBox("interjections", true)
    check_int.setOpaque(false)
    check_int.setFocusable(false)
    optCont.add(check_noun)
    optCont.add(check_verb)
    optCont.add(check_adj)
    optCont.add(check_adv)
    optCont.add(check_pp)
    optCont.add(check_pro)
    optCont.add(check_con)
    optCont.add(check_int)

    optPanel.add(subpanel1)    
    optPanel.add(optCont)

    val label_score = new JLabel("academical score: ")
    rightPanel.add(label_score)
    val label_words = new JLabel("found words: ")
    rightPanel.add(label_words)
    val label_est = new JLabel("estimation: ")
    rightPanel.add(label_est)
    val label_time = new JLabel("computation time: ")
    rightPanel.add(label_time)
    val inputText = new JPanel(new FlowLayout(FlowLayout.LEFT))
    inputText.add(new JLabel("input text:"))
    inputText.setBounds(10, 45, 200, 20)
    
    val wordlistTitle = new JPanel()
    wordlistTitle.add(new JLabel("Least academic words:"))
    val listModel = new DefaultListModel[String]();
    val wordlist = new JList(listModel)
    val scrollPane1 = new JScrollPane(wordlist)
    wordlist.setEnabled(false)
   
    val area = new JTextArea()
    area.setBackground(new Color(250, 250, 250))
    val scrollPane2 = new JScrollPane(area)

    val btn = new JButton("compute")
    btn.setFocusable(false)
    btn.setBackground(new Color(210, 220, 230))
    btn.setBorder(new RoundedBorder(10))
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR))

    val tgl = new JButton("compute only word classes");
    tgl.setFocusable(false)
    tgl.setBackground(new Color(210, 220, 230))
    tgl.setBorder(new RoundedBorder(10))
    tgl.setCursor(new Cursor(Cursor.HAND_CURSOR))

    val acd = new AcademicalOMeter()
    //i know, i should rather use a layout but i am too lazy
    def placeObjects(){
        scrollPane2.setBounds(10, 70, getWidth()/2, getHeight() - 150)
        btn.setBounds(getWidth()/2 - 93, getHeight() - 75, 100, 30)
        tgl.setBounds(10, getHeight() - 75, 220, 30)
        acd.setBounds(150, 7, getWidth()/2 - 150, 30)
        acd.repaint()
        rightPanel.setBounds(getWidth()/2 + 20, 7, getWidth()/2 -40, 70)
        rightPanel.repaint()
        optPanel.setBounds(getWidth()/2 + 20, 90, getWidth()/2 -40, 90)
        optCont.setBounds(5, 22, getWidth()/2 -50, 70)
        optCont.repaint()
        if(optCont.getWidth() <= 450){
            optPanel.setBounds(getWidth()/2 + 20, 90, getWidth()/2 -40, 110)
            optCont.setBounds(5, 22, getWidth()/2 -50, 85)
        }
        optPanel.repaint()
        wordlistTitle.setBounds(optPanel.getLocation().x, optPanel.getLocation().y + optPanel.getHeight() + 10, 160, 20)
        scrollPane1.setLocation(optPanel.getLocation().x, wordlistTitle.getLocation().y + wordlistTitle.getHeight() + 10)
        scrollPane1.setSize(250, getHeight() - scrollPane1.getLocation().y - 80)
        scrollPane1.repaint()
        repaint()
    }
    placeObjects()
    addComponentListener(new ComponentAdapter(){
        override def componentResized(e:ComponentEvent):Unit = placeObjects()
    })
    add(acd)
    add(inputText)
    add(topPanel)
    add(rightPanel)
    add(scrollPane2)
    add(btn)
    add(optPanel)
    add(wordlistTitle)
    add(scrollPane1)
    add(tgl)

    btn.addActionListener(new ActionListener(){
      override def actionPerformed(x$1: ActionEvent): Unit ={ 
        btn.setEnabled(false)
        btn.repaint()
        val start = System.currentTimeMillis()
        import WordClasses._
        var filt = HashSet.empty[WordClass]
        if(check_noun.isSelected()) filt = filt + NOUN
        if(check_verb.isSelected()) filt = filt + VERB
        if(check_adj.isSelected()) filt = filt + ADJ
        if(check_adv.isSelected()) filt = filt + ADV
        if(check_con.isSelected()) filt = filt + CON
        if(check_int.isSelected()) filt = filt + INTER
        if(check_pro.isSelected()) filt = filt + PRO
        if(check_pp.isSelected()) filt = filt + PP
        val sol = db.estimateAcademical(area.getText(), filt)
        val end = System.currentTimeMillis()
        acd.value = sol._1
        acd.repaint()
        listModel.clear()
        import collection.JavaConverters._
        import java.text.DecimalFormat;
        val form = new DecimalFormat("#,##0.00")
        listModel.addAll(sol._2.distinct.sortWith((a, b) => a._2 <= b._2).map(s => s._1 + " (" + form.format(s._2) + ")").toSeq.asJavaCollection)
        label_score.setText("academical score: " + form.format(sol._1))
        label_words.setText("found words: " + sol._2.size + "/" + sol._3)
        label_time.setText("computation time: " + (end-start) + "ms")
        label_est.setText("estimation: " + (
            if(sol._1 > 1.3) "very academical"
            else if(sol._1 > 1.125) "academical"
            else if(sol._1 > 0.875) "neutral"
            else if(sol._1 > 0.7) "unacademical"
            else "very unacademical"
        ))

        btn.setEnabled(true)
        btn.repaint()
      }
    })
    tgl.addActionListener(new ActionListener(){
      override def actionPerformed(x$1: ActionEvent): Unit ={ 
        tgl.setEnabled(false)
        tgl.repaint()

        val start = System.currentTimeMillis()
        import WordClasses._
        var filt = HashSet.empty[WordClass]
        if(check_noun.isSelected()) filt = filt + NOUN
        if(check_verb.isSelected()) filt = filt + VERB
        if(check_adj.isSelected()) filt = filt + ADJ
        if(check_adv.isSelected()) filt = filt + ADV
        if(check_con.isSelected()) filt = filt + CON
        if(check_int.isSelected()) filt = filt + INTER
        if(check_pro.isSelected()) filt = filt + PRO
        if(check_pp.isSelected()) filt = filt + PP
        val sol = db.estimateOnlyWordClasses(wordclassratings)(area.getText(), filt)

        val end = System.currentTimeMillis()
        acd.value = sol._1
        acd.repaint()
        import collection.JavaConverters._
        import java.text.DecimalFormat;
        val form = new DecimalFormat("#,##0.00")
        label_score.setText("academical score: " + form.format(sol._1))
        label_words.setText("found words: " + sol._2 + "/" + sol._3)
        label_time.setText("computation time: " + (end-start) + "ms")
        label_est.setText("estimation: " + (
            if(sol._1 > 1.3) "very academical"
            else if(sol._1 > 1.125) "academical"
            else if(sol._1 > 0.875) "neutral"
            else if(sol._1 > 0.7) "unacademical"
            else "very unacademical"
        ))
        tgl.setEnabled(true)
        tgl.repaint()
      }
    })
    setVisible(true)
}