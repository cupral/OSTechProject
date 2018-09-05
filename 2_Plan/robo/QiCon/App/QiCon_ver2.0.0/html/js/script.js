
var session = new QiSession();
var behaviorPath = "";
var AppNameJP = "";
var AppNameUS = "";
var container = "";
var list = "";
var name = "";

$(function(){
  $("img").on("contextmenu",function(){
    return false;
  });
});


function ChangeTab(tabname) {
  // 全部消す
  document.getElementById('tab1').style.display = 'none';
  document.getElementById('tab2').style.display = 'none';
  // 指定箇所のみ表示
  if(tabname) {
    document.getElementById(tabname).style.display = 'block';
  }
}

//ロボアプリ一覧を取得
function displayResult() {
  session.service("PackageManager").done(function(PackageManager) {
    PackageManager.packages().done(function(appPackage) {
      container = '';
      list = document.getElementById("list");
      for (var i = 0; i < appPackage.length; i++) {
        var allPackage = appPackage[i];
        try {
          var behaviors = allPackage.behaviors;
          var behavior = behaviors[0];
          var path = behavior.path;
          var uuid = allPackage.uuid;
          var behaviorPath = uuid + "/" + path;
        } catch (e) {
          continue;
        }
        var langToNames = allPackage.langToName;
        var AppNameJP = langToNames.ja_JP;
        var AppNameUS = langToNames.en_US;
        if (AppNameJP == undefined) {
          if (AppNameUS == undefined) {
            continue;
          }
          if (~AppNameUS.indexOf('dialog_') || ~AppNameUS.indexOf('Language') || ~AppNameUS.indexOf('forBiz') || ~AppNameUS.indexOf('Animation')) {
            continue;
          }
          container += '<button class="allAppButton" type="submit" value="' + behaviorPath + '" onclick="runBehavior(value)">' + AppNameUS + '</button>';
        } else {
          if (~AppNameJP.indexOf('dialog_') || ~AppNameJP.indexOf('Language') || ~AppNameJP.indexOf('forBiz') || ~AppNameJP.indexOf('Animation')) {
            continue;
          }
          container += '<button class="allAppButton" type="submit" value="' + behaviorPath + '" onclick="runBehavior(value)">' + AppNameJP + '</button>';
        }
      }
      //console.log(container);
      list.innerHTML = container;
    });
  });

}

//アプリケーションを起動
function runBehavior(value) {
  soundButtonTouch();
  session.service("ALBehaviorManager").done(function(BeManager) {
    BeManager.runBehavior(value);
  });
}



//再起動
function robotReboot() {
  soundButtonTouch();
  session.service("ALMotion").done(function(motion) {
    motion.rest();
  });
  session.service("ALSystem").done(function(system) {
    system.reboot();
  });
}

//シャットダウン
function robotShutdown() {
  soundButtonTouch();
  session.service("ALMotion").done(function(motion) {
    motion.rest();
  });
  session.service("ALSystem").done(function(system) {
    system.shutdown();
  });
}

//現在接続しているロボットの名前を取得
function getRobotName() {
  session.service("ALSystem").done(function(system) {
    system.robotName().done(function(Name) {
      robotName = document.getElementById("robotName");
      robotName.innerHTML = "QiCon(" + Name + ".local/)";
    });
  });
}

//スピーカーの音量を上げる
function roboVolumeUp() {
  soundVolTouchUp();
  session.service("ALAudioDevice").done(function(audio) {
    audio.getOutputVolume().done(function(roboVolume) {
      roboVolume += 5;
      audio.setOutputVolume(roboVolume);
      nowRoboVolume = document.getElementById("nowRoboVolume");
      nowRoboVolume.innerHTML = roboVolume;
    });
  });
}

//スピーカーの音量を下げる
function roboVolumeDown() {
  soundVolTouchDown();
  session.service("ALAudioDevice").done(function(audio) {
    audio.getOutputVolume().done(function(roboVolume) {
      roboVolume -= 5;
      audio.setOutputVolume(roboVolume);
      nowRoboVolume = document.getElementById("nowRoboVolume");
      nowRoboVolume.innerHTML = roboVolume;
    });
  });
}

//現在のスピーカー音量を取得
function getRoboVolume() {
  session.service("ALAudioDevice").done(function(audio) {
    audio.getOutputVolume().done(function(roboVolume) {
      nowRoboVolume = document.getElementById("nowRoboVolume");
      nowRoboVolume.innerHTML = roboVolume;
    });
  });
}

//全てのアプリを停止させる
function stopAllBehaviors() {
  soundButtonTouch();
  hideWebView();
  session.service("ALBehaviorManager").done(function(BeManager) {
    BeManager.stopAllBehaviors();
  });
}

//wakeUpモード
function wakeUpMode() {
  session.service("ALMotion").done(function(motion) {
    motion.wakeUp();
  });
  startBasicAwareness();
}

//StartBA
function startBasicAwareness() {
  session.service("ALBasicAwareness").done(function(BasicAwareness) {
    BasicAwareness.startAwareness();
  });
}

//Restモード
function restMode() {
  session.service("ALMotion").done(function(motion) {
    motion.rest();
  });
  stopBasicAwareness();
  stopAllBehaviors();
}

//StopBA
function stopBasicAwareness() {
  session.service("ALBasicAwareness").done(function(BasicAwareness) {
    BasicAwareness.stopAwareness();
  });
}

//ディスプレイを初期化
function hideWebView() {
  try {
    session.service("ALTabletService").done(function(TabletService) {
      TabletService.hideWebview();
    });
  } catch (e) {
    console.log("NotFound:ALTabletService");
  }
}

//音楽再生
function soundButtonTouch() {
  document.getElementById("buttonTouch-sound").play();
}

function soundVolTouchUp() {
  document.getElementById("volTouchUp-sound").play();
}

function soundVolTouchDown() {
  document.getElementById("volTouchDown-sound").play();
}

//テキストエリア
function textareaToSpeech() {
  var text = document.forms.id_form1.input_text.value;
  session.service("ALTextToSpeech").done(function(tts) {
    tts.say(text);
  });
}

//バッテリー情報取得
var img = new Array();
  img[0] = new Image();
  img[0].src = "img/battery0-48.png";
  img[1] = new Image();
  img[1].src = "img/battery1-48.png";
  img[2] = new Image();
  img[2].src = "img/battery2-48.png";
  img[3] = new Image();
  img[3].src = "img/battery3-48.png";
  img[4] = new Image();
  img[4].src = "img/battery4-48.png";
  img[5] = new Image();
  img[5].src = "img/battery5-48.png";
var batteryCnt = 0;

function getBatteryValue() {
  session.service("ALBattery").done(function(battery) {
    battery.getBatteryCharge().done(function(batteryValue) {
      var batteryVal = document.getElementById("id_battery");
      if (16 >= batteryValue) {
        batteryCnt = 0;
      } else if (32 >= batteryValue) {
        batteryCnt = 1;
      } else if (48 >= batteryValue) {
        batteryCnt = 2;
      } else if (64 >= batteryValue) {
        batteryCnt = 3;
      } else if (80 >= batteryValue) {
        batteryCnt = 4;
      } else if (100 >= batteryValue) {
        batteryCnt = 5;
      } else {
        batteryCnt = 0;
      }
      document.getElementById("id_batteryImage").src = img[batteryCnt].src;
    });
  });
}

// モ ー シ ョ ン タ ブ 機 能

// 初期モーション
function standInit(){
  session.service("ALRobotPosture").done(function(posture){
    posture.goToPosture("StandInit", 1.0);
  });
}

// ストップボタン
function stopMove(){
  session.service("ALMotion").done(function(motion){
    clearInterval($intervalID);
    motion.killMove();
    standInit();
  });
}

// 前進
function moveToFunc(x,y,theta){
  session.service("ALMotion").done(function(motion){
    motion.moveTo(x, y, theta);
  });
}

function runPause(pauseNum){
  var pauseBoxNum = pauseNum;
  var pausePath = "";
  session.service("ALBehaviorManager").done(function(BeManager){
    if (pauseNum==1) {
      pausePath = "handUpRight";
    }else if (pauseNum==2) {
      pausePath = "handUpLeft";
    }else if (pauseNum==3) {
      pausePath = "shakeHandRight";
    }else if (pauseNum==4) {
      pausePath = "shakeHandLeft";
    }else if (pauseNum==5) {
      pausePath = "shakeHands";
    }else if (pauseNum==6) {
      pausePath = "shakeTheHip";
    }else if (pauseNum==7) {
      pausePath = "runHere";
    }else if (pauseNum==8) {
      pausePath = "stoop";
    }else{
      alert("runPauseError");
    }
    BeManager.runBehavior("qicon_ver3/" + pausePath);
  });
}

// 押しっぱなし対応


// window.onload =function(){
//   var ua =navigator.userAgent;
//   if(ua.indexOf('iPhone') > -1 || ua.indexOf('iPad') > -1 || ua.indexOf('iPod')  > -1){
//     var start = "ontouchstart";
//     var move  = "ontouchmove";
//     var end   = "ontouchend";
//   }else{
//     var start = "onmousedown";
//     var move  = "onmousemove";
//     var end   = "onmouseup";
//   }
//
//   var $button_move = document.getElementById("button_move");
//   $button_move.ontouchstart = function($event){
//     $intervalID =setInterval(
//       function(){
//         session.service("ALMotion").done(function(motion){
//             motion.moveTo(0.1, 0, 0);
//         });
//       },1000
//     );
//   }
//   $button_move.ontouchend = function ($event){
//     clearInterval($intervalID);
//     stopMove();
//   };
// }

var $buttonElement = "";
var x = 0;
var y = 0;
var theta = 0;
function touchMoveToButton(buttonNum){
  if (buttonNum == 0) {
    $buttonElement = document.getElementById("move_ahead");
    x = 1;y = 0;theta = 0;
  }else if (buttonNum == 1) {
    $buttonElement = document.getElementById("move_back");
    x = -1;y = 0;theta = 0;
  }else if (buttonNum == 2) {
    $buttonElement = document.getElementById("move_goRight");
    x = 0;y = -0.5;theta = 0;
  }else if (buttonNum == 3) {
    $buttonElement = document.getElementById("move_goLeft");
    x = 0;y = 0.5;theta = 0;
  }else if (buttonNum == 4) {
    $buttonElement = document.getElementById("move_turnRight");
    x = 0;y = 0;theta = -1;
  }else if (buttonNum == 5) {
    $buttonElement = document.getElementById("move_turnLeft");
    x = 0;y = 0;theta = 1;
  }else{
    console.log("Error");
  }
  $intervalID = setInterval(
    function(){
      session.service("ALMotion").done(function(motion){
        motion.moveTo(x,y,theta);
      });
    },1000
  );

  $buttonElement.ontouchend = function ($event){
    console.log("end");
    clearInterval($intervalID);
    stopMove();
  };
}

displayResult();
getBatteryValue();
getRoboVolume();
getRobotName();
