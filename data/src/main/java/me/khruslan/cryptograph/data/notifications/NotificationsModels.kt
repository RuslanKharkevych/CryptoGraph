package me.khruslan.cryptograph.data.notifications

import java.time.LocalDate

data class Notification(
    val id: Long,
    val coinId: String,
    val title: String,
    val createdAt: LocalDate,
    val completedAt: LocalDate?,
    val expirationDate: LocalDate?,
    val trigger: NotificationTrigger,
    val status: NotificationStatus,
    val unread: Boolean,
)

enum class NotificationStatus {
    Pending,
    Completed,
    Expired
}

sealed class NotificationTrigger(val targetPrice: Double) {

    class PriceLessThan(targetPrice: Double) : NotificationTrigger(targetPrice) {
        override fun equals(other: Any?): Boolean {
            return other is PriceLessThan && targetPrice == other.targetPrice
        }

        override fun hashCode(): Int {
            return targetPrice.hashCode()
        }

        override fun toString(): String {
            return "PriceLessThan(targetPrice=$targetPrice)"
        }
    }

    class PriceMoreThan(targetPrice: Double) : NotificationTrigger(targetPrice) {
        override fun equals(other: Any?): Boolean {
            return other is PriceMoreThan && targetPrice == other.targetPrice
        }

        override fun hashCode(): Int {
            return targetPrice.hashCode()
        }

        override fun toString(): String {
            return "PriceMoreThan(targetPrice=$targetPrice)"
        }
    }
}