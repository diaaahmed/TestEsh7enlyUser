package com.esh7enly.domain.entity.chargebalancerequest

data class ChargeBalanceRequest(
    var id: Int,
    var amount: String,
    var card_id: String,
    var uuid: String,
    var member_id: String,
    var total_amount: String,
    var total_amount_currency: String,
    var payment_for: String,
    var quantity: String,
    var status: String,
    var total_amount_piasters: String
)
