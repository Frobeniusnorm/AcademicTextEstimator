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
class Gui(db:WordDatabase) extends JFrame("Academical Text Estimator"){
    
    val (screenwidth, screenheight) = {
        val size = Toolkit.getDefaultToolkit().getScreenSize()
        (size.width, size.height)
    }
    setSize(1100, 750)
    setMinimumSize(new Dimension(600, 300))
    setLocationRelativeTo(null); 
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    //i know, i should rather use a layout but i am too lazy
    setLayout(null)
    val topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT))
    topPanel.setBounds(10, 10, 130, 30)
    val label1 = new JLabel("academicalness:")
    label1.setVerticalAlignment(SwingConstants.CENTER)
    topPanel.add(label1)
    
    val inputText = new JPanel(new FlowLayout(FlowLayout.LEFT))
    inputText.add(new JLabel("input text:"))
    inputText.setBounds(10, 45, 200, 20)

    val area = new JTextArea()
    area.setBackground(new Color(250, 250, 250))

    val btn = new JButton("compute")
    btn.setFocusable(false)
    btn.setBackground(new Color(210, 220, 230))
    btn.setBorder(new RoundedBorder(10))
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR))

    val acd = new AcademicalOMeter()
    
    def placeObjects(){
        area.setBounds(10, 70, getWidth()/2, getHeight() - 150)
        btn.setBounds(getWidth()/2 - 93, getHeight() - 75, 100, 30)
        acd.setBounds(150, 7, getWidth()/2 - 150, 30)
        acd.repaint()
    }
    placeObjects()
    addComponentListener(new ComponentAdapter(){
        override def componentResized(e:ComponentEvent):Unit = placeObjects()
    })
    add(acd)
    add(inputText)
    add(topPanel)
    add(area)
    add(btn)

    btn.addActionListener(new ActionListener(){
      override def actionPerformed(x$1: ActionEvent): Unit ={ 
        btn.setEnabled(false)
        btn.repaint()
        val sol = db.estimateAcademical(area.getText())
        acd.value = sol._1
        acd.repaint()
        btn.setEnabled(true)
        btn.repaint()
      }
    })
    setVisible(true)
}