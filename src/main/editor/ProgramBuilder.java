package main.editor;

import main.editor.utils.WorkingSystem;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Class to typecheck / build a particular file
 * Prints status to main application console
 */
public class ProgramBuilder
{
    private ProcessBuilder builder;
    private String fileName;

    private String availablePython = "";

    public ProgramBuilder(String file)
    {
        this.fileName = file;
        this.builder = new ProcessBuilder();
        CheckPythonAvailable();
    }

    private void CheckPythonAvailable()
    {
        try
        {
            builder.directory(new File(System.getProperty("user.dir")));
            builder.command(WorkingSystem.cmdCall, WorkingSystem.param, "python", "--version");

            Process process = builder.start();
            ErrorCheckStream pythonCheck =
                    new ErrorCheckStream(process.getErrorStream(), Console::WriteLine);
            Executors.newSingleThreadExecutor().submit(pythonCheck);
            int exitCode = process.waitFor();
            if (exitCode == 0)
            {
                availablePython = "python";
                Console.WriteLine("Python available.");
            } else
            {
                builder.directory(new File(System.getProperty("user.dir")));
                builder.command(WorkingSystem.cmdCall, WorkingSystem.param, "python3", "--version");

                process = builder.start();
                Executors.newSingleThreadExecutor().submit(pythonCheck);
                exitCode = process.waitFor();
                if (exitCode == 0)
                {
                    availablePython = "python3";
                    Console.WriteLine("Python3 available.");
                }
                else
                {
                    Console.WriteLine("WARNING: python or python3 not found on system");
                    availablePython = "";
                }
            }
        }
        catch (IOException | InterruptedException e )
        {
            Console.WriteLine("ERROR when trying check python availability on system - "+e.getMessage());
        }
    }

    public void TypeCheck()
    {
        if (availablePython.length()==0)
        {
            Console.WriteLine("Type checking disabled - no python found.");
            return;
        }

        try
        {
            builder.directory(new File(System.getProperty("user.dir")));
            builder.command(WorkingSystem.cmdCall, WorkingSystem.param, availablePython, "porth.py", "check", fileName);

            Console.Clear();

            Process process = builder.start();
            ErrorCheckStream pythonCheck =
                    new ErrorCheckStream(process.getErrorStream(), Console::WriteLine);
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

    public void BuildFile()
    {
        if (availablePython.length()==0)
        {
            Console.WriteLine("Build disabled - no python found");
            return;
        }

        try
        {
            builder.directory(new File(System.getProperty("user.dir")));
            builder.command(WorkingSystem.cmdCall, WorkingSystem.param, availablePython, "porth.py", "com", fileName);

            Console.Clear();

            Process process = builder.start();
            ErrorCheckStream pythonCheck =
                    new ErrorCheckStream(process.getErrorStream(), Console::WriteLine);
            Executors.newSingleThreadExecutor().submit(pythonCheck);
            int exitCode = process.waitFor();
            if (exitCode == 0)
            {
                Console.WriteLine("BUILD SUCCESSFUL - No issues found");
            } else
            {
                Console.WriteLine("BUILD FAILED - " + exitCode);
            }
        }
        catch (IOException | InterruptedException e)
        {
            Console.WriteLine("ERROR when trying to build program - "+e.getMessage());
        }
    }

    public void ChangeFile(String newFile)
    {
        fileName = newFile;
    }
}
