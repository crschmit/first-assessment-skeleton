/**
 * @Author: Christian Schmitt
 * @Date:   2017-06-19T14:22:37-05:00
 * @Email:  crschmit@gmail.com
 * @Filename: cmd.js
 * @Last modified by:   Christian Schmitt
 * @Last modified time: 2017-06-19T14:47:40-05:00
 */

let command = (color, printMssg) => ({ color, printMssg })

let CMDs = {
  connect: command('blue', m => `${m.time}: <${m.username}> has connected`),
  disconnect: command('yellow', m => `${m.time}: <${m.username}> has disconnected`),
  echo: command('gray', m => `${m.time} <${m.username}> (echo) ${m.contents}`),
  whisper: command('cyan', m => `${m.time} <${m.username}>: (whisper) ${m.contents.split(' ').slice(1).join(' ')}`),
  broadcast: command('white', m => `${m.time} <${m.username}> (all): ${m.contents}`),
  users: command('magenta', m => `${m.time}: currently connected users: \n${m.contents.split(' ').map(s => ' ' + s).join('\n')}`),
  default: command('red', m => `${m.time} ${m.username}: ${m.command}`)
}

export const execCMD = (mssg, logger) => {
  let c = CMDs[mssg.command]
  if (c != null) {
    logger(c.color, c.printMssg(mssg))
  } else {
    logger(CMDs.default.color, CMDs.default.printMssg(mssg))
  }
}
