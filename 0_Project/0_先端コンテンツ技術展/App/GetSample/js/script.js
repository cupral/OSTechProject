jQuery(function($){
    var targetURL = 'http://haiku.ostechps.com/api?token=QF6rDyMMAcvMTGSEYfweKAGrbRBy297z&key=冬';
    var xhr = $.ajax({
        type : 'GET',
        url : targetURL,
        dataType: 'JSONP',
        jsonpCallback: 'callback',
        timeout : 30000,
        success: function (data) {
          console.log(data);
        }
    })
    .done(function(res){
      $('body').append('通信成功');
    })
    .fail(function(res){
      $('body').append('通信失敗');
    })
    .always(function(res){
    });
});
