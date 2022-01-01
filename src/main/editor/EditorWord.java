package main.editor;

public class EditorWord
{
    private final String word;
    private final int startIndex;
    private boolean painted;

    public EditorWord(String a, int start)
    {
        word = a;
        startIndex = start;
        painted = false;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getEndIndex()
    {
        return startIndex+word.length();
    }

    public String getWord()
    {
        return word;
    }

    public void setPainted(boolean set)
    {
        painted = set;
    }
}
