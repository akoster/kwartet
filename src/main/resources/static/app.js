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
    stompClient.subscribe('/topic', function(greeting) {
      showMessage(JSON.parse(greeting.body).content);
    });
    stompClient.subscribe('/user/topic', function(greeting) {
      showMessage(JSON.parse(greeting.body).content);
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

function showMessage(message) {
  $('#messages').append('<tr><td>' + message + '</td></tr>');
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