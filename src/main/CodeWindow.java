package main;

import main.editor.*;
import main.editor.utils.SystemType;
import main.editor.utils.WorkingSystem;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

public class CodeWindow extends JFrame
{
    private JPanel mainPanel;
    private JTextArea console;
    private JTextArea documentStats;
    private JTextPane editorText;

    private final TextEditor codeEditor;

    private final File compLoc;

    public static final String VERSION = "0.0.3.2a";


    public static void main(String[] args) throws BadLocationException
    {
        new CodeWindow();
    }

    public CodeWindow()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800,600);

        Console.text = console;
        DocumentStats.stats = documentStats;

        String osname = "";

        try
        {
            osname = System.getProperty("os.name");
        }
        catch (Exception e)
        {
            System.out.println("Exception getting property os name: "+e.getMessage());
        }

        Console.WriteLine("Initializing on system "+osname);

        if (osname.contains("Win"))
        {
            WorkingSystem.setSystem(SystemType.WINDOWS);
        }
        else if (osname.contains("nix") || osname.contains("nux") || osname.contains("aix"))
        {
            WorkingSystem.setSystem(SystemType.LINUX);
        }
        else
        {
            WorkingSystem.setSystem(SystemType.OTHER);
            Console.WriteLine("Incompatible system, some features may not work.");
        }

        compLoc = new File(System.getProperty("user.dir")+"/porth.py");

        if (!compLoc.exists())
        {
            Console.WriteLine("WARNING - porth.py not found. No type checking will occur.");
        }
        else
        {
            Console.WriteLine("porth.py found. All set.");
        }

        File f = new File(System.getProperty("user.dir")+"/temp.porth");

        FileFilter porthFilter = new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.getAbsolutePath().endsWith(".porth") || f.isDirectory();
            }

            @Override
            public String getDescription()
            {
                return "Porth file | *.porth";
            }
        };

        codeEditor = new TextEditor(this,editorText,f.getAbsolutePath(),true);

        SetTitle(codeEditor.getFileName(),false);

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenu tools = new JMenu("Tools");

        JMenuItem save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        save.addActionListener(e ->
        {
            if (codeEditor.isTempFile())
            {
                JFileChooser jf = new JFileChooser();

                jf.setDialogTitle("Save new porth file...");
                jf.setFileFilter(porthFilter);

                jf.setCurrentDirectory(new File(System.getProperty("user.dir")));
                if (jf.showSaveDialog(CodeWindow.this) == JFileChooser.APPROVE_OPTION)
                {
                    codeEditor.SetNewName(jf.getSelectedFile().getAbsolutePath());
                }
            }
            codeEditor.SaveFile();
        });

        JMenuItem open = new JMenuItem("Open");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        open.addActionListener(e ->
        {
            JFileChooser file = new JFileChooser();

            file.setDialogTitle("Open porth file");
            file.setCurrentDirectory(new File(System.getProperty("user.dir")));
            file.setFileFilter(porthFilter);
            if (file.showOpenDialog(CodeWindow.this) == JFileChooser.APPROVE_OPTION)
            {
                codeEditor.SetNewName(file.getSelectedFile().getAbsolutePath());
                codeEditor.LoadFile(file.getSelectedFile());
            }
        });

        JMenuItem quit = new JMenuItem("Quit");
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        quit.addActionListener(e -> System.exit(0));

        menu.add(open);
        menu.add(save);
        menu.add(quit);

        JMenuItem build = new JMenuItem("Build file");
        build.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        build.addActionListener(e->
                {
                    if (codeEditor.isTempFile())
                    {
                        save.doClick();
                    }

                    codeEditor.getBuilder().BuildFile();
                }
                );

        tools.add(build);

        mb.add(menu);
        mb.add(tools);

        setJMenuBar(mb);

        add(mainPanel);


        setVisible(true);

        editorText.setFocusable(true);
    }

    public void SetTitle(String file, boolean dirty)
    {
        setTitle("JPorthIDE "+VERSION+" - "+file+(dirty?"*":""));
    }



}