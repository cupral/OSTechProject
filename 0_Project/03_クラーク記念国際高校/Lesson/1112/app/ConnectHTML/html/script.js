//NAOのプロキシ一式を読み込み
console.log("yahoo");
var session = new QiSession();

//TextToSpeechプロキシを読み込み
session.service("ALTextToSpeech").done(function (tts) {
  tts.say("Hello");
}).fail(function (error) {
  console.log("An error occurred:", error);
});

//ALMemoryプロキシ読みk味
function touchButtonA(){
  session.service("ALMemory").done(function (memory) {
    memory.raiseEvent("NaoEvent/touchA","ボタンA");
  }).fail(function (error) {
    console.log("An error occurred:", error);
  });
}
function touchButtonB(){
  session.service("ALMemory").done(function (memory) {
    memory.raiseEvent("NaoEvent/touchB","ボタンB");
  }).fail(function (error) {
    console.log("An error occurred:", error);
  });
}
function touchButtonC(){
  session.service("ALMemory").done(function (memory) {
    memory.raiseEvent("NaoEvent/touchC","ボタンC");
  }).fail(function (error) {
    console.log("An error occurred:", error);
  });
}
//サブスクライブ 監視
session.service("ALMemory").done(function (ALMemory) {
  ALMemory.subscriber("FrontTactilTouched").done(function (subscriber) {
    // subscriber.signal is a signal associated to "FrontTactilTouched"
    subscriber.signal.connect(function (state) {
      //TextToSpeechプロキシを読み込み
      session.service("ALTextToSpeech").done(function (tts) {
        tts.say("頭をタッチしたことをJS側で認識しました。");
      }).fail(function (error) {
        console.log("An error occurred:", error);
      });
    });
  });
});
