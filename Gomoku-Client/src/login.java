import java.awt.*;
import java.applet.*;
import java.awt.event.*;

public class login extends Applet implements ActionListener
{
    // A Button to click
        Button okButton;
    // A textField to get text input
        TextField nameField;
    // A group of radio buttons
        Label label1;
        public static String nickname;

        public void init()
        {
            // Tell the applet not to use a layout manager.
            setLayout(null);
            label1 = new Label("Insert Your Nickname : ");
            // initialze the button and give it a text.
                  okButton = new Button("Play");
            // text and length of the field
                  nameField = new TextField("",100);
                  okButton.addActionListener(this);

            // now we will specify the positions of the GUI components.
            // this is done by specifying the x and y coordinate and
            //the width and height.
                    label1.setBounds(110, 25, 200, 25);
                    okButton.setBounds(120,80,100,30);
                    nameField.setBounds(50,50,250,25);

            // now that all is set we can add these components to the applet
                add(okButton);
                add(label1);
                add(nameField);
         }

    @Override
    public void actionPerformed(ActionEvent e) {
        Button source = (Button)e.getSource();
        if (source.getLabel() == "Play"){
            nickname = nameField.getText();
            remove(okButton);
            remove(label1);
            remove(nameField);
            resize(1080,600);
            GoMoku playGomoku = new GoMoku();
            setLayout(new GridLayout(1,0));
            add(playGomoku);
            playGomoku.init();
            playGomoku.start();
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    } 