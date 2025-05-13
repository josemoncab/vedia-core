# File System

A configuration system using annotations that bind a file to a class in a
relation many-to-one, meaning one class can have multiple files (languages for
example)

## Annotations

### @Source

`Required (or @MultiSource)`

Marks a class as a holder for the library. Needs the relative path of the file
inside the plugin's folder.

### @Section

Mark a local class as a config section. Need a path

### @Path

`Required`

Path of the field inside the corresponding file. If it's inside a section, the
section path will be added automatically

```java

@Path("Path")
public static String test = "Text example!";
```

Inside a section.

```java

@Section("Section")
public static class Section {
    @Path("Path")
    // Result path is Section.Path
    public static String test = "Text example!";
}
```

Both examples will result in the same path inside the file

### @Comments

List of comment for the given field, section or file. Each element in the list
will be converted in a new line

```java

@Comments({"Example comment", "Another line"})
public static String test = "Text example!";
```

## Example

Given this code:

```java

@MultiSource("lang/%.yml")
public final class Language {
    @Comments({"Message when console tries to execute a command"})
    @Path("NoConsole")
    public static String NO_CONSOLE = "You cant use this in the console";

    private transient Commands commands = new Commands();

    @Comments({"Command related messages", "Example"})
    public static class Commands {
        @Path("Commands.NoPermission")
        public static String NO_PERMISSION = "You don't have permission";

        private transient HelpCommand helpCommand = new HelpCommand();

        private transient TpCommand tpCommand = new TpCommand();

        public static class HelpCommand {
            @Path("Commands.HelpCommand.Txt")
            public static String TXT = "List of commands in the server";
        }

        public static class TpCommand {
            @Path("Commands.TpCommand.Txt")
            public static String TXT = "Use /tp <player>";
        }
    }

    private transient Others others = new Others();

    public static class Others {
        @Path({"Others", "NotPlayer"})
        public static String NOT_PLAYER = "That entity is not a player";
    }
}
```

The result is:

```yaml
# Message when console tries to execute a command
NoConsole: "You cant use this in the console"
# Command related messages
# Example
Commands:
    NoPermission: "You don't have permission"
    HelpCommand:
        Txt: "List of commands in the server"
    TpCommand:
        Txt: "Use /tp <player>"
Others:
    NotPlayer: "That entity is not a player"
```