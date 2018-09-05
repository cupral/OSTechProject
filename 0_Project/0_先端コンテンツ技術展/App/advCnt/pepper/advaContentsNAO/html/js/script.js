// =============================================================================
//      共通設定 (IP,アプリIDなど)
// =============================================================================
// アプリケーションID
var applicationID = "advacontents_nao";
// ロボットの名前
var robotName = "tantan";

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

// =============================================================================
//      共通関数 javascript
// =============================================================================

var worldFunc = "共通関数JavaScript";
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
    player("playFile", "switch.ogg", 0.5);
  }

// ~~Onload系の関数はすべてHTMLを読み込んだ時に作動する関数
// =============================================================================
//      index.html
// =============================================================================
function indexOnload() {
    $.when(initIndex()).done(endIndex());
  }

  function initIndex() {
    var dfd = $.Deferred();
    player("stopAll");
    ttsStopAll();
    // ユーザデータ初期化
    memory("insertData", "PepperMemory/userName", "");
    memory("insertData", "PepperMemory/userScore", "");

    dfd.resolve();
    return dfd.promise();
  }


  function endIndex() {
    // ランダム発言用配列
    var indexSay = new Array("こんにちは、今日はアジュールのコンピュータ、ビジョンエーピーアイを使ったデモを行っています！","俳句を自動生成するデモを行っています！","よってらっしゃい見てらっしゃい","どうもどうも");
    var randa = Math.floor(Math.random()*5);
    animSpeech("say",indexSay[randa]);
    // Pepperにランダムで発言させる
    var indexSayTimer = setInterval(function () {
      var rand = Math.floor(Math.random()*5);
      animSpeech("say",indexSay[rand]);
    }, 15000);//発話インターバル秒数(ms)
    // 効果音を流す
    window.setTimeout(function(){
      player("playFileInLoop", "opening.mp3", 0.1);
    },1000);
  }

  function indexStartBtn(){
    touchButtonSE();
    pageJump("preview");
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
    window.setTimeout(function(){
      animSpeech("say", "それでは、僕のヒタイのカメラに季語となる画像を見せてください！");
    },1000);
    shutterCount();
    window.setTimeout(takePicture, 10000);
  }

function shutterCount(){
  var cnt = 10;
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
//      showImg.html
// =============================================================================
function showImgOnload() {
  ttsStopAll();
  player("stopAll");
  beManager("runBehavior", "motion/handsForTablet");
  window.setTimeout(function() {
    animSpeech("say", "この画像でよろしいか？");
  }, 1000);
}

function imgCheckNo(){
  touchButtonSE();
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
    window.setTimeout(function() {
      player("playFile", "piano.mp3" , 0.1);
      animSpeech("say", "はーいッッ！！。お疲れ様でしたッッ！！。今回の俳句は、こちらッッ！！。");
    }, 1000);
    try {
      session.service("ALMemory").done(function(mmr){
        mmr.getData("RobotMemory/dispKigo").done(function(dispKigo){
           resultKigo = document.getElementById("resultKigo");
           resultKigo.innerHTML = dispKigo;
           console.log(dispKigo);
        });
        mmr.getData("RobotMemory/disp7").done(function(disp7){
           result7 = document.getElementById("result7");
           result7.innerHTML = disp7;
           console.log(disp7);
        });
        mmr.getData("RobotMemory/disp5").done(function(disp5){
           result5 = document.getElementById("result5");
           result5.innerHTML = disp5;
           console.log(disp5);
        });

        mmr.getData("RobotMemory/robotHaiku").done(function(robotHaiku){
          animSpeech("say",robotHaiku);
        });
      });
      window.setTimeout(function() {
        pageJump("finish")
      }, 15000);
    } catch (e) {
      tts("say","俳句を取得できませんでした。コレグラフ側のアプリが確認しているか確認してください。");
      console.log(e)

    }
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
    window.setTimeout(function(){
      animSpeech("say", "また、俳句生成に挑戦してみてねッッ！！。");
    },1000);
    window.setTimeout(function(){
      pageJump("index");
    },8000);
  }


// =============================================================================
//      VisionAPI
// =============================================================================
$(function(){
  $('#photo_Check').click(function(){
    touchButtonSE();
    //$(this).text("クリックされました");
    var img = document.getElementById("usr_Photo");
    // Base64 String
    // document.getElementById('log').value = ImageToBase64(img, "image/png");
    var img64 = ImageToBase64(img,"image/png");
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
      ctx.drawImage(image_src, 0, 0);
      // Image Base64
      return canvas.toDataURL(mime_type);
  }
  //=====================================================
  // Base64形式の文字列 → <img>要素に変換
  //   base64img: Base64形式の文字列
  //   callback : 変換後のコールバック。引数は<img>要素
  //=====================================================
  // function Base64ToImage(base64img, callback) {
  //     var img = new Image();
  //     img.onload = function() {
  //         callback(img);
  //     };
  //     img.src = base64img;
  // }

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

    var params = {
      "visualFeatures": "Categories,Tags",
      "details": "Landmarks",
      "language": "en",};
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
        try {
          var tags = data["tags"];
          console.log(tags);
          var tagsLen = tags.length;
          console.log(tagsLen);
          var step;
          var resultName;
          for(step = 0; step < tagsLen; step++){
            resultName += tags[step]["name"]+ ",";
          }
          console.log(resultName);
          sendGGLTrans(resultName);
        } catch (e) {
          tts("say","配列が正しく受信できませんでした。技術者を呼んでください。");
          console.log(e);
        }
        console.log(JSON.stringify(data, null, 2));
      })
      .fail(function(jqXHR, textStatus, errorThrown) {
        // Display error message.
        tts("say","ネットワークの接続状況が悪いか、ビジョンエーピーアイとうまく連動できません。ネットワークの接続状況を確認してください。");
        var errorString = (errorThrown === "") ? "Error. " : errorThrown + " (" + jqXHR.status + "): ";
        errorString += (jqXHR.responseText === "") ? "" : jQuery.parseJSON(jqXHR.responseText).message;
        console.log(errorString);
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
        // $('body').append('通信成功');
        console.log(res);
        var data = res["data"];
        console.log(data);
        var translations = data["translations"];
        var translatedTxt = translations[0].translatedText;
        console.log(translations[0].translatedText);
        memory("raiseEvent","RobotEvent/transText",translations[0].translatedText);
        window.setTimeout(function() {
          pageJump("resultCheck");
        }, 1500);
    })
    .fail(function(res){
      tts("say","翻訳エーピーアイとうまく連動ができませんでした。ネットワークの通信状況などを確認してください。");
      console.log(res);
    })
    .always(function(res){

    });
  };

});
