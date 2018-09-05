/* -----------------------------------------------------
	ロールオーバー画像変更
----------------------------------------------------- */
imgOn = function() {
	this.setAttribute( 'src', this.getAttribute( 'src' ).replace( '_off.', '_on.' ) );
}
imgOff = function() {
	this.setAttribute( 'src', this.getAttribute( 'src' ).replace( '_on.', '_off.' ) );
}

/* -----------------------------------------------------
	初期設定: ロールオーバー画像変更
----------------------------------------------------- */
function setRollOver(){
	var images = document.getElementsByTagName( 'img' );
	var len = images.length;
	for ( var i=0; i < len; i++ ) {
		var img = images[i];
		img.btnOnMouseOver = imgOn;
		if ( img.getAttribute( 'src' ).match( '_off.' ) ) {
			addEvent( img, 'mouseover', 'btnOnMouseOver' );
			addEvent( img, 'mouseout', 'btnOnMouseOut' );
			img.btnOnMouseOut = imgOff;
		} else if ( img.getAttribute( 'src' ).match( '_on.' ) ) {
		addEvent( img, 'mouseover', 'btnOnMouseOver' );
		addEvent( img, 'mouseout', 'btnOnMouseOut' );
		img.btnOnMouseOut = imgOn;
		}
	}
}

addEvent( window, 'load', 'setRollOver' );

/* -----------------------------------------------------
	イベント追加
----------------------------------------------------- */
function addEvent( target, eventName, handlerName ) {
	if ( target.attachEvent ) {
		target.attachEvent( 'on' + eventName, function(e){ target[handlerName](e); } );
	} else if ( target.addEventListener ) {
		target.addEventListener(eventName, function(e){ target[handlerName](e); }, false );
	} else {
		var originalHandler = target['on' + eventName];
		if ( originalHandler ) {
			target['on' + eventName] = function(e){ originalHandler(e); target[handlerName](e); };
		} else {
			target['on' + eventName] = target[handlerName];
		}
	}
}



});
