// =============================================================================
//      共通設定 (IP,アプリIDなど)
// =============================================================================

var applicationID = "ai_expo_pepper";// アプリケーションID
var robotName = "pepper";// ロボットの名前


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

// 画像切り替え
  session.service('ALMemory').done(function(alMemory){
    alMemory.subscriber('PepperEvent/pageChange').done(function(subscriber){
      subscriber.signal.connect(function(val){
        if (val == 0) {
          $('#contents-image').attr({src:"img/init.jpg",alt:"init"});
        }else if (val == 1) {
          $('#contents-image').attr({src:"img/ostech.jpg",alt:"ostech"});
        }else if (val == 2) {
          $('#contents-image').attr({src:"img/takumi.jpg",alt:"takumi"});
        }else if (val == 3) {
          $('#contents-image').attr({src:"img/rishoku.jpg",alt:"rishoku"});
        }
        // else if (val == 4) {
        //   $('#contents-image').attr({src:"img/furiwake.jpg",alt:"furiwake"});
        // }
        else {
          alert("Page Change Error");
          console.log("Page Change Error");
        }
      });
    });
  });

function indexOnload(){
  checkDisabled();
}

function labelBtnClick(){
  touchButtonSE();
  checkDisabled();
}


function checkDisabled(){
  var checkResult = $(":checkbox");
  window.setTimeout(function(){
    if (checkResult[0]["checked"] == false && checkResult[1]["checked"] == false && checkResult[2]["checked"] == false) {
      $('#button-once').css({backgroundImage: 'url("img/button/play_disabled.png")' });
      $('#button-repeat').css({backgroundImage: 'url("img/button/repeat_disabled.png")' });
      $("#button-once").prop("disabled", true);
      $("#button-repeat").prop("disabled", true);
    }else {
      $('#button-once').css({backgroundImage: 'url("img/button/play_enable.png")' });
      $('#button-repeat').css({backgroundImage: 'url("img/button/repeat_enable.png")' });
      $("#button-once").prop("disabled", false);
      $("#button-repeat").prop("disabled", false);
    }
  },250);
}

// 再生ボタンクリック
  function onceBtnClick(){
    touchButtonSE();
    $('#button-once').css({backgroundImage: 'url("img/button/play_press.png")' });
    var checkResult = $(":checkbox");
    if (checkResult[0]["checked"] == false && checkResult[1]["checked"] == false && checkResult[2]["checked"] == false) {
      tts.say("タイトルを選択して下さい。");
    }else {
      $("#button-once").prop("disabled", true);
      $("#button-repeat").prop("disabled", true);
      var check0 = checkResult[0]["checked"];
      var check1 = checkResult[1]["checked"];
      var check2 = checkResult[2]["checked"];
      // var check3 = checkResult[3]["checked"];
      memory("insertData","PepperMemory/ostechOnce",check0);
      memory("insertData","PepperMemory/takumiOnce",check1);
      memory("insertData","PepperMemory/rishokuOnce",check2);
      // memory("insertData","PepperMemory/furiwakeOnce",check3);
      memory("raiseEvent","PepperEvent/once","");

      pageJump("image");
    }
  }
//ループボタンクリック
  function repeatBtnClick(){
    touchButtonSE();
    $('#button-repeat').css({backgroundImage: 'url("img/button/repeat_press.png")' });
    var checkResult = $(":checkbox");
    if (checkResult[0]["checked"] == false && checkResult[1]["checked"] == false && checkResult[2]["checked"] == false) {
      // && checkResult[3]["checked"] == false
      tts.say("タイトルを選択して下さい。");
    }else {
      $("#button-once").prop("disabled", true);
      $("#button-repeat").prop("disabled", true);

      pageJump("image");

      var check0 = checkResult[0]["checked"];
      var check1 = checkResult[1]["checked"];
      var check2 = checkResult[2]["checked"];
      // var check3 = checkResult[3]["checked"];
      memory("insertData","PepperMemory/ostechRepeat",check0);
      memory("insertData","PepperMemory/takumiRepeat",check1);
      memory("insertData","PepperMemory/rishokuRepeat",check2);
      // memory("insertData","PepperMemory/furiwakerepeat",check3);
      memory("raiseEvent","PepperEvent/repeat","");
    }
  }
// クローズボタンクリック
  function closeBtnClick(){
    touchButtonSE();
    memory("raiseEvent","PepperEvent/talkStop","");
    memory("raiseEvent","PepperEvent/pageChange",0);
    $("#button-close").prop("disabled", true);
    $("#button-once").prop("disabled", false);
    $("#button-repeat").prop("disabled", false);
    pageJump("index");
  }

  // 再生終わり
    session.service('ALMemory').done(function(alMemory){
      alMemory.subscriber('PepperEvent/onceEnd').done(function(subscriber){
        subscriber.signal.connect(function(val){
          memory("raiseEvent","PepperEvent/pageChange",0);
          $("#button-once").prop("disabled", false);
          $("#button-repeat").prop("disabled", false);
          pageJump("index");
        });
      });
    });



    function dialogBtnClick(){
      touchButtonSE();
      $("#dialog_view").html('');
    }
