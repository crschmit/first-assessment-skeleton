/**
 * @Author: Christian Schmitt
 * @Date:   2017-06-14T09:17:14-05:00
 * @Email:  crschmit@gmail.com
 * @Filename: Message.js
 * @Last modified by:   Christian Schmitt
 * @Last modified time: 2017-06-19T15:17:41-05:00
 */


 export const messenger = (server, client) => {
   let msgs = {
     disconnect: m => server.end(m.toJSON() + '\n'),
     echo: m => server.write(m.toJSON() + '\n'),
     whisper: m => server.write(m.toJSON() + '\n'),
     broadcast: m => server.write(m.toJSON() + '\n'),
     users: m => server.write(m.toJSON() + '\n'),
     default: m => client.log(`Command <${m.command}> was not recognized`)
   }

   return message => {
     let c = msgs[message.command]
     if (c != null) {
       return c(message)
     } else {
       return msgs.default(message)
     }
   }
 }


export class Message {
  static fromJSON (buffer) {
    return new Message(JSON.parse(buffer.toString()))
  }

  constructor ({ username, command, contents, time }) {
    this.username = username
    this.command = command
    this.contents = contents
    this.time = time
  }

  toJSON () {
    return JSON.stringify({
      username: this.username,
      command: this.command,
      contents: this.contents
    })
  }

  toString () {
    return this.contents
  }
}
