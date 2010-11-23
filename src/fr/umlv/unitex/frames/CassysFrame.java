package fr.umlv.unitex.frames;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.CassysCommand;
import fr.umlv.unitex.process.commands.MultiCommands;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


/**
 * Main frame of the cassys menu.
 * <p/>
 * <p/>
 * This class displays a frame allowing the user to do the following actions :
 * <ul>
 * <li> select a transducers list file and launch a cascade
 * <li> create a new transducer list file or edit an existing one
 * </ul>
 * <p/>
 * Internally, this class is made of <code>JFileChooser</code> which allows the user to select a transducer list
 * file and three <code>JButton</code> which allows the user to launch the cascade, edit an existing transducer
 * list file or create a new existing transducer list file.
 *
 * @author David Nott
 */
public class CassysFrame extends JInternalFrame implements ActionListener {

    static CassysFrame frame;

    /**
     * The file explorer which allows the user to select a transducer list file.
     * <p/>
     * This file explorer is currently initiated on the cassys directory which is expected to contain transducers
     * list files.
     * <code>open</code> and <code>cancel</code> buttons are hidden since an editing already exists
     */
    private JFileChooser fc;

    /**
     * The <code>launch</code> button.
     * <p/>
     * This class is listenning to it
     */
    private JButton launch;

    /**
     * The <code>new</code> button.
     * <p/>
     * This class is listenning to it
     */
    private JButton _new;

    /**
     * The <code>edit</code> button.
     * <p/>
     * This class is listenning to it
     */
    private JButton edit;


    /**
     * The <code>CassysFrame</code> constructor
     * <p/>
     * This class creates the <code>JFileChooser</code> and the <code>JButton</code>. It uses the <code>BorderLayout</code>
     * layout
     * manager to display these elements on the frame.
     */
    public CassysFrame() {
        super("Cassys", true, true, true, true);

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                setVisible(false);
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);


        setLayout(new BorderLayout());

        fc = Config.getTransducerListDialogBox();
        this.getContentPane().add(fc, BorderLayout.WEST);


        JPanel jpan = new JPanel();
        jpan.setLayout(new BoxLayout(jpan, BoxLayout.Y_AXIS));

        Dimension defaultButtonDimension = new Dimension(110, 28);

        jpan.add(Box.createRigidArea(new Dimension(150, 80)));


        _new = new JButton("New");
        _new.setMaximumSize(defaultButtonDimension);
        _new.setAlignmentX(Component.CENTER_ALIGNMENT);
        _new.addActionListener(this);
        jpan.add(_new);

        edit = new JButton("Edit");
        edit.setMaximumSize(defaultButtonDimension);
        edit.setAlignmentX(Component.CENTER_ALIGNMENT);
        edit.addActionListener(this);
        jpan.add(edit);


        jpan.add(Box.createRigidArea(new Dimension(150, 150)));

        launch = new JButton("Launch");
        launch.setMaximumSize(defaultButtonDimension);
        launch.setAlignmentX(Component.CENTER_ALIGNMENT);
        launch.addActionListener(this);
        jpan.add(launch);

        this.getContentPane().add(jpan, BorderLayout.EAST);


        this.pack();
        this.setVisible(true);
    }


    /**
     * This functions defines reactions when an action event is listened by this class
     *
     * @param a the action listened by the frame
     */
    public void actionPerformed(ActionEvent a) {

        if (a.getSource() == _new) {
            Config.setCurrentTransducerList(null);
            UnitexFrame.getFrameManager().newTransducerListConfigurationFrame(null);
        }


        if (a.getSource() == edit) {
            Config.setCurrentTransducerList(fc.getSelectedFile());
            UnitexFrame.getFrameManager().newTransducerListConfigurationFrame(fc.getSelectedFile());
        }

        if (a.getSource() == launch) {
            if (fc.getSelectedFile() != null) {

                MultiCommands cassysCommand = new MultiCommands();

                File f_alphabet = Config.getAlphabet();
                File f_transducer = fc.getSelectedFile();
                File f_target = Config.getCurrentSnt();
                CassysCommand com = new CassysCommand().alphabet(f_alphabet)
                        .targetText(f_target).transducerList(f_transducer);

                cassysCommand.addCommand(com);
                //new ProcessInfoFrame(com, true, new CassysDo());
                Launcher.exec(cassysCommand, true, new CassysDo());
            }
        }

    }

    /**
     * Defines the action to take when the Cassys command ends.
     *
     * @author David Nott
     */
    class CassysDo implements ToDo {

        public void toDo() {
            UnitexFrame.getFrameManager().newConcordanceParameterFrame();
        }

    }

}
