// QiSessionオブジェクトの作成
var session = new QiSession();

// タブレットに処理を反映
function toTabletHandler(value) {
	document.getElementById("pepper").innerHTML = value;
}

// ALMemoryイベント監視
function startSubscribe() {
	session.service("ALMemory").then(function (ALMemory) {
		ALMemory.subscriber("sample6-5/fromPepper").then(function(subscriber) {
			subscriber.signal.connect(toTabletHandler);
		});
	});
}
