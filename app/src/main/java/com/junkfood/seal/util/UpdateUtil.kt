package com.junkfood.seal.util

import com.junkfood.seal.App.Companion.context
import com.junkfood.seal.util.PreferenceUtil.getInt
import com.junkfood.seal.util.PreferenceUtil.updateLong
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Updates the embedded yt-dlp engine only. Application updates are intentionally
 * delivered by Google Play, so this class never downloads or installs an APK.
 */
object UpdateUtil {
    suspend fun updateYtDlp(): YoutubeDL.UpdateStatus? =
        withContext(Dispatchers.IO) {
            val channel =
                when (YT_DLP_UPDATE_CHANNEL.getInt()) {
                    YT_DLP_NIGHTLY -> YoutubeDL.UpdateChannel.NIGHTLY
                    else -> YoutubeDL.UpdateChannel.STABLE
                }

            YoutubeDL.getInstance()
                .updateYoutubeDL(appContext = context, updateChannel = channel)
                .also {
                    if (it == YoutubeDL.UpdateStatus.DONE) {
                        YoutubeDL.getInstance().version(context)?.let { version ->
                            PreferenceUtil.encodeString(YT_DLP_VERSION, version)
                        }
                    }
                    YT_DLP_UPDATE_TIME.updateLong(System.currentTimeMillis())
                }
        }
}
