package me.khruslan.cryptograph.ui.coins.shared

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.ui.util.navigation.NavResultEffect
import me.khruslan.cryptograph.ui.util.navigation.setNavResult

private const val COIN_INFO_NAV_RESULT_KEY = "coin-info"

internal data class CoinInfo(
    val id: String,
    val name: String,
    val price: String?,
    val iconUrl: String?,
) : Parcelable {

    private constructor(parcel: Parcel) : this(
        id = checkNotNull(parcel.readString()),
        name = checkNotNull(parcel.readString()),
        price = parcel.readString(),
        iconUrl = parcel.readString()
    )

    companion object {

        @Suppress("unused")
        @JvmField
        val CREATOR = object : Parcelable.Creator<CoinInfo> {
            override fun createFromParcel(parcel: Parcel) = CoinInfo(parcel)
            override fun newArray(size: Int) = arrayOfNulls<CoinInfo>(size)
        }

        fun fromCoin(coin: Coin): CoinInfo {
            return CoinInfo(
                id = coin.id,
                name = coin.name,
                price = coin.price,
                iconUrl = coin.iconUrl
            )
        }

        fun fromArgs(args: CoinInfoArgs): CoinInfo {
            return CoinInfo(
                id = args.coinId,
                name = args.coinName,
                price = args.coinPrice,
                iconUrl = args.coinIconUrl
            )
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeString(iconUrl)
    }

    override fun describeContents() = 0
}

internal interface CoinInfoArgs {
    val coinId: String
    val coinName: String
    val coinPrice: String?
    val coinIconUrl: String?
}

internal fun NavController.dismissWithSelectedCoin(coin: Coin) {
    val coinInfo = CoinInfo.fromCoin(coin)
    setNavResult(COIN_INFO_NAV_RESULT_KEY, coinInfo)
    popBackStack()
}

@Composable
internal fun CoinInfoNavResultEffect(
    navBackStackEntry: NavBackStackEntry,
    onResult: (coinInfo: CoinInfo) -> Unit,
) {
    NavResultEffect(
        navBackStackEntry = navBackStackEntry,
        key = COIN_INFO_NAV_RESULT_KEY,
        onResult = onResult
    )
}