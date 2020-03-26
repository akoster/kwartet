var stompClient = null;
var thisPlayer = null;
var scoreBoard = null;

function join() {
  stompClient.send('/app/join', {}, $('#name').val());
}

function start() {
  stompClient.send('/app/start');
}

function ask() {
  stompClient.send('/app/ask', {}, JSON.stringify(
      {'card': $('#card').val(), 'opponent': $('#opponent').val()}));
  $('#turn-panel').hide();
}

function onMessage(message) {
  $('#messages').
      prepend('<tr><td>' + message.content + '</td></tr>');
  if (message.type === 'START') {
    $('#join').prop('disabled', true);
    $('#start-panel').hide();
    populateOpponentSelect();
  }
  if (message.type === 'YOUR_TURN') {
    $('#turn-panel').show();
  }
  if (message.type === 'RESTART') {
    restart();
  }
}

function onPlayer() {
  $('#cards-panel').show();
  $('#name').val(thisPlayer.name);
  let $cards = $('#cards');
  $cards.html('');
  for (i in thisPlayer.cards) {
    let card = thisPlayer.cards[i];
    $cards.
        append('<tr><td>' + card.description + '</td></tr>');
  }
}

function onScoreBoard() {
  $('#players-panel').show();
  let $players = $('#players');
  $players.html('');
  for (i in scoreBoard.scores) {
    let score = scoreBoard.scores[i];
    let scoreDisplay = score.name + ' : ' + score.score;
    if (isCurrentPlayer(score.name)) {
      scoreDisplay = '<b>' + scoreDisplay + '</b>';
    }
    $players.append('<tr><td>' + scoreDisplay + '</td></tr>');
  }
}

function populateOpponentSelect() {
  var $opponents = $('#opponent');
  $opponents.html('');
  $.each(scoreBoard.scores, function() {
    if (!isCurrentPlayer(this.name)) {
      $opponents.append($('<option />').val(this.name).text(this.name));
    }
  });
}

function isCurrentPlayer(name) {
  return thisPlayer != null && name == thisPlayer.name;
}

$(function() {
  connect();
  $('#messages').html('');
  $('form').on('submit', function(e) {
    e.preventDefault();
  });
  $('#join').click(function() {
    join();
  });
  $('#start').click(function() {
    start();
  });
  $('#ask').click(function() {
    ask();
  });
});

function connect() {
  var socket = new SockJS('/kwartet');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/message', function(message) {
      onMessage(JSON.parse(message.body));
    });
    stompClient.subscribe('/user/topic/message', function(message) {
      onMessage(JSON.parse(message.body));
    });
    stompClient.subscribe('/user/topic/player', function(message) {
      thisPlayer = JSON.parse(message.body);
      onPlayer();
    });
    stompClient.subscribe('/user/topic/scoreboard', function(message) {
      scoreBoard = JSON.parse(message.body);
      onScoreBoard();
    });
  });
}

function restart() {
  thisPlayer = null;
  scoreBoard = null;
  $('#start-panel').show();
  $('#turn-panel').hide();
  $('#cards-panel').hide();
  $('#players-panel').hide();
  $('#join').prop('disabled', false);
}

window.onbeforeunload = function() {
  if (stompClient !== null) {
    stompClient.send('/app/leave');
    stompClient.disconnect();
  }
  console.log('Disconnected');
  return 'bye';
};


