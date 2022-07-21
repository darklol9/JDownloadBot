# JDownloadBot
![downloads](https://img.shields.io/github.com/darklol9/JDowloadBot/total.svg)

Ready to use discord download bot for mainly jar-file based products.\
Useful features:
- Watermarking
- Obfuscation
- Highly configurable

## Implementations
If you want to add your own obfuscators to the bot it is fairly simple if you follow the example of Proguard and Caesium however these are free and the process may differ for proprietary software.\
If you wish to add your own watermarking, you may need some [asm](https://asm.ow2.io/) knowledge as you'd be editing class method or fields. Other ways do exist such as:
- Adding information to the zip comment
- Adding extra information to end of files
- Setting obfuscation dictionaries to the username or user id of the user who is downloading

## Specs
It is designed to work with MySql, but you can always change that, and I may add different versions so you can easily choose in the config file

## Contributers
- [darklol9](https://github.com/darklol9)
