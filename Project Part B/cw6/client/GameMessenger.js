"use strict";

/**
 * Constructs a new messenger to communicate with the matchmaker and the judge.
 *
 * @param url the url of the matchmaker.
 */
var GameMessenger = function () {};

GameMessenger.prototype = Object.create(Messenger.prototype);
GameMessenger.prototype.constructor = GameMessenger;

/**
 * Decodes a message and acts accordingly.
 *
 * @param message the message to react to.
 */
GameMessenger.prototype.handleMessage = function (message) {
  var decodedMessage = JSON.parse(message);
  if (debug) console.log("MESSAGE IN:" + decodedMessage.type);
  switch (decodedMessage.type) {
    case "REGISTERED":
      this.interpretRegistered(decodedMessage);
      break;
    case "READY":
      this.interpretReady(decodedMessage);
      break;
    case "PENDING_GAME":
      this.interpretPendingGame(decodedMessage);
      break;
    case "NOTIFY_TURN":
      this.interpretNotifyTurn(decodedMessage);
      break;
    case "NOTIFY":
      this.interpretNotify(decodedMessage);
      break;
    case "GAME_OVER":
      this.interpretGameOver(decodedMessage);
      break;
    case "GAMES":
      this.interpretGames(decodedMessage);
      break;
    case "CONNECTION":
      this.interpretConnection(decodedMessage);
    default:
      break;
  }
};


/**
 * Shows the view containing the game id.
 *
 * @param messageRegistered the REGISTERED message.
 */
GameMessenger.prototype.interpretRegistered = function (messageRegistered) {
  gameId = messageRegistered['game_id'];
  guiConnector.showGameIdPane(gameId);
  this.storedMessage = messageRegistered;
};

/**
 * Updates the UI with the information contained in the READY message.
 *
 * @param messageReady the READY message.
 */
GameMessenger.prototype.interpretReady = function (messageReady) {
  //numDetectives = messageReady['n_detectives'];
  //guiConnector.
  var rounds = messageReady['rounds'];
  var currentRound = messageReady['current_round'];
  guiConnector.setTicketView(rounds,currentRound);
  var locations = messageReady['locations'];
  guiConnector.setPlayerLocations(locations);
  var tickets = messageReady['tickets'];
  guiConnector.setPlayerTickets(tickets);
  guiConnector.setSetUpViewVisible(false);
  this.sendMessageToAI(messageReady);

};


GameMessenger.prototype.sendMessageToAI = function (message) {
    if(AIPlayers.length > 0 && aiMessenger.isConnected()){
        aiMessenger.sendMessage(message);
    }
};
/**
 * Shows the pending game view (Where you wait until your oppenent joins).
 *
 * @param messagePendingGame the PENDING_GAME message.
 */
GameMessenger.prototype.interpretPendingGame = function (messagePendingGame) {
  var missingPlayers = messagePendingGame['opponents'];
  guiConnector.showStringInSetUpView("Waiting for: " + missingPlayers.toString());
};

/**
 * Updates the UI accordingly using the information in the NOTIFY message.
 *
 * @param messageNotify the NOTIFY message.
 */
GameMessenger.prototype.interpretNotify = function (messageNotify) {
  var move = messageNotify['move'];
  var move_type = messageNotify['move_type'];

  if(move_type == "MovePass"){}
  else{
    if(move_type == "MoveDouble"){
      guiConnector.removeTicket(move.colour,move.move1.ticket);
      guiConnector.removeTicket(move.colour,move.move2.ticket);
      guiConnector.removeTicket(move.colour,"Double");
      if(move.colour == "Black"){
          guiConnector.updateTicketView(move.move1.ticket,move.move1.target);
          guiConnector.updateTicketView(move.move2.ticket,move.move2.target);
        }
    }
    else{
      guiConnector.animatePlayer(move.colour,move.target);
      guiConnector.removeTicket(move.colour,move.ticket);
      if(move.colour == "Black"){
          guiConnector.updateTicketView(move.ticket,move.target);
        }
    }

    this.sendMessageToAI(messageNotify);
  }



};

function isInArray(value, array) {
  return array.indexOf(value) > -1;
}
/**
 * Shows the SetUpView again and sets up for a new game.
 * If there are some AI players, it notifies the AI of the game over.
 *
 * @param messageGameOver the GAME_OVER message.
 */
GameMessenger.prototype.interpretGameOver = function (messageGameOver) {
  guiConnector.setGameOver(messageGameOver);
  this.sendMessageToAI(messageGameOver);


};

/**
 * Updates the list of available games to spectate.
 *
 * @param messageGames the GAMES message.
 */
GameMessenger.prototype.interpretGames = function (messageGames) {
  guiConnector.updateGamesList(messageGames['games']);
};

/**
 * Sends a spectate message to the judge whose connection information is
 * contained within the CONNECTION message.
 *
 * @param messageConnection the CONNECTION message.
 */
GameMessenger.prototype.interpretConnection = function (messageConnection) {
  var self = this;
  var gameId = guiConnector.getSelectedGame();
  this.changeConnection("ws://" + messageConnection.host + ":" + messageConnection.port);
  var notajoinMessage = {};
              notajoinMessage['type'] = "SPECTATE";
              notajoinMessage['game_id'] = gameId;
              this.sendMessage(notajoinMessage);
};


/**
 * Sends the notify turn message to the AI server if the current player is an AI player.
 * Updates the views accordingly.
 *
 * @param messageNotifyTurn the NOTIFY_TURN message.
 */
GameMessenger.prototype.interpretNotifyTurn = function (messageNotifyTurn) {
    var validmoves = messageNotifyTurn['valid_moves'];
    var indexZeroMove = validmoves[0];
    var submove = indexZeroMove['move'];
    var cplayer = submove['colour'];
  if(isInArray(cplayer,AIPlayers)){
    guiConnector.startTurn(messageNotifyTurn,true);
    this.sendMessageToAI(messageNotifyTurn);

  }else{
    guiConnector.startTurn(messageNotifyTurn,false);
  }
};

/**
 * Sends a JOIN message to the server whose connection details are contained in the REGISTERED message.
 *
 * @param message the REGISTERED message containing connection and game information.
 */
GameMessenger.prototype.sendJoin = function () {
  var self = this;
  var message = this.storedMessage;
  var callback = function () {
    var colours = message.colours;
    var aiColours = [];

    for (var i = 0; i < colours.length; i++) {
      if (AIPlayers.indexOf(colours[i]) != -1) {
        aiColours.push(colours[i]);
      } else {
        var joinMessage = {};
        joinMessage['type'] = "JOIN";
        joinMessage['colour'] =  colours[i];
        joinMessage['game_id'] = message['game_id'];
        self.sendMessage(joinMessage);
      }
    }
    if (aiColours.length > 0 && aiMessenger && aiMessenger.isConnected()) {
      message.colours = aiColours;
      if (aiMessenger) aiMessenger.sendMessage(message);
    }
  };
  var precallback = function () {
    if (self.isConnected()) {
      callback();
    }
  };
  this.changeConnection("ws://" + message.host + ":" + message.port, precallback);
};

