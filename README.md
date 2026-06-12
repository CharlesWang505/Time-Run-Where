# 时间去哪了 Android 客户端

这是一个可直接导入 Android Studio 的 Kotlin + Jetpack Compose 原生 Android 工程，实现本机应用使用时长统计、局域网同步、专注倒计时和强制悬浮窗拦截。

## 架构

- UI：Jetpack Compose + Material 3，底部三页：今日概览、专注模式、系统设置。
- 状态管理：`MainViewModel` 暴露 `StateFlow<MainUiState>`，页面只消费状态和回调。
- 数据采集：`UsageStatsCollector` 通过 `UsageStatsManager.queryEvents()` 汇总今日前台应用时长，只保留有 Launcher 入口的应用。
- 本地分类：`CategoryDictionary` 维护常见娱乐/效率包名映射，时间黑洞应用会参与专注拦截。
- Token 存储：`SecureTokenStore` 使用 Android Keystore 生成 AES/GCM 密钥，加密 6 位配对码后写入私有 `SharedPreferences`。
- 局域网同步：`DiscoveryClient` UDP 广播 `DISCOVER:<Token>` 到 8001，`SyncClient` 使用 OkHttp POST 到 `http://<IP>:8000/api/sync`。
- 后台任务：`SyncWorker` 通过 WorkManager 每 2 小时在 Wi-Fi/非计量网络下自动同步。
- 专注拦截：`FocusGuardService` 是前台 Service，每 800ms 轮询前台包名，命中时间黑洞包名时用 `SYSTEM_ALERT_WINDOW` 显示全屏红色覆盖层并返回桌面。
- 荣耀保活：Manifest 声明电池白名单、开机广播、前台服务；设置页内置 MagicOS 手动配置指南。

## 运行

1. 使用 Android Studio 打开 `android-time-run-where` 目录。
2. 等待 Gradle 同步完成。
3. 连接 Android 8.0+ 真机运行。UsageStats 和 Overlay 不能在普通 JVM 或多数模拟器上完整验证。
4. 首次启动后按设置页提示授予：
   - 使用情况访问权限
   - 悬浮窗权限
   - 忽略电池优化
   - Android 13+ 通知权限

## GitHub Actions 生成 APK

项目已内置 `.github/workflows/android-build.yml`。把 `android-time-run-where` 目录作为 GitHub 仓库根目录推送后，Actions 会在每次推送到 `main` 或 `master` 时自动构建 debug APK。

手动构建：

1. 打开 GitHub 仓库页面。
2. 进入 `Actions`。
3. 选择 `Build Android APK`。
4. 点击 `Run workflow`。
5. 构建完成后，在 run 页面底部的 `Artifacts` 下载 `time-run-where-debug-apk`。

下载后解压，里面的 `app-debug.apk` 可以安装到 Android 真机测试。

## Windows 端协议

Android 客户端会广播：

```text
DISCOVER:123456
```

Windows 服务端监听 UDP `8001` 后可回复以下任一格式：

```text
SERVER:192.168.1.10
IP:192.168.1.10
192.168.1.10
```

客户端随后请求：

```http
POST http://192.168.1.10:8000/api/sync
Authorization: Bearer 123456
Content-Type: application/json
```

JSON 结构示例：

```json
{
  "deviceId": "android-id",
  "deviceName": "HONOR",
  "date": "2026-06-12",
  "generatedAtMillis": 1781234567890,
  "focusMillis": 1500000,
  "apps": [
    {
      "packageName": "com.ss.android.ugc.aweme",
      "appName": "抖音",
      "durationMillis": 3600000,
      "category": "TIME_SINK",
      "categoryName": "时间黑洞"
    }
  ]
}
```

## 重要限制

- `PACKAGE_USAGE_STATS` 是特殊权限，无法通过运行时弹窗直接授权，必须跳转系统设置。
- `SYSTEM_ALERT_WINDOW` 在不同国产系统上可能还需要额外后台弹窗开关。
- 荣耀 MagicOS 必须手动锁定多任务卡片，并在手机管家中关闭自动管理，否则后台服务和 WorkManager 仍可能被系统回收。
- 如果准备上架应用市场，Android 14+ 的 `FOREGROUND_SERVICE_SPECIAL_USE` 需要按市场要求说明专注拦截用途。
