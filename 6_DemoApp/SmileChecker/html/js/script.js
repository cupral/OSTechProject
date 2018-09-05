// ==共通関数(PepperAPI)========================================================
// ==共通関数(PepperAPI)========================================================

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
      playerFunc.playFile("/var/www/apps/smilechecker/html/sound/" + value1);
    } else if (method == "setMasterVolume") {
      playerFunc.setMasterVolume(value1);
    } else if (method == "playFileInLoop") {
      playerFunc.playFileInLoop("/var/www/apps/smilechecker/html/sound/" + value1);
    } else if (method == "stopAll") {
      playerFunc.stopAll();
    } else {
      console.log("playerError");
    }
  });
}


// ==共通関数(javascript)========================================================


// ページ遷移
function pageJump(pageName) {
  window.setTimeout(function() {
    document.location.href = pageName + ".html";
  }, 500);
}

// アプリケーション終了命令
function appFinish() {
  touchButtonSE();
  memory("raiseEvent", "PepperEvent/appFinish", 0);
  ttsStopAll();
  player("stopAll");
}

// ボタンを触れた合図用のSE
function touchButtonSE() {
  player("playFile", "switch.ogg");
}

// ~~Onload系の関数はすべてHTMLを読み込んだ時に作動する関数
// =============================================================================
//      index.html
// =============================================================================
function indexOnload() {
  $.when(initIndex()).done(endIndex());
  // ユーザデータ初期化
  memory("insertData", "PepperMemory/userName", "");
  memory("insertData", "PepperMemory/userScore", "");
}

function initIndex() {
  var dfd = $.Deferred();
  // 効果音の音量調節
  player("setMasterVolume", "0.01");
  // 両手をあげるモーションを呼び出す
  beManager("runBehavior", "motion/handsUp");
  dfd.resolve();
  return dfd.promise();
}

function endIndex() {
  // Pepperに発言させる
  tts("say", "\\vct=150\\はーいッッ！！。\\vct=135\\それではッッ！！ボク、NAOが、勝手に採点しちゃう。スマイル認定試験を、始めちゃいマァァすッッ！！。");
  // 効果音を流す
  player("playFileInLoop", "opening.mp3");
}

// =============================================================================
//      inputName.html
// =============================================================================
function inputNameOnload(){
$.when(initInputName()).done(endInputName());
}

function initInputName() {
  var dfd = $.Deferred();
  // Pepperの今の発言を停止させる
  ttsStopAll();
  beManager("runBehavior", "motion/handsForTablet");
  dfd.resolve();
  return dfd.promise();
}

function endInputName() {
  tts("say", "まずは、あなたのお名前を、教えてくださいッッ！！。");
}


function inputName() {
  touchButtonSE();
  var userNameInput = document.forms.inputFormId.inputNameId.value;
  // Pepperのメモリへユーザの名前を挿入
  memory("insertData", "PepperMemory/userName", userNameInput);
  // memoryへ挿入するのを１秒まつ
  window.setTimeout(function() {
    pageJump("preview")
  }, 1000);
}







// =============================================================================
//      preview.html
// =============================================================================

// preview画面の初期動作
function previewOnload() {
  $.when(initPreview()).done(endPreview());
}

function initPreview() {
  var dfd = $.Deferred();
  ttsStopAll();
  beManager("runBehavior", "motion/handsForFront");
  dfd.resolve();
  return dfd.promise();
}

function endPreview() {
  tts("say", "それでは、ボクの両手をつかんで、三秒間。とびっ霧の笑顔をみせてくださいッッ！！\\pau=1200\\スマイル認定試験、スター当ッッ！！。３。２。１。");
  window.setTimeout(takePicture, 17000);
}


// 笑顔数値を検出

function smileCheck() {
  session.service("ALMemory").done(function(memory) {
    // Pepper内でリアルタイムに更新されるメモリイベントを参照
    memory.getData("PeoplePerception/PeopleList").done(function(ids) {
      // Pepperが認識している眼の前のユーザごとのIDを取得
      console.log("ユーザID" + ids[0]);

      if (ids[0] == undefined) {
        $("#userCheck").text("ユーザを見失いました。");
      } else {
        $("#userCheck").text("ユーザを発見");
      }

      session.service("ALFaceCharacteristics").done(function(faceC) {
        faceC.analyzeFaceCharacteristics(ids[0]);
      });
      // 眼の前にいるユーザの笑顔数値を取得
      memory.getData("PeoplePerception/Person/" + ids[0] + "/SmileProperties").done(function(value) {
        console.log("笑顔数値と信頼度" + value);
        var smileLev = document.getElementById("smileLev");
        //笑顔数値を点数化
        var smileScore = value[0];
        // 笑顔数値を100倍して点数化
        var smileScore = smileScore * 100;
        console.log("smileScore" + value[0]);
        // 小数点削除
        var smileScore = Math.round(smileScore);
        console.log("smileScore" + smileScore);
        // プレビュー画面へ3秒毎に出力
        $("#smileLev").text("笑顔度：" + smileScore);
        // 笑顔数値をPepperへ記録。そのたびにPepperへスコアを保存
        memory.insertData("PepperMemory/userScore", smileScore);
        console.log("smileScore" + smileScore);
      });
    });
  });
}

// カメラ機能
function takePicture() {
  ttsStopAll();

  //アプリケーション内のimgフォルダに画像を一時的に保存
  var recordFolder = '/var/www/apps/smilechecker/html/img';
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
    photoCapture.setPictureFormat("jpg");
    photoCapture.takePicture(recordFolder, fileName);
  });

  // シャッター音を再生
  player("playFile", "camera1.ogg");

  session.service("ALMemory").done(function(memo) {
    // 笑顔点数が低すぎないか判断して、一定以上の点数を取ると次のフローへ
    memo.getData("PepperMemory/userScore").done(function(repeatCheck) {
      if (repeatCheck < 10) {
        tts("say", "あれれ？緊張してる？。もう一回お願いしますッッ！！。");
        window.setTimeout("location.reload()", 5000);
      } else if (repeatCheck > 100) {
        memo.insertData("PepperMemory/userScore", 100);
        window.setTimeout(function() {
          pageJump("scoring")
        }, 2000);
      } else {
        window.setTimeout(function() {
          pageJump("scoring")
        }, 2000);
      }
    });
  });

  // n秒後に採点中画面へページ遷移
}








// =============================================================================
//      scoring.html
// =============================================================================
function scoringOnload() {
  $.when(initScoring()).done(endScoring());
}

function initScoring() {
  var dfd = $.Deferred();
  ttsStopAll();
  player("stopAll");
  beManager("runBehavior", "motion/initPose");
  dfd.resolve();
  return dfd.promise();
}

function endScoring() {
  tts("say", "はーいッッ！！。お疲れ様でしたッッ！！。今から、あなたの笑顔を採点しますッッ！！。");
  window.setTimeout(function() {
    player("playFile", "piano.mp3")
  }, 1000);
  window.setTimeout(function() {
    pageJump("announcement")
  }, 6000);
}







// =============================================================================
//      announcement.html
// =============================================================================

function announcementOnload() {
  $.when(initAnnouncement()).done(endAnnouncement());
}

function initAnnouncement() {
  var dfd = $.Deferred();
  ttsStopAll();
  player("stopAll");
  dfd.resolve();
  return dfd.promise();
}

function endAnnouncement() {
  tts("say", "それでは。ケッカ発表ですッッ！！。");
  window.setTimeout(function() {
    player("playFile", "drumroll.mp3")
  }, 3000);
  window.setTimeout(function() {
    pageJump("result")
  }, 6000);
}


// =============================================================================
//      result.html
// =============================================================================
function resultOnload() {
  ttsStopAll();
  player("stopAll");

  session.service("ALMemory").done(function(memory) {
    // ユーザ名をPepperのメモリから取得
    memory.getData("PepperMemory/userName").done(function(userName) {
      console.log(userName);
      // 未入力の場合は名無しさんへ変換
      if (!(userName)) {
        $("#usrNameOutput").text("名無しさん");
      } else {
        $("#usrNameOutput").text(userName + "様");
      }
    });
    // ユーザの点数を出力
    memory.getData("PepperMemory/userScore").done(function(userScore){
      if (userScore < 10) {
        $("#usrScoreOutput").text("うーんいい笑顔だ！元がいいとどんな顔をしてもいいね！そんな貴方にはバレル型なんてどうかな？さらに表情を締めて出来るオーラを出そう！");
      }else if (userScore < 30) {
        $("#usrScoreOutput").text("んん…！？ちょっと引きつってない？疲れてる？大丈夫？ラウンド型で表情に柔らかさを演出してみない？");
      }else if (userScore < 50) {
        $("#usrScoreOutput").text("I'ts　cool…！ここはフレームレス…いや敢えて銀縁…これは難しい…流行モノに手を出すのはどうかな？店員さん！店員さーん！");
      }else if (userScore < 70) {
        $("#usrScoreOutput").text("んん…ッ！これは…もう笑顔だなんだより貴方からは勝負時の機運を感じます！金！ゴールドのフレームで運気更にドン！");
      }else if (userScore < 100) {
        $("#usrScoreOutput").text("随分若い顔の造りをしてらっしゃる！んー…ここは一つスケルトンに手を出してみるのはどうかな？意外とハマるんだよこれが。ちょっと試着してみよ？");
      }else {
        console.log("getData:PepperMemory/userScore");
      }
    });
    // memory.getData("PepperMemory/userScore").done(function(userScore) {
    //   $("#usrScoreOutput").text(userScore + "点");
    //   // ユーザの点数ごとに発言を変更
    //   window.setTimeout(function() {
    //     if (userScore < 10) {
    //       tts("say", "笑顔がほぼありませんでした。お疲れのようですね。");
    //     } else if (userScore < 30) {
    //       tts("say", "何をニヤけてるんですかッッ！！。まさか、照れてますね？");
    //     } else if (userScore < 50) {
    //       tts("say", "微笑んでる感じですね？。可愛らしいですッッ！！。");
    //     } else if (userScore < 70) {
    //       tts("say", "いい笑顔ですねッッ！！。保存した写真を送ってあげたいくらいですッッ！！。");
    //     } else if (userScore < 100) {
    //       tts("say", "人類でも、こんな素敵な笑顔を見た事がある人は少ないでしょう。ボクはラッキーだッッ！！ありがとうございますッッ！！。");
    //     } else {
    //       tts("say", "真顔じゃないですかッッ！！。そんなにつまらなかったですか？。悲しいです。");
    //     }
    //   }, 2000);
    // });

  });

  beManager("runBehavior", "motion/handsForTablet");
  window.setTimeout(function() {
    tts("say", "\\pau=2000\\あなたの結果は、こちらッッ!!。");
  }, 1000);

  window.setTimeout(function() {
    player("playFile", "rollend.mp3")
  }, 2000);
}

// =============================================================================
//      finish.html
// =============================================================================
function finishOnload() {
  $.when(initFinish()).done(endFinish());
}

function initFinish() {
  var dfd = $.Deferred();
  ttsStopAll();
  beManager("runBehavior", "motion/initPose");
  dfd.resolve();
  return dfd.promise();
}

function endFinish() {
  tts("say", "また挑戦してください根ッッ！！。");
}
