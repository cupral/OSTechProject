$(function() {

	// QiSessionオブジェクトの作成
	var session = new QiSession();

	// いずれかのボタンがタッチされたら
	$(".button").on("touchstart", function(){
		var buttonName = "";
		if ($(this).attr("class") == "button a") {
			buttonName = "A";
		}
		else if ($(this).attr("class") == "button b") {
			buttonName = "B";
		}

		// この位置に、Pepperへデータを送信する処理を追加
		session.service("ALMemory").then(function (ALMemory) {
			ALMemory.raiseEvent("practice6-1", buttonName);
		});
	});

});
