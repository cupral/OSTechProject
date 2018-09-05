
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
        playerFunc.playFile("/home/nao/.local/share/PackageManager/apps/advcontentsdemo/html/sound/" + value1);
      } else if (method == "setMasterVolume") {
        playerFunc.setMasterVolume(value1);
      } else if (method == "playFileInLoop") {
        playerFunc.playFileInLoop("/home/nao/.local/share/PackageManager/apps/advcontentsdemo/html/sound/" + value1);
      } else if (method == "stopAll") {
        playerFunc.stopAll();
      } else {
        console.log("playerError");
      }
    });
  }

// =============================================================================
//      共通関数 javascript
// =============================================================================


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

// ランダム発言用配列
var indexSay = new Array("こんにちは、今日はアジュールのコンピュータ、ビジョンエーピーアイを使ったデモを行っています！","俳句を自動生成するデモを行っています！","よってらっしゃい見てらっしゃい","どうもどうも");

function endIndex() {

  // Pepperにランダムで発言させる
  var indexSayTimer = setInterval(function () {
    var rand = Math.floor(Math.random()*5);
    tts("say",indexSay[rand]);
  }, 10000);
  // 効果音を流す
  player("playFileInLoop", "opening.mp3");
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
  tts("say", "それでは、僕の額のカメラに季語となる画像を見せてください！\\pau=1200\\俳句生成、スター当ッッ！！。３。２。１。");
  shutterCount();
  window.setTimeout(takePicture, 17000);
}

function shutterCount(){
  var cnt = 5;
  var timer1 = setInterval(function(){
    cnt = cnt - 1;
    cntDown = document.getElementById("countDown");
    cntDown.innerHTML = cnt;
    if (cnt == 0) {
      clearInterval(timer1);
    }
  },1000);

}


// カメラ機能
function takePicture() {
  ttsStopAll();
  console.log("takePicture");
  //アプリケーション内のimgフォルダに画像を一時的に保存
  var recordFolder = '/home/nao/.local/share/PackageManager/apps/advcontentsdemo/html/img';
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
  player("playFile", "camera1.ogg");
  window.setTimeout(function(){pageJump("showImg")},1000)
  // n秒後に採点中画面へページ遷移
}


// =============================================================================
//      showImg.html
// =============================================================================
function showImgOnload() {
  ttsStopAll();
  player("stopAll");

  beManager("runBehavior", "motion/handsForTablet");
  window.setTimeout(function() {
    tts("say", "\\pau=2000\\こちらの画像でよろしいですか？");
  }, 1000);

  window.setTimeout(function() {
    player("playFile", "rollend.mp3")
  }, 2000);
}

function imgCheckNo(){
  pageJump("preview");
}

// =============================================================================
//      resultCheck.html
// =============================================================================
function resultCheckOnload() {
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
  tts("say", "はーいッッ！！。お疲れ様でしたッッ！！。今回の俳句は、こちらッッ！！。");
  window.setTimeout(function() {
    player("playFile", "piano.mp3")
  }, 1000);
  session.service("ALMemory").done(function(mmr){
    mmr.getData("RobotMemory/dispHaiku").done(function(dispHaiku){
       target = document.getElementById("resultHaiku");
       target.innerHTML = dispHaiku;
       console.log(dispHaiku);
    });
    mmr.getData("RobotMemory/robotHaiku").done(function(robotHaiku){
      tts("say",robotHaiku);
    });
  });
  window.setTimeout(function() {
    pageJump("finish")
  }, 30000);
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


// =============================================================================
//      VisionAPI
// =============================================================================
$(function(){
  $('#photo_Check').click(function(){
    //$(this).text("クリックされました");
    var img = document.getElementById("usr_Photo");
    // Base64 String
    // document.getElementById('log').value = ImageToBase64(img, "image/png");
    var img64 = ImageToBase64(img,"image/png");
    // var imgBinary  = makeblob(img64);
    sendBinary(img64);
  });

// ===========================================================================
    function ImageToBase64(image_src, mime_type) {
        // New Canvas
        var canvas = document.createElement('canvas');
        canvas.width = image_src.width;
        canvas.height = image_src.height;
        // Draw
        var ctx = canvas.getContext('2d');
        // ctx.drawImage(image_src, 0, 0);
        // Image Base64
        return canvas.toDataURL(mime_type);
    }
// ===========================================================================
  function makeblob(dataURL){
    var BASE64_MARKER = ';base64,';
    if (dataURL.indexOf(BASE64_MARKER) == -1) {
      var parts = dataURL.split(',');
      var contentType = parts[0].split(':')[1];
      var raw = decodeURIComponent(parts[1]);
      return new Blob([raw], { type: contentType });
    }
    var parts = dataURL.split(BASE64_MARKER);
    var contentType = parts[0].split(':')[1];
    var raw = window.atob(parts[1]);
    var rawLength = raw.length;

    var uInt8Array = new Uint8Array(rawLength);

    for (var i = 0; i < rawLength; ++i) {
      uInt8Array[i] = raw.charCodeAt(i);
    }
    return new Blob([uInt8Array], { type: contentType });
  }
// ===========================================================================

  function sendBinary(file){
    //Request params https://westus.api.cognitive.microsoft.com/vision/v1.0
    //var apiUrl = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze"
    var apiUrl = "https://westus.api.cognitive.microsoft.com/vision/v1.0/analyze"

    var apiKey = "a335af393cf94210a5d5f61273a200de";
    var params = {"visualFeatures": "Categories,Tags","details": "Landmarks","language": "en",};
    $.ajax({
      url: apiUrl + "?" + $.param(params),
      beforeSend: function(xhrObj){
          // Request headers
          xhrObj.setRequestHeader("Content-Type","application/octet-stream");
          xhrObj.setRequestHeader("Ocp-Apim-Subscription-Key", apiKey);
      },
      type: "POST",
      data: makeblob(file),
      processData: false
    })
    .done(function(data) {
      // Show formatted JSON on webpage.
      var categories = data["categories"];
      console.log(categories);
      var name0 = categories[0]["name"];
      var score0 = categories[0]["score"];
      var name1 = categories[1]["name"];
      var score1 = categories[1]["score"];
      var name2 = categories[2]["name"];
      var score2 = categories[2]["score"];
      if (score0 > score1) {
        if (score0 > score2) {
          sendGGLTrans(name0);
        }
      }else if (score1 > score0) {
        if (score1 > score2) {
          sendGGLTrans(name1);
        }
      }else if (score2 > score0) {
        if (score2 > score1) {
          sendGGLTrans(name2);
        }
      }else {
        alert("scoreIfError");
      }
      console.log(name0);
      console.log(score0);
      console.log(name1);
      console.log(score1);
      console.log(name2);
      console.log(score2);
      console.log(JSON.stringify(data, null, 2));
    })
    .fail(function(jqXHR, textStatus, errorThrown) {
      // Display error message.
      var errorString = (errorThrown === "") ? "Error. " : errorThrown + " (" + jqXHR.status + "): ";
      errorString += (jqXHR.responseText === "") ? "" : jQuery.parseJSON(jqXHR.responseText).message;
      alert(errorString);
    });
  };
  function sendGGLTrans(category){
    var defURL = 'https://www.googleapis.com/language/translate/v2?key=';
    var apiKey = 'AIzaSyDc0Dd7N3iT0hVYIKgZ8c4u6V4ObEc3yPg';
    var target = '&target=';
    var changelang = 'ja';
    var requestWord = '&q=';
    var changeWord = category;
    var sendUrl = defURL + apiKey + target + changelang + requestWord + changeWord;

    var xhr = $.ajax({
        type: 'GET',
        url: sendUrl,
        dataType: 'jsonp',
        timeout: 30000
    })
    .done(function(res){
        $('body').append('通信成功');
        console.log(res);
        var data = res["data"];
        console.log(data);
        var translations = data["translations"];
        var translatedTxt = translations[0].translatedText;
        console.log(translations[0].translatedText);
        memory("raiseEvent","RobotEvent/transText",translations[0].translatedText);
        window.setTimeout(function() {
          pageJump("scoring");
        }, 3000);
    })
    .fail(function(res){
        $('body').append('通信失敗');
    })
    .always(function(res){

    });
  };

// ===========================================================================
});
