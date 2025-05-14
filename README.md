<img src="/BunnyRunning.gif" alt="Drawing" width="350"/>

This is the official tool for creating and animating blocks, items and entities for [Vintage Story](http://www.vintagestory.at/). It is based on on [MrCrayfish's Model Creator for Minecraft](https://github.com/MrCrayfish/ModelCreator) but extends upon it with many more features and saves the models in a format the vintage story engine can read.

> [!NOTE]
> Requires Java 8 to run, download from: https://www.java.com

Current featureset:
- Create/Remove/Modify/Arrange single cubes to build shapes
- Texture Mapping Mode, automatic texture reload
- Undo/Redo capability
- Keyframe editor to create animations
- Attachment point editor
- Auto UV Unwrap for individual boxes with several unwrap modes
- Export screenshots and animated gifs

<hr>

First launch guide for developers:
- Setup Eclipse and open it
- Import VSMC via File -> Import -> Git
- Click on project name and open Build Path -> Configure Build Path
  - Check Java library is Java 8
  - Add JARs from libs folder and natives/\<os\>/swt.jar
- Mark assets folder as source folder (Build Path -> Use as Source folder)
- Click on project name and Run As -> Java Application
