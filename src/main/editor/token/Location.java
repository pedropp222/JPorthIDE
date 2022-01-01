package main.editor.token;

public class Location
{
    public int index;
    public int line;
    public int column;

    public Location(int ind, int line)
    {
        index = ind;
        this.line = line;
    }
}
