package com.example.netadminmarketplace.util

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {
    @Test fun validEmail_hasNoError() = assertNull(Validators.emailError("cat@example.com"))
    @Test fun invalidEmail_hasError() = assertNotNull(Validators.emailError("not-an-email"))
    @Test fun shortPassword_hasError() = assertNotNull(Validators.passwordError("123"))
    @Test fun validReview_hasNoError() = assertNull(Validators.reviewError("Отличная настройка VPN", 5))
    @Test fun emptyRating_hasError() = assertNotNull(Validators.reviewError("Хорошая работа", 0))
    @Test fun listing_requiresCategory() = assertNotNull(Validators.listingError("VPN setup", emptyList(), 4, 100, "Подробное описание услуги для клиента"))
}
