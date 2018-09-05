//Pepper機能
// =============================================================================
//      共通設定 (IP,アプリIDなど)
// =============================================================================
// アプリケーションID
var applicationID = "disopa_reception";
// ロボットの名前
var robotName = "nao";

// =============================================================================
//      共通関数 Pepper
// =============================================================================
// Pepperの機能を読み込む(NAOqi)
var session = new QiSession();

  // ALTextToSpeech
  // Pepperが喋るためのAPI
  function tts(method, value1, value2) {
    session.service("ALTextToSpeech").done(function(ttsFunc) {
      if (method = "say") {
        ttsFunc.say(value1);
      } else {
        console.log("TextToSpeechError");
      }
    });
  }
  // stopAllは使用時にラグがあると問題なので別関数として置いて即座に使えるようにしておく
  function ttsStopAll() {
    session.service("ALTextToSpeech").done(function(ttsStop) {
      ttsStop.stopAll();
    });
  }

  //ALAnimatedSpeech
  // 自動モーションを行いながら喋らせたい場合はAnimatedSpeechのsayを使用
  function animSpeech(method, value1, value2) {
    session.service("ALAnimatedSpeech").done(function(animSpeechFunc) {
      if (method = "say") {
        animSpeechFunc.say(value1);
      } else if (method = "stopAll") {
        animSpeechFunc.stopAll();
      } else {
        console.log("AnimatedSpeech");
      }
    });
  }

  // ALMemory
  // Pepper内にあるMemoryに保持しておきたいデータを送信、もしくはタブレット側の操作をイベントとして通知
  // ※Pepperの電源を消すとMemoryにあるデータは初期化される

  function memory(method, value1, value2) {
    session.service("ALMemory").done(function(memoryFunc) {
      if (method = "raiseEvent") {
        memoryFunc.raiseEvent(value1, value2);
      } else if (method = "insertData") {
        memoryFunc.insertData(value1, value2);
      } else {
        console.log("memoryError");
      }
    });
  }

  // ALBehaviorManager
  // Pepper内にあるbehaviorをコントロール
  function beManager(method, value1, value2) {
    session.service("ALBehaviorManager").done(function(beMaFunc) {
      if (method == "runBehavior") {
        beMaFunc.runBehavior(value1);
      } else {
        console.log("beManagerError");
      }
    });
  }

  // ALAudioPlayer
  // Pepperのスピーカーから出力する音を設定
  // 引数にはsoundディレクトリに保存されてある音源を拡張子込みで
  function player(method, value1, value2) {
    session.service("ALAudioPlayer").done(function(playerFunc) {
      if (method == "playFile") {
        playerFunc.playFile("/home/nao/.local/share/PackageManager/apps/" + applicationID + "/html/sound/" + value1 , value2, 0.0);
      } else if (method == "setMasterVolume") {
        playerFunc.setMasterVolume(value1);
      } else if (method == "playFileInLoop") {
        playerFunc.playFileInLoop("/home/nao/.local/share/PackageManager/apps/" + applicationID + "/html/sound/" + value1 , value2, 0.0);

      } else if (method == "stopAll") {
        playerFunc.stopAll();
      } else {
        console.log("playerError");
      }
    });
  }


// 共通処理

function pageJump(pageName) {
  window.setTimeout(function() {
    document.location.href = pageName + ".html";
  }, 500);
}

function touchButtonSE() {
  player("playFile", "switch.ogg", 0.5);
}

function mypreload() {
    for(var i = 0; i< arguments.length; i++){
        $("<img>").attr("src", arguments[i]);
    }
}
//関数の呼び出し。引数には先読みしておく画像のパスを指定
mypreload('../img/weather/cloud.png', '../img/weather/rain.png', '../img/weather/snow.png', '../img/weather/sun.png', '../img/weather/thunder.png');
// =============================================================================
//      ページ毎の関数
// =============================================================================

// ---index.html----------------------------------------------------------------
function indexOnload(){
  ttsStopAll();
  beManager("runBehavior","lookAtNao");
  window.setTimeout(function(){
    animSpeech("say","ボタンを押してねッッ！！。");
  },1000);
}

function clickStartBtn(){
  touchButtonSE();
  memory("raiseEvent","NaoEvent/lookAtNaoStop",1);
  pageJump("iceBreak");
}

function clickEndBtn(){
  ttsStopAll();
  touchButtonSE();
  beManager("runBehavior","animations/Stand/Emotions/Neutral/Hello_1");
  window.setTimeout(function(){
    tts("say","ご利用ありがとうございましたッッ！！。またのご来店を、お待ちしておりますッッ！！。お気をつけてッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("index");
  },7000);
}


// iceBreak.html----------------------------------------------------------------
function iceBreakOnload(){
  ttsStopAll();
  beManager("runBehavior","ojigi");
  window.setTimeout(function(){
    getWeather();
  },1000);
  window.setTimeout(function(){
    pageJump("intro");
  },12000);
}

// intro.html----------------------------------------------------------------
function introOnload(){
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","こんにちはッッ！！。僕の名前は、NAOですッッ！！。フランスで開発されて、最近では日本でも、仕事をするようになりましたッッ！！。どうぞ、宜しくお願い致します！。");
  },1000);
  window.setTimeout(function(){
    pageJump("question1");
  },18000);
}

//question1.html----------------------------------------------------------------
function question1Onload(){
  ttsStopAll();
  window.setTimeout(function(){
    beManager("runBehavior","question1");
  },500);
  window.setTimeout(function(){
    q1DialogStop();
    pageJump("index");
  },45000);
}

function q1_1BtnClick(){
  q1BtnDisabled();
  q1DialogStop();
  touchButtonSE();
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","お疲れ様ですッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("question2");
  },2000);
}

function q1_2BtnClick(){
  q1BtnDisabled();
  q1DialogStop();
  touchButtonSE();
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","お勉強頑張ってくださいねッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("question2");
  },2000);
}

function q1BtnDisabled(){
  document.getElementById("q1Btn1").disabled=true;
  document.getElementById("q1Btn2").disabled=true;
}

function q1DialogStop(){
  memory("raiseEvent","NaoEvent/q1DialogStop",1);
}

// pepperイベント監視
session.service('ALMemory').done(function(alMemory){
  alMemory.subscriber('NaoEvent/question1end').done(function(subscriber){
    subscriber.signal.connect(function(val){
      // イベントが発生すると呼び出される
      console.log('[EVENT]NaoEvent/question1end:' + val);
      q1BtnDisabled();
      animSpeech("say",val);
      window.setTimeout(function(){
        pageJump("question2");
      },3000);
    });
  });
});



// question2.html----------------------------------------------------------------
function question2Onload(){
  ttsStopAll();
  window.setTimeout(function(){
    beManager("runBehavior","question2");
  },500);
  window.setTimeout(function(){
    q2DialogStop();
    pageJump("index");
  },45000);
}

function q2_1BtnClick(){
  q2BtnDisabled();
  q2DialogStop();
  touchButtonSE();
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","１時間、ですね？。ありがとうございますッッ！！。あと、店長よろしくッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("finish");
  },12000);
}
function q2_2BtnClick(){
  q2BtnDisabled();
  q2DialogStop();
  touchButtonSE();
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","2時間、ですね？。ありがとうございますッッ！！。あと、店長よろしくッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("finish");
  },12000);
}
function q2_3BtnClick(){
  q2BtnDisabled();
  q2DialogStop();
  touchButtonSE();
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","3時間、ですね？。ありがとうございますッッ！！。あと、店長よろしくッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("finish");
  },12000);
}
function q2_4BtnClick(){
  q2BtnDisabled();
  q2DialogStop();
  touchButtonSE();
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","4時間、ですね？。ありがとうございますッッ！！。あと、店長よろしくッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("finish");
  },12000);
}
function q2_5BtnClick(){
  q2BtnDisabled();
  q2DialogStop();
  touchButtonSE();
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","イチニチ降る、ですね？。ありがとうございますッッ！！。あと、店長よろしくッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("finish");
  },12000);
}

function q2BtnDisabled(){
  document.getElementById("q2Btn1").disabled=true;
  document.getElementById("q2Btn2").disabled=true;
  document.getElementById("q2Btn3").disabled=true;
  document.getElementById("q2Btn4").disabled=true;
  document.getElementById("q2Btn5").disabled=true;
}

function q2DialogStop(){
  memory("raiseEvent","NaoEvent/q2DialogStop",1);
}

// pepperイベント監視
session.service('ALMemory').done(function(alMemory){
  alMemory.subscriber('NaoEvent/question2end').done(function(subscriber){
    subscriber.signal.connect(function(val){
      // イベントが発生すると呼び出される
      console.log('[EVENT]NaoEvent/question2end:' + val);
      q2BtnDisabled();
      animSpeech("say",val + "、ですね？。ありがとうございますッッ！！。あと、店長よろしくッッ！！。");
      window.setTimeout(function(){
        pageJump("finish");
      },12000);
    });
  });
});


// finish.html----------------------------------------------------------------
function finishOnload(){
  ttsStopAll();
  window.setTimeout(function(){
    animSpeech("say","ごゆっくり、おくつろぎくださいッッ！！。");
  },500);
  window.setTimeout(function(){
    pageJump("index");
  },10000);
}




// =============================================================================
//      高度処理
// =============================================================================


function getWeather(){
  $(document).ready(function() {
    $.simpleWeather({
      //初期設定
      location: 'Tokyo, JP',
      unit: 'c',
      //正常に実行された時の処理
      success: function(weather) {
        var weatherArray = new Array();
        var code = weather.code;
        // 表示用の日本語へCODEを変換
        var wCode = changeWeatherCode(code);

        $("#city").html(weather.city);
        $("#temp").html(weather.temp + '℃');
        $("#code").html(wCode);

        weatherArray[0] = weather.city;
        weatherArray[1] = weather.temp;
        weatherArray[2] = weather.wCode;

        animSpeech("say","いらっしゃいませ。今日の東京の気温は、" + weather.temp + "度。天気は、" + wCode + "と、予報が出ています。");

      },
      error: function(error) {
        tts("say","ネットワークに接続できませんでした。");
        tts("say",error);
        console.log(error);

	    }
    });
  });
}

  function changeWeatherCode(wcode){
    var rlt = "";
    // 雨
    if (wcode == 0) {rlt = "竜巻";}
    else if (wcode == 1) {rlt = "雨";}
    else if (wcode == 6) {rlt = "雨";}
    else if (wcode == 9) {rlt = "霧雨";}
    else if (wcode == 11) {rlt = "雨";}
    else if (wcode == 12) {rlt = "雨";}
    else if (wcode == 20) {rlt = "霧";}
    else if (wcode == 23) {rlt = "嵐";}
    else if (wcode == 35) {rlt = "雨のち雹";}
    else if (wcode == 37) {rlt = "ゲリラ豪雨";}
    else if (wcode == 38) {rlt = "雷雨";}
    else if (wcode == 39) {rlt = "雷雨";}
    else if (wcode == 40) {rlt = "雨";}
    else if (wcode == 45) {rlt = "雷雨";}
    else if (wcode == 47) {rlt = "ゲリラ豪雨";}

    // 晴れ
    else if (wcode == 19) {rlt = "黄砂";}
    else if (wcode == 21) {rlt = "煙霧";}
    else if (wcode == 22) {rlt = "煙";}
    else if (wcode == 24) {rlt = "風";}
    else if (wcode == 25) {rlt = "極寒";}
    else if (wcode == 31) {rlt = "晴れ";}
    else if (wcode == 32) {rlt = "晴れ";}
    else if (wcode == 33) {rlt = "晴れ";}
    else if (wcode == 34) {rlt = "晴れ";}
    else if (wcode == 36) {rlt = "猛暑";}
    else if (wcode == 3200) {rlt = "利用不可";}

    // くもり
    else if (wcode == 26) {rlt = "くもり";}
    else if (wcode == 27) {rlt = "くもり";}
    else if (wcode == 28) {rlt = "くもり";}
    else if (wcode == 29) {rlt = "くもり";}
    else if (wcode == 30) {rlt = "くもり";}
    else if (wcode == 44) {rlt = "晴れ時々くもり";}

    // 雷
    else if (wcode == 2) {rlt = "ハリケーン";}
    else if (wcode == 3) {rlt = "激しい雷雨";}
    else if (wcode == 4) {rlt = "雷雨";}

    // 雪
    else if (wcode == 5) {rlt = "雨雪";}
    else if (wcode == 7) {rlt = "雪泥";}
    else if (wcode == 8) {rlt = "霧と雹";}
    else if (wcode == 10) {rlt = "雪雨";}
    else if (wcode == 13) {rlt = "吹雪";}
    else if (wcode == 14) {rlt = "吹雪";}
    else if (wcode == 15) {rlt = "吹雪";}
    else if (wcode == 16) {rlt = "雪";}
    else if (wcode == 17) {rlt = "雹";}
    else if (wcode == 18) {rlt = "みぞれ";}
    else if (wcode == 41) {rlt = "大雪";}
    else if (wcode == 42) {rlt = "雪";}
    else if (wcode == 43) {rlt = "大雪";}
    else if (wcode == 46) {rlt = "雪";}

    else {rlt = "天気コードが間違えています。"}
    var weatherImg = new Array();
    weatherImg[0] = new Image();
    weatherImg[0].src = "img/weather/rain.png";
    weatherImg[1] = new Image();
    weatherImg[1].src = "img/weather/sun.png";
    weatherImg[2] = new Image();
    weatherImg[2].src=  "img/weather/cloud.png";
    weatherImg[3] = new Image();
    weatherImg[3].src=  "img/weather/thunder.png";
    weatherImg[4] = new Image();
    weatherImg[4].src=  "img/weather/snow.png";
    var weatherNum;

    if (rlt == "竜巻"||rlt == "雨"||rlt == "霧雨"||rlt == "霧"||rlt == "嵐"||rlt == "雨のち雹"||rlt == "ゲリラ豪雨"||rlt == "雷雨") {
      weatherNum = 0;
    }else if (rlt == "黄砂"||rlt == "煙霧"||rlt == "煙"||rlt == "風"||rlt == "極寒"||rlt == "晴れ"||rlt == "猛暑"||rlt == "利用不可") {
      weatherNum = 1;
    }else if (rlt == "くもり"||rlt == "晴れ時々くもり") {
      weatherNum = 2;
    }else if (rlt == "ハリケーン"||rlt == "激しい雷雨"||rlt == "雷雨") {
      weatherNum = 3;
    }else if (rlt == "雨雪"||rlt == "雪泥"||rlt == "霧と雹"||rlt == "雪雨"||rlt == "吹雪"||rlt == "みぞれ"||rlt == "大雪"||rlt == "雪") {
      weatherNum = 4;
    }else {
      weatherNum = 0;
    }
    document.getElementById("weatherImage").src = weatherImg[weatherNum].src;
    return rlt;
  }
