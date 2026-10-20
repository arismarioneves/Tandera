package com.mari05lim.tandera.data

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DonationOption(val productId: String, val title: String, val price: String)

sealed interface SupportState {
    data object Loading : SupportState
    data class Ready(val options: List<DonationOption>, val purchasing: Boolean = false) : SupportState
    data object Unavailable : SupportState
    data object Thanks : SupportState
}

/**
 * Doações via Google Play Billing (mesmo modelo do Bibliateca): produtos consumíveis, para que
 * a pessoa possa doar de novo. Conecta só quando a tela "Apoiar" é aberta.
 */
class BillingRepository(context: Context) {

    private val _state = MutableStateFlow<SupportState>(SupportState.Loading)
    val state: StateFlow<SupportState> = _state.asStateFlow()

    private val details = mutableMapOf<String, ProductDetails>()

    private val client: BillingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener { result, purchases -> onPurchasesUpdated(result, purchases) }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    fun connect() {
        if (client.isReady) {
            queryProducts()
            return
        }
        _state.value = SupportState.Loading
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingResponseCode.OK) {
                    consumeLeftovers()
                    queryProducts()
                } else {
                    _state.value = SupportState.Unavailable
                }
            }

            override fun onBillingServiceDisconnected() = Unit
        })
    }

    fun purchase(activity: Activity, productId: String) {
        val product = details[productId] ?: return
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(product).build()))
            .build()
        (_state.value as? SupportState.Ready)?.let { _state.value = it.copy(purchasing = true) }
        client.launchBillingFlow(activity, params)
    }

    /** Volta para a lista de valores depois do agradecimento. */
    fun reset() {
        if (_state.value == SupportState.Thanks) queryProducts()
    }

    private fun queryProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(PRODUCTS.keys.map {
                QueryProductDetailsParams.Product.newBuilder().setProductId(it).setProductType(BillingClient.ProductType.INAPP).build()
            })
            .build()
        client.queryProductDetailsAsync(params) { result, productDetails ->
            val list = productDetails.productDetailsList
            if (result.responseCode != BillingResponseCode.OK || list.isEmpty()) {
                _state.value = SupportState.Unavailable
                return@queryProductDetailsAsync
            }
            list.forEach { details[it.productId] = it }
            _state.value = SupportState.Ready(
                PRODUCTS.mapNotNull { (id, title) ->
                    val price = details[id]?.oneTimePurchaseOfferDetails?.formattedPrice ?: return@mapNotNull null
                    DonationOption(id, title, price)
                },
            )
        }
    }

    private fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        val purchased = purchases.orEmpty().filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        if (result.responseCode == BillingResponseCode.OK && purchased.isNotEmpty()) {
            purchased.forEach(::consume)
            _state.value = SupportState.Thanks
        } else {
            (_state.value as? SupportState.Ready)?.let { _state.value = it.copy(purchasing = false) }
        }
    }

    /** Uma compra não consumida (app fechado no meio) impediria doar o mesmo valor de novo. */
    private fun consumeLeftovers() {
        val params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        client.queryPurchasesAsync(params) { _, purchases ->
            purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }.forEach(::consume)
        }
    }

    private fun consume(purchase: Purchase) {
        client.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()) { _, _ -> }
    }

    companion object {
        /** IDs cadastrados no Play Console (os mesmos da versão 2.x). */
        val PRODUCTS = linkedMapOf(
            "tandera.coffee.3" to "Um café",
            "tandera.coffee.5" to "Uma pipoca",
            "tandera.coffee.8" to "Um ingresso",
            "tandera.coffee.10" to "Uma sessão completa",
        )
    }
}
