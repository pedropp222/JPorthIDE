package main.editor;

import javax.swing.*;

public class Console
{
    public static JTextArea text;

    public static void WriteLine(String x)
    {
        text.append(x+"\n");
    }

    public static void Clear()
    {
        text.setText("");
    }
}
