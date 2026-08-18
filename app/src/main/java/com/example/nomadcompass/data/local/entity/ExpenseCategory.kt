package com.example.nomadcompass.data.local.entity

enum class ExpenseCategory(val displayName: String, val emoji: String) {
    FOOD("Food & Dining", "🍽️"),
    TRANSPORT("Transport", "🚗"),
    LODGING("Lodging & Stay", "🏨"),
    WORK("Work & Co-working", "💻"),
    MISC("Miscellaneous", "🛍️");

    companion object {
        fun fromString(value: String): ExpenseCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MISC
        }
    }
}
