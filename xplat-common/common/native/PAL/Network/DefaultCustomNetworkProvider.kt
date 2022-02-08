package native.PAL.Network

import com.yandex.xplat.common.CustomNetworkProvider
import com.yandex.xplat.common.DefaultJSONSerializer
import com.yandex.xplat.common.DefaultNetwork
import com.yandex.xplat.common.Network
import com.yandex.xplat.common.NetworkConfig
import okhttp3.HttpUrl

class DefaultCustomNetworkProvider(private val networkConfig: NetworkConfig) : CustomNetworkProvider {
    override fun provideNetwork(baseUrl: String): Network {
        return DefaultNetwork(HttpUrl.parse(baseUrl)!!.url(), networkConfig, DefaultJSONSerializer())
    }
}
