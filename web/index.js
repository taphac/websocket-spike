var ws = new WebSocket("ws://10.194.195.2:8080/toWebSocket");

var canvas = document.getElementById("canvas");
var ctx = canvas.getContext('2d');

ws.onopen = function() {
    //alert("Opened!");
    //ws.send("Hello Server");
};

ws.onmessage = function (evt) {
    //alert("Message: " + evt.data);
    //console.log(evt.data.length);

    var img = new Image();
    img.onload = function() {
    	//console.log("loaded")
    	ctx.drawImage(img, 0, 0, 200, 200, 0, 0, 800, 800);
    }

    img.src = "data:image/jpg;base64," + event.data;
};

ws.onclose = function() {
    //alert("Closed!");
};

ws.onerror = function(err) {
    //alert("Error: " + err);
};