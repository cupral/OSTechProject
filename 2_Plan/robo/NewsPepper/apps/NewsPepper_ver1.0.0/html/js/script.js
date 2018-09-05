// インポート
var session = new QiSession();
var xmlHttpRequest = new XMLHttpRequest();

// ==================================================================
// 宣言
var contents1;var contents2;var contents3;var title1;var title2;var title3;
var overview1_1;var overview1_2;var overview1_3;var overview2_1;var overview2_2;var overview2_3;
var overview3_1;var overview3_2;var overview3_3;

// ==================================================================

//サーバ(apiary)からjsonを取得
function getXml(){
  xmlHttpRequest.open('GET', 'http://private-9e87d-newspepperapi.apiary-mock.com/list', true);
  xmlHttpRequest.responseType = 'json';
  xmlHttpRequest.send( null );
}

// トーク、出力言語を切り替え
function getLangContents(lang){
  getXml();
  if (lang=="jp") {
    setLangueages("Japanese");
  }else if (lang=="us") {
    setLangueages("English");
  }else if (lang=="cn") {
    setLangueages("Chinese");
  }else {
    console.log("changeLanguageError");
  }

  xmlHttpRequest.onreadystatechange = function(){
    if( this.readyState == 4 && this.status == 200 ){
      if( this.response ){
          contents1 = this.response[lang + "_contents1"];
          contents2 = this.response[lang + "_contents2"];
          contents3 = this.response[lang + "_contents3"];
          title1 = this.response[lang + "_title1"];
          title2 = this.response[lang + "_title2"];
          title3 = this.response[lang + "_title3"];
          overview1_1 = this.response[lang + "_overview1_1"];
          overview1_2 = this.response[lang + "_overview1_2"];
          overview1_3 = this.response[lang + "_overview1_3"];
          overview2_1 = this.response[lang + "_overview2_1"];
          overview2_2 = this.response[lang + "_overview2_2"];
          overview2_3 = this.response[lang + "_overview2_3"];
          overview3_1 = this.response[lang + "_overview3_1"];
          overview3_2 = this.response[lang + "_overview3_2"];
          overview3_3 = this.response[lang + "_overview3_3"];
          document.getElementById("contents_id1").textContent=title1;
          document.getElementById("contents_id2").textContent=title2;
          document.getElementById("contents_id3").textContent=title3;
      }
    }
  }
}

// ==================================================================
// Pepper機能

// Pepperの発言を止める
function sayStop(){
  session.service("ALTextToSpeech").done(function(tts){
    tts.stopAll();
  });
}

// テキストを読み上げる
function sayContents(text){
  session.service("ALAnimatedSpeech").done(function(aniSpe){
    aniSpe.say(text);
  });
}

// アプリケーションを終了
function appFinish(){
  sayStop();
  hideWebView();
  session.service("ALMemory").done(function(memory){
    memory.raiseEvent("PepperTablet/appFinish", 0);
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

// 発言言語変更
function setLangueages(language){
  session.service("ALTextToSpeech").done(function(tts){
    tts.setLanguage(language);
  });
}

//------------------------------------------------------------------------------

// Javascript処理

// ニュースボタンを押した際に、記事内容を読上
function contentsButton(contentsNum){
  sayStop();
  if (contentsNum==0) {
    showContents(overview1_1, overview1_2 ,overview1_3);
    sayContents(contents1);
  }else if (contentsNum==1) {
    showContents(overview2_1, overview2_2 ,overview2_3);
    sayContents(contents2);
  }else if (contentsNum==2) {
    showContents(overview3_1, overview3_2 ,overview3_3);
    sayContents(contents3);
  }else{
    console.log("contentsButtonError");
  }
}

// タブレット画面に記事の概要を表示
function showContents(overview1, overview2 ,overview3){
  document.getElementById("overview1").textContent=overview1;
  document.getElementById("overview2").textContent=overview2;
  document.getElementById("overview3").textContent=overview3;
}

// アプリ開始時のアナウンス
function sayHello(){
  sayContents("\\vct=135\\ \\rspd=105\\　こんにちはッッ！！。気になるニュースを選んでね？");
}

// フォームから値を取得して、Pepperの抑揚などを変更
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
  sayContents(speedSharping);
}

// ==================================================================
// 関数実行欄

//Xmlを取得
getLangContents("jp");

// ==================================================================
