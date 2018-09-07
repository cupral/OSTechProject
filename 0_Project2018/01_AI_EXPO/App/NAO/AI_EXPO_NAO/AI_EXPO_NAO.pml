<?xml version="1.0" encoding="UTF-8" ?>
<Package name="OSTech_AI_EXPO_NAO" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="ostech" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="takumi" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="furiwake" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="rishoku" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="launcher" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="akb" xar="behavior.xar" />
        <BehaviorDescription name="behavior" src="thriller" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="talkLauncher" src="talkLauncher/talkLauncher.dlg" />
    </Dialogs>
    <Resources>
        <File name="" src=".DS_Store" />
        <File name="index" src="html/index.html" />
        <File name="style" src="html/css/style.css" />
        <File name="close" src="html/img/close.png" />
        <File name="furiwake" src="html/img/furiwake.jpg" />
        <File name="init" src="html/img/init.jpg" />
        <File name="init" src="html/img/init.png" />
        <File name="ostech" src="html/img/ostech.png" />
        <File name="rishoku" src="html/img/rishoku.jpg" />
        <File name="takumi" src="html/img/takumi.jpg" />
        <File name="script" src="html/js/script.js" />
        <File name="buttonTouch" src="html/sound/buttonTouch.wav" />
        <File name="" src="html/img/.DS_Store" />
        <File name="001" src="html/img/button/001.gif" />
        <File name="039" src="html/img/button/039.gif" />
        <File name="minus" src="html/img/button/minus.png" />
        <File name="plus" src="html/img/button/plus.png" />
        <File name="background" src="html/img/background.jpg" />
        <File name="100" src="html/img/battery/100.png" />
        <File name="35" src="html/img/battery/35.png" />
        <File name="70" src="html/img/battery/70.png" />
        <File name="charging" src="html/img/battery/charging.png" />
        <File name="AKBcokki" src="akb/AKBcokki.mp3" />
        <File name="swiftswords_ext" src="akb/swiftswords_ext.mp3" />
        <File name="popup" src="thriller/popup.ogg" />
        <File name="thriller" src="thriller/thriller.ogg" />
    </Resources>
    <Topics>
        <Topic name="talkLauncher_jpj" src="talkLauncher/talkLauncher_jpj.top" topicName="talkLauncher" language="ja_JP" />
    </Topics>
    <IgnoredPaths />
</Package>
