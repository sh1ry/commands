# commands
 bukkit command framework

[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)
[![Build Status](https://travis-ci.com/ShiryuDev/commands.svg?branch=master)](https://travis-ci.com/ShiryuDev/commands)

##Setup

<details>
  <summary>Maven</summary>
  
  ```maven
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.ShiryuDev.commands</groupId>
            <artifactId>bukkit</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>
 ```
</details>

##Examples

<details>
 <summary> making basic commands </summary>
 
 ```java
    public class Example implements CommandHandler {

        @Command(names = {"mycommand", "mycommand help"}, permission = "mycommand.help")
        public void myCommand(@NotNull final CommandSender sender){
            sender.sendMessage("test");
        }

        @Command(names = {"mycommand withparameter"}, permission = "mycommand.withparameter")
        public void myCommandParameter(@Parameter(name = "player") @NotNull final Player player){
            player.sendMessage("player");
        }
    } 
 ```
</details>

<details>
 <summary> registering to the bukkit </summary>
 
 ```java
   public class ExamplePlugin extends JavaPlugin {

      @Override
      public void onEnable(){
          final BukkitCommandManager commandManager = new BukkitCommandManager();

          commandManager.handle(this);
          commandManager.registerCommand(
                  new Example()
          );
      }
   }

 ```
 
 </details>
 

