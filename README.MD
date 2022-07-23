# JDownloadBot
![downloads](https://img.shields.io/github/downloads/darklol9/JDownloadBot/total.svg)
![license](https://img.shields.io/github/license/darklol9/JDownloadBot)

Discord download bot ready to use with some modifications for jar based products.\
Useful features:
- Watermarking
- Obfuscation

## Implementations
If you want to add your own obfuscators to the bot it is fairly simple if you follow the example of Proguard but is limited to obfuscators that have **command-line support**\
If you wish to add your own watermarking, you may need some [asm](https://asm.ow2.io/) knowledge as you'd be editing class method or fields. Other ways do exist such as:
- Adding information to the zip comment
- Adding extra information to end of files
- Setting obfuscation dictionaries to the username or user id of the user who is downloading

## Specs
- MySql

## Screenshots
### Download Panel
![panel](https://cdn.upload.systems/uploads/Q3qMbzrF.png)

## Configuration
```yaml
token: token

panels:
  download:
    title: Polar Downloads
    description: This is an area where you can download the polar products
    footer: Polar Downloads
    color: 11393254
    channel: 0

products:
  polar-vaults:
    name: Vaults
    table: vaults
    url: https://google.com
    libraries: [ ]

database:
  query: SELECT * FROM %s WHERE discord = ?
  username: root
  password: pass
  host: localhost
  port: 3306
  dbname: polar
  use-ssl: false
  ssl:
    username: user
    password: pass
    host: localhost
    port: 22

messages:
  enqueue: You are **%d** in the queue
  already-queued: You are currently **%d** in the queue for **%s**
  not-whitelisted: You aren't whitelisted to **%s**
```
