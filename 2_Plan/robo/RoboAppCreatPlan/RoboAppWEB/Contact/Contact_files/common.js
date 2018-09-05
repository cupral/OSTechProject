/**
 * common.js
 *
 */

(function($){
	//文字サイズ変更イベント	
	var txtResizeChecker = function() {
		var interval = 500;
		var selector = "txtResizeChecker";
		if($("."+selector)[0]) return $("."+selector);
		
		$("body").append("<span class="+selector+">t</span>");
		var checker = $("."+selector);
		checker.css({
			visibility:"hidden",
			position:"absolute",
			top:"0"
		});
		var h = checker[0].offsetHeight;		
		
		txtResize = function(){
			if(h != checker[0].offsetHeight){				
				h = checker[0].offsetHeight;
				checker.trigger("TXT_RESIZE");
			}
		}
		setInterval(txtResize,interval);
		return checker;
	}
	
	var checker;
	
	//要素の高さを揃える
	$.fn.autoHeight = function(box){
		if(!checker) checker = txtResizeChecker();
		var _this = this;
		var _tBox = $(box,this);
		this.isResize = true;
		
		var resizeHeight = function(){
			if(!_this.isResize){
				_tBox.height("auto");
				return;
			}
			
			_tBox.css({
				"height":"auto"
			});
			
			window.setTimeout(function(){
			var mh = 0;
			
			_tBox.each(function(){
				if($(this)[0].offsetHeight > mh) {
					mh = $(this)[0].offsetHeight;
				}
			});
			_tBox.css({
				height:mh
			});
			},0);
			
			
		}
		checker.bind("TXT_RESIZE",resizeHeight);
		
		if($(window).width() >= 768) resizeHeight();
		
		$(window).resize(function(){
			var ww = $(this).width();
			
			if(ww >= 768) {
				_this.isResize = true;
				resizeHeight();
			} else {
				_this.isResize = false;
				_tBox.removeAttr("style");
			}
		});
		
		return this;
		//setInterval(resizeHeight,100);
	}
	
		
})(jQuery);

$(function(){
	$(window).load(function(){
		setTimeout(function(){
		$("#header").addClass("headerTransition");
		$("#headerUpper").addClass("headerTransition");
		$("#headerLower").addClass("headerTransition");
		$("#headerSubNav li a").addClass("headerTransition");
		$("#headerLower h1").addClass("headerTransition");
		$("#headerGlobalNav").addClass("headerTransition");
		},100);
		
		var hash = location.hash;
		
		if(hash) {
			var target  = hash;
			var targetY = $(target).offset().top　-　80;
			$(this).scrollTop(targetY);
			
		}
	});
	
	
	
	
});

/* !stack ------------------------------------------------------------------- */


jQuery(document).ready(function($) {
	var btn_pagetop_pc = $(".btnPageTop.onlyPC"),
			btn_pagetop_sp = $(".btnPageTop.onlySP"),
			isPagetop = false;

	
	
	$(window).scroll(function(){
		var wt = $(this).scrollTop();
		if(wt >= 160) {
			$("#header").addClass("headerSmall");
		}else {
			$("#header").removeClass("headerSmall");
		}
		setPagetop();
	});
	
	
		
	function setPagetop() {				
		if($(window).scrollTop() > 200 && !isPagetop) {
			btn_pagetop_pc.stop()
			.animate({
				"bottom" : 30
			},300);
			isPagetop = true;
			
			btn_pagetop_sp.stop()
			.animate({
				"bottom" : 0
			},300);
			isPagetop = true;
			
		}else if($(window).scrollTop() < 199 && isPagetop) {
			btn_pagetop_pc.stop()
			.animate({
				"bottom" : -80
			},300);
			
			btn_pagetop_sp.stop()
			.animate({
				"bottom" : -40
			},300);
			isPagetop = false;
		}
		
	}
	
	
	

	//ウインドウサイズ取得
	//var windowWidth = document.body.clientWidth;
	
	//全デバイス共通
	pageScroll();
	rollover();

	/*
	$(window).resize(function(){
		var ww = $(this).width();
		
		if(ww >= 768) {
		}else{
		}
		
	});*/
	
	
	$("#btnMenu a").bind("click",function(){
		$("#headerGlobalNav").slideToggle(300);
		return false;
	});
	
	var btnWebsiteSlide = $("#btnWebsiteSlide a"),
			btnCompanySlide = $("#btnCompanySlide a"),
			btnResultSlide  = $("#btnResultSlide a")
	
	btnWebsiteSlide.bind("click",function() {
		websiteToggle();
		return false;
	});
	
	$("#btnWebsiteSlideClose a").bind("click",function() {
		websiteToggle();
		return false;
	});
	
	btnCompanySlide.bind("click",function() {
		companyToggle();
		return false;
	});
	
	$("#btnCompanySlideClose").bind("click",function() {
		companyToggle();
		return false;
	});
	
	btnResultSlide.bind("click",function() {
		resultToggle();
		return false;
	});
	
	$("#btnResultSlideClose").bind("click",function() {
		resultToggle();
		return false;
	});
	
	function websiteToggle() {
		$("#websiteList").slideToggle(300);
		if(btnWebsiteSlide.hasClass("close")) btnWebsiteSlide.removeClass("close");
		else btnWebsiteSlide.addClass("close");
	}
	
	function companyToggle() {
		$("#companyBox").slideToggle(300);
		if(btnCompanySlide.hasClass("close")) btnCompanySlide.removeClass("close");
		else btnCompanySlide.addClass("close");
	}
	
	
	function resultToggle() {
		$("#resultList").slideToggle(300);
		if(btnResultSlide.hasClass("close")) btnResultSlide.removeClass("close");
		else btnResultSlide.addClass("close");
	}
	
	
	$("a").bind("mouseover",function() {
		$(this).stop()
		.animate({
			"opacity" : 0.5
		},200);
	}).bind("mouseout",function() {
		$(this).stop()
		.animate({
			"opacity" : 1
		},200);
	});	
});


/* !isUA -------------------------------------------------------------------- */
var isUA = (function(){
	var ua = navigator.userAgent.toLowerCase();
	indexOfKey = function(key){ return (ua.indexOf(key) != -1)? true: false;}
	var o = {};
	o.ie      = function(){ return indexOfKey("msie"); }
	o.fx      = function(){ return indexOfKey("firefox"); }
	o.chrome  = function(){ return indexOfKey("chrome"); }
	o.opera   = function(){ return indexOfKey("opera"); }
	o.android = function(){ return indexOfKey("android"); }
	o.ipad    = function(){ return indexOfKey("ipad"); }
	o.ipod    = function(){ return indexOfKey("ipod"); }
	o.iphone  = function(){ return indexOfKey("iphone"); }
	return o;
})();

/* !rollover ---------------------------------------------------------------- */
var rollover = function(){
	var suffix = { normal : '_no.', over   : '_on.'}
	$('a.over, img.over, input.over').each(function(){
		var a = null;
		var img = null;

		var elem = $(this).get(0);
		if( elem.nodeName.toLowerCase() == 'a' ){
			a = $(this);
			img = $('img',this);
		}else if( elem.nodeName.toLowerCase() == 'img' || elem.nodeName.toLowerCase() == 'input' ){
			img = $(this);
		}

		var src_no = img.attr('src');
		var src_on = src_no.replace(suffix.normal, suffix.over);

		if( elem.nodeName.toLowerCase() == 'a' ){
			a.bind("mouseover focus",function(){ img.attr('src',src_on); })
			 .bind("mouseout blur",  function(){ img.attr('src',src_no); });
		}else if( elem.nodeName.toLowerCase() == 'img' ){
			img.bind("mouseover",function(){ img.attr('src',src_on); })
			   .bind("mouseout", function(){ img.attr('src',src_no); });
		}else if( elem.nodeName.toLowerCase() == 'input' ){
			img.bind("mouseover focus",function(){ img.attr('src',src_on); })
			   .bind("mouseout blur",  function(){ img.attr('src',src_no); });
		}

		var cacheimg = document.createElement('img');
		cacheimg.src = src_on;
	});
};
/* !pageScroll -------------------------------------------------------------- */
var pageScroll = function(){
	jQuery.easing.easeInOutCubic = function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t + b;
		return c/2*((t-=2)*t*t + 2) + b;
	}; 
	$('a.scroll, .scroll a, .pageTop a').each(function(){
		$(this).on("click",function(e){
			e.preventDefault();
			var target  = $(this).attr('href');
			var targetY = $(target).offset().top;
			if((target != "#wrapper") && $(window).width() >= 768) targetY = targetY - 80; 
			var parent  = ( isUA.opera() )? (document.compatMode == 'BackCompat') ? 'body': 'html' : 'html,body';
			$(parent).animate(
				{scrollTop: targetY },
				400,
				'easeInOutCubic'
			);
			return false;
		});
	});
}

