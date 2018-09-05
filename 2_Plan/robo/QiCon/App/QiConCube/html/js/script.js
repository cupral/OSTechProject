
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




// タブ設定
function ChangeTab(tabname) {
  // 全部消す
  document.getElementById('tab1').style.display = 'none';
  document.getElementById('tab2').style.display = 'none';
  document.getElementById('tab3').style.display = 'none';
  document.getElementById('tab4').style.display = 'none';

  // 指定箇所のみ表示
  if(tabname) {
    document.getElementById(tabname).style.display = 'block';
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









  // _________________________________________________________________________
// ラ ン チ ャ ー タ ブ


function getFormValue(){
  var speedValue;
  var voiceSharpingValue;
  var speed = document.forms.form1.speed.value;
  var voiceSharping = document.forms.form1.voiceSharping.value;
  if (speed=="遅め") {
    speedValue = "\\rspd=80\\";
  }else if (speed=="普通") {
    speedValue = "\\rspd=100\\";
  }else if (speed=="早め") {
    speedValue = "\\rspd=120\\";
  }else if (speed=="高速") {
    speedValue = "\\rspd=150\\";
  }else{
    console.log("getFormValueError");
  }
  if (voiceSharping=="低め") {
    voiceSharpingValue = "\\vct=100\\";
  }else if (voiceSharping=="普通") {
    voiceSharpingValue = "\\vct=120\\";
  }else if (voiceSharping=="高め") {
    voiceSharpingValue = "\\vct=140\\";
  }else{
    console.log("getFormValueError");
  }
  var speedSharping = speedValue + voiceSharpingValue;
  robotSay(speedSharping);
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

function alifeLauncher(onoff){
  if (onoff==0) {
    alifeOn();
  }else if (onoff==1) {
    alifeOff();
  }else {
    console.log("alifeLauncherError");
  }
}
function alifeChecker(){
  alert("touch");
}


function alifeOn(){
  session.service("ALAutonomousLife").done(function(alife){
    alife.setState("solitary");
  });
}

function alifeOff(){
  session.service("ALAutonomousLife").done(function(alife){
    alife.setState("safeguard");
  });
  stopAllBehaviors();
  hideWebView();
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
}

//Restモード
function restMode() {
  session.service("ALMotion").done(function(motion) {
    motion.rest();
  });
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
// function getRobotName() {
//   session.service("ALSystem").done(function(system) {
//     system.robotName().done(function(Name) {
//       robotName = document.getElementById("robotName");
//       robotName.innerHTML = "QiCon(" + Name + ".local/)";
//     });
//   });
// }

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

// function getBatteryValue() {
//   session.service("ALBattery").done(function(battery) {
//     battery.getBatteryCharge().done(function(batteryValue) {
//       var batteryVal = document.getElementById("id_battery");
//       if (16 >= batteryValue) {
//         batteryCnt = 0;
//       } else if (32 >= batteryValue) {
//         batteryCnt = 1;
//       } else if (48 >= batteryValue) {
//         batteryCnt = 2;
//       } else if (64 >= batteryValue) {
//         batteryCnt = 3;
//       } else if (80 >= batteryValue) {
//         batteryCnt = 4;
//       } else if (100 >= batteryValue) {
//         batteryCnt = 5;
//       } else {
//         batteryCnt = 0;
//       }
//       document.getElementById("id_batteryImage").src = img[batteryCnt].src;
//     });
//   });
// }












// _________________________________________________________________________
// モーションタブ

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
    BeManager.runBehavior("qiconcube/motion/" + pausePath);
  });
}






















// _________________________________________________________________________
// ト ー ク 集 タ ブ

function robotSay(p){
  var text = p;
  session.service("ALAnimatedSpeech").done(function (anispe){
    anispe.say(text);
  });
}

function talkFree(talkNum){
  try {
    var text2 = document.forms.id_form2.input_text2.value;
    var text3 = document.forms.id_form2.input_text3.value;
    var text4 = document.forms.id_form2.input_text4.value;
    var text5 = document.forms.id_form2.input_text5.value;
    var text6 = document.forms.id_form2.input_text6.value;
    var text7 = document.forms.id_form2.input_text7.value;
    var text8 = document.forms.id_form2.input_text8.value;
    var text9 = document.forms.id_form2.input_text9.value;
    var text10 = document.forms.id_form2.input_text10.value;
    var text11 = document.forms.id_form2.input_text11.value;
  } catch (e) {
    console.log("talkFree取得漏れ");
  } finally {
    console.log("talkfinally");
  }

  session.service("ALMemory").done(function (memory){
    console.log("ALMemory成功");
    memory.insertData("roboMemory/text2", text2);
    memory.insertData("roboMemory/text3", text3);
    memory.insertData("roboMemory/text4", text4);
    memory.insertData("roboMemory/text5", text5);
    memory.insertData("roboMemory/text6", text6);
    memory.insertData("roboMemory/text7", text7);
    memory.insertData("roboMemory/text8", text8);
    memory.insertData("roboMemory/text9", text9);
    memory.insertData("roboMemory/text10", text10);
    memory.insertData("roboMemory/text11", text11);
  });

  var readText = "";
  if (talkNum==2) {
    readText = text2;
  }else if (talkNum==3) {
    readText = text3;
  }else if (talkNum==4) {
    readText = text4;
  }else if (talkNum==5) {
    readText = text5;
  }else if (talkNum==6) {
    readText = text6;
  }else if (talkNum==7) {
    readText = text7;
  }else if (talkNum==8) {
    readText = text8;
  }else if (talkNum==9) {
    readText = text9;
  }else if (talkNum==10) {
    readText = text10;
  }else if (talkNum==11) {
    readText = text11;
  }else{
    console.log("talkNumError");
  }

  session.service("ALAnimatedSpeech").done(function (anispe){
    anispe.say(readText);
  });
}



function talkMemory(){
  session.service("ALMemory").done(function(memory){
    memory.getData("roboMemory/text2").done(function(text2memory){
      var text2value = document.getElementById("input_text2");
      text2value.value = text2memory;
    });
    memory.getData("roboMemory/text3").done(function(text3memory){
      var text3value = document.getElementById("input_text3");
      text3value.value = text3memory;
    });
    memory.getData("roboMemory/text4").done(function(text4memory){
      var text4value = document.getElementById("input_text4");
      text4value.value = text4memory;
    });
    memory.getData("roboMemory/text5").done(function(text5memory){
      var text5value = document.getElementById("input_text5");
      text5value.value = text5memory;
    });
    memory.getData("roboMemory/text6").done(function(text6memory){
      var text6value = document.getElementById("input_text6");
      text6value.value = text6memory;
    });
    memory.getData("roboMemory/text7").done(function(text7memory){
      var text7value = document.getElementById("input_text7");
      text7value.value = text7memory;
    });
    memory.getData("roboMemory/text8").done(function(text8memory){
      var text8value = document.getElementById("input_text8");
      text8value.value = text8memory;
    });
    memory.getData("roboMemory/text9").done(function(text9memory){
      var text9value = document.getElementById("input_text9");
      text9value.value = text9memory;
    });
    memory.getData("roboMemory/text10").done(function(text10memory){
      var text10value = document.getElementById("input_text10");
      text10value.value = text10memory;
    });
    memory.getData("roboMemory/text11").done(function(text11memory){
      var text11value = document.getElementById("input_text11");
      text11value.value = text11memory;
    });
  });
}


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


// カーブボタン対応
 function touchCurvesButton(curvesNum){
   var behaviorPath;
   if(curvesNum==0){
     $curvesElement = document.getElementById("move_curvesLeft");
     behaviorPath = "leftCurves";
   }else if (curvesNum==1) {
     $curvesElement = document.getElementById("move_curvesRight");
     behaviorPath = "RightCurves";
   }else {
     console.log("touchCurvesButtonError");
   }

   $intervalID = setInterval(
     function(){
       session.service("ALBehaviorManager").done(function(BeManager){
        BeManager.runBehavior("qiconcube/" + behaviorPath);
       });
     },1000
   );

   $curvesElement.ontouchend = function ($event){
     console.log("end");
     clearInterval($intervalID);
     stopMove();
   };
 }

 // =======音声認識==============================================================
function memory(method, value1,value2){
 session.service("ALMemory").done(function(mem){
   if (method=="raiseEvent") {
     mem.raiseEvent(value1,value2);
   }else if (method=="insertData") {
     mem.insertData(value1,value2);
   }else {
     console.log("memoryError");
     alert("memoryError");
   }
 });
}

function tts(method, value1,value2){
  session.service("ALTextToSpeech").done(function(ttsFunc){
    if (method=="say") {
      ttsFunc.say(value1);
    }else if (method=="setLanguage") {
      ttsFunc.setLanguage(value1);
    }else {
      console.log("ttsError");
      alert("ttsError");
    }
  });
}

function asr(method, value1,value2){
  session.service("ALSpeechRecognition").done(function(asrFunc){
    if (method=="setLanguage") {
      asrFunc.setLanguage(value1);
    }else {
      console.log("asrError");
      alert("asrError");
    }
  });
}


//setLanguage
function setLanguage(num){
  if (num==0) {
    tts("setLanguage","Japanese");
    asr("setLanguage","Japanese");
    $("#nowLanguage").text("現在の言語：日本語");
    window.setTimeout(function(){tts("say","日本語で喋ります。")}, 2000);
  }else if (num==1) {
    tts("setLanguage","English");
    asr("setLanguage","English");
    $("#nowLanguage").text("現在の言語：英語");
    window.setTimeout(function(){tts("say","speak English")}, 2000);
  }else if (num==2) {
    tts("setLanguage","Chinese");
    asr("setLanguage","Chinese");
    $("#nowLanguage").text("現在の言語：中国語");
    window.setTimeout(function(){tts("say","我们中国人说话。")}, 2000);
  }else {
    console.log("setLanguageError");
    alert("setLanguageError");
  }
}


function recoStart(){
  session.service("ALBehaviorManager").done(function(beMana){
    beMana.runBehavior("qiconcube/speechReco");
  });
  window.setTimeout(function(){
    var recoWord = $('#recoFormId [name=recoWord]').val();
    var threshold = $('#recoFormId [name=threshold]').val();
    threshold = parseInt(threshold);
    var resWord = $('#resFormId [name=resWord]').val();
    memory("insertData","RoboMemory/resWord", resWord);
    memory("raiseEvent","RoboEvent/recoWord",recoWord);
    memory("raiseEvent","RoboEvent/threshold",threshold);
    window.setTimeout( function(){memory("raiseEvent","RoboEvent/recoStart",1)}, 1000 );
  }, 1000);
}
function recoStop(){
  memory("raiseEvent","RoboEvent/recoStop",1);
  window.setTimeout( function(){tts("say","stop")}, 1500 );
}
function getLanguage(){
  session.service("ALTextToSpeech").done(function(tts){
    tts.getLanguage().done(function(lang){
      if (lang=="Japanese") {
        var language = "日本語";
      }else if (lang=="English") {
        var language = "英語";
      }else if (lang=="Chinese") {
        var language = "中国語";
      }else {
        console.log("getLanguageError");
      }
      $("#nowLanguage").text("現在の言語：" + language);
    });
  });
}



 // =============================================================================


displayResult();
// getBatteryValue();
getRoboVolume();
getRobotName();
getLanguage();
