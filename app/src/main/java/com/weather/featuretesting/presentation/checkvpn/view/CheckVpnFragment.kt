package com.weather.featuretesting.presentation.checkvpn.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.weather.featuretesting.R
import com.weather.featuretesting.presentation.mainscreen.basic.fragment.FragmentLog
import com.weather.featuretesting.presentation.mainscreen.viewmodel.MainActivityViewModel

class CheckVpnFragment : FragmentLog(R.layout.fragment_check_vpn) {
    private val viewModelActivity: MainActivityViewModel by activityViewModels()

    private lateinit var button: Button
    private lateinit var textView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        button.setOnClickListener {
            // полезные статьи
            // https://stackoverflow.com/questions/32242384/android-getallnetworkinfo-is-deprecated-what-is-the-alternative
            // https://stackoverflow.com/questions/28386553/check-if-a-vpn-connection-is-active-in-android

            val connectivityManager: ConnectivityManager =
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // API (c 21 - ++ )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network: Network? = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                val text = """
                    Network : $network
                    VPN transport is: ${networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN)}
                    NOT_VPN capability is: ${networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)}
                """.trimIndent()
                textView.text = text
            } else {
                // API (1 - 22) устарел на API 23
                val network: Array<out Network> = connectivityManager.allNetworks
                network.forEachIndexed { index, currentNetwork ->
                    val networkCapabilities =
                        connectivityManager.getNetworkCapabilities(currentNetwork)
                    val text = """
                        Network $index : ${network[index]}
                        VPN transport is: ${networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN)}
                        NOT_VPN capability is: ${
                    networkCapabilities?.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_NOT_VPN
                    )
                    }
                    """.trimIndent()
                    textView.text = text
                }
            }
        }
    }

    private fun initView(view: View) {
        textView = view.findViewById(R.id.textCheckVpn)
        button = view.findViewById(R.id.buttonCheckVpn)
    }
}
