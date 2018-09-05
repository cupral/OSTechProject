<?xml version="1.0" encoding="UTF-8" ?>
<Package name="DISOPA" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="." xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="question1" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="question2" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="lookAtNao" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="ojigi" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="motion/erai" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="motion/no" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="motion/camera" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="question1_dialog" src="question1_dialog/question1_dialog.dlg" />
        <Dialog name="question2_dialog" src="question2_dialog/question2_dialog.dlg" />
        <Dialog name="look" src="look/look.dlg" />
    </Dialogs>
    <Resources>
        <File name="" src=".DS_Store" />
        <File name="style" src="html/css/style.css" />
        <File name="finish" src="html/finish.html" />
        <File name="iceBreak" src="html/iceBreak.html" />
        <File name="logo" src="html/img/logo.png" />
        <File name="sLogo" src="html/img/sLogo.png" />
        <File name="cloud" src="html/img/weather/cloud.png" />
        <File name="rain" src="html/img/weather/rain.png" />
        <File name="snow" src="html/img/weather/snow.png" />
        <File name="sun" src="html/img/weather/sun.png" />
        <File name="thunder" src="html/img/weather/thunder.png" />
        <File name="index" src="html/index.html" />
        <File name="intro" src="html/intro.html" />
        <File name="jquery.min" src="html/js/jquery.min.js" />
        <File name="jquery.qimhelpers" src="html/js/jquery.qimhelpers.js" />
        <File name="script" src="html/js/script.js" />
        <File name="" src="html/js/simpleWeather/.jshintrc" />
        <File name="CHANGELOG" src="html/js/simpleWeather/CHANGELOG.md" />
        <File name="MIT-LICENSE" src="html/js/simpleWeather/MIT-LICENSE.txt" />
        <File name="README" src="html/js/simpleWeather/README.md" />
        <File name="bower" src="html/js/simpleWeather/bower.json" />
        <File name="component" src="html/js/simpleWeather/component.json" />
        <File name="gulpfile" src="html/js/simpleWeather/gulpfile.js" />
        <File name="index" src="html/js/simpleWeather/index.html" />
        <File name="jquery.simpleWeather" src="html/js/simpleWeather/jquery.simpleWeather.js" />
        <File name="jquery.simpleWeather.min" src="html/js/simpleWeather/jquery.simpleWeather.min.js" />
        <File name="package" src="html/js/simpleWeather/package.json" />
        <File name="simpleweather.jquery" src="html/js/simpleWeather/simpleweather.jquery.json" />
        <File name="socket.io.min" src="html/js/socket.io.min.js" />
        <File name="question1" src="html/question1.html" />
        <File name="question2" src="html/question2.html" />
        <File name="icon" src="icon.png" />
        <File name="switch" src="html/sound/switch.ogg" />
        <File name="loading" src="html/img/loading.gif" />
        <File name="nao" src="html/img/nao.png" />
    </Resources>
    <Topics>
        <Topic name="question1_dialog_jpj" src="question1_dialog/question1_dialog_jpj.top" topicName="question1_dialog" language="ja_JP" />
        <Topic name="question2_dialog_jpj" src="question2_dialog/question2_dialog_jpj.top" topicName="question2_dialog" language="ja_JP" />
        <Topic name="look_jpj" src="look/look_jpj.top" topicName="look" language="ja_JP" />
    </Topics>
    <IgnoredPaths />
</Package>
