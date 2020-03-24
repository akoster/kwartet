var stompClient = null;
var thisPlayer = null;
var playerNames = null;

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
      append('<tr><td>' + message.content + '</td></tr>');
  if (message.type === 'START') {
    $('#join').prop('disabled', true);
    $('#start-panel').hide();
    populateOpponentSelect();
  }
  if (message.type === 'YOUR_TURN') {
    $('#turn-panel').show();
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

function onPlayerNames() {
  $('#players-panel').show();
  let $players = $('#players');
  $players.html('');
  for (i in playerNames) {
    let name = playerNames[i];
    if (isCurrentPlayer(name)) {
      name = '<b>' + name + '</b>';
    }
    $players.append('<tr><td>' + name + '</td></tr>');
  }
}

function populateOpponentSelect() {
  var $opponents = $('#opponent');
  $opponents.html('');
  $.each(playerNames, function() {
    if (!isCurrentPlayer(this)) {
      $opponents.append($('<option />').val(this).text(this));
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
    stompClient.subscribe('/user/topic/playernames', function(message) {
      playerNames = JSON.parse(message.body).names;
      onPlayerNames();
    });
  });
}

window.onbeforeunload = function() {
  if (stompClient !== null) {
    stompClient.send('/app/leave');
    stompClient.disconnect();
  }
  console.log('Disconnected');
  return 'bye';
};
