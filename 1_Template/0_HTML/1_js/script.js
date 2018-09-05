

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

  // ページ遷移アニメーション
	$(".welcome")	.delay(1000)		.animate({opacity:1},	800);
	$(".outputIP").delay(0)         .animate({opacity:0},800);

// 要素に値を挿入
	$("#deleteTxt").html("<br>よろしければ、アップロード開始ボタンをタップしてください。");
	$("#gaugeNumber").text("0%");

//画像入れ替え
  // html側
  // <img src="img/test.png" alt="テスト" name="test"/>
  var testImage = {};
  	testImage[0]="img/test.png";
    testImage[1]="img/test1.png";
	document.test.src=testImage[0];

  // 背景画像入れ替え
  $('.box').css({backgroundImage: 'url("img/test.png")' });

  // 無効化
  $(".box").prop("disabled", true);
