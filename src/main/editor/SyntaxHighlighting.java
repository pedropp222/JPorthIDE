package main.editor;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlighting
{
    private final TextEditor editor;
    private final JTextPane textPane;

    private final String keywords = "if else elif while do include memory proc const end offset reset assert in print dup over drop swap";

    private final StyledDocument doc;

    public SyntaxHighlighting(TextEditor ref)
    {
        editor = ref;

        textPane = editor.getPane();

        textPane.setBackground(new Color(25,25,25,255));

        doc = textPane.getStyledDocument();

        StyledEditorKit s = new StyledEditorKit();

        setupDocument(doc);
    }

    public void UpdateStyleAll() throws BadLocationException
    {
        //ArrayList<EditorWord> words = new ArrayList<>();

        for(int i = 0; i < textPane.getDocument().getDefaultRootElement().getElementCount(); i++)
        {
            UpdateStyleLine(i);
        }
        //StyleAll(words);
    }

    public void UpdateStyleLine(int t) throws BadLocationException
    {
        int startT = textPane.getDocument().getDefaultRootElement().getElement(t).getStartOffset();
        int endT = textPane.getDocument().getDefaultRootElement().getElement(t).getEndOffset();

        if (t>0)startT--;

        String txt = textPane.getText(startT, (endT-1)-startT);

        //\/\/.+|\"[a-zA-Z ]+\"+|[a-z0-9A-Z_+\-><\(\)]+
        //comment | string | anything else
        Pattern p = Pattern.compile("//.+|\"[0-9a-zA-Z _\\-?]+\"+|[a-z0-9A-Z_+\\-><()]+");
        Matcher match = p.matcher(txt);

        //System.out.println("Working line "+t);
        //System.out.println("Matching string "+txt);

        boolean any = false;

        while(match.find())
        {
            //System.out.println("Found "+match.group(0));
            StyleWord(startT+match.start(),startT+match.end());
            any = true;
        }

        if (!any)
        {
            StyleWord(startT,endT-1);
        }
    }

    private void StyleAll(ArrayList<EditorWord> wrds)
    {
        Runnable doHighlightAll = () ->
        {
            for (EditorWord a :wrds)
            {
                String wrd = a.getWord();
                try
                {
                    wrd = textPane.getText(a.getStartIndex(), a.getEndIndex() - a.getStartIndex());
                    wrd = wrd.trim();
                    wrd = wrd.replace('\n', '\0');
                    //System.out.println("Word is "+wrd);
                } catch (BadLocationException e)
                {
                    System.out.println("Bad location at " + a.getStartIndex() + " - " + a.getEndIndex());
                }

                if (wrd == null || wrd.length() == 0) return;

                if (wrd.startsWith("\""))
                {
                    doc.setCharacterAttributes(a.getStartIndex(), wrd.length(), doc.getStyle("string"), true);
                } else
                {
                    if (wrd.startsWith("//"))
                    {
                        doc.setCharacterAttributes(a.getStartIndex(), wrd.length(), doc.getStyle("comment"), true);
                    } else if (Arrays.asList(keywords.split(" ")).contains(wrd))
                    {
                        doc.setCharacterAttributes(a.getStartIndex(), wrd.length(), doc.getStyle("keyword"), true);
                    } else
                    {
                        doc.setCharacterAttributes(a.getStartIndex(), wrd.length(), doc.getStyle("regular"), true);
                    }
                }

                a.setPainted(true);
                doc.setLogicalStyle(textPane.getText().length() - 1, doc.getStyle("regular"));
            }
        };
        SwingUtilities.invokeLater(doHighlightAll);
    }

    private void StyleWord(int start, int end)
    {
        Runnable doHighlight = () ->
        {
            String wrd = null;
            try
            {
                wrd = textPane.getText(start, end-start);
                wrd = wrd.trim();
                wrd = wrd.replace('\n','\0');
                //System.out.println("Word is "+wrd);
            } catch (BadLocationException e)
            {
                System.out.println("Bad location at "+start+" - "+end);
            }

            if (wrd==null||wrd.length()==0)return;

            if (wrd.startsWith("\""))
            {
                doc.setCharacterAttributes(start, wrd.length(), doc.getStyle("string"), true);
            }
            else
            {
                if (wrd.startsWith("//"))
                {
                    doc.setCharacterAttributes(start, wrd.length(), doc.getStyle("comment"), true);
                }
                else if (Arrays.asList(keywords.split(" ")).contains(wrd))
                {
                    doc.setCharacterAttributes(start, wrd.length(), doc.getStyle("keyword"), true);
                }
                else
                {
                    doc.setCharacterAttributes(start, wrd.length(), doc.getStyle("regular"), true);
                }
            }

            doc.setLogicalStyle(textPane.getText().length()-1,doc.getStyle("regular"));
        };
        SwingUtilities.invokeLater(doHighlight);
    }

    private void setupDocument(StyledDocument doc)
    {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style s = doc.addStyle("regular",def);
        //StyleConstants.setFontFamily(s,"Monospace");
        StyleConstants.setForeground(s, Color.WHITE);
        StyleConstants.setFontSize(s, 18);

        s = doc.addStyle("keyword",s);
        StyleConstants.setForeground(s,Color.ORANGE);
        StyleConstants.setBold(s,true);

        s = doc.addStyle("comment",s);
        StyleConstants.setForeground(s,new Color(7, 128, 22));
        StyleConstants.setBold(s,false);

        s = doc.addStyle("string",s);
        StyleConstants.setForeground(s,new Color(6, 122, 178));
        StyleConstants.setBold(s,false);
    }
}
