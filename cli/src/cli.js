/**
 * @Author: Christian Schmitt
 * @Date:   2017-06-14T09:17:14-05:00
 * @Email:  crschmit@gmail.com
 * @Filename: cli.js
 * @Last modified by:   Christian Schmitt
 * @Last modified time: 2017-06-19T15:23:17-05:00
 */



import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message, messenger } from './Message'
import { execCMD } from './cmd'

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
      let self = this
      let logger = (color, message) => self.log(cli.chalk[color](message))

      execCMD(mssg, logger)
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    const [ command, ...rest ] = words(input)
    const contents = rest.join(' ')

    let message = new Message({ username, command, contents })
    let self = this
    let dispatch = messenger(server, self)

    dispatch(message)

    callback()
  })
