<div align="center">

![AllMusic](https://socialify.git.ci/Coloryr/AllMusic/image?description=1&font=Inter&forks=1&logo=https%3A%2F%2Fgithub.com%2FColoryr%2FAllMusic%2Fblob%2Fmain%2Fserver%2Fneoforge_26_1%2Fsrc%2Fmain%2Fresources%2Ficon.png%3Fraw%3Dtrue&name=1&owner=1&pattern=Signal&stargazers=1&theme=Auto)

![](https://img.shields.io/badge/Version-4.2.9-blue?style=for-the-badge)
![](https://img.shields.io/github/actions/workflow/status/Coloryr/AllMusic/client.yml?style=for-the-badge)
![](https://img.shields.io/github/actions/workflow/status/Coloryr/AllMusic/server.yml?style=for-the-badge)
![](https://img.shields.io/github/actions/workflow/status/Coloryr/AllMusic/onejar.yml?style=for-the-badge)
![](https://img.shields.io/github/license/Coloryr/AllMusic?style=for-the-badge)

</div>

![GIF.gif](img/GIF.gif)

**注：需要配合客户端Mod使用**  
**1.7.10和1.12.2没有技术更新**  
**不兼容旧版客户端Mod，需要新版客户端Mod**  
**如果面板服有问题，请自己去问服务器商给不给你用**  
**4.0服务器只能跟4.0客户端用，不兼容3.0的服务器或客户端**

插件支持的服务器
- Paper
- Folia
- BungeeCord
- Velocity

模组支持的服务器
- Forge
- NeoForge
- Fabric

## 使用方法
1. 安装AllMusic_Server

Paper服务器  
复制`[paper]AllMusic_Server-4.3.0.jar`到你的`plugins`文件夹  
Folia服务器  
复制`[folia]AllMusic_Server-4.3.0.jar`到你的`plugins`文件夹  
Velocity服务器  
复制`[velocity]AllMusic_Server-4.3.0.jar`到你的`plugins`文件夹  
BungeeCord服务器  
复制`[bungeecord]AllMusic_Server-4.3.0.jar`到你的`plugins`文件夹  
Forge/Fabric/NeoForge类服务器  
复制`[xxx-xxx]AllMusic_Server-4.3.0.jar`到你的`mods`文件夹  

注：Release页面下载的文件名会把方括号换成点，比如`paper.AllMusic_Server-4.3.0.jar`，和上面是同一个文件

2. 安装客户端mod
复制`[xxx-xxx]AllMusic_Client-4.3.0.jar`到客户端的`mods`文件夹
重启客户端

如果你是单人游戏使用或者联机使用OneJar版本

## 音乐API
AllMusic不会内置音乐api，你想要正常使用需要自己安装
这里提供一个参考api https://github.com/Coloryr/netapi
`allmusic_server/api`（插件端为`plugins/AllMusic/api`）文件夹放音乐api构建出来的jar，需要重启插件才能加载

> 群组服（BungeeCord/Velocity）模式下，音乐API要放在**代理端**，因为放歌的是代理端

## 日志
所有日志都输出到**服务端控制台**（同时写入`logs/latest.log`），统一带`[AllMusic]`前缀：

- 插件端（Paper/Folia/BungeeCord/Velocity）：使用 MiniMessage 颜色输出
- 模组端（Forge/Fabric/NeoForge）：输出到对应模组日志，颜色会被去掉

启动时会依次打印插件版本、配置文件版本、语言文件版本、注册的音乐API，最后是`已启动-4.3.0`

| 日志 | 说明 |
| --- | --- |
| `没有注册音乐` | `api`文件夹里没有音乐API，放入API的jar后重启 |
| `配置文件config.json错误，已覆盖` | 配置文件损坏或字段缺失，已被默认配置覆盖 |
| `请及时更新配置文件` | `config.json`里的`version`和插件不匹配，建议删除后重新生成 |
| `语言文件版本号错误，运行可能会发生问题` | `message.json`版本和插件不匹配，删除后重载 |
| `没有找到目标服务器` | 顶层模式下没有找到`economy.backend`配置的后端服 |
| `经济数据请求超时` | 顶层模式下后端服没有在1秒内返回经济结果 |

插件数据目录（配置、语言、缓存、运行时数据都在这里）：

| 平台 | 目录 |
| --- | --- |
| Paper / Folia | `plugins/AllMusic` |
| BungeeCord | `plugins/AllMusic` |
| Velocity | `plugins/allmusic` |
| Forge / Fabric / NeoForge | 服务端根目录下的`allmusic_server` |

| 文件 | 说明 |
| --- | --- |
| `config.json` | 主配置文件 |
| `message.json` | 语言/提示文本文件 |
| `cookie.json` | 网易云cookie池（音乐API使用） |
| `hud.json` | 默认HUD布局 |
| `music.json` | 空闲歌单 |
| `ban.json` | 禁歌/禁点歌玩家/禁播服务器列表 |
| `players/` | 每个玩家的HUD布局（`玩家名.json`） |
| `api/` | 音乐API的jar |

## 指令表
主指令为`/music`，别名以各平台注册为准。

### 玩家指令

| 指令 | 说明 |
| --- | --- |
| `/music <音乐ID/网易云分享链接>` | 用默认API点歌 |
| `/music <音乐API> <音乐ID>` | 指定API点歌 |
| `/music help` | 查看帮助 |
| `/music list` | 查看歌曲队列 |
| `/music cancel [序号]` | 取消自己的点歌，不带序号时取消自己最早的一首 |
| `/music vote` | 发起切歌投票 |
| `/music vote cancel` | 取消自己发起的切歌投票 |
| `/music agree` | 同意当前投票 |
| `/music push [序号]` | 发起插歌投票（把自己的歌插到下一首） |
| `/music push cancel` | 取消自己发起的插歌投票 |
| `/music mute` | 不再参与点歌，再输入一次恢复 |
| `/music mute list` | 不接收空闲歌单的歌，再输入一次恢复 |
| `/music search <歌名>` | 用默认API搜索歌曲 |
| `/music searchapi <音乐API> <歌名>` | 指定API搜索歌曲 |
| `/music select <序号>` | 选择搜索结果 |
| `/music <序号>` | 搜索后直接按序号选择搜索结果，等同 `/music select <序号>` |
| `/music nextpage` | 搜索结果下一页 |
| `/music lastpage` | 搜索结果上一页 |
| `/music join` | 重新接收当前正在播放的歌曲（重连/切服后使用） |

### HUD 指令

| 指令 | 说明 |
| --- | --- |
| `/music hud enable [true/false]` | 打开/关闭全部HUD，不带参数为切换 |
| `/music hud reset` | 重置全部HUD为默认布局 |
| `/music hud alpha <0~1>` | 设置全部HUD透明度 |
| `/music hud <info/lyric/state/pic> enable [true/false]` | 打开/关闭单个HUD |
| `/music hud <info/lyric/state/pic> reset` | 重置单个HUD |
| `/music hud <info/lyric/state/pic> pos <x> <y>` | 设置单个HUD位置 |
| `/music hud <info/lyric/state/pic> dir <对齐方式>` | 设置单个HUD对齐方式 |
| `/music hud <info/lyric/state/pic> alpha <0~1>` | 设置单个HUD透明度 |
| `/music hud info/lyric color <颜色HEX>` | 设置文字颜色，如`FFAA00` |
| `/music hud info/lyric/state shadow [true/false]` | 设置文字阴影 |
| `/music hud info/lyric/state gap <像素>` | 设置行间距/文字到进度条间距 |
| `/music hud info/lyric maxwidth <像素>` | 设置最大宽度，超出滚动 |
| `/music hud info loop [true/false]` | 设置信息是否循环滚动 |
| `/music hud pic size <像素>` | 设置封面尺寸 |
| `/music hud pic rotate [true/false]` | 设置封面旋转 |
| `/music hud pic speed <数值>` | 设置封面旋转速度（每多少毫秒转1度） |
| `/music hud pic bg [true/false]` | 设置图片底图是否渲染 |

对齐方式取值：`TOP_LEFT`、`TOP_CENTER`、`TOP_RIGHT`、`LEFT`、`CENTER`、`RIGHT`、`BOTTOM_LEFT`、`BOTTOM_CENTER`、`BOTTOM_RIGHT`

### 管理员指令

管理员指`config.json`里`adminList`中的玩家、OP或者控制台。

| 指令 | 说明 |
| --- | --- |
| `/music reload` | 重读配置文件和语言文件 |
| `/music next` | 强制切歌 |
| `/music ban <ID>` | 禁止点这首歌 |
| `/music ban <音乐API> <ID>` | 禁止点这首歌（指定API） |
| `/music unban <ID>` | 解禁这首歌 |
| `/music unban <音乐API> <ID>` | 解禁这首歌（指定API） |
| `/music banplayer <玩家>` | 禁止某位玩家点歌 |
| `/music unbanplayer <玩家>` | 解禁某位玩家 |
| `/music delete <序号>` | 删除队列中的歌曲 |
| `/music addlist <歌单ID>` | 添加歌单到空闲列表 |
| `/music addlist <音乐API> <歌单ID>` | 添加歌单到空闲列表（指定API） |
| `/music clearlist` | 清空空闲歌单 |
| `/music clearban` | 清空禁歌列表 |
| `/music clearbanplayer` | 清空禁点歌玩家列表 |
| `/music test <ID>` | 测试歌曲解析并输出播放地址 |
| `/music test <音乐API> <ID>` | 测试歌曲解析（指定API） |

## 权限
仅在`config.json`里`needPermission`为`true`时检查权限。

| 权限 | 说明 |
| --- | --- |
| `allmusic.addmusic` | 点歌 |
| `allmusic.search` | 搜歌、翻页、选择搜索结果 |
| `allmusic.push` | 插歌 |
| `allmusic.vote` | 发起投票/同意投票 |

> 管理员指令不检查上面的权限，只判断`adminList`、OP或者控制台。

## 配置文件
配置文件采用json格式，需要遵守json编写的格式规范  
旧版的`maxPlayList`/`maxPlayerList`已经移到`limit`，`minVote`/`voteTime`已经移到`vote`，`musicBR`已移除（音质由音乐API决定），旧配置建议删除后重新生成

```json
{
  "lyricDelay": 0,
  "defaultAddMusic": 0,
  "ktvLyricDelay": 0,
  "adminList": ["color_yr"],
  "playListSwitch": true,
  "playListRandom": true,
  "playListEscapeDeep": 40,
  "sendLyric": true,
  "needPermission": false,
  "topAPI": false,
  "mutePlayMessage": false,
  "muteAddMessage": false,
  "showInBar": false,
  "showClick": true,
  "ktvMode": true,
  "version": "402",
  "economy": {
    "backend": "server1",
    "vault": true
  },
  "funConfig": {
    "rain": true,
    "rainRate": 10
  },
  "limit": {
    "messageLimit": false,
    "messageLimitSize": 20,
    "listLimit": false,
    "listLimitSize": 10,
    "infoLimit": false,
    "infoLimitSize": 20,
    "musicTimeLimit": false,
    "maxMusicTime": 600,
    "limitText": "...",
    "maxPlayList": 10,
    "maxPlayerList": 10
  },
  "cost": {
    "searchCost": 20,
    "addMusicCost": 10,
    "useCost": false
  },
  "vote": {
    "minVote": 3,
    "voteTime": 30,
    "voteListSize": 10
  },
  "defaultApi": "netapi",
  "sendDelay": 1000,
  "joinDelay": 1000,
  "lyricReplace": {},
  "fixSongTime": 0
}
```

| 字段 | 默认值 | 说明 |
| --- | --- | --- |
| `lyricDelay` | `0` | 歌词延迟，单位毫秒 |
| `defaultAddMusic` | `0` | 默认点歌方式，`0`直接点歌、`1`搜歌 |
| `ktvLyricDelay` | `0` | KTV模式歌词延迟，单位毫秒 |
| `adminList` | `["color_yr"]` | 管理员名单（不区分大小写） |
| `playListSwitch` | `true` | 玩家点歌后是否直接从空闲歌单切到玩家歌曲 |
| `playListRandom` | `true` | 空闲歌单是否随机播放 |
| `playListEscapeDeep` | `40` | 空闲歌单多少首之内不重复 |
| `sendLyric` | `true` | 是否发送歌词到客户端 |
| `needPermission` | `false` | 是否检查`allmusic.*`权限 |
| `topAPI` | `false` | 顶层模式，用于和BC/Velocity代理交换数据（群组服开启，代理端放歌） |
| `mutePlayMessage` | `false` | 是否不广播播放信息 |
| `muteAddMessage` | `false` | 是否不广播点歌信息 |
| `showInBar` | `false` | 播放信息是否显示在action bar |
| `showClick` | `true` | 是否显示`[点我]`操作，1.7.10客户端不支持点击事件，可设为`false` |
| `ktvMode` | `true` | 是否启用KTV歌词 |
| `version` | `"402"` | 配置文件版本号，不要手动改 |
| `economy.backend` | `"server1"` | 顶层模式下转发经济请求的后端服名字 |
| `economy.vault` | `true` | 是否挂钩Vault |
| `funConfig.rain` | `true` | 是否启用随机下雨 |
| `funConfig.rainRate` | `10` | 随机下雨概率（百分比） |
| `limit.messageLimit` | `false` | 是否限制广播消息长度 |
| `limit.messageLimitSize` | `20` | 广播消息最大长度 |
| `limit.listLimit` | `false` | 是否限制歌曲列表长度 |
| `limit.listLimitSize` | `10` | 歌曲列表最大长度 |
| `limit.infoLimit` | `false` | 是否限制信息长度 |
| `limit.infoLimitSize` | `20` | 信息最大长度 |
| `limit.musicTimeLimit` | `false` | 是否限制歌曲时长 |
| `limit.maxMusicTime` | `600` | 允许的最长歌曲，单位秒 |
| `limit.limitText` | `"..."` | 超长时替换的文本 |
| `limit.maxPlayList` | `10` | 队列最大歌曲数 |
| `limit.maxPlayerList` | `10` | 单个玩家最大点歌数，`0`为不限制 |
| `cost.searchCost` | `20` | 搜歌花费 |
| `cost.addMusicCost` | `10` | 点歌花费 |
| `cost.useCost` | `false` | 是否启用花钱点歌 |
| `vote.minVote` | `3` | 投票通过所需最小人数 |
| `vote.voteTime` | `30` | 投票时间，单位秒 |
| `vote.voteListSize` | `10` | 投票队列最大数量 |
| `defaultApi` | `"netapi"` | 默认音乐API的ID |
| `sendDelay` | `1000` | HUD信息刷新间隔，单位毫秒 |
| `joinDelay` | `1000` | 玩家进服后延迟多久开始发送歌曲，单位毫秒 |
| `lyricReplace` | `{}` | 歌词正则替换表，`{"正则": "替换"}` |
| `fixSongTime` | `0` | 歌曲总时长修正，单位毫秒（加到歌曲长度上，提前/延后切歌） |

## PAPI变量  
> %allmusic_now_music_name% 歌曲名字  
> %allmusic_now_music_al% 歌曲专辑  
> %allmusic_now_music_alia% 歌曲原曲  
> %allmusic_now_music_author% 歌曲作者  
> %allmusic_now_music_call% 点歌人  
> %allmusic_now_music_info% 歌曲所有信息  
> %allmusic_list_size% 队列大小  
> %allmusic_music_list% 队列歌曲  
> %allmusic_lyric% 歌词  
> %allmusic_tlyric% 翻译的歌词

## 构建
1. 安装JDK25、Git
2. 使用 `git submodule update --init --recursive` 初始化项目
3. 使用 `gradlew :server:buildServer` 构建服务器
4. 使用 `gradlew :client:buildClient` 构建客户端
