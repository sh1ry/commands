# commands
[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)
[![Build Status](https://travis-ci.com/ShiryuDev/commands.svg?branch=master)](https://travis-ci.com/ShiryuDev/commands)
[![](https://jitpack.io/v/ShiryuDev/commands.svg)](https://jitpack.io/#ShiryuDev/commands)
[![CodeFactor](https://www.codefactor.io/repository/github/shiryudev/commands/badge)](https://www.codefactor.io/repository/github/shiryudev/commands)

A lightweight,annotation based, easy to use command framework for bukkit and bungeecord
 
If you need help please come to our support discord: 
https://discord.com/invite/xaGZhwW

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
        ## for bukkit projects
        <dependency>
            <groupId>com.github.ShiryuDev.commands</groupId>
            <artifactId>bukkit</artifactId>
            <version>1.4.4</version>
        </dependency>
        
        ## for bungeecord projects
        <dependency>
            <groupId>com.github.ShiryuDev.commands</groupId>
            <artifactId>bungee</artifactId>
            <version>1.4.4</version>
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
        public void myCommandParameter(@NotNull final Player sender, @Parameter(name="target") Player target){
            player.sendMessage("player target: " + target.getName());
        }
    } 
 ```
</details>

<details>
 <summary> creating parameter types for your own objects </summary>
 
 ```java
   public class SapphireRankParameterType implements ParameterType<SapphireRank> {

       @NotNull
       @Override
       public SapphireRank transform(@NotNull final SimpleSender sender, @NotNull final String value) {
           final SapphireRank rank = Sapphire.getInstance()
                   .getManagerHandler()
                   .getManager(RankManager.class)
                   .findRank(value)
                   .orElse(null);

           if (rank == null){
               sender.sendMessage(String.format(CommandLocale.NOT_FOUND, value));

               return null;
           }

           return rank;
       }

       @Override
       @NotNull
       public List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> set, @NotNull final String value) {
           return Sapphire.getInstance()
                   .getManagerHandler()
                   .getManager(RankManager.class)
                   .getRANKS()
                   .stream()
                   .filter(rank -> StringUtils.startsWithIgnoreCase(value, rank.getName()))
                   .map(SapphireRank::getName)
                   .collect(Collectors.toList());
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
          commandManager.registerCommand(new Commands());
      }
   }

 ```
 
 </details>
 
 <details>
 <summary> registering to the bungeecord </summary>
 
 ```java
    public class ExamplePlugin extends Plugin {

        @Override
        public void onEnable() {
            final BungeeCommandManager commandManager = new BungeeCommandManager();

            commandManager.handle(this);
            commandManager.registerCommand(new Commands());
        }
    }

 ```
 </details>



