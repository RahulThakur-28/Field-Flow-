package com.rahul.fieldflow.data.workspace

import com.rahul.fieldflow.domain.model.Workspace
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("company_id_display") val companyIdDisplay: String,
    @SerialName("created_at") val createdAt: String
) {
    fun toDomain() = Workspace(
        id = id,
        name = name,
        ownerId = ownerId,
        companyIdDisplay = companyIdDisplay,
        createdAt = createdAt
    )
}
