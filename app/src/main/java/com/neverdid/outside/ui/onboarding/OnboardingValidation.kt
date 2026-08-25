package com.neverdid.outside.ui.onboarding

import com.neverdid.outside.model.ActivityCategory

private val emailPattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

fun isValidEmail(value: String): Boolean = emailPattern.matches(value.trim())

fun isValidPassword(value: String): Boolean = value.length >= 6

fun isValidFirstName(value: String): Boolean = value.trim().length >= 2

fun isValidCity(value: String): Boolean = value.trim().length >= 2

fun hasEnoughInterests(interests: Set<ActivityCategory>): Boolean =
    interests.count { it != ActivityCategory.ALL } >= 2
