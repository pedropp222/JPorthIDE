package main.editor;

import main.CodeWindow;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class TextEditor
{
    private final JTextPane text;

    private String fileName;
    private boolean tempFile;

    private boolean updateAll;
    private boolean removeTab;

    private int lastIndex;
    private int lastLine;

    private boolean loading = false;

    private EditorTokens tokenList;
    private SyntaxHighlighting syntax;

    private ProcessBuilder builder;

    private PCaret caret;

    private final CodeWindow window;

    public TextEditor(CodeWindow w, JTextPane p, String f, boolean temp)
    {
        text = p;
        tempFile = temp;
        fileName = f;

        updateAll = false;
        removeTab = false;

        lastIndex = 0;
        lastLine = 0;

        window = w;

        Initialize();
    }

    private void Initialize()
    {
        tokenList = new EditorTokens();
        syntax = new SyntaxHighlighting(this);

        builder = new ProcessBuilder();

        caret = new PCaret();

        text.setCaretColor(new Color(245, 197, 14));

        text.firePropertyChange("caretWidth",-1,2);

        //text.setCaret(caret);

        text.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                window.SetTitle(getFileName(),true);
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                window.SetTitle(getFileName(),true);
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {

            }
        });



        text.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                super.keyPressed(e);
                if (e.getKeyCode()==KeyEvent.VK_V && e.isControlDown())
                {
                    System.out.println("pasted");
                    updateAll = true;
                }
                else if (e.getKeyCode()==KeyEvent.VK_S && e.isControlDown())
                {
                    if (SaveFile())
                    {
                        try
                        {
                            RefreshTokens();
                        } catch (IOException | InterruptedException ex)
                        {
                            Console.WriteLine("ERROR in text syntax styler "+ex.getMessage());
                        }
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_TAB)
                {
                    removeTab = true;
                }
            }
        });



        text.addCaretListener(new CaretListener()
        {
            @Override
            public void caretUpdate(CaretEvent e)
            {
                if (loading) return;

                lastIndex = e.getDot();

                lastLine = GetLine(lastIndex);

                if (updateAll)
                {
                    updateAll = false;
                    try
                    {
                        syntax.UpdateStyleAll();
                    } catch (BadLocationException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    try
                    {
                        syntax.UpdateStyleLine(GetLine(e.getDot()));
                    } catch (BadLocationException ex)
                    {
                        ex.printStackTrace();
                    }
                }

                //always convert tabs to 4 spaces
                if (removeTab)
                {
                    removeTab = false;

                    SwingUtilities.invokeLater(()->
                    {
                        try
                        {
                            text.getStyledDocument().remove(e.getDot()-1,1);
                            text.getStyledDocument().insertString(e.getDot(),"   ",text.getStyle("regular"));
                        } catch (BadLocationException ex)
                        {
                            Console.WriteLine("Error deleting tab "+ex.getMessage());
                        }
                     });

                }

                UpdateStats(e.getDot());

                //doc.setLogicalStyle(lastIndex,doc.getStyle("regular"));
            }
        });
    }

    public boolean SaveFile()
    {
        if (tempFile)
        {
            return false;
        }

        try
        {

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(text.getText());

            writer.close();

            window.SetTitle(getFileName(),false);

            tempFile = false;

            TypeCheck();
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private int GetLine(int position)
    {
        return text.getDocument().getDefaultRootElement().getElementIndex(position);
    }

    private int GetTotalLines()
    {
        return text.getDocument().getDefaultRootElement().getElementCount();
    }

    private String GetTextLine(int line) throws BadLocationException
    {
        int startT = GetStartOffset(line);
        int endT = GetEndOffset(line);

        if (line>0)startT--;

        return text.getText(startT, (endT-1)-startT);
    }

    private int GetStartOffset(int line)
    {
        return text.getDocument().getDefaultRootElement().getElement(line).getStartOffset();
    }

    private int GetEndOffset(int line)
    {
        return text.getDocument().getDefaultRootElement().getElement(line).getEndOffset();
    }

    public String getFileName()
    {
        return fileName;
    }

    public JTextPane getPane()
    {
        return text;
    }

    private void RefreshTokens() throws IOException, InterruptedException
    {
        if (tempFile)
        {
            Console.Clear();
            Console.WriteLine("Checking disabled. Save file first");
            return;
        }

        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                //console.setText("");

                try
                {
                    tokenList.AddTokensLine(GetTextLine(lastLine),lastLine,GetStartOffset(lastLine));
                } catch (BadLocationException e)
                {
                    e.printStackTrace();
                }

                //console.append("Token count: "+tokenList.tokens.size()+"\n");
                //console.append(tokenList.GetErrors());
            }
        };
        SwingUtilities.invokeLater(r);

        if (!SaveFile())
        {
            return;
        }

        TypeCheck();

        /*if (isWindows) {
            builder.command("cmd.exe", "/c", "dir");
        } else {
            builder.command("sh", "-c", "ls");
        }*/

        //System.out.println(System.getProperty("user.dir"));
        //python porth.py check hello.porth


    }

    private void TypeCheck()
    {
        try
        {
            builder.directory(new File(System.getProperty("user.dir")));
            builder.command("cmd.exe", "/c", "python", "porth.py", "check", fileName);

            Console.Clear();

            Process process = builder.start();
            PythonStream pythonCheck =
                    new PythonStream(process.getErrorStream(), Console::WriteLine);
            Executors.newSingleThreadExecutor().submit(pythonCheck);
            int exitCode = process.waitFor();
            if (exitCode == 0)
            {
                Console.WriteLine("Finished checking - No issues found");
            } else
            {
                Console.WriteLine("Finished checking - " + exitCode);
            }
        }
        catch (IOException | InterruptedException e )
        {
            Console.WriteLine("ERROR when trying to type check program - "+e.getMessage());
        }
        //assert exitCode == 0;
    }

    private void UpdateStats(int pos)
    {
        int ln = GetLine(pos);
        int col = pos-GetStartOffset(ln);

        ln++;
        col++;

        try
        {
            DocumentStats.stats.setText("LN: " + ln + " | COL: " + col);
        }
        catch (NullPointerException e)
        {
            System.out.println("DOCUMENT STATS IS NULL");
        }
    }

    public void SetNewName(String x)
    {
        if (tempFile)
        {
            tempFile = false;
        }

        if (!x.endsWith(".porth"))
        {
            x+=".porth";
        }

        fileName = x;
    }

    public void LoadFile(File f)
    {
        loading = true;
        try
        {
            text.setText("");

            Scanner sc = new Scanner(f);
            while (sc.hasNextLine())
            {
                text.setText(text.getText()+sc.nextLine()+"\n");
            }


            updateAll = true;
            loading = false;
            text.setCaretPosition(0);
            Console.WriteLine("File loaded");
        }
        catch (FileNotFoundException fex)
        {
            Console.WriteLine("Could not read file "+f.getAbsolutePath());
        }
        loading = false;
    }

    public boolean isTempFile()
    {
        return tempFile;
    }
}