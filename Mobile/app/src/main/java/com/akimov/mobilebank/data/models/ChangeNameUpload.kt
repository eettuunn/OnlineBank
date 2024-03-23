package com.akimov.mobilebank.data.models

import java.util.UUID

data class ChangeNameUpload(
    val name: String,
    val userId: UUID
)
