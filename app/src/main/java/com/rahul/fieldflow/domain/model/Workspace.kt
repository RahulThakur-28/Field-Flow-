package com.rahul.fieldflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Workspace(
    val id: String,
    val name: String,
    val ownerId: String,
    val companyIdDisplay: String,
    val createdAt: String
)
