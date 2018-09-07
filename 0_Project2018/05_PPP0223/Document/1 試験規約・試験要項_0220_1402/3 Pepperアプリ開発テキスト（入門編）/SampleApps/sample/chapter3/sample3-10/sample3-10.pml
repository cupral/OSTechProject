<?xml version="1.0" encoding="UTF-8" ?>
<Package name="sample3-10" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="behavior_1" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="MyTopic" src="MyTopic/MyTopic.dlg" />
    </Dialogs>
    <Resources />
    <Topics>
        <Topic name="MyTopic_jpj" src="MyTopic/MyTopic_jpj.top" topicName="MyTopic" language="ja_JP" />
    </Topics>
    <IgnoredPaths />
    <Translations auto-fill="en_US">
        <Translation name="translation_en_US" src="translations/translation_en_US.ts" language="en_US" />
        <Translation name="translation_ja_JP" src="translations/translation_ja_JP.ts" language="ja_JP" />
    </Translations>
</Package>
