/*package gui;

import java.awt.Color;

import javax.swing.*;
import javax.swing.text.*;

public class Console extends JTextPane {
    private final StyledDocument doc;
    private JScrollBar scrollBar;

    public Console() {
        super();
        doc = this.getStyledDocument();
        this.setEditable(false);
    }

     //http://www.java2s.com/Code/Java/Swing-JFC/
     //ExtensionofJTextPanethatallowstheusertoeasilyappendcoloredtexttothedocument
     //.htm
     
    private void append(String s, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, c);

        try {
            doc.insertString(doc.getLength(), s, aset);
        } catch (BadLocationException e) {
            // This should never happen. (toon)
            e.printStackTrace();
        }
        // setCharacterAttributes(aset, false);
        // replaceSelection(s); // there is no selection, so inserts at caret
        if (!scrollBar.getValueIsAdjusting()) {
            setCaretPosition(doc.getLength()); // place caret at the end (with
                                               // no
                                               // selection)
        }
    }

    public void output(String output) {
        append(output, Color.gray);
    }

    public void outputError(String output) {
        append(output, Color.red);
    }

    public void outputStatus(String output) {
        append(output, Color.black);
    }

    public void outputTestSysOut(String output) {
        append(output, Color.magenta);
    }

    public void setScroll(JScrollPane scrollPane) {
        this.scrollBar = scrollPane.getVerticalScrollBar();
    }

    // Gewoon om te zien welke kleuren je kan gebruiken.
    @SuppressWarnings(value = { "unused" })
    private void printAllColors() {
        append("black\n", Color.black);
        append("blue\n", Color.blue);
        append("cyan\n", Color.cyan);
        append("darkGray\n", Color.darkGray);
        append("gray\n", Color.gray);
        append("green\n", Color.green);
        append("lightGray\n", Color.lightGray);
        append("magenta\n", Color.magenta);
        append("orange\n", Color.orange);
        append("pink\n", Color.pink);
        append("red\n", Color.red);
        append("white\n", Color.white);
        append("yellow\n", Color.yellow);

    }
}
*/