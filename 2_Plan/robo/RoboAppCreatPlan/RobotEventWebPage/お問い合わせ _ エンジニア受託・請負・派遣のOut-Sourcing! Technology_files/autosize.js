$(document).ready(function(){

$("#desc").height(30);//init
$("#desc").css("lineHeight","20px");//init
$("#desc").css("overflow","hidden");
$("#desc").on("input",function(evt){
    if(evt.target.scrollHeight > evt.target.offsetHeight){   
        $(evt.target).height(evt.target.scrollHeight);
    }else{          
        var lineHeight = Number($(evt.target).css("lineHeight").split("px")[0]);
        while (true){
            $(evt.target).height($(evt.target).height() - lineHeight); 
            if(evt.target.scrollHeight > evt.target.offsetHeight){
                $(evt.target).height(evt.target.scrollHeight);
                break;
            }
        }
    }
});


});
