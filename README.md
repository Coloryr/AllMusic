<div align="center">

![AllMusic](https://socialify.git.ci/Coloryr/AllMusic/image?description=1&font=Inter&forks=1&logo=https%3A%2F%2Fgithub.com%2FColoryr%2FAllMusic%2Fblob%2Fmain%2Fserver%2Fneoforge_26_1%2Fsrc%2Fmain%2Fresources%2Ficon.png%3Fraw%3Dtrue&name=1&owner=1&pattern=Signal&stargazers=1&theme=Auto)

![](https://img.shields.io/badge/Version-4.0.3-blue?style=for-the-badge)
![](https://img.shields.io/github/actions/workflow/status/Coloryr/AllMusic/gradle.yml?style=for-the-badge)
![](https://img.shields.io/github/license/Coloryr/AllMusic?style=for-the-badge)

</div>

![GIF.gif](img/GIF.gif)

注：需要配合客户端Mod使用  
**不兼容旧版客户端Mod，需要新版客户端Mod**  
**如果面板服有问题，请自己去问服务器商给不给你用**
**4.0服务器只能跟4.0客户端用，不兼容3.0的服务器或客户端**

插件支持的服务器
- Paper
- Folia
- Velocity

模组支持的服务器
- Forge
- NeoForge
- Fabric

## 使用方法
1. 安装AllMusic_Server

Paper服务器  
复制`[paper]AllMusic_Server-4.0.0-all.jar`到你的`plugins`文件夹  
Velocity服务器  
复制`[velocity]AllMusic_Server-4.0.0-all.jar`到你的`plugins`文件夹  
Forge/Fabric/NeoForge类服务器  
复制`[xxx-xxx]AllMusic_Server-4.0.0-all.jar`到你的`mods`文件夹  

2. 安装客户端mod
复制`[xxx-xxx]AllMusic_Client-4.0.0-all`到客户端的`mods`文件夹
重启客户端

## 音乐API
AllMusic不会内置音乐api，你想要正常使用需要自己安装
这里提供一个参考api https://github.com/Coloryr/netapi
`allmusic\api`文件夹放音乐api构建出来的jar，需要重启插件才能加载

## 构建
1. 安装JDK25、Git
2. 使用 `git submodule update --init --recursive` 初始化项目
3. 使用 `gradlew :server:buildServer` 构建服务器
4. 使用 `gradlew :client:buildClient` 构建客户端

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

## 更新日志

```
4.0.0：全新字体渲染，拆分音乐API
```

## 配置文件说明
配置文件采用json格式，需要遵守json编写的格式规范  
- maxPlayList              最大歌曲数  
- maxPlayerList            一个玩家最大可点数量，0代表不限制
- minVote                  最小通过投票数
- voteTime                 投票时间
- lyricDelay               歌曲延迟，单位毫秒
- defaultAddMusic          默认添加歌曲方式，1为搜歌
- ktvLyricDelay            KTV模式歌词延迟，单位毫秒
- adminList                管理员列表
- playListSwitch           是否玩家点歌后是否直接从空闲歌单切换至玩家歌曲
- playListRandom           是否空闲歌单随机播放
- sendLyric                是否发送歌词到客户端
- needPermission           是否指令需要权限
- topAPI                   是否启用顶层模式，用于和BC交换数据
- mutePlayMessage          是否不发送播放信息
- muteAddMessage           是否不发送点歌信息
- showInBar                是否将信息限制在bar处
- ktvMode                  是否启用KTV歌词
- musicBR                  歌曲音质
- version                  配置文件版本号
- economy                  经济扩展配置
    - mysqlUrl             目前无用
    - backend              目前无用
    - vault                是否使用vault插件
- funConfig                娱乐选项
    - rain                 是否启用随机下雨
    - rainRate             随机下雨概率
- limit                    限制设置
    - messageLimit         是否启用广播消息长度限制
    - messageLimitSize     广播消息限制长度
    - listLimit            是否启用歌曲列表长度限制
    - listLimitSize        歌曲列表限制长度
    - infoLimit            是否启用信息长度限制
    - infoLimitSize        信息长限制长度
    - musicTimeLimit       是否启用歌曲长度限制
    - maxMusicTime         限制最长歌曲长度，单位秒
    - limitText            限制长度替换文本
- cost                     花费相关配置
    - searchCost           搜歌花费
    - addMusicCost         点歌花费
    - useCost              启用花费
- sendDelay                Hud信息更新延迟
- defaultApi               默认音乐API

## 指令说明
普通玩家指令  
- /music [音乐ID/网易云分享链接] 点歌
- /music [音乐API] [音乐ID] 点歌
- /music stop 停止播放歌曲
- /music list 查看歌曲队列
- /music cancel [序号] 取消你的点歌
- /music vote 投票切歌
- /music vote cancel 取消发起的切歌
- /music push [序号] 投票将歌曲插入到队列头
- /music push cancel 取消发起的插歌
- /music mute 不再参与点歌，再输入一次恢复
- /music mute list 不接收空闲列表点歌，再输入一次恢复
- /music search [歌名] 搜索歌曲
- /music searchapi [音乐API] [歌名] 搜索歌曲
- /music select [序列] 选择歌曲
- /music nextpage 切换下一页歌曲搜索结果
- /music lastpage 切换上一页歌曲搜索结果
- /music hud enable 启用/关闭全部界面
- /music hud reset 重置全部界面
- /music hud [位置] enable 启用关闭单一界面
- /music hud [位置] pos [x] [y] 设置某个界面的位置
- /music hud [位置] dir [对齐方式] 设置某个界面的对齐方式
- /music hud [位置] color [颜色HEX] 设置某个界面的颜色
- /music hud [位置] reset 重置单一界面
- /music hud pic size [尺寸] 设置图片尺寸
- /music hud pic rotate [开关] 设置图片旋转模式
- /music hud pic speed [数值] 设置图片旋转速度

管理员指令 
- /music reload 重读配置文件
- /music next 强制切歌
- /music ban [ID] 禁止点这首歌
- /music ban [音乐API] [ID] 禁止点这首歌
- /music unban [ID] 解禁点这首歌
- /music unban [音乐API] [ID] 解禁点这首歌
- /music banplayer [ID] 禁止某位玩家点歌
- /music unbanplayer [ID] 解禁某位玩家点歌
- /music delete [序号] 删除队列中的歌曲
- /music addlist [歌单ID] 添加歌单到空闲列表
- /music clearlist 清空空闲歌单
- /music clearban 清空禁止点歌列表
- /music clearbanplayer 清空禁止点歌玩家列表
- /music test [ID] 测试歌曲内容解析
- /music test [音乐API] [ID] 测试歌曲内容解析

若开启权限后  
- 点歌需要权限`allmusic.addmusic`
- 搜歌需要权限`allmusic.search`
- 插歌需要权限`allmusic.push`
- 投票切歌需要权限`allmusic.vote`
