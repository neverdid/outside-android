package com.neverdid.outside.ui.onboarding

import com.neverdid.outside.model.ActivityCategory
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingValidationTest {
    @Test
    fun `email validation accepts ordinary addresses and trims whitespace`() {
        assertTrue(isValidEmail("  alex@example.com "))
        assertFalse(isValidEmail("alex@example"))
        assertFalse(isValidEmail("alex example.com"))
    }

    @Test
    fun `password requires at least six characters`() {
        assertFalse(isValidPassword("12345"))
        assertTrue(isValidPassword("123456"))
    }

    @Test
    fun `profile fields reject blank and one-character values`() {
        assertFalse(isValidFirstName(" A "))
        assertFalse(isValidCity("B"))
        assertTrue(isValidFirstName(" Alex "))
        assertTrue(isValidCity(" Brașov "))
    }

    @Test
    fun `at least two real interests are required`() {
        assertFalse(hasEnoughInterests(emptySet()))
        assertFalse(hasEnoughInterests(setOf(ActivityCategory.ALL, ActivityCategory.HIKING)))
        assertTrue(hasEnoughInterests(setOf(ActivityCategory.HIKING, ActivityCategory.CAMPING)))
    }
}
