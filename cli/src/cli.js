/**
 * @Author: Christian Schmitt
 * @Date:   2017-06-14T09:17:14-05:00
 * @Email:  crschmit@gmail.com
 * @Filename: cli.js
 * @Last modified by:   Christian Schmitt
 * @Last modified time: 2017-06-15T08:48:28-05:00
 */



import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server

cli
  .delimiter(cli.chalk['yellow']('ftd~$'))

cli
  .mode('connect <username> [host] [port]')
  .delimiter(cli.chalk['green']('connected>'))
  .init(function (args, callback) {
    username = args.username
    server = connect({ host: args.host ? args.host : 'localhost',
      port: args.port ? parseInt(args.port) : 8080 }, () => {
      server.write(new Message({ username, command: 'connect' }).toJSON() + '\n')
      callback() // callback finishes transaction?
    })

    server.on('data', (buffer) => {
      this.log(Message.fromJSON(buffer).toString())
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    const [ command, ...rest ] = words(input)
    const cmd_args = rest
    const contents = rest.join(' ')

    if (command === 'disconnect') {
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'broadcast') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
      // this.log(`broadcast: ${contents}`)
    } else if (command === 'whisper') {
      //server.write(new Message({ username, command, contents }).toJSON() + '\n')
      this.log(`whisper: ${contents}`)
    } else if (command === 'users') {
      //server.write(new Message({ username, command, contents }).toJSON() + '\n')
      this.log(`users: ...`)
    } else {
      this.log(`Command <${command}> was not recognized`)
    }

    callback()
  })
