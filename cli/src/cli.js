/**
 * @Author: Christian Schmitt
 * @Date:   2017-06-14T09:17:14-05:00
 * @Email:  crschmit@gmail.com
 * @Filename: cli.js
 * @Last modified by:   Christian Schmitt
 * @Last modified time: 2017-06-15T23:46:34-05:00
 */



import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server
let lastMssg

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
      callback()
    })

    server.on('data', (buffer) => {
      let mssg = Message.fromJSON(buffer)
      let cmd = mssg.command
      let clr = null
      let m = null

      let ts = mssg.time.split(' ')
      let t = [ts[3], ...ts.slice(0, 3)].join(' ')
      let u = mssg.username
      let c = mssg.contents
      let as = null
      if (cmd === 'disconnect') {
        clr = 'yellow'
        m = `${t}: <${u}> has disconnected`
      } else if (cmd === 'echo') {
        clr = 'gray'
        m = `${t} <${u}> (echo) ${c}`
      } else if (cmd === 'broadcast') {
        clr = 'white'
        m = `${t} <${u}> (all): ${c}`
      } else if (cmd === 'whisper') {
        clr = 'cyan'
        as = c.split(' ')
        m = `${t} <${u}>: (whisper) ${as.slice(1).join(' ')}`
      } else if (cmd === 'users') {
        clr = 'magenta'
        m = `${t}: currently connected users: \n${c.split(' ').map(s => ' ' + s).join('\n')}`
      } else if (cmd === 'connect') {
        clr = 'blue'
        m = `${t}: <${u}> has connected`
      } else {
        clr = 'red'
      }
      if (m != null) this.log(cli.chalk[clr](m))
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    const [ command, ...rest ] = words(input)
    const contents = rest.join(' ')
    let mssg = null


    if (command === 'disconnect') {
      mssg = new Message({ username, command })
      server.end(mssg.toJSON() + '\n')
    } else if (command === 'echo') {
      mssg = new Message({ username, command, contents })
      server.write(mssg.toJSON() + '\n')
    } else if (command === 'broadcast') {
      mssg = new Message({ username, command, contents })
      server.write(mssg.toJSON() + '\n')
    } else if (command === 'whisper') {
      mssg = new Message({ username, command, contents })
      server.write(mssg.toJSON() + '\n')
    } else if (command === 'users') {
      mssg = new Message({ username, command, contents })
      server.write(mssg.toJSON() + '\n')
    } else if ((input.split(' ').length === 1) && (input.split(' ')[0] != '')) {
      this.log(`Command <${input.split(' ')[0]}> was not recognized`)
    } else {
      //this.log(`Please provide a command, ${username}`)
    }

    callback()
  })
