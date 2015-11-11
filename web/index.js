var canvas = document.getElementById("canvas");
var ctx = canvas.getContext('2d');

var img;
var audioContext;
var bufferCount = 10

var sampleBuffer = [];
var imageBuffer = [];

var playing = false;
var videoPlaying = false;

if (canvas.ontouchend == undefined) {
    canvas.onmouseup = start;     
} else {
    canvas.ontouchend = start;
} 

function start() {
    var ws = new WebSocket("ws://10.194.195.2:8080/toWebSocket");
    ws.binaryType = "arraybuffer";

    audioContext  = new webkitAudioContext();

    var src = audioContext.createBufferSource()

    var audioBuffer = audioContext.createBuffer(1, 1024 * bufferCount, 44100 );
      
    src.buffer = audioBuffer;
    src.connect( audioContext.destination );
    src.start( 0 );

    ws.onopen = function() {
    //ws.send("Hello Server");
    };

    ws.onclose = function() {
    //alert("Closed!");
    };

    ws.onerror = function(err) {
        //alert("Error: " + err);
    };

    ws.onmessage = function (event) {        
        
        if (event.data.byteLength < 10000) {
            img = new Image();
            
            img.onload = function() {

                imageBuffer.push(img);

                if (videoPlaying == false && imageBuffer.length > 20) {
                    videoPlaying = true;
                }

            }

            var urlCreator = window.URL || window.webkitURL;

            var blob = new Blob([event.data]);
            blob.type = "image/jpeg"
            var imageUrl = urlCreator.createObjectURL(blob);
            img.src = imageUrl;

        } else {
        
            var buffer = new Float32Array(event.data, 0, 1024 * bufferCount);

            sampleBuffer.push(buffer);

            if (playing == false) {
                if (sampleBuffer.length > 5) {
                    playing = true;
                }
            }

            if (sampleBuffer.length < 2) {
                playing = false;
            }

        }

    };

   
    setInterval(playAudio, 225);
    setInterval(playVideo, 30);
}


function playVideo() {

   if (videoPlaying && imageBuffer.length > 0) {
        if (imageBuffer.length > 21) {
            imageBuffer.shift();   
        }
        ctx.drawImage(imageBuffer.shift(), 0, 0, 200, 200, 0, 0, 800, 800);
   }

}

function playAudio() {
    if (playing == true && sampleBuffer.length > 0) {
        var src = audioContext.createBufferSource()
        var audioBuffer = audioContext.createBuffer(1, 1024 * bufferCount, 44100 );
      
        audioBuffer.getChannelData(0).set(sampleBuffer.shift());
        src.buffer = audioBuffer;
        src.connect( audioContext.destination );
        src.start(0);
    }
}


