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

function onMessage(message, isPrivate) {
  $('#messages').
      append('<tr><td>' + decorate(message.content, isPrivate) + '</td></tr>');
  if (message.type === 'START') {
    $('#join').prop('disabled', true);
    $('#start-panel').hide();
  }
  if (message.type === 'YOUR_TURN') {
    $('#turn-panel').show();
    var $opponents = $('#opponent');
    $.each(playerNames, function() {
      if (!isCurrentPlayer(this)) {
        $opponents.append($('<option />').val(this).text(this));
      }
    });
  }
}

function onPlayer() {
  $('#cards-panel').show();
  $('#name').val(thisPlayer.name);
  for (i in thisPlayer.cards) {
    let card = thisPlayer.cards[i];
    $('#cards').
        append('<tr><td>' + card.times + ' x ' + card.table + ' = ' +
            card.outcome + '</td></tr>');
  }
}

function onPlayerNames() {
  $('#players-panel').show();
  let $players = $('#players');
  $players.html('');
  for (i in playerNames) {
    let name = playerNames[i];
    $players.append(
        '<tr><td>' + decorate(name, isCurrentPlayer(name)) + '</td></tr>');
  }
}

function isCurrentPlayer(name) {
  return thisPlayer != null && name === thisPlayer.name;
}

function decorate(content, mustMark) {
  if (mustMark) {
    content = '<b>' + content + '</b>';
  }
  return content;
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
      onMessage(JSON.parse(message.body), false);
    });
    stompClient.subscribe('/user/topic/message', function(message) {
      onMessage(JSON.parse(message.body), true);
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
