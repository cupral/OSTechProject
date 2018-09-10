// この位置で、QiSessionオブジェクト生成
var session = new QiSession();

// イベントハンドラ関数
function toTabletHandler(value) {
	document.getElementById("hand").innerHTML = value;
}

// この位置に、イベントを監視する処理を追加
function startSubscribe() {
	session.service("ALMemory").then(function (ALMemory) {
		ALMemory.subscriber("practice6-2").then(function(subscriber) {
			subscriber.signal.connect(toTabletHandler);
		});
	});
}