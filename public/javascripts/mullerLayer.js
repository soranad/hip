var startTimer = setInterval(function() {showTimer()},2000);
var showTimerTime = 0;
var count = 5;
var delayTime = 0;
var delayTime2 = 0;
var expDuration = 0;
var d = 0;
var score = 0;
var differentChoice ; 
var lenghtType ; 
var differentLenght ; 
var lineLength = "5%";

function showTimer(){
    clearInterval(startTimer);
    showTimerTime =   setInterval(calculateTimeLeft,1000);
    delayTime = setInterval(function(){delay()},5000);
}

function calculateTimeLeft()
{
    count=count-1;
    document.getElementById("word").innerHTML= " โจทย์จะเริ่มในอีก " + (count-1) +" วินาที" ;
}
function delay(){
    clearInterval(delayTime);
    clearInterval(showTimerTime);
    document.getElementById("word").innerHTML="";
    delayTime2 = setInterval(function(){showQuestion()},1000);
}
function showQuestion(){
    clearInterval(delayTime2);
    d = new Date();
    expDuration = d.getTime();
    document.getElementById("mc").style.visibility = "visible";  
    document.getElementById("ic").style.visibility = "visible";  
}

function done(name){
    d = new Date();
    expDuration = (d.getTime()-expDuration)/1000;

    if(name == "t"){
        score = score+1;
    }
    document.getElementById("time").value = expDuration;
    document.getElementById("score").value = score;

}
function setUp(differLenght,differChoice){
    differentChoice = document.getElementById("differentChoice").innerHTML;
    lenghtType = document.getElementById("lenghtType").innerHTML.toString();
    differentLenght = document.getElementById("differentLenght").innerHTML;
   
    if(lenghtType == 1){
        lineLength = 30;
    }
    else if(lenghtType == 2){
        lineLength = 50;        
    }
    else if(lenghtType == 3){
        lineLength = 80;
    }
    var line = document.getElementsByClassName('arrowButton');
    for(var k =0;k<line.length;k++){
        line[k].style['width'] = lineLength + "%";
    }
    document.getElementById(differChoice-1).style.width = lineLength - differLenght+"%";

}
