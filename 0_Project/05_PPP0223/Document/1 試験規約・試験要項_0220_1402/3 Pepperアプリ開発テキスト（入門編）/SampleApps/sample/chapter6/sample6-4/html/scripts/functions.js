$(function() {

	// QiSessionオブジェクトの作成
	var session = new QiSession();

	// いずれかのボタンがタッチされたら
	$(".button").on("touchstart", function(){
		var speakString = "";
		if ($(this).attr("class") == "button a") {
			speakString = "えー";
		}
		else if ($(this).attr("class") == "button b") {
			speakString = "びー";
		}
		else if ($(this).attr("class") == "button c") {
			speakString = "しー";
		}

		// Pepperにイベントを通知してデータを転送
		session.service("ALMemory").then(function (ALMemory) {
			ALMemory.raiseEvent("sample6-4/toPepper", speakString);
		});
	});

});
