import javax.swing.*;

public class Frame extends JFrame {
    /**
     * https://docs.oracle.com/javase/7/docs/api/javax/swing/JFrame.html
     */

    public Frame(){
        this.add(new Panel()); //?
        this.setTitle("Snake"); //Sets title
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); //The pack() method is defined in Window class in Java, and it sizes the frame so that all its contents are at or above their preferred sizes.
        this.setVisible(true);
        this.setLocationRelativeTo(null); //Window now appear in the middle of computer screen
    }
}
