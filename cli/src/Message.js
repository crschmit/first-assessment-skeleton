/**
 * @Author: Christian Schmitt
 * @Date:   2017-06-14T09:17:14-05:00
 * @Email:  crschmit@gmail.com
 * @Filename: Message.js
 * @Last modified by:   Christian Schmitt
 * @Last modified time: 2017-06-15T20:22:54-05:00
 */



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
