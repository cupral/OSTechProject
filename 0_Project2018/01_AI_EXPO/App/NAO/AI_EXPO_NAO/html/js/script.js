// =============================================================================
//      共通設定 (IP,アプリIDなど)
// =============================================================================

var applicationID = "ai_expo_nao";// アプリケーションID
var robotName = "boss";// ロボットの名前


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
      }else if (method == "setLanguage") {
        ttsFunc.setLanguage(value1);
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

  // カメラ機能
  function takePicture() {
    ttsStopAll();
    try {
      console.log("takePicture");
      //アプリケーション内のimgフォルダに画像を一時的に保存
      var recordFolder = '/home/nao/.local/share/PackageManager/apps/'+ applicationID + '/html/img';
      session.service("ALPhotoCapture").done(function(photoCapture) {
        //160×120 = 0,320×240 = 1,640×480 = 2,1280×960 = 3
        var resolutin = 2;
        // TopCamera = 0 , BottomCamera = 1
        var cameraID = 0;
        // ファイル名の設定
        var fileName = "usrPhoto";
        photoCapture.setResolution(resolutin);
        photoCapture.setCameraID(cameraID);
        // ファイル型式の設定
        photoCapture.setPictureFormat("png");
        photoCapture.takePicture(recordFolder, fileName);
      });
      // シャッター音を再生
      player("playFile", "camera1.ogg", 0.5);
      window.setTimeout(function(){pageJump("showImg")},2000)
      // n秒後に採点中画面へページ遷移
    } catch (e) {
      tts("say","写真が正しく保存されませんでした。再起動するか、技術者を呼んでください。");
      console.log(e);
    }
  }

  //全てのアプリを停止させる
  function stopAllBehaviors() {
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

// =============================================================================
//      共通関数 javascript
// =============================================================================


  // アプリケーション終了命令
  function appFinish() {
    touchButtonSE();
    memory("raiseEvent", "PepperEvent/appFinish", 0);
    ttsStopAll();
    player("stopAll");
  }

  // ボタンを触れた合図用のSE
  function touchButtonSE() {
    player("playFile", "buttonTouch.wav", 1.0);
  }
  // ページジャンプ
  function pageJump(pageName) {
    window.setTimeout(function() {
      document.location.href = pageName + ".html";
    }, 500);
  }




  // =============================================================================
  //      ボタン処理
  // =============================================================================
function indexOnload(){
  getVolume();
  getRobotBattery();
  getRobotName();
}

function launcherBtnClick(num){
  touchButtonSE();
  stopAllBehaviors();
  var path = "";
  if (num == 1) {
    path = "ostech";
  }else if (num == 2) {
    path = "takumi";
  }else if (num == 3) {
    path = "furiwake";
  }else if (num == 4) {
    path = "rishoku";
  }else {
    tts("say","javascriptにエラーがおきました。");
  }
  window.setTimeout(function(){
    beManager("runBehavior", applicationID + "/" + path);
  },1000);
}

function systemBtnClick(num){
  touchButtonSE();
  if (num == 1) {
    stopAllBehaviors();
  }else if (num == 2) {
    wakeUpMode();
  }else if (num == 3) {
    restMode();
  }else {
    tts("say","javascriptにエラーがおきました。");
  }
}

function getVolume(){
  session.service("ALAudioDevice").done(function(audio){
    audio.getOutputVolume().done(function(outputVolume){
      console.log(outputVolume);
      $("#volume-Value").html(outputVolume);
    });
  });
}

function plusBtnClick(){
  touchButtonSE();
  session.service("ALAudioDevice").done(function(audio){
    audio.getOutputVolume().done(function(outputVolume){
      outputVolume = outputVolume + 5;
      session.service("ALAudioDevice").done(function(audio){
        audio.setOutputVolume(outputVolume);
        console.log(outputVolume);
        $("#volume-Value").html(outputVolume);
      });
    });
  });
}

function minusBtnClick(){
  touchButtonSE();
  session.service("ALAudioDevice").done(function(audio){
    audio.getOutputVolume().done(function(outputVolume){
      outputVolume = outputVolume - 5;
      session.service("ALAudioDevice").done(function(audio){
        audio.setOutputVolume(outputVolume);
        console.log(outputVolume);
        $("#volume-Value").html(outputVolume);
      });
    });
  });
}
var batteryImage = {};
	batteryImage[0]="img/battery/35.png";
	batteryImage[1]="img/battery/70.png";
	batteryImage[2]="img/battery/100.png";
  batteryImage[3]="img/battery/charging.png";



function getRobotName(){
  session.service("ALSystem").done(function(sys){
    sys.robotName().done(function(name){
      $("#robot-name").text(name + ".local");
    });
  });
}

function getRobotBattery(){
  session.service("ALBattery").done(function(battery){
    battery.getBatteryCharge().done(function(charge){
      $("#battery-value").text(charge +"%");
      if (charge <= 35) {
        document.battery.src=batteryImage[0];
      }else if (charge <= 70) {
        document.battery.src=batteryImage[1];
      }else if (charge <= 100) {
        document.battery.src=batteryImage[2];
      }else {
        console.log("getRobotBatteryError");
      }
    });
  });
}

// 画像切り替え
  // session.service('ALMemory').done(function(alMemory){
  //   alMemory.subscriber('PepperEvent/pageChange').done(function(subscriber){
  //     subscriber.signal.connect(function(val){
  //
  //     });
  //   });
  // });
