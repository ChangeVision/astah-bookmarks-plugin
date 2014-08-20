Bookmarksプラグイン
===============================
![Sample Image](https://github.com/kenkenji/astah-bookmarks-plugin/raw/master/doc/screenshots/Astah_Bookmark_Plugin_jp.png)

Version
----------------
0.9.6

Available for
----------------
astah* UML, professional 6.6 以降、astah* SysML 1.2以降、astah* GSN 1.0以降

Description
----------------
図上で描画された図要素に対して、ブックマークを付けることができます。

How to install
----------------
[ここから、プラグイン.jarファイルをダウンロードします。](http://astah.change-vision.com/plugins/bookmarks/0.9.6.html)

### astah* UML、professional、SysMLをご利用の場合
1. 上部メニュー[ヘルプ] – [プラグイン一覧] からプラグイン一覧を開きます。
2. [インストール] ボタンをクリックし、jarファイルを選択してインストールしてください。
3. astah* を再起動し、プロジェクトを開きます。
4. 下部の拡張ビューに[bookmarks]タブが追加されます。

(astah* UML、professional 6.8以降をご利用の場合は、ダウンロードした.jarファイルを、astah*のツールバーにドラッグ＆ドロップすることでもインストールが可能です。)

### astah* GSN をご利用の場合
1. <ユーザホーム>/.astah/gsn/pluginsフォルダに本プラグインのjarファイルを置きます。
2. astah*を起動し、プロジェクトを開きます。
3. 下部の拡張ビューに[bookmarks]タブが追加されます。

How to use
----------------
### 基本操作

1. クラス図のクラスなどを選択状態にします。
2. メニューより「![Sample Image](https://github.com/kenkenji/astah-bookmarks-plugin/raw/master/doc/screenshots/tag_blue_add.png)
Add bookmark」をクリックすると一覧に追加されます。
3. 一覧内でブックマークに設定した図要素をクリックすると、選択した図要素が、図の中央に表示されます。
4. ブックマークを削除する場合は、一覧より選択後、メニューの「![Sample Image](https://github.com/kenkenji/astah-bookmarks-plugin/raw/master/doc/screenshots/tag_blue_delete.png)Remove bookmark」をクリックすると削除されます。

![How To Use](https://github.com/kenkenji/astah-bookmarks-plugin/raw/master/doc/screenshots/HowToUse.png)

### その他
* ブックマークデータは、プロジェクトの保存時に.astahファイルに保存されます。

* Descriptionを任意に編集することができます。ダブルクリック、または、選択後Enterキーにて編集を行います。
* 左部「▲」「▼」ボタンにて順序を入れ替えることができます。また、Ctrl＋↑キー、Ctrl＋↓キーでも同様な操作となります。
* 「![Sample Image](https://github.com/kenkenji/astah-bookmarks-plugin/raw/master/doc/screenshots/table_refresh.png)Refresh」ボタンをクリックすると一覧表を更新します。図の要素が削除されている場合は、一覧からも削除されます。

License
---------------
Copyright 2014 Change Vision, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Icons by famfamfam silk icons 
----------------
<http://famfamfam.com/>
