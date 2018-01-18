# GW2 Raid Bot [![Discord Bots](https://discordbots.org/api/widget/status/387404077308575744.png)](https://discordbots.org/bot/387404077308575744)
This bot is meant to be used to help organize raid teams in discord servers for GuildWars 2.
It attempts to create a streamlined user experience so that creating and joining raids are easy.

## How does it work?
Once the bot is running, there needs to be a role called 'Raid Leader' (this can be updated to be any role name in the RaidBot.java class).
Any user who should be able to create a raid needs this role.

To create a raid, use the !createRaid command. This will prompt the bot to ask you some questions about your raid, and once you are done, it will create the embedded message
announcing the raid in the channel that you specified.
For people to join the raid, they simply need to click a reaction on the embedded message and then answer the bot on what role they want to play.
After this do this, they can click another specialization reaction to set themselves as a flex role (other roles they can play if necessary).

Raid leaders can also remove people from raids via !removeFromRaid \[raid id\] \[name\]

## How to install the bot
First, the bot requires that Java 8 or higher be installed. Then, the best way to install the bot is to pull this repository
and compile it with maven.

Then, you need to put the resulting jar file where-ever you want to run the bot from.

Next, you need to unzip the icon pack included in this repository and put all of the icons in discord
with the default names discord gives them. Each icon **must** be named after the specialization it represents.

Finally, you need to create a bot via the discord developer application creation page and copy the bot token and put it in a file named 'token'
in the same directory as the jar file.

Finally, you can run the bot and invite it to your discord server and start using it for raids!

## Support Discord
[Discord Server for support for issues with the bot](https://discord.gg/pWs8tDY)

## Credits
- Christopher Bitler: Bot development
- Tyler "Inverness": Bot idea

## License
GPLv3
