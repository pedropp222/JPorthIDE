# JPorthIDE

**Warning. This is not ready for any type of "professional use" and is just a hobby project. Use at your own risk.**

Porth IDE in Java. Enables you to write your porth code in a simple environment.\
The text editor includes some cool features like syntax highlighting and it's integration with the porth.py compiler makes it easy and fast to automatically type check and build your code while you write it!

# Features
- Porth Syntax Highlighting
- Integration with porth.py compiler for fast and automatic typechecking (save file to perform type check)
- Build support on Linux
- Drag and drop to quickly open files
- It's written in java in 2022

# Requirements

## Windows
- python or python3 installed and available as a terminal command
- porth.py file from [Tsoding's Porth Language](https://gitlab.com/tsoding/porth/) (this file is outdated now, but it's the only way of working with windows)
- jre to run the jar file
- converting to asm is possible but you cannot generate an executable

## Linux
- python or python installed and available as a terminal command (to be deprecated)
- porth.py file from [Tsoding's Porth Language](https://gitlab.com/tsoding/porth/) (to be changed to the main porth executable soon)
- jre to run the jar file
- nasm and GNU ld to compile and link final executable
