package main.editor;

import main.editor.token.Location;
import main.editor.token.Token;
import main.editor.token.TokenType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorTokens
{
    public ArrayList<Token> tokens;

    public EditorTokens()
    {
        tokens = new ArrayList<>();
    }

    public void AddTokensLine(String line, int lineNum, int startingIndex)
    {
        Pattern p = Pattern.compile("//.+|[.\\S]+");
        Matcher match = p.matcher(line);

        DeleteTokensLine(lineNum);

        while(match.find())
        {
            //no more matches if found comment
            if(!match.group(0).startsWith("//"))
            {
                tokens.add(new Token(match.group(0),
                        new Location(startingIndex+match.start(),lineNum))
                );
            }
        }
    }

    public void DeleteTokensLine(int lineNum)
    {
        tokens.removeIf(e->e.getLocation().line==lineNum);
    }

    public String GetErrors()
    {
        StringBuilder f = new StringBuilder();

        for(Token t : tokens)
        {
            if (t.getType()== TokenType.ERROR)
            {
                f.append(t.getErrorText());
            }
        }

        if (f.length()==0)
        {
            return "No issues with code\n";
        }
        f.append("\n");
        return f.toString();
    }
}
