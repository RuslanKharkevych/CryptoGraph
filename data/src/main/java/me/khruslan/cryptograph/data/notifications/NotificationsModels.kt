package me.khruslan.cryptograph.data.notifications

import java.time.LocalDate

data class Notification(
    val id: Long,
    val coinId: String,
    val title: String,
    val createdAt: LocalDate,
    val expirationDate: LocalDate?,
    val trigger: NotificationTrigger,
)

sealed class NotificationTrigger(val targetPrice: Double) {

    class PriceLessThen(targetPrice: Double): NotificationTrigger(targetPrice) {
        override fun equals(other: Any?): Boolean {
            return other is PriceLessThen && targetPrice == other.targetPrice
        }

        override fun hashCode(): Int {
            return targetPrice.hashCode()
        }

        override fun toString(): String {
            return "PriceLessThen(targetPrice=$targetPrice)"
        }
    }

    class PriceMoreThen(targetPrice: Double) : NotificationTrigger(targetPrice) {
        override fun equals(other: Any?): Boolean {
            return other is PriceMoreThen && targetPrice == other.targetPrice
        }

        override fun hashCode(): Int {
            return targetPrice.hashCode()
        }

        override fun toString(): String {
            return "PriceMoreThen(targetPrice=$targetPrice)"
        }
    }
}