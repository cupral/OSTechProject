参考URL：
http://www.plusdesign.co.jp/blog/?p=3372


テキストに動きを追加するプラグインです。
実装も簡単なプラグインです。
一番の特徴はエフェクトやアニメーションの種類がとても多い点で、
テキストに見せ方に困っている時に役立ちます！

「textillate.js」の導入

必要なファイルを下記公式ページの右上の「Download　ZIP」から一式ダウンロード。
https://github.com/jschr/textillate

ダウンロードしたファイルから下記3つを読み込みます。
jquery.textillate.js
jquery.lettering.js
animate.css

読み込む記述は下記のようになります。
※jQuery本体ファイルの読み込みを忘れずに！

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript" src="jquery.textillate.js"></script>
<script type="text/javascript" src="jquery.lettering.js"></script>
<link rel="stylesheet" type="text/css" href="animate.css">


基本的な記述方法

基本的にはhead内か外部JSとして下記のscript部分を記述すれば、イベントが実行されます。
例として「lead」というclass名にしていますが、idやclassでも可能で、自由に指定できます。
また同時に複数指定も可能です。

<script>
$(function () {
    $('.lead').textillate({loop: true});
})
</script>

<p class="lead">アニメーションさせたい文字列</p>


オプションの指定方法

細かい設定は下記のように指定可能です。

<script>
$(function () {
    $('.lead').textillate({
      //繰り返し
      loop: true,
      // アニメーションの間隔時間
      minDisplayTime: 3000,
      // アニメーション開始までの遅延時間
      initialDelay: 1000,
      // アニメーションの自動スタート
      autoStart: true,

      // 開始時のアニメーション設定
      in: {
        // エフェクトの指定
        effect: 'fadeIn',
        // 遅延時間の指数
        delayScale: 1.5,
        // 文字ごとの遅延時間
        delay: 50,
        // true:アニメーションをすべての文字に同時適用
        sync: false,
        // true:文字表示がランダムな順に表示される
        shuffle: false
      },

      // 終了時のアニメーション設定
      out: {
        effect: 'fadeOut',
        delayScale: 1.5,
        delay: 50,
        sync: false,
        shuffle: false
      }
    });
})
</script>
エフェクトの指定はデモサイトの名称から選びます。
種類が豊富なので、いろいろ試してみてください。
http://jschr.github.io/textillate/



実装してみて

実装はとても簡単でした。
またエフェクトやアニメーションの種類がとても多いので、
いろいろな組み合わせができ、とても便利です。

懸念点としてはCSS3を使っているので、IE9以下は動きが少し異なります。
対象ブラウザにIE9以下も含める時は注意する必要があります。
※先日IE10までのサポートは終了したので、そこまで意識しなくてもいいかもしれませんが。

あと終了時のアニメーション指定は繰り返し「loop」を「true」にしていないと反応しません。
複数のテキストに実装したり、繰り返しはせず、
終了時のアニメーションをする場合は、他のプラグインと組み合わせるか、
直接指定をするのがいいかもしれません。

ただテキストにひとひねりきかせた動きを追加したい時にはオススメです！
ぜひ試してみてはいかがでしょうか。



参考リンク

下記サイトは参考にしたサイトになります。
丁寧に説明してくれているので、詳しく知りたい方はどうぞ。
http://peacepopo.net/blog-entry-188.html
