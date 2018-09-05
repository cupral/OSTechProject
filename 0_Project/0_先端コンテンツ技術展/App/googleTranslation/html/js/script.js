jQuery(function($){
    var defURL = 'https://www.googleapis.com/language/translate/v2?key=';
    var apiKey = 'AIzaSyDc0Dd7N3iT0hVYIKgZ8c4u6V4ObEc3yPg';
    var target = '&target=';
    var changelang = 'ja';
    var requestWord = '&q=';
    var changeWord = 'food';
    var sendUrl = defURL + apiKey + target + changelang + requestWord + changeWord;

    var xhr = $.ajax({
        type: 'GET',
        url: sendUrl,
        dataType: 'jsonp',
        timeout: 30000
    })
    .done(function(res){
        $('body').append('通信成功');
        console.log(res);
        var data = res["data"];
        console.log(data);
        var translations = data["translations"];
        var translatedTxt = translations[0].translatedText;
        console.log(translations[0].translatedText);
    })
    .fail(function(res){
        $('body').append('通信失敗');
    })
    .always(function(res){
    });
});
