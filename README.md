WrkFoo
======
(Pronounced work foo)

Inspired by AS/400 PDM (Program Development Manager).

Builds on my other project jguifier to create a GUI and TUI (text user interface) to create everyday tools,
such as listing directories, browsing though databases, managing git etc.

Each tool is called a WrkFoo, and has a set of parameters which defines how to perform a task.
Many WrkFoos produce a list of results, such as a set of Files.
These are displayed in a table, and then other WrkFoos can be applied to them.

For example, use a WrkFoo called "WrkF" to lists all images within a directory.
It has a set of companion WrkFoos, which can manipulate those images.
Run one of these tools by right clicking on one of the files, and selecting the tool from the popup menu.
Or use just the keyboard by typing the tool's shortcut into the text field at the left of each row in the table.

In this example, we may have WrkFoos for rotating an image, displaying the image in your favourite image viewer,
editting the image in GIMP, deleting or renaming the image.

It is planned to create both a GUI and a TUI (an ncurses like interface), so you can use the same tools in a
rich GUI, and also use the TUI using SSH to a remote server.


