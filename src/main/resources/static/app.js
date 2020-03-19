var stompClient = null;

function setConnected(connected) {
  $('#connect').prop('disabled', connected);
  $('#disconnect').prop('disabled', !connected);
  if (connected) {
    $('#conversation').show();
  } else {
    $('#conversation').hide();
  }
  $('#greetings').html('');
}

function connect() {
  var socket = new SockJS('/kwartet');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function(frame) {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', function(message) {
      showMessage(JSON.parse(message.body).content);
    });
    stompClient.subscribe('/user/topic/messages', function(message) {
      showUserMessage(JSON.parse(message.body).content);
    });
    stompClient.subscribe('/user/topic/player', function(player) {
      showPlayer(JSON.parse(player.body));
    });
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log('Disconnected');
}

function joinGame() {
  stompClient.send('/app/joinGame', {}, $('#name').val());
}

function startGame() {
  stompClient.send('/app/startGame', {}, 'START');
}

function showMessage(content) {
  $('#messages').append('<tr><td>' + content + '</td></tr>');
  if (content.includes("Spel gestart")) {
    $('#joinStartForm').hide();
    $('#player-hand').show();
  }
}

function showUserMessage(content) {
  $('#messages').append('<tr><td><b>' + content + '</b></td></tr>');
}

function showPlayer(player) {
  for (i in player.cards) {
    card = player.cards[i];
    $('#player-hand').append('<tr><td>' + card.times + ' x ' + card.table + ' = ' + card.outcome + '</td></tr>');
  }
}

$(function() {
  $('form').on('submit', function(e) {
    e.preventDefault();
  });
  $('#joinGame').click(function() {
    joinGame();
  });
  $('#startGame').click(function() {
    startGame();
  });
  connect();
});

$(window).on('unload', function() {
  disconnect();
  return 'bye';
});