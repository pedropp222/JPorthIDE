package main.editor.utils;

public class WorkingSystem
{
    public static String cmdCall = "";
    public static String param = "";

    private static SystemType system;

    public static void setSystem(SystemType t)
    {
        if (t == SystemType.WINDOWS)
        {
            cmdCall = "cmd.exe";
            param = "/c";
        }
        else if (t == SystemType.LINUX)
        {
            cmdCall = "sh";
            param = "-c";
        }
    }
}
