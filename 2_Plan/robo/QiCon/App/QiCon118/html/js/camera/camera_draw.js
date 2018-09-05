$(function () {
    // 初期化
    var _canvas = document.getElementById("preview");
    var _context = _canvas.getContext('2d');
    var _mainCanvas = document.getElementById("main");
    var _mainContext = _mainCanvas.getContext('2d');

    // 反転させて表示
    _mainContext.translate( 640, 0 );
    _mainContext.scale(-1,1);


    var _enablePlay = false;
    var _timer;
    var _handle;
    var _videoDevice;

    var _name = 'preview';
    var _fps = 20;
    var _type = 0;

    var session = new QiSession();


    // カメラのプレビューを開始
    startPreview();
    /* Webページ表示時の処理ここまで */

    /* アンロード時のプレビュー停止 */
    $(window).unload(function () {
        console.log("stopPreview");
        stopPreview();
    });


    /* カメラプレビューの開始 */
    function startPreview() {
        console.log("startPreview start.");

        if (_enablePlay) {
            stopPreview();
            console.log("playing preview stop!!!");
        }

        // 上部、下部カメラの場合
        var color = 9;          // カラー
        var resolution = 0;     // 解像度最低
        _draw = drawColorCamera;
        console.log("ALVideoDevice get!");

        session.service("ALVideoDevice").done(function (videoDevice) {
            console.log("subscribeCamera start.");
            // カメラの解像度情報等を設定し、そのハンドルを取得
            videoDevice.subscribeCamera(_name, _type, resolution, color, _fps).then(function (handle) {
                console.log("subscribeCamera done.");
                _enablePlay = true;
                _handle = handle;
                _videoDevice = videoDevice;

                // プレビューの更新開始
                _timer = setTimeout(function () {
                    updatePreview(videoDevice, handle)
                }, 1000 / _fps);
                console.log("preview start!!!");
            }, $.onQimError);
        }, $.onQimError);
        console.log("startPreview end.");
    }

    /* カメラプレビューの停止 */
    function stopPreview() {
        console.log("stopPreview start.");
        _enablePlay = false;

        // プレビューの更新停止
        clearTimeout(_timer);

        // ハンドルを解放
        unsubscribeCamera(_videoDevice, _handle);
        console.log("stopPreview end.");
    }

    /* カメラプレビューの更新 */
    function updatePreview(videoDevice, handle) {
        if (!_enablePlay)
            return;

        // カメラから画像データ取得
        videoDevice.getImageRemote(handle).then(function (image) {
            if (image) {
                var width = image[0];
                var height = image[1];

                // 取得した画像をcanvasへ表示
                _draw(atob(image[6]), width, height);

                videoDevice.releaseImage(handle);

                _timer = setTimeout(function () {
                    updatePreview(videoDevice, handle)
                }, 1000 / _fps);
            }
        }, $.onQimError);
    }

    /* カメラの登録解除 */
    function unsubscribeCamera(videoDevice, handle) {
        console.log("unsubscribeCamera start.");
        if (handle) {
            videoDevice.unsubscribe(handle).then(function () {
                handle = null;
            }, $.onQimError);
        }
        console.log("unsubscribeCamera end.");
    }


    /* カメラの描画 */
    var t = [];
    for (var i = 0; i < 1024; ++i) {
        t[String.fromCharCode(i)] = i;
    }

    /* カラー情報変換 */
    function YUV_to_RGBA(y, u, v) {
        var r = y + 1.402 * (v - 128);
        var g = y - 0.344 * (u - 128) - 0.714 * (v - 128);
        var b = y + 1.772 * (u - 128);

        return [r, g, b, 0xFF];
    }

    /* カメラの描画（カラー） */
    function drawColorCamera(data, width, height) {
        var image = _context.createImageData(width, height);
        var imageData = image.data;
        var data_ptr = 0;
        var image_ptr = 0;

        for (var y = 0; y < height; y++) {
            // YUV422は4バイトで2ピクセルを表現する形式なので、X方向は2ドットずつ飛ぶ
            for (var x = 0; x < width; x += 2, data_ptr += 4, image_ptr += 8) {
                var y1 = data.charCodeAt(data_ptr);
                var u = data.charCodeAt(data_ptr + 1);
                var y2 = data.charCodeAt(data_ptr + 2);
                var v = data.charCodeAt(data_ptr + 3);

                //RGBAに変換
                var rgb1 = YUV_to_RGBA(y1, u, v);
                var rgb2 = YUV_to_RGBA(y2, u, v);

                // 2ドット分のRGBAを入れた配列 (= 8bytes)
                var rgb = rgb1.concat(rgb2);

                // canvasのimageDataへ設定
                imageData[image_ptr + 0] = rgb[0];
                imageData[image_ptr + 1] = rgb[1];
                imageData[image_ptr + 2] = rgb[2];
                imageData[image_ptr + 3] = rgb[3];
                imageData[image_ptr + 4] = rgb[4];
                imageData[image_ptr + 5] = rgb[5];
                imageData[image_ptr + 6] = rgb[6];
                imageData[image_ptr + 7] = rgb[7];
            }
        }

        // canvasへ書き出し
        _context.putImageData(image, 0, 0);

        // 画像サイズを引き伸ばして画面へ表示
        _mainContext.drawImage(_canvas, 0, 0, 720, 540);



    }

});
