var session = new QiSession();

function memory(method,value1,value2){
  session.service("ALMemory").done(function(mth){
    if (method=="raiseEvent") {
      mth.raiseEvent(value1,value2);
    }else if (method=="insertData") {
      mth.insertData(value1,value2);
    }else {
      alert("memoryError");
    }
  });
}

function tts(method,value1,value2){
  session.service("ALTextToSpeech").done(function(mth){
    if (method=="say") {
      mth.say(value1);
    }else if (method=="stopAll") {
      mth.stopAll();
    }else {
      alert("ttsError");
    }
  });
}

function animSpeech(method,value1,value2){
  session.service("ALAnimatedSpeech").done(function(mth){
    if (method=="say") {
      mth.say(value1);
    }else if (method=="stopAll") {
      mth.stopAll();
    }else {
      alert("animSpeechError");
    }
  });
}

function bm(method,value1,value2){
  session.service("ALBehaviorManager").done(function(mth){
    if (method=="run") {
      mth.run(value1);
    }else if (method=="stop") {
      mth.stop(value1);
    }else {
      alert("bmError");
    }
  });
}

function appsAnalytics(method,value1,value2){
  session.service("ALAppsAnalytics").done(function(mtd){
    if (method=="push_mood") {
      mtd.push_mood(value1,value2);
    }else {
      alert("appsAnalyticsError");
    }
  });
}

function player(method,value1,value2){
  session.service("ALAudioPlayer").done(function(mtd){
    if (method=="playFile") {
      mtd.playFile("/var/www/apps/jtb_event/html/sound/"+ value1);
    }else if (method=="setMasterVolume") {
      mtd.setMasterVolume(value1);
    }else if (method=="playFileInLoop") {
      mtd.playFileInLoop("/var/www/apps/jtb_event/html/sound/"+ value1);
    }else if (method=="stopAll") {
      mtd.stopAll();
    }else{
      console.log("playerError");
    }
  });
}
// ============================================================================
// 独自関数
// ============================================================================
function testBtnCli(){
	var text = document.forms.formId.textarea1Id.value;
	tts("say",text);
}
function stopBtnCli(){
	tts("stopAll");
}

function testBtn2Cli(){
	var text = document.forms.formId.textarea2Id.value;
	tts("say",text);
}
function stopBtn2Cli(){
	tts("stopAll");
}

function plusVolume(){
	session.service("ALAudioDevice").done(function(audio){
		audio.getOutputVolume().done(function(nowVol){
			var plusVol = nowVol + 5;
			audio.setOutputVolume(plusVol);
			getVolume();
		});
	});
}

function minusVolume(){
	session.service("ALAudioDevice").done(function(audio){
		audio.getOutputVolume().done(function(nowVol){
			var minusVol = nowVol - 5;
			audio.setOutputVolume(minusVol);
			getVolume();
		});
	});
}

function getVolume(){
	session.service("ALAudioDevice").done(function(audio){
		audio.getOutputVolume().done(function(nowVol){
			var nowVolDiv = document.getElementById("nowVol");
			nowVolDiv.innerHTML = nowVol;
		});
	});
}

getVolume();




// ==================================================================================================v




function conversion(){
  var input = document.forms.form1.input_text.value;
  input = input.replace(/〜/g,"");
  input = input.replace(/。/g,"ッッ！！\\pau=700\\");
  input = input.replace(/ /g,"、");
  input = input.replace(/&nbsp;/g,"、");
  input = input.replace(/　/g,"、");
  input = input.replace(/「/g,"、");
  input = input.replace(/」/g,"、");
  input = input.replace(/\(/g,"、");
  input = input.replace(/\)/g,"、");
  input = input.replace(/（/g,"、");
  input = input.replace(/）/g,"、");
  input = input.replace(/・/g,"\\pau=200\\");
  input = input.replace(/\n/g,"\\pau=300\\");

  var search1 = document.forms.form2.search1.value;
  var substitution1 = document.forms.form2.substitution1.value;
  var result1 = new RegExp(search1, "g");
  input = input.replace(result1, substitution1);

  var search2 = document.forms.form2.search2.value;
  var substitution2 = document.forms.form2.substitution2.value;
  var result2 = new RegExp(search2, "g");
  input = input.replace(result2, substitution2);

  var search3 = document.forms.form2.search3.value;
  var substitution3 = document.forms.form2.substitution3.value;
  var result3 = new RegExp(search3, "g");
  input = input.replace(result3, substitution3);

  var search4 = document.forms.form2.search4.value;
  var substitution4 = document.forms.form2.substitution4.value;
  var result4 = new RegExp(search4, "g");
  input = input.replace(result4, substitution4);

  var search5 = document.forms.form2.search5.value;
  var substitution5 = document.forms.form2.substitution5.value;
  var result5 = new RegExp(search5, "g");
  input = input.replace(result5, substitution5);



  var output = document.getElementById("output_text");
  output.innerHTML = input;
}
