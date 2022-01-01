package main.editor.token.parse;

public class ParseResult
{
    public int value;
    public ResValue result;

    public ParseResult(ResValue r, int v)
    {
        this.value = v;
        this.result = r;
    }
}