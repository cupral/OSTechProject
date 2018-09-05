// forked from Event's "Web Creator's Contest Q the 2nd【vol.2】エントリー用コード" http://jsdo.it/Event/zojr

(function(window, requestAnimationFrame, cancelAnimationFrame) {

  // Run

  $(initialize);


  //---------------------------
  // CONSTANTS
  //---------------------------

  var TOP_ID = 'qtop'; // トップページを示す id
  var ABOUT_ID = 'about'; // アバウトページを示す id
  var ABOUT_BG_IMG = 'http://jsrun.it/assets/o/P/g/v/oPgvh.png'; // About の背景画像
  var CONTENT_MAX_WIDTH = 800; // コンテンツの最大幅
  var CONTENT_ID_SUFFIX = '-content'; // アンカーリンクでの移動を避ける為に id に付与するサフィックス


  //---------------------------
  // VARS
  //---------------------------

  // 要素の jQuery オブジェクト
  var $window, $body, $container, $cube, $contents, $nav, $background;
  // ベンダープレフィックスとプロパティ名
  var prefix = Akm2.Vendor.prefix;
  var prefixHyphen = prefix ? '-' + prefix.toLowerCase() + '-' : '';
  var transformKey = getVendorProperty('transform');
  var transitionDurationKey = getVendorProperty('transitionDuration');
  var perspectiveOriginKey = getVendorProperty('perspectiveOrigin');
  // jQuery.css() で使用する設定用オブジェクト
  var containerStyle = {},
    cubeStyle = {},
    contentStyles = {
      current: {
        opacity: 1,
        zIndex: 2
      },
      prev: {
        opacity: 0,
        zIndex: 0
      },
      next: {
        opacity: 0,
        zIndex: 1
      }
    };
  backgroundAtAboutStyle = {
    backgroundPosition: '50% 180px',
    backgroundRepeat: 'no-repeat',
    backgroundImage: 'none'
  };
  var width, height; // ウィンドウサイズ
  var naviButtonsList = []; // ナビゲーションボタン要素のリスト
  var contentsIdList = []; // コンテンツ id のリスト
  var currentIndex; // 現在のインデックス
  var zLength; // cube, コンテンツに設定する z 距離
  var cubeTransitionIsFirstSec = false; // cube のモーションが最初の段階であることを示す
  var backgroundEffect; // バックグラウンド描画用の SunburstEffect クラスインスタンス

  //---------------------------
  // INITIALIZE
  //---------------------------

  function initialize() {
    $window = $(window);
    $body = $('body');
    $nav = $('#navi');
    $container = $('#container');
    $cube = $('#cube');
    $contents = $('.content');

    // 背景用 canvas を取得して背景描画用オブジェクトを初期化
    $background = $('#background');
    backgroundEffect = new SunburstEffect($background[0], 16, 233, 330);

    // ナビゲーションのボタンをコンテンツと対応させる
    var $buttons = $('#navi li');
    $contents.each(function(i) {
      var $content = $(this);

      // コンテンツの id を取得して配列に格納
      var id = $content.attr('id');
      contentsIdList.push(id || null);

      // ハッシュの変更でジャンプしてしまわないよう id にサフィックスを追加
      if (id) $content.attr('id', id + CONTENT_ID_SUFFIX);

      // 対応するボタンを取得する, コンテンツへのアンカー指定のあるリンクを子として持つリストアイテムをコンテンツの順で配列に格納
      var $targetButton = null;
      $buttons.each(function() {
        var $button = $(this);
        if ($button.children('a:first-child').attr('href') === '#' + id) {
          $targetButton = $button;
          return false;
        }
      });
      naviButtonsList[i] = $targetButton;
    });

    // スクロール用に要素をキャッシュ
    var $html_body = $('html, body');

    // 全てのリンクを走査
    $('a').each(function(i) {
      var $link = $(this);
      var href = $link.attr('href');

      // リンクがハッシュなら
      if (href && href.search(/^#/) > -1) {
        // 対象コンテンツのインデックスを取得
        var targetIndex = contentsIdList.indexOf(href.replace(/^#/, ''));

        // ハッシュが示す id がコンテンツに存在しなければ continue
        if (targetIndex < 0) return true;

        // リンクにクリックイベントリスナーを追加
        $link.on('click', function(e) {
          if (
            currentIndex === targetIndex || // 現在のインデックスと同じなら抜ける
            targetIndex < 0 || targetIndex >= $contents.length // インデックスが範囲外なら抜ける
          ) return;

          setNaviActiveButton(targetIndex);
          contentsTransition(targetIndex);

          // 一番上までスクロール
          $html_body.animate({
            scrollTop: 0
          }, 600);

          // アンカーを無効に
          e.preventDefault();
        });
      }
    });

    // cube のトランジション完了を監視, モーションの進捗の判定用
    // jQuery を使用すると Opera でリスナーが登録できないっぽいけど 3D Transform のみなので無視
    $cube.on(Akm2.Vendor.transitionend, onCubeTransitionEnd);

    // URL ハッシュの変更を監視
    $window.on('hashchange', onWindowHashChange);

    // ウィンドウのリサイズを監視, translateZ を更新する
    $window.on('resize', onWindowResize);
    onWindowResize(null);

    // ハッシュから現在のインデックスを取得
    currentIndex = getTargetIndexFromHash();

    // スクロールを監視, perspective-origin を更新する
    $window.on('scroll', onWindowScroll);
    onWindowScroll(null);

    // ナビゲーションの状態を設定
    setNaviActiveButton(currentIndex);

    // コンテンツの状態を設定, トランジションが無効の状態で実行
    $contents.each(function(i) {
      var $content = $(this);
      if (i === currentIndex) $content.css(contentStyles.current).removeClass('hide');
      else if (i < currentIndex) $content.css(contentStyles.prev).addClass('hide');
      else if (i > currentIndex) $content.css(contentStyles.next).addClass('hide');
    });

    // トップの状態を設定, トランジションが無効の状態で実行, $body にアクセスするので先に初期化しておく
    specialContentEffectsEnable(currentIndex);

    // body のトランジションを有効にする
    $body.addClass('transition-enable');

    // コンテンツを表示
    $cube.fadeIn('first');
  }


  //---------------------------
  // EVENT HANDLERS
  //---------------------------

  /**
   * Window 'resize' listener
   */
  function onWindowResize(e) {
    width = window.innerWidth;
    height = window.innerHeight;

    // ウィンドウサイズから z 座標の距離を決定する
    zLength = (width > CONTENT_MAX_WIDTH ? CONTENT_MAX_WIDTH : width) * 0.5 | 0;

    // コンテンツと cube の z 方向移動値を更新する

    contentStyles.current[transformKey] = 'rotateY( 0deg)  translateZ(' + zLength + 'px)';
    contentStyles.prev[transformKey] = 'rotateY(-90deg) translateZ(' + zLength + 'px)';
    contentStyles.next[transformKey] = 'rotateY( 90deg) translateZ(' + zLength + 'px)';

    $contents.each(function(i) {
      var $content = $(this);
      if (i === currentIndex) $content.css(contentStyles.current);
      else if (i < currentIndex) $content.css(contentStyles.prev);
      else if (i > currentIndex) $content.css(contentStyles.next);
    });

    cubeStyle[transformKey] = 'translateZ(' + -zLength + 'px)';
    $cube.css(cubeStyle);

    // トップの背景効果の描画サイズを設定
    backgroundEffect.setSize(width, height);
  }

  /**
   * Window 'scroll' listener
   */
  function onWindowScroll(e) {
    var scrollTop = $window.scrollTop();
    var originY = Math.floor(scrollTop ? scrollTop / height * 100 : 0) + 20;

    // スクロール位置に応じて perspectiveOrigin を更新する, 速度を優先して jQuery.css() を使用しない
    $container[0].style[perspectiveOriginKey] = '50% ' + originY + '%';
  }

  /**
   * Window 'hashchange' listener
   */
  function onWindowHashChange(e) {
    var targetIndex = getTargetIndexFromHash();

    // 現在のインデックスと同じなら抜ける
    if (currentIndex === targetIndex) return;

    setNaviActiveButton(targetIndex);
    contentsTransition(targetIndex);
  }


  /**
   * Content element 'transitionend' listener
   */
  function onContentTransitionEnd(e) {
    var $content = $(this).removeClass('transition-enable');
    $content[0].removeEventListener(Akm2.Vendor.transitionend, onContentTransitionEnd, false);

    if ($contents.index($content) === currentIndex) {
      // イベントの対象コンテンツが現在のコンテンツならハッシュを更新
      window.location.hash = contentsIdList[currentIndex];
    } else {
      // 現在のコンテンツでなければ隠す
      $content.addClass('hide');
    }
  }


  /**
   * Cube element 'transitionend' listener
   */
  function onCubeTransitionEnd(e) {
    if (cubeTransitionIsFirstSec) {
      // cube が遠ざかるモーションなら近づくモーションへ移行
      cubeTransitionIsFirstSec = false;
      cubeStyle[transitionDurationKey] = '.45s';
      cubeStyle[transformKey] = 'translateZ(' + -zLength + 'px)';
      $cube.css(cubeStyle);
    }
  }


  //---------------------------
  // FUNCTIONS
  //---------------------------

  /**
   * コンテンツを遷移させる
   *
   * @param targetIndex 対象コンテンツのインデックス
   */
  function contentsTransition(targetIndex) {
    specialContentEffectsEnable(targetIndex);

    // cube のモーション, keyframes では移動中の遷移でタイミングがあわせづらいので transition をはしごする
    // まずは遠ざかる動作を開始, transitionend リスナーが実行されて元の位置へ戻る
    cubeTransitionIsFirstSec = true;
    cubeStyle[transitionDurationKey] = '.25s';
    cubeStyle[transformKey] = 'translateZ(' + (-zLength - (zLength < 200 ? zLength : 200)) + 'px)';
    $cube.css(cubeStyle);

    // コンテンツの状態を設定
    $contents.each(function(i) {
      var $content = $(this);

      if (i === targetIndex || i === currentIndex) {
        // 遷移先と現在のコンテンツのみモーションするように
        $content
          .removeClass('hide')
          .addClass('transition-enable');

        // Opera でリスナーが登録できなかったので jQuery を使用せず直接リスナーを登録する
        $content[0].addEventListener(Akm2.Vendor.transitionend, onContentTransitionEnd, false);
      }

      if (i === targetIndex) $content.css(contentStyles.current);
      else if (i < targetIndex) $content.css(contentStyles.prev);
      else if (i > targetIndex) $content.css(contentStyles.next);
    });

    currentIndex = targetIndex;
  }


  /**
   * 特別ページの効果を設定する
   *
   * @param targetIndex 対象のインデックス, インデックスが示す id が 特別ページの id と一致すれば有効に, 一致しなければ無効にする
   */
  function specialContentEffectsEnable(targetIndex) {
    var currentId = contentsIdList[targetIndex];
    var isTop = currentId === TOP_ID;
    var isAbout = currentId === ABOUT_ID;

    if (isAbout) {
      $body.addClass('at-about');
      backgroundAtAboutStyle.backgroundImage = 'url(' + ABOUT_BG_IMG + ')';
    } else {
      $body.removeClass('at-about');
      backgroundAtAboutStyle.backgroundImage = 'none';
    }
    $background.css(backgroundAtAboutStyle);

    if (isTop || isAbout) {
      backgroundEffect.startRender();
      $background.removeClass('hide');
    } else {
      backgroundEffect.stopRender();
      $background.addClass('hide');
    }

    if (isTop) {
      $body.addClass('at-top');
      $nav.addClass('hide');
    } else {
      $body.removeClass('at-top');
      $nav.removeClass('hide');
    }
  }


  /**
   * 対象コンテンツに対応するナビゲーションボタンを設定する
   *
   * @param targetIndex 対象コンテンツのインデックス
   */
  function setNaviActiveButton(targetIndex) {
    $.each(naviButtonsList, function(i, $button) {
      if ($button.hasClass('active')) $button.removeClass('active');
      if (i === targetIndex) $button.addClass('active');
    });
  }


  /**
   * URL ハッシュに対応するコンテンツのインデックスを取得する
   *
   * @return 対象コンテンツのインデックス, ハッシュがない場合は 0 を返す
   */
  function getTargetIndexFromHash() {
    var hash = window.location.hash;
    return hash ? contentsIdList.indexOf(hash.replace(/^#/, '')) || 0 : 0;
  }


  /**
   * 文字列にベンダープレフィックスを追加して返す
   *
   * @return プレフィックスを追加した文字列
   */
  function getVendorProperty(property) {
    return prefix ? prefix + property.charAt(0).toUpperCase() + property.slice(1) : property;
  }



  //---------------------------
  // CLASS
  //---------------------------

  /**
   * Sunburst
   *
   * @see http://jsdo.it/akm2/gxh2
   */
  function Sunburst(x, y, radius, num) {
    this.x = x || 0;
    this.y = y || 0;
    this.radius = radius || 0;
    this.setNum(num === undefined ? 10 : num);
  }

  Sunburst.prototype = {
    angle: 0,

    setNum: function(num) {
      this._step = Math.PI * 2 / Math.floor(num) * 0.5;
      this._num = num;
    },

    render: function(ctx) {
      var x = this.x,
        y = this.y,
        angle = this.angle,
        radius = this.radius,
        step = this._step;

      var twoPi = Math.PI * 2;
      var twoStep = step * 2;

      ctx.beginPath();
      for (var i = 0; i < twoPi; i += twoStep) {
        ctx.moveTo(x, y);
        ctx.arc(x, y, radius, angle + i, angle + i + step, false);
        ctx.closePath();
      }
      ctx.fill();
    }
  };

  /**
   * SunburstEffect
   *
   * @param canvas 描画先の canvas
   * @param burstNum 光の数
   * @param centerY 光の中心の y 座標, x 座標は指定されたサイズのセンター固定
   * @param transparentSize グラデーションの中心からの透明部分のサイズ
   */
  function SunburstEffect(canvas, burstNum, centerY, transparentSize) {
    var context = canvas.getContext('2d');
    var animationId = null;
    var sunburst = new Sunburst();
    sunburst.setNum(burstNum);

    this.setSize = function(width, height) {
      canvas.width = width;
      canvas.height = height;

      sunburst.x = width * 0.5;
      sunburst.y = centerY;
      var dy = height - sunburst.y;
      sunburst.radius = Math.sqrt(sunburst.x * sunburst.x + dy * dy);

      var stop = transparentSize / sunburst.radius;
      if (stop > 1) stop = 1;

      var grad = context.createRadialGradient(sunburst.x, sunburst.y, 65, sunburst.x, sunburst.y, sunburst.radius);
      grad.addColorStop(0, 'rgba(255, 255, 255, 0)');
      grad.addColorStop(stop, 'rgba(255, 255, 255, 0.2)');
      grad.addColorStop(1, 'rgba(255, 255, 255, 0.2)');
      context.fillStyle = grad;
    };

    this.startRender = function() {
      if (animationId) return;

      (function loop() {
        context.clearRect(0, 0, canvas.width, canvas.height);
        sunburst.render(context);
        sunburst.angle += 0.0025;
        animationId = requestAnimationFrame(loop);
      })();
    };

    this.stopRender = function() {
      cancelAnimationFrame(animationId);
      animationId = null;
    };
  }

})(window, Akm2.Vendor.requestAnimationFrame, Akm2.Vendor.cancelAnimationFrame);
