package main.editor.token;

import main.editor.token.parse.ParseResult;
import main.editor.token.parse.ResValue;

public class Token
{
    private TokenType type;
    private String text;
    private String errorText;
    private final Location location;
    private int value;

    public Token(String txt, Location loc)
    {
        text = txt;
        location = loc;
        ValueFromString(txt);
    }

    public void setType(TokenType t)
    {
        type = t;
    }

    void ValueFromString(String v)
    {
        if (TryParse(v).result!=ResValue.FAIL)
        {
            type = TokenType.INTEGER;
        }
        else if (v.equals("if") || v.equals("print") || v.equals("include"))
        {
            type = TokenType.KEYWORD;
        }
        else if (v.equals("+"))
        {
            type = TokenType.OPERAND;
        }
        else if (v.startsWith("\"")&&v.endsWith("\""))
        {
            type = TokenType.STRING;
        }
        else
        {
            type = TokenType.ERROR;
            if (v.startsWith("\"")&&!v.endsWith("\""))
            {
                errorText = "Unclosed string literal "+"'"+v+"' - line "+(location.line+1);
            }
            else
            {
                errorText = "Unknown word " + "'" + v + "' - line " + (location.line + 1);
            }
        }
    }

    ParseResult TryParse(String f)
    {
        try
        {
            int a = Integer.parseInt(f);
            return new ParseResult(ResValue.SUCESS, a);
        }
        catch (Exception e)
        {
            return new ParseResult(ResValue.FAIL,-1);
        }
    }

    public Location getLocation()
    {
        return location;
    }

    public TokenType getType()
    {
        return type;
    }

    public String getErrorText()
    {
        return errorText;
    }
}
