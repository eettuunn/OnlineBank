package com.akimov.mobilebank.data.models

import java.util.UUID

data class CreateAccountUpload(
    val name: String,
    val userId: UUID
)
